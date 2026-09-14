package com.sprint.findex.domain.openapi.client;

import com.sprint.findex.domain.openapi.dto.request.StockMarketIndexQuery;
import com.sprint.findex.domain.openapi.dto.response.StockMarketIndexItem;
import java.util.List;

public interface OpenApiClient {

    /**
     * 조회 조건에 맞는 주가지수시세를 모든 외부 페이지에서 수집한다.
     *
     * <p>조회 조건 - query 자체는 필수이며, 각 필드는 선택이다. - indexName은 앞뒤 공백을 제거한 지수명과 완전 일치한다. null이나 공백이면 이름
     * 조건을 적용하지 않는다. - baseDate는 하루 조회용이며, fromDate 또는 toDate와 함께 사용할 수 없다. - fromDate/toDate는 양 끝
     * 날짜를 포함한다. 한쪽만 지정하면 해당 경계만 적용한다. - 시작일이 종료일보다 늦거나 종료일 다음 날을 계산할 수 없으면 INVALID_REQUEST다. - 날짜를
     * 모두 생략하면 제공자의 기본 조회 범위를 따른다. 최신 하루만 반환된다고 가정하면 안 된다.
     *
     * <p>반환 결과 - 전체 페이지를 검증한 뒤 원래 날짜 범위와 지수명으로 다시 필터링한 불변 리스트를 반환한다. - 정상 0건 또는 필터링 후 0건은 빈 리스트다.
     * null이나 일부 페이지만 수집한 결과는 반환하지 않는다. - 각 항목의 idxCsf, idxNm은 공백이 아니며, basDt는 null이 아니다. 나머지 필드는
     * null일 수 있다. - 누락된 값을 0으로 보정하지 않는다. 저장에 필요한 값의 검증과 누락 행 처리 정책은 호출부에서 정한다. - 지수 정보에 연결할 때는
     * idxCsf와 idxNm을 함께 비교한다. 같은 지수의 여러 날짜 중 대표 행 선택은 호출부 책임이다. - basDt는 시세 기준일이고 basPntm은 지수 산출 기준
     * 시점이므로 저장 시 구분한다.
     *
     * <p>실패 및 실행 방식 - 잘못된 조회 조건은 INVALID_REQUEST, 서비스 키 누락 등 설정 문제는 CONFIG_UNAVAILABLE이다. - 외부 HTTP
     * 상태코드 오류, 네트워크 오류, 타임아웃은 각각 HTTP_ERROR, NETWORK_ERROR, TIMEOUT으로 구분한다. - 외부 resultCode가 00이
     * 아니면 EXTERNAL_HEADER, 응답 구조나 필수 식별자가 잘못되면 MALFORMED_RESPONSE다. - 페이지 진행·건수가 모순되거나 같은 (idxCsf,
     * idxNm, basDt)가 반복되면 PAGINATION_INCONSISTENT다. - 한 페이지라도 실패하면 OpenApiClientException을 던진다. 잘못된
     * 응답을 정상 0건으로 처리하지 않는다. - 동기 호출이며 자동 재시도는 하지 않는다. 타임아웃은 페이지별로 적용되고 전체 페이지를 합친 시간 제한은 없다.
     *
     * @param query 지수명과 시세 기준일 또는 기간 조건
     * @return 조회 조건에 맞는 시세 행의 불변 리스트
     * @throws com.sprint.findex.domain.openapi.exception.OpenApiClientException 조회 조건·설정·전송·응답 검증
     *     실패
     */
    List<StockMarketIndexItem> fetchStockMarketIndex(StockMarketIndexQuery query);
}
