package com.sprint.findex.domain.openapi.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sprint.findex.domain.openapi.dto.jackson.FlexibleItemListDeserializer;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockMarketIndexItems(
        @JsonProperty("item") @JsonDeserialize(using = FlexibleItemListDeserializer.class)
                List<StockMarketIndexItem> item) {

    public List<StockMarketIndexItem> asList() {
        return item == null ? List.of() : item;
    }
}
