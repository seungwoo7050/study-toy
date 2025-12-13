# Toy Project Suite

---

## 🏆 프로젝트별 주요 기능/학습 포인트

| 영역 | 주요 기술/개념 | 실습 예시 |
|------|----------------|-------------------------------|
| 백엔드 (Spring) | Spring Boot, JPA, JWT, REST, CI | 엔드포인트 구현, DB 연동, 인증/인가, 예외처리, 테스트, CI |
| 백엔드 (Node) | Express, TypeScript, JWT, REST | 동일한 Job API를 Node.js 스택으로 구현, 인메모리 저장, 테스트 |
| 프론트엔드 | React, Vite, 상태관리, E2E, API | 컴포넌트/상태, 폼, API 연동, E2E/단위테스트, 환경변수 |
| C++ | OOP, 네트워크, 멀티스레드 | 클래스 설계, TCP 서버/클라, 채팅, 빌드/실행 |


---

## 🧭 학습 절차 및 문서 읽는 순서

1. **환경 설정**: [`ENV_SETUP.md`](./ENV_SETUP.md)를 먼저 읽고 개발 환경을 준비하세요.
2. **전체 로드맵 파악**: [`ROOT_ROADMAP.md`](./ROOT_ROADMAP.md)에서 전체 학습 흐름과 각 프로젝트의 목표를 확인하세요.
3. **실습 진행**: 각 프로젝트별 튜토리얼을 단계별로 따라 하세요.
        - [백엔드 튜토리얼 (Spring)](./backend/mini-job-service/TUTORIAL_BE.md)
        - [백엔드 튜토리얼 (Node)](./backend/mini-job-service-node/TUTORIAL_BE_NODE.md)
        - [프론트엔드 튜토리얼](./frontend/mini-job-dashboard/TUTORIAL_FE.md)
        - [C++ 튜토리얼](./cpp/TUTORIAL_CPP.md)
4. **스크립트/유틸 참고**: 필요시 [`DOCS/SCRIPTS.md`](./DOCS/SCRIPTS.md)에서 빌드/테스트/정리 스크립트 사용법을 확인하세요.

---

작은 학습용 프로젝트 모음입니다. 백엔드(Java Spring Boot), 프론트엔드(React + Vite), C++ 예제들이 포함되어 있습니다.

## 목표
- 로컬에서 각 모듈을 빌드하고 실행하는 방법을 문서화
- Gradle wrapper 기반 빌드를 권장 (전역 Gradle 설치 불필요)
- 프론트엔드는 Vite 환경변수 `VITE_API_BASE_URL`을 통해 백엔드 엔드포인트를 설정할 수 있습니다. (예: `http://localhost:8080/api`)

## 디렉터리 구조
```
├── backend/                # 백엔드(Spring Boot + Node.js)
│   ├── mini-job-service/       # Job 관리 REST API (Spring)
│   └── mini-job-service-node/  # Job 관리 REST API (Express + TS)
│       ├── src/                # TypeScript 소스
│       ├── package.json        # npm 스크립트/의존성
│       └── ...
├── frontend/               # 프론트엔드(React + Vite)
│   └── mini-job-dashboard/ # Job 대시보드 UI
│       ├── src/            # React 컴포넌트, 서비스 등
│       ├── package.json    # npm 패키지 관리
│       └── ...
├── cpp/                    # C++ 예제
│   ├── battle-game/        # 콘솔 턴제 배틀 게임
│   ├── echo-server/        # TCP 에코 서버/클라이언트
│   └── multi-chat-server/  # 다중 채팅 서버(선택)
├── scripts/                # 빌드/테스트/유틸 스크립트
├── tools/                  # 환경 체크 등 보조 스크립트
├── DOCS/                   # 추가 문서
├── ENV_SETUP.md            # 개발 환경 설정 가이드
├── ROOT_ROADMAP.md         # 전체 학습 로드맵
├── build-all.sh            # 전체 빌드 스크립트
└── ...
```



## 🚀 빠른 시작 (Quick Start)

1. 환경 확인: `tools/check-env.sh` (또는 수동으로 `java -version`, `node -v`, `npm -v`, `git --version` 확인)
2. Git hooks 설정(수동): `git config core.hooksPath .husky` (자세한 내용은 `TUTORIAL_GIT_WORKFLOW.md` 참고)
3. 백엔드 빌드(Spring): `cd backend/mini-job-service && ./gradlew clean build`
4. 백엔드 빌드(Node): `cd backend/mini-job-service-node && npm install && npm test`
5. 프론트엔드 개발 서버: `cd frontend/mini-job-dashboard && npm ci && npm run dev`

