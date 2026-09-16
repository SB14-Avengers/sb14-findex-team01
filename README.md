# 📈 Findex

### 금융위원회 Open API 기반 한국 주가지수 분석 서비스

> 공공데이터 Open API를 활용하여 주가지수 데이터를 수집하고,
> 지수 정보 관리, 금융 지표 분석, 자동 데이터 연동을 제공하는
> 금융 분석 백엔드 서비스입니다.

프로젝트 기간 : 2026.09.08 ~ 2026.09.17

---

## 👥 Contributors

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/Hanna-log">
        <img src="https://github.com/Hanna-log.png" width="120px;" alt="Hanna"/>
        <br />
        <sub><b>이승현</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/kim-yejunn">
        <img src="https://github.com/kim-yejunn.png" width="120px;" alt="김예준"/>
        <br />
        <sub><b>김예준</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/lsc0869">
        <img src="https://github.com/lsc0869.png" width="120px;" alt="lsc0869"/>
        <br />
        <sub><b>이수찬</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/WinLike-dev">
        <img src="https://github.com/WinLike-dev.png" width="120px;" alt="WinLike"/>
        <br />
        <sub><b>김승호</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/xian980">
        <img src="https://github.com/xian980.png" width="120px;" alt="SungJun"/>
        <br />
        <sub><b>강성준</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/yyy2724">
        <img src="https://github.com/yyy2724.png" width="120px;" alt="yyy2724"/>
        <br />
        <sub><b>김양현</b></sub>
      </a>
    </td>
  </tr>
</table>

---

## 🛠 Tech Stack

### Backend

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)

### Database

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![H2](https://img.shields.io/badge/H2-Database-1E90FF?style=flat-square)

### Documentation

![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black)

### External API

- 금융위원회 지수시세정보 Open API
- Spring RestClient

### Scheduling

- Spring Scheduler

### Collaboration

- Git / GitHub
- Jira
- Notion

### Deployment

- Railway

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
<summary>🧑‍💻이승현 (팀장)</summary>

(내용 준비 중)

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

(내용 준비 중)

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

(내용 준비 중)

</details>

<details>
<summary>🧑‍💻이수찬</summary>

(내용 준비 중)

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



