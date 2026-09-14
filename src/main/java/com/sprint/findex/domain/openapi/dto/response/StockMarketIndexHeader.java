package com.sprint.findex.domain.openapi.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockMarketIndexHeader(
        @JsonProperty("resultCode") String resultCode,
        @JsonProperty("resultMsg") String resultMsg) {}
