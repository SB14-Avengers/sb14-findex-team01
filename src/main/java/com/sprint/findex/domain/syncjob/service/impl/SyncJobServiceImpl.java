package com.sprint.findex.domain.syncjob.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.findex.domain.indexdata.entity.IndexData;
import com.sprint.findex.domain.indexdata.repository.IndexDataRepository;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.sprint.findex.domain.openapi.client.OpenApiClient;
import com.sprint.findex.domain.openapi.dto.request.StockMarketIndexQuery;
import com.sprint.findex.domain.openapi.dto.response.StockMarketIndexItem;
import com.sprint.findex.domain.syncjob.dto.request.SyncJobCreateRequest;
import com.sprint.findex.domain.syncjob.dto.request.SyncJobSearchRequest;
import com.sprint.findex.domain.syncjob.dto.response.SyncJobDto;
import com.sprint.findex.domain.syncjob.entity.SyncJob;
import com.sprint.findex.domain.syncjob.mapper.SyncJobMapper;
import com.sprint.findex.domain.syncjob.repository.SyncJobRepository;
import com.sprint.findex.domain.syncjob.service.SyncJobService;
import com.sprint.findex.global.common.CursorPageResponse;
import com.sprint.findex.global.exception.BusinessException;
import com.sprint.findex.global.exception.errorcode.IndexInfoErrorCode;
import com.sprint.findex.global.exception.errorcode.SyncJobErrorCode;
import com.sprint.findex.global.type.JobResult;
import com.sprint.findex.global.type.JobType;
import com.sprint.findex.global.type.SourceType;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyncJobServiceImpl implements SyncJobService {
    private static final DateTimeFormatter BAS_DT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final OpenApiClient openApiClient;
    private final IndexInfoRepository indexInfoRepository;
    private final SyncJobRepository syncJobRepository;
    private final SyncJobMapper syncJobMapper;
    private final IndexDataRepository indexDataRepository;

    // TODO: OpenApi 만들어지기 전에 임시 사용 코드
    @Value("${findex.openapi.uri}")
    private String baseUrl;

    @Value("${PUBLIC_API_SERVICE_KEY}")
    private String serviceKey;

    private JsonNode fetch(LocalDate baseDate) {
        try {
            String url =
                    baseUrl
                            + "?serviceKey="
                            + URLEncoder.encode(serviceKey, StandardCharsets.UTF_8)
                            + "&resultType=json&pageNo=1&numOfRows=1000"
                            + "&basDt="
                            + baseDate.format(BAS_DT);

            String json =
                    RestClient.create().get().uri(URI.create(url)).retrieve().body(String.class);

            return new ObjectMapper()
                    .readTree(json)
                    .path("response")
                    .path("body")
                    .path("items")
                    .path("item");
        } catch (Exception e) {
            log.error("[OpenApi] 호출 실패 basDt={}", baseDate, e);
            throw new BusinessException(SyncJobErrorCode.OPEN_API_CALL_FAILED);
        }
    }

    @Override
    @Transactional
    public List<SyncJobDto> indexInfoSync() {
        // TODO: OpenApi로 교체
        List<SyncJobDto> syncJobs = new ArrayList<>();
        JsonNode items = fetch(LocalDate.of(2026, 9, 8));
        String worker = clientIpResolver();

        for (JsonNode item : items) {
            if (!item.hasNonNull("basPntm")
                    || !item.hasNonNull("basIdx")
                    || !item.hasNonNull("epyItmsCnt")) {
                log.warn(
                        "[연동] 필수값 누락으로 건너뜀: {} / {}",
                        item.path("idxCsf").asText(),
                        item.path("idxNm").asText());
                continue;
            }
            String indexClassification = item.path("idxCsf").asText();
            String indexName = item.path("idxNm").asText();
            int employedItemsCount = item.path("epyItmsCnt").asInt();
            LocalDate baseDate = LocalDate.parse(item.get("basPntm").asText(), BAS_DT);
            BigDecimal baseIndex = new BigDecimal(item.get("basIdx").asText());

            // TODO: indexInfo 존재하면 업데이트, 미존재하면 생성 - 업데이트(employedItemsCount, baseDate, baseIndex)
            // TODO: 나중에 findByIndexClassificationAndIndexName 필요
            IndexInfo indexInfo =
                    indexInfoRepository.save(
                            IndexInfo.of(
                                    indexClassification,
                                    indexName,
                                    employedItemsCount,
                                    baseDate,
                                    baseIndex,
                                    SourceType.OPEN_API,
                                    false));

            // TODO: worker 수정
            SyncJob created =
                    syncJobRepository.save(
                            SyncJob.of(
                                    JobType.INDEX_INFO,
                                    indexInfo,
                                    null,
                                    worker,
                                    JobResult.SUCCESS));

            syncJobs.add(syncJobMapper.toDto(created));
        }

        return syncJobs;
    }

    @Override
    @Transactional
    public List<SyncJobDto> indexDataSync(SyncJobCreateRequest request) {
        List<SyncJobDto> syncJobDtoList = new ArrayList<>();
        String worker = clientIpResolver();
        List<Long> indexInfoIds = request.indexInfoIds();
        LocalDate baseDateFrom = request.baseDateFrom();
        LocalDate baseDateTo = request.baseDateTo();

        // 전체 지수 조회 시 id에 -1 값으로 들어옴
        if (indexInfoIds.contains(-1L)) {
            StockMarketIndexQuery query =
                    new StockMarketIndexQuery(null, null, baseDateFrom, baseDateTo);

            Map<String, IndexInfo> indexInfoMap =
                    indexInfoRepository.findAll().stream()
                            .collect(
                                    Collectors.toMap(
                                            info ->
                                                    indexKey(
                                                            info.getIndexClassification(),
                                                            info.getIndexName()),
                                            info -> info));

            List<StockMarketIndexItem> items = openApiClient.fetchStockMarketIndex(query);

            for (StockMarketIndexItem item : items) {
                IndexInfo indexInfo = indexInfoMap.get(indexKey(item.idxCsf(), item.idxNm()));

                if (indexInfo == null) {
                    continue;
                }
                indexDataCreate(syncJobDtoList, worker, indexInfo, item);
            }
            return syncJobDtoList;
        }

        IndexInfo indexInfo =
                indexInfoRepository
                        .findById(indexInfoIds.get(0))
                        .orElseThrow(() -> new BusinessException(IndexInfoErrorCode.NOT_FOUND));

        StockMarketIndexQuery query =
                new StockMarketIndexQuery(indexInfo.getIndexName(), null, baseDateFrom, baseDateTo);
        List<StockMarketIndexItem> items = openApiClient.fetchStockMarketIndex(query);
        String indexClassification = indexInfo.getIndexClassification();

        for (StockMarketIndexItem item : items) {
            if (!indexClassification.equals(item.idxCsf())) {
                continue;
            }
            indexDataCreate(syncJobDtoList, worker, indexInfo, item);
        }
        return syncJobDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponse<SyncJobDto> find(SyncJobSearchRequest request) {
        int size = request.size();
        List<SyncJob> found = syncJobRepository.search(request, size + 1);
        // 다음 페이지 존재 확인
        boolean hasNext = found.size() > size;
        List<SyncJob> content = hasNext ? found.subList(0, size) : found;
        String nextCursor = null;
        Long nextIdAfter = null;
        if (hasNext) {
            SyncJob last = content.get(content.size() - 1);
            nextCursor =
                    "targetDate".equals(request.sortField())
                            ? (last.getTargetDate() == null
                                    ? "null"
                                    : last.getTargetDate().toString())
                            : last.getCreatedAt().toString();
            nextIdAfter = last.getId();
        }

        return new CursorPageResponse<>(
                syncJobMapper.toDtoList(content),
                nextCursor,
                nextIdAfter,
                size,
                syncJobRepository.countBy(request),
                hasNext);
    }

    private String clientIpResolver() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
            return "system";
        }
        HttpServletRequest httpServletRequest = servletAttributes.getRequest();

        String clientIp = httpServletRequest.getHeader("X-Forwarded-For");

        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = httpServletRequest.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = httpServletRequest.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = httpServletRequest.getHeader("HTTP_CLIENT_IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = httpServletRequest.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = httpServletRequest.getHeader("X-Real-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = httpServletRequest.getHeader("X-RealIP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = httpServletRequest.getHeader("REMOTE_ADDR");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = httpServletRequest.getRemoteAddr();
        }

        if (clientIp != null && clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0].trim();
        }

        return clientIp;
    }

    private void indexDataCreate(
            List<SyncJobDto> syncJobDtoList,
            String worker,
            IndexInfo indexInfo,
            StockMarketIndexItem item) {
        Optional<IndexData> existing =
                indexDataRepository.findByIndexInfoIdAndBaseDate(indexInfo.getId(), item.basDt());
        JobResult result = JobResult.SUCCESS;

        if (existing.isPresent()) {
            existing.get()
                    .update(
                            item.mkp(),
                            item.clpr(),
                            item.hipr(),
                            item.lopr(),
                            item.vs(),
                            item.fltRt(),
                            item.trqu(),
                            item.trPrc(),
                            item.lstgMrktTotAmt());
        } else if (hasAllPrices(item)) {
            IndexData indexData =
                    IndexData.of(
                            indexInfo,
                            item.basDt(),
                            SourceType.OPEN_API,
                            item.mkp(),
                            item.clpr(),
                            item.hipr(),
                            item.lopr(),
                            item.vs(),
                            item.fltRt(),
                            item.trqu(),
                            item.trPrc(),
                            item.lstgMrktTotAmt());

            indexDataRepository.save(indexData);
        } else {
            log.warn("[연동] 시세 누락: {} / {} / {}", item.idxCsf(), item.idxNm(), item.basDt());
            result = JobResult.FAILED;
        }

        SyncJob created =
                syncJobRepository.save(
                        SyncJob.of(JobType.INDEX_DATA, indexInfo, item.basDt(), worker, result));

        syncJobDtoList.add(syncJobMapper.toDto(created));
    }

    private boolean hasAllPrices(StockMarketIndexItem item) {
        return Stream.of(
                        item.mkp(),
                        item.clpr(),
                        item.hipr(),
                        item.lopr(),
                        item.vs(),
                        item.fltRt(),
                        item.trqu(),
                        item.trPrc(),
                        item.lstgMrktTotAmt())
                .allMatch(Objects::nonNull);
    }

    private String indexKey(String indexClassification, String indexName) {
        return indexClassification + "|" + indexName;
    }
}