### 전체 빌드 (루트에서)
```bash
./build-all.sh
```

### 빌드 산출물/로그 정리
```bash
# 루트에서 실행
./scripts/cleanup-build-results.sh
```

### 백엔드만 빌드/실행
```bash
cd backend/mini-job-service
./gradlew clean bootJar
java -jar build/libs/mini-job-service-0.0.1-SNAPSHOT.jar
```

### 프론트엔드 개발 서버/빌드
```bash
cd frontend/mini-job-dashboard
npm ci
# 개발 서버
npm run dev
# 프로덕션 빌드
npm run build
```

#### 프론트엔드 환경변수
- 로컬 개발/배포 환경에 따라 `VITE_API_BASE_URL`을 설정하세요.
- 예시: `frontend/mini-job-dashboard/.env` 파일 생성
  ```
  VITE_API_BASE_URL="http://localhost:8080/api"
  ```

#### CI
- `.github/workflows/ci.yml`에서 Gradle wrapper + Node 빌드를 수행합니다. CI는 `VITE_API_BASE_URL`을 설정하여 프론트엔드 빌드가 예상값을 사용하도록 합니다.

---

## 📚 학습 로드맵 & 실습 흐름

이 프로젝트는 단계별 실습과 커밋 경험을 통해 백엔드(Spring Boot), 프론트엔드(React), C++(네트워크/OOP)까지 폭넓게 학습할 수 있도록 설계되었습니다.

### 예시 실습 흐름 및 커밋 메시지

#### 백엔드 예시
1. HealthController 구현 →
	```bash
	GIT_AUTHOR_DATE="2025-01-02 18:00:00" GIT_COMMITTER_DATE="2025-01-02 18:00:00" git commit -m "feat: add health check endpoint"
	```
2. In-memory Job CRUD 구현 →
	```bash
	GIT_AUTHOR_DATE="2025-01-04 20:00:00" GIT_COMMITTER_DATE="2025-01-04 20:00:00" git commit -m "feat: implement in-memory Job CRUD"
	```
3. JPA/H2 연동 →
	```bash
	GIT_AUTHOR_DATE="2025-01-06 19:00:00" GIT_COMMITTER_DATE="2025-01-06 19:00:00" git commit -m "feat: integrate JPA and H2 database"
	```

#### 프론트엔드 예시
1. React 프로젝트 초기화 →
	```bash
	GIT_AUTHOR_DATE="2025-01-03 10:00:00" GIT_COMMITTER_DATE="2025-01-03 10:00:00" git commit -m "chore: initialize React project with Vite"
	```
2. Job 리스트 컴포넌트(dummy) →
	```bash
	GIT_AUTHOR_DATE="2025-01-05 19:00:00" GIT_COMMITTER_DATE="2025-01-05 19:00:00" git commit -m "feat: render job list with dummy data"
	```

#### C++ 예시
1. battle-game 프로젝트 생성 →
	```bash
	GIT_AUTHOR_DATE="2025-01-05 10:00:00" GIT_COMMITTER_DATE="2025-01-05 10:00:00" git commit -m "chore: create battle-game project and base classes"
	```
2. echo-server TCP 서버/클라 구현 →
	```bash
	GIT_AUTHOR_DATE="2025-01-08 10:00:00" GIT_COMMITTER_DATE="2025-01-08 10:00:00" git commit -m "chore: create echo-server project and implement TCP server/client"
	```

---

더 자세한 단계별 실습 흐름과 커밋 예시는 각 튜토리얼 문서([TUTORIAL_BE.md](./backend/mini-job-service/TUTORIAL_BE.md), [TUTORIAL_FE.md](./frontend/mini-job-dashboard/TUTORIAL_FE.md), [TUTORIAL_CPP.md](./cpp/TUTORIAL_CPP.md))에서 확인할 수 있습니다.


## PR, VERIFY & Tutorials
- PR 체크리스트 템플릿: `.github/PULL_REQUEST_TEMPLATE.md` (빌드/린트/테스트 체크)
- 수동 검증 체크리스트: `VERIFY.md`
- Git 훅 및 워크플로우 튜토리얼: `TUTORIAL_GIT_WORKFLOW.md`

