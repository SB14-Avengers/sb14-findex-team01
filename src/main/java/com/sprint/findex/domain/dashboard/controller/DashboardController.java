package com.sprint.findex.domain.dashboard.controller;

import com.sprint.findex.domain.dashboard.dto.response.IndexChartDto;
import com.sprint.findex.domain.dashboard.dto.response.IndexInfoSummaryDto;
import com.sprint.findex.domain.dashboard.dto.response.IndexPerformanceDto;
import com.sprint.findex.domain.dashboard.dto.response.RankedIndexPerformanceDto;
import com.sprint.findex.domain.dashboard.service.DashboardService;
import com.sprint.findex.global.type.ChartPeriodType;
import com.sprint.findex.global.type.PerformancePeriodType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController implements DashboardApi {

    private final DashboardService dashboardService;

    @Override
    public IndexChartDto chartSelect(Long id, ChartPeriodType periodType) {
        return dashboardService.getChart(id, periodType);
    }

    @Override
    public List<RankedIndexPerformanceDto> rankSelect(
            Long indexInfoId, PerformancePeriodType periodType, Integer limit) {
        return null;
    }

    @Override
    public List<IndexPerformanceDto> favoriteSelect(PerformancePeriodType periodType) {
        return null;
    }

    @Override
    public List<IndexInfoSummaryDto> summaries() {
        return dashboardService.summaries();
    }
}
