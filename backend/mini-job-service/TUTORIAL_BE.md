# mini-job-service CLI 튜토리얼

이 문서는 `mini-job-service` 백엔드를 버전별로 단계적으로 학습하는 튜토리얼입니다.

참고: 이 레포에 포함된 유틸 스크립트 목록과 사용 방법은 `DOCS/SCRIPTS.md`에서 확인할 수 있습니다.

---


## BE-v0.1 – 헬스체크까지

### 1. 사전 준비
- 필요한 선행 버전: 없음
- 필요한 도구: JDK 21, Gradle (ENV_SETUP.md 참고)

### 2. 코드 체크아웃 (선택)
```bash
git checkout BE-v0.1
```

### 3. 구현 및 커밋
- `HealthController` 클래스를 만들고 `/health` 엔드포인트를 구현하세요.
- **Tip:** `@RestController`와 `@GetMapping`을 사용합니다.

💡 **[Git Commit Tip]**
구현을 마쳤다면 아래와 같이 커밋하세요:
```bash
GIT_AUTHOR_DATE="2025-01-02 18:00:00" GIT_COMMITTER_DATE="2025-01-02 18:00:00" git commit -m "feat: add health check endpoint"
```

### 4. 실행
```bash
cd backend/mini-job-service
./gradlew bootRun
```

### 5. 동작 확인
```bash
curl http://localhost:8080/health
```
**기대 응답:**
```json
{"status":"ok"}
```

### 6. 이 단계에서 배우는 것
- Spring Boot 앱 실행 방법
- @RestController를 이용한 간단한 엔드포인트 구현
- 헬스체크 엔드포인트의 역할

---


## BE-v0.2 – 메모리 기반 Job CRUD

### 1. 사전 준비
- 필요한 선행 버전: BE-v0.1
- 필요한 도구: JDK 21, Gradle

### 2. 코드 체크아웃 (선택)
```bash

```

### 3. 구현 및 커밋
- `Job`, `JobRepository`, `JobController`를 추가하여 메모리 기반 CRUD를 구현하세요.
- **Tip:** `@Repository`, `@RestController`, DTO 패턴을 활용합니다.

💡 **[Git Commit Tip]**
구현을 마쳤다면 아래와 같이 커밋하세요:
```bash
GIT_AUTHOR_DATE="2025-01-04 20:00:00" GIT_COMMITTER_DATE="2025-01-04 20:00:00" git commit -m "feat: implement in-memory Job CRUD"
```

### 4. 실행
```bash
./gradlew bootRun
```

### 5. 동작 확인
**Job 생성:**
```bash
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"type": "VIDEO_TRIM", "payload": "{\"duration\": 60}"}'
```
**기대 응답:**
```json
{
  "id": 1,
  "type": "VIDEO_TRIM",
  "status": "PENDING",
  "payload": "{\"duration\": 60}",
  "createdAt": "2024-01-15T10:30:00"
}
```
**Job 목록 조회:**
```bash
curl http://localhost:8080/api/jobs
```
**단일 Job 조회:**
```bash
curl http://localhost:8080/api/jobs/1
```
**Job 삭제:**
```bash
curl -X DELETE http://localhost:8080/api/jobs/1
```

### 6. 이 단계에서 배우는 것
- REST API 설계 (GET, POST, DELETE)
- DTO 패턴 (CreateJobRequest, JobResponse)
- 메모리 기반 Repository 패턴
---

## BE-v0.3 – H2 + JPA 기반 Job 영속화

### 1. 사전 준비

- 필요한 선행 버전: BE-v0.2
- 필요한 도구: JDK 21, Gradle

### 2. 코드 체크아웃 (선택)

```bash
git checkout BE-v0.3
```

### 3. 실행

```bash
./gradlew bootRun
```

### 4. 동작 확인

**Job 생성 및 조회 (앞선 버전과 동일):**
```bash
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"type": "MATCHMAKING", "payload": null}'

curl http://localhost:8080/api/jobs
```

**H2 콘솔 접속:**
- 브라우저에서 `http://localhost:8080/h2-console` 접속
- JDBC URL: `jdbc:h2:mem:minijob`
- Username: `sa`
- Password: (빈 값)

**H2 콘솔에서 SQL 실행:**
```sql
SELECT * FROM JOBS;
```

### 5. 이 단계에서 배우는 것

- JPA 엔티티 매핑 (@Entity, @Id, @Column)
- Spring Data JPA Repository
- H2 인메모리 데이터베이스 사용법

---

## BE-v0.4 – PostgreSQL + Flyway 도입

### 1. 사전 준비

- 필요한 선행 버전: BE-v0.3
- 필요한 도구: JDK 21, Gradle, Docker

### 2. 코드 체크아웃 (선택)

```bash
git checkout BE-v0.4
```

### 3. PostgreSQL 시작

```bash
cd backend/mini-job-service
docker-compose up -d
```

**컨테이너 상태 확인:**
```bash
docker ps
```

### 4. 애플리케이션 실행 (postgres 프로파일)

```bash
./gradlew bootRun --args='--spring.profiles.active=postgres'
```

### 5. 동작 확인

```bash
curl http://localhost:8080/health
curl http://localhost:8080/api/jobs
```

**PostgreSQL 직접 접속:**
```bash
docker exec -it minijob-postgres psql -U postgres -d minijob

# SQL 실행
SELECT * FROM jobs;
\dt  # 테이블 목록
\q   # 종료
```

