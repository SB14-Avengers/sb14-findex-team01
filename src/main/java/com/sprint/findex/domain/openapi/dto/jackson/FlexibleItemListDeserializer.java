package com.sprint.findex.domain.openapi.dto.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sprint.findex.domain.openapi.dto.response.StockMarketIndexItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** 외부 API의 단건 객체와 다건 배열을 동일한 리스트로 변환한다. */
public final class FlexibleItemListDeserializer
        extends JsonDeserializer<List<StockMarketIndexItem>> {

    @Override
    public List<StockMarketIndexItem> deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonToken token = parser.currentToken();
        if (token == JsonToken.VALUE_NULL) {
            return List.of();
        }
        if (token == JsonToken.VALUE_STRING) {
            String text = parser.getText();
            if (text == null || text.isBlank()) {
                return List.of();
            }
            throw JsonMappingException.from(parser, "Item은 객체거나 배열이거나 비어있어야 한다: " + text);
        }
        ObjectCodec codec = parser.getCodec();
        if (token == JsonToken.START_ARRAY) {
            List<StockMarketIndexItem> items = new ArrayList<>();
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                items.add(codec.readValue(parser, StockMarketIndexItem.class));
            }
            return List.copyOf(items);
        }
        if (token == JsonToken.START_OBJECT) {
            return List.of(codec.readValue(parser, StockMarketIndexItem.class));
        }
        throw JsonMappingException.from(parser, "Item은 객체거나 배열이거나 비어있어야 한다:");
    }

    @Override
    public List<StockMarketIndexItem> getNullValue(DeserializationContext context) {
        return List.of();
    }
}
