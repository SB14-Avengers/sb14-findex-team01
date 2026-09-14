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
import java.math.BigDecimal;
import java.time.LocalDate;
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
        RegisterParams params =
                new RegisterParams(
                        request.indexClassification(),
                        request.indexName(),
                        request.employedItemsCount(),
                        request.basePointInTime(),
                        request.baseIndex(),
                        SourceType.USER,
                        Boolean.TRUE.equals(request.favorite()));
        return register(params);
    }

    @Override
    @Transactional
    public IndexInfoDto registerFromOpenApi(IndexInfoOpenApiRegisterRequest request) {
        RegisterParams params =
                new RegisterParams(
                        request.indexClassification(),
                        request.indexName(),
                        request.employedItemsCount(),
                        request.basePointInTime(),
                        request.baseIndex(),
                        SourceType.OPEN_API,
                        false);
        return register(params);
    }

    private IndexInfoDto register(RegisterParams params) {
        if (indexInfoRepository.existsByIndexClassificationAndIndexName(
                params.indexClassification(), params.indexName())) {
            throw new BusinessException(IndexInfoErrorCode.DUPLICATE);
        }

        IndexInfo indexInfo =
                IndexInfo.of(
                        params.indexClassification(),
                        params.indexName(),
                        params.employedItemsCount(),
                        params.basePointInTime(),
                        params.baseIndex(),
                        params.sourceType(),
                        params.favorite());

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

        boolean hasNext = results.size() > request.sizeOrDefault();
        List<IndexInfo> content =
                hasNext
                        ? results.subList(0, request.sizeOrDefault()) // 초과분 잘라내기
                        : results;

        List<IndexInfoDto> dtos = content.stream().map(indexInfoMapper::toDto).toList();
        Long nextIdAfter = null;
        String nextCursor = null;

        if (!content.isEmpty()) {
            IndexInfo last = content.get(content.size() - 1);
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

    private record RegisterParams(
            String indexClassification,
            String indexName,
            Integer employedItemsCount,
            LocalDate basePointInTime,
            BigDecimal baseIndex,
            SourceType sourceType,
            boolean favorite) {}
}
