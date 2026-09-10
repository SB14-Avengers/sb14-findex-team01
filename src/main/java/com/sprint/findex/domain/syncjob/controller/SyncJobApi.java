package com.sprint.findex.domain.syncjob.controller;

import com.sprint.findex.domain.syncjob.dto.request.SyncJobSearchRequest;
import com.sprint.findex.domain.syncjob.dto.response.SyncJobDto;
import com.sprint.findex.global.common.CursorPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "연동 작업 API", description = "연동 작업 관리 API")
@RequestMapping(value = "/api/sync-jobs")
public interface SyncJobApi {

    @Operation(summary = "지수 정보 연동")
    @PostMapping(value = "/index-infos")
    ResponseEntity<List<SyncJobDto>> indexInfoSync();

    @Operation(summary = "지수 데이터 연동")
    @PostMapping(value = "/index-data")
    ResponseEntity<List<SyncJobDto>> indexDataSync();

    @Operation(summary = "연동 작업 목록 조회")
    @GetMapping
    ResponseEntity<CursorPageResponse<SyncJobDto>> findSyncJobs(SyncJobSearchRequest request);
}
