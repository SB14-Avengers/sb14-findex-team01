package com.sprint.findex.domain.indexdata.service.impl;

import com.sprint.findex.domain.indexdata.dto.request.IndexDataCreateRequest;
import com.sprint.findex.domain.indexdata.dto.request.IndexDataSearchRequest;
import com.sprint.findex.domain.indexdata.dto.request.IndexDataUpdateRequest;
import com.sprint.findex.domain.indexdata.dto.response.IndexDataDto;
import com.sprint.findex.domain.indexdata.entity.IndexData;
import com.sprint.findex.domain.indexdata.mapper.IndexDataMapper;
import com.sprint.findex.domain.indexdata.repository.IndexDataRepository;
import com.sprint.findex.domain.indexdata.service.IndexDataService;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import com.sprint.findex.domain.indexinfo.repository.IndexInfoRepository;
import com.sprint.findex.global.common.CursorPageResponse;
import com.sprint.findex.global.exception.BusinessException;
import com.sprint.findex.global.exception.errorcode.IndexDataErrorCode;
import com.sprint.findex.global.type.SourceType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class IndexDataServiceImpl implements IndexDataService {

    private final IndexInfoRepository indexInfoRepository;
    private final IndexDataRepository indexDataRepository;
    private final IndexDataMapper indexDataMapper;

    @Override
    @Transactional
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

    @Override
    public IndexDataDto getById(Long indexDataId) {
        IndexData indexData =
                indexDataRepository
                        .findById(indexDataId)
                        .orElseThrow(() -> new BusinessException(IndexDataErrorCode.NOT_FOUND));
        IndexDataDto indexDataDto = indexDataMapper.toDto(indexData);
        log.info(
                "지수 데이터 단건 조회 완료: indexDataId={}, indexInfoId={}, baseDate={}, sourceType={}",
                indexDataDto.id(),
                indexDataDto.indexInfoId(),
                indexDataDto.baseDate(),
                indexDataDto.sourceType());
        return indexDataDto;
    }

    @Override
    @Transactional
    public IndexDataDto update(Long indexDataId, IndexDataUpdateRequest updateRequest) {
        IndexData indexData =
                indexDataRepository
                        .findById(indexDataId)
                        .orElseThrow(() -> new BusinessException(IndexDataErrorCode.NOT_FOUND));

        indexData.update(
                updateRequest.marketPrice(),
                updateRequest.closingPrice(),
                updateRequest.highPrice(),
                updateRequest.lowPrice(),
                updateRequest.versus(),
                updateRequest.fluctuationRate(),
                updateRequest.tradingQuantity(),
                updateRequest.tradingPrice(),
                updateRequest.marketTotalAmount());

        IndexDataDto indexDataDto = indexDataMapper.toDto(indexData);
        log.info(
                "지수 데이터 수정 완료: indexDataId={}, marketPrice={}, closingPrice={}"
                        + ", highPrice={}, lowPrice={}, versus={},"
                        + " fluctuationRate={}, tradingQuantity={}, tradingPrice={},"
                        + "marketTotalAmount={}",
                indexDataDto.id(),
                indexDataDto.marketPrice(),
                indexDataDto.closingPrice(),
                indexDataDto.highPrice(),
                indexDataDto.lowPrice(),
                indexDataDto.versus(),
                indexDataDto.fluctuationRate(),
                indexDataDto.tradingQuantity(),
                indexDataDto.tradingPrice(),
                indexDataDto.marketTotalAmount());
        return indexDataDto;
    }

    @Override
    @Transactional
    public void deleteById(Long indexDataId) {
        IndexData indexData =
                indexDataRepository
                        .findById(indexDataId)
                        .orElseThrow(() -> new BusinessException(IndexDataErrorCode.NOT_FOUND));

        indexDataRepository.delete(indexData);

        log.info(
                "지수 데이터 삭제 성공: indexDataId={}, indexInfoId={}, baseDate={}, sourceType={}",
                indexData.getId(),
                indexData.getIndexInfo().getId(),
                indexData.getBaseDate(),
                indexData.getSourceType());
    }

    @Override
    public CursorPageResponse<IndexDataDto> find(IndexDataSearchRequest request) {
        int size = request.size();
        List<IndexData> found = indexDataRepository.search(request, size + 1);

        boolean hasNext = found.size() > size;
        List<IndexData> content = hasNext ? found.subList(0, size) : found;

        String nextCursor = null;
        Long nextIdAfter = null;

        if (hasNext) {
            IndexData last = content.get(content.size() - 1);
            nextCursor = cursorValue(last, request.sortField());
            nextIdAfter = last.getId();
        }

        return new CursorPageResponse<>(
                indexDataMapper.toDtoList(content),
                nextCursor,
                nextIdAfter,
                size,
                indexDataRepository.countBy(request),
                hasNext);
    }

    // Repository의 cursorPredicate()와 짝이 이뤄져야 함.
    private String cursorValue(IndexData indexData, String sortField) {
        return switch (sortField) {
            case "baseDate" -> indexData.getBaseDate().toString();
            case "marketPrice" -> indexData.getMarketPrice().toPlainString();
            case "closingPrice" -> indexData.getClosingPrice().toPlainString();
            case "highPrice" -> indexData.getHighPrice().toPlainString();
            case "lowPrice" -> indexData.getLowPrice().toPlainString();
            case "tradingQuantity" -> String.valueOf(indexData.getTradingQuantity());
            case "versus" -> indexData.getVersus().toPlainString();
            case "fluctuationRate" -> indexData.getFluctuationRate().toPlainString();
            default -> throw new BusinessException(IndexDataErrorCode.INVALID_SORT_FIELD);
        };
    }
}
