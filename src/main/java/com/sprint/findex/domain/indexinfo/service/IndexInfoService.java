package com.sprint.findex.domain.indexinfo.service;

import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoCreateRequest;
import com.sprint.findex.domain.indexinfo.dto.response.IndexInfoDto;

public interface IndexInfoService {
    IndexInfoDto register(IndexInfoCreateRequest request);
}
