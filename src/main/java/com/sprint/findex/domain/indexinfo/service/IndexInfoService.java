package com.sprint.findex.domain.indexinfo.service;

import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoCreateRequest;
import com.sprint.findex.domain.indexinfo.dto.response.IndexInfoResponse;

public interface IndexInfoService {
    IndexInfoResponse register(IndexInfoCreateRequest request);
}
