package com.sprint.findex.domain.indexdata.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexDataCreateRequest(
        @NotNull Long indexInfoId,
        @NotNull LocalDate baseDate,
        @NotNull @PositiveOrZero BigDecimal marketPrice, // 시가
        @NotNull @PositiveOrZero BigDecimal closingPrice, // 종가
        @NotNull @PositiveOrZero BigDecimal highPrice, // 고가
        @NotNull @PositiveOrZero BigDecimal lowPrice, // 저가
        @NotNull BigDecimal versus, // 대비
        @NotNull BigDecimal fluctuationRate, // 등락률
        @NotNull @PositiveOrZero Long tradingQuantity, // 거래량
        @NotNull @PositiveOrZero Long tradingPrice, // 거래대금
        @NotNull @PositiveOrZero Long marketTotalAmount // 상장시가총액
        ) {}
