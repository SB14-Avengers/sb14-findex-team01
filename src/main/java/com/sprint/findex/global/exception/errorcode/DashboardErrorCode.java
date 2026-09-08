package com.sprint.findex.global.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DashboardErrorCode implements BaseErrorCode {
    INDEX_INFO_NOT_FOUND(
            HttpStatus.NOT_FOUND, "DASHBOARD_INDEX_INFO_NOT_FOUND", "차트/성과를 조회할 지수 정보를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
