package com.sprint.findex.domain.indexinfo.service;

import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoCreateRequest;
import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoOpenApiRegisterRequest;
import com.sprint.findex.domain.indexinfo.dto.response.IndexInfoDto;

public interface IndexInfoService {
    IndexInfoDto registerFromUser(IndexInfoCreateRequest request);

    IndexInfoDto registerFromOpenApi(IndexInfoOpenApiRegisterRequest request);

    IndexInfoDto getById(Long id);
}
