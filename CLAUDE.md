# Findex 백엔드 프로젝트

이 파일은 Claude Code가 이 레포에서 작업할 때 참고하는 가이드입니다.
전체 기능/API 명세/ERD 등 상세 요구사항은 코드잇이 제공한 노션 안내 문서(미션 안내 페이지)를 참고한다.

---

## 프로젝트 개요

**Findex**는 금융위원회(공공데이터포털) Open API(주가 지수)와 연동하는 금융 지수 관리 백엔드 API입니다.
지수 정보/지수 데이터를 CRUD하고, 외부 API 자동 연동, 이동평균선(MA5/MA20) 계산, 성과 분석 등을
제공하며, 코드잇에서 제공하는 고정된 프론트엔드와 API 명세(api-docs 레퍼런스 서버) 기준으로 연동합니다.

- 난이도: 초급 (스프린트 백엔드 트랙 팀 프로젝트)
- 팀: Findex 14기 team01 (SB14)
- 프론트엔드는 코드잇 제공 — 빌드 산출물이 `src/main/resources/static/`에 포함되어 있어 백엔드가 함께 서빙한다 (`http://localhost:8080/`)

## 필수 명령어 (Commands)

### 개발 환경 설정
```bash
# 1. Docker Desktop 실행 (필수 — 꺼져있으면 앱 실행 시 DockerNotRunningException 발생)

# 2. 환경 변수 설정
cp .env.example .env
# .env 파일 열어서 값 입력 (노션 팀 공지사항 공유 값)
#   DB_PASSWORD             : 로컬 Postgres 비밀번호
#   PUBLIC_API_SERVICE_KEY  : 공공데이터포털 Open API 서비스 키(디코딩 키), 절대 커밋 금지

# 3. 로컬 DB 기동 (또는 앱 실행 시 자동)
docker compose up        # compose.yaml 기반 Postgres 15 컨테이너

# 4. 앱 실행 (IntelliJ Run 또는 아래 명령어)
./gradlew bootRun
# spring-boot-docker-compose 의존성으로 compose.yaml이 자동 감지되어 Postgres 컨테이너가 같이 뜸
# 스키마는 spring.jpa.hibernate.ddl-auto: update 로 Hibernate가 엔티티 기준 자동 생성/반영 (수동 DDL 없음)
# 프로파일: 미지정 시 local (application-local.yml). 배포는 SPRING_PROFILES_ACTIVE=prod
```

### 빌드 / 테스트
```bash
./gradlew build          # 빌드 (스타일 위반해도 실패하지 않음, spotless enforceCheck = false)
                         #   build 시 installGitHooks / installGitMessageTemplate / installIntellijCodeStyle
                         #   태스크가 함께 실행됨 (단, 실제로는 build 안 돌려도 됨 — 아래 참고)
./gradlew bootRun        # 앱 실행 (http://localhost:8080)
./gradlew test           # 테스트 실행 (H2, ddl-auto: create-drop) + JaCoCo 리포트 자동 생성
./gradlew spotlessApply  # 코드 스타일 수동 정리 (커밋 시 pre-commit hook이 자동 실행해줌)
```

### 문서 / 모니터링
- Swagger UI: `http://localhost:8080/swagger-ui.html` (springdoc 기본 경로, 별도 path 설정 없음)
- API 문서 JSON: `http://localhost:8080/v3/api-docs`
- 서버 상태 확인: `http://localhost:8080/actuator/health`
- H2 콘솔: `http://localhost:8080/h2-console` — 로컬 프로파일에서 enabled (연결된 Postgres 조회용, 참고)
  - 테스트 프로파일은 `jdbc:h2:mem:testdb` 인메모리 사용

### 중요 참고사항
- **환경 변수**: `.env`에 `DB_PASSWORD`, `PUBLIC_API_SERVICE_KEY` (`.env.example` 참고, `.gitignore`에 이미 제외됨). `application.yml`은 `optional:file:.env[.properties]`로 이 파일을 읽는다
- **Open API 엔드포인트**: 비밀이 아니라서 `.env`가 아니라 `application.yml`의 `findex.openapi.uri` 고정값으로 둔다
- **로컬 DB**: 기본은 Docker Compose로 뜨는 Postgres 15. H2는 테스트 프로파일(`src/test/resources/application.yml`)과 로컬 콘솔용으로만 사용
- **커밋**: pre-commit hook이 커밋 직전 `spotlessApply` 자동 실행, commit-msg hook이 메시지 형식 검사 (아래 Git Workflow 참고)
- **코드 스타일 수동 설정 0단계**: 깃 훅뿐 아니라 인텔리제이 코드 스타일(`Project`, 4칸 들여쓰기 커스텀 스킴)도 프로젝트를 열기만 하면 자동 설정됨 (아래 Git Workflow의 "자동 설치" 참고)

