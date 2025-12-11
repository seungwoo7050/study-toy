# Spring 엔터프라이즈 패턴 기초 가이드  
`spring-enterprise-patterns-basics.md`

> 이 문서는 **Spring으로 "서비스다운 서비스"를 만들 때 반복해서 쓰이는 패턴**을 정리한다.  
> 목표는 "코드 예쁘게"가 아니라 **도메인/계층/트랜잭션/보안 구조를 설계할 수 있는 상태**다.

---

## [필수] 0. 이 문서로 어디까지 가는가

### 0.1 최종 목표

이 문서를 끝까지 보면 최소한 아래를 설명할 수 있어야 한다.

1. **레이어드 / 헥사고날 아키텍처 차이**를 한 문단으로 정리할 수 있다.
2. 코드에서 **도메인 / 애플리케이션 / 인프라 계층을 명확히 나눌 수 있다.**
3. `@Transactional` 경계를 "감으로"가 아니라 **유스케이스 기준으로 설계할 수 있다.**
4. JWT + Spring Security 기반 인증 플로우를 **요청 단위로 추적해서 설명할 수 있다.**
5. **RBAC(Roles + Permissions)** 구조와 팀 단위 권한 모델을 설계할 수 있다.

### 0.2 학습 대상 프로젝트 예시

- 예: `spring-patterns`
  - Issue / Project / Team 같은 도메인을 가진 **업무용 서비스**
  - Phase 1: CRUD + JWT 로그인 + 팀 기반 RBAC
  - 이후 Phase에서 ES, Kafka, WebFlux 등으로 확장된다고 가정

이 문서는 **Phase 1을 설계·구현하기 위한 개념 가이드**를 목표로 한다.

### 0.3 선행 완료 전제

- `spring-rest-basics.md` 수준의 내용을 이해하고 있음
  - Controller / Service / Repository 레이어로 API를 쪼갤 수 있다.
  - JPA로 간단한 엔티티를 저장/조회해 본 상태.
- JWT 기본 개념을 알고 있다.
  - "토큰 안에 유저 정보 + 만료시간 넣고 서명해서 주고,  
    이후 요청에서 이 토큰을 검사한다" 정도 이해.

위가 안 돼 있으면:  
**먼저 Spring 기초 + JWT 기초 문서를 보고 오는 걸 권장.**

### 0.4 이 문서에서 다루지 않는 것들

의도적으로 다루지 않는 영역:

- Spring 내부 동작(Bean 라이프사이클, 프록시 생성 방식 등) 디테일
- 고급 JPA 튜닝 (N+1 완전 제거, 복잡한 성능 최적화)
- Kafka/Elasticsearch 클러스터 운영, 인프라 레벨 세팅
- 복잡한 분산 트랜잭션 / SAGA 패턴 구현

여기서는 **"서비스 코드 구조"** 관점에 집중한다.

---

## [필수] 1. 아키텍처 스타일 개요

### 1.1 레이어드 아키텍처 복습

보통 Spring 예제에서 많이 보는 구조:

- **Controller 레이어**
  - HTTP 요청/응답을 담당 (`@RestController`)
  - URL, 쿼리파라미터, 헤더 → Java 타입 매핑
  - HTTP status code, JSON 응답 포맷 결정
  - **비즈니스 규칙은 최대한 넣지 않는다.**

- **Service 레이어**
  - "유스케이스 단위" 작업을 묶는 곳
  - 트랜잭션 경계가 보통 여기 있다.
  - 여러 Repository / 외부 시스템 호출을 조합해서 하나의 작업으로 만든다.

- **Repository 레이어**
  - DB 접근 캡슐화 (`JpaRepository`, 커스텀 Repository)
  - `findById`, `save`, `delete` 등 데이터 저장/조회 책임

의존 방향:

```text
Controller -> Service -> Repository -> DB
```

**문제 되는 패턴**

* Controller에서 JPA 엔티티를 직접 조작
* Service 하나가 "프로젝트 전체"를 담당해서 2~3000라인이 넘어감
* Repository에서 도메인 로직(검증, 상태전이)을 처리

이 문서의 나머지 내용은 이런 안티패턴을 피하기 위한 구조를 만드는 이야기다.

---

### 1.2 DDD(Domain-Driven Design) 간단 개념

DDD 전체를 할 필요는 없고, 이 정도만 가져온다고 보면 된다.

* **엔티티(Entity)**

  * 식별자(키)로 구분되는 도메인 객체
  * 시간에 따라 상태가 변한다.
  * 예: `Issue`, `Project`, `Team`, `User`

* **밸류 오브젝트(Value Object)**

  * 값으로 비교되는 객체
  * 불변(immutable)인 경우가 많다.
  * 예: `IssueTitle`, `EmailAddress`, `Money`

* **도메인 서비스(Domain Service)**

  * 특정 엔티티 하나에 두기 애매한 **도메인 규칙**을 담는 서비스
  * "여러 엔티티가 엮이는 규칙"이 여기에 온다고 보면 된다.
  * 예: "팀 권한 규칙", "특정 상황에서 이슈 상태 전환 가능 여부"

핵심은:

> **"코드 구조를 HTTP/DB가 아니라, 비즈니스 개념 기준으로 자른다."**

---

### 1.3 헥사고날(포트/어댑터) 아키텍처 개념

헥사고날(Port & Adapter)의 골자는 단순하다.

* **도메인 코어**

  * 비즈니스 규칙만 있다. HTTP, DB, Kafka 같은 건 모른다.
* **포트(Ports)**

  * 도메인이 외부 세계에 기대는 인터페이스
  * 예: `IssueRepository`, `NotificationSender`
* **어댑터(Adapters)**

  * 실제 구현체 (JPA, REST 클라이언트, Kafka producer 등)
  * 포트 인터페이스를 구현해서, 인프라 기술을 숨긴다.

그림으로 단순화하면:

```text
[HTTP Controller] --+
[Message Listener] --+--> [Application/Domain(Core)] --> [Ports] --> [Adapters] --> DB/Kafka/...
```

의미:

* 코어는 "Issue를 생성하고, 권한을 검사한다" 같은 **도메인 로직만** 알면 된다.
* HTTP, 메시지 큐, DB, 캐시 등은 **어댑터**가 책임진다.

---

### 1.4 Spring에서의 현실적인 타협점

