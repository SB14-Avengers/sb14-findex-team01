package com.sprint.findex.global.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum IndexDataErrorCode implements BaseErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "INDEX_DATA_NOT_FOUND", "지수 데이터를 찾을 수 없습니다."),
    DUPLICATE(HttpStatus.CONFLICT, "INDEX_DATA_DUPLICATE", "이미 등록된 (지수, 날짜) 조합입니다."),
    INDEX_INFO_NOT_FOUND(
            HttpStatus.BAD_REQUEST, "INDEX_DATA_INDEX_INFO_NOT_FOUND", "존재하지 않는 지수 정보입니다."),
    INVALID_SORT_FIELD(
            HttpStatus.BAD_REQUEST, "INDEX_DATA_INVALID_SORT_FIELD", "지원하지 않는 정렬 필드입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
