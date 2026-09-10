package com.sprint.findex.domain.syncjob.repository;

import com.sprint.findex.domain.syncjob.dto.request.SyncJobSearchRequest;
import com.sprint.findex.domain.syncjob.entity.SyncJob;
import java.util.List;

public interface SyncJobRepositoryCustom {
    List<SyncJob> search(SyncJobSearchRequest request, int limit);

    Long countBy(SyncJobSearchRequest request);
}
