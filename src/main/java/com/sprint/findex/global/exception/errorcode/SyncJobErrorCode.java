package com.sprint.findex.global.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 연동 작업(syncjob) + Open API 연동 도메인 전용 에러 코드. 이 도메인 담당자만 이 파일을 건드리면 된다. OPEN_API_CALL_FAILED는 {@code
 * external.openapi.MarketIndexOpenApiClient}에서 던진다 — Open API 호출은 결국 연동 작업(SyncJob) 이력으로 귀결되는 동작이라
 * 이 도메인에 묶음.
 */
@Getter
@RequiredArgsConstructor
public enum SyncJobErrorCode implements BaseErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "SYNC_JOB_NOT_FOUND", "연동 작업 이력을 찾을 수 없습니다."),
    INVALID_SORT_FIELD(HttpStatus.BAD_REQUEST, "SYNC_JOB_INVALID_SORT_FIELD", "지원하지 않는 정렬 필드입니다."),
    OPEN_API_CALL_FAILED(
            HttpStatus.BAD_GATEWAY,
            "SYNC_JOB_OPEN_API_CALL_FAILED",
            "공공데이터포털 Open API 호출에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
