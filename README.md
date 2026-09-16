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


</details>

<details>
<summary>🧑‍💻김예준</summary>

(내용 준비 중)

</details>

<details>
<summary>🧑‍💻김승호</summary>

(내용 준비 중)

</details>

<details>
<summary>🧑‍💻강성준</summary>

(내용 준비 중)

</details>

<details>
<summary>🧑‍💻김양현</summary>

(내용 준비 중)

</details>

<details>
<summary>🧑‍💻이수찬</summary>

(내용 준비 중)

</details>

<details>
<summary>🧑‍💻이승현 </summary>

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

[개발 홈페이지 가기](https://sb14-findex-team01-production.up.railway.app)

---

## 📝 프로젝트 개인 개발 리포트

- [이승현](https://few-patch-6f7.notion.site/3dd65019b736803086d5d61a246a6441)
- [김예준](https://few-patch-6f7.notion.site/dd465019b736838db1ab819bb609c022)
- [김승호](https://few-patch-6f7.notion.site/6d265019b73682cb9abf8177e79d308f)
- [강성준](https://few-patch-6f7.notion.site/3dd65019b7368019aa22f2698fe9816a)
- [김양현](https://few-patch-6f7.notion.site/de065019b736825c9df20105a575d54d)
- [이수찬](https://app.notion.com/p/3dd65019b7368013ba85d0e33e1bb9db?source=copy_link)




