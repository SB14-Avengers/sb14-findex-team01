package com.sprint.findex.domain.autosyncconfig.dto.request;

import jakarta.validation.constraints.NotNull;

public record AutoSyncConfigUpdateRequest(@NotNull Boolean enabled) {}
