# 부록 – 실무용 추가 기초

> JWT / 인증, Git 태그·브랜치, Docker + Postgres

이 문서는 필수는 아니고, 프로젝트 진행 중에 "이거 뭔지 대충만 알고 싶다" 할 때 참고용이다.

---

## 1. JWT / 인증 기초

### 1.1 세션 vs 토큰

**문제:**
사용자가 로그인했는지 어떻게 기억할 것인가?

#### 세션 기반

* 로그인 성공 → 서버가 **세션 ID**를 발급해서 **서버 메모리/Redis/DB** 등에 저장.
* 클라이언트에는 쿠키로 세션 ID만 들고 있게 함.
* 요청마다 쿠키 안의 세션 ID 보고, 서버에서 세션 데이터를 찾아서 유저 정보 확인.

특징:

* 서버가 상태(state)를 들고 있음.
* 서버 수가 많아지면 세션 저장소를 공유해야 함(예: Redis).

#### 토큰(JWT) 기반

* 로그인 성공 → 서버가 **JWT**라는 토큰에 유저 식별 정보, 만료 시간 등을 담고 **서명**해서 돌려줌.
* 서버는 별도 세션 저장 안 하고,
  이후 요청에서 온 JWT를 검증해서 바로 유저 정보를 꺼내 씀.

특징:

* 서버는 기본적으로 "무상태(stateless)"에 가까움.
* 서버 여러 대여도, 같은 비밀키/공개키만 공유하면 됨.

이 레포의 백엔드는 보통 **JWT 기반 인증**을 가정한다고 보면 된다.

---

### 1.2 JWT 구조

JWT는 점(`.`)으로 구분된 세 부분으로 이루어진 문자열이다:

```text
xxxxx.yyyyy.zzzzz
```

* **Header (헤더)** – 어떤 알고리즘으로 서명했는지
* **Payload (페이로드)** – 실제 데이터(클레임, claim)
* **Signature (서명)** – 위 둘을 비밀키로 서명한 값

헤더, 페이로드는 **Base64URL 인코딩된 JSON**이고,
서명은 서버만 아는 비밀키로 만들어진다.

페이로드에 자주 들어가는 것들:

* `sub`: 사용자 ID (subject)
* `exp`: 만료 시각(Unix timestamp)
* `iat`: 발급 시각
* 그 외 권한/역할(role) 정보 등

주의:

* **비밀번호 같은 민감 정보는 넣지 않는다.**
* 토큰은 클라이언트도 내용을 볼 수 있다(서명만 위조 불가).

---

### 1.3 기본 동작 플로우

로그인 기준 플로우:

1. 클라이언트가 아이디/비밀번호로 `/login` 요청.
2. 서버:

   * ID/PW 검증
   * 성공하면 JWT 생성 (userId, exp 등 넣고 서명)
3. 서버가 JWT를 응답으로 돌려줌.

   * 보통:

     * `Authorization: Bearer <JWT>` 헤더로 주고,
     * 클라이언트는 이걸 저장(로컬스토리지/세션스토리지/메모리/HttpOnly 쿠키 등)한다.
4. 이후 모든 API 요청에서:

   * 클라이언트 → `Authorization: Bearer <JWT>` 헤더에 토큰 첨부
   * 서버 → 토큰 서명 검증, 만료(`exp`) 확인
   * 유효하면 JWT 안의 `sub` 같은 값을 꺼내서 "이 요청은 어떤 유저"인지 판단

로그아웃:

* JWT 자체에는 "로그아웃" 개념이 없음.
* 일반적인 패턴:

  * **짧은 만료 시간** + 필요하면 **블랙리스트(서버에 차단 목록 저장)**.

---

### 1.4 실무에서 최소한 신경 써야 할 것

프로젝트 진행하면서 나올 수 있는 포인트들만 정리:

1. **HTTPS 필수**

   * JWT를 헤더로 보내도 결국 네트워크를 타기 때문에, 평문 HTTP면 그대로 털린다.
   * 로컬 개발 외에는 무조건 HTTPS.