현실에서 "순수 헥사고날"을 지키는 프로젝트는 거의 없다.
그래서 다음 정도를 **현실적인 목표**로 잡는다.

* 패키지 구조에서:

  * `domain`, `application`, `infrastructure`를 **최소한 구분**한다.
  * 하지만 Controller는 그냥 `api` 또는 `presentation` 밑에 둔다.
* 도메인 계층에서:

  * Spring 프레임워크 타입(`@Transactional`, `EntityManager`) 의존을 피한다.
* 인프라 계층에서:

  * JPA 엔티티, Spring Data 인터페이스 등 **프레임워크 의존을 몰아넣는다.**

현업에서 자주 쓰는 적당한 타협 예:

```text
com.example.issue
  ├─ domain/
  ├─ application/
  ├─ infrastructure/
  └─ presentation/   (REST Controller)
```

이 문서의 이후 섹션은 이 구조를 전제로 설명한다.

---

### 1.5 실습 1: 단순 CRUD 모듈 레이어/패키지 구조 나누기

직접 해볼 것:

1. "Issue 관리" 정도의 작은 도메인 하나를 고른다.
2. 아래 네 패키지를 직접 만들어본다.

   * `issue.domain`
   * `issue.application`
   * `issue.infrastructure`
   * `issue.presentation`
3. 기존에 만들었던 `IssueController`, `IssueService`, `IssueRepository`를 위 패키지로 재배치해보면서,

   * 컨트롤러: `presentation`
   * 서비스: `application`
   * JPA 엔티티, JPA 리포지토리: `infrastructure`
   * **도메인 규칙/상태전이 로직**: `domain`
     로 옮겨보면서 역할을 다시 한 번 생각해본다.

구조를 옮기는 것만으로도 "무슨 코드가 어디에 속해야 하는지" 감이 잡힌다.

---

## [필수] 2. 도메인 / 애플리케이션 / 인프라 계층

### 2.1 도메인 계층

**역할**

* 비즈니스 개념/규칙을 표현하는 계층.
* HTTP, DB, 메시지 브로커를 모른다고 가정한다.

**포함되는 것들**

* 엔티티, 밸류 오브젝트
* 도메인 서비스
* 도메인 이벤트(필요하다면)

예시 (Issue 도메인):

```java
// domain
public class Issue {

    private final IssueId id;
    private IssueStatus status;
    private String title;
    private String description;

    public void changeTitle(String newTitle) {
        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("제목은 비어있을 수 없습니다.");
        }
        this.title = newTitle;
    }

    public void close() {
        if (status == IssueStatus.CLOSED) {
            throw new IllegalStateException("이미 닫힌 이슈입니다.");
        }
        this.status = IssueStatus.CLOSED;
    }
}
```

여기에는:

* `@Entity`, `@Table` 같은 JPA 어노테이션도 안 들어가는 쪽이 이상적이지만,
* 현실에서는 **JPA 엔티티를 도메인 엔티티로 그대로 쓰기도 한다.**

중요한 건:

> "HTTP/DB/보안 같은 인프라 이슈를 최대한 이 계층에 끌고 들어오지 않는다."

---

### 2.2 애플리케이션 계층

**역할**

* **유스케이스 단위**로 도메인 객체와 인프라를 orchestration 하는 계층.
* 트랜잭션 경계가 대부분 여기 있다.

예: "이슈 생성" 유스케이스

```java
// application
@Service
public class CreateIssueService {

    private final IssueRepository issueRepository;
    private final PermissionChecker permissionChecker;

    @Transactional
    public IssueId createIssue(CreateIssueCommand command) {
        // 1. 권한 검사
        if (!permissionChecker.canCreateIssue(command.getUserId(), command.getProjectId())) {
            throw new AccessDeniedException("권한 없음");
        }

        // 2. 도메인 객체 생성
        Issue issue = new Issue(
            IssueId.newId(),
            command.getTitle(),
            command.getDescription()
        );

        // 3. 저장
        issueRepository.save(issue);

        return issue.getId();
    }
}
```

애플리케이션 계층의 특징:

* HTTP 세부사항(헤더, DTO 직렬화)은 모른다.
* DB 세부사항(JPA query, SQL)은 모른다.
* **도메인 규칙을 조합해서 "하나의 작업"으로 묶는 역할**만 한다.

---

### 2.3 인프라 계층

**역할**

* DB, 메시지 브로커, 외부 API 등 **기술 의존성**을 다루는 계층.
* 도메인/애플리케이션에서 정의한 포트(인터페이스)를 구현한다.

예: 도메인에서 정의한 Repository 인터페이스

```java
// domain
public interface IssueRepository {
    Optional<Issue> findById(IssueId id);
    void save(Issue issue);
}
```

인프라 계층에서의 구현:

```java
// infrastructure
@Repository
public class JpaIssueRepository implements IssueRepository {

    private final SpringDataIssueRepository jpa;

    public JpaIssueRepository(SpringDataIssueRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Issue> findById(IssueId id) {
        return jpa.findById(id.getValue());
    }

    @Override
    public void save(Issue issue) {
        jpa.save(IssueEntity.from(issue));
    }
}
```

여기서:

* `SpringDataIssueRepository`는 `JpaRepository`를 상속한 **프레임워크 의존 인터페이스**.
* `IssueEntity`는 DB 테이블과 직접 매핑되는 엔티티.

이렇게 쪼개면:

* 도메인/애플리케이션 코드는 `JpaRepository`를 몰라도 된다.
* 나중에 DB를 바꾸거나, 별도 읽기 모델을 추가하기 쉬워진다.

---

### 2.4 Spring에서의 매핑 방식

현실적인 패키지 예:

```text
com.example.issue
  ├─ domain
  │   ├─ Issue.java
  │   ├─ IssueId.java
  │   ├─ IssueRepository.java
  │   └─ PermissionChecker.java
  ├─ application
  │   ├─ CreateIssueService.java
  │   └─ CloseIssueService.java
  ├─ infrastructure
  │   ├─ JpaIssueRepository.java
  │   ├─ SpringDataIssueRepository.java
  │   └─ IssueEntity.java
  └─ presentation
      └─ IssueController.java
```

Spring 관련 어노테이션:

* `@Service`, `@Transactional` → 주로 `application`에 둔다.
* `@Repository`, `@Entity`, `JpaRepository` → `infrastructure`.
* `@RestController`, `@RequestMapping`, DTO 변환 → `presentation`.

