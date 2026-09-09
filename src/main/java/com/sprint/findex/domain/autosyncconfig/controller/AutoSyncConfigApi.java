package com.sprint.findex.domain.autosyncconfig.controller;

import com.sprint.findex.domain.autosyncconfig.dto.request.AutoSyncConfigUpdateRequest;
import com.sprint.findex.domain.autosyncconfig.dto.response.AutoSyncConfigDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "자동 연동 설정 관리", description = "자동 연동 설정 API")
@RequestMapping("/api/auto-sync-configs")
public interface AutoSyncConfigApi {

    @Operation(summary = "자동 연동 설정 수정")
    @PatchMapping("/{id}")
    AutoSyncConfigDto update(
            @PathVariable Long id, @Valid @RequestBody AutoSyncConfigUpdateRequest request);
}
