package com.sprint.findex.domain.syncjob.service.impl;

import com.sprint.findex.domain.autosyncconfig.service.AutoSyncConfigInitializer;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.sprint.findex.domain.openapi.client.OpenApiClient;
import com.sprint.findex.domain.openapi.dto.request.StockMarketIndexQuery;
import com.sprint.findex.domain.openapi.dto.response.StockMarketIndexItem;
import com.sprint.findex.domain.openapi.exception.OpenApiClientException;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Slf4j
@RequiredArgsConstructor
public class SyncJobServiceImpl implements SyncJobService {
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final int MAX_LOOKBACK_DAYS = 10;
    private final OpenApiClient openApiClient;
    private final IndexInfoRepository indexInfoRepository;
    private final SyncJobRepository syncJobRepository;
    private final SyncJobMapper syncJobMapper;
    private final IndexDataWriter indexDataWriter;
    private final AutoSyncConfigInitializer autoSyncConfigInitializer;

    @Override
    @Transactional
    public List<SyncJobDto> indexInfoSync() {

        List<StockMarketIndexItem> items = fetchLatestDayItems();

        Map<String, StockMarketIndexItem> latestItemByIndex =
                items.stream()
                        .collect(
                                Collectors.toMap(
                                        item -> indexKey(item.idxCsf(), item.idxNm()),
                                        item -> item,
                                        (existingItem, newItem) ->
                                                existingItem.basDt().isAfter(newItem.basDt())
                                                        ? existingItem
                                                        : newItem));

        Map<String, IndexInfo> indexInfoMap =
                indexInfoRepository.findAll().stream()
                        .collect(
                                Collectors.toMap(
                                        info ->
                                                indexKey(
                                                        info.getIndexClassification(),
                                                        info.getIndexName()),
                                        info -> info));

        String worker = clientIpResolver();
        List<SyncJobDto> syncJobs = new ArrayList<>();

        for (Map.Entry<String, StockMarketIndexItem> entry : latestItemByIndex.entrySet()) {
            StockMarketIndexItem item = entry.getValue();

            if (item.epyItmsCnt() == null || item.basPntm() == null || item.basIdx() == null) {
                log.warn("[연동] 지수 정보 필수값 누락으로 건너뜀: {} / {}", item.idxCsf(), item.idxNm());
                continue;
            }

            IndexInfo indexInfo = indexInfoMap.get(entry.getKey());

            if (indexInfo == null) {
                indexInfo =
                        indexInfoRepository.save(
                                IndexInfo.of(
                                        item.idxCsf(),
                                        item.idxNm(),
                                        item.epyItmsCnt(),
                                        item.basPntm(),
                                        item.basIdx(),
                                        SourceType.OPEN_API,
                                        false));

                autoSyncConfigInitializer.initializeFor(indexInfo);
            } else {
                indexInfo.update(item.epyItmsCnt(), item.basPntm(), item.basIdx(), null);
            }

            SyncJob syncJob =
                    syncJobRepository.save(
                            SyncJob.of(
                                    JobType.INDEX_INFO,
                                    indexInfo,
                                    null,
                                    worker,
                                    JobResult.SUCCESS));
            syncJobs.add(syncJobMapper.toDto(syncJob));
        }

        return syncJobs;
    }

    @Override
    public List<SyncJobDto> indexDataSync(SyncJobCreateRequest request) {
        List<SyncJobDto> syncJobDtoList = new ArrayList<>();
        String worker = clientIpResolver();
        List<Long> indexInfoIds = request.indexInfoIds();
        LocalDate baseDateFrom = request.baseDateFrom();
        LocalDate baseDateTo = request.baseDateTo();

        // 전체 지수 조회 시 id에 -1 값으로 들어옴
        if (indexInfoIds.contains(-1L)) {
            Map<String, IndexInfo> indexInfoMap =
                    indexInfoRepository.findAll().stream()
                            .collect(
                                    Collectors.toMap(
                                            info ->
                                                    indexKey(
                                                            info.getIndexClassification(),
                                                            info.getIndexName()),
                                            info -> info));

            List<StockMarketIndexItem> items =
                    fetchItems(new StockMarketIndexQuery(null, null, baseDateFrom, baseDateTo));

            for (StockMarketIndexItem item : items) {
                IndexInfo indexInfo = indexInfoMap.get(indexKey(item.idxCsf(), item.idxNm()));

                if (indexInfo == null) {
                    continue;
                }
                syncJobDtoList.add(syncOne(indexInfo.getId(), worker, item));
            }
            return syncJobDtoList;
        }

        IndexInfo indexInfo =
                indexInfoRepository
                        .findById(indexInfoIds.get(0))
                        .orElseThrow(() -> new BusinessException(IndexInfoErrorCode.NOT_FOUND));

        List<StockMarketIndexItem> items =
                fetchItems(
                        new StockMarketIndexQuery(
                                indexInfo.getIndexName(), null, baseDateFrom, baseDateTo));
        String indexClassification = indexInfo.getIndexClassification();

        for (StockMarketIndexItem item : items) {
            if (!indexClassification.equals(item.idxCsf())) {
                continue;
            }
            syncJobDtoList.add(syncOne(indexInfo.getId(), worker, item));
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

    private String indexKey(String indexClassification, String indexName) {
        return indexClassification + "|" + indexName;
    }

    private List<StockMarketIndexItem> fetchLatestDayItems() {
        LocalDate baseDate = LocalDate.now(SEOUL);

        for (int attempt = 0; attempt < MAX_LOOKBACK_DAYS; attempt++) {
            List<StockMarketIndexItem> items =
                    fetchItems(new StockMarketIndexQuery(null, baseDate, null, null));

            if (!items.isEmpty()) {
                log.info("[연동] 지수 정보 기준일: {}", baseDate);
                return items;
            }
            baseDate = baseDate.minusDays(1);
        }
        throw new BusinessException(SyncJobErrorCode.OPEN_API_NO_DATA);
    }

    private List<StockMarketIndexItem> fetchItems(StockMarketIndexQuery query) {
        try {
            return openApiClient.fetchStockMarketIndex(query);
        } catch (OpenApiClientException exception) {
            log.error(
                    "[연동] Open API 호출 실패 kind={} message={}",
                    exception.getKind(),
                    exception.getMessage());
            throw new BusinessException(SyncJobErrorCode.OPEN_API_CALL_FAILED);
        }
    }

    private SyncJobDto syncOne(Long indexInfoId, String worker, StockMarketIndexItem item) {
        try {
            return indexDataWriter.sync(indexInfoId, worker, item);
        } catch (DataAccessException exception) {
            log.warn(
                    "[연동] 저장 실패: {} / {} / {}",
                    item.idxCsf(),
                    item.idxNm(),
                    item.basDt(),
                    exception);
            return indexDataWriter.recordFailure(indexInfoId, item.basDt(), worker);
        }
    }
}
