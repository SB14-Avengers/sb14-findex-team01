package com.sprint.findex.domain.indexdata.controller;

import com.sprint.findex.domain.indexdata.dto.request.IndexDataCreateRequest;
import com.sprint.findex.domain.indexdata.dto.request.IndexDataUpdateRequest;
import com.sprint.findex.domain.indexdata.dto.response.IndexDataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "지수 데이터 관리", description = "지수 데이터 API")
@RequestMapping("/api/index-data")
public interface IndexDataApi {
    @Operation(summary = "지수 데이터 등록")
    @PostMapping
    ResponseEntity<IndexDataDto> create(@Valid @RequestBody IndexDataCreateRequest createRequest);

    @Operation(summary = "지수 데이터 단건 조회")
    @GetMapping("/{indexDataId}")
    ResponseEntity<IndexDataDto> getById(@PathVariable Long indexDataId);

    @Operation(summary = "지수 데이터 수정")
    @PatchMapping("/{indexDataId}")
    ResponseEntity<IndexDataDto> update(
            @PathVariable Long indexDataId,
            @Valid @RequestBody IndexDataUpdateRequest updateRequest);

    @Operation(summary = "지수 데이터 삭제")
    @DeleteMapping("/{indexDataId}")
    ResponseEntity<Void> deleteById(@PathVariable Long indexDataId);
}
