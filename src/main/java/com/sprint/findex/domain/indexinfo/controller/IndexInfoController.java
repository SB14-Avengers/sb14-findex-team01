package com.sprint.findex.domain.indexinfo.controller;

import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoCreateRequest;
import com.sprint.findex.domain.indexinfo.dto.response.IndexInfoResponse;
import com.sprint.findex.domain.indexinfo.service.IndexInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class IndexInfoController implements IndexInfoApi {
    private final IndexInfoService indexInfoService;

    @Override
    public ResponseEntity<IndexInfoResponse> register(IndexInfoCreateRequest request) {
        IndexInfoResponse response = indexInfoService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
