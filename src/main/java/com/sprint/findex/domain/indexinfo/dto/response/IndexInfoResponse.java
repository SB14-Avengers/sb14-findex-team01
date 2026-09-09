package com.sprint.findex.domain.indexinfo.dto.response;

import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.global.type.SourceType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record IndexInfoResponse(
        Long id,
        String indexClassification,
        String indexName,
        Integer employedItemsCount,
        LocalDate basePointInTime,
        BigDecimal baseIndex,
        SourceType sourceType,
        boolean favorite) {

    public static IndexInfoResponse from(IndexInfo indexInfo) {
        return new IndexInfoResponse(
                indexInfo.getId(),
                indexInfo.getIndexClassification(),
                indexInfo.getIndexName(),
                indexInfo.getEmployedItemsCount(),
                indexInfo.getBaseDate(),
                indexInfo.getBaseIndex(),
                indexInfo.getSourceType(),
                indexInfo.isFavorite());
    }
}
