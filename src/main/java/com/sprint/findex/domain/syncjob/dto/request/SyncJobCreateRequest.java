package com.sprint.findex.domain.syncjob.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record SyncJobCreateRequest(
        @NotEmpty List<Long> indexInfoIds,
        @NotNull LocalDate baseDateFrom,
        @NotNull LocalDate baseDateTo) {}
