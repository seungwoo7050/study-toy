# mini-job-service-node 튜토리얼 (Express + TypeScript)

이 문서는 `backend/mini-job-service-node` 프로젝트를 **"스프링 버전과 1:1로 비교하며"** 학습하는 것이 목표입니다.

- 같은 도메인(Job, User, Auth)을 **Node/Express + TypeScript**로 구현
- 인메모리 저장소 → 간단한 도메인 로직 → JWT 인증 → 에러 핸들링 순서로 학습
- "실무 Node 백엔드 서비스의 최소 골격"을 몸으로 익히는 데 초점

---

## 0. 사전 준비

- 위치: 레포 루트 기준  
  `cd backend/mini-job-service-node`
- 필요 도구
  - Node.js 20.x LTS
  - npm 10.x
- 스프링 버전과 함께 테스트하려면:
  - `backend/mini-job-service`도 `./gradlew bootRun` 또는 `bootJar`로 실행해 둘 수 있습니다.

```bash
node -v
npm -v
````

---

## 1. 프로젝트 구조 훑어보기

[LEARN] **Express + TS 백엔드의 기본 분해**

* `src/index.ts`

  * 애플리케이션 진입점. `app.ts`에서 만든 Express 인스턴스를 가져와 포트를 열어줍니다.
* `src/app.ts`

  * Express 앱 생성, 공통 미들웨어(body parser, CORS, 로깅 등) 설정.
  * 라우터(`router/index`)와 에러 핸들러(`common/errorHandler`)를 붙이는 곳.
* `src/router/`

  * `router/index.ts` : `/health`, `/api/jobs`, `/api/auth` 등 **prefix**별 라우터를 한 번에 모아줌.
  * `router/routes/*.ts` : 도메인 라우터(health, job, auth 등).
* `src/job/`

  * `job.ts` : Job 도메인 모델.
  * `jobRepository.ts` : 인메모리 Job 저장소.
  * `jobService.ts` : 비즈니스 로직 (검증, 상태 변경, 조회).
  * `jobScheduler.ts` : Job 상태를 "비동기 작업처럼" 바꿔주는 시뮬레이터.
* `src/user/`

  * `user.ts`, `userRepository.ts`, `userService.ts` : User 도메인/저장소/서비스.
* `src/common/errorHandler.ts`

  * 공통 에러 타입(`HttpError`)과 Express 에러 핸들러 미들웨어.

> 스프링에서의 `Controller / Service / Repository / Entity` 조합이
> 여기서는 `Router / Service / Repository / Domain Class`로 대응된다고 보면 편합니다.

---

## 2. 설치 & 기본 빌드/테스트

### 2-1. 의존성 설치

```bash
cd backend/mini-job-service-node
npm install
```

> 가능하면 `npm ci`를 쓰고 싶겠지만, 이 프로젝트는 로컬 학습 위주라 기본적으로 `npm install`을 사용해도 무방합니다.

### 2-2. 빌드 & 테스트

```bash
# 타입스크립트 빌드
npm run build

# 테스트 (vitest)
npm test
# 또는 watch 모드가 있다면
npm run test:watch
```

* `npm run build`

  * `tsconfig.json` 설정에 따라 `src/` → `dist/` 로 컴파일
* `npm test`

  * vitest + supertest 등을 사용해 **REST API 흐름**을 검증하도록 설계 (샘플/연습 테스트 추가 가능)

---

## 3. 서버 실행 & 헬스 체크

### 3-1. 개발 서버 실행

```bash
npm run dev
# 또는
npm start
```

* `npm run dev`

  * 일반적으로 `ts-node-dev` 혹은 `nodemon` 기반의 핫리로드 개발 서버를 띄우는 스크립트.
* `npm start`

  * 빌드된 `dist/index.js`를 실행하는 프로덕션 모드에 가까운 실행.

실행 후 콘솔 로그 예시:

```text
Server started on port 8081
```

(포트 번호는 `appConfig.ts` 또는 환경 변수에서 관리)

### 3-2. 헬스 체크 확인

```bash
curl -i http://localhost:8081/health
```

예상 응답:

```http
HTTP/1.1 200 OK
Content-Type: application/json; charset=utf-8

{"status":"OK","message":"mini-job-service-node is up"}
```

* `router/routes/healthRouter.ts`에서 이 엔드포인트를 정의합니다.
* 스프링의 `HealthController`와 거의 같은 역할.

[LEARN]
헬스 체크는 **로드 밸런서/쿠버네티스** 등이 서비스의 살아있음을 확인할 때 가장 먼저 참고하는 엔드포인트입니다.

---

## 4. Job 도메인 이해하기 (Node 관점)

### 4-1. Job 모델 (`job.ts`)

주요 필드 (예시):

* `id`: string
* `type`: string (작업 종류)
* `payload`: string (작업에 필요한 데이터)
* `status`: `'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED'`
* `createdAt`, `updatedAt`: 날짜 정보

[LEARN]
"엔티티"라기보다는 **"작업 큐에 들어가는 Job"**을 표현하는 객체입니다.
스프링에서의 `Job` 엔티티/도메인과 대칭 구조를 가지도록 설계되어 있습니다.

### 4-2. JobRepository (`jobRepository.ts`)

* 내부적으로 `Map<string, Job>` 사용
* 주요 메서드 예시:

  * `save(job: Job): Job`
  * `findById(id: string): Job | undefined`
  * `findAll(): Job[]`
  * `delete(id: string): void`

[LEARN]
실무에서는 DB(예: PostgreSQL, Redis 등)를 사용하지만,
학습을 위해 **메모리 기반 Map**으로 동일한 패턴을 연습하도록 만들었습니다.

### 4-3. JobService (`jobService.ts`)

* `Job`을 생성/조회/삭제/상태 변경하는 비즈니스 로직 담당
* 예시 기능:

  * `createJob(type, payload)`
  * `getJob(id)`
  * `listJobs()`
  * `cancelJob(id)`
  * `markInProgress / markCompleted / markFailed`

[LEARN]
컨트롤러(라우터)에서는 **입출력 & HTTP 프로토콜 처리**만 하고,
실제 비즈니스 규칙은 항상 `Service`가 담당하도록 분리하는 것이 핵심입니다.

---

## 5. Job REST API 흐름 (라우터 ↔ 서비스)

### 5-1. Job Router 개요 (`router/routes/jobRouter.ts`)

라우터는 대략 이런 구조를 가집니다:

* `GET /api/jobs` : Job 목록 조회
* `GET /api/jobs/:id` : Job 단건 조회
* `POST /api/jobs` : Job 생성
* `POST /api/jobs/:id/cancel` : Job 취소

[LEARN]
Express에서는 `Router` 인스턴스를 만들어 경로/메서드 별로 핸들러를 연결합니다.
Spring의 `@RestController + @RequestMapping` 조합을 떠올리면 됩니다.

### 5-2. 생성 API 예시 (`POST /api/jobs`)

흐름:

1. `jobRouter`에서 요청 바디(`type`, `payload` 등)를 읽음
2. 간단한 유효성 검사 후 `JobService.createJob(...)` 호출
3. 생성된 Job 객체를 JSON으로 반환 (`201 Created`)

테스트 예시:

```bash
curl -i -X POST http://localhost:8081/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"type":"EMAIL","payload":"{\"to\":\"test@example.com\"}"}'
```

예상 응답:

```http
HTTP/1.1 201 Created
Content-Type: application/json

{"id":"...","type":"EMAIL","status":"PENDING", ...}
```

[LEARN]

* Node/Express에서는 **스키마 없는 JSON**을 그대로 주고 받을 수 있지만,
  TypeScript 타입(`Job`)으로 "마음속의 스키마"를 강제하는 것이 포인트입니다.
* 추후 실습으로 **요청 바디를 Zod/Valibot 등으로 검증**해 볼 수도 있습니다.

---

## 6. Job 상태 전이 & 스케줄러

### 6-1. JobScheduler (`jobScheduler.ts`)

* 일정 간격(tick)으로 `PENDING` Job을 찾아 `IN_PROGRESS` → `COMPLETED` 등으로 상태를 변경하는 시뮬레이션용 컴포넌트.
* 실제 메시지 큐(Kafka, SQS, Redis Streams)가 없는 환경에서 **비동기 작업 처리의 느낌**을 내기 위해 존재합니다.

[LEARN]
스프링에서 `@Scheduled` 작업이나 메시지 리스너가 하던 일을
Node 환경에서는 `setInterval` 또는 전용 스케줄러 라이브러리(node-cron 등)가 담당합니다.

### 6-2. 상태 확인

1. Job 생성
2. 일정 시간이 지난 후 `GET /api/jobs/:id`로 다시 조회
3. status 값이 `IN_PROGRESS` 또는 `COMPLETED`로 바뀌어 있는지 확인

---

## 7. 인증/인가 (JWT 기반)

### 7-1. User 도메인 및 저장소

* `user.ts` : User 모델 (`id`, `email`, `passwordHash` 등)
* `userRepository.ts` : 이메일로 사용자 찾기, 신규 사용자 저장
* `userService.ts` : 회원가입, 로그인, 패스워드 검증, JWT 발급 등을 담당

[LEARN]
실무에서는 Bcrypt 등으로 패스워드를 해시하여 저장합니다.
튜토리얼에서도 "텍스트 비밀번호 저장은 금지"라는 메시지를 주는 것이 중요합니다.

### 7-2. Auth Router (`router/routes/authRouter.ts`)

예상 엔드포인트:

* `POST /api/auth/signup` : 회원가입
* `POST /api/auth/login` : 로그인 → JWT 토큰 반환

로그인 테스트 예시:

```bash
# 1) 회원가입
curl -i -X POST http://localhost:8081/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"pass1234"}'

# 2) 로그인
curl -i -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"pass1234"}'
```

로그인 성공 시 응답 예시:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 7-3. 보호된 라우트와 미들웨어

* 일부 Job API를 **인증된 사용자만 접근**하도록 바꾸고 싶다면:

  * JWT 검증 미들웨어를 만들어 `jobRouter`의 특정 라우트 앞에 붙이면 됩니다. (연습 과제용으로 적합)

[LEARN]
스프링의 `OncePerRequestFilter + SecurityConfig`에서 하던 일을
여기서는 **Express 미들웨어 하나**로 구현한다고 보면 됩니다.

---

## 8. 에러 핸들링 패턴

### 8-1. HttpError & errorHandler (`common/errorHandler.ts`)

* `HttpError` 클래스

  * `statusCode`, `message`를 가진 커스텀 에러 타입.
* Express 에러 핸들러 미들웨어:

  * `next(err)`로 전달된 에러를 잡아서 JSON 응답으로 포맷팅.
  * 예: `{ "status": 404, "message": "Job not found" }`

### 8-2. 사용하는 쪽 패턴

* 잘못된 입력/존재하지 않는 리소스 등에서:

```ts
if (!job) {
  throw new HttpError(404, 'Job not found');
}
```

* 라우터에서는 `try/catch` 후 `next(err)` 호출:

```ts
router.get('/:id', async (req, res, next) => {
  try {
    const job = await jobService.getJob(req.params.id);
    res.json(job);
  } catch (err) {
    next(err);
  }
});
```

[LEARN]
스프링의 `@ControllerAdvice`와 `@ExceptionHandler`가 하던 일을
Express에서는 `error-handling middleware`가 담당합니다.

---

## 9. 테스트 (Vitest + Supertest 기준)

> 실제 리포에 포함된 테스트 파일 이름에 맞춰 읽으시면 됩니다.
> 여기서는 "테스트 설계 관점" 위주로 정리합니다.

### 9-1. 유닛 테스트 아이디어

* `jobService.spec.ts`

  * `createJob`이 올바른 Job을 반환하는지
  * 존재하지 않는 Job 조회/취소 시 올바른 예외를 던지는지
* `userService.spec.ts`

  * 잘못된 패스워드로 로그인 시 에러 발생 여부
  * JWT 토큰이 올바른 payload를 갖는지

### 9-2. 통합 테스트 (API 레벨)

* `app` 또는 `router`를 Supertest로 띄워 놓고:

```ts
import request from 'supertest';
import { app } from '../app';

it('creates job via POST /api/jobs', async () => {
  const res = await request(app)
    .post('/api/jobs')
    .send({ type: 'EMAIL', payload: '{}' })
    .expect(201);

  expect(res.body.id).toBeDefined();
});
```

[LEARN]
스프링의 `MockMvc`와 비슷한 역할을 하는 것이 `supertest`입니다.
"HTTP 레벨에서 API를 검증한다"는 컨셉을 Node에서도 그대로 가져오는 것이 목표입니다.

---

## 10. 환경 변수 & 설정 (appConfig.ts)

* 포트, JWT 시크릿 등은 보통 `.env`에서 관리합니다.

예시 `.env`:

```env
PORT=8081
JWT_SECRET=local-dev-secret
JWT_EXPIRES_IN=1h
```

* `appConfig.ts`에서 `process.env.PORT ?? 8081`처럼 기본값을 두고 읽습니다.

[LEARN]
스프링의 `application.yml`에 있던 설정들이
Node/Express에서는 `process.env` + 작은 설정 모듈로 분리된다고 보면 됩니다.

---

## 11. 스프링 버전과 1:1 비교하며 복습하기 (권장 루틴)

1. `backend/mini-job-service`의 `JobController`, `JobService`, `InMemoryJobRepository`를 다시 읽어 보기
2. 동일한 책임을 가진 Node 파일(`jobRouter`, `jobService`, `jobRepository`)을 차례로 읽기
3. "어떤 개념이 어디로 매핑되었는지"를 표로 정리해 보는 것을 추천

   * 예:

     * `@RestController` ↔ `Router`
     * `Service` ↔ `Service (동일)`
     * `JPA Entity` ↔ `TypeScript class`
     * `@Scheduled` ↔ `JobScheduler` + `setInterval`
     * `@ControllerAdvice` ↔ error-handling middleware

---

## 12. 마무리 및 확장 아이디어

[LEARN]
이 Node 백엔드는 "스프링 백엔드와 똑같은 기능을 다른 기술 스택으로 구현해 보는" 학습용 프로젝트입니다.
**목표는 "Node를 잘 하는 것"이 아니라 "백엔드 개념을 스택에 관계없이 이해하는 것"**입니다.

추가로 해볼 수 있는 연습:

* Job에 `priority` 필드를 추가하고, 스케줄러가 높은 우선순위부터 처리하게 만들기
* 인메모리 저장소를 파일 저장 / SQLite / Redis 등으로 교체해 보기
* 인증된 사용자만 Job을 생성/조회할 수 있도록 미들웨어 추가
* CI(`.github/workflows/ci.yml`)에서 Node 테스트 스텝을 수정/확장해 보기

---

이걸 기준으로 기존 파일과 비교하면서
"이 부분은 너무 장황하다 / 여기는 더 쪼개고 싶다" 같은 피드백 주시면,
그에 맞춰 한 번 더 다듬어진 **v2 튜토리얼**도 만들어 줄 수 있습니다.
