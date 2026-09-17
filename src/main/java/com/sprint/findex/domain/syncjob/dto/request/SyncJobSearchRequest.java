package com.sprint.findex.domain.syncjob.dto.request;

import com.sprint.findex.global.type.JobResult;
import com.sprint.findex.global.type.JobType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SyncJobSearchRequest(
        JobType jobType,
        Long indexInfoId,
        LocalDate baseDateFrom,
        LocalDate baseDateTo,
        String worker,
        LocalDateTime jobTimeFrom,
        LocalDateTime jobTimeTo,
        JobResult status,
        Long idAfter,
        String cursor,
        String sortField,
        String sortDirection,
        Integer size) {
    public SyncJobSearchRequest {
        if (sortField == null || sortField.isBlank()) sortField = "jobTime";
        if (sortDirection == null || sortDirection.isBlank()) sortDirection = "desc";
        if (size == null || size <= 0) size = 10;
    }
}
