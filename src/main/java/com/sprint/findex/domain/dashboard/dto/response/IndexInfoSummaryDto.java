package com.sprint.findex.domain.dashboard.dto.response;

import com.sprint.findex.domain.indexinfo.entity.IndexInfo;

// 실제 엔드포인트(GET /api/index-infos/summaries)는 api-docs상 "지수 정보 API" 태그 소속이지만,
// 코드잇 요구사항 명세서 기준 "대시보드 관리" 기능이라 dashboard 패키지에 둔다 (기능 단위 패키징).
public record IndexInfoSummaryDto(Long id, String indexClassification, String indexName) {

    public static IndexInfoSummaryDto from(IndexInfo indexInfo) {

        return new IndexInfoSummaryDto(
                indexInfo.getId(), indexInfo.getIndexClassification(), indexInfo.getIndexName());
    }
}
