package com.sprint.findex.domain.syncjob.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

// api-docs의 "지수 데이터 연동" 요청(IndexDataSyncRequest)에 대응.
// "지수 정보 연동"(POST /api/sync-jobs/index-infos)은 요청 바디가 없음.
public record SyncJobCreateRequest(
        List<Long> indexInfoIds, // 비어있으면 전체 지수 대상
        @NotNull LocalDate baseDateFrom,
        @NotNull LocalDate baseDateTo) {}
