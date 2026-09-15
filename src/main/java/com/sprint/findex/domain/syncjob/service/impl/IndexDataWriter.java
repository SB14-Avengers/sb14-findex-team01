package com.sprint.findex.domain.syncjob.service.impl;

import com.sprint.findex.domain.indexdata.entity.IndexData;
import com.sprint.findex.domain.indexdata.repository.IndexDataRepository;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.sprint.findex.domain.openapi.dto.response.StockMarketIndexItem;
import com.sprint.findex.domain.syncjob.dto.response.SyncJobDto;
import com.sprint.findex.domain.syncjob.entity.SyncJob;
import com.sprint.findex.domain.syncjob.mapper.SyncJobMapper;
import com.sprint.findex.domain.syncjob.repository.SyncJobRepository;
import com.sprint.findex.global.type.JobResult;
import com.sprint.findex.global.type.JobType;
import com.sprint.findex.global.type.SourceType;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexDataWriter {
    private final IndexDataRepository indexDataRepository;
    private final SyncJobRepository syncJobRepository;
    private final SyncJobMapper syncJobMapper;
    private final IndexInfoRepository indexInfoRepository;

    @Transactional
    public SyncJobDto sync(Long indexInfoId, String worker, StockMarketIndexItem item) {
        IndexInfo indexInfo = indexInfoRepository.getReferenceById(indexInfoId);
        Optional<IndexData> existing =
                indexDataRepository.findByIndexInfoIdAndBaseDate(indexInfoId, item.basDt());

        boolean hasAllPrices = hasAllPrices(item);
        JobResult result = JobResult.SUCCESS;

        if (!hasAllPrices) {
            // null 값일 경우
            log.warn("[연동] 시세 누락: {} / {} / {}", item.idxCsf(), item.idxNm(), item.basDt());
            result = JobResult.FAILED;
        } else if (existing.isPresent()) {
            // 이미 존재할 경우
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
        } else {
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
        }

        SyncJob created =
                syncJobRepository.save(
                        SyncJob.of(JobType.INDEX_DATA, indexInfo, item.basDt(), worker, result));

        return syncJobMapper.toDto(created);
    }

    @Transactional
    public SyncJobDto recordFailure(Long indexInfoId, LocalDate targetDate, String worker) {
        IndexInfo indexInfo = indexInfoRepository.getReferenceById(indexInfoId);
        SyncJob failed =
                syncJobRepository.save(
                        SyncJob.of(
                                JobType.INDEX_DATA,
                                indexInfo,
                                targetDate,
                                worker,
                                JobResult.FAILED));
        return syncJobMapper.toDto(failed);
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
}
