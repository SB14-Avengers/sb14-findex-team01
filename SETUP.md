## 팀원 시작 가이드 (처음 세팅하는 법)

### 1. 저장소 클론받기
```bash
git clone git@github.com:SB14-project01/sb14-findex-team01.git
```
기본 브랜치가 `develop`이라 클론받으면 자동으로 `develop` 브랜치로 내려받아져요.
혹시 다른 브랜치로 열려있다면:
```bash
git checkout develop
```

### 2. IntelliJ에서 열기
클론받은 폴더를 IntelliJ로 열면(`File > Open`) `build.gradle`을 자동으로 인식해서
Gradle sync가 시작돼요. 필요한 라이브러리들이 알아서 다운로드되니 잠깐 기다리면 끝!
(별도로 스프링 부트 zip 받아서 파일 옮기고 그럴 필요 없어요.)

### 3. 환경 변수 파일(.env) 만들기
`.env.example` 파일을 복사해서 `.env` 파일을 만들고, 아래 값들을 채워주세요.
```bash
cp .env.example .env
```
- `DB_PASSWORD`: DB 비밀번호
- `PUBLIC_API_SERVICE_KEY`: 공공데이터포털 API 키

값은 전부 **노션 팀 공지사항**에 올려뒀어요. (⚠️ 이 값들은 절대 코드에 직접 적거나 커밋하지 마세요!)

### 4. 로컬 DB 띄우기 (Postgres)
먼저 본인 컴퓨터에 **Docker Desktop이 설치되어 있고 켜져 있는지** 확인해주세요
(설치만 해두고 꺼져있으면 안 돼요! 트레이/메뉴바에 고래 아이콘이 떠있어야 정상).

Docker가 켜진 상태에서, IntelliJ 하단 터미널창에 아래 명령어를 입력하세요:
```bash
docker compose up
```
`compose.yaml`이 이미 레포에 포함되어 있어서 이 한 줄이면 로컬 DB가 바로 뜹니다.

### 5. 애플리케이션 실행하고 확인하기
⚠️ **4번(Docker DB 실행)이 먼저 되어있어야 합니다.** DB가 안 켜져 있으면 서버 실행이 실패하거나
actuator에서 상태가 DOWN으로 뜰 수 있어요.

IntelliJ에서 메인 애플리케이션 클래스(▶️ 실행 버튼)를 눌러 서버를 켜주세요.
정상적으로 실행됐다면 브라우저에서 아래 주소로 확인할 수 있어요:
- 서버 상태 확인: http://localhost:8080/actuator/health → `{"status":"UP"}` 이라고 뜨면 정상!
- Swagger(API 문서): http://localhost:8080/swagger
- H2 콘솔(DB 조회, 참고용): http://localhost:8080/h2-console

여기까지 잘 뜨면 세팅 완료! 바로 작업 시작하시면 됩니다 🙌

> 참고: 공공데이터포털 주가지수 API 엔드포인트는 비밀이 아니라서 `.env`가 아니라
> `src/main/resources/application.yml`의 `findex.openapi.uri`에 고정값으로 있습니다.
