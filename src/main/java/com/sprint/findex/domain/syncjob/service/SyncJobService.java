package com.sprint.findex.domain.syncjob.service;

import com.sprint.findex.domain.syncjob.dto.request.SyncJobSearchRequest;
import com.sprint.findex.domain.syncjob.dto.response.SyncJobDto;
import com.sprint.findex.global.common.CursorPageResponse;
import java.util.List;

public interface SyncJobService {
    List<SyncJobDto> indexInfoSync();

    List<SyncJobDto> indexDataSync();

    CursorPageResponse<SyncJobDto> find(SyncJobSearchRequest request);
}
