package com.sprint.findex.domain.openapi.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.findex.domain.openapi.client.OpenApiClient;
import com.sprint.findex.domain.openapi.config.OpenApiProperties;
import com.sprint.findex.domain.openapi.dto.request.StockMarketIndexQuery;
import com.sprint.findex.domain.openapi.dto.response.StockMarketIndexBody;
import com.sprint.findex.domain.openapi.dto.response.StockMarketIndexHeader;
import com.sprint.findex.domain.openapi.dto.response.StockMarketIndexItem;
import com.sprint.findex.domain.openapi.dto.response.StockMarketIndexResponse;
import com.sprint.findex.domain.openapi.exception.OpenApiClientException;
import com.sprint.findex.domain.openapi.exception.OpenApiErrorKind;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriUtils;

@Slf4j
@Component
public class OpenApiClientImpl implements OpenApiClient {

    private static final DateTimeFormatter BASIC_ISO_DATE = DateTimeFormatter.BASIC_ISO_DATE;
    private static final String RESULT_TYPE_JSON = "json";

    private final RestClient openApiRestClient;
    private final OpenApiProperties properties;
    private final ObjectMapper objectMapper;

    public OpenApiClientImpl(
            @Qualifier("openApiRestClient") RestClient openApiRestClient,
            OpenApiProperties properties,
            ObjectMapper objectMapper) {
        this.openApiRestClient = openApiRestClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<StockMarketIndexItem> fetchStockMarketIndex(StockMarketIndexQuery query) {
        validateQuery(query);
        validateConfig();

        List<StockMarketIndexItem> collected = new ArrayList<>();
        Set<ItemKey> seenKeys = new HashSet<>();
        Integer expectedTotal = null;
        int pageNo = 1;

        while (true) {
            StockMarketIndexResponse response = fetchPage(query, pageNo);
            StockMarketIndexBody body = requireEnvelope(response);
            int totalCount = requireTotalCount(body);

            if (expectedTotal == null) {
                expectedTotal = totalCount;
            } else if (expectedTotal != totalCount) {
                throw fail(
                        OpenApiErrorKind.PAGINATION_INCONSISTENT,
                        "Open API totalCount가 페이지마다 다릅니다.");
            }

            if (body.pageNo() != null && body.pageNo() != pageNo) {
                throw fail(OpenApiErrorKind.PAGINATION_INCONSISTENT, "Open API pageNo가 요청과 다릅니다.");
            }

            List<StockMarketIndexItem> pageItems = response.items();
            if (pageItems == null) {
                pageItems = List.of();
            }

            if (expectedTotal == 0) {
                if (!pageItems.isEmpty()) {
                    throw fail(
                            OpenApiErrorKind.PAGINATION_INCONSISTENT,
                            "Open API totalCount=0 이지만 항목이 있습니다.");
                }
                return List.of();
            }

            if (pageItems.isEmpty()) {
                throw fail(
                        OpenApiErrorKind.PAGINATION_INCONSISTENT,
                        "Open API 페이지에 항목이 없어 수집을 진행할 수 없습니다.");
            }

            for (StockMarketIndexItem item : pageItems) {
                validateItem(item);
                ItemKey itemKey = new ItemKey(item.idxCsf(), item.idxNm(), item.basDt());
                if (!seenKeys.add(itemKey)) {
                    throw fail(
                            OpenApiErrorKind.PAGINATION_INCONSISTENT,
                            "Open API 항목이 페이지에서 중복되었습니다.");
                }
            }
            collected.addAll(pageItems);

            if (collected.size() == expectedTotal) {
                break;
            }
            if (collected.size() > expectedTotal) {
                throw fail(
                        OpenApiErrorKind.PAGINATION_INCONSISTENT,
                        "Open API 수집 건수가 totalCount를 초과했습니다.");
            }

            pageNo++;
        }

        return List.copyOf(postFilter(collected, query));
    }

    private StockMarketIndexResponse fetchPage(StockMarketIndexQuery query, int pageNo) {
        log.debug("Open API 페이지 요청: pageNo={}", pageNo);
        String raw;
        try {
            raw =
                    openApiRestClient
                            .get()
                            .uri(uriBuilder -> buildRequestUri(uriBuilder, query, pageNo))
                            .retrieve()
                            .onStatus(
                                    HttpStatusCode::isError,
                                    (request, response) -> {
                                        // 오류 본문에 서비스 키가 포함될 수 있으므로 상태 코드만 기록한다.
                                        throw fail(
                                                OpenApiErrorKind.HTTP_ERROR,
                                                "Open API HTTP "
                                                        + response.getStatusCode().value());
                                    })
                            .body(String.class);
        } catch (OpenApiClientException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw translateTransport(ex);
        }

        if (isBlank(raw)) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답 본문이 비어 있습니다.");
        }
        if (raw.stripLeading().startsWith("<")) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답이 JSON이 아닙니다.");
        }
        try {
            StockMarketIndexResponse parsed =
                    objectMapper.readValue(raw, StockMarketIndexResponse.class);
            if (parsed == null) {
                throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답 JSON을 해석할 수 없습니다.");
            }
            return parsed;
        } catch (OpenApiClientException ex) {
            throw ex;
        } catch (JsonProcessingException ex) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답 JSON을 해석할 수 없습니다.");
        }
    }

    private URI buildRequestUri(UriBuilder uriBuilder, StockMarketIndexQuery query, int pageNo) {
        uriBuilder.queryParam("serviceKey", encodeOnce(properties.getServiceKey()));
        uriBuilder.queryParam("resultType", encodeOnce(RESULT_TYPE_JSON));
        uriBuilder.queryParam("numOfRows", encodeOnce(Integer.toString(properties.getNumOfRows())));
        uriBuilder.queryParam("pageNo", encodeOnce(Integer.toString(pageNo)));

        String indexName = trimmedIndexName(query);
        if (indexName != null) {
            uriBuilder.queryParam("idxNm", encodeOnce(indexName));
        }
        if (query.baseDate() != null) {
            uriBuilder.queryParam("basDt", encodeOnce(formatDate(query.baseDate())));
        } else {
            if (query.fromDate() != null) {
                uriBuilder.queryParam("beginBasDt", encodeOnce(formatDate(query.fromDate())));
            }
            if (query.toDate() != null) {
                uriBuilder.queryParam(
                        "endBasDt", encodeOnce(formatDate(query.toDate().plusDays(1))));
            }
        }
        return uriBuilder.build();
    }

    private static StockMarketIndexBody requireEnvelope(StockMarketIndexResponse response) {
        if (response == null) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답이 비어 있습니다.");
        }
        StockMarketIndexHeader header = response.header();
        if (header == null) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답 header가 없습니다.");
        }
        String resultCode = header.resultCode();
        if (isBlank(resultCode)) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답 header resultCode가 없습니다.");
        }
        if (!response.isNormalService()) {
            throw fail(OpenApiErrorKind.EXTERNAL_HEADER, externalHeaderMessage(resultCode));
        }
        StockMarketIndexBody body = response.body();
        if (body == null) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답 body가 없습니다.");
        }
        return body;
    }

    /** 서비스 키 노출을 막기 위해 resultCode는 두 자리 숫자만 메시지에 포함한다. */
    private static String externalHeaderMessage(String resultCode) {
        if (resultCode.length() == 2 && resultCode.chars().allMatch(Character::isDigit)) {
            return "Open API header error: " + resultCode;
        }
        return "Open API header error";
    }

    private static int requireTotalCount(StockMarketIndexBody body) {
        Integer totalCount = body.totalCount();
        if (totalCount == null || totalCount < 0) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API totalCount가 없습니다.");
        }
        return totalCount;
    }

    private static void validateItem(StockMarketIndexItem item) {
        if (item == null) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 항목이 비어 있습니다.");
        }
        if (isBlank(item.idxNm())) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 항목에 지수명이 없습니다.");
        }
        if (isBlank(item.idxCsf())) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 항목에 지수 분류명이 없습니다.");
        }
        if (item.basDt() == null) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 항목에 기준일자가 없습니다.");
        }
    }

    private static List<StockMarketIndexItem> postFilter(
            List<StockMarketIndexItem> items, StockMarketIndexQuery query) {
        String exactName = trimmedIndexName(query);
        LocalDate exactDate = query.baseDate();
        LocalDate from = query.fromDate();
        LocalDate to = query.toDate();
        List<StockMarketIndexItem> filtered = new ArrayList<>();
        for (StockMarketIndexItem item : items) {
            if (exactName != null && !exactName.equals(item.idxNm())) {
                continue;
            }
            LocalDate baseDate = item.basDt();
            if (exactDate != null && !exactDate.equals(baseDate)) {
                continue;
            }
            if (from != null && baseDate.isBefore(from)) {
                continue;
            }
            if (to != null && baseDate.isAfter(to)) {
                continue;
            }
            filtered.add(item);
        }
        return filtered;
    }

    private static void validateQuery(StockMarketIndexQuery query) {
        if (query == null) {
            throw fail(OpenApiErrorKind.INVALID_REQUEST, "Open API 조회 조건이 없습니다.");
        }
        boolean hasExact = query.baseDate() != null;
        boolean hasRange = query.fromDate() != null || query.toDate() != null;
        if (hasExact && hasRange) {
            throw fail(OpenApiErrorKind.INVALID_REQUEST, "기준일과 기간을 함께 지정할 수 없습니다.");
        }
        if (query.fromDate() != null
                && query.toDate() != null
                && query.fromDate().isAfter(query.toDate())) {
            throw fail(OpenApiErrorKind.INVALID_REQUEST, "시작일은 종료일보다 이후일 수 없습니다.");
        }
        if (query.toDate() != null) {
            try {
                query.toDate().plusDays(1);
            } catch (DateTimeException ex) {
                throw fail(OpenApiErrorKind.INVALID_REQUEST, "종료일 다음 날을 계산할 수 없습니다.");
            }
        }
    }

    private void validateConfig() {
        if (isBlank(properties.getServiceKey())) {
            throw fail(OpenApiErrorKind.CONFIG_UNAVAILABLE, "Open API 서비스 키가 설정되지 않았습니다.");
        }
        if (isBlank(properties.getUri())) {
            throw fail(OpenApiErrorKind.CONFIG_UNAVAILABLE, "Open API URI가 설정되지 않았습니다.");
        }
        if (properties.getNumOfRows() < 1) {
            throw fail(OpenApiErrorKind.CONFIG_UNAVAILABLE, "Open API numOfRows가 올바르지 않습니다.");
        }
        if (!isPositive(properties.getConnectTimeout())
                || !isPositive(properties.getCallTimeout())) {
            throw fail(OpenApiErrorKind.CONFIG_UNAVAILABLE, "Open API 타임아웃이 올바르지 않습니다.");
        }
    }

    private static OpenApiClientException translateTransport(RuntimeException ex) {
        if (isTimeout(ex)) {
            return fail(OpenApiErrorKind.TIMEOUT, "Open API 호출이 시간 초과되었습니다.");
        }
        if (isNetwork(ex)) {
            return fail(OpenApiErrorKind.NETWORK_ERROR, "Open API 네트워크 오류가 발생했습니다.");
        }
        return fail(OpenApiErrorKind.NETWORK_ERROR, "Open API 호출에 실패했습니다.");
    }

    private static boolean isTimeout(Throwable ex) {
        for (Throwable current = ex; current != null; current = current.getCause()) {
            // 타임아웃도 IOException이므로 일반 네트워크 오류보다 먼저 분류한다.

            if (current instanceof TimeoutException
                    || current instanceof HttpTimeoutException
                    || current instanceof SocketTimeoutException) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNetwork(Throwable ex) {
        for (Throwable current = ex; current != null; current = current.getCause()) {
            if (current instanceof ResourceAccessException
                    || current instanceof ConnectException
                    || current instanceof IOException) {
                return true;
            }
        }
        return false;
    }

    private static OpenApiClientException fail(OpenApiErrorKind kind, String message) {
        log.warn("Open API 호출 실패: kind={}", kind);
        return new OpenApiClientException(kind, message);
    }

    private static String encodeOnce(String value) {
        // 서비스 키의 +가 공백으로 해석되지 않도록 encodeQueryParam 대신 encode를 사용한다.
        return UriUtils.encode(value, StandardCharsets.UTF_8);
    }

    private static String formatDate(LocalDate date) {
        return date.format(BASIC_ISO_DATE);
    }

    private static String trimmedIndexName(StockMarketIndexQuery query) {
        if (isBlank(query.indexName())) {
            return null;
        }
        return query.indexName().trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static boolean isPositive(Duration duration) {
        return duration != null && !duration.isZero() && !duration.isNegative();
    }

    private record ItemKey(String idxCsf, String idxNm, LocalDate basDt) {}
}
