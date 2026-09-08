package com.sprint.findex.global.common;

public record ApiResponse<T>(int status, T data) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, data);
    }
}
