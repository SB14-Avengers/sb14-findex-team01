package com.sprint.findex.domain.openapi.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/*
 {
   "response": {
     "header": { "resultCode": "00", "resultMsg": "NORMAL SERVICE." },
     "body": {
       "numOfRows": 1,
       "pageNo": 1,
       "totalCount": 254685,
       "items": {
         "item": [
           {
             "basDt": "20260916",
             "idxNm": "IT 서비스",
             "idxCsf": "KOSDAQ시리즈",
             "epyItmsCnt": "228",
             "clpr": "653.18",
             "vs": "-11.55",
             "fltRt": "-1.74",
             "mkp": "659.23",
             "hipr": "659.23",
             "lopr": "650.93",
             "trqu": "103142063",
             "trPrc": "713340714217",
             "lstgMrktTotAmt": "30086184859066",
             "lsYrEdVsFltRg": "-234",
             "lsYrEdVsFltRt": "-26.38",
             "yrWRcrdHgst": "1045.53",
             "yrWRcrdHgstDt": "20260129",
             "yrWRcrdLwst": "0",
             "yrWRcrdLwstDt": "20260917",
             "basPntm": "20240701",
             "basIdx": "1000"
           }
         ]
       }
     }
   }
 }
*/

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
