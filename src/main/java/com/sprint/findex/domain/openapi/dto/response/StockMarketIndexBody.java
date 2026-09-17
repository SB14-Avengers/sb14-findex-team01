package com.sprint.findex.domain.openapi.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sprint.findex.domain.openapi.dto.jackson.OpenApiIntegerDeserializer;
import com.sprint.findex.domain.openapi.dto.jackson.StockMarketIndexItemsDeserializer;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockMarketIndexBody(
        @JsonProperty("numOfRows") @JsonDeserialize(using = OpenApiIntegerDeserializer.class)
                Integer numOfRows,
        @JsonProperty("pageNo") @JsonDeserialize(using = OpenApiIntegerDeserializer.class)
                Integer pageNo,
        @JsonProperty("totalCount") @JsonDeserialize(using = OpenApiIntegerDeserializer.class)
                Integer totalCount,
        @JsonProperty("items") @JsonDeserialize(using = StockMarketIndexItemsDeserializer.class)
                StockMarketIndexItems items) {

    public List<StockMarketIndexItem> itemList() {
        if (items == null) {
            return List.of();
        }
        return items.asList();
    }
}
