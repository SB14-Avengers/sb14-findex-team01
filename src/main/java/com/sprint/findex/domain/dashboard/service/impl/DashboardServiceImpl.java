package com.sprint.findex.domain.dashboard.service.impl;

import com.sprint.findex.domain.dashboard.dto.response.ChartDataPoint;
import com.sprint.findex.domain.dashboard.dto.response.IndexChartDto;
import com.sprint.findex.domain.dashboard.dto.response.IndexInfoSummaryDto;
import com.sprint.findex.domain.dashboard.service.DashboardService;
import com.sprint.findex.domain.indexdata.entity.IndexData;
import com.sprint.findex.domain.indexdata.repository.IndexDataRepository;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.sprint.findex.global.exception.BusinessException;
import com.sprint.findex.global.exception.errorcode.DashboardErrorCode;
import com.sprint.findex.global.type.ChartPeriodType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;

    @Override
    public IndexChartDto getChart(Long id, ChartPeriodType periodType) {

        IndexInfo indexInfo =
                indexInfoRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                DashboardErrorCode.INDEX_INFO_NOT_FOUND,
                                                "지수정보를 찾을 수 없습니다."));

        List<IndexData> indexDataList = indexDataRepository.findByIndexInfoIdOrderByBaseDateAsc(id);

        List<ChartDataPoint> dataPoints = indexDataList.stream().map(ChartDataPoint::from).toList();

        List<ChartDataPoint> ma5DatePoints = List.of();
        List<ChartDataPoint> ma20DataPoints = List.of();

        IndexChartDto dto =
                IndexChartDto.of(indexInfo, periodType, dataPoints, ma5DatePoints, ma20DataPoints);

        return dto;
    }

    @Override
    public List<IndexInfoSummaryDto> summaries() {

        List<IndexInfo> indexInfoList = indexInfoRepository.findAll();

        List<IndexInfoSummaryDto> dtos =
                indexInfoList.stream().map(IndexInfoSummaryDto::from).toList();

        return dtos;
    }
}
