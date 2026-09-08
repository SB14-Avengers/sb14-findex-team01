package com.sprint.findex.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sprint.findex.global.exception.errorcode.BaseErrorCode;
import jakarta.validation.ConstraintViolation;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final Instant timestamp;
    private final int status;
    private final String message;
    private final String details;

    private ErrorResponse(int status, String message, String details) {
        this.timestamp = Instant.now();
        this.status = status;
        this.message = message;
        this.details = details;
    }

    public static ErrorResponse of(BaseErrorCode errorCode, String details) {
        return new ErrorResponse(
                errorCode.getHttpStatus().value(), errorCode.getMessage(), details);
    }

    public static ErrorResponse of(HttpStatus status, String message, String details) {
        return new ErrorResponse(status.value(), message, details);
    }

    public static ErrorResponse of(BindingResult bindingResult) {
        String details =
                bindingResult.getFieldErrors().stream()
                        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                        .collect(Collectors.joining(", "));
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "입력값 검증에 실패했습니다.", details);
    }

    /** 파라미터/경로변수 제약 위반({@code @Validated} + {@code @RequestParam} 등) */
    public static ErrorResponse of(Set<ConstraintViolation<?>> constraintViolations) {
        String details =
                constraintViolations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining(", "));
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "제약 조건을 위반했습니다.", details);
    }
}
