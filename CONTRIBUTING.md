# Contributing Guide

이 저장소에 기여할 때 개발자들이 알아야 할 기본 규칙과 워크플로우를 정리합니다.

## 기본 원칙
- 이 저장소는 학습용 토이 프로젝트입니다. 프로덕션 수준의 보안/비밀 관리는 별도 고려가 필요합니다.
- CLI가 생성하는 파일(예: `node_modules`, `dist`, 컴파일 결과물 등)은 저장소에 커밋하지 마세요.
 - CLI가 생성하는 파일(예: `node_modules`, `dist`, 컴파일 결과물 등)은 저장소에 커밋하지 마세요.
 - PR/push 전에 로컬에서 `npm ci` 또는 `./gradlew clean build`를 통해 빌드가 깨지지 않는지 확인하세요.
- `package-lock.json`과 `gradle-wrapper.jar`는 의존성/빌드를 고정하기 위해 커밋하는 것을 권장합니다. 팀 정책에 따라 다를 수 있으니 프로젝트 내 합의된 방식을 따르세요.

## 커밋 전 점검
- 새로운 의존성을 추가했다면 `package-lock.json`/`yarn.lock`을 함께 커밋하세요(정책에 따름).
- `node_modules`, `dist`, `build` 디렉터리, 컴파일된 바이너리(`*.class`, C++ /bin 등)는 커밋 금지입니다. `.gitignore`를 확인하세요.
- 코드 스타일/린트 규칙을 준수하세요. (FE: `npm run lint`, BE: `./gradlew check`)
 - 코드 스타일/린트 규칙을 준수하세요. (FE: `npm run lint`, BE: `./gradlew check`)
 - PR을 만들기 전 `git status`와 `git diff --staged`로 의도치 않은 파일(예: `node_modules`, 바이너리, `*.class`)이 포함되어 있지 않은지 확인하세요.

## 빌드/실행/개발용 명령어
- 프론트엔드:
  - `cd frontend/mini-job-dashboard`
  - `npm ci` (권장, lockfile 기반 재현 가능한 설치)
  - `npm run dev` (개발 서버)
  - `npm run build` (프로덕션 번들 생성)

- 백엔드:
  - `cd backend/mini-job-service`
  - `./gradlew clean build` (Gradle wrapper 사용 권장)
  - `./gradlew bootRun` (로컬 실행)

- C++ 예제:
  - 각 프로젝트의 `build.sh` 스크립트를 사용하세요.

## 커밋 규칙
- 문서/튜토리얼을 업데이트할 때는 다음을 포함하세요:
  - 수정 목적(버그 수정/문서 보강 등)
  - 변경된 CLI 명령 또는 새로 권장하는 워크플로우

## CI 및 PR 규칙
- PR을 생성하기 전에 로컬에서 `./build-all.sh`를 실행하거나, 최소한 서브모듈의 빌드/테스트를 실행하세요.
 - PR을 생성하기 전에 로컬에서 `./build-all.sh`를 실행하거나, 최소한 서브모듈의 빌드/테스트를 실행하세요.
 - 모든 Pull Request는 CI를 통과해야 합니다. PR 템플릿에 체크리스트를 넣어 `build`, `lint`, `test`를 확인하도록 유도하세요.

### pre-commit 훅
- 이 리포지토리는 불필요한 빌드/CLI 산출물이 커밋되는 것을 방지하기 위해 root-level git pre-commit hook을 제공합니다. 로컬 개발환경에서 Git 훅이 사용되도록 하려면 다음을 확인하세요:
  - 루트에서 실행: `git config core.hooksPath .husky` (일회성 설정, 다른 개발자에게 공유 필요)
  - 프론트엔드 디렉터리에서는 `husky`와 `lint-staged`가 설정되어 있습니다. `npm ci` 후 첫 설치 시 `prepare` 스크립트가 훅을 설정합니다.
- pre-commit 훅은 기본적으로 다음을 수행합니다:
  - `scripts/check-staged-for-artifacts.sh` 를 실행하여 `node_modules/`, `dist/`, `build/`, `*.class`, `*.jar` 등의 신규 스테이징 파일을 차단
  - 프론트엔드의 경우, `lint-staged`를 통해 staged `*.{ts,tsx}` 파일들에 대해 eslint 자동수정을 실행

### 훅 건너뛰기 (주의)
- 어떠한 이유로 훅을 무시해야 하는 경우(예: 대량 리포지토리 리팩토리 작업 등)에는 `git commit --no-verify` 옵션을 사용하세요. 다만 이는 일시적 조치로, PR 전에 반드시 훅을 다시 켜고 CI를 통과해야 합니다.
- CI는 PR에 지정된 빌드/테스트/린트 과정을 수행합니다. PR은 CI 통과 후에 병합하세요.

## 기타
- 빌드 산출물(더 이상 필요하지 않은 로그/이전 빌드 기록)은 `scripts/cleanup-build-results.sh`를 사용해서 정리하세요.
- 저장소에 큰 바이너리(예: `node_modules`, 컴파일된 바이너리)를 실수로 커밋했다면 `git filter-repo` 또는 BFG를 사용하여 역사를 정리할 수 있습니다.

감사합니다!
