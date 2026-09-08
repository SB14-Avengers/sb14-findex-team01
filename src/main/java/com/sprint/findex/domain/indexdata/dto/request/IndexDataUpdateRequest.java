package com.sprint.findex.domain.indexdata.dto.request;

import java.math.BigDecimal;

public record IndexDataUpdateRequest(
        BigDecimal marketPrice, // 시가
        BigDecimal closingPrice, // 종가
        BigDecimal highPrice, // 고가
        BigDecimal lowPrice, // 저가
        BigDecimal versus, // 대비
        BigDecimal fluctuationRate, // 등락률
        Long tradingQuantity, // 거래량
        Long tradingPrice, // 거래대금
        Long marketTotalAmount // 상장시가총액
        ) {}