## 코드 아키텍처 (Architecture)

### 핵심 기술 스택
- **Java 17** (Gradle toolchain)
- **Spring Boot 3.5.16** (Spring Framework 6.x) — `build.gradle` 플러그인 `org.springframework.boot` 3.5.16 기준.
  표준 스타터 사용: `spring-boot-starter-web`, `spring-boot-starter-validation`, `spring-boot-starter-data-jpa`,
  `spring-boot-starter-aop`, `spring-boot-starter-actuator`
- **Spring WebFlux** (`spring-boot-starter-webflux`) — Open API 연동용 `WebClient` 사용 목적으로 추가. `domain/openapi/client/`에 `OpenApiClient` 자리는 잡아놨지만 실제 `WebClient` 호출 로직은 아직 미구현
- **Gradle** (Groovy DSL) + `io.spring.dependency-management` 1.1.7 (BOM 기반 버전 관리)
- **Spring Data JPA + PostgreSQL 15** (Docker Compose로 로컬 실행)
- **H2** — 테스트/로컬 콘솔 전용 (`runtimeOnly` 스코프)
- **springdoc-openapi 2.8.17** (`springdoc-openapi-starter-webmvc-ui`) — Swagger 문서 자동 생성. `SwaggerConfig`에서 `OpenAPI` 빈으로 제목/설명/버전만 지정
- **MapStruct 1.6.3** — Entity ↔ DTO 변환 자동화 (`lombok-mapstruct-binding` 0.2.0으로 Lombok과 공존)
- **Lombok** — 컴파일 시점 보일러플레이트 코드 생성
- **Spring Boot Actuator** — `/actuator/health` 등 서버 상태 모니터링
- **QueryDSL (OpenFeign fork) 6.10.1** — `global/config/QueryDSLConfig.java`에서 `JPAQueryFactory` 빈 등록 완료.
  실제 동적 쿼리(필터/정렬) 적용은 아직 미착수 — 도메인 담당자가 목록 조회 구현 시 사용
- **Spring Scheduler** — `FindexApplication`에 `@EnableScheduling` 이미 적용됨(별도 `SchedulingConfig` 클래스 없이 메인 클래스에 직접). 실제 `@Scheduled` 배치 로직은 아직 미구현 — 자동 연동 설정(auto_sync_config) 담당자가 구현 시 추가
- **Spotless 8.6.0 + google-java-format** (`.aosp()` 옵션 — 들여쓰기 4칸, 기본 Google 스타일은 2칸) — 코드 스타일 자동 정리
- **JaCoCo 0.8.12** — 테스트 실행 후 커버리지 리포트(html/xml) 자동 생성
- **spring-boot-devtools** (`developmentOnly`) — 코드 변경 시 자동 재시작
- **spring-boot-docker-compose** (`developmentOnly`) — 로컬 실행 시 `compose.yaml` 자동 인식/기동
- 배포: Railway.io (`Dockerfile` — eclipse-temurin 17, 멀티스테이지 빌드)

