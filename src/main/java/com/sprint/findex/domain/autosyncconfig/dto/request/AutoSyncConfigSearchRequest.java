package com.sprint.findex.domain.autosyncconfig.dto.request;

import jakarta.validation.constraints.Pattern;

public record AutoSyncConfigSearchRequest(
        Long indexInfoId,
        Boolean enabled,
        Long idAfter,
        String cursor,
        @Pattern(regexp = "indexInfo\\.indexName|enabled", message = "지원하지 않는 정렬 필드입니다.")
                String sortField,
        @Pattern(regexp = "asc|desc", message = "정렬 방향은 asc 또는 desc여야 합니다.") String sortDirection,
        Integer size) {
    public AutoSyncConfigSearchRequest {
        if (size == null || size <= 0) {
            size = 10;
        }
    }

    public String sortFieldOrDefault() {
        return sortField != null ? sortField : "indexInfo.indexName";
    }

    public String sortDirectionOrDefault() {
        return sortDirection != null ? sortDirection : "asc";
    }
}
