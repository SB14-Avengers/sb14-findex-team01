package com.sprint.findex.domain.autosyncconfig.service;

import com.sprint.findex.domain.autosyncconfig.dto.request.AutoSyncConfigUpdateRequest;
import com.sprint.findex.domain.autosyncconfig.dto.response.AutoSyncConfigDto;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;

public interface AutoSyncConfigService {

    void initializeFor(IndexInfo indexInfo);

    AutoSyncConfigDto update(Long id, AutoSyncConfigUpdateRequest request);

    void executeAutoSync();
}
