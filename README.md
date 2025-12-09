# toy

작은 학습용 프로젝트 모음입니다. 백엔드(Java Spring Boot), 프론트엔드(React + Vite), C++ 예제들이 포함되어 있습니다.

목표
- 로컬에서 각 모듈을 빌드하고 실행하는 방법을 문서화
- Gradle wrapper 기반 빌드를 권장 (전역 Gradle 설치 불필요)
 - 프론트엔드는 Vite 환경변수 `VITE_API_BASE_URL`을 통해 백엔드 엔드포인트를 설정할 수 있습니다. (예: `http://localhost:8080/api`)

빠른 시작

Getting started (quick)
1. Verify your environment: `tools/check-env.sh` (optional) or manually check `java -version`, `node -v`, `npm -v`, `git --version`.
2. Set up Git hooks (manual): `git config core.hooksPath .husky` (see `TUTORIAL_GIT_WORKFLOW.md` for details).
3. Build backend: `cd backend/mini-job-service && ./gradlew clean build`.
4. Start frontend dev: `cd frontend/mini-job-dashboard && npm ci && npm run dev`.

1) 전체 빌드 (프로젝트 루트에서):
```
./build-all.sh
```
Cleanup
-------
빌드 후 생성되는 결과물과 로그를 정리하려면 다음 스크립트를 사용하세요:

```bash
# 루트에서 실행
chmod +x ./scripts/cleanup-build-results.sh
./scripts/cleanup-build-results.sh
```


2) 백엔드만 빌드/실행:
```
cd backend/mini-job-service
chmod +x ./gradlew
./gradlew clean bootJar
java -jar build/libs/mini-job-service-0.0.1-SNAPSHOT.jar
```

3) 프론트엔드 개발 서버 / 빌드 (권장: `npm ci`로 의존성 고정 설치):
```
cd frontend/mini-job-dashboard
npm ci
# 개발서버
npm run dev
# 프로덕션 빌드
npm run build
```

프론트엔드 환경변수
- 로컬 개발/배포 환경에 따라 `VITE_API_BASE_URL`을 설정하세요.
- 예시: 프로젝트 루트에 `frontend/mini-job-dashboard/.env` 파일 생성
```
VITE_API_BASE_URL="http://localhost:8080/api"
```

CI
- `.github/workflows/ci.yml`에서 Gradle wrapper + Node 빌드를 수행합니다. CI는 `VITE_API_BASE_URL`을 설정하여 프론트엔드 빌드가 예상값을 사용하도록 합니다.

로그
- 빌드 로그는 `build-logs/` 디렉터리에 타임스탬프 폴더로 저장됩니다.

참고:
- `node_modules/`, `dist/`, `build/`와 같이 CLI/빌드로 생성되는 산출물은 저장소에 커밋하지 않습니다. `.gitignore`를 확인하세요.
- `package-lock.json`(프론트엔드)와 `gradle-wrapper.jar`(백엔드)는 reproducible build를 위해 보통 커밋하는 것을 권장합니다.

[LEARN]
- 학습용 프로젝트로, 프로덕션 수준의 보안/비밀 관리는 본문서에 포함되지 않습니다. JWT 등의 비밀값은 로컬 개발에서는 테스트용 기본값을 사용하지만, 실제 운영 시에는 환경변수/시크릿 매니저를 사용하세요.
