package com.sprint.findex.domain.indexdata.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record IndexDataSearchRequest(
        Long indexInfoId,
        LocalDate startDate,
        LocalDate endDate,
        @Positive(message = "idAfter는 양수여야 합니다.") Long idAfter,
        String cursor,
        @Pattern(
                        regexp =
                                "baseDate|marketPrice|closingPrice|highPrice|lowPrice|versus|"
                                        + "fluctuationRate|tradingQuantity|tradingPrice|marketTotalAmount",
                        message = "지원하지 않는 정렬 필드입니다.")
                String sortField,
        @Pattern(regexp = "asc|desc", message = "정렬 방향은 asc 또는 desc만 가능합니다.") String sortDirection,
        Integer size) {

    public IndexDataSearchRequest {
        if (sortField == null || sortField.isBlank()) {
            sortField = "baseDate";
        }
        if (sortDirection == null || sortDirection.isBlank()) {
            sortDirection = "desc";
        }
        if (size == null || size <= 0) {
            size = 10;
        }
    }

    @AssertTrue(message = "cursor와 idAfter는 함께 입력해야 합니다.")
    public boolean isCursorPairValid() {
        boolean hasCursor = cursor != null && !cursor.isBlank();
        return hasCursor == (idAfter != null);
    }

    @AssertTrue(message = "시작일은 종료일보다 늦을 수 없습니다.")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !startDate.isAfter(endDate);
    }
}
