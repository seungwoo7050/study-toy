# mini-job-dashboard (프론트엔드)

> ⚠️ 이 리포지토리는 **학습용 토이 프로젝트**입니다.  
>   실서비스 운영/보안/장애 대응을 전제로 설계되지 않았습니다.

React 기반의 Job 관리 대시보드 UI입니다.  
백엔드 `mini-job-service`와 HTTP로 통신합니다.

---

## 기술 스택

- React 18
- TypeScript
- Vite (빌드 도구)
- fetch API (HTTP 클라이언트)
- CSS (스타일링)

---

## Version Roadmap

| 버전 | 설명 | Git 태그 |
|------|------|----------|
| F0.1 | 프로젝트 뼈대, `/jobs` 기본 화면 | `FE-F0.1` |
| F0.2 | dummy 데이터 Job 리스트 렌더링 | `FE-F0.2` |
| F0.3 | Job 생성 폼 (로컬 상태) | `FE-F0.3` |
| F0.4 | 백엔드 REST 연동 (GET/POST) | `FE-F0.4` |
| F0.5 | Job 상태 폴링 (주기적 갱신) | `FE-F0.5` |

---

## Implementation Order (Files)

### FE-F0.1

1. `main.tsx` - 앱 진입점
2. `App.tsx` - 메인 컴포넌트

### FE-F0.2

1. `types/Job.ts` - Job 타입 정의
2. `components/JobList.tsx` - Job 목록 컴포넌트

### FE-F0.3

1. `components/JobForm.tsx` - Job 생성 폼 컴포넌트
2. `hooks/useLocalJobs.ts` - 로컬 상태 관리 훅 (선택)

### FE-F0.4

1. `hooks/useJobsApi.ts` - 백엔드 API 호출 훅
2. `App.tsx` - 백엔드 연동으로 업데이트

### FE-F0.5

1. `hooks/useJobsPolling.ts` - 폴링 로직 훅
2. `App.tsx` - 폴링 로직 연결

---

## 빠른 시작

```bash
# 프로젝트 디렉토리로 이동
cd frontend/mini-job-dashboard

# 의존성 설치 (권장: `npm ci` - lockfile 기반 설치)
npm ci

# 개발 서버 실행
npm run dev
```

브라우저에서 `http://localhost:5173` 접속

자세한 단계별 튜토리얼은 [TUTORIAL_FE.md](./TUTORIAL_FE.md)를 참고하세요.

---

## 화면 구성

| 경로 | 설명 | 버전 |
|------|------|------|
| `/` | 메인 페이지 (Job 목록) | F0.1+ |
| Job 생성 폼 | 새 Job 추가 | F0.3+ |

---

## 환경 변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `VITE_API_BASE_URL` | `http://localhost:8080/api` | 백엔드 API URL |

`.env.local` 파일을 생성하여 설정할 수 있습니다. `VITE_API_BASE_URL` 값을 설정하세요:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

빌드 후 생성된 `dist/`는 기본적으로 git에 포함하지 않도록 `.gitignore`에 추가되어야 합니다.
또한 빌드 결과와 로그를 정리하려면 루트의 `./scripts/cleanup-build-results.sh`을 사용하세요.

---

## Troubleshooting

### `npm ci` 또는 `npm install` 에러

- Node.js 버전이 ENV_SETUP.md에 명시된 버전 이상인지 확인한다.
- `node -v`로 버전 확인

### 개발 서버 포트 충돌 (3000 또는 5173 already in use)

- 다른 개발 서버를 종료하거나, `vite.config.ts`에서 포트를 변경한다.

```typescript
// vite.config.ts
export default defineConfig({
  server: {
    port: 3001, // 다른 포트로 변경
  },
});
```

### 백엔드 연동 시 CORS 에러

- 백엔드 `mini-job-service`의 CORS 설정을 확인한다.
- 프론트에서 호출하는 API URL이 실제 백엔드 주소와 일치하는지 확인한다.

```
Access to fetch at 'http://localhost:8080/api/jobs' from origin 'http://localhost:5173' 
has been blocked by CORS policy
```

→ 백엔드의 `config/WebConfig.java` 또는 `SecurityConfig.java`에서 CORS 설정 추가 필요

### 빈 화면이 보이는 경우

- 브라우저 개발자 도구(F12)의 Console 탭에서 에러 확인
- Network 탭에서 API 호출 실패 여부 확인