### 패키지 구조
```
src/main/java/com/sprint/findex/
├── FindexApplication.java        # 엔트리 포인트, @SpringBootApplication + @EnableJpaAuditing
├── global/                       # 도메인 무관 공통 코드
│   ├── entity/
│   │   └── BaseEntity.java           # @MappedSuperclass, Long PK(@GeneratedValue IDENTITY) + createdAt/updatedAt(Instant) + AuditingEntityListener
│   ├── common/
│   │   └── CursorPageResponse.java   # 커서 기반 페이지네이션 응답 record
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java # @RestControllerAdvice, 전역 예외 처리
│   │   ├── ErrorResponse.java          # of(...) 오버로드로 여러 예외 소스 처리
│   │   ├── BusinessException.java      # BaseErrorCode를 담아 던지는 공통 런타임 예외
│   │   └── errorcode/
│   │       ├── BaseErrorCode.java              # HttpStatus/code/message 인터페이스
│   │       ├── IndexInfoErrorCode.java
│   │       ├── IndexDataErrorCode.java
│   │       ├── SyncJobErrorCode.java
│   │       ├── AutoSyncConfigErrorCode.java
│   │       └── DashboardErrorCode.java
│   ├── type/
│   │   ├── SourceType.java            # 데이터 입력 경로 (USER / OPEN_API)
│   │   ├── JobType.java               # 연동 작업 대상 유형 (INDEX_INFO / INDEX_DATA)
│   │   ├── JobResult.java             # 연동 작업 결과 (SUCCESS / FAILED)
│   │   ├── ChartPeriodType.java       # 차트 집계 단위 (MONTHLY / QUARTERLY / YEARLY)
│   │   └── PerformancePeriodType.java # 성과 분석 비교 기간 (DAILY / WEEKLY / MONTHLY)
│   └── config/
│       ├── SwaggerConfig.java         # OpenAPI 빈 (제목/설명/버전)
│       └── QueryDSLConfig.java        # JPAQueryFactory 빈 등록
├── domain/                       # 도메인별 수직 분리
│   ├── indexinfo/     # 지수 정보 관리
│   │   ├── entity/ controller/ service/(impl/) repository/(impl/) mapper/
│   │   └── dto/request/ (Create, Update)  dto/response/ (IndexInfoDto)
│   ├── indexdata/     # 지수 데이터 관리
│   │   └── dto/request/ (Create, Update)  dto/response/ (IndexDataDto)
│   ├── syncjob/       # 연동 작업 이력
│   │   └── dto/request/ (Create)  dto/response/ (SyncJobDto)     ← Update 없음
│   ├── autosyncconfig/# 자동 연동 설정
│   │   └── dto/request/ (Update)  dto/response/ (AutoSyncConfigDto)  ← Create 없음(지수 등록 시 초기화)
│   ├── dashboard/     # 대시보드 (entity/repository 없이 다른 도메인 조합 조회)
│   │   └── controller/ service/(impl/)  dto/response/
│   │       (ChartDataPoint, IndexInfoSummaryDto, IndexChartDto, IndexPerformanceDto, RankedIndexPerformanceDto)
│   └── openapi/       # Open API 연동 (entity/controller/repository 없음 — 자체 DB/REST 없이 연동작업이 소비하는 내부 클라이언트)
│       └── client/(impl/)  # OpenApiClient — 연동작업(syncjob) 담당자와 인터페이스 계약 합의 후 구현
```

**패키지 규칙**: 도메인별로 폴더를 나누고(`domain/{도메인명}`), 그 안에서 `entity/controller/service/repository/mapper`를 다시 하위 패키지로 나눈다. Service/Repository는 인터페이스(`service/`, `repository/`)와 구현체(`service/impl/`, `repository/impl/`)를 분리한다. 여러 도메인이 공통으로 쓰는 코드만 `global/`에 둔다.

**현재 상태**:
- 채워진 것: `global/` 인프라(BaseEntity, CursorPageResponse, BusinessException/GlobalExceptionHandler + 도메인별 ErrorCode, type enum, SwaggerConfig)와 도메인별 요청/응답 DTO(record, api-docs 레퍼런스 스펙 기준 필드 정렬)
- 빈 스텁: 각 도메인의 Entity/Controller/Service(+impl)/Repository(+impl)/Mapper는 `public class Xxx {}` / `public interface Xxx {}` 형태의 빈 껍데기. Repository도 아직 `JpaRepository`를 상속하지 않음 — 팀원 배정 후 본격 구현 예정
- 없는 것(과거 스캐폴딩에서 삭제됨, 필요 시 재도입): `global/aop/TimeTraceAspect`, `global/config/openapi/OpenApiConfig`
  - `SchedulingConfig` 클래스는 없지만 `@EnableScheduling`은 `FindexApplication`에 이미 적용돼있음 — 위 "핵심 기술 스택" Spring Scheduler 항목 참고
  - `global/config/QueryDSLConfig`(`JPAQueryFactory` 빈)는 있음 — 위 "핵심 기술 스택" QueryDSL 항목 참고
  - `OpenApiClient`는 `external/` 대신 `domain/openapi/client/`(+`impl/`)에 빈 스텁으로 있음 — 6명/6도메인 매칭을 위해 연동작업(syncjob)에서 도메인으로 분리(9/8 팀 회의)

