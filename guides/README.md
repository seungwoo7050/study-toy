# docs/ – 레포 문서 구조

이 디렉터리는 이 레포에서 쓰이는 **학습용/개념 문서**를 모아둔 곳이다.  
코드 자체보다는, 프로젝트를 시작하기 전에 필요한 **기초 + 스택 개념 정리**가 목적이다.

현재 구조:

```text
docs/
├── README.md
├── foundations
│   ├── 00-programming-zero-step.md
│   └── 01-practical-appendix-auth-git-docker.md
└── stack-guides
    ├── mini
    │   ├── cpp-network-basics.md
    │   ├── node-express-ts-basics.md
    │   ├── react-spa-basics.md
    │   └── spring-rest-basics.md
    └── portfolio
        ├── cpp-realtime-pvp-server-basics.md
        ├── spring-enterprise-patterns-basics.md
        └── web-native-video-editor-basics.md
```

---

## 1. foundations/ – 공통 기초

프로그래밍 자체나, 개발 도구 개념이 불안한 사람을 위한 **0단계 문서들**이다.

### `00-programming-zero-step.md`

* 완전 0단계용.
* 내용:

  * 터미널/파일 시스템 기본 명령
  * Git 기초(클론, 브랜치/태그 개념 수준)
  * "프로그램이 뭔지" – 입력/처리/출력 구조
  * 변수 / 조건문 / 반복문 / 함수 / 배열 같은 언어 공통 개념
  * 에러 메시지 읽는 법, 검색/디버깅 최소 원칙

### `01-practical-appendix-auth-git-docker.md`

* 실무스러운 부록 개념.
* 내용:

  * 세션 vs 토큰, JWT 구조/플로우
  * Git 브랜치/태그를 튜토리얼 레포에서 어떻게 쓰는지
  * Docker + Postgres로 개발 DB 환경 구성하는 기본 패턴

**권장 사용**

* 완전 초보 → `00` → `01` 순서로 한 번 정독.
* 이미 프로그래밍/백엔드 경험이 확실하다 → 필요할 때만 찾아보는 참고용.

---

## 2. stack-guides/mini – 미니 프로젝트용 스택 가이드

"앞단계 미니 프로젝트 세트"를 위한 스택별 개념서다.
각 문서는 **해당 스택으로 첫 토이 프로젝트를 돌리기 전에 읽는 용도**다.

### `cpp-network-basics.md`

* 대상: C++로 TCP 서버를 처음 짜보는 사람.
* 내용:

  * C++ 기본 문법/빌드 구조 요약
  * 소켓/TCP 개념
  * 단일 클라이언트 에코 서버까지의 흐름

### `node-express-ts-basics.md`

* 대상: Node.js + Express + TypeScript + Postgres로 간단 백엔드를 만들 사람.
* 내용:

  * Node 런타임, 비동기 I/O 개념
  * Express 라우팅/미들웨어 구조
  * TypeScript 적용 포인트
  * DB 연동(간단 CRUD)까지의 기본 흐름

### `react-spa-basics.md`

* 대상: React로 SPA를 처음 만드는 사람.
* 내용:

  * 컴포넌트/props/state, 기본 훅
  * 라우팅 개념
  * fetch/axios를 이용한 API 연동
  * 간단한 목록/폼 UI 패턴

### `spring-rest-basics.md`

* 대상: Spring Boot로 REST API를 처음 만드는 사람.
* 내용:

  * 프로젝트 구조, 레이어드 아키텍처(Controller/Service/Repository)
  * REST 컨트롤러 작성
  * JPA/H2를 이용한 간단 CRUD
  * 기본 예외 처리 패턴

**전제**

* 이 네 문서 + 대응 미니 프로젝트들이 **이미 한 바퀴 돌아갔다**는 상태를
  `stack-guides/portfolio`에서 전제로 삼는다.

---

## 3. stack-guides/portfolio – 포트폴리오용 스택 가이드

여기부터는 **실제 포트폴리오로 쓸 만한 큰 프로젝트 3개**를 위한 개념서다.

각 문서는 "이 스택/도메인으로 한 단계 더 올라갈 때 필요한 전체 개념 정리"에 가깝고,
구체적인 설계/버전/코드는 각 프로젝트 레포의 `design/` 문서와 소스가 담당한다고 가정한다.

### `cpp-realtime-pvp-server-basics.md`

* 대상: C++로 실시간 PVP 게임 서버를 만들 사람.
* 내용:

  * 틱 기반 게임 루프, authoritative 서버 개념
  * TCP / UDP / WebSocket 선택 기준과 조합 패턴
  * 세션 / 매치메이킹 / 매치 상태 모델
  * Postgres + Redis 역할 분리(영속 vs 실시간 상태)
  * 여러 게임 서버 인스턴스(샤딩)와 로드밸런싱 개념
  * Prometheus/Grafana 등으로 기본 메트릭을 보는 이유

### `spring-enterprise-patterns-basics.md`

* 대상: Spring으로 "엔터프라이즈 서비스 패턴 모음집" 같은 프로젝트를 할 사람.
* 내용:

  * 레이어드 / 헥사고날 아키텍처 개념
  * 도메인 / 애플리케이션 / 인프라 계층 역할
  * 트랜잭션 경계, 쓰기/읽기 분리(CQRS 라이트 버전)
  * JWT 인증 / RBAC 인가 구조(Spring Security)
  * RDB + Redis + Elasticsearch + Kafka 조합의 역할 분리
  * WebFlux / 가상 스레드 패턴이 어디에 쓰일 수 있는지 개략

### `web-native-video-editor-basics.md`

* 대상: 웹 기반 비디오 에디터(React + Node + FFmpeg + 네이티브 모듈)를 만들 사람.
* 내용:

  * 전체 아키텍처(프론트/백엔드/스토리지/네이티브 모듈) 개요
  * 타임라인 / 트랙 / 클립 / 이펙트 도메인 모델
  * 업로드 / 프록시 / 썸네일 / 렌더 파이프라인
  * 편집 동작 → FFmpeg 필터/옵션으로 매핑하는 관점
  * 렌더 잡, 큐, 진행 상태(progress) 관리 개념

---

## 4. 추천 읽기 순서

레포 전체 흐름을 기준으로 한 **권장 순서**는 대략 이렇게 본다.

1. **기초 정리**

   * 프로그래밍/도구가 불안한 상태라면:
     `foundations/00-programming-zero-step.md`
     `foundations/01-practical-appendix-auth-git-docker.md`

2. **미니 프로젝트 전용 스택 기초**

   * C++ 네트워크: `stack-guides/mini/cpp-network-basics.md`
   * Spring REST: `stack-guides/mini/spring-rest-basics.md`
   * Node/Express/TS: `stack-guides/mini/node-express-ts-basics.md`
   * React SPA: `stack-guides/mini/react-spa-basics.md`
   * → 그리고 레포 루트의 로드맵에 따라 **미니 프로젝트들을 실제로 구현**

3. **포트폴리오용 프로젝트로 넘어갈 때**

   * 실시간 PVP 서버 쪽으로 갈 거면:
     `stack-guides/portfolio/cpp-realtime-pvp-server-basics.md`
   * Spring 엔터프라이즈 패턴 쪽이면:
     `stack-guides/portfolio/spring-enterprise-patterns-basics.md`
   * 웹 비디오 에디터/미디어 도메인 쪽이면:
     `stack-guides/portfolio/web-native-video-editor-basics.md`

이 README는 어디까지나 "지도" 역할만 한다.
각 파일 내용이 실제 기준이므로, 구조가 바뀌면 이 파일도 같이 맞춰서 업데이트해야 한다.