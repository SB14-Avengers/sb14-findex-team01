package com.sprint.findex.domain.indexinfo.service.impl;

import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoCreateRequest;
import com.sprint.findex.domain.indexinfo.dto.response.IndexInfoResponse;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.sprint.findex.domain.indexinfo.service.IndexInfoService;
import com.sprint.findex.global.type.SourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexInfoServiceImpl implements IndexInfoService {
    private final IndexInfoRepository indexInfoRepository;

    @Override
    @Transactional
    public IndexInfoResponse register(IndexInfoCreateRequest request) {
        if (indexInfoRepository.existsByIndexClassificationAndIndexName(
                request.indexClassification(), request.indexName())) {
            throw new IllegalArgumentException("이미 등록된 지수입니다.");
        }

        IndexInfo indexInfo =
                IndexInfo.of(
                        request.indexClassification(),
                        request.indexName(),
                        request.employedItemsCount(),
                        request.baseDate(),
                        request.baseIndex(),
                        SourceType.USER,
                        Boolean.TRUE.equals(request.favorite()));

        IndexInfo saved = indexInfoRepository.save(indexInfo);

        return IndexInfoResponse.from(saved);
    }
}
