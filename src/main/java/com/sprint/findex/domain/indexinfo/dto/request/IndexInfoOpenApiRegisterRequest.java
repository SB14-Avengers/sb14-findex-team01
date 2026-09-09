package com.sprint.findex.domain.indexinfo.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexInfoOpenApiRegisterRequest(
        String indexClassification,
        String indexName,
        int employedItemsCount,
        LocalDate baseDate,
        BigDecimal baseIndex) {}
