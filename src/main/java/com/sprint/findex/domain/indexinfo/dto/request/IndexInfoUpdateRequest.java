package com.sprint.findex.domain.indexinfo.dto.request;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexInfoUpdateRequest(
        @Positive Integer employedItemsCount, // 채용 종목 수
        LocalDate basePointInTime, // 기준 시점
        @Positive BigDecimal baseIndex, // 기준 지수
        Boolean favorite // 즐겨찾기
        ) {}
