package com.sprint.findex.domain.autosyncconfig.controller;

import com.sprint.findex.domain.autosyncconfig.dto.request.AutoSyncConfigSearchRequest;
import com.sprint.findex.domain.autosyncconfig.dto.request.AutoSyncConfigUpdateRequest;
import com.sprint.findex.domain.autosyncconfig.dto.response.AutoSyncConfigDto;
import com.sprint.findex.global.common.CursorPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Tag(name = "자동 연동 설정 관리", description = "자동 연동 설정 API")
@RequestMapping("/api/auto-sync-configs")
public interface AutoSyncConfigApi {

    @Operation(summary = "자동 연동 설정 수정")
    @PatchMapping("/{id}")
    AutoSyncConfigDto update(
            @PathVariable Long id, @Valid @RequestBody AutoSyncConfigUpdateRequest request);

    @Operation(summary = "자동 연동 설정 목록 조회")
    @GetMapping
    CursorPageResponse<AutoSyncConfigDto> getAutoSyncConfigList(
            @ParameterObject @Valid AutoSyncConfigSearchRequest request);
}
