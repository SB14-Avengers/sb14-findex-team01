package com.sprint.findex.domain.dashboard.dto.response;

import com.sprint.findex.global.type.ChartPeriodType;
import java.util.List;

public record IndexChartDto(
        Long indexInfoId,
        String indexClassification,
        String indexName,
        ChartPeriodType periodType,
        List<ChartDataPoint> dataPoints,
        List<ChartDataPoint> ma5DataPoints,
        List<ChartDataPoint> ma20DataPoints) {}