2. **저장 위치**

   * 브라우저 기준:

     * `localStorage`/`sessionStorage` → XSS에 취약.
     * HttpOnly 쿠키 → JS에서 못 읽으니 XSS에 강하지만, CSRF를 추가로 신경 써야 함.
   * 이 레포는 보통 "JWT가 있다는 전제"만 쓰고, 저장 전략은 크게 다루지 않을 수 있음.

3. **만료 설계**

   * Access Token은 짧게, Refresh Token은 길게 가져가는 패턴이 많다.
   * 토이 프로젝트 단계에서는 Access Token만 써도 무방.

4. **서버 쪽 검증 로직**

   * 필수: 서명 검증, 만료시간 확인
   * 추가: 토큰이 블랙리스트에 있는지(강제 로그아웃, 계정 정지 등)

이 정도 알고 있으면 "왜 여기서 JWT를 검증하는지", "왜 만료 시간이 필요한지"는 이해 가능하다.

---

## 2. Git 태그 / 브랜치 활용

튜토리얼 레포에서 버전별 체크아웃 할 때 자주 볼 내용.

### 2.1 브랜치 vs 태그

**브랜치(branch)**

* "진행 중인 줄기"
* 계속 커밋을 추가해 나가는 가변적인 선
* 예: `main`, `develop`, `feature/login`

**태그(tag)**

* 특정 커밋에 붙이는 "라벨"
* 보통 배포 버전, 튜토리얼 단계 표시용
* 예: `BE-v0.1`, `FE-F0.3`, `CPP-C0.2`
* 일반적으로 한 번 붙이면 잘 안 바꾼다고 가정

튜토리얼 레포에서:

* **각 단계별 완성 코드**를 태그로 달아두고,
* 학습자는 태그를 체크아웃해서 코드 상태를 재현한다.

---

### 2.2 태그/브랜치 확인 명령

레포 루트에서:

```bash
# 브랜치 목록
git branch

# 로컬 + 리모트 브랜치까지 보고 싶으면
git branch -a

# 태그 목록
git tag

# 특정 태그가 어디를 가리키는지
git show BE-v0.3

# 커밋 로그 간단히 보기
git log --oneline --decorate --graph --all
```

여기서 `--decorate` 옵션은 브랜치/태그가 어디에 붙어 있는지 같이 보여줘서 유용하다.

---

### 2.3 태그/브랜치로 이동

```bash
# 브랜치로 이동
git switch 브랜치이름
# 예: git switch BE-main

# 예전 스타일
git checkout 브랜치이름

# 태그로 이동 (detached HEAD)
git checkout BE-v0.3
# 또는
git checkout tags/BE-v0.3   # 환경에 따라 필요할 수 있음
```

태그를 직접 체크아웃하면 **detached HEAD** 상태가 되는데,
그 상태에서 커밋을 찍어도 브랜치 이름이 없으니 조금 헷갈릴 수 있다.

그래서 보통은:

```bash
# 태그 기준으로 개인 작업용 브랜치를 새로 판다
git switch -c my-playground-BE-v0.3 BE-v0.3
```

* `BE-v0.3` 태그가 가리키는 커밋을 기준으로
* `my-playground-BE-v0.3`라는 브랜치를 만든 뒤,
* 거기서 자유롭게 수정/커밋.

---

### 2.4 튜토리얼 레포에서의 추천 사용 패턴

1. 단계별 태그를 확인한다.

   ```bash
   git tag
   # BE-v0.1, BE-v0.2, BE-v0.3 ...
   ```
2. 보고 싶은 단계 태그를 기준으로 개인 브랜치를 판다.

   ```bash
   git switch -c my-BE-v0.2 BE-v0.2
   ```
3. 코드 읽고, 실행해보고, 마음대로 수정해본다.
4. 다른 단계로 넘어갈 때도 같은 패턴 반복.

이렇게 하면:

* 태그는 "깨끗한 기준점"으로 그대로 남고,
* 본인 실습은 본인 브랜치에만 남기게 된다.

---

## 3. Docker + Postgres 최소 가이드

