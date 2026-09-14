package com.sprint.findex.domain.indexinfo.dto.request;

public record IndexInfoSearchRequest(
        String indexClassification,
        String indexName,
        Boolean favorite,
        Long idAfter,
        String cursor,
        String sortField,
        String sortDirection,
        Integer size) {
    public String sortFieldOrDefault() {
        return sortField != null ? sortField : "indexClassification";
    }

    public String sortDirectionOrDefault() {
        return sortDirection != null ? sortDirection : "asc";
    }

    public int sizeOrDefault() {
        return size != null ? size : 10;
    }
}