**도메인 계층**에는:

* 최대한 순수 Java 코드만 남기는 방향으로 잡는다.
* 필요하면 `@Component` 정도는 허용하되, 프레임워크 의존이 커지지 않도록 관리한다.

---

### 2.5 실습 2: 계층별 패키지 트리 설계

직접 해볼 것:

1. 이미 있는 Spring 프로젝트에서 **기능 하나**만 고른다.

   * 예: "이슈 관리", "팀 관리", "게시글 관리"
2. 위 예시처럼 `domain / application / infrastructure / presentation` 패키지를 만든다.
3. 클래스 5~10개 정도만:

   * 어느 계층이 맞는지 우선 종이에 분류해보고,
   * 실제 패키지로 옮겨 본다.
4. 옮기면서:

   * "이 클래스가 이 계층에 있는 게 맞나?"
   * "이 의존성(Spring/JPA/HTTP)이 도메인 쪽으로 새고 있지 않나?"
     를 한 번씩 체크해본다.

실제로 옮겨보면, 어떤 코드가 "중심"이어야 하는지가 꽤 분명해진다.

---

## [필수] 3. 트랜잭션과 일관성

### 3.1 @Transactional 기본 개념

`@Transactional`은 **하나의 작업 단위를 DB 트랜잭션으로 묶는다.**

기본적인 사용 패턴:

* **애플리케이션 서비스 메서드**에 붙인다.
* 그 메서드 안에서 수행되는 JPA 작업들이 모두 하나의 트랜잭션으로 묶인다.
* 예외(런타임 예외) 발생 시 전체 롤백.

주의할 점:

* Spring의 `@Transactional`은 **프록시 기반**이므로,

  * 같은 클래스 내부의 메서드끼리 호출할 때는 트랜잭션 경계가 새로 안 생긴다.
  * 보통 "public 서비스 메서드"를 경계로 사용한다.

---

### 3.2 "1 요청 = 1 트랜잭션" 패턴

가장 기본적인 패턴:

* 한 HTTP 요청이 하나의 유스케이스 = 하나의 트랜잭션.

  * 예: `POST /issues` → `CreateIssueService.createIssue(...)` → @Transactional

장점:

* 단순하다.
* 한 요청 안에서의 일관성이 보장된다.
* 디버깅도 "요청 단위"로 보면 된다.

주의:

* 한 요청 안에 **너무 많은 작업**을 넣으면 트랜잭션이 길어진다.

  * 락 오래 유지, DB 부하 증가, 타임아웃 리스크.
* 트랜잭션 경계는 되도록 **Service 메서드 내부에서 짧게** 유지하는 게 좋다.

---

### 3.3 쓰기와 읽기 분리 개념

엔터프라이즈 서비스에서는:

* "쓰기" 유스케이스와
* "읽기" 유스케이스의 요구사항이 다를 때가 많다.

간단한 분리 패턴:

* **쓰기 서비스**

  * `@Transactional`
  * 도메인 로직 + Repository를 엮는 애플리케이션 서비스
* **읽기 서비스**

  * `@Transactional(readOnly = true)` 또는 아예 트랜잭션 없이
  * DTO projection / 조회 전용 쿼리 위주

예:

```java
@Service
public class IssueQueryService {

    private final IssueReadRepository readRepository;

    @Transactional(readOnly = true)
    public IssueDetailView getIssue(Long id) {
        return readRepository.findDetailView(id);
    }
}
```

여기서 핵심은:

* **읽기 모델**을 별도로 두면,
  나중에 Elasticsearch나 캐시를 붙일 때 구조가 덜 꼬인다.
* 반대로 "쓰기 로직"은 도메인/애플리케이션 계층에서,
  트랜잭션 경계 하나로 묶어 관리한다.

---

### 3.4 통합 트랜잭션 vs 이벤트 기반 최종 일관성

한 유스케이스 안에서:

* **하나의 DB**만 건드릴 때:

  * "1 요청 = 1 트랜잭션"으로 끝낼 수 있다.
* **여러 시스템**(다른 DB, 메시지 브로커, 외부 API 등)을 건드려야 하면:

  * 하나의 글로벌 트랜잭션으로 묶기 어렵다.
  * 현실적으로는 **이벤트 기반 최종 일관성** 패턴을 쓴다.

간단한 예:

1. Issue 생성 시:

   * DB에 Issue INSERT
   * "IssueCreatedEvent" 발행 (트랜잭션 안에서 동일 DB에 이벤트 로그 남기거나, 메시지 브로커에 전송)
2. 별도의 소비자(Consumer)가:

   * IssueCreatedEvent를 받아 통계/검색 인덱스 등 **2차 작업**을 수행

이 패턴의 의미:

* "Issue 자체가 저장되는 것"과
* "검색 인덱스/통계가 업데이트되는 것"의 시간차를 허용한다.
* 대신 코드 구조가 훨씬 단순해진다.

이 문서에서는 개념만 잡고,
실제 "이벤트 저장/발행 코드"는 심화 문서에서 다룬다고 보면 된다.

---

### 3.5 배치/비동기 처리와 트랜잭션

배치/비동기 처리 시에는 트랜잭션이 **요청 단위**가 아니라:

* 배치 잡 실행 단위
* 메시지 1개 처리 단위

로 바뀐다.

예:

* `@Scheduled` 배치:

  * 잡 메서드에 `@Transactional`을 붙이면,
    그 메서드 전체가 한 트랜잭션이 된다.
* 메시지 리스너(Kafka, MQ 등):

  * "메시지 1개 처리" 또는 "배치로 가져온 N개 처리"를
    트랜잭션 단위로 본다.

중요한 포인트:

* **트랜잭션 경계를 어디에 두느냐가 장애 시 재시도/보상 전략에 바로 연결된다.**
* 이 섹션에서 알아둘 것은:

  * HTTP 요청이 아닌 경우에도 "작업 단위"를 정의하고,
  * 그걸 기준으로 @Transactional을 붙여야 한다는 점.

---

### 3.6 실습 3: 트랜잭션 경계 비교 설계

직접 해볼 것:

