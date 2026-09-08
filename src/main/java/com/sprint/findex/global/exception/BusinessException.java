package com.sprint.findex.global.exception;

import com.sprint.findex.global.exception.errorcode.BaseErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final transient BaseErrorCode errorCode;

    public BusinessException(BaseErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(BaseErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
