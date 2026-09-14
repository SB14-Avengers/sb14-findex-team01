package com.sprint.findex.domain.openapi.dto.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sprint.findex.domain.openapi.dto.response.StockMarketIndexItems;
import java.io.IOException;
import java.util.List;

/** body.items의 빈 문자열과 null은 빈 목록으로 처리한다. 숫자 정밀도를 보존하기 위해 JsonNode를 거치지 않고 역직렬화한다. */
public final class StockMarketIndexItemsDeserializer
        extends JsonDeserializer<StockMarketIndexItems> {

    @Override
    public StockMarketIndexItems deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonToken token = parser.currentToken();
        if (token == JsonToken.VALUE_NULL) {
            return empty();
        }
        if (token == JsonToken.VALUE_STRING) {
            String text = parser.getText();
            if (text == null || text.isBlank()) {
                return empty();
            }
            throw JsonMappingException.from(parser, "Item은 객체이거나 비어있어야 한다. " + text);
        }
        if (token != JsonToken.START_OBJECT) {
            throw JsonMappingException.from(parser, "Item은 객체이거나 비어있어야 한다.");
        }

        return parser.getCodec().readValue(parser, StockMarketIndexItems.class);
    }

    @Override
    public StockMarketIndexItems getNullValue(DeserializationContext context) {
        return empty();
    }

    private static StockMarketIndexItems empty() {
        return new StockMarketIndexItems(List.of());
    }
}
