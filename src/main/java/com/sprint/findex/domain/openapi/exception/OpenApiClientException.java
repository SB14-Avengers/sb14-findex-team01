package com.sprint.findex.domain.openapi.exception;

import lombok.Getter;

/**
 * syncjob에서 잡아 실패 이력을 기록하는 예외.
 *
 * 서비스 키가 포함된 URL이 노출될 수 있으므로 원인 예외를 연결하거나 메시지에 요청 URL을 넣지 않는다.
 */
@Getter
public class OpenApiClientException extends RuntimeException {

    private final OpenApiErrorKind kind;

    public OpenApiClientException(OpenApiErrorKind kind, String message) {
        super(message);
        this.kind = kind;
    }
}
