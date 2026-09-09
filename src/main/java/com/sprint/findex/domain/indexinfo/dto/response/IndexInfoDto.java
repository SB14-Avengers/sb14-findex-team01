package com.sprint.findex.domain.indexinfo.dto.response;

import com.sprint.findex.global.type.SourceType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexInfoDto(
        Long id,
        String indexClassification,
        String indexName,
        Integer employedItemsCount,
        LocalDate baseDate,
        BigDecimal baseIndex,
        SourceType sourceType,
        boolean favorite) {}
