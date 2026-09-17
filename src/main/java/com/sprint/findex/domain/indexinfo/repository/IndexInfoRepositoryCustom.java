package com.sprint.findex.domain.indexinfo.repository;

import com.sprint.findex.domain.indexinfo.dto.request.IndexInfoSearchRequest;
import com.sprint.findex.domain.indexinfo.entity.IndexInfo;
import java.util.List;

public interface IndexInfoRepositoryCustom {
    List<IndexInfo> search(IndexInfoSearchRequest request);

    long count(IndexInfoSearchRequest request);
}
