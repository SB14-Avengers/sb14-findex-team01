package com.sprint.findex.domain.indexdata.service.impl;

import com.sprint.findex.domain.indexdata.dto.request.IndexDataCreateRequest;
import com.sprint.findex.domain.indexdata.dto.response.IndexDataDto;
import com.sprint.findex.domain.indexdata.entity.IndexData;
import com.sprint.findex.domain.indexdata.mapper.IndexDataMapper;
import com.sprint.findex.domain.indexdata.repository.IndexDataRepository;
import com.sprint.findex.domain.indexdata.service.IndexDataService;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.sprint.findex.global.exception.BusinessException;
import com.sprint.findex.global.exception.errorcode.IndexDataErrorCode;
import com.sprint.findex.global.type.SourceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexDataServiceImpl implements IndexDataService {

    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;
    private final IndexDataMapper indexDataMapper;

    @Override
    public IndexDataDto create(IndexDataCreateRequest createRequest) {

        IndexInfo indexInfo =
                indexInfoRepository
                        .findById(createRequest.indexInfoId())
                        .orElseThrow(
                                () ->
                                        new BusinessException(
                                                IndexDataErrorCode.INDEX_INFO_NOT_FOUND));

        if (indexDataRepository.existsByIndexInfoIdAndBaseDate(
                createRequest.indexInfoId(), createRequest.baseDate())) {
            throw new BusinessException(IndexDataErrorCode.DUPLICATE);
        }

        IndexData indexData =
                IndexData.of(
                        indexInfo,
                        createRequest.baseDate(),
                        SourceType.USER,
                        createRequest.marketPrice(),
                        createRequest.closingPrice(),
                        createRequest.highPrice(),
                        createRequest.lowPrice(),
                        createRequest.versus(),
                        createRequest.fluctuationRate(),
                        createRequest.tradingQuantity(),
                        createRequest.tradingPrice(),
                        createRequest.marketTotalAmount());

        IndexData savedIndexData = indexDataRepository.save(indexData);

        IndexDataDto indexDataDto = indexDataMapper.toDto(savedIndexData);
        log.info(
                "지수 데이터 생성 완료: indexInfoId={}, baseDate={}, sourceType={}",
                indexDataDto.indexInfoId(),
                indexDataDto.baseDate(),
                indexDataDto.sourceType());

        return indexDataDto;
    }
}
