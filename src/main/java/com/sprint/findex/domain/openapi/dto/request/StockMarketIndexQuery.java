package com.sprint.findex.domain.openapi.dto.request;

import java.time.LocalDate;

/**
 * 모든 필드는 선택이며, indexName은 완전 일치 조건이다.
 *
 * <p>baseDate와 기간(fromDate/toDate)은 함께 사용할 수 없다. 기간은 양 끝 날짜를 포함한다. 외부 API의 endBasDt는 종료일을 포함하지 않으므로
 * 종료일 다음 날을 보낸다.
 */
public record StockMarketIndexQuery(
        String indexName, LocalDate baseDate, LocalDate fromDate, LocalDate toDate) {}
