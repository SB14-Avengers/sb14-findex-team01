package com.sprint.findex.global.common;

import java.util.List;

public record CursorPageResponse<T>(
        List<T> content, // 실제 데이터 목록
        String nextCursor, // 다음 페이지 조회용 커서 값 (예: 마지막 항목의 정렬 기준값)
        Long nextIdAfter, // 커서 값이 같을 때를 대비한 보조 키(마지막 항목 ID)
        int size, // 페이지 크기
        long totalElements, // 전체 개수
        boolean hasNext) {} // 다음 페이지 존재 여부
