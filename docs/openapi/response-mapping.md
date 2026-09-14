# Open API 응답 구조와 내부 모델 매핑

금융위원회(공공데이터포털) `getStockMarketIndex` 응답을 Findex `IndexInfo` / `IndexData`에 어떻게 대응하는지 정리한다. HTTP 호출(#13)은 이 문서 범위가 아니다.

공식 제공 페이지: https://www.data.go.kr/data/15094807/openapi.do

## 샘플 출처와 라이브 확인 상태

서비스 키가 아직 없어 **실제 호출 원본은 캡처하지 않았다.** 인증키로 실제 응답을 확인하는 작업은 남아 있다.

테스트 JSON은 요구사항 06의 응답 항목 표와, 공공데이터포털 XML→JSON 변환에서 **흔히 쓰이는** wrapper 형태를 바탕으로 만든 **대표 샘플**이다. 실응답이라고 취급하면 안 된다.

| 구분 | 의미 |
|---|---|
| **방어적으로 지원하는 envelope** | DTO가 역직렬화하도록 구현·테스트한 형태. 제공자가 항상 이렇게 준다는 증거가 아니다. |
| **확인된 라이브 제공자 동작** | 없음. 유효 키도, 실캡처도 없다. |

라이브로 아직 확인하지 못한 것:

- `endBasDt`가 문서대로 **미만(검색값보다 작은)** 인지, 양끝 포함인지
- 0건일 때 `items`가 빈 문자열(`""`)인지, `{}`인지, `{ "item": [] }`인지
- 인증/쿼터/파라미터 오류 시 header만 오는지, body가 빠지는지, HTTP 상태 코드가 4xx인지

## 전체 응답 구조

```text
{
  "response": {                          // StockMarketIndexResponse.response
    "header": {                          // StockMarketIndexHeader
      "resultCode": "00",                // 정상은 "00"
      "resultMsg": "NORMAL_SERVICE"
    },
    "body": {                            // StockMarketIndexBody
      "numOfRows": 10,
      "pageNo": 1,
      "totalCount": 2,
      "items": {                         // StockMarketIndexItems
        "item": [ { ... }, { ... } ]     // StockMarketIndexItem 리스트
      }
    }
  }
}
```

파싱 루트는 `StockMarketIndexResponse`이다. `#13`은 `ObjectMapper.readValue(json, StockMarketIndexResponse.class)`로 읽는다.

`body`가 없으면 편의 메서드 `items()`는 빈 리스트를 돌려준다. 그래도 **#13은 `body() == null`을 잘못된 응답으로 처리해야 한다.** 빈 목록과 본문 누락을 같은 정상 0건으로 보면 안 된다.

### item 단건 / 다건 / 빈 목록 (방어적 지원)

XML→JSON 잔재로 `item` 형태가 갈릴 수 있어, 아래 형태는 **항상 `List<StockMarketIndexItem>`** 으로 맞춘다. 라이브에서 어떤 빈 목록 형태가 오는지는 미확인이다.

| JSON 형태 | DTO 결과 (방어적) |
|---|---|
| `"item": [ {...}, {...} ]` | 다건 리스트 |
| `"item": { ... }` | 원소 1개 리스트 |
| `"items": ""` | 빈 리스트 |
| `"items": {}` | 빈 리스트 |
| `"item": []` / `item` 없음 / null | 빈 리스트 |

알 수 없는 JSON 필드는 `@JsonIgnoreProperties(ignoreUnknown = true)`로 무시한다.

소비 필드가 빈 문자열이면 `null`. 형식이 잘못된 **소비 필드** 숫자/날짜는 `JsonMappingException`.

## 날짜 두 개 — 서로 바꿔 넣으면 안 된다

| 외부 필드 | 의미 | 내부 |
|---|---|---|
| `basPntm` | 지수를 **산출하기 위한 기준 시점** (예: 코스피 1980-01-04) | 공개 DTO `IndexInfoDto.basePointInTime` / 현재 엔티티 `IndexInfo.baseDate` |
| `basDt` | 그 행의 **시세 기준일자** | `IndexData.baseDate` |

`IndexInfo.baseDate`라는 엔티티 이름만 보고 `basDt`를 넣으면 안 된다.

형식은 둘 다 숫자 8자리 `yyyyMMdd` → `LocalDate` (달력 STRICT, 빈 문자열은 전송 계층에서 `null`).

## IndexInfo 매핑

유니크 키: **`idxCsf` + `idxNm`** → `indexClassification` + `indexName`.

| 외부 | 타입 | IndexInfo / IndexInfoDto |
|---|---|---|
| `idxCsf` | `String` | `indexClassification` |
| `idxNm` | `String` | `indexName` |
| `epyItmsCnt` | `Integer` | `employedItemsCount` |
| `basPntm` | `LocalDate` | `baseDate` (엔티티) / `basePointInTime` (공개 DTO) |
| `basIdx` | `BigDecimal` | `baseIndex` |
| (없음) | — | `sourceType` — 연동 저장 시 `OPEN_API`로 부여 |
| (없음) | — | `favorite` — 외부에 없음. 신규 등록 기본 `false`, 갱신 시 기존 값 유지 |

같은 지수가 날짜별로 여러 행으로 온다. **대표 행을 고르는 일은 #11 서비스 정책**이다. #13 전송 계층은 행을 고르지 않는다.

## IndexData 매핑

유니크 키: **연결한 `IndexInfo` + `basDt`** → `indexInfo` + `baseDate`.

| 외부 | 타입 | IndexData / IndexDataDto |
|---|---|---|
| (idxCsf+idxNm으로 찾은 지수) | — | `indexInfo` / `indexInfoId` |
| `basDt` | `LocalDate` | `baseDate` |
| `mkp` | `BigDecimal` | `marketPrice` |
| `clpr` | `BigDecimal` | `closingPrice` |
| `hipr` | `BigDecimal` | `highPrice` |
| `lopr` | `BigDecimal` | `lowPrice` |
| `vs` | `BigDecimal` | `versus` |
| `fltRt` | `BigDecimal` | `fluctuationRate` |
| `trqu` | `Long` | `tradingQuantity` |
| `trPrc` | `Long` | `tradingPrice` |
| `lstgMrktTotAmt` | `Long` | `marketTotalAmount` |
| (없음) | — | `sourceType` — 연동 저장 시 `OPEN_API` |

## 무시하는 외부 필드

`StockMarketIndexItem`에는 Findex가 쓰는 필드만 둔다. 아래는 스펙에 있어도 DTO 컴포넌트가 아니며, unknown으로 무시한다. 형식이 달라도 나머지 소비 필드는 읽을 수 있다.

- `lsYrEdVsFltRg` (전년말대비 등락폭)
- `lsYrEdVsFltRt` (전년말대비 등락률)
- `yrWRcrdHgst` / `yrWRcrdHgstDt` (연중최고)
- `yrWRcrdLwst` / `yrWRcrdLwstDt` (연중최저)

## 숫자 정밀도

JSON 숫자 리터럴과 숫자 문자열 모두 원문 텍스트로 `BigDecimal`/`Long`/`Integer`를 만든다. `Double`이나 JsonNode(`DoubleNode`)를 거치지 않는다.

- 가격·지수·등락률: `BigDecimal`
- 거래량·거래대금·상장시가총액: `Long` (`.0`처럼 정수인 소수 리터럴은 허용, 진짜 소수는 거부)
- 채용 종목 수·페이지 필드: `Integer`

## header 결과 코드

`resultCode == "00"`이면 정상, **그 외 값이거나 없으면 정상이 아니다.** 제공자 오류 코드 전체 표는 라이브로 확인하지 않았으므로 여기에 적지 않는다.

## 요청 파라미터 메모 (`endBasDt`)

문서(요구사항 06): `endBasDt`는 **기준일자가 검색값보다 작은** 데이터. **실제 경계는 라이브 호출로 확인하기 전에는 확정하지 않는다.**

## #13 계약 요약

- 패키지: `com.sprint.findex.domain.openapi.dto.response`
- 파싱 타입: `StockMarketIndexResponse`
- 항목 타입: `StockMarketIndexItem` (소비 필드만)
- `body() == null` 이면 잘못된 응답으로 처리한다 (`items()`가 비어 있어도 정상 0건이 아님)
- 응답 DTO 범위라 HTTP 클라이언트/인터페이스/엔티티 저장 매퍼는 포함하지 않는다

## 테스트

```text
./gradlew test
```

테스트는 네트워크를 타지 않으므로 실제 호출 확인을 대체하지 않는다.
