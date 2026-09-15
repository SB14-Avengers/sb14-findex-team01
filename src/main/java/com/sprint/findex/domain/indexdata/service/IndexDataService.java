package com.sprint.findex.domain.indexdata.service;

import com.sprint.findex.domain.indexdata.dto.request.IndexDataCreateRequest;
import com.sprint.findex.domain.indexdata.dto.request.IndexDataExportRequest;
import com.sprint.findex.domain.indexdata.dto.request.IndexDataSearchRequest;
import com.sprint.findex.domain.indexdata.dto.request.IndexDataUpdateRequest;
import com.sprint.findex.domain.indexdata.dto.response.IndexDataDto;
import com.sprint.findex.global.common.CursorPageResponse;
import java.io.IOException;
import java.io.OutputStream;

public interface IndexDataService {
    IndexDataDto create(IndexDataCreateRequest createRequest);

    IndexDataDto getById(Long indexDataId);

    IndexDataDto update(Long indexDataId, IndexDataUpdateRequest updateRequest);

    void deleteById(Long indexDataId);

    CursorPageResponse<IndexDataDto> find(IndexDataSearchRequest request);

    void exportCsv(IndexDataExportRequest request, OutputStream outputStream) throws IOException;
}
