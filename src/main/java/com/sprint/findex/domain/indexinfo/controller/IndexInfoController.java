package com.sprint.findex.domain.indexinfo.controller;

import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoCreateRequest;
import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoUpdateRequest;
import com.sprint.findex.domain.indexinfo.dto.response.IndexInfoDto;
import com.sprint.findex.domain.indexinfo.service.IndexInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IndexInfoController implements IndexInfoApi {
    private final IndexInfoService indexInfoService;

    @Override
    public ResponseEntity<IndexInfoDto> register(IndexInfoCreateRequest request) {
        IndexInfoDto response = indexInfoService.registerFromUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<IndexInfoDto> getById(Long id) {
        IndexInfoDto response = indexInfoService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<IndexInfoDto> update(Long id, IndexInfoUpdateRequest request) {
        IndexInfoDto response = indexInfoService.update(id, request);
        return ResponseEntity.ok(response);
    }
}
