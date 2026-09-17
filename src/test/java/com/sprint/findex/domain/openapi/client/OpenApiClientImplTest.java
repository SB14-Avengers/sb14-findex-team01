package com.sprint.findex.domain.openapi.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sprint.findex.domain.openapi.client.impl.OpenApiClientImpl;
import com.sprint.findex.domain.openapi.config.OpenApiProperties;
import com.sprint.findex.domain.openapi.config.OpenApiRestClientConfig;
import com.sprint.findex.domain.openapi.dto.request.StockMarketIndexQuery;
import com.sprint.findex.domain.openapi.dto.response.StockMarketIndexItem;
import com.sprint.findex.domain.openapi.exception.OpenApiClientException;
import com.sprint.findex.domain.openapi.exception.OpenApiErrorKind;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

class OpenApiClientImplTest {

    private static final String SECRET_KEY = "SECRETKEY+/=lea%k";
    private static final ObjectMapper OBJECT_MAPPER =
            JsonMapper.builder().addModule(new JavaTimeModule()).build();

    private HttpServer server;
    private java.util.concurrent.ExecutorService executor;

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Test
    void encodesQueryValuesExactlyOnceIncludingKeyAndKoreanName() {
        AtomicReference<String> rawQuery = new AtomicReference<>();
        startServer(
                exchange -> {
                    rawQuery.set(exchange.getRequestURI().getRawQuery());
                    writeJson(exchange, envelope(1, 1, 1, itemJson("20260102", "KOSPI", "코스피")));
                });

        OpenApiProperties properties = properties(SECRET_KEY, 10, Duration.ofSeconds(2));
        client(properties)
                .fetchStockMarketIndex(
                        new StockMarketIndexQuery(
                                "코스피", null, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 2)));

        String query = rawQuery.get();
        String encodedKey = queryValue(query, "serviceKey");
        String encodedName = queryValue(query, "idxNm");