1. 아래 두 유스케이스에 대해,

   * 어떤 메서드에 `@Transactional`을 붙일지,

   * 트랜잭션 안/밖에서 어떤 일이 일어날지 적어본다.

   1. 단순 Issue 생성

   * 제목/내용으로 Issue 생성 후 저장

   2. Issue 생성 + 검색 인덱스 업데이트

   * Issue 저장 후, 검색 인덱스(예: ES)에도 반영

2. 두 번째 케이스에서:

   * "Issue 저장"과 "검색 인덱스 업데이트"를 **같은 트랜잭션**으로 끌고 갈 것인지,
   * Issue 저장만 트랜잭션에 넣고, 검색 인덱스는 **이벤트 기반 비동기 작업**으로 보낼 것인지
     두 가지 버전을 그려본다.

3. 각각에 대해:

   * 장애 시 롤백/재시도 전략이 어떻게 달라지는지 간단히 적어본다.

이 연습만 해도 "어디까지를 한 트랜잭션으로 묶을지" 감이 많이 잡힌다.

---

## [필수] 4. 인증 / 인가 / RBAC 패턴

### 4.1 JWT 기반 인증 플로우 개요

기본 플로우:

1. 로그인 요청 (`POST /auth/login`)

   * 이메일/비밀번호 또는 OAuth 토큰 등으로 인증.
2. 서버:

   * 사용자 검증 후, **JWT Access Token** 발급.
   * `sub`(userId), `roles`, `exp`(만료시각) 등을 넣고 서명.
3. 클라이언트:

   * 이후 요청에 `Authorization: Bearer <JWT>` 헤더로 토큰 첨부.
4. 서버:

   * **필터 단계에서 JWT 검증**.
   * 유효하면 `Authentication` 객체를 만들어 `SecurityContext`에 저장.
   * 컨트롤러/서비스에서 `@AuthenticationPrincipal` 등으로 유저 정보 사용.

중요한 점:

* "로그인 성공 → 토큰 발급"과
  "토큰 검증 → 인증된 요청 처리"는 서로 다른 흐름이다.
* Spring Security는 **주로 후자(요청 처리 시 토큰 검증)**를 담당한다.

---

### 4.2 Spring Security 필터 체인 개념

Spring Security는 **필터 체인**으로 동작한다.

* `SecurityFilterChain` bean 하나가
  "어떤 요청에 어떤 보안 규칙을 적용할지" 정의.
* JWT 기반 구조에서 자주 쓰는 형태:

  * `UsernamePasswordAuthenticationFilter` 이전에,
    커스텀 `JwtAuthenticationFilter` 또는 `OncePerRequestFilter`를 둔다.
  * 이 필터가:

    * 헤더에서 JWT 추출
    * 검증/파싱
    * `UsernamePasswordAuthenticationToken` 생성
    * `SecurityContextHolder.getContext().setAuthentication(...)`

대략 흐름:

```text
HTTP 요청
  -> (JwtFilter: 토큰 검증)
  -> (Authorization Filter: 권한 체크)
  -> Controller
```

이 문서에서는 Filter 구현 디테일보다,

> "요청이 필터 체인을 거치면서 인증/인가가 어디서 수행되는지" 흐름만 잡으면 된다.

---

### 4.3 Authentication / Principal / SecurityContext 역할

Spring Security에서 최소한 알아야 할 타입:

* **Authentication**

  * "이 요청의 인증 상태"를 담고 있는 객체
  * `isAuthenticated()`, `getPrincipal()`, `getAuthorities()` 등 제공

* **Principal**

  * 도메인 관점의 "현재 사용자"
  * JWT에서 읽어온 userId, email, roles 등을 담은 커스텀 타입을 쓰는 경우가 많다.

* **SecurityContext**

  * `Authentication`을 보관하는 컨텍스트
  * 보통 `SecurityContextHolder`를 통해 ThreadLocal에 저장된다.

흐름:

1. 필터에서 JWT 검증 후, `Authentication` 객체 생성
2. `SecurityContextHolder.getContext().setAuthentication(auth)`
3. Controller/Service에서:

   * `@AuthenticationPrincipal` 또는 `SecurityContextHolder.getContext().getAuthentication()`으로 현재 유저 조회

핵심은:

> "현재 유저 정보는 Parameter로 안 흘려도, SecurityContext에서 꺼낼 수 있다."
> 다만, **도메인/애플리케이션 계층에는 SecurityContext 의존을 최대한 안 끌고 들어가는 게 좋다.**

---

### 4.4 RBAC 구조 (Role / Permission / Resource)

RBAC의 기본 구성 요소:

* **User**
* **Role (역할)**

  * 예: `ROLE_ADMIN`, `ROLE_MEMBER`, `ROLE_GUEST`
* **Permission (권한)**

  * 예: `ISSUE_READ`, `ISSUE_WRITE`, `TEAM_MEMBER_MANAGE`
* **Resource**

  * 예: `Issue`, `Team`, `Project` 등

간단한 구조:

* User ↔ Role (N:N)
* Role ↔ Permission (N:N)

Spring Security에서 가장 단순한 패턴:

* `GrantedAuthority`를 Role 이름으로 사용 (`"ROLE_ADMIN"` 등)
* `@PreAuthorize("hasRole('ADMIN')")`

하지만 엔터프라이즈 서비스에서는:

* 권한이 "팀/조직 단위로 달라지는" 경우가 많기 때문에,
* 단순 Role만으로는 부족해진다 → 다음 섹션의 Team 기반 모델로 확장.

---

### 4.5 팀(조직) 단위 권한 모델

예상되는 요구사항:

* "같은 서비스라도, 각 **Team마다 역할이 다르다**"

  * 팀 A에서 Admin, 팀 B에서는 Member
  * 팀마다 접근 가능한 Issue/Project 범위가 다르다.

간단한 모델 예:

* **User**
* **Team**
* **TeamMember**

  * 어느 User가 어느 Team에 속하는지
* **TeamRole**

  * 예: `OWNER`, `ADMIN`, `MEMBER`, `VIEWER`
* **TeamPermission**

  * Role별 권한 맵핑 (예: OWNER는 전부, MEMBER는 읽기+쓰기, VIEWER는 읽기만)

Spring 코드에서는:

* "현재 요청에 대해 어떤 teamId 를 기준으로 권한을 평가할지"를 정해야 한다.

  * URL Path나 QueryParam, Body, JWT 클레임에서 teamId를 읽어온다.
