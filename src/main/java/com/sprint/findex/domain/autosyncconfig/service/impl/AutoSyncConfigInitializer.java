package com.sprint.findex.domain.autosyncconfig.service.impl;

import com.sprint.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import com.sprint.findex.domain.autosyncconfig.repository.AutoSyncConfigRepository;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.global.exception.BusinessException;
import com.sprint.findex.global.exception.errorcode.AutoSyncConfigErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AutoSyncConfigInitializer {

    private final AutoSyncConfigRepository autoSyncConfigRepository;

    @Transactional
    public void initializeFor(IndexInfo indexInfo) {
        if (indexInfo == null) {
            throw new BusinessException(AutoSyncConfigErrorCode.INDEX_INFO_NULL);
        }
        if (autoSyncConfigRepository.existsByIndexInfo(indexInfo)) {
            throw new BusinessException(AutoSyncConfigErrorCode.ALREADY_EXISTS);
        }
        try {
            autoSyncConfigRepository.save(AutoSyncConfig.from(indexInfo));
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(AutoSyncConfigErrorCode.ALREADY_EXISTS);
        }
    }
}