### 엔티티 설계 패턴
- 모든 엔티티는 `BaseEntity`(Long PK + `createdAt`/`updatedAt`, 타입 `Instant`)를 상속 — 원래 `BaseUpdatableEntity`로 분리돼있었으나, api-docs 응답 어디에도 생성/수정일시 필드가 없어서 구분 실익이 없다고 판단해 하나로 통합
- 기본키는 `@GeneratedValue(strategy = GenerationType.IDENTITY)`(자동증가 BIGINT) `Long` — api-docs(코드잇 제공 레퍼런스 서버)와 동일하게 Long 유지
- Enum은 `@Enumerated(EnumType.STRING)`으로 저장
- Auditing은 `FindexApplication`의 `@EnableJpaAuditing` + `BaseEntity`의 `@EntityListeners(AuditingEntityListener.class)`로 동작

### API 응답 패턴
```java
// 성공 응답: 감싸지 않고 DTO(또는 DTO 배열/CursorPageResponse)를 그대로 최상위에 반환
return indexInfoMapper.toDto(savedIndexInfo);

// 실패 응답: GlobalExceptionHandler가 ErrorResponse로 통일해서 반환
ErrorResponse.of(errorCode, message)   // BusinessException 처리 시
```
- **`ApiResponse<T>`(status/data 래퍼) 안 씀 — 삭제됨.** 원래 "성공 응답은 ApiResponse로 감싸서 반환"이 규칙이었으나, api-docs 명세서 원문(`findexclaude.md` swagger 전체)을 다시 확인해보니 이런 래퍼가 어디에도 없고 성공 응답은 전부 DTO를 그대로(배열이면 배열째로) 최상위에 반환하는 구조였음. 코드잇 제공 프론트엔드가 이 명세서 기준으로 파싱하므로, 래퍼를 씌우면 연동이 깨짐 — 발견 시점에 실제로 이 클래스를 쓰는 컨트롤러가 하나도 없어서(아직 아무도 구현 전) `ApiResponse.java` 삭제하고 컨벤션만 바로잡음
- 에러 응답(`ErrorResponse`)은 명세서에도 실제로 그 구조(`timestamp`/`status`/`message`/`details`)로 있어서 그대로 유지 — 영향 없음
- 커스텀 예외는 `BusinessException` 하나만 두고, 도메인별 `BaseErrorCode` 구현 enum(`global/exception/errorcode/{Domain}ErrorCode.java`)으로 상태코드/코드/메시지를 정의한다.
- 예: `throw new BusinessException(IndexInfoErrorCode.NOT_FOUND)` — `GlobalExceptionHandler`가 `BusinessException`을 잡아서 해당 errorCode 기준 `ErrorResponse`로 변환한다.
- 새 도메인 예외가 필요하면 새 클래스를 만들 필요 없이, 그 도메인의 `{Domain}ErrorCode` enum에 상수만 추가하면 된다.
- `MethodArgumentNotValidException`(`@Valid` 검증 실패), `ConstraintViolationException`(`@RequestParam` 등 검증 실패), `MethodArgumentTypeMismatchException`/`HttpMessageNotReadableException`(타입 불일치·본문 파싱 실패 → 400), 그 외 `Exception`(500)도 `GlobalExceptionHandler`가 각각 잡아서 `ErrorResponse`로 내려준다.
  - **주의**: `ConstraintViolationException`(즉 `@RequestParam`/`@PathVariable`에 붙인 `@Min`, `@NotBlank` 등)은 **컨트롤러 클래스에 `@Validated`가 있어야** 발생한다. 파라미터 검증을 쓰는 컨트롤러엔 `@Validated`를 꼭 붙일 것 (안 붙이면 검증이 조용히 무시됨).

### 커서 기반 페이지네이션
```java
record CursorPageResponse<T>(
    List<T> content,       // 실제 데이터 목록
    String nextCursor,     // 다음 페이지 조회용 커서 값 (마지막 항목의 정렬 기준값)
    Long nextIdAfter,      // 커서 값이 같을 때 대비한 보조 키 (마지막 항목 ID)
    int size,              // 페이지 크기
    long totalElements,    // 전체 개수
    boolean hasNext)       // 다음 페이지 존재 여부
```
목록 조회 API는 이 패턴을 따른다.

### DTO / Mapper 패턴
- 요청/응답 DTO는 `record`로 정의, `domain/{도메인}/dto/request`, `dto/response`에 위치
- 검증은 `jakarta.validation` 어노테이션 사용 (`@NotBlank`, `@NotNull`, `@Positive` 등). 필드 required 여부는 api-docs 레퍼런스 서버 기준
- Entity ↔ DTO 변환은 MapStruct `@Mapper(componentModel = "spring")` 인터페이스로 자동화 (`domain/{도메인}/mapper/`)
- Controller/Service에서 Entity를 직접 반환하지 않고 반드시 응답용 DTO로 변환해서 내보낸다 (클래스명은 "Response"가 아니라 api-docs 기준 — 예: `IndexInfoDto`, `ChartDataPoint`. 아래 "API 스펙 기준" 참고)

