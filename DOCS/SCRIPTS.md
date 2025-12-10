# Script Catalog

이 파일은 튜토리얼 레포에서 학생/사용자가 자주 마주치게 될 스크립트들의 목적, 사용법 및 주의사항을 모아놓은 문서입니다.

## scripts/check-staged-for-artifacts.sh
- 목적: pre-commit hook (혹은 수동 실행)으로 커밋 인덱스에 빌드 산출물(예: `node_modules`, `dist`, `build`, `*.class`, `*.jar`)이 들어있는지 검사합니다.
- 사용법: `./scripts/check-staged-for-artifacts.sh` (리포지토리 루트에서 실행)
- 예외/주의: 파일명을 단순 매칭으로 검사하기 때문에 필요에 따라 패턴을 수정하세요.

## scripts/cleanup-build-results.sh
- 목적: 로컬에서 생성된 빌드 아티팩트와 불필요한 로그 디렉토리를 정리합니다(개발 환경을 초기화하는 용도).
- 사용법: `./scripts/cleanup-build-results.sh` (리포지토리 루트에서 실행)
- 보존: 스크립트는 기본적으로 `build-logs/frontend-20251210T055849` 디렉토리를 보존합니다. 필요하면 스크립트를 수정하여 보존 목록을 변경하세요.

## scripts/frontend-smoke-test.sh
- 목적: 정적 빌드(`dist`)가 정상 동작하는지 간단히 확인하는 스모크 테스트입니다.
- 사용법:
  - 빌드: `cd frontend/mini-job-dashboard && npm ci && npm run build`
  - 검증: 루트에서 `./scripts/frontend-smoke-test.sh` 실행
- 동작: `npx http-server`를 통해 `dist`를 8081 포트로 서빙 후 `/` 루트 페이지의 HTTP 200 응답을 확인합니다.
- 주의: 포트 충돌 시 스크립트를 수정하여 다른 포트를 사용하거나 기존 프로세스를 종료하세요.

## Frontend unit tests (Vitest)
- 명령: `cd frontend/mini-job-dashboard` 후 `npm run test:unit`
- 목적: React 컴포넌트 단위 테스트를 통한 빠른 피드백(학습 목적)
- 학습 포인트: 컴포넌트 렌더링, UI 텍스트/상태 검증, 간단한 props 전달 테스트 등을 추가하세요.

## Frontend E2E tests (Playwright)
- 명령: `cd frontend/mini-job-dashboard` 후 `npm run test:e2e`
- 목적: 사용자가 실제로 보는 플로우를 시뮬레이션하여 UI 및 네트워크 상호작용을 검증합니다.
- 학습 포인트: Playwright의 `page.route`로 네트워크 요청을 모킹하는 방법과 폼 상호작용 검증을 학습하세요.

## build-all.sh
- 목적: 빠른 시작을 위해 repository 내 모든 서브프로젝트(backend, frontend, cpp)를 빌드하는 convenience 스크립트입니다.
- 사용법: `./build-all.sh`
- 동작:
  - backend: gradle wrapper(`./gradlew`) 우선 사용, 없으면 전역 gradle 사용
  - frontend: `npm ci` + `npm run build`
  - cpp: 명시된 g++ 컴파일 또는 `build.sh` 호출
- 참고: CI 환경에서 빌드와 테스트를 나누는 것이 일반적이므로, 이 스크립트를 CI에서 바로 호출하기보다는 개별 빌드/테스트 스텝을 사용하는 것이 좋습니다.

## tools/check-env.sh
- 목적: 학생/개발자가 필요한 실행 환경(버전/도구)을 간단히 확인하기 위한 스크립트입니다.
- 사용법: `./tools/check-env.sh` (레포트 루트에서 실행)
- 출력: 도구 버전 목록 출력과, 누락된 도구가 있으면 에러로 종료합니다. Docker가 사용 가능하면 버전도 출력합니다.

## .husky/pre-commit (루트 및 프로젝트별)
- 목적: 로컬에서 커밋을 만들 때 자동 실행되는 훅으로, 빌드 아티팩트가 스테이지된 경우 커밋을 차단합니다.
- 사용법: 일반적으로 수동 수정 불필요; 훅이 활성화되어 있는지 확인하려면 아래 명령을 참고하세요.
  - 활성화(로컬): `git config core.hooksPath .husky` (이미 설정된 경우 생략)

## 커스텀 스크립트 등록
- 스크립트를 새로 추가할 때는:
  1. 스크립트 상단에 목적, 사용법, 예시 실행 명령, 주의사항이 포함된 헤더 주석을 추가하세요.
  2. `DOCS/SCRIPTS.md`에 스크립트를 등록하여 학습자가 쉽게 찾을 수 있도록 하세요.

---

필요에 따라 이 문서에 더 많은 스크립트와 세부 사용 방법을 추가하세요. 스크립트 파일 자체에도 헤더 주석으로 목적/사용법을 적어두는 것을 권장합니다.