* Permission 체크 로직은 **애플리케이션/도메인 계층의 서비스**로 뺀다.

  * 예: `TeamPermissionChecker.canManageIssue(userId, teamId)`

이렇게 해두면, 권한 규칙이 바뀔 때
Controller나 SecurityConfig를 크게 건드리지 않고 **도메인 로직만 수정**하면 된다.

---

### 4.6 @PreAuthorize / SpEL 기반 접근 제어

Spring Security는 `@PreAuthorize`에서 SpEL로 조건을 지정할 수 있다.

간단한 예시:

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/users")
public List<UserDto> listUsers() { ... }
```

팀 단위 권한 예시:

```java
@PreAuthorize("@teamPermissionEvaluator.canAccessTeam(#teamId)")
@GetMapping("/teams/{teamId}/issues")
public List<IssueDto> listIssues(@PathVariable Long teamId) {
    ...
}
```

여기서:

* `@teamPermissionEvaluator`는 Spring Bean 이름.
* `canAccessTeam(Long teamId)` 메서드는 내부에서:

  * SecurityContext에서 현재 userId를 읽고,
  * TeamMember/TeamRole 정보를 조회해 권한을 판단.

패턴 요약:

* 복잡한 권한 로직은 **SpEL 문자열 안에 다 쓰지 말고**,
  별도 Bean 메서드로 위임한다.
* SpEL 쪽에는 **"어떤 Bean의 어떤 메서드를 부를지"만** 남겨둔다.

---

### 4.7 실습 4: 간단 Role/Permission 모델 + SecurityConfig 설계

직접 해볼 것:

1. 아래 같은 도메인 요구사항을 가정한다.

   * User는 여러 Team에 속할 수 있다.
   * Team 안에서 Role: OWNER / MEMBER / VIEWER
   * Issue에는 "소속 Team"이 있다.
   * 규칙:

     * OWNER, MEMBER: 팀 내 Issue 읽기/쓰기 가능
     * VIEWER: 팀 내 Issue 읽기만 가능

2. **도메인 모델만** 설계해 본다.

   * `User`, `Team`, `TeamMember`, `TeamRole`, `Issue` 클래스/엔티티
   * 최소한의 필드/연관관계만 스케치

3. 권한 체크 서비스를 설계한다.

   * 인터페이스 이름 예: `TeamPermissionChecker`
   * 메서드 예:

     * `boolean canReadIssue(UserId userId, IssueId issueId)`
     * `boolean canWriteIssue(UserId userId, IssueId issueId)`

4. Controller 수준에서의 사용 예를 적어본다.

   * `@PreAuthorize("@teamPermissionChecker.canWriteIssue(#issueId)")`
   * 또는 Service 내부에서 직접 `TeamPermissionChecker` 호출

5. 마지막으로 SecurityConfig에:

   * JWT 필터 설정
   * 기본 `authorizeHttpRequests` 규칙
     정도를 어떻게 둘지 간단한 스켈레톤을 그려본다.

이 정도까지 설계해보면,
`spring-patterns` Phase 1에서 나올 **JWT + 팀 RBAC 구조**를 읽고 이해하는 데 무리는 없다.

---

## [심화] 5. 읽기/쓰기 분리와 검색(Elasticsearch)

### 5.1 RDB와 검색 엔진 역할 분리

RDB(Postgres, MySQL 등)와 검색 엔진(Elasticsearch 등)은 역할이 다르다.

- **RDB**
  - 정합성(트랜잭션), 정규화, 조인, 참조 무결성이 강점
  - "쓰기 중심" + "정확한 조회"에 적합
- **검색 엔진**
  - 역색인, full-text 검색, 점수 계산, 집계(aggregation)에 최적화
  - 약한 정합성(최종 일관성 수준)을 전제로 함

실무 기준 간단 기준:

- "id나 키로 단건/리스트 조회" → RDB
- "검색창/필터/정렬/통계" → 검색 엔진이 맞는다.

### 5.2 쓰기 모델 / 읽기 모델 개념

**쓰기 모델(Write Model)**

- 도메인 규칙을 지키며 상태를 바꾸는 용도
- RDB + 도메인/애플리케이션 계층이 중심
- 예: Issue 생성/수정/삭제, 팀/멤버 관리

**읽기 모델(Read Model)**

- 조회/검색을 위해 최적화된 구조
- RDB view / 전용 테이블 / Elasticsearch index 등
- 예: Issue 리스트 화면, 대시보드, 통계 페이지

보통 구조는:

- 쓰기: RDB 도메인 모델
- 읽기: 검색 인덱스 or denormalized 테이블

을 분리하는 것부터 시작한다.

### 5.3 인덱싱 전략 (동기 / 비동기 / 배치)

**1) 동기 인덱싱**

- 트랜잭션 안에서 바로 ES에 인덱싱 요청
- 장점: 요청이 끝날 때 RDB/ES가 거의 일치
- 단점: ES 장애가 전체 요청 실패로 이어짐, latency 증가

**2) 비동기 인덱싱 (추천)**

- RDB에만 쓰고, 이벤트/로그를 남긴 뒤
- 별도 워커가 이벤트를 소비해서 ES에 인덱싱
- 장점:
  - 본 요청의 latency, 장애 영향이 낮아짐
- 단점:
  - "RDB에는 있는데 검색에는 아직 안 보이는" 찰나의 구간 존재

**3) 배치 인덱싱**

- 밤에 한 번, 혹은 일정 주기마다 대신 인덱싱 작업
- 대량 마이그레이션이나 초기 구축 시 사용
- 실시간성이 낮아도 되는 통계/대시보드 등에서 사용

이 가이드 기준 추천:

- 실시간성이 아주 중요하지 않다면 **비동기 인덱싱**을 기본으로 두고,
- 대량 수정/백필은 배치로 분리.

### 5.4 검색 API 설계

검색 API에 최소로 들어가는 요소:

- **필터**
  - 상태, 작성자, 팀, 태그…
- **정렬**
  - 최신순, 오래된 순, 중요도, 점수…
- **페이지네이션**
  - `page`, `size` or `cursor`
- **검색어**
  - 제목/내용 full-text

HTTP 관점에서:

```text
GET /api/issues/search
  ?query=...
  &status=OPEN
  &assigneeId=...
  &sort=createdAt,desc
  &page=0
  &size=20
