package com.sprint.findex.domain.syncjob.controller;

import com.sprint.findex.domain.syncjob.dto.request.SyncJobSearchRequest;
import com.sprint.findex.domain.syncjob.dto.response.SyncJobDto;
import com.sprint.findex.domain.syncjob.service.SyncJobService;
import com.sprint.findex.global.common.CursorPageResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<SyncJobDto>> indexDataSync() {
        // TODO: 12번 이슈 진행 예정
        List<SyncJobDto> syncJobs = syncJobService.indexDataSync();
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<CursorPageResponse<SyncJobDto>> findSyncJobs(
            @ParameterObject SyncJobSearchRequest request) {
        CursorPageResponse<SyncJobDto> syncJobs = syncJobService.find(request);
        return ResponseEntity.ok().body(syncJobs);
    }
}
