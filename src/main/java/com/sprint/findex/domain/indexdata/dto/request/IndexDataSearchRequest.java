package com.sprint.findex.domain.indexdata.dto.request;

import java.time.LocalDate;

public record IndexDataSearchRequest(
        Long indexInfoId,
        LocalDate startDate,
        LocalDate endDate,
        Long idAfter,
        String cursor,
        String sortField,
        String sortDirection,
        Integer size) {
    public IndexDataSearchRequest {
        if (sortField == null || sortField.isBlank()) sortField = "baseDate";
        if (sortDirection == null || sortDirection.isBlank()) sortDirection = "desc";
        if (size == null || size <= 0) size = 10;
    }
}
