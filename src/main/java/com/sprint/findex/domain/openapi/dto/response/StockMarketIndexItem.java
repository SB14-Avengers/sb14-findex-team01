package com.sprint.findex.domain.openapi.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sprint.findex.domain.openapi.dto.jackson.OpenApiBigDecimalDeserializer;
import com.sprint.findex.domain.openapi.dto.jackson.OpenApiIntegerDeserializer;
import com.sprint.findex.domain.openapi.dto.jackson.OpenApiLongDeserializer;
import com.sprint.findex.domain.openapi.dto.jackson.OpenApiYyyyMmDdDeserializer;
import java.math.BigDecimal;
import java.time.LocalDate;

/** basDt는 시세 기준일, basPntm은 지수 산출 기준 시점이므로 매핑 시 구분한다. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record StockMarketIndexItem(
        @JsonProperty("basDt") @JsonDeserialize(using = OpenApiYyyyMmDdDeserializer.class)
                LocalDate basDt,
        @JsonProperty("idxCsf") String idxCsf,
        @JsonProperty("idxNm") String idxNm,
        @JsonProperty("epyItmsCnt") @JsonDeserialize(using = OpenApiIntegerDeserializer.class)
                Integer epyItmsCnt,
        @JsonProperty("clpr") @JsonDeserialize(using = OpenApiBigDecimalDeserializer.class)
                BigDecimal clpr,
        @JsonProperty("vs") @JsonDeserialize(using = OpenApiBigDecimalDeserializer.class)
                BigDecimal vs,
        @JsonProperty("fltRt") @JsonDeserialize(using = OpenApiBigDecimalDeserializer.class)
                BigDecimal fltRt,
        @JsonProperty("mkp") @JsonDeserialize(using = OpenApiBigDecimalDeserializer.class)
                BigDecimal mkp,
        @JsonProperty("hipr") @JsonDeserialize(using = OpenApiBigDecimalDeserializer.class)
                BigDecimal hipr,
        @JsonProperty("lopr") @JsonDeserialize(using = OpenApiBigDecimalDeserializer.class)
                BigDecimal lopr,
        @JsonProperty("trqu") @JsonDeserialize(using = OpenApiLongDeserializer.class) Long trqu,
        @JsonProperty("trPrc") @JsonDeserialize(using = OpenApiLongDeserializer.class) Long trPrc,
        @JsonProperty("lstgMrktTotAmt") @JsonDeserialize(using = OpenApiLongDeserializer.class)
                Long lstgMrktTotAmt,
        @JsonProperty("basPntm") @JsonDeserialize(using = OpenApiYyyyMmDdDeserializer.class)
                LocalDate basPntm,
        @JsonProperty("basIdx") @JsonDeserialize(using = OpenApiBigDecimalDeserializer.class)
                BigDecimal basIdx) {}
