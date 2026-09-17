package com.sprint.findex.domain.autosyncconfig.mapper;

import com.sprint.findex.domain.autosyncconfig.dto.response.AutoSyncConfigDto;
import com.sprint.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AutoSyncConfigMapper {

    @Mapping(target = "indexInfoId", source = "indexInfo.id")
    @Mapping(target = "indexClassification", source = "indexInfo.indexClassification")
    @Mapping(target = "indexName", source = "indexInfo.indexName")
    AutoSyncConfigDto toDto(AutoSyncConfig autoSyncConfig);
}
