package com.sprint.findex.domain.dashboard.controller;

import com.sprint.findex.domain.dashboard.dto.response.IndexChartDto;
import com.sprint.findex.domain.dashboard.dto.response.IndexInfoSummaryDto;
import com.sprint.findex.domain.dashboard.dto.response.IndexPerformanceDto;
import com.sprint.findex.domain.dashboard.dto.response.RankedIndexPerformanceDto;
import com.sprint.findex.global.type.ChartPeriodType;
import com.sprint.findex.global.type.PerformancePeriodType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "지수 데이터 API", description = "지수 데이터 관리 API")
@RequestMapping(value = "/api")
public interface DashboardApi {

    @Operation(summary = "지수 차트 조회")
    @GetMapping(value = "/index-data/{id}/chart")
    ResponseEntity<IndexChartDto> chartSelect(
            @PathVariable Long id,
            @RequestParam(defaultValue = "MONTHLY") ChartPeriodType periodType);

    @Operation(summary = "지수 성과 랭킹 조회")
    @GetMapping(value = "/index-data/performance/rank")
    ResponseEntity<List<RankedIndexPerformanceDto>> rankSelect(
            @RequestParam(required = false) Long indexInfoId,
            @RequestParam(defaultValue = "DAILY") PerformancePeriodType periodType,
            @RequestParam(defaultValue = "10") Integer limit);

    @Operation(summary = "관심 지수 성과 조회")
    @GetMapping(value = "/index-data/performance/favorite")
    ResponseEntity<List<IndexPerformanceDto>> favoriteSelect(
            @RequestParam(defaultValue = "DAILY") PerformancePeriodType periodType);

    @Tag(name = "지수 정보 API", description = "지수 정보 목록 조회")
    @Operation(summary = "지수 정보 요약 목록 조회")
    @GetMapping(value = "/index-infos/summaries")
    ResponseEntity<List<IndexInfoSummaryDto>> summaries();
}
