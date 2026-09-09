package com.sprint.findex.domain.openapi.dto.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StockMarketIndexResponseDeserializationTest {

    private static final ObjectMapper MAPPER =
            JsonMapper.builder()
                    .addModule(new JavaTimeModule())
                    .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .build();

    /** 전역 BigDecimal 설정 없이도 숫자 정밀도가 보존되는지 검증한다. */
    private static final ObjectMapper VANILLA_MAPPER = new ObjectMapper().findAndRegisterModules();

    private static final String HIGH_PRECISION_LEXEME = "0.123456789012345678901";
    private static final BigDecimal HIGH_PRECISION = new BigDecimal(HIGH_PRECISION_LEXEME);

    @Test
    @DisplayName("다건 item 배열과 숫자 문자열을 매핑하고 basDt와 basPntm을 구분한다")
    void deserializesMultiItemArrayFromNumericStrings() throws Exception {
        StockMarketIndexResponse response =
                readResource("openapi/stock-market-index-multi-item.json");

        assertThat(response.isNormalService()).isTrue();
        assertThat(response.header().resultMsg()).isEqualTo("NORMAL_SERVICE");
        assertThat(response.body().numOfRows()).isEqualTo(2);
        assertThat(response.body().pageNo()).isEqualTo(1);
        assertThat(response.body().totalCount()).isEqualTo(2);
        assertThat(response.items()).hasSize(2);

        StockMarketIndexItem kospi = response.items().get(0);
        assertThat(kospi.idxCsf()).isEqualTo("KOSPI시리즈");
        assertThat(kospi.idxNm()).isEqualTo("코스피");
        assertThat(kospi.epyItmsCnt()).isEqualTo(847);
        assertThat(kospi.basDt()).isEqualTo(LocalDate.of(2024, 1, 2));
        assertThat(kospi.basPntm()).isEqualTo(LocalDate.of(1980, 1, 4));
        assertThat(kospi.basDt()).isNotEqualTo(kospi.basPntm());
        assertThat(kospi.basIdx()).isEqualByComparingTo("100");
        assertThat(kospi.clpr()).isEqualByComparingTo("2669.81");
        assertThat(kospi.vs()).isEqualByComparingTo("16.52");
        assertThat(kospi.fltRt()).isEqualByComparingTo("0.62");
        assertThat(kospi.mkp()).isEqualByComparingTo("2655.28");
        assertThat(kospi.hipr()).isEqualByComparingTo("2675.64");
        assertThat(kospi.lopr()).isEqualByComparingTo("2652.19");
        assertThat(kospi.trqu()).isEqualTo(412345678L);
        assertThat(kospi.trPrc()).isEqualTo(9_876_543_210_000L);
        assertThat(kospi.lstgMrktTotAmt()).isEqualTo(2_150_123_456_789_012L);
    }

    @Test
    @DisplayName("단건 item 객체를 원소 1개 리스트로 읽고 Double이 깨는 정수를 Long으로 보존한다")
    void deserializesSingletonItemObjectWithoutDoublePrecisionLoss() throws Exception {
        StockMarketIndexResponse response =
                readResource("openapi/stock-market-index-singleton-item.json");

        assertThat(response.items()).hasSize(1);
        StockMarketIndexItem item = response.items().get(0);
        assertThat(item.idxNm()).isEqualTo("코스피");
        assertThat(item.basDt()).isEqualTo(LocalDate.of(2024, 1, 3));
        assertThat(item.basPntm()).isEqualTo(LocalDate.of(1980, 1, 4));

        long exact = 9_007_199_254_740_993L;
        assertThat(item.trPrc()).isEqualTo(exact);
        assertThat(item.lstgMrktTotAmt()).isEqualTo(exact);
        assertThat((long) Double.parseDouble("9007199254740993")).isNotEqualTo(exact);
        assertThat(item.fltRt()).isEqualByComparingTo(new BigDecimal("-0.33"));
    }

    @Test
    @DisplayName("단건 item의 숫자 리터럴 clpr은 Double 정밀도 없이 원문 소수를 보존한다")
    void preservesHighPrecisionNumericLiteralInSingletonItem() throws Exception {
        String json =
                itemEnvelope("{ \"clpr\": " + HIGH_PRECISION_LEXEME + ", \"idxNm\": \"코스피\" }");

        StockMarketIndexItem item =
                VANILLA_MAPPER.readValue(json, StockMarketIndexResponse.class).items().get(0);

        assertThat(item.clpr()).isEqualTo(HIGH_PRECISION);
        assertThat(item.clpr())
                .isNotEqualTo(BigDecimal.valueOf(Double.parseDouble(HIGH_PRECISION_LEXEME)));
    }

    @Test
    @DisplayName("배열 item의 숫자 리터럴 clpr도 원문 소수를 보존한다")
    void preservesHighPrecisionNumericLiteralInItemArray() throws Exception {
        String json =
                """
                {
                  "response": {
                    "header": { "resultCode": "00", "resultMsg": "NORMAL_SERVICE" },
                    "body": {
                      "items": {
                        "item": [
                          { "idxNm": "코스피", "clpr": %s },
                          { "idxNm": "코스닥", "clpr": %s }
                        ]
                      }
                    }
                  }
                }
                """
                        .formatted(HIGH_PRECISION_LEXEME, HIGH_PRECISION_LEXEME);

        StockMarketIndexResponse response =
                VANILLA_MAPPER.readValue(json, StockMarketIndexResponse.class);

        assertThat(response.items()).hasSize(2);
        assertThat(response.items().get(0).clpr()).isEqualTo(HIGH_PRECISION);
        assertThat(response.items().get(1).clpr()).isEqualTo(HIGH_PRECISION);
        assertThat(response.items().get(0).clpr())
                .isNotEqualTo(BigDecimal.valueOf(Double.parseDouble(HIGH_PRECISION_LEXEME)));
    }

    @Test
    @DisplayName("정수인 소수 리터럴 9007199254740993.0은 정확한 Long이고 소수 Long/Integer는 거부한다")
    void mapsIntegralFloatLiteralToLongAndRejectsFractionalWholeNumbers() throws Exception {
        String json =
                itemEnvelope(
                        "{ \"trqu\": 9007199254740993.0, \"lstgMrktTotAmt\": 9007199254740993.0 }");

        StockMarketIndexItem item = read(json).items().get(0);
        long exact = 9_007_199_254_740_993L;
        assertThat(item.trqu()).isEqualTo(exact);
        assertThat(item.lstgMrktTotAmt()).isEqualTo(exact);
        assertThat((long) Double.parseDouble("9007199254740993.0")).isNotEqualTo(exact);

        assertThatThrownBy(() -> read(itemEnvelope("{ \"trqu\": 12.34 }")))
                .isInstanceOf(JsonMappingException.class);
        assertThatThrownBy(() -> read(itemEnvelope("{ \"epyItmsCnt\": 1.5 }")))
                .isInstanceOf(JsonMappingException.class);
    }

    @Test
    @DisplayName("윤년 2월 29일은 허용하고 존재하지 않는 달력 날짜는 거부한다")
    void acceptsValidLeapDateAndRejectsInvalidCalendarDates() throws Exception {
        StockMarketIndexItem leap =
                read(itemEnvelope("{ \"basDt\": \"20240229\" }")).items().get(0);
        assertThat(leap.basDt()).isEqualTo(LocalDate.of(2024, 2, 29));

        assertThatThrownBy(() -> read(itemEnvelope("{ \"basDt\": \"20260230\" }")))
                .isInstanceOf(JsonMappingException.class);
        assertThatThrownBy(() -> read(itemEnvelope("{ \"basDt\": \"20250229\" }")))
                .isInstanceOf(JsonMappingException.class);
        assertThatThrownBy(() -> read(itemEnvelope("{ \"basDt\": \"20260431\" }")))
                .isInstanceOf(JsonMappingException.class);
        assertThatThrownBy(() -> read(itemEnvelope("{ \"basDt\": \"2024-01-02\" }")))
                .isInstanceOf(JsonMappingException.class);
    }

    @Test
    @DisplayName("소비하지 않는 연중최고일 형식이 깨져도 나머지 행은 읽는다")
    void ignoresMalformedUnusedYearHighDate() throws Exception {
        StockMarketIndexResponse response =
                read(
                        itemEnvelope(
                                """
                                {
                                  "basDt": "20240102",
                                  "idxNm": "코스피",
                                  "yrWRcrdHgstDt": "20260230"
                                }
                                """));

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).basDt()).isEqualTo(LocalDate.of(2024, 1, 2));
        assertThat(response.items().get(0).idxNm()).isEqualTo("코스피");
    }

    @Test
    @DisplayName("items가 빈 문자열이면 빈 리스트다")
    void deserializesEmptyItemsStringAsEmptyList() throws Exception {
        StockMarketIndexResponse response =
                read(
                        """
                        {
                          "response": {
                            "header": { "resultCode": "00", "resultMsg": "NORMAL_SERVICE" },
                            "body": {
                              "numOfRows": 10,
                              "pageNo": 1,
                              "totalCount": 0,
                              "items": ""
                            }
                          }
                        }
                        """);

        assertThat(response.isNormalService()).isTrue();
        assertThat(response.body().totalCount()).isEqualTo(0);
        assertThat(response.items()).isEmpty();
        assertThat(response.body().itemList()).isEmpty();
    }

    @Test
    @DisplayName("items가 빈 객체이거나 item 배열이 비어 있으면 빈 리스트다")
    void deserializesEmptyItemsObjectAndEmptyItemArray() throws Exception {
        StockMarketIndexResponse emptyObject =
                read(
                        """
                        {
                          "response": {
                            "header": { "resultCode": "00", "resultMsg": "NORMAL_SERVICE" },
                            "body": { "totalCount": 0, "items": {} }
                          }
                        }
                        """);
        StockMarketIndexResponse emptyArray =
                read(
                        """
                        {
                          "response": {
                            "header": { "resultCode": "00", "resultMsg": "NORMAL_SERVICE" },
                            "body": { "items": { "item": [] } }
                          }
                        }
                        """);
        StockMarketIndexResponse missingItems =
                read(
                        """
                        {
                          "response": {
                            "header": { "resultCode": "00", "resultMsg": "NORMAL_SERVICE" },
                            "body": { "totalCount": 0 }
                          }
                        }
                        """);

        assertThat(emptyObject.items()).isEmpty();
        assertThat(emptyArray.items()).isEmpty();
        assertThat(missingItems.items()).isEmpty();
    }

    @Test
    @DisplayName("문서에 없는 필드는 무시한다")
    void ignoresUnknownFields() throws Exception {
        StockMarketIndexResponse response =
                read(
                        """
                        {
                          "response": {
                            "header": {
                              "resultCode": "00",
                              "resultMsg": "NORMAL_SERVICE",
                              "unknownHeader": true
                            },
                            "body": {
                              "numOfRows": 1,
                              "extraBody": 1,
                              "items": {
                                "item": {
                                  "basDt": "20240102",
                                  "idxNm": "코스피",
                                  "undocumented": "skip-me"
                                }
                              }
                            }
                          },
                          "traceId": "not-in-spec"
                        }
                        """);

        assertThat(response.isNormalService()).isTrue();
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).idxNm()).isEqualTo("코스피");
        assertThat(response.items().get(0).basDt()).isEqualTo(LocalDate.of(2024, 1, 2));
    }

    @Test
    @DisplayName("잘못된 숫자 문자열은 매핑 예외를 던진다")
    void rejectsMalformedNumericString() {
        assertThatThrownBy(
                        () ->
                                read(
                                        itemEnvelope(
                                                "{ \"idxNm\": \"코스피\", \"clpr\": \"not-a-number\" }")))
                .isInstanceOf(JsonMappingException.class);
    }

    @Test
    @DisplayName("빈 숫자·날짜 문자열은 null이고 소수 있는 Long 문자열은 거부한다")
    void treatsBlankScalarsAsNullAndRejectsNonIntegralLong() throws Exception {
        StockMarketIndexResponse response =
                read(
                        itemEnvelope(
                                """
                                {
                                  "basDt": "",
                                  "basPntm": "   ",
                                  "clpr": "",
                                  "epyItmsCnt": "",
                                  "trqu": "",
                                  "idxNm": "코스피"
                                }
                                """));

        StockMarketIndexItem item = response.items().get(0);
        assertThat(item.basDt()).isNull();
        assertThat(item.basPntm()).isNull();
        assertThat(item.clpr()).isNull();
        assertThat(item.epyItmsCnt()).isNull();
        assertThat(item.trqu()).isNull();

        assertThatThrownBy(() -> read(itemEnvelope("{ \"trqu\": \"12.34\" }")))
                .isInstanceOf(JsonMappingException.class);
    }

    @Test
    @DisplayName("오류 header는 정상이 아니며 body 없음은 편의 items()만 비울 뿐 본문 누락이다")
    void mapsErrorHeaderWithoutPretendingSuccess() throws Exception {
        StockMarketIndexResponse response =
                read(
                        """
                        {
                          "response": {
                            "header": {
                              "resultCode": "30",
                              "resultMsg": "SERVICE_KEY_IS_NOT_REGISTERED_ERROR"
                            }
                          }
                        }
                        """);

        assertThat(response.isNormalService()).isFalse();
        assertThat(response.header().resultCode()).isEqualTo("30");
        assertThat(response.body()).isNull();
        assertThat(response.items()).isEmpty();
    }

    @Test
    @DisplayName("resultCode 00이어도 body가 없으면 본문 누락이다")
    void missingBodyIsStillNullWhenResultCodeIsNormal() throws Exception {
        StockMarketIndexResponse response =
                read(
                        """
                        {
                          "response": {
                            "header": { "resultCode": "00", "resultMsg": "NORMAL_SERVICE" }
                          }
                        }
                        """);

        assertThat(response.isNormalService()).isTrue();
        assertThat(response.body()).isNull();
        assertThat(response.items()).isEmpty();
    }

    private static String itemEnvelope(String itemJson) {
        return """
                {
                  "response": {
                    "header": { "resultCode": "00", "resultMsg": "NORMAL_SERVICE" },
                    "body": {
                      "items": {
                        "item": %s
                      }
                    }
                  }
                }
                """
                .formatted(itemJson);
    }

    private static StockMarketIndexResponse readResource(String path) throws IOException {
        try (InputStream input =
                StockMarketIndexResponseDeserializationTest.class
                        .getClassLoader()
                        .getResourceAsStream(path)) {
            assertThat(input).as("fixture %s", path).isNotNull();
            return MAPPER.readValue(input, StockMarketIndexResponse.class);
        }
    }

    private static StockMarketIndexResponse read(String json) throws JsonProcessingException {
        return MAPPER.readValue(json, StockMarketIndexResponse.class);
    }
}
