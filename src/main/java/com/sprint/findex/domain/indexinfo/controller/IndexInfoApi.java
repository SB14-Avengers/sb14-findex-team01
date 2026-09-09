package com.sprint.findex.domain.indexinfo.controller;

import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoCreateRequest;
import com.sprint.findex.domain.indexinfo.dto.response.IndexInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "지수 정보 관리", description = "지수 정보 API")
@RequestMapping("/api/index-infos")
public interface IndexInfoApi {
    @Operation(summary = "지수 정보 등록")
    @PostMapping
    ResponseEntity<IndexInfoResponse> register(@RequestBody @Valid IndexInfoCreateRequest request);
}
