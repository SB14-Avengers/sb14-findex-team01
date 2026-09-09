package com.sprint.findex.domain.dashboard.dto.response;

import com.sprint.findex.domain.indexdata.entity.IndexData;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ChartDataPoint(LocalDate date, BigDecimal value) {

    // 종가 반환
    public static ChartDataPoint from(IndexData data) {
        return new ChartDataPoint(data.getBaseDate(), data.getClosingPrice());
    }
}
