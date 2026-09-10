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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;

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
                        .map(ChartDataPoint::from)
                        .toList();
        List<ChartDataPoint> ma5DatePoints = dataPoint(id, 5, startDate);
        List<ChartDataPoint> ma20DataPoints = dataPoint(id, 20, startDate);

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

    // 평균가 계산 메서드, 1. id넣기 2. 5일전 평균가면 5 넣기, 3. startDate 넣기
    private List<ChartDataPoint> dataPoint(Long id, int days, LocalDate startDate) {

        // 다섯번째 데이터 까지는 이전 5일자 데이터를 가질 수 없으니까 추가로 -를 해준다.
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
            dataPoints.add(ChartDataPoint.of(indexDataList.get(i).getBaseDate(), maDateAvg));
        }

        return dataPoints;
    }
}
