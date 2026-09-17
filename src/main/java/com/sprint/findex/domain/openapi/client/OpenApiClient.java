package com.sprint.findex.domain.openapi.client;

import com.sprint.findex.domain.openapi.dto.request.StockMarketIndexQuery;
import com.sprint.findex.domain.openapi.dto.response.StockMarketIndexItem;
import java.util.List;

public interface OpenApiClient {

    List<StockMarketIndexItem> fetchStockMarketIndex(StockMarketIndexQuery query);
}
