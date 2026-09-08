package com.sprint.findex.domain.dashboard.dto.response;

import java.math.BigDecimal;

public record IndexPerformanceDto(
        Long indexInfoId,
        String indexClassification,
        String indexName,
        BigDecimal versus,
        BigDecimal fluctuationRate,
        BigDecimal currentPrice,
        BigDecimal beforePrice) {}
