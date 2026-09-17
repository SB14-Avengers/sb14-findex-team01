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
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
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
        String indexName = trimmedIndexName(query);
        Map<String, String> baseParams = buildBaseParams(query, indexName);

        StockMarketIndexBody firstPage = fetchBody(baseParams, 1);
        int totalCount = requireTotalCount(firstPage);
        if (totalCount == 0) {
            requireNoItems(firstPage);
            return List.of();
        }

        List<StockMarketIndexItem> collected = new ArrayList<>();
        Set<ItemKey> seenKeys = new HashSet<>();
        appendPage(firstPage, collected, seenKeys, totalCount);
        for (int pageNo = 2; collected.size() < totalCount; pageNo++) {
            StockMarketIndexBody page = fetchBody(baseParams, pageNo);
            requireSameTotalCount(page, totalCount);
            appendPage(page, collected, seenKeys, totalCount);
        }

        return filterByIndexName(collected, indexName);
    }

    /** 페이지마다 달라지는 pageNo를 제외한 쿼리 파라미터를 인코딩된 값으로 한 번만 만든다. */
    private Map<String, String> buildBaseParams(StockMarketIndexQuery query, String indexName) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("serviceKey", encodeOnce(properties.getServiceKey()));
        params.put("resultType", encodeOnce(RESULT_TYPE_JSON));
        params.put("numOfRows", encodeOnce(Integer.toString(properties.getNumOfRows())));

        if (indexName != null) {
            params.put("idxNm", encodeOnce(indexName));
        }
        if (query.baseDate() != null) {
            params.put("basDt", encodeOnce(formatDate(query.baseDate())));
        }
        if (query.fromDate() != null) {
            params.put("beginBasDt", encodeOnce(formatDate(query.fromDate())));
        }
        if (query.toDate() != null) {
            params.put("endBasDt", encodeOnce(formatDate(exclusiveEndDate(query.toDate()))));
        }
        return params;
    }

    /** 외부 API의 endBasDt는 종료일을 포함하지 않으므로 다음 날을 보낸다. */
    private static LocalDate exclusiveEndDate(LocalDate toDate) {
        try {
            return toDate.plusDays(1);
        } catch (DateTimeException ex) {
            throw fail(OpenApiErrorKind.INVALID_REQUEST, "종료일 다음 날을 계산할 수 없습니다.");
        }
    }

    private StockMarketIndexBody fetchBody(Map<String, String> baseParams, int pageNo) {
        String raw = requestPage(baseParams, pageNo);
        StockMarketIndexResponse response = parse(raw);
        return requireEnvelope(response);
    }

    private String requestPage(Map<String, String> baseParams, int pageNo) {
        log.debug("Open API 페이지 요청: pageNo={}", pageNo);
        try {
            return openApiRestClient
                    .get()
                    .uri(
                            uriBuilder -> {
                                baseParams.forEach(uriBuilder::queryParam);
                                uriBuilder.queryParam("pageNo", pageNo);
                                return uriBuilder.build();
                            })
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            (request, response) -> {
                                // 오류 본문에 서비스 키가 포함될 수 있으므로 상태 코드만 기록한다.
                                throw fail(
                                        OpenApiErrorKind.HTTP_ERROR,
                                        "Open API HTTP " + response.getStatusCode().value());
                            })
                    .body(String.class);
        } catch (OpenApiClientException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw translateTransport(ex);
        }
    }

    private StockMarketIndexResponse parse(String raw) {
        if (isBlank(raw)) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답 본문이 비어 있습니다.");
        }
        if (raw.stripLeading().startsWith("<")) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답이 JSON이 아닙니다.");
        }
        StockMarketIndexResponse parsed;
        try {
            parsed = objectMapper.readValue(raw, StockMarketIndexResponse.class);
        } catch (JsonProcessingException ex) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답 JSON을 해석할 수 없습니다.");
        }
        if (parsed == null) {
            throw fail(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답 JSON을 해석할 수 없습니다.");
        }
        return parsed;
    }

    private static StockMarketIndexBody requireEnvelope(StockMarketIndexResponse response) {
        if (!response.isNormalService()) {
            StockMarketIndexHeader header = response.header();
            String resultCode = header == null ? null : header.resultCode();
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
        if (resultCode != null
                && resultCode.length() == 2
                && resultCode.chars().allMatch(Character::isDigit)) {
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

    private static void requireSameTotalCount(StockMarketIndexBody page, int expectedTotal) {
        if (requireTotalCount(page) != expectedTotal) {
            throw fail(
                    OpenApiErrorKind.PAGINATION_INCONSISTENT, "Open API totalCount가 페이지마다 다릅니다.");
        }
    }

    private static void requireNoItems(StockMarketIndexBody page) {
        if (!page.itemList().isEmpty()) {
            throw fail(
                    OpenApiErrorKind.PAGINATION_INCONSISTENT,
                    "Open API totalCount=0 이지만 항목이 있습니다.");
        }
    }

    /** 한 페이지의 항목을 검증해 누적한다. 빈 페이지를 허용하면 수집이 끝나지 않으므로 실패로 처리한다. */
    private static void appendPage(
            StockMarketIndexBody page,
            List<StockMarketIndexItem> collected,
            Set<ItemKey> seenKeys,
            int totalCount) {
        List<StockMarketIndexItem> pageItems = page.itemList();
        if (pageItems.isEmpty()) {
            throw fail(
                    OpenApiErrorKind.PAGINATION_INCONSISTENT,
                    "Open API 페이지에 항목이 없어 수집을 진행할 수 없습니다.");
        }

        for (StockMarketIndexItem item : pageItems) {
            validateItem(item);
            if (!seenKeys.add(new ItemKey(item.idxCsf(), item.idxNm(), item.basDt()))) {
                throw fail(OpenApiErrorKind.PAGINATION_INCONSISTENT, "Open API 항목이 페이지에서 중복되었습니다.");
            }
        }
        collected.addAll(pageItems);

        if (collected.size() > totalCount) {
            throw fail(
                    OpenApiErrorKind.PAGINATION_INCONSISTENT,
                    "Open API 수집 건수가 totalCount를 초과했습니다.");
        }
    }

    private static void validateItem(StockMarketIndexItem item) {
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

    /** 날짜 조건은 외부 API가 정확히 적용하므로(beginBasDt 이상, endBasDt 미만) 지수명만 다시 확인한다. */
    private static List<StockMarketIndexItem> filterByIndexName(
            List<StockMarketIndexItem> items, String indexName) {
        if (indexName == null) {
            return List.copyOf(items);
        }
        return items.stream().filter(item -> indexName.equals(item.idxNm())).toList();
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
    }

    /** URI와 타임아웃은 OpenApiRestClientConfig가 빈 생성 시 검증하므로 여기서는 확인하지 않는다. */
    private void validateConfig() {
        if (isBlank(properties.getServiceKey())) {
            throw fail(OpenApiErrorKind.CONFIG_UNAVAILABLE, "Open API 서비스 키가 설정되지 않았습니다.");
        }
        if (properties.getNumOfRows() < 1) {
            throw fail(OpenApiErrorKind.CONFIG_UNAVAILABLE, "Open API numOfRows가 올바르지 않습니다.");
        }
    }

    /** 타임아웃이 아닌 전송 실패는 모두 네트워크 오류로 분류한다. */
    private static OpenApiClientException translateTransport(RuntimeException ex) {
        if (isTimeout(ex)) {
            return fail(OpenApiErrorKind.TIMEOUT, "Open API 호출이 시간 초과되었습니다.");
        }
        return fail(OpenApiErrorKind.NETWORK_ERROR, "Open API 네트워크 오류가 발생했습니다.");
    }

    private static boolean isTimeout(Throwable ex) {
        for (Throwable current = ex; current != null; current = current.getCause()) {
            if (current instanceof TimeoutException
                    || current instanceof HttpTimeoutException
                    || current instanceof SocketTimeoutException) {
                return true;
            }
        }
        return false;
    }

    /** 실패 로그는 호출부에서 한 번만 남기므로 여기서는 예외만 만든다. */
    private static OpenApiClientException fail(OpenApiErrorKind kind, String message) {
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

    private record ItemKey(String idxCsf, String idxNm, LocalDate basDt) {}
}
