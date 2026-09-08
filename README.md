# 📈 Findex

### 금융위원회 Open API 기반 한국 주가지수 분석 서비스

> 공공데이터 Open API를 활용하여 주가지수 데이터를 수집하고,
> 지수 정보 관리, 금융 지표 분석, 자동 데이터 연동을 제공하는
> 금융 분석 백엔드 서비스입니다.

---

## 👥 Contributors

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/Hanna-log">
        <img src="https://github.com/Hanna-log.png" width="120px;" alt="Hanna"/>
        <br />
        <sub><b>Hanna</b></sub>
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
        <sub><b>lsc0869</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/WinLike-dev">
        <img src="https://github.com/WinLike-dev.png" width="120px;" alt="WinLike"/>
        <br />
        <sub><b>WinLike</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/xian980">
        <img src="https://github.com/xian980.png" width="120px;" alt="SungJun"/>
        <br />
        <sub><b>SungJun</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/yyy2724">
        <img src="https://github.com/yyy2724.png" width="120px;" alt="yyy2724"/>
        <br />
        <sub><b>yyy2724</b></sub>
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

## 📌 Project Overview

Findex는 금융위원회에서 제공하는 주가지수 Open API를 활용하여
한국 주가지수 데이터를 관리하고 분석하는 백엔드 서비스입니다.

사용자는 지수 정보를 등록, 수정, 삭제할 수 있으며
지수별 시계열 데이터를 조회하고 CSV 파일로 Export할 수 있습니다.

또한 외부 Open API를 통한 데이터 연동과
Spring Scheduler를 활용한 자동 데이터 수집 기능을 제공합니다.

---

## ✨ Features

### 📊 지수 정보 관리

- 지수 정보 등록
- 지수 정보 수정
- 지수 정보 삭제
- 지수 정보 목록 조회
- 즐겨찾기 관리
- 지수 분류명 및 지수명 검색
- 커서 기반 페이지네이션

### 📈 지수 데이터 관리

- 지수 데이터 등록
- 지수 데이터 수정
- 지수 데이터 삭제
- 지수 데이터 목록 조회
- 날짜 범위 검색
- 커서 기반 페이지네이션
- CSV Export

### 🔗 Open API 연동

- 금융위원회 주가지수 Open API 연동
- 지수 정보 자동 등록 및 수정
- 지수 데이터 자동 수집
- 연동 작업 결과 기록

### ⚙️ 자동 연동

- 자동 연동 설정 관리
- Spring Scheduler 기반 자동 데이터 수집
- 최신 지수 데이터 자동 업데이트

### 📊 대시보드

- 주요 지수 현황 요약
- 지수 시계열 차트
- 5일 / 20일 이동평균선
- 전일 / 전주 / 전월 대비 성과 분석
- 지수 성과 랭킹

---

## 🚀 Getting Started

### Requirements

- Java 17+
- PostgreSQL
- Gradle

### Clone

```bash
git clone https://github.com/Hanna-log/sb14-findex-team01.git
cd sb14-findex-team01
```

### Run

```bash
./gradlew bootRun
```

### Environment Variables

프로젝트 실행을 위해 다음 환경변수를 설정해야 합니다.

```text
DB_URL=
DB_USERNAME=
DB_PASSWORD=
OPEN_API_KEY=
```

> ⚠️ 실제 API 인증키와 비밀번호는 README나 GitHub 저장소에 업로드하지 않습니다.

---

## 🏗 Architecture

### System Architecture

```text
[Frontend]
     ↓
[Spring Boot]
     ↓
[Controller]
     ↓
[Service]
     ↓
[Repository]
     ↓
[PostgreSQL]

[Spring Scheduler]
     ↓
[금융위원회 Open API]
     ↓
[지수 데이터 수집 및 저장]
```

### 주요 구성

| 구성 | 역할 |
|---|---|
| Controller | 클라이언트 요청 처리 |
| Service | 핵심 비즈니스 로직 처리 |
| Repository | 데이터베이스 접근 |
| Entity | 데이터베이스 테이블과 객체 매핑 |
| Open API Client | 금융위원회 Open API 연동 |
| Scheduler | 지수 데이터 자동 수집 |

---

## 💡 Technical Challenges

### Open API 연동

금융위원회 주가지수 Open API를 활용하여
외부 데이터를 수집하고 프로젝트의 데이터 구조에 맞게 변환합니다.

### 커서 기반 페이지네이션

대량의 지수 데이터를 효율적으로 조회하기 위해
커서 기반 페이지네이션을 적용합니다.

### 자동 데이터 연동

Spring Scheduler를 활용하여 일정한 주기마다
최신 지수 데이터를 자동으로 수집하고 저장합니다.

### 금융 지표 계산

지수 데이터를 기반으로 등락률과 이동평균선 등의
금융 지표를 계산하여 제공합니다.

---

## 🐛 Troubleshooting

프로젝트 진행 중 발생한 문제와 해결 과정을 기록합니다.

| 문제 | 원인 | 해결 |
|---|---|---|
| Open API 인증 오류 | 인증키 인코딩 및 요청 설정 문제 | API 요청 방식과 인증키 형식 확인 |
| 데이터 중복 저장 | 동일 데이터의 반복 연동 | 지수와 날짜를 기준으로 중복 방지 |
| 페이지네이션 문제 | 커서와 정렬 조건 불일치 | 정렬 기준과 ID를 함께 사용 |

---

## 📚 Documentation

| 문서 | 설명 |
|---|---|
| Notion | 프로젝트 회의록 및 프로젝트 문서 |
| Jira | 일정 및 이슈 관리 |
| Swagger | API 명세 |
| GitHub Wiki | 프로젝트 기술 문서 |

---

## 🤝 Collaboration

### Branch Strategy

- `main` : 최종 및 배포 코드
- `develop` : 개발 통합 브랜치
- `feature/*` : 기능 개발 브랜치

### Pull Request

- 기능 개발 후 Pull Request 생성
- 팀원 코드 리뷰 후 `develop`에 병합
- 최종 테스트 후 `main`에 병합

### Commit Convention

```text
feat: 새로운 기능 추가
fix: 버그 수정
refactor: 코드 리팩토링
docs: 문서 수정
test: 테스트 코드 추가 및 수정
chore: 기타 설정 및 작업
```

---

## 📄 License

This project is created for educational purposes
as part of the Codeit Bootcamp.
