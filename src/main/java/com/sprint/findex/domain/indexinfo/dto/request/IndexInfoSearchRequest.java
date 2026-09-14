package com.sprint.findex.domain.indexinfo.dto.request;

import jakarta.validation.constraints.Pattern;

public record IndexInfoSearchRequest(
        String indexClassification,
        String indexName,
        Boolean favorite,
        Long idAfter,
        String cursor,
        @Pattern(
                        regexp = "indexClassification|indexName|employedItemsCount",
                        message = "지원하지 않는 정렬 필드입니다.")
                String sortField,
        @Pattern(regexp = "asc|desc", message = "정렬 방향은 asc 또는 desc여야 합니다.") String sortDirection,
        Integer size) {

    public IndexInfoSearchRequest {
        if (size == null || size <= 0) {
            size = 10;
        }
    }

    public String sortFieldOrDefault() {
        return sortField != null ? sortField : "indexClassification";
    }

    public String sortDirectionOrDefault() {
        return sortDirection != null ? sortDirection : "asc";
    }

    public int sizeOrDefault() {
        return size;
    }
}
