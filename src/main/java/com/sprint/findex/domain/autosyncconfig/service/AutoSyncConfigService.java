package com.sprint.findex.domain.autosyncconfig.service;

import com.sprint.findex.domain.autosyncconfig.dto.request.AutoSyncConfigSearchRequest;
import com.sprint.findex.domain.autosyncconfig.dto.request.AutoSyncConfigUpdateRequest;
import com.sprint.findex.domain.autosyncconfig.dto.response.AutoSyncConfigDto;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.global.common.CursorPageResponse;

public interface AutoSyncConfigService {

    void initializeFor(IndexInfo indexInfo);

    AutoSyncConfigDto update(Long id, AutoSyncConfigUpdateRequest request);

    void executeAutoSync();

    CursorPageResponse<AutoSyncConfigDto> getAutoSyncConfigList(
            AutoSyncConfigSearchRequest request);
}
