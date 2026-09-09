package com.sprint.findex.domain.openapi.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockMarketIndexResponse(@JsonProperty("response") StockMarketIndexPayload response) {

    private static final String NORMAL_SERVICE_RESULT_CODE = "00";

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StockMarketIndexPayload(
            @JsonProperty("header") StockMarketIndexHeader header,
            @JsonProperty("body") StockMarketIndexBody body) {}

    public StockMarketIndexHeader header() {
        return response == null ? null : response.header();
    }

    public StockMarketIndexBody body() {
        return response == null ? null : response.body();
    }

    /** 항목이 없으면 빈 리스트를 반환한다. 응답 유효성 검증은 호출부에서 수행해야 한다. */
    public List<StockMarketIndexItem> items() {
        StockMarketIndexBody body = body();
        if (body == null) {
            return List.of();
        }
        return body.itemList();
    }

    public boolean isNormalService() {
        StockMarketIndexHeader header = header();
        return header != null && NORMAL_SERVICE_RESULT_CODE.equals(header.resultCode());
    }
}
