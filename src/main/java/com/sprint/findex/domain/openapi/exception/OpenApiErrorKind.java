package com.sprint.findex.domain.openapi.exception;

/** HTTP 상태와 연동 이력으로의 변환은 syncjob에서 처리한다. */
public enum OpenApiErrorKind {
    /** 서비스 키 누락 등 호출에 필요한 설정이 없는 경우. */
    CONFIG_UNAVAILABLE,

    INVALID_REQUEST,
    /** HTTP 4xx/5xx. */
    HTTP_ERROR,

    NETWORK_ERROR,

    TIMEOUT,
    /** HTTP 응답은 정상이지만 header.resultCode가 00이 아닌 경우. */
    EXTERNAL_HEADER,
    /** JSON 구조나 필수 필드가 잘못된 응답. 정상 0건과 구분하며, 외부 오류 코드는 EXTERNAL_HEADER로 분류한다. */
    MALFORMED_RESPONSE,
    /** 페이지가 진행되지 않거나 totalCount와 수집 건수가 불일치하는 경우. */
    PAGINATION_INCONSISTENT
}