### 6. 이 단계에서 배우는 것

- Docker Compose로 PostgreSQL 실행
- Spring Profiles를 이용한 환경 분리
- Flyway를 이용한 DB 마이그레이션

---

## BE-v0.5 – 공통 예외 처리 + 검증 + 로깅

### 1. 사전 준비

- 필요한 선행 버전: BE-v0.4
- 필요한 도구: JDK 21, Gradle

### 2. 코드 체크아웃 (선택)

```bash
git checkout BE-v0.5
```

### 3. 실행

```bash
./gradlew bootRun
```

### 4. 동작 확인

**검증 실패 테스트:**
```bash
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"type": "", "payload": "test"}'
```

**기대 응답 (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Job type is required",
  "path": "/api/jobs",
  "timestamp": "2024-01-15T10:30:00"
}
```

**존재하지 않는 Job 조회:**
```bash
curl http://localhost:8080/api/jobs/99999
```

**기대 응답 (404 Not Found):**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Job not found with id: 99999",
  "path": "/api/jobs/99999",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 5. 이 단계에서 배우는 것

- @ControllerAdvice를 이용한 전역 예외 처리
- @Valid, @NotBlank 등을 이용한 입력 검증
- 일관된 에러 응답 형식

---

## BE-v0.6 – User + JWT 인증/인가

### 1. 사전 준비

- 필요한 선행 버전: BE-v0.5
- 필요한 도구: JDK 21, Gradle

### 2. 코드 체크아웃 (선택)

```bash
git checkout BE-v0.6
```

### 3. 실행

```bash
./gradlew bootRun
```

### 4. 동작 확인

**회원가입:**
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123"}'
```

**기대 응답:**
```json
{
  "id": 1,
  "email": "test@example.com",
  "message": "User registered successfully"
}
```

**로그인:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123"}'
```

**기대 응답:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**JWT 토큰으로 API 호출:**
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl http://localhost:8080/api/jobs \
  -H "Authorization: Bearer $TOKEN"
```

### 5. 이 단계에서 배우는 것

- Spring Security 설정
- JWT 토큰 생성 및 검증
- BCrypt를 이용한 비밀번호 해싱
- 인증 필터 체인

---

## BE-v0.7 – 비동기 Job 처리 시뮬레이션

### 1. 사전 준비

- 필요한 선행 버전: BE-v0.6
- 필요한 도구: JDK 21, Gradle

### 2. 코드 체크아웃 (선택)

```bash
git checkout BE-v0.7
```

### 3. 실행

```bash
./gradlew bootRun
```

### 4. 동작 확인

**Job 생성:**
```bash
curl -X POST http://localhost:8080/api/jobs \
  -H "Content-Type: application/json" \
  -d '{"type": "VIDEO_THUMBNAIL", "payload": "{\"url\": \"http://example.com/video.mp4\"}"}'
```

**10초 후 상태 확인:**
```bash
# 잠시 대기 후
curl http://localhost:8080/api/jobs/1
```

**기대 응답 (상태가 DONE 또는 FAILED로 변경됨):**
```json
{
  "id": 1,
  "type": "VIDEO_THUMBNAIL",
  "status": "DONE",
  "payload": "{\"url\": \"http://example.com/video.mp4\"}",
  "createdAt": "2024-01-15T10:30:00"
}
```

**로그 확인:**
서버 로그에서 "Processing job", "Job completed successfully" 메시지 확인

### 5. 이 단계에서 배우는 것

- @Scheduled를 이용한 주기적 작업
- Job 상태 전이 (PENDING → RUNNING → DONE/FAILED)
- 백그라운드 작업 패턴

---

## BE-v0.8 – 통합 테스트 + Docker Compose

### 1. 사전 준비

- 필요한 선행 버전: BE-v0.7
- 필요한 도구: JDK 21, Gradle, Docker

### 2. 코드 체크아웃 (선택)

```bash
git checkout BE-v0.8
```

### 3. 테스트 실행

```bash
./gradlew test
```

**기대 출력:**
```
BUILD SUCCESSFUL

> Task :test
JobControllerIntegrationTest > healthCheck_ReturnsOk() PASSED
JobControllerIntegrationTest > createJob_Success() PASSED
JobControllerIntegrationTest > createJob_InvalidRequest_ReturnsBadRequest() PASSED
JobControllerIntegrationTest > listJobs_ReturnsJobList() PASSED
JobControllerIntegrationTest > getJob_NotFound() PASSED
```

### 4. Docker Compose로 전체 실행

```bash
# PostgreSQL 시작
docker-compose up -d

# 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=postgres'
```

### 5. 이 단계에서 배우는 것

- @SpringBootTest를 이용한 통합 테스트
- MockMvc를 이용한 HTTP 요청 테스트
- Docker Compose를 이용한 개발 환경 구성

---

## 전체 버전 체크아웃 예시

```bash
# 특정 버전으로 이동
git checkout BE-v0.1
git checkout BE-v0.3
git checkout BE-v0.6

# 최신 버전으로 복귀
git checkout main
```

---

## 다음 단계

백엔드 학습을 완료했다면, 프론트엔드 튜토리얼로 이동하세요:

- [TUTORIAL_FE.md](../../frontend/mini-job-dashboard/TUTORIAL_FE.md)
