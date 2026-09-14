package com.sprint.findex.domain.autosyncconfig.service.impl;

import com.sprint.findex.domain.autosyncconfig.dto.request.AutoSyncConfigSearchRequest;
import com.sprint.findex.domain.autosyncconfig.dto.request.AutoSyncConfigUpdateRequest;
import com.sprint.findex.domain.autosyncconfig.dto.response.AutoSyncConfigDto;
import com.sprint.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import com.sprint.findex.domain.autosyncconfig.mapper.AutoSyncConfigMapper;
import com.sprint.findex.domain.autosyncconfig.repository.AutoSyncConfigRepository;
import com.sprint.findex.domain.autosyncconfig.service.AutoSyncConfigService;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.domain.syncjob.dto.request.SyncJobCreateRequest;
import com.sprint.findex.domain.syncjob.dto.request.SyncJobSearchRequest;
import com.sprint.findex.domain.syncjob.dto.response.SyncJobDto;
import com.sprint.findex.domain.syncjob.service.SyncJobService;
import com.sprint.findex.global.common.CursorPageResponse;
import com.sprint.findex.global.exception.BusinessException;
import com.sprint.findex.global.exception.errorcode.AutoSyncConfigErrorCode;
import com.sprint.findex.global.type.JobResult;
import com.sprint.findex.global.type.JobType;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AutoSyncConfigServiceImpl implements AutoSyncConfigService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    private static final int MAX_FAILED_RETRY_DAYS = 7;

    private final AutoSyncConfigRepository autoSyncConfigRepository;
    private final AutoSyncConfigMapper autoSyncConfigMapper;
    private final SyncJobService syncJobService;

    @Override
    @Transactional
    public void initializeFor(IndexInfo indexInfo) {

        if (indexInfo == null) {
            throw new BusinessException(AutoSyncConfigErrorCode.INDEX_INFO_NULL);
        }

        if (autoSyncConfigRepository.existsByIndexInfo(indexInfo)) {
            throw new BusinessException(AutoSyncConfigErrorCode.ALREADY_EXISTS);
        }

        try {
            autoSyncConfigRepository.save(AutoSyncConfig.from(indexInfo));
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(AutoSyncConfigErrorCode.ALREADY_EXISTS);
        }
    }

    @Override
    @Transactional
    public AutoSyncConfigDto update(Long id, AutoSyncConfigUpdateRequest request) {
        if (id == null) {
            throw new BusinessException(AutoSyncConfigErrorCode.ID_NULL);
        }

        if (request == null || request.enabled() == null) {
            throw new BusinessException(AutoSyncConfigErrorCode.ENABLED_REQUIRED);
        }

        AutoSyncConfig config =
                autoSyncConfigRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new BusinessException(AutoSyncConfigErrorCode.NOT_FOUND));

        config.updateEnabled(request.enabled());

        return autoSyncConfigMapper.toDto(config);
    }

    @Override
    public CursorPageResponse<AutoSyncConfigDto> getAutoSyncConfigList(
            AutoSyncConfigSearchRequest request) {

        List<AutoSyncConfig> results = autoSyncConfigRepository.search(request);
        boolean hasNext = results.size() > request.size();
        List<AutoSyncConfig> content = hasNext ? results.subList(0, request.size()) : results;

        List<AutoSyncConfigDto> dtos = content.stream().map(autoSyncConfigMapper::toDto).toList();

        Long nextIdAfter = null;
        String nextCursor = null;
        if (!content.isEmpty()) {
            AutoSyncConfig last = content.get(content.size() - 1);
            nextIdAfter = last.getId();
            nextCursor =
                    switch (request.sortFieldOrDefault()) {
                        case "enabled" -> String.valueOf(last.isEnabled());
                        default -> last.getIndexInfo().getIndexName();
                    };
        }

        long totalElements = autoSyncConfigRepository.count(request);
        return new CursorPageResponse<>(
                dtos, nextCursor, nextIdAfter, request.size(), totalElements, hasNext);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void executeAutoSync() {
        List<AutoSyncConfig> targets = autoSyncConfigRepository.findByEnabledTrue();
        LocalDate today = LocalDate.now(SEOUL_ZONE);
        List<AutoSyncResult> results = new ArrayList<>();

        for (AutoSyncConfig config : targets) {
            IndexInfo indexInfo = config.getIndexInfo();
            Long indexInfoId = indexInfo.getId();

            try {
                log.info("[자동연동] 대상 확인 : indexInfoId={}", indexInfoId);
                SyncTarget target = resolveSyncTarget(indexInfo, today);
                results.add(syncOneIndex(target));
            } catch (Exception e) {
                log.error("[자동연동] 실패 : indexInfoId={}", indexInfoId, e);
                results.add(new AutoSyncResult(indexInfoId, false));
            }
        }

        long successCount = results.stream().filter(AutoSyncResult::success).count();
        log.info("[자동연동] 배치 완료 : 총 {}건 중 성공 {}건", results.size(), successCount);
    }

    private LocalDate resolveFromDate(IndexInfo indexInfo, LocalDate today) {
        CursorPageResponse<SyncJobDto> lastSuccess =
                syncJobService.find(
                        buildSingleSearchRequest(indexInfo.getId(), JobResult.SUCCESS, "desc"));

        LocalDate candidateFrom;
        if (lastSuccess.content().isEmpty()) {
            LocalDate basePointInTime = indexInfo.getBasePointInTime();
            candidateFrom =
                    (basePointInTime == null || basePointInTime.isAfter(today))
                            ? today
                            : basePointInTime;
        } else {
            LocalDate lastSuccessDate = lastSuccess.content().get(0).targetDate();
            candidateFrom = lastSuccessDate.plusDays(1);
        }

        CursorPageResponse<SyncJobDto> earliestFailed =
                syncJobService.find(
                        buildSingleSearchRequest(indexInfo.getId(), JobResult.FAILED, "asc"));

        if (!earliestFailed.content().isEmpty()) {
            LocalDate earliestFailedDate = earliestFailed.content().get(0).targetDate();
            if (earliestFailedDate.isBefore(candidateFrom)
                    && !earliestFailedDate.isBefore(today.minusDays(MAX_FAILED_RETRY_DAYS))) {
                return earliestFailedDate;
            }
        }

        return candidateFrom;
    }

    private SyncJobSearchRequest buildSingleSearchRequest(
            Long indexInfoId, JobResult result, String sortDirection) {
        return new SyncJobSearchRequest(
                JobType.INDEX_DATA,
                indexInfoId,
                null,
                null,
                null,
                null,
                null,
                result,
                null,
                null,
                "targetDate",
                sortDirection,
                1);
    }

    private SyncTarget resolveSyncTarget(IndexInfo indexInfo, LocalDate today) {
        LocalDate from = resolveFromDate(indexInfo, today);
        return new SyncTarget(indexInfo.getId(), from, today);
    }

    private AutoSyncResult syncOneIndex(SyncTarget target) {
        if (target.from().isAfter(target.to())) {
            return new AutoSyncResult(target.indexInfoId(), true);
        }

        SyncJobCreateRequest request =
                new SyncJobCreateRequest(List.of(target.indexInfoId()), target.from(), target.to());
        List<SyncJobDto> syncJobDtos = syncJobService.indexDataSync(request);

        boolean success = syncJobDtos.stream().noneMatch(dto -> dto.result() == JobResult.FAILED);
        return new AutoSyncResult(target.indexInfoId(), success);
    }

    private record SyncTarget(Long indexInfoId, LocalDate from, LocalDate to) {}

    private record AutoSyncResult(Long indexInfoId, boolean success) {}
}
