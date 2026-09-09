package com.sprint.findex.domain.indexdata.controller;

import com.sprint.findex.domain.indexdata.dto.request.IndexDataCreateRequest;
import com.sprint.findex.domain.indexdata.dto.response.IndexDataDto;
import com.sprint.findex.domain.indexdata.service.IndexDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class IndexDataController implements IndexDataApi {

    private final IndexDataService indexDataService;

    @PostMapping
    public ResponseEntity<IndexDataDto> create(
            @Valid @RequestBody IndexDataCreateRequest createRequest) {
        IndexDataDto indexDataDto = indexDataService.create(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(indexDataDto);
    }
}
