package com.sprint.findex.domain.autosyncconfig.service.impl;

import com.sprint.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import com.sprint.findex.domain.autosyncconfig.repository.AutoSyncConfigRepository;
import com.sprint.findex.domain.autosyncconfig.service.AutoSyncConfigService;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AutoSyncConfigServiceImpl implements AutoSyncConfigService {

    private final AutoSyncConfigRepository autoSyncConfigRepository;

    @Override
    @Transactional
    public void initializeFor(IndexInfo indexInfo) {
        autoSyncConfigRepository.save(AutoSyncConfig.from(indexInfo));
    }
}
