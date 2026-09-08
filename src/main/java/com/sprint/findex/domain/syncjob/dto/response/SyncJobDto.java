package com.sprint.findex.domain.syncjob.dto.response;

import com.sprint.findex.global.type.JobResult;
import com.sprint.findex.global.type.JobType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SyncJobDto(
        Long id,
        JobType jobType,
        Long indexInfoId,
        LocalDate targetDate,
        String worker,
        LocalDateTime jobTime,
        JobResult result) {}
