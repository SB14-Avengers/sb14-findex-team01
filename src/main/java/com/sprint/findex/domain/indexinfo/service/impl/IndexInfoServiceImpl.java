package com.sprint.findex.domain.indexinfo.service.impl;

import com.sprint.findex.domain.autosyncconfig.service.AutoSyncConfigService;
import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoCreateRequest;
import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoOpenApiRegisterRequest;
import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoSearchRequest;
import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoUpdateRequest;
import com.sprint.findex.domain.indexinfo.dto.response.IndexInfoDto;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.domain.indexinfo.mapper.IndexInfoMapper;
import com.sprint.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.sprint.findex.domain.indexinfo.service.IndexInfoService;
import com.sprint.findex.global.common.CursorPageResponse;
import com.sprint.findex.global.exception.BusinessException;
import com.sprint.findex.global.exception.errorcode.IndexInfoErrorCode;
import com.sprint.findex.global.type.SourceType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexInfoServiceImpl implements IndexInfoService {
    private final IndexInfoRepository indexInfoRepository;
    private final IndexInfoMapper indexInfoMapper;
    private final AutoSyncConfigService autoSyncConfigService;

    @Override
    @Transactional
    public IndexInfoDto registerFromUser(IndexInfoCreateRequest request) {
        if (indexInfoRepository.existsByIndexClassificationAndIndexName(
                request.indexClassification(), request.indexName())) {
            throw new BusinessException(IndexInfoErrorCode.DUPLICATE);
        }

        IndexInfo indexInfo =
                IndexInfo.of(
                        request.indexClassification(),
                        request.indexName(),
                        request.employedItemsCount(),
                        request.basePointInTime(),
                        request.baseIndex(),
                        SourceType.USER,
                        Boolean.TRUE.equals(request.favorite()));

        IndexInfo saved = indexInfoRepository.save(indexInfo);
        autoSyncConfigService.initializeFor(saved);
        return indexInfoMapper.toDto(saved);
    }

    @Override
    @Transactional
    public IndexInfoDto registerFromOpenApi(IndexInfoOpenApiRegisterRequest request) {
        if (indexInfoRepository.existsByIndexClassificationAndIndexName(
                request.indexClassification(), request.indexName())) {
            throw new BusinessException(IndexInfoErrorCode.DUPLICATE);
        }

        IndexInfo indexInfo =
                IndexInfo.of(
                        request.indexClassification(),
                        request.indexName(),
                        request.employedItemsCount(),
                        request.basePointInTime(),
                        request.baseIndex(),
                        SourceType.OPEN_API,
                        false);

        IndexInfo saved = indexInfoRepository.save(indexInfo);
        autoSyncConfigService.initializeFor(saved);
        return indexInfoMapper.toDto(saved);
    }

    @Override
    public IndexInfoDto getById(Long id) {
        IndexInfo indexInfo =
                indexInfoRepository
                        .findById(id)
                        .orElseThrow(() -> new BusinessException(IndexInfoErrorCode.NOT_FOUND));
        return indexInfoMapper.toDto(indexInfo);
    }

    @Override
    @Transactional
    public IndexInfoDto update(Long id, IndexInfoUpdateRequest request) {
        IndexInfo indexInfo =
                indexInfoRepository
                        .findById(id)
                        .orElseThrow(() -> new BusinessException(IndexInfoErrorCode.NOT_FOUND));

        indexInfo.update(
                request.employedItemsCount(),
                request.basePointInTime(),
                request.baseIndex(),
                request.favorite());

        return indexInfoMapper.toDto(indexInfo);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        IndexInfo indexInfo =
                indexInfoRepository
                        .findById(id)
                        .orElseThrow(() -> new BusinessException(IndexInfoErrorCode.NOT_FOUND));
        indexInfoRepository.delete(indexInfo);
    }

    @Override
    public CursorPageResponse<IndexInfoDto> getIndexInfoList(IndexInfoSearchRequest request) {
        List<IndexInfo> results = indexInfoRepository.search(request);

        List<IndexInfoDto> dtos = results.stream().map(indexInfoMapper::toDto).toList();

        boolean hasNext = dtos.size() == request.sizeOrDefault();
        Long nextIdAfter = null;
        String nextCursor = null;

        if (!results.isEmpty()) {
            IndexInfo last = results.get(results.size() - 1);
            nextIdAfter = last.getId();
            nextCursor =
                    switch (request.sortFieldOrDefault()) {
                        case "indexName" -> last.getIndexName();
                        case "employedItemsCount" -> String.valueOf(last.getEmployedItemsCount());
                        default -> last.getIndexClassification();
                    };
        }
        long totalElements = indexInfoRepository.count(request);

        return new CursorPageResponse<>(
                dtos, nextCursor, nextIdAfter, request.sizeOrDefault(), totalElements, hasNext);
    }
}
