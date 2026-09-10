package com.sprint.findex.domain.syncjob.mapper;

import com.sprint.findex.domain.syncjob.dto.response.SyncJobDto;
import com.sprint.findex.domain.syncjob.entity.SyncJob;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SyncJobMapper {
    @Mapping(target = "indexInfoId", source = "indexInfo.id")
    @Mapping(target = "jobTime", source = "createdAt")
    SyncJobDto toDto(SyncJob syncJob);

    default LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"));
    }
}
