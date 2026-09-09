package com.sprint.findex.domain.indexinfo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

// api-docs(레퍼런스 서버) 기준 favorite 제외 전 필드 required.
public record IndexInfoCreateRequest(
        @NotBlank(message = "지수 분류명은 필수입니다") String indexClassification,
        @NotBlank(message = "지수명은 필수입니다") String indexName,
        @NotNull @Positive Integer employedItemsCount,
        @NotNull LocalDate basePointInTime,
        @NotNull BigDecimal baseIndex,
        Boolean favorite) {}
