package com.sprint.findex.domain.indexdata.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record IndexDataExportRequest(
        Long indexInfoId,
        LocalDate startDate,
        LocalDate endDate,
        @Pattern(
                        regexp =
                                "baseDate|marketPrice|closingPrice|highPrice|lowPrice|versus|"
                                        + "fluctuationRate|tradingQuantity|tradingPrice|marketTotalAmount",
                        message = "지원하지 않는 정렬 필드입니다.")
                String sortField,
        @Pattern(regexp = "asc|desc", message = "정렬 방향은 asc 또는 desc만 가능합니다.")
                String sortDirection) {

    public IndexDataExportRequest {
        if (sortField == null || sortField.isBlank()) {
            sortField = "baseDate";
        }
        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = "desc";
        }
    }

    @AssertTrue(message = "시작일은 종료일보다 늦을 수 없습니다.")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !startDate.isAfter(endDate);
    }
}
