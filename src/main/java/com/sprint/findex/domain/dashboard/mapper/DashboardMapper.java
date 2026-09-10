package com.sprint.findex.domain.dashboard.mapper;

import com.sprint.findex.domain.dashboard.dto.response.ChartDataPoint;
import com.sprint.findex.domain.dashboard.dto.response.IndexChartDto;
import com.sprint.findex.domain.dashboard.dto.response.IndexInfoSummaryDto;
import com.sprint.findex.domain.dashboard.dto.response.IndexPerformanceDto;
import com.sprint.findex.domain.indexdata.entity.IndexData;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.global.type.ChartPeriodType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DashboardMapper {

    List<IndexInfoSummaryDto> toSummaryDto(List<IndexInfo> indexInfos);

    @Mapping(source = "baseDate", target = "date")
    @Mapping(source = "closingPrice", target = "value")
    ChartDataPoint toChartDataDto(IndexData indexData);

    ChartDataPoint toMovingAverageDto(LocalDate date, BigDecimal value);

    @Mapping(source = "indexInfo.id", target = "indexInfoId")
    IndexChartDto toIndexChartDto(
            IndexInfo indexInfo,
            ChartPeriodType periodType,
            List<ChartDataPoint> dataPoints,
            List<ChartDataPoint> ma5DataPoints,
            List<ChartDataPoint> ma20DataPoints);

    @Mapping(source = "indexInfo.id", target = "indexInfoId")
    @Mapping(source = "indexInfo.indexClassification", target = "indexClassification")
    @Mapping(source = "indexInfo.indexName", target = "indexName")
    IndexPerformanceDto toPerformanceDto(
            IndexInfo indexInfo,
            BigDecimal versus,
            BigDecimal fluctuationRate,
            BigDecimal currentPrice,
            BigDecimal beforePrice);
}
