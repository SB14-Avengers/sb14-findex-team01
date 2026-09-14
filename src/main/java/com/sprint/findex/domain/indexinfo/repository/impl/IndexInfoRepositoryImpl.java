package com.sprint.findex.domain.indexinfo.repository.impl;

import static com.sprint.findex.domain.indexinfo.entity.QIndexInfo.indexInfo;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoSearchRequest;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.domain.indexinfo.repository.IndexInfoRepositoryCustom;
import com.sprint.findex.global.exception.BusinessException;
import com.sprint.findex.global.exception.errorcode.IndexInfoErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IndexInfoRepositoryImpl implements IndexInfoRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<IndexInfo> search(IndexInfoSearchRequest request) {
        BooleanBuilder builder = new BooleanBuilder();
        if (request.indexClassification() != null) {
            builder.and(
                    indexInfo.indexClassification.containsIgnoreCase(
                            request.indexClassification()));
        }
        if (request.indexName() != null) {
            builder.and(indexInfo.indexName.containsIgnoreCase(request.indexName()));
        }
        if (request.favorite() != null) {
            builder.and(indexInfo.favorite.eq(request.favorite()));
        }
        if (request.cursor() != null && request.idAfter() != null) {
            builder.and(
                    getCursorCondition(
                            request.sortFieldOrDefault(),
                            request.sortDirectionOrDefault(),
                            request.cursor(),
                            request.idAfter()));
        }

        OrderSpecifier<?> orderSpecifier =
                getOrderSpecifier(request.sortFieldOrDefault(), request.sortDirectionOrDefault());

        return queryFactory
                .selectFrom(indexInfo)
                .where(builder)
                .orderBy(orderSpecifier, indexInfo.id.asc())
                .limit(request.sizeOrDefault())
                .fetch();
    }

    @Override
    public long count(IndexInfoSearchRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        if (request.indexClassification() != null) {
            builder.and(
                    indexInfo.indexClassification.containsIgnoreCase(
                            request.indexClassification()));
        }
        if (request.indexName() != null) {
            builder.and(indexInfo.indexName.containsIgnoreCase(request.indexName()));
        }
        if (request.favorite() != null) {
            builder.and(indexInfo.favorite.eq(request.favorite()));
        }

        Long total =
                queryFactory.select(indexInfo.count()).from(indexInfo).where(builder).fetchOne();

        return total != null ? total : 0L;
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortField, String sortDirection) {
        boolean isAsc = !"desc".equalsIgnoreCase(sortDirection);

        return switch (sortField) {
            case "indexName" -> isAsc ? indexInfo.indexName.asc() : indexInfo.indexName.desc();
            case "employedItemsCount" ->
                    isAsc
                            ? indexInfo.employedItemsCount.asc()
                            : indexInfo.employedItemsCount.desc();
            case "indexClassification" ->
                    isAsc
                            ? indexInfo.indexClassification.asc()
                            : indexInfo.indexClassification.desc();
            default -> throw new BusinessException(IndexInfoErrorCode.INVALID_SORT_FIELD);
        };
    }

    private BooleanExpression getCursorCondition(
            String sortField, String sortDirection, String cursor, Long idAfter) {
        boolean isAsc = !"desc".equalsIgnoreCase(sortDirection);

        return switch (sortField) {
            case "indexName" ->
                    isAsc
                            ? indexInfo
                                    .indexName
                                    .gt(cursor)
                                    .or(
                                            indexInfo
                                                    .indexName
                                                    .eq(cursor)
                                                    .and(indexInfo.id.gt(idAfter)))
                            : indexInfo
                                    .indexName
                                    .lt(cursor)
                                    .or(
                                            indexInfo
                                                    .indexName
                                                    .eq(cursor)
                                                    .and(indexInfo.id.gt(idAfter)));

            case "employedItemsCount" -> {
                int cursorValue = Integer.parseInt(cursor);
                yield isAsc
                        ? indexInfo
                                .employedItemsCount
                                .gt(cursorValue)
                                .or(
                                        indexInfo
                                                .employedItemsCount
                                                .eq(cursorValue)
                                                .and(indexInfo.id.gt(idAfter)))
                        : indexInfo
                                .employedItemsCount
                                .lt(cursorValue)
                                .or(
                                        indexInfo
                                                .employedItemsCount
                                                .eq(cursorValue)
                                                .and(indexInfo.id.gt(idAfter)));
            }

            case "indexClassification" ->
                    isAsc
                            ? indexInfo
                                    .indexClassification
                                    .gt(cursor)
                                    .or(
                                            indexInfo
                                                    .indexClassification
                                                    .eq(cursor)
                                                    .and(indexInfo.id.gt(idAfter)))
                            : indexInfo
                                    .indexClassification
                                    .lt(cursor)
                                    .or(
                                            indexInfo
                                                    .indexClassification
                                                    .eq(cursor)
                                                    .and(indexInfo.id.gt(idAfter)));

            default -> throw new BusinessException(IndexInfoErrorCode.INVALID_SORT_FIELD);
        };
    }
}
