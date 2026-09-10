package com.sprint.findex.domain.autosyncconfig.service;

import com.sprint.findex.domain.indexinfo.entity.IndexInfo;

public interface AutoSyncConfigService {

    void initializeFor(IndexInfo indexInfo);
}
