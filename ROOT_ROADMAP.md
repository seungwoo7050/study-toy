# Toy Project Suite – 전체 로드맵

## 1. 전체 개요

이 리포지토리는 **학습용 토이 프로젝트 묶음**입니다.  
백엔드(Spring Boot), 프론트엔드(React), C++(언어/네트워크 기초)의 세 가지 영역을 단계별로 학습할 수 있도록 구성되어 있습니다.

### 포함된 프로젝트

| 영역 | 프로젝트 | 설명 |
|------|---------|------|
| 백엔드 | `mini-job-service` | Spring Boot 기반 Job 관리 REST API |
| 프론트엔드 | `mini-job-dashboard` | React 기반 Job 대시보드 UI |
| C++ | `battle-game` | 콘솔 턴제 배틀 게임 |
| C++ | `echo-server` | TCP 에코 서버/클라이언트 |
| C++ | `multi-chat-server` | 다중 클라이언트 채팅 서버 (선택) |

---

## 2. 추천 진행 순서

### 1단계: BE-v0.1 ~ BE-v0.3 (Spring + JPA 기본)

**학습 목표**: Spring Boot 앱 구조, REST API 설계, JPA를 이용한 데이터 영속화

- `BE-v0.1`: 헬스체크 엔드포인트 구현
- `BE-v0.2`: 메모리 기반 Job CRUD
- `BE-v0.3`: H2 + JPA 기반 Job 영속화

**이 단계를 끝내면**:
- Spring Boot 앱을 실행하고 REST API를 호출할 수 있다.
- 기본적인 CRUD 엔드포인트를 설계할 수 있다.
- JPA를 이용해 데이터를 DB에 저장할 수 있다.

### 2단계: FE-F0.1 ~ FE-F0.3 (React 컴포넌트/상태 기본)

**학습 목표**: React 프로젝트 구조, 컴포넌트 분리, 상태 관리

- `FE-F0.1`: 프로젝트 뼈대, 기본 화면
- `FE-F0.2`: dummy 데이터 Job 리스트 렌더링
- `FE-F0.3`: Job 생성 폼 (로컬 상태)

**이 단계를 끝내면**:
- React 개발 환경을 세팅하고 실행할 수 있다.
- 함수형 컴포넌트와 Props를 이용해 UI를 구성할 수 있다.
- useState를 이용해 로컬 상태를 관리할 수 있다.

### 3단계: BE-v0.4 ~ BE-v0.7 (Postgres, JWT, 비동기)

**학습 목표**: 실제 DB 연동, 인증/인가, 비동기 처리

- `BE-v0.4`: PostgreSQL + Flyway 도입
- `BE-v0.5`: 공통 예외 처리 + 검증 + 로깅
- `BE-v0.6`: User + JWT 인증/인가
- `BE-v0.7`: 비동기 Job 처리 시뮬레이션

**이 단계를 끝내면**:
- Docker로 PostgreSQL을 띄우고 앱과 연동할 수 있다.
- JWT를 이용한 인증 흐름을 이해한다.
- @Scheduled를 이용한 백그라운드 처리를 구현할 수 있다.

### 4단계: FE-F0.4 ~ FE-F0.5 (백엔드 연동 + 폴링)

**학습 목표**: REST API 호출, 실시간 데이터 갱신

- `FE-F0.4`: 백엔드 REST 연동 (GET/POST)
- `FE-F0.5`: Job 상태 폴링 (주기적 갱신)

**이 단계를 끝내면**:
- fetch/axios를 이용해 백엔드 API를 호출할 수 있다.
- 폴링을 통해 데이터를 주기적으로 갱신할 수 있다.

### 5단계: CPP-C0.1 ~ CPP-C0.3 (C++ 언어 + 네트워크)

**학습 목표**: C++ 클래스 설계, 소켓 프로그래밍 기초

- `CPP-C0.1`: battle-game (콘솔 턴제 배틀)
- `CPP-C0.2`: echo-server (단일 클라이언트 TCP 에코)
- `CPP-C0.3`: multi-chat-server (다중 클라이언트 채팅, 선택)

**이 단계를 끝내면**:
- C++ 클래스/헤더/소스 파일 분리를 할 수 있다.
- 소켓 API를 이용해 TCP 서버/클라이언트를 구현할 수 있다.
- 멀티스레드 서버 패턴을 이해한다.

---

## 3. Git 태그 체크아웃 방법

각 버전은 Git 태그로 관리됩니다. 특정 버전의 코드를 보려면:

```bash
# 백엔드 v0.3 버전으로 체크아웃
git checkout BE-v0.3

# 프론트엔드 F0.2 버전으로 체크아웃
git checkout FE-F0.2

# C++ battle-game 버전으로 체크아웃
git checkout CPP-C0.1
```

---

## 4. 서브 프로젝트 및 문서 링크

### 환경 설정

- [ENV_SETUP.md](./ENV_SETUP.md) – 개발 환경 설정 가이드

### 백엔드

- [backend/mini-job-service/README.md](./backend/mini-job-service/README.md)
- [backend/mini-job-service/TUTORIAL_BE.md](./backend/mini-job-service/TUTORIAL_BE.md)

### 프론트엔드

- [frontend/mini-job-dashboard/README.md](./frontend/mini-job-dashboard/README.md)
- [frontend/mini-job-dashboard/TUTORIAL_FE.md](./frontend/mini-job-dashboard/TUTORIAL_FE.md)

### C++

- [cpp/battle-game/README.md](./cpp/battle-game/README.md)
- [cpp/echo-server/README.md](./cpp/echo-server/README.md)
- [cpp/multi-chat-server/README.md](./cpp/multi-chat-server/README.md)
- [cpp/TUTORIAL_CPP.md](./cpp/TUTORIAL_CPP.md)

---

## 5. 기여 및 피드백

이 프로젝트는 학습 목적으로 만들어졌습니다.  
버그 리포트나 개선 제안은 Issue로 등록해 주세요.
