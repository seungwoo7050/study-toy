# mini-job-service (백엔드)

> ⚠️ 이 리포지토리는 **학습용 토이 프로젝트**입니다.  
>   실서비스 운영/보안/장애 대응을 전제로 설계되지 않았습니다.

Spring Boot 기반의 Job 관리 REST API 서비스입니다.

---

## 기술 스택

- Java 21
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- Spring Security (JWT)
- DB: H2 (초기) → PostgreSQL (이후)
- Flyway (마이그레이션)
- Gradle

---

## Version Roadmap

| 버전 | 설명 | Git 태그 |
|------|------|----------|
| v0.1 | 헬스체크 엔드포인트만 있는 최소 Spring Boot 앱 | `BE-v0.1` |
| v0.2 | 메모리 기반 Job CRUD (DB 없음) | `BE-v0.2` |
| v0.3 | H2 + JPA 기반 Job 영속화 | `BE-v0.3` |
| v0.4 | PostgreSQL + Flyway 도입 | `BE-v0.4` |
| v0.5 | 공통 예외 처리 + 검증 + 로깅 | `BE-v0.5` |
| v0.6 | User + JWT 인증/인가 | `BE-v0.6` |
| v0.7 | 비동기 Job 처리 시뮬레이션 (@Scheduled) | `BE-v0.7` |
| v0.8 | 통합 테스트 + Docker Compose | `BE-v0.8` |

---

## Implementation Order (Files)

### BE-v0.1

1. `MiniJobApplication.java` - 메인 애플리케이션 클래스
2. `HealthController.java` - 헬스체크 엔드포인트

### BE-v0.2

1. `job/domain/Job.java` - Job 도메인 모델
2. `job/repository/InMemoryJobRepository.java` - 메모리 저장소
3. `job/service/JobService.java` - 비즈니스 로직
4. `job/api/CreateJobRequest.java` - 생성 요청 DTO
5. `job/api/JobResponse.java` - 응답 DTO
6. `job/api/JobController.java` - REST 컨트롤러

### BE-v0.3

1. `job/domain/Job.java` - JPA 엔티티로 변경
2. `job/repository/JobRepository.java` - JPA Repository 인터페이스
3. `application.yml` - H2 데이터베이스 설정

### BE-v0.4

1. `docker-compose.yml` - PostgreSQL 컨테이너 정의
2. `application.yml` - PostgreSQL 연결 설정
3. `db/migration/V1__init_job.sql` - Flyway 마이그레이션

### BE-v0.5

1. `common/exception/GlobalExceptionHandler.java` - 전역 예외 처리
2. `common/exception/JobNotFoundException.java` - 커스텀 예외
3. `common/dto/ErrorResponse.java` - 에러 응답 DTO
4. `job/api/CreateJobRequest.java` - 검증 어노테이션 추가

### BE-v0.6

1. `user/domain/User.java` - User 엔티티
2. `user/repository/UserRepository.java` - User Repository
3. `user/service/UserService.java` - User 서비스
4. `user/api/AuthController.java` - 인증 컨트롤러
5. `config/SecurityConfig.java` - Spring Security 설정
6. `config/JwtTokenProvider.java` - JWT 토큰 유틸리티
7. `db/migration/V2__init_user.sql` - User 테이블 마이그레이션

### BE-v0.7

1. `job/service/JobScheduler.java` - @Scheduled 기반 Job 처리
2. `config/AsyncConfig.java` - 비동기 설정

### BE-v0.8

1. `src/test/java/.../JobControllerIntegrationTest.java` - 통합 테스트
2. `docker-compose.yml` - 테스트 환경 포함

---

## 빠른 시작

```bash
# 프로젝트 디렉토리로 이동
cd backend/mini-job-service

# 빌드 및 실행
./gradlew bootRun

# 헬스체크 확인
curl http://localhost:8080/health
```

자세한 단계별 튜토리얼은 [TUTORIAL_BE.md](./TUTORIAL_BE.md)를 참고하세요.

---

## API 엔드포인트

| Method | Endpoint | 설명 | 버전 |
|--------|----------|------|------|
| GET | `/health` | 헬스체크 | v0.1+ |
| GET | `/api/jobs` | Job 목록 조회 | v0.2+ |
| GET | `/api/jobs/{id}` | Job 상세 조회 | v0.2+ |
| POST | `/api/jobs` | Job 생성 | v0.2+ |
| DELETE | `/api/jobs/{id}` | Job 삭제 | v0.2+ |
| POST | `/api/auth/signup` | 회원가입 | v0.6+ |
| POST | `/api/auth/login` | 로그인 (JWT 발급) | v0.6+ |

---

## Troubleshooting

### 서버가 바로 종료됨


### DB 연결 오류 (connection refused / authentication failed)


### 포트 충돌 (8080 already in use)


```bash
# 8080 포트 사용 프로세스 확인
lsof -i :8080
```

## Gradle Wrapper (권장)

이 프로젝트는 **Gradle wrapper**를 사용하도록 구성되어 있습니다. 따라서 로컬에 Gradle을 설치할 필요 없이 다음 명령으로 빌드/테스트를 실행할 수 있습니다.

```bash
cd backend/mini-job-service
./gradlew clean build
./gradlew test
```

만약 `gradle-wrapper.jar`가 프로젝트에 누락되어 있으면, 같은 리포지토리 내 다른 프로젝트에서 복사하거나 전역 Gradle을 사용하여 wrapper를 생성할 수 있습니다.

### 환경 변수/프로퍼티(런타임)

실행(예: `./gradlew bootRun`) 시 구성값이 누락되어 서비스가 시작 실패할 수 있습니다. 예를 들어 `jwt.secret` 환경 변수 또는 프로퍼티가 없을 경우 필수 구성으로 간주됩니다.

런타임에서 `jwt.secret`이 누락되면 기본 값이 사용되도록 `JwtTokenProvider`에 기본값을 설정했습니다. 권장 방식은 환경변수를 통해 값을 주입하는 것입니다:

 - `jwt.secret`이 누락되면 기본 값이 사용되도록 `JwtTokenProvider`에 기본값을 설정했습니다. 그러나 **프로덕션 환경에서는 절대로 기본값을 사용하지 마세요.** 환경 변수를 통해 값을 주입해야 합니다. 예시(.env의 내용을 `backend/mini-job-service/.env.example`로 제공):

```bash
# 환경변수 설정 (Linux/macOS)
export JWT_SECRET="yourSuperSecureSecretHere"
export JWT_EXPIRATION=86400000
# 로컬 실행
./gradlew bootRun
```

또는 `.env`/도커/시스템 서비스에 환경변수로 제공하세요.

또는 `application.yml`에서 값을 수정해도 됩니다.

### Gradle 빌드 실패

- JDK 21이 설치되어 있는지 확인: `java -version`
- JAVA_HOME 환경변수가 올바르게 설정되어 있는지 확인
