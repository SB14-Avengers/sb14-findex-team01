package com.sprint.findex.domain.indexdata.controller;

import com.sprint.findex.domain.indexdata.dto.request.IndexDataCreateRequest;
import com.sprint.findex.domain.indexdata.dto.request.IndexDataExportRequest;
import com.sprint.findex.domain.indexdata.dto.request.IndexDataSearchRequest;
import com.sprint.findex.domain.indexdata.dto.request.IndexDataUpdateRequest;
import com.sprint.findex.domain.indexdata.dto.response.IndexDataDto;
import com.sprint.findex.domain.indexdata.service.IndexDataService;
import com.sprint.findex.global.common.CursorPageResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequiredArgsConstructor
public class IndexDataController implements IndexDataApi {

    private final IndexDataService indexDataService;

    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");

    @Override
    public ResponseEntity<IndexDataDto> create(IndexDataCreateRequest createRequest) {
        IndexDataDto indexDataDto = indexDataService.create(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(indexDataDto);
    }

    @Override
    public ResponseEntity<IndexDataDto> getById(Long indexDataId) {
        IndexDataDto indexDataDto = indexDataService.getById(indexDataId);
        return ResponseEntity.ok(indexDataDto);
    }

    @Override
    public ResponseEntity<IndexDataDto> update(
            Long indexDataId, IndexDataUpdateRequest updateRequest) {
        IndexDataDto indexDataDto = indexDataService.update(indexDataId, updateRequest);
        return ResponseEntity.ok(indexDataDto);
    }

    @Override
    public ResponseEntity<Void> deleteById(Long indexDataId) {
        indexDataService.deleteById(indexDataId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CursorPageResponse<IndexDataDto>> find(IndexDataSearchRequest request) {
        CursorPageResponse<IndexDataDto> indexDataPage = indexDataService.find(request);
        return ResponseEntity.ok().body(indexDataPage);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> exportCsv(IndexDataExportRequest request) {
        String fileName = "index-data-" + LocalDate.now(KOREA_ZONE_ID) + ".csv";

        StreamingResponseBody stream =
                outputStream -> indexDataService.exportCsv(request, outputStream);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(fileName, StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(stream);
    }
}
