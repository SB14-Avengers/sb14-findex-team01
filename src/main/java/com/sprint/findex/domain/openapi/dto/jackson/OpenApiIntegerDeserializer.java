package com.sprint.findex.domain.openapi.dto.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public final class OpenApiIntegerDeserializer extends JsonDeserializer<Integer> {

    @Override
    public Integer deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        return OpenApiNumberParser.toInteger(
                parser, OpenApiNumberParser.readBigDecimal(parser, context));
    }
}
