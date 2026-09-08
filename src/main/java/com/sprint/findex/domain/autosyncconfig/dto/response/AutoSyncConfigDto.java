package com.sprint.findex.domain.autosyncconfig.dto.response;

public record AutoSyncConfigDto(
        Long id, Long indexInfoId, String indexClassification, String indexName, boolean enabled) {}
