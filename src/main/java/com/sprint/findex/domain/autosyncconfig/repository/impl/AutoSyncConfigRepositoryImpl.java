package com.sprint.findex.domain.autosyncconfig.repository.impl;

import static com.sprint.findex.domain.autosyncconfig.entity.QAutoSyncConfig.autoSyncConfig;
import static com.sprint.findex.domain.indexinfo.entity.QIndexInfo.indexInfo;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.findex.domain.autosyncconfig.dto.request.AutoSyncConfigSearchRequest;
import com.sprint.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import com.sprint.findex.domain.autosyncconfig.repository.AutoSyncConfigRepositoryCustom;
import com.sprint.findex.global.exception.BusinessException;
import com.sprint.findex.global.exception.errorcode.AutoSyncConfigErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AutoSyncConfigRepositoryImpl implements AutoSyncConfigRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<AutoSyncConfig> search(AutoSyncConfigSearchRequest request) {
        boolean isAsc = !"desc".equalsIgnoreCase(request.sortDirectionOrDefault());
        String sortField = request.sortFieldOrDefault();

        BooleanBuilder builder = filters(request);
        if (request.cursor() != null && request.idAfter() != null) {
            builder.and(getCursorCondition(sortField, isAsc, request.cursor(), request.idAfter()));
        }

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sortField, isAsc);

        return queryFactory
                .selectFrom(autoSyncConfig)
                .innerJoin(autoSyncConfig.indexInfo, indexInfo)
                .fetchJoin()
                .where(builder)
                .orderBy(orderSpecifier, autoSyncConfig.id.asc())
                .limit(request.size() + 1L)
                .fetch();
    }

    @Override
    public long count(AutoSyncConfigSearchRequest request) {
        BooleanBuilder builder = filters(request);

        Long total =
                queryFactory
                        .select(autoSyncConfig.count())
                        .from(autoSyncConfig)
                        .where(builder)
                        .fetchOne();

        return total != null ? total : 0L;
    }

    private BooleanBuilder filters(AutoSyncConfigSearchRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        if (request.indexInfoId() != null) {
            builder.and(autoSyncConfig.indexInfo.id.eq(request.indexInfoId()));
        }
        if (request.enabled() != null) {
            builder.and(autoSyncConfig.enabled.eq(request.enabled()));
        }
        return builder;
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortField, boolean isAsc) {
        return switch (sortField) {
            case "enabled" -> isAsc ? autoSyncConfig.enabled.asc() : autoSyncConfig.enabled.desc();
            case "indexInfo.indexName" ->
                    isAsc ? indexInfo.indexName.asc() : indexInfo.indexName.desc();
            default -> throw new BusinessException(AutoSyncConfigErrorCode.INVALID_SORT_FIELD);
        };
    }

    private BooleanExpression getCursorCondition(
            String sortField, boolean isAsc, String cursor, Long idAfter) {
        return switch (sortField) {
            case "indexInfo.indexName" ->
                    isAsc
                            ? indexInfo
                                    .indexName
                                    .gt(cursor)
                                    .or(
                                            indexInfo
                                                    .indexName
                                                    .eq(cursor)
                                                    .and(autoSyncConfig.id.gt(idAfter)))
                            : indexInfo
                                    .indexName
                                    .lt(cursor)
                                    .or(
                                            indexInfo
                                                    .indexName
                                                    .eq(cursor)
                                                    .and(autoSyncConfig.id.gt(idAfter)));

            case "enabled" -> {
                boolean cursorValue = Boolean.parseBoolean(cursor);
                BooleanExpression sameGroup =
                        autoSyncConfig.enabled.eq(cursorValue).and(autoSyncConfig.id.gt(idAfter));

                boolean hasNextGroup = isAsc ? !cursorValue : cursorValue;

                yield hasNextGroup ? autoSyncConfig.enabled.eq(isAsc).or(sameGroup) : sameGroup;
            }

            default -> throw new BusinessException(AutoSyncConfigErrorCode.INVALID_SORT_FIELD);
        };
    }
}
