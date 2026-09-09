package com.sprint.findex.domain.dashboard.dto.response;

import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.global.type.ChartPeriodType;
import java.util.List;

public record IndexChartDto(
        Long indexInfoId,
        String indexClassification,
        String indexName,
        ChartPeriodType periodType,
        List<ChartDataPoint> dataPoints,
        List<ChartDataPoint> ma5DataPoints,
        List<ChartDataPoint> ma20DataPoints) {

    public static IndexChartDto of(
            IndexInfo indexInfo,
            ChartPeriodType periodType,
            List<ChartDataPoint> dataPoints,
            List<ChartDataPoint> ma5DataPoints,
            List<ChartDataPoint> ma20DataPoints) {
        return new IndexChartDto(
                indexInfo.getId(),
                indexInfo.getIndexClassification(),
                indexInfo.getIndexName(),
                periodType,
                dataPoints,
                ma5DataPoints,
                ma20DataPoints);
    }
}