```

내부 구현은:

* RDB로 할 수도 있고,
* ES로 할 수도 있고,
* 둘을 섞을 수도 있다.

**중요한 건 API 계약을 먼저 잘 정해두는 것**
→ 나중에 RDB→ES로 갈아탈 때에도 클라이언트가 안 깨진다.

### 5.5 실습 5: Issue/Article 검색 모델 스케치

직접 해볼 것:

1. `Issue` 도메인 기준으로 **검색 화면**을 상상한다.

   * 필터: 상태, 작성자, 팀, 생성일 범위…
   * 검색어: 제목+내용
   * 정렬: 최신순
2. 이 화면을 위해 필요한 **읽기 모델**을 정의해본다.

   * ES 인덱스 필드 목록
   * 혹은 RDB용 전용 View/테이블 컬럼 목록
3. **인덱싱 타이밍**을 적어본다.

   * Issue 생성/수정/삭제 시 어떤 이벤트를 발생시키고,
   * 어느 쪽에서(동기/비동기/배치) 인덱스를 갱신할지.

---

## [심화] 6. 비동기 처리와 이벤트 (Kafka 등)

### 6.1 이벤트 발행/구독 개념

**이벤트(Event)** = "이미 일어난 사실"에 대한 기록.

예:

* `IssueCreated`
* `IssueClosed`
* `TeamMemberAdded`

**발행/구독 패턴**

* 발행자(Producer): 이벤트 발생 시 "이런 일이 있었다"를 기록
* 구독자(Consumer): 이벤트를 보고 **추가 작업**(알림, 통계, 인덱싱 등)을 실행

핵심:

* 메인 유스케이스 코드에서 "부가 작업"을 떼어낼 수 있다.
* 시스템 간 결합도를 낮출 수 있다.

### 6.2 도메인 이벤트 vs 통합 이벤트

**도메인 이벤트**

* 도메인 모델 내부에서 발생하는 이벤트
* 예: `IssueClosedEvent(issueId, closedAt)`

**통합 이벤트(Integration Event)**

* 다른 시스템/서비스와 통합할 때 사용하는 이벤트
* 예: `IssueClosedIntegrationEvent(issueId, teamId, severity)`

실무에서 자주 하는 타협:

* 일단 도메인 이벤트와 통합 이벤트를 **같은 모델**로 쓰되,
* 필요해지면 별도 타입으로 나눈다.

이 가이드에서는:

* "도메인 계층에서 이벤트 객체 만들어서 발행 →
  인프라/워커에서 받아서 처리" 정도만 이해하면 충분.

### 6.3 Kafka를 어디에 쓰는지

Kafka(또는 RabbitMQ, Pulsar 등)를 쓰는 대표 상황:

* **로그/트래킹**

  * 유저 행동 로그, 요청 로그를 비동기 수집
* **비동기 업무 처리**

  * 이메일 전송, 알림, 통계 업데이트, 인덱싱, 청구 등
* **시스템 간 통합**

  * 마이크로서비스 간 이벤트 기반 통신

`spring-patterns` 관점에서는:

* Issue/Team/Member 변화 이벤트 →
  통계/검색/알림/감사 로깅 등으로 흘려보내는 용도로 보는 게 적당.

### 6.4 재시도 / 실패 처리 / DLQ 개념

비동기 처리에서 중요한 것:

* 메시지를 받았을 때 처리에 실패하면?

  * 재시도 횟수
  * 재시도 간격
  * 결국 처리 못하는 메시지를 어디로 보내는지

**DLQ(Dead Letter Queue)**

* 여러 번 실패한 메시지를 따로 모아두는 큐/토픽
* 운영자가 수동으로 조사/복구할 수 있게 한다.

최소 패턴:

1. Consumer에서 예외 발생 시 N번까지 재시도
2. 그래도 실패하면 DLQ로 메시지 이동
3. DLQ를 별도 모니터링 / 대시보드에서 확인

HTTP 요청과의 차이:

* 요청은 실패하면 클라이언트가 바로 알지만,
* 이벤트 소비는 실패를 클라이언트에 직접 보여줄 수 없다.
* 대신 **"언젠가 처리된다"는 최종 일관성 모델**에서 재시도/DLQ가 중요해진다.

### 6.5 실습 6: IssueCreatedEvent → 통계 업데이트 시나리오 설계

직접 해볼 것:

1. 이벤트 정의

   * `IssueCreatedEvent(issueId, projectId, createdAt, creatorId, priority, tags...)`
2. 발행 시점

   * Issue 생성 트랜잭션 안에서?
   * 트랜잭션 이후에?
3. 소비자 시나리오

   * 소비자가 `IssueStats` 테이블을 업데이트한다.
   * (예: 프로젝트/우선순위/날짜별 카운트)
4. 실패 시나리오

   * DB 일시 장애, 네트워크 장애 등
   * 재시도 횟수, DLQ 설계
5. 이 모든 걸 코드나 시퀀스 다이어그램으로 스케치해 본다.

---

## [심화] 7. 캐시 / Redis 패턴

### 7.1 캐싱 목적과 전략

캐시의 목적:

* **느린 연산/IO 결과를 재사용**해서 응답 시간과 부하 줄이기

기본 전략:

* 읽기 많은 데이터
* 자주 바뀌지 않는 데이터
* 계산 비용이 큰 데이터

에 캐시를 붙인다.

### 7.2 @Cacheable / @CacheEvict 패턴

Spring의 단순 캐싱 패턴:

* `@Cacheable`

  * 메서드 결과를 캐시에 저장
  * 같은 키로 다시 호출하면 메서드를 실행하지 않고 캐시 값을 반환
* `@CacheEvict`

  * 캐시 무효화(삭제) 담당

예:

```java
@Cacheable(cacheNames = "teamPermissions", key = "#teamId + ':' + #userId")
public PermissionSet getPermissions(Long teamId, Long userId) {
    // DB 조인 여러 번 하는 비싼 연산
}
```

```java
@CacheEvict(cacheNames = "teamPermissions", key = "#teamId + ':' + #userId")
public void changeTeamRole(Long teamId, Long userId, TeamRole newRole) {
    // 역할 변경 로직
}
```

**주의**: 캐시를 쓰면 **정합성(언제 최신 정보가 반영되느냐)**이 새로운 문제로 등장한다.

### 7.3 분산 캐시로서 Redis 위치

단일 JVM 안의 캐시(ehcache, Caffeine 등)로는:

* 인스턴스가 여러 대일 때 캐시가 인스턴스마다 따로 생긴다.
* 스케일 아웃/롤링 업데이트 시 복잡해진다.

그래서:

* Redis 같은 분산 캐시를 쓰면

  * 서버 여러 대가 같은 캐시를 공유.
  * 캐시 무효화가 단순해진다.

Spring에서는:

* `spring-data-redis` + 캐시 설정으로 쉽게 연결 가능.

### 7.4 캐시 키 설계

캐시 키는 **"무엇을 기준으로 결과가 달라지는지"**를 명확히 반영해야 한다.

예:

* 팀 권한:

  * 키: `teamId:userId`
* Issue 검색 결과:

  * 키: `query:status:page:size` (조합이 많으면 캐시 효과 떨어질 수 있음)

룰:

* 키는 사람이 봐도 이해 가능한 문자열이면 좋다.
* 너무 많은 조합이 생기는 키(고카디널리티)는 캐시 적중률이 떨어진다.

### 7.5 캐시 일관성 이슈/전략

대표적인 패턴:

1. **Write-through**

   * 쓰기 시 DB와 캐시를 동시에 갱신
   * 단순하지만 구현이 번거로울 수 있다.

2. **Cache-aside (가장 흔함)**

   * 읽기:

     * 캐시에 없으면 DB에서 읽고, 캐시에 넣는다.
   * 쓰기:

     * DB만 갱신하고, 관련 캐시를 무효화(evict)하거나 갱신.

기본적으로 이 가이드에서는 **Cache-aside + TTL** 정도를 기준으로 생각하면 충분하다.

### 7.6 실습 7: 팀 단위 권한 캐시 설계

직접 해볼 것:

1. `TeamPermissionChecker` 빈이 있다고 가정하고,

   * `getPermissions(teamId, userId)` 메서드에 캐시를 붙인다면:

     * 캐시 이름
     * 캐시 키
       를 어떻게 설계할지 적어본다.
2. 역할 변경/팀 탈퇴/팀 삭제 시:

   * 어떤 키를 어떻게 무효화할지 케이스별로 적어본다.
3. TTL(만료 시간)을 어떻게 잡을지 정해본다.

   * 예: 5분, 30분 등

---

## [심화] 8. WebFlux / 가상 스레드 패턴

### 8.1 블로킹 vs 논블로킹 I/O 개념

* **블로킹 I/O**

  * 스레드가 IO 결과를 기다리며 "멈춰 있는" 모델
  * 전통적인 Spring MVC + 서블릿 방식
* **논블로킹 I/O**

  * IO 요청만 던져두고, 결과 준비되면 콜백/이벤트로 알려줌
  * WebFlux + Netty 기반

웹 서비스에서는:

* 동시 연결 수, 외부 API/DB 호출 패턴에 따라
  어느 쪽이 더 유리한지가 갈린다.

### 8.2 WebFlux를 고려할 만한 케이스

* **IO 대기 시간이 길고, 동시 연결 수가 높을 때**

  * 예: 스트리밍, SSE, 많은 방 실시간 업데이트
* **완전 논블로킹 콜스택을 유지할 수 있을 때**

  * DB, 외부 API 클라이언트까지 논블로킹이 준비되어 있어야 효과가 크다.

반대로:

* 내부 업무 시스템에서 동시 접속 적고,
* 복잡한 트랜잭션/동기 처리 많으면

→ MVC로 둬도 충분하고, WebFlux는 오히려 복잡도 추가다.

### 8.3 Reactor 기반 코드 구조 맛보기

WebFlux에서는 `Mono<T>`, `Flux<T>` 같은 **리액티브 타입**을 쓴다.

```java
@GetMapping("/issues/{id}")
public Mono<IssueDto> getIssue(@PathVariable Long id) {
    return issueQueryService.findById(id); // Mono<IssueDto>
}
```

서비스:

```java
public Mono<IssueDto> findById(Long id) {
    return issueRepository.findById(id) // Mono<Issue>
        .map(this::toDto);
}
```

리액터 체인에서는:

* 동기 코드처럼 `return`이 아니라,
* "연산을 나열해서 파이프라인을 구성"하는 방식으로 사고해야 한다.

이 문서에서는 그냥:

> "리액티브 스택은 코드 스타일/디버깅까지 다 달라지니,
> 필요할 때 의도적으로 도입해야 한다."

정도만 인지해두면 된다.

### 8.4 가상 스레드 개념

JDK의 가상 스레드(프로젝트 Loom):

* "가볍게 만들고, 많이 만들 수 있는 스레드" 개념
* 기존 블로킹 I/O 코드를 **그대로** 두고도 동시성을 크게 늘릴 수 있다.

장점:

* 기존 MVC/블로킹 코드 스타일을 유지하면서,
* 스레드 수 문제를 완화할 수 있다.

단점:

* 아직 모든 라이브러리/프레임워크가 100% 대응한 건 아니다.
* 운영/모니터링 관점에서 새로운 특성 고려 필요.

### 8.5 선택 기준: MVC / WebFlux / 가상 스레드

간단한 선택 가이드:

* "기존 Spring MVC + JPA 기반 업무 서비스"
  → 그냥 **MVC** 유지
* "극단적으로 IO많고, 완전 비동기 스택 준비 가능"
  → **WebFlux** 고려
* "동시성이 높은데, 코드 스타일은 그대로 유지하고 싶다"
  → **MVC + 가상 스레드** 고려

`spring-patterns` 같은 교육/포트폴리오용 레포에서는:

* Phase 1: MVC
* Phase 2/3에서 WebFlux/가상 스레드를 **"패턴 소개 수준"**으로 쓰는 게 현실적이다.

---

## [심화] 9. 테스트 전략

### 9.1 단위 / 통합 / 인수 테스트

간단 정의:

* **단위 테스트(Unit Test)**

  * 가장 작은 단위(도메인/서비스 메서드 등)를 외부 의존성 없이 검증
* **통합 테스트(Integration Test)**

  * DB/외부 시스템까지 붙인 상태로 "조합" 검증
* **인수 테스트(Acceptance / E2E)**

  * 실제 API/시나리오 단위 검증

비율에 대한 고전적인 추천:

* 단위 테스트 많게
* 통합 테스트 적당히
* 인수 테스트 꼭 필요한 시나리오만

### 9.2 계층별 테스트 대상

* **도메인 계층**

  * 순수 비즈니스 규칙, 상태 전이
  * JUnit 테스트로 로직만 다룬다 (DB/스프링 컨텍스트 없이)

* **애플리케이션 계층**

  * 유스케이스 단위
  * Mock Repository 또는 Testcontainers/임베디드 DB 사용

* **인프라 계층**

  * Repository/외부 API 어댑터
  * 실제 DB/외부 시스템(or 테스트 대역)과 통합 테스트

* **프레젠테이션 계층**

  * REST API 인수 테스트 (`WebTestClient`, `MockMvc` 등)

핵심:

> "도메인 로직은 도메인 테스트로,
> 인프라 문제는 인프라 테스트로 분리해서 잡는다."

### 9.3 Testcontainers / 임베디드 DB 활용 개요

통합 테스트에서:

* H2 같은 임베디드 DB를 쓰면,

  * 실제 프로덕션 DB(Postgres 등)와 동작이 다를 수 있다.
* Testcontainers:

  * Docker 컨테이너로 실제 DB를 띄워 테스트
  * CI에서 쉽게 돌릴 수 있는 패턴

간단한 전략:

* 도메인 테스트: JUnit + Mockito 정도
* 통합 테스트: Spring Boot Test + Testcontainers(Postgres, Redis 등)

### 9.4 실습 8: 도메인 서비스 테스트 케이스 설계

직접 해볼 것:

1. 팀 권한 도메인 서비스 하나를 고른다.

   * 예: `TeamPermissionChecker`
2. "권한 있음/없음" 케이스를 몇 개 정의한다.

   * OWNER, MEMBER, VIEWER 별로
3. 이걸 JUnit 테스트 메서드 목록으로 적어본다.
4. 실제 DB나 Spring 컨텍스트 없이,
   순수 Java 객체만으로 테스트할 수 있게 설계해본다.

---

## [심화] 10. 자주 하는 실수 & 안티패턴

### 10.1 Service 하나에 모든 로직 몰기

문제:

* `IssueService` 하나가 프로젝트 전체 비즈니스 로직을 다 들고 있음.
* 1000~3000라인 넘어가면 아무도 전모를 파악 못 한다.

대안:

* 유스케이스별/도메인별 서비스로 쪼갠다.

  * `CreateIssueService`, `CloseIssueService`, `IssueQueryService` 등
* "큰 단일 서비스"보다 "작은 서비스 여러 개"가 유지보수에 낫다.

### 10.2 Controller에서 비즈니스 로직 작성

문제:

* `@RestController` 안에서 Validation/권한체크/상태전이/DB 작업을 다 한다.
* 나중에 같은 로직을 다른 입구(API, 배치, 메시지 소비자)에서 재사용하기 어렵다.

대안:

* Controller는

  * 입력 파싱 + 간단한 유효성 체크 + 서비스 호출 + 응답 매핑
  * 여기까지만.

### 10.3 이벤트 남발 / 관리 부재

문제:

* "확장성을 위해 이벤트로 쪼개자" 하다가

  * 누가 어떤 이벤트를 소비하는지
  * 실패 시 어떻게 되는지
  * 순서를 어떤 보장으로 보는지
    아무도 모르게 된다.

대안:

* 이벤트는 "필요할 때만, 명확한 책임/소비자와 함께" 도입.
* 최소한 문서 하나에:

  * 이벤트 타입 목록
  * 발행 시점
  * 소비자 목록
  * 실패/재시도/DLQ 정책
    을 적어둔다.

### 10.4 캐시에 모든 걸 맡기려는 접근

문제:

* "DB 느리니까 캐시로 다 해결하자" → 캐시가 실제 소스 오브 트루스처럼 쓰이기 시작.
* 캐시 일관성/정합성 지옥.

대안:

* 캐시는 "성능 최적화 옵션"이고,
* **진짜 데이터 소스는 DB**라는 원칙 유지.
* 캐시가 깨져도 크게 문제없는 영역부터 도입.

---

## [심화] 11. FAQ / 자기점검

### 11.1 개념 체크 질문 리스트

스스로 답해보면 좋을 질문들:

1. 레이어드 아키텍처와 헥사고날 아키텍처의 차이를
   "의존 방향" 관점에서 설명할 수 있는가?
2. 도메인 / 애플리케이션 / 인프라 계층 각각의 책임을
   Issue/Team 같은 구체 예시로 말할 수 있는가?
3. `@Transactional`을 어디에 붙이는지,
   "1 요청 = 1 트랜잭션" 패턴의 장단점을 말할 수 있는가?
4. JWT 기반 인증에서:

   * 로그인 요청
   * 토큰 검증
   * 권한 체크
     가 각각 어디서 일어나는지(필터/서비스/도메인) 구분할 수 있는가?
5. RDB + Elasticsearch + Kafka + Redis의 역할을
   한 문단씩 설명할 수 있는가?
6. WebFlux/가상 스레드를 언제 고려하고,
   언제 아예 안 쓰는 게 나은지 판단 기준이 있는가?
7. 내 프로젝트의 테스트 전략을

   * 단위/통합/인수 비율
   * 계층별 테스트 범위
     관점에서 설명할 수 있는가?

### 11.2 프로젝트 진입 전 체크리스트

`spring-patterns` 같은 프로젝트에 들어가기 전에, 최소 이 정도는 "대충이라도" 체크:

* [ ] Controller / Service / Repository 패턴 말로 설명 가능
* [ ] 도메인/애플리케이션/인프라 패키지 구조를 손으로라도 그릴 수 있다
* [ ] Issue/Team처럼 하나의 도메인에 대해 트랜잭션 경계를 정해본 적 있다
* [ ] JWT + Spring Security로 인증 플로우 한 번은 구현해봤다
* [ ] 팀/조직 단위 RBAC 모델(TeamMember/Role) 스케치해봤다
* [ ] 읽기/쓰기 분리, 검색 인덱스, 이벤트/캐시 개념이 "듣도 보도 못한 것" 수준은 아니다

여기까지 괜찮다면,
이제 실제 `spring-patterns` 설계/코드 보면서 "이 패턴들을 어떻게 꽂아넣었는지" 비교해보면 된다.