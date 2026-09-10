package com.sprint.findex.domain.indexdata.service;

import com.sprint.findex.domain.indexdata.dto.request.IndexDataCreateRequest;
import com.sprint.findex.domain.indexdata.dto.response.IndexDataDto;

public interface IndexDataService {
    IndexDataDto create(IndexDataCreateRequest createRequest);

    IndexDataDto getById(Long indexDataId);
}
