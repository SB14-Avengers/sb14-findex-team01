package com.sprint.findex.domain.syncjob.controller;

import com.sprint.findex.domain.syncjob.dto.request.SyncJobCreateRequest;
import com.sprint.findex.domain.syncjob.dto.request.SyncJobSearchRequest;
import com.sprint.findex.domain.syncjob.dto.response.SyncJobDto;
import com.sprint.findex.domain.syncjob.service.SyncJobService;
import com.sprint.findex.global.common.CursorPageResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SyncJobController implements SyncJobApi {
    private final SyncJobService syncJobService;

    @Override
    public ResponseEntity<List<SyncJobDto>> indexInfoSync() {
        List<SyncJobDto> syncJobs = syncJobService.indexInfoSync();
        return ResponseEntity.accepted().body(syncJobs);
    }

    @Override
    public ResponseEntity<List<SyncJobDto>> indexDataSync(
            @RequestBody SyncJobCreateRequest request) {
        List<SyncJobDto> syncJobs = syncJobService.indexDataSync(request);
        return ResponseEntity.accepted().body(syncJobs);
    }

    @Override
    public ResponseEntity<CursorPageResponse<SyncJobDto>> findSyncJobs(
            @ParameterObject SyncJobSearchRequest request) {
        CursorPageResponse<SyncJobDto> syncJobs = syncJobService.find(request);
        return ResponseEntity.ok().body(syncJobs);
    }
}
