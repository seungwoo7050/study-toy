[FILE] frontend/mini-job-dashboard/src/App.tsx
[Order 1]
# Mini Job Dashboard - Frontend 튜토리얼

이 문서는 `frontend/mini-job-dashboard` 프로젝트를 로컬에서 빌드하고 백엔드와 연동하여 동작을 확인하는 방법을 단계별로 설명합니다.

사전 요구사항
- Node.js (권장 LTS) 및 `npm`이 설치되어 있어야 합니다. macOS에서는 `brew install node` 또는 nvm을 사용하세요.
- 로컬에서 백엔드(`backend/mini-job-service`)가 `http://127.0.0.1:8080`에서 실행되어 있어야 합니다.

빠른 시작
1. 프로젝트 루트로 이동:
   - `cd frontend/mini-job-dashboard`
2. 의존성 설치:
   - `npm ci`
3. 개발 서버 실행 (hot reload):
   - `npm run dev`
   - 브라우저에서 `http://localhost:5173` (Vite 기본 포트) 열기

프로덕션 빌드 및 정적 파일 확인
1. 빌드:
   - `npm run build`
   - 산출물은 `dist/` 폴더에 생성됩니다.
2. 간단한 정적 서버로 확인:
   - `npx serve dist` 또는 `npx http-server dist -p 8081`
   - 브라우저에서 `http://localhost:8081` 열기

백엔드 연동 설정
- 프론트엔드는 `src/services/JobApiService.ts`에서 백엔드 엔드포인트를 사용합니다.
- 기본은 `http://localhost:8080/api` 입니다. (또는 `http://127.0.0.1:8080/api`)
- Vite 환경변수로 값을 오버라이드하려면 프로젝트 루트에 `.env` 파일을 만들고 `VITE_API_BASE_URL`을 설정하세요. 예:

```
VITE_API_BASE_URL="http://staging.example.com/api"
```

프론트엔드 코드는 `import.meta.env.VITE_API_BASE_URL`를 읽어 사용하므로 개발/배포 환경에 맞춰 쉽게 변경할 수 있습니다.
- 필요하면 `JobApiService.ts`의 `BASE_URL`을 변경하거나, 빌드 시 환경 변수를 주입하도록 Vite 설정을 업데이트하세요.

검증 시나리오
1. 백엔드 실행:
   - `cd backend/mini-job-service`
   - `./gradlew clean bootJar`
   - `java -jar build/libs/mini-job-service-0.0.1-SNAPSHOT.jar`
   - 또는 `./gradlew bootRun` (wrapper 사용 권장)
2. 프론트엔드 개발 서버 실행: `npm run dev`
3. 브라우저에서 프론트엔드 접근 후, Job 생성 폼을 통해 새 Job을 등록하고 목록에 표시되는지 확인합니다.

테스트 및 디버깅 팁
- 백엔드가 8080 포트를 이미 사용 중이면 종료하거나 다른 포트를 사용하세요.
- CORS 관련 이슈 발생 시 백엔드에서 `CorsConfiguration`을 확인하세요 (디폴트로 로컬 개발을 위해 허용 설정 가능).
- 프론트엔드 콘솔에 요청/응답 오류가 나오면 `Network` 탭에서 요청 URL과 응답 코드를 확인하세요.

문제 해결
- `npm ci` 실패 시 `node`/`npm` 버전을 확인하고 캐시 삭제 후 재시도: `npm cache clean --force`.
- 정적 빌드 후 빈 페이지가 보이면 `dist/index.html`을 직접 열어 JS 번들이 로드되는지 확인하세요.

추가 정보
- 개발 편의를 위해 `TUTORIAL_FE.md`와 연동한 `TUTORIAL_BE.md` 및 루트 `ROOT_ROADMAP.md`를 참조하세요.

[LEARN]
- 이 튜토리얼은 로컬 개발을 위한 최소 구성과 검증 절차를 제공합니다.
