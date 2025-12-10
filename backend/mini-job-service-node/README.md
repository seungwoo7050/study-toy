# mini-job-service-node (백엔드 - Node.js)

> ⚠️ 이 리포지토리는 **학습용 토이 프로젝트**입니다. 실서비스 운영/보안/장애 대응을 전제로 설계되지 않았습니다.

Express + TypeScript 기반의 Job 관리 REST API 서비스입니다. 기존 Spring Boot 버전(`backend/mini-job-service`)과 동일한 목적과 엔드포인트 계약을 따르며, `frontend/mini-job-dashboard`가 `VITE_API_BASE_URL`을 통해 이 서버(`http://localhost:8082/api`)에 연결될 수 있습니다.

---

## 기술 스택

- Node.js 20
- Express 4.x
- TypeScript 5.x
- JWT (jsonwebtoken)
- bcryptjs (패스워드 해싱)
- Vitest + Supertest (테스트)
- npm scripts

---

## Version Roadmap (Node)

| 버전 | 설명 | 태그 제안 |
|------|------|-----------|
| Node-BE-v0.1 | 헬스체크 + 인메모리 Job CRUD + 간단한 JWT 인증 | `NODE-BE-v0.1` |
| 이후 확장 | 파일/DB 저장, Swagger 문서화, Docker Compose 예시 등 | TBD |

---

## API 엔드포인트

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/health` | 헬스체크 |
| GET | `/api/jobs` | Job 목록 조회 |
| GET | `/api/jobs/{id}` | Job 상세 조회 |
| POST | `/api/jobs` | Job 생성 |
| DELETE | `/api/jobs/{id}` | Job 삭제 |
| POST | `/api/auth/signup` | 회원가입 (JWT 발급) |
| POST | `/api/auth/login` | 로그인 (JWT 발급) |

---

## 빠른 시작

```bash
# 1) 의존성 설치
cd backend/mini-job-service-node
npm install

# 2) 개발 서버 실행 (기본 포트 8082)
npm run dev
# 또는 빌드 후 실행
npm run build && npm start

# 3) 헬스체크
curl http://localhost:8082/health
```

프론트엔드 연동 시 `frontend/mini-job-dashboard/.env` 또는 실행 환경에서
`VITE_API_BASE_URL=http://localhost:8082/api`를 설정하면 됩니다.

---

## 테스트

```bash
npm test
```

Vitest + Supertest가 `/health`, `/api/jobs`, `/api/auth` 흐름을 검증합니다.

---

## 학습 가이드

- TypeScript 클래스/모듈이 Java와 유사한 구조를 제공하므로 두 언어를 비교하며 학습해 보세요.
- `src/job/jobScheduler.ts`는 `@Scheduled` 스타일의 비동기 처리를 타이머로 흉내 냅니다.
- 더 자세한 단계별 학습 흐름은 [TUTORIAL_BE_NODE.md](./TUTORIAL_BE_NODE.md)를 확인하세요.

