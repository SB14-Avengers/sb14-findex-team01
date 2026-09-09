package com.sprint.findex.domain.openapi.dto.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.math.BigDecimal;

/** 숫자 정밀도를 보존하기 위해 Double 변환 없이 숫자와 숫자 문자열을 읽는다. */
final class OpenApiNumberParser {

    private OpenApiNumberParser() {}

    static BigDecimal readBigDecimal(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonToken token = parser.currentToken();
        if (token == JsonToken.VALUE_NULL) {
            return null;
        }
        if (token == JsonToken.VALUE_NUMBER_INT
                || token == JsonToken.VALUE_NUMBER_FLOAT
                || token == JsonToken.VALUE_STRING) {
            String text = parser.getText();
            if (text == null || text.isBlank()) {
                return null;
            }
            try {
                return new BigDecimal(text.trim());
            } catch (NumberFormatException ex) {
                throw JsonMappingException.from(
                        parser, "numeric value is not a valid decimal: " + text, ex);
            }
        }
        return (BigDecimal) context.handleUnexpectedToken(BigDecimal.class, parser);
    }

    static Long toLong(JsonParser parser, BigDecimal value) throws JsonMappingException {
        if (value == null) {
            return null;
        }
        try {
            return value.stripTrailingZeros().longValueExact();
        } catch (ArithmeticException ex) {
            throw JsonMappingException.from(
                    parser, "numeric value is not an exact long: " + value, ex);
        }
    }

    static Integer toInteger(JsonParser parser, BigDecimal value) throws JsonMappingException {
        if (value == null) {
            return null;
        }
        try {
            return value.stripTrailingZeros().intValueExact();
        } catch (ArithmeticException ex) {
            throw JsonMappingException.from(
                    parser, "numeric value is not an exact int: " + value, ex);
        }
    }
}
