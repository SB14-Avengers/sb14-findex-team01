package com.sprint.findex.domain.indexdata.repository;

import com.sprint.findex.domain.indexdata.dto.request.IndexDataExportRequest;
import com.sprint.findex.domain.indexdata.dto.request.IndexDataSearchRequest;
import com.sprint.findex.domain.indexdata.entity.IndexData;
import java.util.List;

public interface IndexDataRepositoryCustom {
    List<IndexData> search(IndexDataSearchRequest request, int limit);

    Long countBy(IndexDataSearchRequest request);

    List<IndexData> findForExport(
            IndexDataExportRequest request, String cursor, Long idAfter, int limit);
}
