# 📈 프로젝트 소개

### 금융위원회 Open API 기반 한국 주가지수 분석 서비스

> 공공데이터 Open API를 활용하여 주가지수 데이터를 수집하고,
> 지수 정보 관리, 금융 지표 분석, 자동 데이터 연동을 제공하는
> 금융 분석 백엔드 서비스입니다.

프로젝트 기간 : 2026.09.08 ~ 2026.09.17

---

## 🧑‍💻 팀원 구성

|                                  이승현                                  |                                 김예준                                 |                                   김승호                                   |                                     이수찬                                     |                                    김양현                                    |                                강성준                                |
|:------------------------------------------------------------------------:|:----------------------------------------------------------------------:|:--------------------------------------------------------------------------:|:------------------------------------------------------------------------------:|:----------------------------------------------------------------------------:|:--------------------------------------------------------------------:|
| <img src="https://github.com/Hanna-log.png" width="120" alt="pintordev"> | <img src="https://github.com/kim-yejunn.png" width="120" alt="gim00001"> | <img src="https://github.com/WinLike-dev.png" width="120" alt="rhksgml54"> | <img src="https://github.com/lsc0869.png" width="120" alt="jeongjae5310"> | <img src="https://github.com/yyy2724.png" width="120" alt="Junyeong-An"> | <img src="https://github.com/xian980.png" width="120" alt="shyunii"> |
|                [Hanna-log](https://github.com/Hanna-log)                 |                [kim-yejunn](https://github.com/kim-yejunn)                 |               [WinLike-dev](https://github.com/WinLike-dev)                |                [lsc0869](https://github.com/lsc0869)                 |                [yyy2724](https://github.com/yyy2724)                 |                [xian980](https://github.com/xian980)                 |


---

## 🛠 Tech Stack

### Backend
- ![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot%203.5.16-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=flat-square&logoColor=white)
![Spring Validation](https://img.shields.io/badge/Spring%20Validation-6DB33F?style=flat-square&logo=spring&logoColor=white)

- ![QueryDSL](https://img.shields.io/badge/QueryDSL%206.10.1%20(OpenFeign)-0769AD?style=flat-square&logoColor=white)
![MapStruct](https://img.shields.io/badge/MapStruct%201.6.3-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok%201.18-BC4521?style=flat-square&logoColor=white)

### Database & Infra

- ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![HikariCP](https://img.shields.io/badge/HikariCP-FF6F00?style=flat-square&logoColor=white)
![Actuator](https://img.shields.io/badge/Spring%20Actuator-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Railway](https://img.shields.io/badge/Railway-0B0D0E?style=flat-square&logo=railway&logoColor=white)

### Collaboration

- ![Git](https://img.shields.io/badge/Git-F05032?style=flat-square&logo=Git&logoColor=white)
- ![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)
- ![Discord](https://img.shields.io/badge/Discord-5865F2?style=flat-square&logo=discord&logoColor=white)
- ![Notion](https://img.shields.io/badge/Notion-000000?style=flat-square&logo=notion&logoColor=white)


### Documentation

- ![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black)

### External API

-   <img src="https://img.shields.io/badge/RestClient-6DB33F?style=flat-square&logo=spring&logoColor=white"/>
- 금융위원회 지수시세정보 Open API

### Scheduling

- Spring Scheduler


---


## ⚡ SB14-Avengers

| Category | Link | Description |
| :--- | :--- | :--- |
| Workspace | [Notion Workspace](https://few-patch-6f7.notion.site/Avengers-SB14-Findex-Team-01-a2665019b7368330998b013c6a469ab7) | 기획 문서, 회의록 및 팀 컨벤션 관리 |
| Management | [GitHub Issues](https://github.com/orgs/SB14-Avengers/projects/5) | 이슈 카드를 활용한 역할 및 일정 관리 |
| 요구사항 명세서 | [요구사항 구현 일정관리](https://few-patch-6f7.notion.site/e6065019b73683e08d2b819faa0b0dc5?v=caa65019b73682b6a428084967fae726) | 요구사항 구현 일정관리 |
| API Docs | [Swagger UI](https://sb14-findex-team01-production.up.railway.app/swagger-ui/index.html) | RESTful API 명세서 |
| Design | [ERD & 배포 다이어그램](https://few-patch-6f7.notion.site/ERD-9b565019b73683f398f20174af387e00) | 데이터베이스 구조 설계도 |
| 발표 자료 | [발표 자료](https://drive.google.com/file/d/13RAchL-MK7qadPWmAEE_c0_7ruD0_n60/view) | 발표 자료 |

---

## 🛠️ 팀원별 구현 기능 상세

<details>
<summary>🧑‍💻이승현 </summary>

<br />

**⏱️ 자동 연동 설정 관리**

#### 자동 연동 설정 자동 생성

* 지수 정보 등록 시 `AutoSyncConfig`가 함께 생성되도록 구현 (별도 등록 API 없음 — 지수 등록에 종속)
* 지수 정보 삭제 시 `ON DELETE CASCADE`로 자동 연동 설정도 함께 삭제

#### 자동 연동 설정 수정 API

* `enabled` on/off 토글 PATCH API 구현
* `id`가 없거나 `enabled` 값이 누락되면 400, 존재하지 않는 설정이면 404 처리
* 응답 `200 OK`, `AutoSyncConfigDto` 반환

#### 자동 연동 설정 목록 조회

* `indexInfoId` / `enabled` 필터 + 정렬 지원, 커서 기반 페이지네이션
* QueryDSL 동적 쿼리 — `enabled`(boolean) 정렬 시 동점 그룹 안에서는 `id`로 보조 정렬

#### 자동 연동 배치 스케줄러

* 지수별로 활성화된 자동 연동 설정을 기준으로, 매일 정해진 시각(오후 2시 30분)에 Open API에서 최신 지수 데이터를 자동 수집
* 지수마다 마지막으로 어디까지 가져왔는지 계산해 그 이후 구간만 연동 (`resolveFromDate`)
* `AtomicBoolean`으로 배치 중복 실행 방지

#### 배치 성공/실패 판정 및 이력 저장 정확성

<p align="left">
  <img width="700" alt="연동 관리 대시보드 - 자동 연동 성공/실패 현황 및 이력" src="https://github.com/user-attachments/assets/eb0626b2-59a3-4453-b456-1ab948f914bd" />
</p>

* 연동 결과 리스트가 비어 있으면 무조건 "성공"으로 판정되던 조건에 `!isEmpty()` 검증 추가
* 데이터 0건 · 예외 발생 두 경우 모두 `sync_jobs`에 `FAILED` 이력을 직접 저장하도록 보강
* 활성화된 지수 목록 중 정상 성공 처리를 프로덕션에서 직접 확인

#### 영구 실패 방지 재시도 로직

* 마지막 성공일 다음 날부터 재연동하되, 아직 재시도 안 된 실패 날짜가 있으면 그 날짜부터 우선 재시도
* 너무 오래된 실패까지 훑지 않도록 최대 7일(lookback)까지만 재시도 대상으로 포함

</details>

<details>
<summary>🧑‍💻김예준</summary>

<br />

**🔁 연동 작업 관리**

#### 지수 정보 연동

<p align="left">
  <img width="383" height="256" alt="지수 정보 연동" src="https://github.com/user-attachments/assets/2e4463be-04d8-4925-90c5-0ba9b9b9b614" />
  <img width="410" height="256" alt="지수 정보 연동 결과" src="https://github.com/user-attachments/assets/f8e8d5db-5dd1-4fea-b4bd-55fc4cf4d530" />
</p>

* 공공 API에 지수 정보 전용 엔드포인트가 없어, 시세 API 응답에서 지수 메타데이터(채용 종목 수·기준 시점·기준 지수)를 추출
* 최근 영업일을 최대 10일까지 역탐색해 데이터가 있는 날을 찾고, 지수별 최신 행만 골라 upsert
* 재연동 시 사용자가 설정한 즐겨찾기는 덮어쓰지 않음
* 171개 중 필수값이 누락된 K-샤프지수 4종(1년·3년·5년·10년)을 제외한 **167개 등록**

#### 지수 데이터 연동

<p align="left">
  <img width="507" height="342" alt="지수 데이터 연동" src="https://github.com/user-attachments/assets/9fcade4a-5645-4dcb-8bf0-c8a57ce60d52" />
</p>

* 전체 지수 / 단일 지수 기간 연동
* `(지수, 날짜)` 유니크 제약 기준 upsert — 같은 기간 재연동해도 중복 없이 갱신
* 전체 지수 경로는 `index_info`를 한 번만 조회해 Map으로 매칭 (행마다 조회 제거)
* 시세 필드 중 하나라도 누락되면 저장하지 않고 실패 이력만 기록
* 전체 지수 × 1년(27,625행) 연동 성공 27,625 / 실패 0

#### 청크 트랜잭션

* 행마다 커밋되던 구조를 50건 단위 트랜잭션으로 전환 — **커밋 28,393회 → 553회**
* 청크 실패 시 해당 청크만 건별로 재시도해 데이터 보존
* `@Transactional` self-invocation 제약 때문에 트랜잭션 경계와 저장 본문(`writeOne`)을 분리

#### 연동 작업 목록 조회

<p align="left">
  <img width="800" alt="연동 작업 목록 조회" src="https://github.com/user-attachments/assets/c56dacd3-3297-4122-9ebf-451ec3f676d1" />
</p>

* QueryDSL 동적 필터 6종(유형·지수·대상일 범위·작업자·작업일시 범위·결과) + 정렬 2종
* 커서 페이지네이션 — 정렬값과 `id`를 함께 비교해 동점에도 안정적으로 다음 페이지 조회

</details>

<details>
<summary>🧑‍💻김승호</summary>

## Open API 연동 (`domain/openapi`)

공공데이터포털 **주가지수시세 API**를 호출해 조건에 맞는 시세를 **모든 페이지에서 수집**하는 내부 클라이언트.
자체 DB 테이블이나 REST 엔드포인트는 없고, 연동 작업(`syncjob`)이 호출해서 결과를 저장한다.

**호출 흐름**
`fetchStockMarketIndex(query)` → ① 조회 조건 검증 → ② 설정 검증 → ③ 페이지 반복 호출 → ④ 응답·페이지네이션 검증 → ⑤ 조회 조건으로 다시 걸러서 반환

| 패키지 | 파일 | 역할 |
|---|---|---|
| `client/` | `OpenApiClient`, `impl/OpenApiClientImpl` | 호출 규칙(javadoc)과 구현 |
| `config/` | `OpenApiProperties`, `OpenApiRestClientConfig` | 설정값 바인딩, `openApiRestClient` 빈 |
| `dto/request/` | `StockMarketIndexQuery` | 조회 조건 |
| `dto/response/` | `StockMarketIndexResponse` / `Header` / `Body` / `Items` / `Item` | 외부 응답 구조 |
| `dto/jackson/` | 역직렬화기 6종 + `OpenApiNumberParser` | 불규칙한 외부 응답 형식 처리 |
| `exception/` | `OpenApiClientException`, `OpenApiErrorKind` | 실패 8종 분류 |

---

### 1. 설정 검증 (config)

`findex.openapi.*` → `OpenApiProperties`

| 키 | 기본값 | 설명 |
|---|---|---|
| `uri` | (필수) | 주가지수시세 오퍼레이션 전체 URL |
| `service-key` | `""` | 디코딩 서비스 키 (`.env`의 `PUBLIC_API_SERVICE_KEY`), 인코딩은 클라이언트가 처리 |
| `num-of-rows` | `100` | 페이지당 행 수 |
| `connect-timeout` | `3s` | 연결 타임아웃 |
| `call-timeout` | `15s` | **페이지 1회 요청** 타임아웃 (전체 페이지 합산 제한은 없음) |

- **기동 시점** (`OpenApiRestClientConfig`): `uri`가 비었거나 타임아웃이 0 이하면 `IllegalStateException`으로 **앱 기동 실패**
- **호출 시점** (`validateConfig`): 서비스 키 누락, `numOfRows < 1`, 잘못된 타임아웃이면 `CONFIG_UNAVAILABLE`
  → 서비스 키가 없어도 앱은 뜨고, 실제 호출할 때 실패함
- `RestClient` 구성: JDK `HttpClient` 사용, **리다이렉트 따라가지 않음**, URI 인코딩 모드는 `NONE`(클라이언트가 직접 인코딩해서 이중 인코딩 방지)

### 2. 조회 조건 검증 (query)

`StockMarketIndexQuery(indexName, baseDate, fromDate, toDate)`: query 자체는 필수, 각 필드는 선택

| 규칙 | 위반 시 |
|---|---|
| query가 `null` | `INVALID_REQUEST` |
| `baseDate`(하루)와 `fromDate`/`toDate`(기간)를 같이 지정 | `INVALID_REQUEST` |
| `fromDate`가 `toDate`보다 뒤 | `INVALID_REQUEST` |
| `toDate` 다음 날을 계산할 수 없음 (`LocalDate.MAX`) | `INVALID_REQUEST` |

- `indexName`: 앞뒤 공백을 뺀 값과 **완전 일치**. `null`이나 공백이면 이름 조건 없음
- 기간은 **양 끝 포함**. 외부 API의 `endBasDt`가 종료일을 포함하지 않아서 **종료일 + 1일**을 보낸다
- 날짜를 모두 생략하면 제공자의 기본 조회 범위를 따른다 (최신 하루만 온다고 가정하면 안 됨)

### 3. 요청 생성

- 공통 파라미터: `serviceKey`, `resultType=json`, `numOfRows`, `pageNo`
- 조건 파라미터: `idxNm`, `basDt` 또는 `beginBasDt`/`endBasDt` (`yyyyMMdd`)
- 모든 값은 `UriUtils.encode`로 **한 번만** 인코딩한다 (서비스 키의 `+`가 공백으로 바뀌는 문제 방지)
- 동기 호출이며 **자동 재시도는 없다**

### 4. 응답 역직렬화 (jackson)

외부 응답 형식이 불규칙해서 전용 역직렬화기를 둔다.

| 역직렬화기 | 처리 내용 |
|---|---|
| `StockMarketIndexItemsDeserializer` | `body.items`가 `null`이거나 `""`이면 빈 목록 |
| `FlexibleItemListDeserializer` | `item`이 **단건 객체든 배열이든** 같은 `List`로 변환 |
| `OpenApiBigDecimal/Integer/LongDeserializer` | 숫자와 숫자 문자열을 모두 허용, **`double`을 거치지 않아 정밀도 유지**, 빈 값은 `null`, Integer/Long은 소수부가 있으면 거부 |
| `OpenApiYyyyMmDdDeserializer` | 8자리 `yyyyMMdd`를 엄격하게 파싱 (`20260230` 같은 날짜 거부) |

- 모든 응답 DTO는 `@JsonIgnoreProperties(ignoreUnknown = true)`
- `basDt`(시세 기준일)와 `basPntm`(지수 산출 기준 시점)은 **서로 다른 값이므로 저장할 때 구분**

### 5. 응답 검증

| 검사 | 위반 시 |
|---|---|
| HTTP 4xx/5xx | `HTTP_ERROR` (상태 코드만 기록) |
| 본문이 비었거나, XML(`<`로 시작)이거나, JSON 파싱 실패 | `MALFORMED_RESPONSE` |
| `header`나 `resultCode`가 없음 | `MALFORMED_RESPONSE` |
| `resultCode`가 `"00"`이 아님 | `EXTERNAL_HEADER` |
| `body` 누락, `totalCount` 누락 또는 음수 | `MALFORMED_RESPONSE` |
| 항목에 `idxNm`, `idxCsf`, `basDt` 중 하나라도 없음 | `MALFORMED_RESPONSE` |

### 6. 페이지네이션 검증

`totalCount`만큼 모일 때까지 `pageNo`를 1부터 올리며 반복하고, 아래 경우는 모두 `PAGINATION_INCONSISTENT`로 처리한다.

- 페이지마다 `totalCount`가 다름
- 응답의 `pageNo`가 요청한 번호와 다름
- `totalCount = 0`인데 항목이 있음
- 수집이 끝나기 전에 빈 페이지가 옴 (무한 루프 방지)
- 같은 `(idxCsf, idxNm, basDt)`가 중복됨
- 수집 건수가 `totalCount`를 넘음

### 7. 반환 결과

- 모든 페이지를 검증한 뒤 **원래 조회 조건(지수명 완전 일치, 날짜 범위)으로 다시 걸러서** 불변 리스트로 반환
- 정상 0건이나 필터링 후 0건은 **빈 리스트**. `null`이나 일부 페이지만 모은 결과는 반환하지 않음 (한 페이지라도 실패하면 예외)
- `idxCsf`, `idxNm`, `basDt`만 값이 보장되고 나머지 필드는 `null`일 수 있음. **누락 값을 0으로 채우지 않으며**, 누락 행 처리는 호출부 책임
- 지수 정보와 연결할 때는 `idxCsf`와 `idxNm`을 **함께** 비교한다

### 8. 예외 (exception)

실패는 모두 `OpenApiClientException(kind, message)` 하나로 던지고, `syncjob`이 잡아서 실패 이력으로 남긴다.
HTTP 상태로 바꾸거나 이력으로 변환하는 일은 `syncjob`이 맡는다.

| `OpenApiErrorKind` | 의미 |
|---|---|
| `CONFIG_UNAVAILABLE` | 서비스 키 누락 등 설정 문제 |
| `INVALID_REQUEST` | 잘못된 조회 조건 |
| `HTTP_ERROR` | 외부 HTTP 4xx/5xx |
| `NETWORK_ERROR` | 연결 실패 등 네트워크·IO 오류 |
| `TIMEOUT` | 연결 또는 요청 시간 초과 |
| `EXTERNAL_HEADER` | HTTP는 정상이지만 `resultCode`가 `00`이 아님 |
| `MALFORMED_RESPONSE` | JSON 구조나 필수 필드 오류 (정상 0건과 구분) |
| `PAGINATION_INCONSISTENT` | 페이지 진행이나 건수가 서로 맞지 않음 |

- 타임아웃도 `IOException` 계열이라서 **타임아웃을 네트워크 오류보다 먼저** 판별한다

</details>

<details>
<summary>🧑‍💻강성준</summary>

<br />

#### 지수 데이터 목록 조회

<p align="left">
  <img width="350" height="250" alt="지수 데이터 목록 조회 및 필터링" src="https://github.com/user-attachments/assets/76182f3c-2425-4184-9d1c-35fa9d7990e1" />
</p>

#### CSV Export

<p align="left">
  <img width="350" height="250" alt="지수 데이터 CSV 다운로드" src="https://github.com/user-attachments/assets/c6f4c74b-fe41-4701-815c-22b0e16dda1a" />
</p>

**📊 지수 데이터 관리**

#### 지수 데이터 CRUD

* 지수 데이터 등록·단건 조회·부분 수정·삭제 API 구현
* 등록 시 지수 정보 존재 여부와 `(지수, 기준일)` 중복 여부 검증
* 사용자가 직접 등록한 데이터는 `USER` 소스 타입으로 저장
* 생성 `201 Created`, 조회·수정 `200 OK`, 삭제 `204 No Content` 응답 적용
* 수정은 PATCH 방식으로 요청에 포함된 필드만 반영

#### 지수 데이터 목록 조회

* 지수 ID, 시작일·종료일 조건으로 데이터 필터링
* QueryDSL 기반 동적 정렬 및 커서 페이지네이션 구현
* `size + 1` 조회와 정렬값·ID 보조 키 비교로 안정적인 다음 페이지 조회
* 정렬 필드, 정렬 방향, 커서 형식, 날짜 범위를 사전에 검증하여 잘못된 요청은 `400 Bad Request`로 처리

#### CSV Export

* 목록 조회와 동일한 지수·기간·정렬 조건을 유지한 CSV 다운로드 구현
* `StreamingResponseBody`로 서버 디스크 저장 없이 응답 스트림에 파일 생성
* 1,000건 단위 커서 조회와 `EntityManager.clear()`를 적용해 대량 데이터 처리 시 메모리 부담 완화
* 한글 헤더와 UTF-8 BOM을 적용해 스프레드시트 프로그램에서 인코딩 문제 없이 확인 가능
* 잘못된 정렬·날짜 범위 요청은 다운로드 전에 검증하여 CSV 대신 JSON 오류 응답 반환

</details>

<details>
<summary>🧑‍💻김양현</summary>

#### 지수 차트 + 이동평균선
<img width="1388" height="466" alt="지수 차트" src="https://github.com/user-attachments/assets/6488be6d-2ea0-4562-8b79-0e0a47342dc7" />

- 기간(월간·분기·연간)별 종가 시계열과 MA5·MA20을 한 응답에 반환 — 세 리스트 모두 날짜 오름차순
- 이동평균 앞구간이 비지 않도록 조회 시작을 days × 3일 앞으로 당겨 가져온 뒤 표시 구간만 응답에 포함
- 평균은 날짜가 아닌 리스트 인덱스로 최근 N개를 세서 거래일 기준 — 휴장일 테이블 없이 연휴 구간 처리
- BigDecimal 소수 둘째 자리 반올림, 조회 쿼리는 JPQL findChartData

#### 관심 지수 성과
<img width="1398" height="350" alt="주요 지수" src="https://github.com/user-attachments/assets/f4c370a8-f2b4-494d-ada0-866154c4c53e" />

- 즐겨찾기 지수마다 현재가·기준가·변동폭·등락률을 기간별(일간·주간·월간)로 계산
- 기준일을 LocalDate.now()가 아닌 지수의 최신 데이터 날짜(findLatest)로 잡아, 당일 데이터 유무와 무관하게 "최신 종가 vs 직전 거래일 종가"가 되도록 수정 — 일간 등락률 전부 0.00% 나오던 버그 해결
- 기간 전 날짜가 휴장이면 baseDate ≤ targetDate 조건으로 직전 거래일 종가를 자동 보정
- 기준가가 없거나 0인 지수(신규 등록 등)는 결과에서 제외해 0으로 나누기 방지

#### 성과 랭킹
<img width="1596" height="710" alt="지수 성과" src="https://github.com/user-attachments/assets/c35ef420-f6c1-4846-81d7-3693e76153a5" />

- 전체 지수의 등락률을 내림차순 정렬해 순위 부여, 상위 N개(기본 10) 반환
- 정렬 기준을 금액이 아닌 등락률로 — 지수 규모 차이(KOSPI 35p = 1.2%, KOSDAQ 25p = 3.3%)로 인한 왜곡 방지
- 등락률 계산 로직(indexPerformance)을 관심 지수 성과와 공유해 한 곳에서만 관리
- 지수당 쿼리 2회(2N+1) 구조는 IN 조회 + groupingBy로 2회로 줄이는 개선안까지 설계, 발표 일정상 미적용 (알고 있는 한계로 기록)


</details>

<details>
<summary>🧑‍💻이수찬</summary>

## 📈 지수 정보 관리

### 지수 정보 등록

<img width="1585" height="661" alt="image" src="https://github.com/user-attachments/assets/89fde672-9d05-42a6-ac49-463536377559" />

* 사용자 직접 등록(`registerFromUser`) / Open API 자동 등록(`registerFromOpenApi`) 분리 구현
* 지수 분류명 + 지수명 조합 중복 등록 시 409 처리
* 등록과 동시에 자동 연동 설정(`AutoSyncConfig`)이 비활성 상태로 함께 생성

### 지수 정보 단건 조회

<img width="1808" height="473" alt="image" src="https://github.com/user-attachments/assets/33bae2d2-3a6f-41a4-8e6e-eb1d597b504a" />

* id로 지수 정보 조회, 존재하지 않으면 404 처리
* 응답 `200 OK`, `IndexInfoDto` 반환

### 지수 정보 수정 API

<img width="1817" height="552" alt="image" src="https://github.com/user-attachments/assets/c8780496-2515-484d-8d50-170be103d424" />

* 채용 종목 수, 기준 시점, 기준 지수, 즐겨찾기만 부분 수정 가능 (지수 분류명·지수명은 수정 불가)
* 값이 없는 필드는 기존 값 유지, 값이 있으면 생성 시와 동일한 검증(`@Positive` 등) 적용
* 존재하지 않는 id면 404 처리

### 지수 정보 삭제

<img width="1585" height="657" alt="image" src="https://github.com/user-attachments/assets/f96b8e80-2087-445d-a079-bc0848acedc7" />

* 삭제 시 `ON DELETE CASCADE`로 연관된 지수 데이터도 함께 삭제
* 존재하지 않는 id면 404 처리

### 지수 정보 목록 조회

* 지수 분류명·지수명 부분 일치, 즐겨찾기 완전 일치 필터 지원, 커서 기반 페이지네이션
* QueryDSL 동적 쿼리 — 지수 분류명/지수명/채용 종목 수 중 1개 정렬, 동점 그룹 안에서는 `id`로 보조 정렬
* 정렬 기준이 `id`가 아닐 때도 데이터 누락 없이 다음 페이지를 찾도록 `cursor`(정렬 기준값) + `idAfter`(보조 id) 조합으로 커서 조건 구성

</details>

---

## 📂 파일 구조

<details>
<summary>클릭해서 전체 구조 보기</summary>

```
src
├── main
│ ├── java
│ │ └── com.sprint.findex
│ │ ├── FindexApplication.java
│ │ │
│ │ ├── global
│ │ │ ├── entity
│ │ │ │ └── BaseEntity.java
│ │ │ ├── common
│ │ │ │ └── CursorPageResponse.java
│ │ │ ├── exception
│ │ │ │ ├── BusinessException.java
│ │ │ │ ├── ErrorResponse.java
│ │ │ │ ├── GlobalExceptionHandler.java
│ │ │ │ └── errorcode
│ │ │ │ ├── BaseErrorCode.java
│ │ │ │ ├── AutoSyncConfigErrorCode.java
│ │ │ │ ├── DashboardErrorCode.java
│ │ │ │ ├── IndexDataErrorCode.java
│ │ │ │ ├── IndexInfoErrorCode.java
│ │ │ │ └── SyncJobErrorCode.java
│ │ │ ├── type
│ │ │ │ ├── ChartPeriodType.java
│ │ │ │ ├── JobResult.java
│ │ │ │ ├── JobType.java
│ │ │ │ ├── PerformancePeriodType.java
│ │ │ │ └── SourceType.java
│ │ │ └── config
│ │ │ ├── QueryDSLConfig.java
│ │ │ └── SwaggerConfig.java
│ │ │
│ │ └── domain
│ │ ├── indexinfo
│ │ │ ├── controller
│ │ │ │ ├── IndexInfoApi.java
│ │ │ │ └── IndexInfoController.java
│ │ │ ├── dto
│ │ │ │ ├── request
│ │ │ │ │ ├── IndexInfoCreateRequest.java
│ │ │ │ │ ├── IndexInfoOpenApiRegisterRequest.java
│ │ │ │ │ ├── IndexInfoSearchRequest.java
│ │ │ │ │ └── IndexInfoUpdateRequest.java
│ │ │ │ └── response
│ │ │ │ └── IndexInfoDto.java
│ │ │ ├── entity
│ │ │ │ └── IndexInfo.java
│ │ │ ├── mapper
│ │ │ │ └── IndexInfoMapper.java
│ │ │ ├── repository
│ │ │ │ ├── IndexInfoRepository.java
│ │ │ │ ├── IndexInfoRepositoryCustom.java
│ │ │ │ └── impl
│ │ │ │ └── IndexInfoRepositoryImpl.java
│ │ │ └── service
│ │ │ ├── IndexInfoService.java
│ │ │ └── impl
│ │ │ └── IndexInfoServiceImpl.java
│ │ │
│ │ ├── indexdata
│ │ │ ├── controller
│ │ │ │ ├── IndexDataApi.java
│ │ │ │ └── IndexDataController.java
│ │ │ ├── dto
│ │ │ │ ├── request
│ │ │ │ │ ├── IndexDataCreateRequest.java
│ │ │ │ │ ├── IndexDataExportRequest.java
│ │ │ │ │ ├── IndexDataSearchRequest.java
│ │ │ │ │ └── IndexDataUpdateRequest.java
│ │ │ │ └── response
│ │ │ │ └── IndexDataDto.java
│ │ │ ├── entity
│ │ │ │ └── IndexData.java
│ │ │ ├── mapper
│ │ │ │ └── IndexDataMapper.java
│ │ │ ├── repository
│ │ │ │ ├── IndexDataRepository.java
│ │ │ │ ├── IndexDataRepositoryCustom.java
│ │ │ │ └── impl
│ │ │ │ └── IndexDataRepositoryImpl.java
│ │ │ └── service
│ │ │ ├── IndexDataService.java
│ │ │ └── impl
│ │ │ └── IndexDataServiceImpl.java
│ │ │
│ │ ├── syncjob
│ │ │ ├── controller
│ │ │ │ ├── SyncJobApi.java
│ │ │ │ └── SyncJobController.java
│ │ │ ├── dto
│ │ │ │ ├── request
│ │ │ │ │ ├── SyncJobCreateRequest.java
│ │ │ │ │ └── SyncJobSearchRequest.java
│ │ │ │ └── response
│ │ │ │ └── SyncJobDto.java
│ │ │ ├── entity
│ │ │ │ └── SyncJob.java
│ │ │ ├── mapper
│ │ │ │ └── SyncJobMapper.java
│ │ │ ├── repository
│ │ │ │ ├── SyncJobRepository.java
│ │ │ │ ├── SyncJobRepositoryCustom.java
│ │ │ │ └── impl
│ │ │ │ └── SyncJobRepositoryImpl.java
│ │ │ └── service
│ │ │ ├── SyncJobService.java
│ │ │ └── impl
│ │ │ ├── IndexDataWriter.java
│ │ │ └── SyncJobServiceImpl.java
│ │ │
│ │ ├── autosyncconfig
│ │ │ ├── controller
│ │ │ │ ├── AutoSyncConfigApi.java
│ │ │ │ └── AutoSyncConfigController.java
│ │ │ ├── dto
│ │ │ │ ├── request
│ │ │ │ │ ├── AutoSyncConfigSearchRequest.java
│ │ │ │ │ └── AutoSyncConfigUpdateRequest.java
│ │ │ │ └── response
│ │ │ │ └── AutoSyncConfigDto.java
│ │ │ ├── entity
│ │ │ │ └── AutoSyncConfig.java
│ │ │ ├── mapper
│ │ │ │ └── AutoSyncConfigMapper.java
│ │ │ ├── repository
│ │ │ │ ├── AutoSyncConfigRepository.java
│ │ │ │ ├── AutoSyncConfigRepositoryCustom.java
│ │ │ │ └── impl
│ │ │ │ └── AutoSyncConfigRepositoryImpl.java
│ │ │ ├── scheduler
│ │ │ │ └── AutoSyncScheduler.java
│ │ │ └── service
│ │ │ ├── AutoSyncConfigService.java
│ │ │ └── impl
│ │ │ ├── AutoSyncConfigInitializer.java
│ │ │ └── AutoSyncConfigServiceImpl.java
│ │ │
│ │ ├── dashboard
│ │ │ ├── controller
│ │ │ │ ├── DashboardApi.java
│ │ │ │ └── DashboardController.java
│ │ │ ├── dto
│ │ │ │ └── response
│ │ │ │ ├── ChartDataPoint.java
│ │ │ │ ├── IndexChartDto.java
│ │ │ │ ├── IndexInfoSummaryDto.java
│ │ │ │ ├── IndexPerformanceDto.java
│ │ │ │ └── RankedIndexPerformanceDto.java
│ │ │ ├── mapper
│ │ │ │ └── DashboardMapper.java
│ │ │ └── service
│ │ │ ├── DashboardService.java
│ │ │ └── impl
│ │ │ └── DashboardServiceImpl.java
│ │ │
│ │ └── openapi
│ │ ├── client
│ │ │ ├── OpenApiClient.java
│ │ │ └── impl
│ │ │ └── OpenApiClientImpl.java
│ │ ├── config
│ │ │ ├── OpenApiProperties.java
│ │ │ └── OpenApiRestClientConfig.java
│ │ ├── dto
│ │ │ ├── jackson
│ │ │ │ ├── FlexibleItemListDeserializer.java
│ │ │ │ ├── OpenApiBigDecimalDeserializer.java
│ │ │ │ ├── OpenApiIntegerDeserializer.java
│ │ │ │ ├── OpenApiLongDeserializer.java
│ │ │ │ ├── OpenApiNumberParser.java
│ │ │ │ ├── OpenApiYyyyMmDdDeserializer.java
│ │ │ │ └── StockMarketIndexItemsDeserializer.java
│ │ │ ├── request
│ │ │ │ └── StockMarketIndexQuery.java
│ │ │ └── response
│ │ │ ├── StockMarketIndexBody.java
│ │ │ ├── StockMarketIndexHeader.java
│ │ │ ├── StockMarketIndexItem.java
│ │ │ ├── StockMarketIndexItems.java
│ │ │ └── StockMarketIndexResponse.java
│ │ └── exception
│ │ ├── OpenApiClientException.java
│ │ └── OpenApiErrorKind.java
│ │
│ └── resources
│ ├── application.yml
│ ├── application-local.yml
│ └── application-prod.yml
│
└── test
└── resources
└── application.yml
compose.yaml
Dockerfile
build.gradle

```

</details>

---

## 🌐 구현 홈페이지

[<img width="1280" height="646" alt="findex_finish" src="https://github.com/user-attachments/assets/320396bf-459d-41c3-a43b-5e86741057f8" />](https://sb14-findex-team01-production.up.railway.app)

- 위 영상 클릭할 경우 구현 사이트로 이동됩니다.

---

## 📝 프로젝트 개인 개발 리포트

- [이승현](https://few-patch-6f7.notion.site/3dd65019b73680d8b285d5fdffe31b48)
- [김예준](https://few-patch-6f7.notion.site/dd465019b736838db1ab819bb609c022)
- [김승호](https://few-patch-6f7.notion.site/6d265019b73682cb9abf8177e79d308f)
- [강성준](https://few-patch-6f7.notion.site/3dd65019b73680158c0ff57a76a3ce83?pvs=74)
- [김양현](https://few-patch-6f7.notion.site/de065019b736825c9df20105a575d54d)
- [이수찬](https://few-patch-6f7.notion.site/3dd65019b7368013ba85d0e33e1bb9db)