## 도메인 & 핵심 기능

### 지수 정보 관리 (index_info)
- 속성: 지수 분류명, 지수명, 채용 종목 수, 기준 시점, 기준 지수, 소스 타입(USER/OPEN_API), 즐겨찾기
- `{지수 분류명}+{지수명}` 조합 유니크
- 등록 시 자동 연동 설정(`auto_sync_config`)도 같이 초기화 (그래서 auto_sync_config에는 Create DTO가 없음)
- 삭제 시 관련 지수 데이터도 함께 삭제 (`ON DELETE CASCADE`)
- 목록 조회: 지수 분류명/지수명(부분 일치), 즐겨찾기(완전 일치) 필터 + 정렬/커서 페이지네이션

### 지수 데이터 관리 (index_data)
- 속성: 지수(FK), 날짜, 소스 타입, 시가/종가/고가/저가, 대비, 등락률, 거래량, 거래대금, 상장 시가총액
- `{지수}+{날짜}` 조합 유니크, 삭제는 index_info와 무관하게 개별 가능
- CSV Export 지원 (목록 조회와 동일한 필터/정렬, 페이지네이션 없음). api-docs 응답이 `format: binary`라 서버 디스크에 저장하지 않고 바로 스트리밍 응답

### 연동 작업 관리 (sync_jobs)
- 유형(INDEX_INFO/INDEX_DATA), 대상 지수, 대상 날짜, 작업자(요청 IP 또는 system), 작업일시, 결과(SUCCESS/FAILED)
- Open API가 지수 정보 API를 직접 제공하지 않으므로 지수 데이터 API를 응용해서 구현

### Open API 연동 (domain/openapi)
- 자체 DB 테이블/REST 엔드포인트 없음 — 연동 작업(syncjob)이 소비하는 내부 클라이언트(`OpenApiClient`)만 존재
- 6명/6도메인 매칭을 위해 연동작업에서 별도 도메인으로 분리(9/8 팀 회의) — 그래도 연동작업과 결과물을 주고받아야 하므로, 구현 시작 전 두 담당자가 인터페이스(메서드 시그니처/반환 DTO)부터 합의할 것

### 자동 연동 설정 관리 (auto_sync_config)
- 지수별 자동 연동 활성화 여부 (`AutoSyncConfigUpdateRequest`는 `enabled` 하나)
- Spring Scheduler 기반 배치로 지수 데이터 자동 연동 (스케줄러 인프라는 아직 미설정 — 구현 시 추가)
- index_info 삭제 시 함께 삭제 (CASCADE)

### 대시보드
- 주요 지수 현황 요약, 지수 차트(일/월/분기/년 + MA5/MA20), 지수 성과 분석 랭킹
- Repository 없이 다른 도메인의 서비스/데이터를 조합해서 조회
- api-docs 기준 일부 응답(예: `IndexInfoSummaryDto`)은 `/api/index-infos/...` 경로에 있음 — `dto/response` 주석 참고

## DB 스키마
수동 DDL(`schema.sql`) 없이 `spring.jpa.hibernate.ddl-auto: update`로 Hibernate가 엔티티 어노테이션 기준으로 테이블/컬럼을 자동 생성·반영한다.
테이블: `index_info`, `index_data`, `sync_jobs`, `auto_sync_config`
- `Long`(자동증가 BIGINT) 기본키, 생성/수정일시는 `Instant`(→ `TIMESTAMP`/`TIMESTAMPTZ`)
- enum류 값 검증은 `@Enumerated(EnumType.STRING)` + 애플리케이션 레벨 검증에 의존 (DB `CHECK` 제약 없음)
- `compose.yaml`의 Postgres 서비스에 볼륨(`findex-postgres-data`)이 지정돼 있어 컨테이너 재생성해도 데이터 유지

