package com.sprint.findex.domain.indexinfo.mapper;

import com.sprint.findex.domain.indexinfo.dto.response.IndexInfoDto;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IndexInfoMapper {
    IndexInfoDto toDto(IndexInfo indexInfo);
}
