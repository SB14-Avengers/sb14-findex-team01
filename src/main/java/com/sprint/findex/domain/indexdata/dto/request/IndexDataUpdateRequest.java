package com.sprint.findex.domain.indexdata.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record IndexDataUpdateRequest(
        @PositiveOrZero BigDecimal marketPrice, // 시가
        @PositiveOrZero BigDecimal closingPrice, // 종가
        @PositiveOrZero BigDecimal highPrice, // 고가
        @PositiveOrZero BigDecimal lowPrice, // 저가
        BigDecimal versus, // 대비
        BigDecimal fluctuationRate, // 등락률
        @PositiveOrZero Long tradingQuantity, // 거래량
        @PositiveOrZero Long tradingPrice, // 거래대금
        @PositiveOrZero Long marketTotalAmount // 상장시가총액
        ) {}
