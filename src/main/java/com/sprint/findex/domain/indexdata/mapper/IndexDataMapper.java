package com.sprint.findex.domain.indexdata.mapper;

import com.sprint.findex.domain.indexdata.dto.response.IndexDataDto;
import com.sprint.findex.domain.indexdata.entity.IndexData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IndexDataMapper {

    @Mapping(source = "indexInfo.id", target = "indexInfoId")
    IndexDataDto toDto(IndexData indexData);
}
