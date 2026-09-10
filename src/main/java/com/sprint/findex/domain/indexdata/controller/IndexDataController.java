package com.sprint.findex.domain.indexdata.controller;

import com.sprint.findex.domain.indexdata.dto.request.IndexDataCreateRequest;
import com.sprint.findex.domain.indexdata.dto.request.IndexDataUpdateRequest;
import com.sprint.findex.domain.indexdata.dto.response.IndexDataDto;
import com.sprint.findex.domain.indexdata.service.IndexDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class IndexDataController implements IndexDataApi {

    private final IndexDataService indexDataService;

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
}
