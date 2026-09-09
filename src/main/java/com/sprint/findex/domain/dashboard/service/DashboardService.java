package com.sprint.findex.domain.dashboard.service;

import com.sprint.findex.domain.dashboard.dto.response.IndexChartDto;
import com.sprint.findex.domain.dashboard.dto.response.IndexInfoSummaryDto;
import com.sprint.findex.global.type.ChartPeriodType;
import java.util.List;

public interface DashboardService {
    IndexChartDto getChart(Long id, ChartPeriodType periodType);

    List<IndexInfoSummaryDto> summaries();
}
