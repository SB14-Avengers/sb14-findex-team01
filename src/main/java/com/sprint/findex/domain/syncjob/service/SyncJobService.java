package com.sprint.findex.domain.syncjob.service;

import com.sprint.findex.domain.syncjob.dto.response.SyncJobDto;
import java.util.List;

public interface SyncJobService {
    List<SyncJobDto> indexInfoSync() throws Exception;

    List<SyncJobDto> indexDataSync();
}
