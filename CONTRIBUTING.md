# Contributing Guide

이 저장소에 기여할 때 개발자들이 알아야 할 기본 규칙과 워크플로우를 정리합니다.

## 기본 원칙
- 이 저장소는 학습용 토이 프로젝트입니다. 프로덕션 수준의 보안/비밀 관리는 별도 고려가 필요합니다.
- CLI가 생성하는 파일(예: `node_modules`, `dist`, 컴파일 결과물 등)은 저장소에 커밋하지 마세요.
- `package-lock.json`과 `gradle-wrapper.jar`는 의존성/빌드를 고정하기 위해 커밋하는 것을 권장합니다. 팀 정책에 따라 다를 수 있으니 프로젝트 내 합의된 방식을 따르세요.

## 커밋 전 점검
- 새로운 의존성을 추가했다면 `package-lock.json`/`yarn.lock`을 함께 커밋하세요(정책에 따름).
- `node_modules`, `dist`, `build` 디렉터리, 컴파일된 바이너리(`*.class`, C++ /bin 등)는 커밋 금지입니다. `.gitignore`를 확인하세요.
- 코드 스타일/린트 규칙을 준수하세요. (FE: `npm run lint`, BE: `./gradlew check`)

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
- CI는 PR에 지정된 빌드/테스트/린트 과정을 수행합니다. PR은 CI 통과 후에 병합하세요.

## 기타
- 빌드 산출물(더 이상 필요하지 않은 로그/이전 빌드 기록)은 `scripts/cleanup-build-results.sh`를 사용해서 정리하세요.
- 저장소에 큰 바이너리(예: `node_modules`, 컴파일된 바이너리)를 실수로 커밋했다면 `git filter-repo` 또는 BFG를 사용하여 역사를 정리할 수 있습니다.

감사합니다!
