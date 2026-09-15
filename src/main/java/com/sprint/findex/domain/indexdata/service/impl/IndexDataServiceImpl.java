package com.sprint.findex.domain.indexdata.service.impl;

import com.sprint.findex.domain.indexdata.dto.request.IndexDataCreateRequest;
import com.sprint.findex.domain.indexdata.dto.request.IndexDataExportRequest;
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
import jakarta.persistence.EntityManager;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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

    private static final int EXPORT_BATCH_SIZE = 1000;

    private final EntityManager entityManager;

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
        List<IndexDataDto> indexDataDtos = indexDataMapper.toDtoList(content);

        if (hasNext) {
            IndexDataDto last = indexDataDtos.get(indexDataDtos.size() - 1);
            nextCursor = cursorValue(last, request.sortField());
            nextIdAfter = last.id();
        }

        return new CursorPageResponse<>(
                indexDataDtos,
                nextCursor,
                nextIdAfter,
                size,
                indexDataRepository.countBy(request),
                hasNext);
    }

    @Override
    public void exportCsv(IndexDataExportRequest request, OutputStream outputStream)
            throws IOException {
        BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

        writer.write('\uFEFF');
        writer.write("기준일자,시가,종가,고가,저가,전일대비등락,등락률,거래량,거래대금,시가총액");
        writer.newLine();

        String cursor = null;
        Long idAfter = null;

        while (true) {
            List<IndexData> indexDataBatch =
                    indexDataRepository.findForExport(request, cursor, idAfter, EXPORT_BATCH_SIZE);

            if (indexDataBatch.isEmpty()) {
                break;
            }

            List<IndexDataDto> indexDataDtos = indexDataMapper.toDtoList(indexDataBatch);

            for (IndexDataDto indexDataDto : indexDataDtos) {
                writeCsvRow(writer, indexDataDto);
            }

            IndexDataDto last = indexDataDtos.get(indexDataDtos.size() - 1);
            cursor = cursorValue(last, request.sortField());
            idAfter = last.id();

            entityManager.clear();

            if (indexDataBatch.size() < EXPORT_BATCH_SIZE) {
                break;
            }
        }

        writer.flush();
    }

    private void writeCsvRow(BufferedWriter writer, IndexDataDto indexDataDto) throws IOException {
        writer.write(
                String.join(
                        ",",
                        escapeCsv(indexDataDto.baseDate()),
                        escapeCsv(indexDataDto.marketPrice().toPlainString()),
                        escapeCsv(indexDataDto.closingPrice().toPlainString()),
                        escapeCsv(indexDataDto.highPrice().toPlainString()),
                        escapeCsv(indexDataDto.lowPrice().toPlainString()),
                        escapeCsv(indexDataDto.versus().toPlainString()),
                        escapeCsv(indexDataDto.fluctuationRate().toPlainString()),
                        escapeCsv(indexDataDto.tradingQuantity()),
                        escapeCsv(indexDataDto.tradingPrice()),
                        escapeCsv(indexDataDto.marketTotalAmount())));
        writer.newLine();
    }

    private String escapeCsv(Object value) {
        if (value == null) {
            return "";
        }

        String text = String.valueOf(value).replace("\"", "\"\"");
        return "\"" + text + "\"";
    }

    // Repository의 cursorPredicate()와 짝이 이뤄져야 함.
    private String cursorValue(IndexDataDto indexDataDto, String sortField) {
        return switch (sortField) {
            case "baseDate" -> indexDataDto.baseDate().toString();
            case "marketPrice" -> indexDataDto.marketPrice().toPlainString();
            case "closingPrice" -> indexDataDto.closingPrice().toPlainString();
            case "highPrice" -> indexDataDto.highPrice().toPlainString();
            case "lowPrice" -> indexDataDto.lowPrice().toPlainString();
            case "tradingQuantity" -> String.valueOf(indexDataDto.tradingQuantity());
            case "versus" -> indexDataDto.versus().toPlainString();
            case "fluctuationRate" -> indexDataDto.fluctuationRate().toPlainString();
            case "tradingPrice" -> String.valueOf(indexDataDto.tradingPrice());
            case "marketTotalAmount" -> String.valueOf(indexDataDto.marketTotalAmount());
            default -> throw new BusinessException(IndexDataErrorCode.INVALID_SORT_FIELD);
        };
    }
}
