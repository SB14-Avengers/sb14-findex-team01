package com.sprint.findex.domain.openapi.dto.request;

import java.time.LocalDate;

/**
 * 모든 필드는 선택이며, indexName은 완전 일치 조건이다.
 *
 * baseDate와 기간(fromDate/toDate)은 함께 사용할 수 없다.
 * 기간은 양 끝 날짜를 포함한다. 외부 요청의 endBasDt에는 종료일 다음 날을 보내고, 응답은 원래 기간으로 다시 필터링한다.
 */
public record StockMarketIndexQuery(
        String indexName, LocalDate baseDate, LocalDate fromDate, LocalDate toDate) {}
