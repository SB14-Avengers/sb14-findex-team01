package com.sprint.findex.domain.dashboard.service.impl;

import com.sprint.findex.domain.dashboard.dto.response.*;
import com.sprint.findex.domain.dashboard.mapper.DashboardMapper;
import com.sprint.findex.domain.dashboard.service.DashboardService;
import com.sprint.findex.domain.indexdata.entity.IndexData;
import com.sprint.findex.domain.indexdata.repository.IndexDataRepository;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.sprint.findex.global.exception.BusinessException;
import com.sprint.findex.global.exception.errorcode.DashboardErrorCode;
import com.sprint.findex.global.type.ChartPeriodType;
import com.sprint.findex.global.type.PerformancePeriodType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;
    private final DashboardMapper dashboardMapper;

    @Override
    @Transactional(readOnly = true)
    public IndexChartDto getChart(Long id, ChartPeriodType periodType) {

        IndexInfo indexInfo =
                indexInfoRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                DashboardErrorCode.INDEX_INFO_NOT_FOUND,
                                                "지수정보를 찾을 수 없습니다."));

        // 조회 날짜에 맞게 자르기
        LocalDate startDate =
                switch (periodType) {
                    case MONTHLY -> LocalDate.now().minusMonths(1);
                    case QUARTERLY -> LocalDate.now().minusMonths(3);
                    case YEARLY -> LocalDate.now().minusMonths(12);
                };

        List<ChartDataPoint> dataPoints =
                indexDataRepository.findChartData(id, startDate).stream()
                        .map(dashboardMapper::toChartDataDto)
                        .toList();
        List<ChartDataPoint> ma5DatePoints = dataPoint(id, 5, startDate);
        List<ChartDataPoint> ma20DataPoints = dataPoint(id, 20, startDate);

        IndexChartDto dto =
                dashboardMapper.toIndexChartDto(
                        indexInfo, periodType, dataPoints, ma5DatePoints, ma20DataPoints);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndexInfoSummaryDto> summaries() {

        List<IndexInfo> indexInfoList = indexInfoRepository.findAll();

        List<IndexInfoSummaryDto> dtos = dashboardMapper.toSummaryDto(indexInfoList);

        return dtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndexPerformanceDto> getFavorite(PerformancePeriodType periodType) {

        // 반환할것
        List<IndexPerformanceDto> dto = new ArrayList<>();

        // 일단 일간, 주간, 월간 보자
        LocalDate startDate =
                switch (periodType) {
                    case DAILY -> LocalDate.now().minusDays(1);
                    case WEEKLY -> LocalDate.now().minusWeeks(1);
                    case MONTHLY -> LocalDate.now().minusMonths(1);
                };

        // 즐겨찾기한거 infoList
        List<IndexInfo> infoList = indexInfoRepository.findByFavoriteTrue();

        for (IndexInfo indexInfo : infoList) {

            IndexPerformanceDto indexPerformanceDto = indexPerformance(indexInfo, startDate);
            if (indexPerformanceDto == null) continue;

            dto.add(indexPerformanceDto);
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RankedIndexPerformanceDto> getRankedIndex(
            Long indexInfoId, PerformancePeriodType periodType, Integer limit) {
        // 반환할 것 선언
        List<IndexPerformanceDto> tempDto = new ArrayList<>();
        List<RankedIndexPerformanceDto> dto = new ArrayList<>();

        LocalDate startDate =
                switch (periodType) {
                    case DAILY -> LocalDate.now().minusDays(1);
                    case WEEKLY -> LocalDate.now().minusWeeks(1);
                    case MONTHLY -> LocalDate.now().minusMonths(1);
                };

        List<IndexInfo> infoList = indexInfoRepository.findAll();

        // id 받았으면 그 id만 나오게 하기
        if (indexInfoId != null) {
            infoList =
                    infoList.stream()
                            .filter(indexInfo -> Objects.equals(indexInfo.getId(), indexInfoId))
                            .toList();
        }

        for (IndexInfo indexInfo : infoList) {
            IndexPerformanceDto indexPerformanceDto = indexPerformance(indexInfo, startDate);
            if (indexPerformanceDto == null) continue;

            tempDto.add(indexPerformanceDto);
        }

        // 내림차순 정렬
        tempDto.sort((o1, o2) -> o2.fluctuationRate().compareTo(o1.fluctuationRate()));

        int rank = 1;
        for (IndexPerformanceDto temp : tempDto) {
            if (rank > limit) break;

            dto.add(dashboardMapper.toRankIndex(temp, rank));
            ++rank;
        }

        return dto;
    }

    // 평균가 계산 메서드, 1. id넣기 2. 5일전 평균가면 5 넣기, 3. startDate 넣기
    private List<ChartDataPoint> dataPoint(Long id, int days, LocalDate startDate) {

        // 예시로, 다섯번째 데이터 까지는 이전 5일자 데이터를 가질 수 없으니까 추가로 -를 해준다.
        // 장이 안열리는 날이 있을수 있으니 여유분으로 3배를 해준다.
        LocalDate date = startDate.minusDays(days * 3L);

        //  시작 날짜보다 크거나 같은것 오름차순으로 정리 하기 -> 이거 그래프 그리는데 필요함
        List<IndexData> indexDataList = indexDataRepository.findChartData(id, date);
        // JPQL 로 빼라. 함수명 심플하게 정리

        // 위 indexDataList를 dto로 변경
        List<ChartDataPoint> dataPoints = new ArrayList<>();

        // 최근 5일 평균 종가
        for (int i = 0; i < indexDataList.size(); i++) {

            IndexData current = indexDataList.get(i);

            // 만약 날짜가 시작 날짜 전거면 날리기
            if (current.getBaseDate().isBefore(startDate)) continue;

            if (i < days - 1) continue; // 5일 전 없으면 재끼기

            BigDecimal maDateSum = BigDecimal.ZERO;
            for (int j = 0; j < days; j++) {
                maDateSum = maDateSum.add(indexDataList.get(i - j).getClosingPrice());
            }

            // 5일 평균 구하고, 소숫점 3자리 에서 반올림,
            BigDecimal maDateAvg =
                    maDateSum.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_EVEN);

            // 더하기
            dataPoints.add(
                    dashboardMapper.toMovingAverageDto(
                            indexDataList.get(i).getBaseDate(), maDateAvg));
        }

        return dataPoints;
    }

    // 현재가, 과거가, 대비, 등락률 계산해서 IndexPerformanceDto로 묶어서 반환한다.
    // 인자로 받을 것 indexInfo, startDate
    private IndexPerformanceDto indexPerformance(IndexInfo indexInfo, LocalDate startDate) {

        BigDecimal currentPrice = indexDataRepository.findByCurrentPrice(indexInfo.getId());
        BigDecimal beforePrice =
                indexDataRepository.findByBeforePrice(startDate, indexInfo.getId());
        // 값이 비어있거나, beforePrice가 0이면(0으로 나누는것 방지)
        if (currentPrice == null
                || beforePrice == null
                || beforePrice.compareTo(BigDecimal.ZERO) == 0) return null;
        BigDecimal versus = currentPrice.subtract(beforePrice);
        BigDecimal fluctuationRate =
                versus.multiply(BigDecimal.valueOf(100))
                        .divide(beforePrice, 4, RoundingMode.HALF_EVEN);

        return dashboardMapper.toPerformanceDto(
                indexInfo, versus, fluctuationRate, currentPrice, beforePrice);
    }
}