## 프로파일 / 설정 파일
- `application.yml` — 공통 설정. `profiles.default: local`, `config.import: optional:file:.env`, `jpa.open-in-view: false`, `ddl-auto: update`, `hibernate.naming.physical-strategy: CamelCaseToUnderscoresNamingStrategy`(Spring Boot 기본값과 기능 중복이지만 팀원 요청으로 명시적으로 병기), Jackson 타임존 `Asia/Seoul`, `findex.openapi.uri`
- `application-local.yml` — 로컬(기본). Postgres 접속 정보, `show-sql: true`, Hibernate SQL 로깅, H2 콘솔 enabled,
  `ddl-auto: create`로 공통 설정(`update`)을 로컬에서만 오버라이드(엔티티 필드 바뀔 때마다 로컬 DB를 새로 만들어주는 용도 — prod는 안 건드림, 여전히 `update`)
- `application-prod.yml` — 배포(Railway). `DB_URL`/`DB_USERNAME`/`DB_PASSWORD` 환경변수, 쿼리 로그 off
- `src/test/resources/application.yml` — 테스트. H2 인메모리, `ddl-auto: create-drop`, `PUBLIC_API_SERVICE_KEY` 더미 주입

## 심화 요구사항 대응 현황
- **H2 로컬 개발환경 활용**: 테스트 프로파일 + 로컬 콘솔에 구성 완료. `application-local.yml`/`application-prod.yml` 프로파일 분리됨
- **쿼리 고도화**: QueryDSL 의존성 + `QueryDSLConfig`(`JPAQueryFactory` 빈) 설정 완료. 실제 동적 쿼리 적용은 아직 미착수 — 복잡한 목록 필터/정렬에 적용 예정

## 중요한 개발 규칙 (Development Guidelines)

1. **응답 형식**: 성공은 DTO(또는 DTO 배열/`CursorPageResponse`)를 그대로 반환(래퍼 없음), 실패는 `GlobalExceptionHandler`를 거친 `ErrorResponse`로 통일
2. **Entity 직접 노출 금지**: Controller/Service는 항상 DTO(record)로 변환해서 응답
3. **예외 처리**: 새 예외 클래스를 만들지 말고, 해당 도메인의 `{Domain}ErrorCode` enum에 상수 추가 후 `throw new BusinessException(errorCode)`로 던진다 (`global/exception/errorcode/`)
4. **페이지네이션**: 목록 조회는 커서 기반(`CursorPageResponse`) 패턴 준수
5. **매핑**: Entity ↔ DTO 변환은 직접 작성하지 않고 MapStruct Mapper 인터페이스 사용
6. **API 스펙 기준**: 요청/응답 DTO는 필드명·타입뿐 아니라 **클래스명도** 코드잇 제공 api-docs 레퍼런스 서버에 있는 스키마 이름 그대로 맞춘다 (임의 변경 금지). "Response는 쓰지 말고 항상 Dto로"처럼 고정된 접미사 규칙이 아니라, **그 스키마가 명세서에 뭐라고 적혀있는지 하나하나 따라가는 것** — 그 결과가 이렇게 갈림:
   - 도메인별 응답 DTO 9종(`IndexInfoDto`, `IndexDataDto`, `SyncJobDto`, `AutoSyncConfigDto`, `IndexInfoSummaryDto`, `ChartDataPoint`, `IndexChartDto`, `IndexPerformanceDto`, `RankedIndexPerformanceDto`) → 명세서 이름 그대로 `Dto`(또는 `ChartDataPoint`처럼 접미사 없음)
   - `ErrorResponse` → 명세서에도 실제로 `Response`라고 돼있어서 그대로 유지 (안 바꿈)
   - `CursorPageResponse<T>` → 얘도 `Response` 유지. 이건 우리 제네릭 클래스 이름 자체가 springdoc에 의해 `CursorPageResponseIndexInfoDto`처럼 자동으로 스키마 이름에 반영되는 거라, 이미 명세서(`CursorPageResponseXxxDto`류)랑 자동으로 맞음