백엔드 튜토리얼에서 Postgres를 쓸 때, 설치 대신 **Docker 컨테이너**로 띄우는 경우를 가정한다.

### 3.1 Docker 개념 1줄 요약

* **이미지(image)**: 프로그램 + 실행 환경이 들어 있는 템플릿
* **컨테이너(container)**: 그 이미지를 실제로 실행한 "프로세스 인스턴스"

우리는 보통:

* `postgres` 이미지로 컨테이너 하나 띄워서
* 앱(Sprint/Node)이 그 DB에 접속하도록 쓴다.

---

### 3.2 docker-compose.yml 예시

프로젝트 루트에 `docker-compose.yml` 하나 두고, 대략 이런 식으로 쓴다고 보자:

```yaml
version: "3.9"

services:
  db:
    image: postgres:15
    container_name: toy_postgres
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: toydb
      POSTGRES_USER: toyuser
      POSTGRES_PASSWORD: toypass
    volumes:
      - toy-postgres-data:/var/lib/postgresql/data

volumes:
  toy-postgres-data:
```

의미:

* `image: postgres:15`

  * Postgres 공식 이미지 버전 15 사용.
* `ports: "5432:5432"`

  * 로컬 5432 포트 → 컨테이너 5432 포트로 연결.
* `environment`:

  * 컨테이너 내부에서 Postgres가 사용할 기본 DB/유저/비밀번호.
* `volumes`:

  * DB 데이터를 로컬 볼륨에 저장해서, 컨테이너를 지워도 데이터는 남도록 한다.

---

### 3.3 기본 명령어

`docker-compose.yml`이 있는 디렉터리 기준.

```bash
# 백그라운드로 컨테이너 실행
docker compose up -d

# 로그 보기
docker compose logs -f

# 실행 중인 컨테이너 목록
docker ps

# DB 컨테이너에 직접 들어가기 (쉘)
docker exec -it toy_postgres bash

# psql로 접속 예시 (컨테이너 내부에서)
psql -U toyuser -d toydb

# 컨테이너 중지 + 삭제
docker compose down

# 볼륨까지 완전 삭제 (DB 데이터 날아감)
docker compose down -v
```

프로젝트에서 요구하지 않는 이상,
학습 단계에서는 `up -d`, `logs`, `down` 정도만 많이 쓰게 된다.

---

### 3.4 애플리케이션에서 접속 정보

Spring / Node 등에서 DB 접속 설정할 때 보통 이렇게 맞춘다:

* 호스트(host): `localhost` (같은 머신에서 접근할 때)
  또는 Docker 네트워크에서 서비스 이름 `db` (컨테이너끼리 통신)
* 포트(port): `5432`
* DB 이름: `toydb`
* 사용자: `toyuser`
* 비밀번호: `toypass`

예시 (Spring application.yml):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/toydb
    username: toyuser
    password: toypass
```

예시 (Node.js – TypeORM/Prisma 등):

```ts
host: 'localhost',
port: 5432,
database: 'toydb',
user: 'toyuser',
password: 'toypass',
```

핵심은:

* `docker-compose.yml`의 env와
* 애플리케이션 설정의 DB 접속 정보가 서로 맞아야 한다는 것.

---

## 4. 문서 활용 요약

* **JWT / 인증**
  → 백엔드 튜토리얼에서 로그인, 인증/인가, `Authorization: Bearer` 헤더가 나올 때 이 문단 참고.

* **Git 태그/브랜치**
  → 레포를 버전별로 돌아다니면서 코드 상태를 재현할 때 이 문단 참고.
  → 특히 태그 기준으로 개인 브랜치 파는 패턴.

* **Docker + Postgres**
  → "Docker 띄우고 Postgres 접속하라"는 말이 나올 때,
  `docker compose up`과 DB 접속 정보를 맞추는 용도로 이 문단 참고.

이 부록은 모르면 프로젝트가 "막히는" 부분이라기보다는,
알면 디버깅/환경 세팅이 훨씬 덜 스트레스 받는 영역들만 모아 둔 것이다.