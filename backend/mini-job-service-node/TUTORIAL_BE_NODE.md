# mini-job-service-node 튜토리얼

이 문서는 `mini-job-service-node` 백엔드를 단계별로 학습하는 가이드입니다. Spring 버전과 동일하게 **헬스체크 → Job CRUD → 인증 → 비동기 시뮬레이션** 흐름을 따라가지만, 구현은 Express + TypeScript로 이뤄집니다.

---

## Node-BE-v0.1 – 헬스체크 + 인메모리 Job CRUD

### 1. 사전 준비
- 필요한 도구: Node.js 20+, npm
- 위치: `backend/mini-job-service-node`

### 2. 의존성 설치
```bash
npm install
```

### 3. 구현 체크 포인트
- `/health` 엔드포인트 확인: `src/router/routes/healthRouter.ts`
- Job 도메인/서비스: `src/job/job.ts`, `src/job/jobService.ts`
- 인증 흐름: `src/user/userService.ts` (bcrypt + JWT)
- 비동기 시뮬레이터: `src/job/jobScheduler.ts`

### 4. 실행
```bash
npm run dev
# 또는
npm run build && npm start
```

### 5. 동작 확인
```bash
# 헬스체크
curl http://localhost:8082/health

# Job 생성 & 조회
curl -X POST http://localhost:8082/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"type":"demo","payload":"hello"}'

curl http://localhost:8082/api/jobs
```

### 6. 이 단계에서 배우는 것
- Express 라우터 구성 및 미들웨어 흐름
- TypeScript 클래스를 통한 도메인 모델링
- JWT 발급/검증 흐름 (login/signup)
- setInterval/setTimeout을 이용한 간단한 배치 처리 아이디어

---

## TroubleShooting

### 포트 충돌 (8082 already in use)
```bash
lsof -i :8082
```
다른 프로세스를 종료하거나 `.env`에서 `PORT`를 변경하세요.

### JWT 시크릿 누락
- `.env` 또는 환경변수에 `JWT_SECRET`을 설정합니다. 설정하지 않으면 기본값(`dev-secret-change-me`)이 사용됩니다. 학습용 외 환경에서는 반드시 별도 값을 사용하세요.

### npm install 실패
- `npm cache verify` 후 다시 시도하거나, 네트워크 프록시 설정을 확인하세요.