        assertThat(URLDecoder.decode(encodedKey, StandardCharsets.UTF_8)).isEqualTo(SECRET_KEY);
        assertThat(encodedKey).doesNotContain("+");
        assertThat(encodedKey).contains("%2B");
        assertThat(encodedKey).contains("%2F");
        assertThat(encodedKey).contains("%3D");
        assertThat(encodedKey).contains("%25");
        assertThat(encodedKey).isNotEqualTo(UriUtils.encode(encodedKey, StandardCharsets.UTF_8));
        assertThat(URLDecoder.decode(encodedName, StandardCharsets.UTF_8)).isEqualTo("코스피");
        assertThat(query).doesNotContain("idxNm=코스피");
        assertThat(query).contains("resultType=json");
        assertThat(query).contains("beginBasDt=20260101");
        assertThat(query).contains("endBasDt=20260103");
        assertThat(query).contains("numOfRows=10");
        assertThat(query).contains("pageNo=1");
    }

    @Test
    void fetchesAllPagesThenReturnsImmutableList() {
        startServer(
                exchange -> {
                    int pageNo = pageNo(exchange);
                    if (pageNo == 1) {
                        writeJson(
                                exchange,
                                envelope(
                                        2,
                                        1,
                                        3,
                                        itemJson("20260101", "KOSPI", "코스피"),
                                        itemJson("20260102", "KOSPI", "코스피")));
                        return;
                    }
                    if (pageNo == 2) {
                        writeJson(
                                exchange, envelope(2, 2, 3, itemJson("20260103", "KOSPI", "코스피")));
                        return;
                    }
                    writePlain(exchange, 500, "unexpected page");
                });

        List<StockMarketIndexItem> result =
                client(properties(SECRET_KEY, 2, Duration.ofSeconds(2)))
                        .fetchStockMarketIndex(new StockMarketIndexQuery("코스피", null, null, null));

        assertThat(result).hasSize(3);
        assertThat(result.get(0).basDt()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(result.get(1).basDt()).isEqualTo(LocalDate.of(2026, 1, 2));
        assertThat(result.get(2).basDt()).isEqualTo(LocalDate.of(2026, 1, 3));
        assertThat(result.get(0).idxCsf()).isEqualTo("KOSPI");
        assertThatThrownBy(() -> result.add(result.get(0)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void returnsEmptyListWhenTotalCountIsZero() {
        startServer(exchange -> writeJson(exchange, envelope(10, 1, 0, "")));

        List<StockMarketIndexItem> result =
                client(properties(SECRET_KEY, 10, Duration.ofSeconds(2)))
                        .fetchStockMarketIndex(new StockMarketIndexQuery(null, null, null, null));

        assertThat(result).isEmpty();
        assertThatThrownBy(() -> result.add(null))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void returnsSingleItemFromBareObject() {
        startServer(
                exchange ->
                        writeJson(
                                exchange,
                                envelope(10, 1, 1, itemJson("20260102", "KOSPI", "코스피"))));

        List<StockMarketIndexItem> result =
                client(properties(SECRET_KEY, 10, Duration.ofSeconds(2)))
                        .fetchStockMarketIndex(
                                new StockMarketIndexQuery(
                                        "코스피", LocalDate.of(2026, 1, 2), null, null));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).idxNm()).isEqualTo("코스피");
        assertThat(result.get(0).idxCsf()).isEqualTo("KOSPI");
        assertThat(result.get(0).basDt()).isEqualTo(LocalDate.of(2026, 1, 2));
    }

    @Test
    void postFiltersInclusiveDateRangeAfterCollectingAllRows() {
        startServer(
                exchange ->
                        writeJson(
                                exchange,
                                envelope(
                                        10,
                                        1,
                                        3,
                                        itemJson("20260101", "KOSPI", "코스피"),
                                        itemJson("20260102", "KOSPI", "코스피"),
                                        itemJson("20260103", "KOSPI", "코스피"))));

        List<StockMarketIndexItem> result =
                client(properties(SECRET_KEY, 10, Duration.ofSeconds(2)))
                        .fetchStockMarketIndex(
                                new StockMarketIndexQuery(
                                        "코스피",
                                        null,
                                        LocalDate.of(2026, 1, 1),
                                        LocalDate.of(2026, 1, 2)));

        assertThat(result)
                .extracting(StockMarketIndexItem::basDt)
                .containsExactly(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 2));
    }

    @Test
    void postFiltersExactIndexName() {
        startServer(
                exchange ->
                        writeJson(
                                exchange,
                                envelope(
                                        10,
                                        1,
                                        2,
                                        itemJson("20260102", "KOSPI", "코스피"),
                                        itemJson("20260102", "KOSPI", "코스피 200"))));

        List<StockMarketIndexItem> result =
                client(properties(SECRET_KEY, 10, Duration.ofSeconds(2)))
                        .fetchStockMarketIndex(new StockMarketIndexQuery("코스피", null, null, null));

        assertThat(result).extracting(StockMarketIndexItem::idxNm).containsExactly("코스피");
    }

    @Test
    void http4xxIsHttpErrorWithoutSecret() {
        startServer(exchange -> writePlain(exchange, 401, "unauthorized " + SECRET_KEY));

        assertSecretFreeFailure(OpenApiErrorKind.HTTP_ERROR, "Open API HTTP 401");
    }

    @Test
    void http5xxIsHttpErrorWithoutSecret() {
        startServer(exchange -> writePlain(exchange, 502, "<html>error " + SECRET_KEY + "</html>"));

        assertSecretFreeFailure(OpenApiErrorKind.HTTP_ERROR, "Open API HTTP 502");
    }

    @Test
    void externalHeaderErrorOnHttp200() {
        startServer(
                exchange ->
                        writeJson(
                                exchange,
                                """
                                {"response":{"header":{"resultCode":"30","resultMsg":"SERVICE KEY IS NOT REGISTERED ERROR"},"body":{"numOfRows":0,"pageNo":1,"totalCount":0,"items":""}}}
                                """));

        OpenApiClientException ex =
                catchFailure(
                        client(properties(SECRET_KEY, 10, Duration.ofSeconds(2))),
                        new StockMarketIndexQuery(null, null, null, null));
        assertThat(ex.getKind()).isEqualTo(OpenApiErrorKind.EXTERNAL_HEADER);
        assertThat(ex.getMessage()).isEqualTo("Open API header error: 30");
        assertNoSecret(ex);
    }

    @Test
    void non00HeaderWithoutBodyIsExternalHeader() {
        startServer(
                exchange ->
                        writeJson(
                                exchange,
                                """
                                {"response":{"header":{"resultCode":"22","resultMsg":"ERROR"}}}
                                """));

        OpenApiClientException ex =
                catchFailure(
                        client(properties(SECRET_KEY, 10, Duration.ofSeconds(2))),
                        new StockMarketIndexQuery(null, null, null, null));
        assertThat(ex.getKind()).isEqualTo(OpenApiErrorKind.EXTERNAL_HEADER);
        assertThat(ex.getMessage()).isEqualTo("Open API header error: 22");
    }

    @Test
    void blankResultCodeIsMalformed() {
        startServer(
                exchange ->
                        writeJson(
                                exchange,
                                """
                                {"response":{"header":{"resultCode":"","resultMsg":"NORMAL SERVICE."},"body":{"numOfRows":0,"pageNo":1,"totalCount":0,"items":""}}}
                                """));

        assertSecretFreeFailure(
                OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답 header resultCode가 없습니다.");
    }

    @Test
    void unsafeResultCodeIsNotEchoed() {
        startServer(
                exchange ->
                        writeJson(
                                exchange,
                                """
                                {"response":{"header":{"resultCode":"SECRETKEY-not-a-code","resultMsg":"x"}}}
                                """));

        OpenApiClientException ex =
                catchFailure(
                        client(properties(SECRET_KEY, 10, Duration.ofSeconds(2))),
                        new StockMarketIndexQuery(null, null, null, null));
        assertThat(ex.getKind()).isEqualTo(OpenApiErrorKind.EXTERNAL_HEADER);
        assertThat(ex.getMessage()).isEqualTo("Open API header error");
        assertNoSecret(ex);
    }

    @Test
    void malformedJsonIsMalformedResponse() {
        startServer(exchange -> writePlain(exchange, 200, "{not-json " + SECRET_KEY));

        assertSecretFreeFailure(
                OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답 JSON을 해석할 수 없습니다.");
    }

    @Test
    void xmlBodyIsMalformedResponse() {
        startServer(
                exchange ->
                        writePlain(
                                exchange,
                                200,
                                "<?xml version=\"1.0\"?><response><header><resultCode>00</resultCode></header></response>"));

        assertSecretFreeFailure(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답이 JSON이 아닙니다.");
    }

    @Test
    void missingHeaderIsMalformedNotZeroData() {
        startServer(
                exchange ->
                        writeJson(
                                exchange,
                                """
                                {"response":{"body":{"numOfRows":10,"pageNo":1,"totalCount":0,"items":""}}}
                                """));

        assertSecretFreeFailure(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답 header가 없습니다.");
    }

    @Test
    void missingBodyIsMalformedNotZeroData() {
        startServer(
                exchange ->
                        writeJson(
                                exchange,
                                """
                                {"response":{"header":{"resultCode":"00","resultMsg":"NORMAL SERVICE."}}}
                                """));

        assertSecretFreeFailure(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 응답 body가 없습니다.");
    }

    @Test
    void blankBaseDateIsMalformedNotZero() {
        startServer(
                exchange -> writeJson(exchange, envelope(10, 1, 1, itemJson("", "KOSPI", "코스피"))));

        assertSecretFreeFailure(OpenApiErrorKind.MALFORMED_RESPONSE, "Open API 항목에 기준일자가 없습니다.");
    }

    @Test
    void timeoutDoesNotLeakSecretOrUrl() {
        startServer(
                exchange -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                    writeJson(exchange, envelope(10, 1, 0, ""));
                });

        OpenApiProperties properties = properties(SECRET_KEY, 10, Duration.ofMillis(100));
        OpenApiClientException ex =
                catchFailure(client(properties), new StockMarketIndexQuery(null, null, null, null));
        assertThat(ex.getKind()).isEqualTo(OpenApiErrorKind.TIMEOUT);
        assertNoSecret(ex);
        assertThat(ex.getMessage()).doesNotContain("http://");
        assertThat(ex.getMessage()).doesNotContain("getStockMarketIndex");
    }

    @Test
    void inconsistentPaginationFailsInsteadOfPartialSuccess() {
        AtomicInteger hits = new AtomicInteger();
        startServer(
                exchange -> {
                    hits.incrementAndGet();
                    writeJson(exchange, envelope(2, 1, 3, itemJson("20260101", "KOSPI", "코스피")));
                });

        assertThatThrownBy(
                        () ->
                                client(properties(SECRET_KEY, 2, Duration.ofSeconds(2)))
                                        .fetchStockMarketIndex(
                                                new StockMarketIndexQuery(null, null, null, null)))
                .isInstanceOf(OpenApiClientException.class)
                .extracting(ex -> ((OpenApiClientException) ex).getKind())
                .isEqualTo(OpenApiErrorKind.PAGINATION_INCONSISTENT);
        assertThat(hits.get()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void duplicateIdentityAcrossPagesIsPaginationInconsistent() {
        startServer(
                exchange -> {
                    int pageNo = pageNo(exchange);
                    String item = itemJson("20260101", "KOSPI", "코스피");
                    writeJson(exchange, envelope(1, pageNo, 2, item));
                });

        assertThatThrownBy(
                        () ->
                                client(properties(SECRET_KEY, 1, Duration.ofSeconds(2)))
                                        .fetchStockMarketIndex(
                                                new StockMarketIndexQuery(null, null, null, null)))
                .isInstanceOf(OpenApiClientException.class)
                .satisfies(
                        ex ->
                                assertThat(((OpenApiClientException) ex).getKind())
                                        .isEqualTo(OpenApiErrorKind.PAGINATION_INCONSISTENT));
    }

    @Test
    void emptyPageBeforeCompleteCollectionIsPaginationFailure() {
        startServer(
                exchange -> {
                    int pageNo = pageNo(exchange);
                    if (pageNo == 1) {
                        writeJson(
                                exchange, envelope(2, 1, 3, itemJson("20260101", "KOSPI", "코스피")));
                        return;
                    }
                    writeJson(exchange, envelope(2, 2, 3, ""));
                });

        assertThatThrownBy(
                        () ->
                                client(properties(SECRET_KEY, 2, Duration.ofSeconds(2)))
                                        .fetchStockMarketIndex(
                                                new StockMarketIndexQuery(null, null, null, null)))
                .isInstanceOf(OpenApiClientException.class)
                .satisfies(
                        ex ->
                                assertThat(((OpenApiClientException) ex).getKind())
                                        .isEqualTo(OpenApiErrorKind.PAGINATION_INCONSISTENT));
    }

    @Test
    void missingServiceKeyFailsOnlyOnCall() {
        OpenApiProperties properties = properties("  ", 10, Duration.ofSeconds(2));
        properties.setUri("http://127.0.0.1:1/getStockMarketIndex");

        OpenApiClientException ex =
                catchFailure(client(properties), new StockMarketIndexQuery(null, null, null, null));
        assertThat(ex.getKind()).isEqualTo(OpenApiErrorKind.CONFIG_UNAVAILABLE);
        assertThat(ex.getMessage()).contains("서비스 키");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void rejectsMixedExactDateAndRange() {
        OpenApiProperties properties = properties(SECRET_KEY, 10, Duration.ofSeconds(2));
        properties.setUri("http://127.0.0.1:1/getStockMarketIndex");

        OpenApiClientException ex =
                catchFailure(
                        client(properties),
                        new StockMarketIndexQuery(
                                null,
                                LocalDate.of(2026, 1, 1),
                                LocalDate.of(2026, 1, 1),
                                LocalDate.of(2026, 1, 2)));
        assertThat(ex.getKind()).isEqualTo(OpenApiErrorKind.INVALID_REQUEST);
    }

    @Test
    void rejectsInvertedRange() {
        OpenApiProperties properties = properties(SECRET_KEY, 10, Duration.ofSeconds(2));
        properties.setUri("http://127.0.0.1:1/getStockMarketIndex");

        OpenApiClientException ex =
                catchFailure(
                        client(properties),
                        new StockMarketIndexQuery(
                                null, null, LocalDate.of(2026, 1, 3), LocalDate.of(2026, 1, 1)));
        assertThat(ex.getKind()).isEqualTo(OpenApiErrorKind.INVALID_REQUEST);
    }

    @Test
    void rejectsDateOverflowOnExclusiveEnd() {
        OpenApiProperties properties = properties(SECRET_KEY, 10, Duration.ofSeconds(2));
        properties.setUri("http://127.0.0.1:1/getStockMarketIndex");

        OpenApiClientException ex =
                catchFailure(
                        client(properties),
                        new StockMarketIndexQuery(null, null, null, LocalDate.MAX));
        assertThat(ex.getKind()).isEqualTo(OpenApiErrorKind.INVALID_REQUEST);
    }

    @Test
    void networkFailureDoesNotLeakUrl() {
        OpenApiProperties properties = properties(SECRET_KEY, 10, Duration.ofMillis(300));
        properties.setUri("http://127.0.0.1:1/getStockMarketIndex");

        OpenApiClientException ex =
                catchFailure(client(properties), new StockMarketIndexQuery(null, null, null, null));
        assertThat(ex.getKind()).isIn(OpenApiErrorKind.NETWORK_ERROR, OpenApiErrorKind.TIMEOUT);
        assertNoSecret(ex);
        assertThat(ex.getMessage()).doesNotContain("127.0.0.1");
        assertThat(ex.getMessage()).doesNotContain("getStockMarketIndex");
    }

    private void assertSecretFreeFailure(OpenApiErrorKind kind, String messagePart) {
        OpenApiClientException ex =
                catchFailure(
                        client(properties(SECRET_KEY, 10, Duration.ofSeconds(2))),
                        new StockMarketIndexQuery(null, null, null, null));
        assertThat(ex.getKind()).isEqualTo(kind);
        assertThat(ex.getMessage()).contains(messagePart);
        assertNoSecret(ex);
    }

    private static OpenApiClientException catchFailure(
            OpenApiClient client, StockMarketIndexQuery query) {
        try {
            client.fetchStockMarketIndex(query);
            throw new AssertionError("expected OpenApiClientException");
        } catch (OpenApiClientException ex) {
            return ex;
        }
    }

    private static void assertNoSecret(OpenApiClientException ex) {
        List<String> texts = new ArrayList<>();
        texts.add(String.valueOf(ex.getMessage()));
        texts.add(ex.toString());
        for (Throwable current = ex; current != null; current = current.getCause()) {
            texts.add(String.valueOf(current.getMessage()));
            texts.add(current.toString());
        }
        assertThat(ex.getCause()).isNull();
        assertThat(texts)
                .allSatisfy(
                        text -> {
                            assertThat(text).doesNotContain("SECRETKEY");
                            assertThat(text).doesNotContain(SECRET_KEY);
                            assertThat(text)
                                    .doesNotContain(
                                            UriUtils.encode(SECRET_KEY, StandardCharsets.UTF_8));
                        });
    }

    private static String queryValue(String rawQuery, String name) {
        for (String pair : rawQuery.split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0 && name.equals(pair.substring(0, eq))) {
                return pair.substring(eq + 1);
            }
        }
        throw new AssertionError("missing query parameter: " + name);
    }

    private OpenApiClient client(OpenApiProperties properties) {
        RestClient restClient = OpenApiRestClientConfig.createRestClient(properties);
        return new OpenApiClientImpl(restClient, properties, OBJECT_MAPPER);
    }

    private OpenApiProperties properties(String serviceKey, int numOfRows, Duration callTimeout) {
        OpenApiProperties properties = new OpenApiProperties();
        if (server != null) {
            properties.setUri(baseUri());
        }
        properties.setServiceKey(serviceKey);
        properties.setNumOfRows(numOfRows);
        properties.setConnectTimeout(Duration.ofMillis(300));
        properties.setCallTimeout(callTimeout);
        return properties;
    }

    private String baseUri() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/getStockMarketIndex";
    }

    private void startServer(Handler handler) {
        try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            executor = Executors.newCachedThreadPool();
            server.setExecutor(executor);
            server.createContext(
                    "/getStockMarketIndex",
                    exchange -> {
                        try {
                            handler.handle(exchange);
                        } catch (Exception ex) {
                            writePlain(exchange, 500, "mock error");
                        } finally {
                            exchange.close();
                        }
                    });
            server.start();
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static int pageNo(HttpExchange exchange) {
        String rawQuery = exchange.getRequestURI().getRawQuery();
        if (rawQuery == null) {
            return 1;
        }
        for (String pair : rawQuery.split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0 && "pageNo".equals(pair.substring(0, eq))) {
                return Integer.parseInt(pair.substring(eq + 1));
            }
        }
        return 1;
    }

    private static void writeJson(HttpExchange exchange, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void writePlain(HttpExchange exchange, int status, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /** 외부 API 응답 형태에 맞춰 0건은 빈 문자열, 단건은 객체, 다건은 배열로 만든다. */
    private static String envelope(int numOfRows, int pageNo, int totalCount, String... itemJsons) {
        List<String> items =
                Arrays.stream(itemJsons)
                        .filter(item -> item != null && !item.isBlank())
                        .map(String::strip)
                        .toList();
        String itemsJson;
        if (items.isEmpty()) {
            itemsJson = "\"\"";
        } else if (items.size() == 1) {
            itemsJson = "{\"item\":" + items.get(0) + "}";
        } else {
            itemsJson = "{\"item\":[" + String.join(",", items) + "]}";
        }
        return """
                {"response":{"header":{"resultCode":"00","resultMsg":"NORMAL SERVICE."},\
                "body":{"numOfRows":%d,"pageNo":%d,"totalCount":%d,"items":%s}}}
                """
                .formatted(numOfRows, pageNo, totalCount, itemsJson);
    }

    private static String itemJson(String basDt, String idxCsf, String idxNm) {
        return """
                {"basDt":"%s","idxCsf":"%s","idxNm":"%s","epyItmsCnt":"10","clpr":"1","vs":"0",\
                "fltRt":"0","mkp":"1","hipr":"1","lopr":"1","trqu":"1","trPrc":"1",\
                "lstgMrktTotAmt":"1","basPntm":"19800104","basIdx":"100"}
                """
                .formatted(basDt, idxCsf, idxNm);
    }

    @FunctionalInterface
    private interface Handler {
        void handle(HttpExchange exchange) throws IOException;
    }
}
