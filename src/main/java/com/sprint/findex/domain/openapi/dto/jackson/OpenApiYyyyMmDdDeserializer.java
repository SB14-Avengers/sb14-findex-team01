package com.sprint.findex.domain.openapi.dto.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

public final class OpenApiYyyyMmDdDeserializer extends JsonDeserializer<LocalDate> {

    private static final Pattern EIGHT_DIGITS = Pattern.compile("\\d{8}");
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("uuuuMMdd").withResolverStyle(ResolverStyle.STRICT);

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonToken token = parser.currentToken();
        if (token == JsonToken.VALUE_NULL) {
            return null;
        }
        if (token != JsonToken.VALUE_STRING && token != JsonToken.VALUE_NUMBER_INT) {
            return (LocalDate) context.handleUnexpectedToken(LocalDate.class, parser);
        }
        String text = parser.getText();
        if (text == null || text.isBlank()) {
            return null;
        }
        String value = text.trim();
        if (!EIGHT_DIGITS.matcher(value).matches()) {
            throw JsonMappingException.from(parser, "date value is not yyyyMMdd: " + value);
        }
        try {
            return LocalDate.parse(value, FORMATTER);
        } catch (DateTimeParseException ex) {
            throw JsonMappingException.from(parser, "date value is not yyyyMMdd: " + value, ex);
        }
    }
}