## 주요 파일 경로 (Critical Files)
```
src/main/java/com/sprint/findex/FindexApplication.java                 # 엔트리 포인트 (@EnableJpaAuditing)
src/main/java/com/sprint/findex/global/entity/BaseEntity.java          # 엔티티 공통 베이스 (Long PK + createdAt/updatedAt)
src/main/java/com/sprint/findex/global/common/CursorPageResponse.java  # 커서 페이지네이션 응답
src/main/java/com/sprint/findex/global/exception/BusinessException.java
src/main/java/com/sprint/findex/global/exception/GlobalExceptionHandler.java
src/main/java/com/sprint/findex/global/exception/errorcode/BaseErrorCode.java
src/main/java/com/sprint/findex/global/type/                           # SourceType, JobType, JobResult, ChartPeriodType, PerformancePeriodType
src/main/java/com/sprint/findex/global/config/SwaggerConfig.java
src/main/java/com/sprint/findex/global/config/QueryDSLConfig.java     # JPAQueryFactory 빈
src/main/resources/application.yml            # 공통 설정, ddl-auto: update
src/main/resources/application-local.yml      # 로컬 개발 프로파일 (기본)
src/main/resources/application-prod.yml       # 배포(Railway) 프로파일
src/test/resources/application.yml            # 테스트(H2) 설정
src/main/resources/static/                    # 코드잇 제공 프론트엔드 빌드 산출물
compose.yaml                                  # 로컬 Postgres 15 컨테이너 정의
Dockerfile                                    # Railway 배포용 멀티스테이지 빌드
build.gradle
config/intellij/codeStyles/codeStyleConfig.xml # 인텔리제이 코드 스타일 자동 동기화용 원본
.github/dependabot.yml                        # 의존성 자동 업데이트 설정
.github/ISSUE_TEMPLATE/                       # 이슈 템플릿 9종 (Git Workflow 참고)
```

## Git Workflow
- **브랜치 전략**: 작업 브랜치 → `develop` → `main`
  - `.github/workflows/validate-pr-target.yml` — `main`으로 가는 PR은 `develop`에서만 허용 (그 외 소스 브랜치면 CI 실패)
- **커밋 메시지 컨벤션** (`.githooks/commit-msg`가 검사, 형식 안 맞으면 커밋 자체가 막힘 — 강제. Conventional Commits/Angular 표준 타입 기준):
  - 형식: `^(feat|fix|refactor|docs|chore): .+` , 제목 70자 이하 (소문자 — 매번 Shift 눌러 대문자로 바꾸는 번거로움을 없애기 위함)
  ```
  feat: 지수 정보 생성 API 추가            # 새로운 기능/파일 추가
  fix: 지수 데이터 중복 저장 오류 수정     # 코드 수정
  refactor: 동기화 서비스 로직 분리        # 코드 리팩토링 (파일/폴더명 변경 포함)
  docs: README 실행 방법 추가             # 문서 수정
  chore: build.gradle 의존성 정리         # 코드 로직과 무관한 설정/빌드/도구 변경
  ```
  - `.gitmessage.txt` 커밋 템플릿이 `installGitMessageTemplate` 태스크로 자동 설정됨 (템플릿 본문 예시 문구와 hook 허용 타입이 살짝 다르니 hook 기준을 따른다)
  - **관련 이슈 표기(권장, 강제 아님)**: 관련 깃이슈가 있으면 제목 끝에 `(#이슈번호)`를 붙인다 (70자 제한에 포함). 예: `feat: 지수 정보 등록 API 구현 (#13)`. GitHub이 커밋↔이슈를 자동으로 링크해준다 (자동으로 닫히진 않음 — 이슈를 닫으려면 PR 설명에 `Closes #13`을 쓴다). 이슈랑 무관한 사소한 커밋(오타 수정 등)은 생략 가능 — hook이 강제하는 건 타입/형식/글자수뿐, 이슈번호 표기는 강제하지 않는다.
- **GitHub 이슈 템플릿**: `.github/ISSUE_TEMPLATE/` — 9종 (파일명 이모지 접두사 유지)
  ```
  ✨-feature.md   # 신규 기능/개선           title: [feature]  label: feature
  🛠️-fix.md       # 원인 아는 간단 수정       title: [fix]      label: fix
  🔨-refactor.md  # 리팩토링                 title: [refactor] label: refactor
  📝-chore.md     # 빌드/설정/의존성 등 기타  title: [chore]    label: chore
  📚-docs.md      # 문서 작성/수정           title: [docs]     label: docs
  🧪-test.md      # 테스트 코드              title: [test]     label: test
  🚀-deploy.md    # 배포 작업                title: [chore]    label: deploy
  🗳️-adr.md       # 설계 결정 기록 (ADR)     title: [docs]     label: adr
  bug_report.yml  # 원인 불명 버그(구조화 폼) title: [bug]      label: bug
  ```
  - `fix.md`(원인 아는 수정)도 재현 방법/기대·실제 결과/에러 로그 섹션이 있어서 `bug_report.yml`(원인 불명 버그, 구조화 폼)이랑 구조가 비슷해 보일 수 있음 — 둘의 차이는 "원인을 이미 아는지"이지 양식 상세도가 아님
  - 각 템플릿 frontmatter의 `labels:`가 실제 리포지토리 라벨명과 정확히 일치해야 "New Issue" 선택 시 라벨이 자동으로 붙는다 (안 맞으면 조용히 라벨만 안 붙고 에러는 안 남 — 라벨 추가/변경 시 템플릿도 같이 확인할 것)
  - **default 브랜치(`develop`)에 있어야 GitHub "New Issue" 템플릿 선택 화면에 뜬다** (feature 브랜치에만 있으면 안 보임)
