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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController implements DashboardApi {

    private final DashboardService dashboardService;

    @Override
    public ResponseEntity<IndexChartDto> chartSelect(Long id, ChartPeriodType periodType) {
        IndexChartDto chart = dashboardService.getChart(id, periodType);
        return ResponseEntity.ok(chart);
    }

    // 살려줘
    @Override
    public ResponseEntity<List<RankedIndexPerformanceDto>> rankSelect(
            Long indexInfoId, PerformancePeriodType periodType, Integer limit) {
        return null;
    }

    @Override
    public ResponseEntity<List<IndexPerformanceDto>> favoriteSelect(
            PerformancePeriodType periodType) {
        List<IndexPerformanceDto> favorite = dashboardService.getFavorite(periodType);
        return ResponseEntity.ok(favorite);
    }

    @Override
    public ResponseEntity<List<IndexInfoSummaryDto>> summaries() {
        List<IndexInfoSummaryDto> summaries = dashboardService.summaries();
        return ResponseEntity.ok(summaries);
    }
}
