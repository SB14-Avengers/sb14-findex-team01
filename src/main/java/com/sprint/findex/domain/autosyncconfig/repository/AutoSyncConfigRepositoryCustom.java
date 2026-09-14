package com.sprint.findex.domain.autosyncconfig.repository;

import com.sprint.findex.domain.autosyncconfig.dto.request.AutoSyncConfigSearchRequest;
import com.sprint.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import java.util.List;

public interface AutoSyncConfigRepositoryCustom {

    List<AutoSyncConfig> search(AutoSyncConfigSearchRequest request);

    long count(AutoSyncConfigSearchRequest request);
}
