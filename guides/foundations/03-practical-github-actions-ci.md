# 부록 – GitHub Actions로 CI 최소 구성

> 목적: PR/푸시마다 자동으로 빌드/테스트(및 린트)를 돌려서 “내 PC에서는 되는데” 문제를 줄인다.
> 이 문서는 배포(CD)까지는 다루지 않고, 학습용 레포에서 바로 쓸 수 있는 CI 템플릿을 제공한다.

---

## 0. 이 문서로 어디까지 가는가

1. workflow 파일 위치와 기본 트리거 이해
2. Node/React/Spring 프로젝트를 CI에서 빌드/테스트
3. Postgres가 필요한 경우: GitHub Actions `services` 또는 `docker compose`로 DB 띄우기
4. 캐시로 실행 시간 줄이기

---

## 1. GitHub Actions 기본

* workflow 파일 위치: `.github/workflows/*.yml`
* 보통 트리거:

  * `pull_request`: PR 열릴 때/업데이트될 때
  * `push`: main에 push(또는 merge)될 때

---

## 2. (템플릿) 모노레포 CI 예시 – Node + React + Spring

아래는 “프로젝트가 서로 다른 폴더에 있다”는 가정의 예시다.
경로는 레포 구조에 맞게 바꿔서 사용한다.

예시 구조(가정):

```text
notes-api/             # Node/Express TS
my-react-app/          # Vite React
spring-notes-api/      # Spring Boot
```

### 2.1 `.github/workflows/ci.yml` (복붙 템플릿)

```yaml
name: CI

on:
  pull_request:
  push:
    branches: [ "main" ]

jobs:
  node-notes-api:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: notes-api
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Setup Node
        uses: actions/setup-node@v4
        with:
          node-version: "20"
          cache: "npm"
          cache-dependency-path: notes-api/package-lock.json

      - name: Install
        run: npm ci

      # 스크립트가 없을 수 있는 학습용 레포를 고려해 --if-present 사용
      - name: Lint
        run: npm run lint --if-present

      - name: Test
        run: npm run test --if-present

      - name: Build
        run: npm run build --if-present

  react-build:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: my-react-app
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Setup Node
        uses: actions/setup-node@v4
        with:
          node-version: "20"
          cache: "npm"
          cache-dependency-path: my-react-app/package-lock.json

      - name: Install
        run: npm ci

      - name: Build
        run: npm run build

  spring-tests:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: spring-notes-api
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: "17"
          cache: gradle

      - name: Test
        run: ./gradlew test
```

---

## 3. (선택) Postgres가 필요한 테스트가 있다면

DB가 필요한 통합 테스트가 있으면, CI에서도 DB를 띄워야 한다.
가장 간단한 방법은 GitHub Actions의 `services`를 쓰는 것이다.

### 3.1 job 안에서 services로 Postgres 띄우기 (예시)

```yaml
services:
  postgres:
    image: postgres:15
    env:
      POSTGRES_DB: toydb
      POSTGRES_USER: toyuser
      POSTGRES_PASSWORD: toypass
    ports:
      - 5432:5432
    options: >-
      --health-cmd="pg_isready -U toyuser -d toydb"
      --health-interval=10s
      --health-timeout=5s
      --health-retries=5
```

그리고 테스트 실행 단계에 환경 변수를 주입한다(예: Node/Spring 모두 동일 컨셉):

```yaml
env:
  DB_HOST: 127.0.0.1
  DB_PORT: "5432"
  DB_USER: toyuser
  DB_PASS: toypass
  DB_NAME: toydb
```

---

## 4. (선택) docker compose로 “기동만” 검증하는 스모크 테스트

레포에 `docker-compose.yml`(또는 compose 파일들)이 있다면,
CI에서 “컨테이너가 정상 기동되는지”를 빠르게 확인할 수도 있다.

```yaml
  compose-smoke:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - run: docker compose up -d --build
      - run: docker compose ps
      - run: docker compose down -v
```

---

## 5. 운영 팁(최소)

* CI가 “초록불”이면, 최소한 **빌드가 깨지지 않는다**는 신뢰를 확보한다.
* 테스트가 없다면, 최소한 “build”라도 돌려서 타입/컴파일 오류를 잡는다.
* 히스토리 재작성(rebase/reset/force push)은 **CI와 함께 쓰면** 사고가 줄어든다.
