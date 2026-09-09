package com.sprint.findex.domain.indexinfo.service.impl;

import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoCreateRequest;
import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoOpenApiRegisterRequest;
import com.sprint.findex.domain.indexinfo.dto.response.IndexInfoDto;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.domain.indexinfo.mapper.IndexInfoMapper;
import com.sprint.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.sprint.findex.domain.indexinfo.service.IndexInfoService;
import com.sprint.findex.global.exception.BusinessException;
import com.sprint.findex.global.exception.errorcode.IndexInfoErrorCode;
import com.sprint.findex.global.type.SourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexInfoServiceImpl implements IndexInfoService {
    private final IndexInfoRepository indexInfoRepository;
    private final IndexInfoMapper indexInfoMapper;

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
        return indexInfoMapper.toDto(saved);
    }
}
