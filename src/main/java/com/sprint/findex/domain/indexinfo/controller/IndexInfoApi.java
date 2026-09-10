package com.sprint.findex.domain.indexinfo.controller;

import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoCreateRequest;
import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoUpdateRequest;
import com.sprint.findex.domain.indexinfo.dto.response.IndexInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "지수 정보 관리", description = "지수 정보 API")
@RequestMapping("/api/index-infos")
public interface IndexInfoApi {
    @Operation(summary = "지수 정보 등록")
    @PostMapping
    ResponseEntity<IndexInfoDto> register(@RequestBody @Valid IndexInfoCreateRequest request);

    @Operation(summary = "지수 정보 조회")
    @ApiResponse(responseCode = "200", description = "지수 정보 조회 성공")
    @ApiResponse(responseCode = "404", description = "조회할 지수 정보를 찾을 수 없음")
    @GetMapping("/{id}")
    ResponseEntity<IndexInfoDto> getById(@PathVariable Long id);

    @Operation(summary = "지수 정보 수정")
    @ApiResponse(responseCode = "200", description = "지수 정보 수정 성공")
    @ApiResponse(responseCode = "404", description = "수정할 지수 정보를 찾을 수 없음")
    @PatchMapping("/{id}")
    ResponseEntity<IndexInfoDto> update(
            @PathVariable Long id, @RequestBody @Valid IndexInfoUpdateRequest request);
}
