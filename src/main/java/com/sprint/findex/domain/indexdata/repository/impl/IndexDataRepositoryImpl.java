package com.sprint.findex.domain.indexdata.repository.impl;

import static com.sprint.findex.domain.indexdata.entity.QIndexData.indexData;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.findex.domain.indexdata.dto.request.IndexDataSearchRequest;
import com.sprint.findex.domain.indexdata.entity.IndexData;
import com.sprint.findex.domain.indexdata.repository.IndexDataRepositoryCustom;
import com.sprint.findex.global.exception.BusinessException;
import com.sprint.findex.global.exception.errorcode.IndexDataErrorCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IndexDataRepositoryImpl implements IndexDataRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<IndexData> search(IndexDataSearchRequest request, int limit) {
        boolean asc = "asc".equalsIgnoreCase(request.sortDirection());

        return queryFactory
                .selectFrom(indexData)
                .where(filters(request))
                .where(cursorPredicate(request, asc))
                .orderBy(orderSpecifiers(request.sortField(), asc))
                .limit(limit)
                .fetch();
    }

    @Override
    public Long countBy(IndexDataSearchRequest request) {
        Long total =
                queryFactory
                        .select(indexData.count())
                        .from(indexData)
                        .where(filters(request))
                        .fetchOne();

        return total == null ? 0L : total;
    }

    // 어떤 데이터만 볼지 정하는 기능
    private BooleanExpression[] filters(IndexDataSearchRequest request) {
        return new BooleanExpression[] {
            eqIndexInfoId(request.indexInfoId()),
            goeBaseDate(request.startDate()),
            loeBaseDate(request.endDate())
        };
    }

    private BooleanExpression eqIndexInfoId(Long indexInfoId) {
        return indexInfoId == null ? null : indexData.indexInfo.id.eq(indexInfoId);
    }

    private BooleanExpression goeBaseDate(LocalDate baseDate) {
        return baseDate == null ? null : indexData.baseDate.goe(baseDate);
    }

    private BooleanExpression loeBaseDate(LocalDate baseDate) {
        return baseDate == null ? null : indexData.baseDate.loe(baseDate);
    }

    // 첫 페이지의 마지막 데이터 다음부터 읽도록 정하는 기능.
    private BooleanExpression cursorPredicate(IndexDataSearchRequest request, boolean asc) {
        if (request.cursor() == null || request.cursor().isBlank() || request.idAfter() == null) {
            return null;
        }

        Long idAfter = request.idAfter();

        return switch (request.sortField()) {
            case "baseDate" -> {
                LocalDate cursor = LocalDate.parse(request.cursor());

                yield asc
                        ? indexData
                                .baseDate
                                .gt(cursor)
                                .or(indexData.baseDate.eq(cursor).and(indexData.id.gt(idAfter)))
                        : indexData
                                .baseDate
                                .lt(cursor)
                                .or(indexData.baseDate.eq(cursor).and(indexData.id.lt(idAfter)));
            }

            case "marketPrice" -> {
                BigDecimal cursor = new BigDecimal(request.cursor());

                yield asc
                        ? indexData
                                .marketPrice
                                .gt(cursor)
                                .or(indexData.marketPrice.eq(cursor).and(indexData.id.gt(idAfter)))
                        : indexData
                                .marketPrice
                                .lt(cursor)
                                .or(indexData.marketPrice.eq(cursor).and(indexData.id.lt(idAfter)));
            }

            case "closingPrice" -> {
                BigDecimal cursor = new BigDecimal(request.cursor());

                yield asc
                        ? indexData
                                .closingPrice
                                .gt(cursor)
                                .or(indexData.closingPrice.eq(cursor).and(indexData.id.gt(idAfter)))
                        : indexData
                                .closingPrice
                                .lt(cursor)
                                .or(
                                        indexData
                                                .closingPrice
                                                .eq(cursor)
                                                .and(indexData.id.lt(idAfter)));
            }

            case "highPrice" -> {
                BigDecimal cursor = new BigDecimal(request.cursor());

                yield asc
                        ? indexData
                                .highPrice
                                .gt(cursor)
                                .or(indexData.highPrice.eq(cursor).and(indexData.id.gt(idAfter)))
                        : indexData
                                .highPrice
                                .lt(cursor)
                                .or(indexData.highPrice.eq(cursor).and(indexData.id.lt(idAfter)));
            }

            case "lowPrice" -> {
                BigDecimal cursor = new BigDecimal(request.cursor());

                yield asc
                        ? indexData
                                .lowPrice
                                .gt(cursor)
                                .or(indexData.lowPrice.eq(cursor).and(indexData.id.gt(idAfter)))
                        : indexData
                                .lowPrice
                                .lt(cursor)
                                .or(indexData.lowPrice.eq(cursor).and(indexData.id.lt(idAfter)));
            }

            case "tradingQuantity" -> {
                Long cursor = Long.parseLong(request.cursor());

                yield asc
                        ? indexData
                                .tradingQuantity
                                .gt(cursor)
                                .or(
                                        indexData
                                                .tradingQuantity
                                                .eq(cursor)
                                                .and(indexData.id.gt(idAfter)))
                        : indexData
                                .tradingQuantity
                                .lt(cursor)
                                .or(
                                        indexData
                                                .tradingQuantity
                                                .eq(cursor)
                                                .and(indexData.id.lt(idAfter)));
            }

            case "versus" -> {
                BigDecimal cursor = new BigDecimal(request.cursor());

                yield asc
                        ? indexData
                                .versus
                                .gt(cursor)
                                .or(indexData.versus.eq(cursor).and(indexData.id.gt(idAfter)))
                        : indexData
                                .versus
                                .lt(cursor)
                                .or(indexData.versus.eq(cursor).and(indexData.id.lt(idAfter)));
            }

            case "fluctuationRate" -> {
                BigDecimal cursor = new BigDecimal(request.cursor());

                yield asc
                        ? indexData
                                .fluctuationRate
                                .gt(cursor)
                                .or(
                                        indexData
                                                .fluctuationRate
                                                .eq(cursor)
                                                .and(indexData.id.gt(idAfter)))
                        : indexData
                                .fluctuationRate
                                .lt(cursor)
                                .or(
                                        indexData
                                                .fluctuationRate
                                                .eq(cursor)
                                                .and(indexData.id.lt(idAfter)));
            }

            default -> throw new BusinessException(IndexDataErrorCode.INVALID_SORT_FIELD);
        };
    }

    private OrderSpecifier<?>[] orderSpecifiers(String sortField, boolean asc) {
        return switch (sortField) {
            case "baseDate" ->
                    asc
                            ? new OrderSpecifier[] {
                                indexData.baseDate.asc().nullsLast(), indexData.id.asc()
                            }
                            : new OrderSpecifier[] {
                                indexData.baseDate.desc().nullsLast(), indexData.id.desc()
                            };
            case "marketPrice" ->
                    asc
                            ? new OrderSpecifier[] {indexData.marketPrice.asc(), indexData.id.asc()}
                            : new OrderSpecifier[] {
                                indexData.marketPrice.desc(), indexData.id.desc()
                            };

            case "closingPrice" ->
                    asc
                            ? new OrderSpecifier[] {
                                indexData.closingPrice.asc(), indexData.id.asc()
                            }
                            : new OrderSpecifier[] {
                                indexData.closingPrice.desc(), indexData.id.desc()
                            };

            case "highPrice" ->
                    asc
                            ? new OrderSpecifier[] {indexData.highPrice.asc(), indexData.id.asc()}
                            : new OrderSpecifier[] {
                                indexData.highPrice.desc(), indexData.id.desc()
                            };

            case "lowPrice" ->
                    asc
                            ? new OrderSpecifier[] {indexData.lowPrice.asc(), indexData.id.asc()}
                            : new OrderSpecifier[] {indexData.lowPrice.desc(), indexData.id.desc()};

            case "tradingQuantity" ->
                    asc
                            ? new OrderSpecifier[] {
                                indexData.tradingQuantity.asc(), indexData.id.asc()
                            }
                            : new OrderSpecifier[] {
                                indexData.tradingQuantity.desc(), indexData.id.desc()
                            };

            case "versus" ->
                    asc
                            ? new OrderSpecifier[] {indexData.versus.asc(), indexData.id.asc()}
                            : new OrderSpecifier[] {indexData.versus.desc(), indexData.id.desc()};

            case "fluctuationRate" ->
                    asc
                            ? new OrderSpecifier[] {
                                indexData.fluctuationRate.asc(), indexData.id.asc()
                            }
                            : new OrderSpecifier[] {
                                indexData.fluctuationRate.desc(), indexData.id.desc()
                            };
            default -> throw new BusinessException(IndexDataErrorCode.INVALID_SORT_FIELD);
        };
    }
}
