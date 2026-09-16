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

### Scheduling

- Spring Scheduler

### Collaboration

- Git / GitHub
- Jira
- Notion

### Deployment

- Railway

---

## ⚡ Anangers_1팀

[📎 팀 협업 문서](https://app.notion.com/p/Avengers-SB14-Findex-Team-01)

---

## 🛠️ 팀원별 구현 기능 상세

### 김예준

<img width="1301" height="619" alt="image" src="https://github.com/user-attachments/assets/85fee8dd-b2c0-4dc1-a0ad-7b9324697f8e" />

**🔁 연동 작업 관리**

- 지수 데이터 연동 실행 
- 지수 정보 연동 실행 
- 연동 작업 목록 조회 

### 이승현

<img width="1397" height="684" alt="image" src="https://github.com/user-attachments/assets/d900180c-e268-4e17-b35f-e38274186b37" />


**⚙️ 자동 연동 설정**
- 지수 등록 시 자동 연동 설정 자동 생성 
- 자동 연동 설정 수정 
- Scheduler 기본 설정
- 배치 실행 로직 구현
- 자동 연동 설정 목록 조회

### 김승호



**🔗 Open API 연동**
- WebClient 호출 구현 
- Open API 응답 구조 파악 및 DTO 매핑

### 강성준

<img width="1229" height="694" alt="image" src="https://github.com/user-attachments/assets/225aad23-9e35-4d69-ae78-f0bba0482f9a" />


**🗂️ 지수 데이터 관리**
- 지수 데이터 등록
- 지수 데이터 단건 조회
- 지수 데이터 수정 
- 지수 데이터 삭제
- 지수 데이터 CSV Export
- 지수 데이터 목록 조회 

### 김양현

<img width="1374" height="453" alt="image" src="https://github.com/user-attachments/assets/0dd1f174-85b2-4552-b383-db6ec49a3c11" />



**📊 대시보드**
- 지수 정보 요약 조회
- 즐겨찾기 지수 성과 요약
- 전일/전주/전월 대비 성과 랭킹
- 지수 시계열 차트 조회
- 이동평균선(MA5/MA20) 계산 로직 구현

### 이수찬

<img width="1414" height="514" alt="image" src="https://github.com/user-attachments/assets/f0cc029d-5368-4f5b-9938-aaecf25ad84d" />


**📈 지수 정보 관리**
- 지수 정보 등록 
- 지수 정보 수정
- 지수 정보 삭제 
- 지수 정보 목록 조회
- 지수 분류명 및 지수명 검색 
- 커서 기반 페이지네이션

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

## 🎬 프로젝트 회고록
[회고 문서 링크](https://app.notion.com/p/4L-Action-Plan)



