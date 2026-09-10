package com.sprint.findex.domain.syncjob.repository.impl;

import static com.sprint.findex.domain.syncjob.entity.QSyncJob.syncJob;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.findex.domain.syncjob.dto.request.SyncJobSearchRequest;
import com.sprint.findex.domain.syncjob.entity.SyncJob;
import com.sprint.findex.domain.syncjob.repository.SyncJobRepositoryCustom;
import com.sprint.findex.global.exception.BusinessException;
import com.sprint.findex.global.exception.errorcode.SyncJobErrorCode;
import com.sprint.findex.global.type.JobResult;
import com.sprint.findex.global.type.JobType;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SyncJobRepositoryImpl implements SyncJobRepositoryCustom {
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private final JPAQueryFactory queryFactory;

    @Override
    public List<SyncJob> search(SyncJobSearchRequest request, int limit) {
        boolean asc = "asc".equalsIgnoreCase(request.sortDirection());

        return queryFactory
                .selectFrom(syncJob)
                .where(filters(request))
                .where(cursorPredicate(request, asc))
                .orderBy(orderSpecifiers(request.sortField(), asc))
                .limit(limit)
                .fetch();
    }

    @Override
    public Long countBy(SyncJobSearchRequest request) {
        Long total =
                queryFactory
                        .select(syncJob.count())
                        .from(syncJob)
                        .where(filters(request))
                        .fetchOne();
        return total == null ? 0L : total;
    }

    private BooleanExpression[] filters(SyncJobSearchRequest request) {
        return new BooleanExpression[] {
            eqJobType((request.jobType())),
            eqIndexInfoId(request.indexInfoId()),
            goeTargetDate(request.baseDateFrom()),
            loeTargetDate(request.baseDateTo()),
            containsWorker(request.worker()),
            goeJobTime(request.jobTimeFrom()),
            loeJobTime(request.jobTimeTo()),
            eqResult(request.status())
        };
    }

    private BooleanExpression eqJobType(JobType jobType) {
        return jobType == null ? null : syncJob.jobType.eq(jobType);
    }

    private BooleanExpression eqIndexInfoId(Long indexInfoId) {
        return indexInfoId == null ? null : syncJob.indexInfo.id.eq(indexInfoId);
    }

    private BooleanExpression goeTargetDate(LocalDate targetDate) {
        return targetDate == null ? null : syncJob.targetDate.goe(targetDate);
    }

    private BooleanExpression loeTargetDate(LocalDate targetDate) {
        return targetDate == null ? null : syncJob.targetDate.loe(targetDate);
    }

    private BooleanExpression containsWorker(String worker) {
        return (worker == null || worker.isBlank()) ? null : syncJob.worker.contains(worker);
    }

    private BooleanExpression goeJobTime(LocalDateTime jobTime) {
        return jobTime == null ? null : syncJob.createdAt.goe(toInstant(jobTime));
    }

    private BooleanExpression loeJobTime(LocalDateTime jobTime) {
        return jobTime == null ? null : syncJob.createdAt.loe(toInstant(jobTime));
    }

    private BooleanExpression eqResult(JobResult result) {
        return result == null ? null : syncJob.result.eq(result);
    }

    private Instant toInstant(LocalDateTime time) {
        return time.atZone(SEOUL).toInstant();
    }

    // 커서 조건이 처음에 없을 경우에는 null 반환
    // 정렬 값에 따라 현재 커서 위치(정렬된 값에서 asc, desc에 따라 다른 위치) 반환
    private BooleanExpression cursorPredicate(SyncJobSearchRequest request, boolean asc) {
        if (request.cursor() == null || request.cursor().isBlank() || request.idAfter() == null) {
            return null;
        }
        return switch (request.sortField()) {
            case "jobTime" -> {
                Instant jobTime = Instant.parse(request.cursor());
                yield asc
                        ? syncJob.createdAt
                                .gt(jobTime)
                                .or(
                                        syncJob.createdAt
                                                .eq(jobTime)
                                                .and(syncJob.id.gt(request.idAfter())))
                        : syncJob.createdAt
                                .lt(jobTime)
                                .or(
                                        syncJob.createdAt
                                                .eq(jobTime)
                                                .and(syncJob.id.lt(request.idAfter())));
            }
            case "targetDate" -> {
                LocalDate targetDate = LocalDate.parse(request.cursor());
                yield asc
                        ? syncJob.targetDate
                                .gt(targetDate)
                                .or(
                                        syncJob.targetDate
                                                .eq(targetDate)
                                                .and(syncJob.id.gt(request.idAfter())))
                        : syncJob.targetDate
                                .lt(targetDate)
                                .or(
                                        syncJob.targetDate
                                                .eq(targetDate)
                                                .and(syncJob.id.lt(request.idAfter())));
            }
            default -> throw new BusinessException(SyncJobErrorCode.INVALID_SORT_FIELD);
        };
    }

    // 동일한 값일 때, Id로 asc, desc
    private OrderSpecifier<?>[] orderSpecifiers(String sortField, boolean asc) {
        return switch (sortField) {
            case "jobTime" ->
                    asc
                            ? new OrderSpecifier[] {syncJob.createdAt.asc(), syncJob.id.asc()}
                            : new OrderSpecifier[] {syncJob.createdAt.desc(), syncJob.id.desc()};
            case "targetDate" ->
                    asc
                            ? new OrderSpecifier[] {
                                syncJob.targetDate.asc().nullsLast(), syncJob.id.asc()
                            }
                            : new OrderSpecifier[] {
                                syncJob.targetDate.desc().nullsLast(), syncJob.id.desc()
                            };
            default -> throw new BusinessException(SyncJobErrorCode.INVALID_SORT_FIELD);
        };
    }
}
