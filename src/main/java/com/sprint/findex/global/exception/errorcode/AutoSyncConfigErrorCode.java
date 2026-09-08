package com.sprint.findex.global.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AutoSyncConfigErrorCode implements BaseErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "AUTO_SYNC_CONFIG_NOT_FOUND", "자동 연동 설정을 찾을 수 없습니다."),
    INVALID_SORT_FIELD(
            HttpStatus.BAD_REQUEST, "AUTO_SYNC_CONFIG_INVALID_SORT_FIELD", "지원하지 않는 정렬 필드입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
