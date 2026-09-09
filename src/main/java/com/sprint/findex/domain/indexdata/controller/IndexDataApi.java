package com.sprint.findex.domain.indexdata.controller;

import com.sprint.findex.domain.indexdata.dto.request.IndexDataCreateRequest;
import com.sprint.findex.domain.indexdata.dto.response.IndexDataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "지수 데이터 관리", description = "지수 데이터 API")
@RequestMapping("/api/index-data")
public interface IndexDataApi {
    @Operation(summary = "지수 데이터 등록")
    @PostMapping
    ResponseEntity<IndexDataDto> create(@Valid @RequestBody IndexDataCreateRequest createRequest);
}
