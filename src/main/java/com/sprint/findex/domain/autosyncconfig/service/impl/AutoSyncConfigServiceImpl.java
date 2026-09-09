package com.sprint.findex.domain.autosyncconfig.service.impl;

import com.sprint.findex.domain.autosyncconfig.dto.request.AutoSyncConfigUpdateRequest;
import com.sprint.findex.domain.autosyncconfig.dto.response.AutoSyncConfigDto;
import com.sprint.findex.domain.autosyncconfig.entity.AutoSyncConfig;
import com.sprint.findex.domain.autosyncconfig.mapper.AutoSyncConfigMapper;
import com.sprint.findex.domain.autosyncconfig.repository.AutoSyncConfigRepository;
import com.sprint.findex.domain.autosyncconfig.service.AutoSyncConfigService;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.global.exception.BusinessException;
import com.sprint.findex.global.exception.errorcode.AutoSyncConfigErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AutoSyncConfigServiceImpl implements AutoSyncConfigService {

    private final AutoSyncConfigRepository autoSyncConfigRepository;
    private final AutoSyncConfigMapper autoSyncConfigMapper;

    @Override
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

    @Override
    @Transactional
    public AutoSyncConfigDto update(Long id, AutoSyncConfigUpdateRequest request) {
        if (id == null) {
            throw new BusinessException(AutoSyncConfigErrorCode.ID_NULL);
        }

        if (request == null || request.enabled() == null) {
            throw new BusinessException(AutoSyncConfigErrorCode.ENABLED_REQUIRED);
        }

        AutoSyncConfig config =
                autoSyncConfigRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new BusinessException(AutoSyncConfigErrorCode.NOT_FOUND));

        config.updateEnabled(request.enabled());

        return autoSyncConfigMapper.toDto(config);
    }
}
