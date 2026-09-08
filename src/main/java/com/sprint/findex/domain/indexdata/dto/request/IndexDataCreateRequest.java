package com.sprint.findex.domain.indexdata.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexDataCreateRequest(
        @NotNull Long indexInfoId,
        @NotNull LocalDate baseDate,
        @NotNull BigDecimal marketPrice, // 시가
        @NotNull BigDecimal closingPrice, // 종가
        @NotNull BigDecimal highPrice, // 고가
        @NotNull BigDecimal lowPrice, // 저가
        @NotNull BigDecimal versus, // 대비
        @NotNull BigDecimal fluctuationRate, // 등락률
        @NotNull Long tradingQuantity, // 거래량
        @NotNull Long tradingPrice, // 거래대금
        @NotNull Long marketTotalAmount // 상장시가총액
        ) {}
