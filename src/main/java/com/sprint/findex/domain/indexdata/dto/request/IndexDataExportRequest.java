package com.sprint.findex.domain.indexdata.dto.request;

import java.time.LocalDate;

public record IndexDataExportRequest(
        Long indexInfoId,
        LocalDate startDate,
        LocalDate endDate,
        String sortField,
        String sortDirection) {

    public IndexDataExportRequest {
        if (sortField == null || sortField.isBlank()) {
            sortField = "baseDate";
        }
        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = "desc";
        }
    }
}
