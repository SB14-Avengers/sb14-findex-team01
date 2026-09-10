package com.sprint.findex.domain.dashboard.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ChartDataPoint(LocalDate date, BigDecimal value) {}
