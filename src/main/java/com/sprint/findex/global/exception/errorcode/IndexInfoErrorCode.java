package com.sprint.findex.global.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum IndexInfoErrorCode implements BaseErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "INDEX_INFO_NOT_FOUND", "지수 정보를 찾을 수 없습니다."),
    DUPLICATE(HttpStatus.CONFLICT, "INDEX_INFO_DUPLICATE", "이미 등록된 (지수 분류명, 지수명) 조합입니다."),
    INVALID_SORT_FIELD(
            HttpStatus.BAD_REQUEST, "INDEX_INFO_INVALID_SORT_FIELD", "지원하지 않는 정렬 필드입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
