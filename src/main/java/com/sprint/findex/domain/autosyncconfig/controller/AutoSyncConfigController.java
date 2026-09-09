package com.sprint.findex.domain.autosyncconfig.controller;

import com.sprint.findex.domain.autosyncconfig.dto.request.AutoSyncConfigUpdateRequest;
import com.sprint.findex.domain.autosyncconfig.dto.response.AutoSyncConfigDto;
import com.sprint.findex.domain.autosyncconfig.service.AutoSyncConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AutoSyncConfigController implements AutoSyncConfigApi {

    private final AutoSyncConfigService autoSyncConfigService;

    @Override
    public AutoSyncConfigDto update(Long id, AutoSyncConfigUpdateRequest request) {
        return autoSyncConfigService.update(id, request);
    }
}
