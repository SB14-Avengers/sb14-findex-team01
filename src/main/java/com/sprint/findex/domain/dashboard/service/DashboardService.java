package com.sprint.findex.domain.dashboard.service;

import com.sprint.findex.domain.dashboard.dto.response.IndexChartDto;
import com.sprint.findex.domain.dashboard.dto.response.IndexInfoSummaryDto;
import com.sprint.findex.domain.dashboard.dto.response.IndexPerformanceDto;
import com.sprint.findex.domain.dashboard.dto.response.RankedIndexPerformanceDto;
import com.sprint.findex.global.type.ChartPeriodType;
import com.sprint.findex.global.type.PerformancePeriodType;
import java.util.List;

public interface DashboardService {
    IndexChartDto getChart(Long id, ChartPeriodType periodType);

    List<IndexInfoSummaryDto> summaries();

    List<IndexPerformanceDto> getFavorite(PerformancePeriodType periodType);

    List<RankedIndexPerformanceDto> getRankedIndex(
            Long indexInfoId, PerformancePeriodType periodType, Integer limit);
}