## 참고 및 정책
- `node_modules/`, `dist/`, `build/` 등 빌드 산출물은 커밋하지 않습니다. `.gitignore` 참고
- reproducible build를 위해 `package-lock.json`(FE), `gradle-wrapper.jar`(BE)는 커밋 권장

## Scripts
레포 내 유틸 스크립트는 각 파일 헤더와 `DOCS/SCRIPTS.md`에 목적/사용법/주의사항이 정리되어 있습니다. 실행 전 반드시 확인하세요.


- 본 프로젝트는 학습 목적입니다. 프로덕션 수준의 보안/비밀 관리는 별도 환경변수/시크릿 매니저를 사용하세요. JWT 등은 로컬 개발에서만 테스트용 기본값을 사용합니다.

## [LEARN] 학습용 안내
- 본 프로젝트는 학습 목적입니다. 프로덕션 수준의 보안/비밀 관리는 별도 환경변수/시크릿 매니저를 사용하세요. JWT 등은 로컬 개발에서만 테스트용 기본값을 사용합니다.

---

## 🖼️ 스크린샷/데모

> (예시: 실제 실행 화면, 주요 UI, CLI 결과 등 캡처/GIF를 여기에 추가하세요)

---

## 🏷️ 버전/태그 안내

| 태그 | 주요 내용 |
|------|-----------------------------|
| BE-v0.1 ~ BE-v0.7 | 백엔드 단계별 실습 (헬스체크~비동기) |
| FE-F0.1 ~ FE-F0.5 | 프론트엔드 단계별 실습 (컴포넌트~폴링) |
| CPP-C0.1 ~ CPP-C0.3 | C++ 단계별 실습 (배틀~채팅) |

각 태그별 코드는 `git checkout <태그명>`으로 확인할 수 있습니다.

---

## 🤝 기여 가이드 & 문의

- 개선/오류 제보: GitHub Issue로 등록해 주세요.
- PR(풀리퀘스트): 실습/문서/코드 개선 등 자유롭게 환영합니다.
- 질문/토론: Discussions 또는 Issue 활용

---

## 🙋 학습자 FAQ

**Q. 이 프로젝트로 뭘 할 수 있나요?**
A. 백엔드, 프론트엔드, C++ 네트워크 등 실무에 가까운 전체 개발 흐름을 실습할 수 있습니다.

**Q. 포트폴리오로 써도 되나요?**
A. 네, 신입/주니어 포트폴리오로 충분히 활용할 수 있습니다. 본인만의 기능/문서/테스트를 추가하면 더 좋습니다.

**Q. 실습/문서가 너무 많아요. 어디서부터 시작하죠?**
A. README 상단 "학습 절차 및 문서 읽는 순서"를 참고해 단계별로 따라오면 됩니다.

**Q. 더 자주 묻는 질문/오류는?**
A. [COMMON_ISSUES.md](./DOCS/COMMON_ISSUES.md)와 FAQ/문제해결 섹션을 참고하세요.
- 본 프로젝트는 학습 목적입니다. 프로덕션 수준의 보안/비밀 관리는 별도 환경변수/시크릿 매니저를 사용하세요. JWT 등은 로컬 개발에서만 테스트용 기본값을 사용합니다.

---

## ❓ 자주 묻는 질문/문제해결 (FAQ & Troubleshooting)

### 1. git 커밋 시 'env: bash\r: No such file or directory' 오류
- **원인:** git hook 스크립트(.husky 등)에 윈도우 스타일(CRLF) 줄바꿈이 남아 있을 때 발생
- **해결:**
	```bash
	find .husky -type f -exec dos2unix {} \;
	# 또는
	find .husky -type f -exec sed -i '' 's/\r$//' {} \;
	```

### 2. npm/node/g++ 빌드 오류
- **원인:** node/npm 버전 불일치, 캐시 문제, 권한 문제 등
- **해결:**
	- node/npm 버전 확인: `node -v`, `npm -v`
	- 캐시 삭제: `npm cache clean --force`
	- 권한 문제: `sudo chown -R $(whoami) ~/.npm`

### 3. 포트 충돌 (address already in use)
- **해결:**
	```bash
	lsof -i :<포트번호>
	kill -9 <PID>
	```

더 많은 오류/해결법은 [`DOCS/COMMON_ISSUES.md`](./DOCS/COMMON_ISSUES.md)에서 확인하세요.
