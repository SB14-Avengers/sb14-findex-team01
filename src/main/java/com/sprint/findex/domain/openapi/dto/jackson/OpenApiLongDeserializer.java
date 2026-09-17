package com.sprint.findex.domain.openapi.dto.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public final class OpenApiLongDeserializer extends JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return OpenApiNumberParser.toLong(
                parser, OpenApiNumberParser.readBigDecimal(parser, context));
    }
}
