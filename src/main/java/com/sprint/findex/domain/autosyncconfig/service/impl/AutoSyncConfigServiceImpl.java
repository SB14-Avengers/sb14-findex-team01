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
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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

    @Override
    @Transactional
    public void executeAutoSync() {
        List<AutoSyncConfig> targets = autoSyncConfigRepository.findByEnabledTrue();

        for (AutoSyncConfig config : targets) {
            IndexInfo indexInfo = config.getIndexInfo();

            try {
                // 연동작업파트 메서드 확정 되면 여기에 추가해서 #18에 수정 예정.
                log.info("[자동연동] 대상 확인 : indexInfoId={}", indexInfo.getId());
            } catch (Exception e) {
                log.error("[자동연동] 실패 : indexInfoId={}", indexInfo.getId(), e);
                // 여기도 #18에서 수정 예정
            }
        }
    }
}