- **GitHub 라벨**: 영문 소문자로 통일(팀원 이해를 돕기 위해 설명은 한글) — `feature`/`fix`/`refactor`/`chore`/`docs`/`test`/`deploy`/`adr`/`bug`/`urgent`/`common`/`review`/`meeting` 13종. 위 이슈 템플릿들의 `labels:` 필드와 1:1 대응
- **GitHub 마일스톤**: "초급 프로젝트 마감일 및 발표일" (마감 2026-09-17) — 초기 기능 분해 이슈(#12~#37, 29개, 도메인별 세분화 + Feature 타입 지정)가 전부 이 마일스톤에 연결되어 있음. 담당자는 9/8 팀 회의 후 배정 예정(현재 미배정)
- **PR 템플릿**: `.github/pull_request_template.md`
- **CI**: `.github/workflows/ci.yml` — `main`/`develop` 대상 PR에서 `./gradlew build` 실행
- **Dependabot**: `.github/dependabot.yml` — Gradle 의존성 + GitHub Actions 버전을 매주 자동 스캔해서 업데이트 PR 자동 생성하는 기능. **현재 `open-pull-requests-limit: 0`으로 임시 중단**(메이저 버전 업그레이드 제안이 프로젝트 마감 전엔 위험해서). 마감 후 숫자를 5로 되돌리면 재개됨
- **자동 설치 (수동 설정 0단계)**: `build.gradle`이 읽히는 시점(=인텔리제이가 프로젝트를 열 때 자동으로 하는 Gradle sync 포함, `./gradlew build`뿐 아니라 `./gradlew help` 같은 아무 명령에도 적용됨)에 아래가 전부 자동 설치됨 — 팀원은 클론 후 프로젝트만 열면 되고 버튼을 따로 누를 필요 없음
  - `scripts/pre-commit`(→ `spotlessApply`) + `.githooks/commit-msg`(메시지 형식 검사)를 `.git/hooks/`에 복사 (`installGitHooks` 태스크)
  - `git config commit.template .gitmessage.txt` (`installGitMessageTemplate` 태스크)
  - `config/intellij/codeStyles/`(디렉토리 전체 — `codeStyleConfig.xml` + `Project.xml`)를 `.idea/codeStyles/`에 복사 (`installIntellijCodeStyle` 태스크) — `.idea/`는 전체 gitignore 대상이라 `config/`에 원본을 따로 두고 복사하는 방식. `codeStyleConfig.xml`은 인텔리제이 내장 스킴이 아니라 같은 폴더의 `Project.xml`(커스텀 4칸 들여쓰기 규칙)을 가리키도록 설정돼있음 — Spotless(`googleJavaFormat().aosp()`)랑 들여쓰기 칸수를 맞춘 거라 코딩 중 실시간 표시가 커밋 시 자동 포맷 결과랑 크게 어긋나지 않음
  - 수동으로 다시 설치하고 싶으면 `./gradlew installGitHooks` / `installGitMessageTemplate` / `installIntellijCodeStyle` 각각 재실행 가능
- **AI 리뷰**: `.coderabbit.yaml` — CodeRabbit 자동 리뷰(한국어, assertive 프로파일). `.junie/` — JetBrains Junie 설정

## 테스트
- 현재 `FindexApplicationTests.java`(컨텍스트 로드 확인)만 존재
- 도메인별 단위/통합 테스트는 앞으로 추가 예정
- 테스트는 H2 인메모리(`ddl-auto: create-drop`) 프로파일로 실행됨. `./gradlew test` 후 JaCoCo 리포트(`build/reports/jacoco`) 자동 생성

## 배포
- Railway.io — `Dockerfile`(eclipse-temurin 17, `./gradlew clean build -x test` 후 jre-alpine 이미지에 jar 실행)
- 배포 프로파일 `prod` — `DB_URL`/`DB_USERNAME`/`DB_PASSWORD`/`PUBLIC_API_SERVICE_KEY` 환경변수 필요
- GitHub Actions CI가 `main`/`develop` 대상 PR에서 빌드/테스트 실행
