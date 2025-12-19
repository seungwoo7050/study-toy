# Spring Boot REST API 기초 학습 문서 

## 0. 이 문서로 어디까지 가는가

최종 목표:

1. Spring Initializr로 Spring Boot 프로젝트 생성 (Gradle 또는 Maven)
2. `GET /hello`, `POST /echo` 같은 단순 REST API 작성
3. Controller / Service / Repository 레이어 분리
4. Postman으로 API 호출·검증
5. 선택: H2/JPA로 인메모리 Note API를 DB 기반으로 변경

학습 대상 프로젝트 예시 이름: `spring-notes-api` (메모/노트 API 서버)

---

## 1. Spring Boot를 배우기 전에 알아두면 좋은 기초

### 1.1 JDK 환경

**필수: JDK 11 이상 (실무는 보통 17 LTS 기준)**

#### 설치 확인

```bash
java -version
javac -version
```

* 버전이 11 미만이면 재설치
* `command not found` 나오면 PATH 문제

#### 자주 하는 실수 & 팁

* JRE만 설치하고 JDK는 설치 안 한 경우
* 여러 버전 설치 후 `JAVA_HOME`이 엉켜 있는 경우
* IDE에서 사용하는 JDK와 터미널에서 사용하는 JDK가 다른 경우

**팁**

* `JAVA_HOME`을 명시적으로 설정해두는 편이 문제 덜 생김
* IDE(특히 IntelliJ)에서 Project SDK, Gradle JVM이 어떤 버전을 쓰는지 확인

#### FAQ

* Q: JDK 8 써도 되나요?
  A: 오래된 프로젝트 아니면 굳이. 새로 시작이면 17 기준으로 맞추는 게 낫다.
* Q: JRE만 있으면 안 되나요?
  A: 컴파일에 `javac` 필요하니 JDK 필수.

---

### 1.2 Gradle / Maven 빌드 도구

Spring Boot는 보통 둘 중 하나를 사용:

* **Maven**

  * XML(`pom.xml`) 기반
  * 예제 많음
* **Gradle**

  * Groovy/Kotlin DSL(`build.gradle`) 기반
  * 속도/유연성 좋음

둘 다 해볼 필요는 없고, 하나 골라서 익히면 된다.

#### 래퍼 사용

프로젝트 안에는 보통 이런 파일이 같이 들어있다:

* Gradle: `gradlew`, `gradlew.bat`, `gradle/wrapper/*`
* Maven: `mvnw`, `mvnw.cmd`, `.mvn/wrapper/*`

**실행 예시 (Gradle 기준)**

```bash
./gradlew bootRun      # 애플리케이션 실행
./gradlew build        # 빌드
./gradlew test         # 테스트
```

**실행 예시 (Maven 기준)**

```bash
./mvnw spring-boot:run
./mvnw clean package
./mvnw test
```

#### 자주 하는 실수 & 팁

* `gradle` / `mvn` 전역 설치해놓고 래퍼 안 쓰는 경우 → 버전 차이로 꼬일 수 있음
* 윈도우에서 `./gradlew` 안 되고 `gradlew.bat`를 써야 하는데 헷갈림
* `build.gradle` / `pom.xml` 수정 후 **다시 import/동기화** 안 해서 IDE가 옛 설정 쓰는 경우

#### FAQ

* Q: Gradle이랑 Maven 중 뭘 써야 하나요?
  A: 팀 기준이 없으면 Gradle 추천. 예제는 Maven도 많으니 둘 다 읽을 수는 있어야 한다.
* Q: 래퍼를 꼭 써야 하나요?
  A: 프로젝트별로 버전 고정할 수 있어서 실무 기준으론 거의 필수.

---

### 1.3 개발 환경(IDE / VSCode)

**권장**

* IntelliJ IDEA (Community도 충분히 사용 가능)
* VSCode + Java / Spring 관련 확장

#### 기본 작업

* 프로젝트 열기 (Gradle/Maven 프로젝트로 인식시키기)
* `main` 클래스에서 실행
* 브레이크포인트 걸고 디버그 실행

#### 자주 하는 실수 & 팁

* Gradle 프로젝트인데 일반 Java 프로젝트로 열어버리는 경우
* JDK 설정 없이 프로젝트 열어서 오류 홍수
* Lombok 사용 시 플러그인/설정 없이 써서 IDE에만 빨간줄

**팁**

* 이 문서 단계에선 한 IDE에만 집중하는 게 낫다. (IntelliJ 기준으로 익혀두면 자료가 많음)

#### FAQ

* Q: IntelliJ 유료/무료 차이?
  A: 무료(Community)로 Spring Boot 개발 충분하다. (JPA 도구 등 일부 기능만 유료)
* Q: VSCode만 써도 되나요?
  A: 가능은 한데, Spring 사용 시 IntelliJ가 문서/예제가 많다.

---

### 1.4 HTTP 클라이언트(Postman 등)

Spring Boot로 서버를 띄우면, 브라우저 말고도 HTTP 클라이언트로 호출해야 한다.

**선택지**

* Postman
* IntelliJ HTTP Client (`.http` 파일)
* VSCode용 REST Client 확장
* cURL (터미널에서 빠르게 확인 가능)

#### Postman 기본

* Method: `GET`, `POST`, `DELETE` 등
* URL: `http://localhost:8080/hello`
* Headers: `Content-Type: application/json`
* Body (raw, JSON 선택):

```json
{
  "message": "hello"
}
```

#### 자주 하는 실수 & 팁

* POST인데 Body 탭 안 켜고 요청 보내놓고, 서버에서 `null` 들어왔다고 헤맴
* JSON 모양 깨져 있는데(콤마, 따옴표 등) 왜 400 뜨는지 모름
* 서버 포트 변경했는데 Postman에서 여전히 8080으로 보내는 경우

#### FAQ

* Q: 브라우저만으로 테스트하면 안 되나요?
  A: GET 정도는 가능하지만, POST/PUT/DELETE, JSON Body 테스트는 힘들다.
* Q: cURL이 더 편한데요?
  A: 쓰던 툴 있으면 그대로 써도 된다. 이 문서에서는 Postman 기준으로 설명.

---

## 2. Java 기본 문법 & OOP (Spring에 필요한 만큼)

### 2.1 클래스, 인터페이스, 구현체

Spring은 인터페이스 기반 설계가 기본 패턴이다.

#### 예시: MessageService

```java
public interface MessageService {
    String getMessage(String name);
}
```

```java
public class SimpleMessageService implements MessageService {

    @Override
    public String getMessage(String name) {
        return "Hello, " + name;
    }
}
```

컨트롤러/서비스/리포지토리도 이런 식으로 인터페이스/구현체로 나누는 패턴이 자주 나온다.

#### 자주 하는 실수 & 팁

* 인터페이스에 구현을 넣으려고 함 (Java 8 default 메서드는 특수 케이스일 뿐)
* 접근 제어자 아무 생각 없이 전부 `public`
* 데이터만 담는 클래스에 setter/getter 직접 다 타이핑하다가 귀찮아서 Lombok 도입 → Lombok 의존성/플러그인 설정 없이 쓰다가 IDE 빨간줄

#### FAQ

* Q: 굳이 인터페이스 만들어야 하나요?
  A: 작은 예제는 없어도 된다. 다만 Spring에서 DI/테스트/교체 가능성 생각하면 인터페이스 패턴이 자연스럽게 나온다.
* Q: record 써도 되나요?
  A: JDK 16+라면 DTO에는 record 쓰는 것도 괜찮다.

---

### 2.2 예외 처리

Spring MVC에서 예외는 HTTP 응답 코드로 이어진다. 기본은 500.

#### 기본 문법

```java
try {
    int value = Integer.parseInt("abc");
} catch (NumberFormatException e) {
    // 처리
    System.out.println("숫자가 아님: " + e.getMessage());
}
```

**checked vs unchecked**

* `RuntimeException` 계열 → unchecked
* 그 외 → checked (throws 강제)

Spring에서 커스텀 예외는 보통 `RuntimeException` 상속해서 만든다.

```java
public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(Long id) {
        super("Note not found: " + id);
    }
}
```

#### 자주 하는 실수 & 팁

* 예외를 catch만 하고 무시 (`// TODO` 남기고 잊음)
* 모든 예외를 `Exception` 하나로 싸잡아서 처리
* 의미 없는 메시지 (`"에러 발생"`)만 던져서 디버깅시 정보 부족

#### FAQ

* Q: checked 예외는 쓰지 말아야 하나요?
  A: 논쟁 많다. 이 문서에서는 커스텀 도메인 예외는 unchecked로 가정.
* Q: 예외는 어디서 잡아야 하나요?
  A: 컨트롤러 단에서 @ControllerAdvice로 한 번에 처리하는 패턴을 뒤에서 다룬다.

---

### 2.3 컬렉션 API

인메모리 버전에서는 DB 대신 컬렉션으로 데이터를 관리한다.

```java
import java.util.*;

public class Note {
    private Long id;
    private String content;
    // getter/setter
}
```

```java
List<Note> notes = new ArrayList<>();
Map<Long, Note> noteMap = new HashMap<>();
```

간단한 예:

```java
List<String> list = new ArrayList<>();
list.add("a");
list.add("b");
for (String s : list) {
    System.out.println(s);
}
```

#### 자주 하는 실수 & 팁

* `List` vs `ArrayList` 차이 몰라서 구현체 타입으로 변수 선언함
* `null` 체크 없이 `Map.get` 결과 바로 사용
* 동시성 신경 안 쓰고 static 컬렉션에 데이터 넣고 끝 (예제용이면 괜찮지만, 실무는 다름)

#### FAQ

* Q: List/Set/Map 언제 쓰나요?
  A: 중복 허용/순서 필요하면 List, 중복 없음/순서 상관 없으면 Set, key-value면 Map.
* Q: stream은 꼭 써야 하나요?
  A: 이 문서 범위에서는 필수 아님. for-each로도 충분히 가능.

---

## 3. Spring Boot 프로젝트 생성 & 실행

### 3.1 Spring Initializr로 생성

1. 브라우저로 Spring Initializr 접속
2. Project: Gradle 또는 Maven
3. Language: Java
4. Spring Boot: 3.x (현재 LTS 기준)
5. Project Metadata:

   * Group: `com.example`
   * Artifact: `spring-notes-api`
6. Dependencies:

   * Spring Web
   * (선택) Lombok
   * (선택) Spring Data JPA, H2 (나중 섹션)

#### 자주 하는 실수 & 팁

* Java 8 선택 + Spring Boot 3.x 조합 → 실행 안 됨 (3.x는 17 필요)
* Web 의존성 빼먹고 생성해서 `@RestController` 못 씀
* Lombok 썼는데 IDE 플러그인/annotation processing 안 켜서 오류

#### FAQ

* Q: Initializr 말고 IDE에서 새 프로젝트 만들어도 되나요?
  A: 똑같다. IDE가 Initializr 래핑해주는 것뿐.
* Q: Group/Artifact는 나중에 바꿀 수 있나요?
  A: 코드/패키지 경로 다 건드려야 해서 귀찮다. 초반에 대충이라도 합리적으로 잡는 게 낫다.

---

### 3.2 프로젝트 구조 이해

기본 생성 직후 구조 예시:

```text
src/
  main/
    java/
      com/example/springnotesapi/
        SpringNotesApiApplication.java
    resources/
      application.properties
```

* `src/main/java`: 실제 애플리케이션 코드
* `src/main/resources`:

  * 설정(`application.properties` 또는 `application.yml`)
  * static, templates 폴더 등 (이번 문서에서는 거의 안 씀)

#### 자주 하는 실수 & 팁

* 패키지 구조 무시하고 root에 클래스 막 만드는 패턴
* `resources`에 yml/properties 두 개 동시에 놓고 둘 다 쓰려는 시도 (우선순위만 알고 있으면 된다. 하나만 쓰자)
* 클래스 이름/패키지 변경하면서 `@SpringBootApplication` 위치를 이상하게 옮김 → Component Scan 범위 꼬임

#### FAQ

* Q: `application.properties` vs `application.yml` 뭐 씁니까?
  A: 취향. yml이 구조 표현하기 편하긴 하다. 둘 다 섞지 않는 게 낫다.
* Q: 프로젝트 루트 패키지는 어디까지가 좋나요?
  A: 대체로 `com.<회사/도메인>.<서비스명>` 정도.

---

### 3.3 @SpringBootApplication과 실행

기본 생성 클래스:

```java
@SpringBootApplication
public class SpringNotesApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringNotesApiApplication.class, args);
    }
}
```

* `@SpringBootApplication` = `@Configuration + @EnableAutoConfiguration + @ComponentScan`
* `main`에서 전체 앱 부팅

#### 실행

* IDE에서 main 메서드 실행
* 또는:

```bash
./gradlew bootRun
# or
./mvnw spring-boot:run
```

브라우저에서 `http://localhost:8080` 접속 → 404 나오면 일단 서버는 떠 있는 상태.

#### 자주 하는 실수 & 팁

* 포트 충돌 (이미 다른 Spring 프로젝트가 8080 사용 중)
* `@SpringBootApplication`을 서브 패키지에 두고, 그 위 패키지의 Component가 스캔 안 되는 경우
* main 클래스에서 패키지명 바뀌었는데 import/경로 안 맞는 경우

#### FAQ

* Q: `SpringApplication.run` 안에서 뭐 하나요?
  A: Tomcat(내장 서버) 띄우고, 빈 생성하고, Component Scan 돌리고… 전체 애플리케이션 컨텍스트 구성.
* Q: @ComponentScan 범위는 어떻게 정해지나요?
  A: 기본은 그 클래스의 패키지와 하위 패키지.

---

## 4. Spring MVC & REST 컨트롤러 기초

### 4.1 @RestController, @GetMapping

`@RestController`는 반환값을 바로 HTTP Response Body로 직렬화(JSON 등)하는 컨트롤러.

#### 예시: `GET /hello`

```java
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring";
    }
}
```

혹은 query param 사용:

```java
@GetMapping("/hello")
public String hello(@RequestParam(defaultValue = "World") String name) {
    return "Hello, " + name;
}
```

#### 자주 하는 실수 & 팁

* `@Controller`만 쓰고 `@ResponseBody` 안 붙여서 view 찾으려고 함
* URL 앞에 `/` 빼먹고 `@GetMapping("hello")` 써서 혼동 (동작은 하긴 함)
* 메서드 반환 타입을 `void`로 해놓고 응답 안 보내서 500

#### FAQ

* Q: @Controller와 @RestController 차이?
  A: @RestController = @Controller + @ResponseBody. 템플릿(HTML) 안 쓰고 API만 만들 거면 @RestController 쓰면 된다.
* Q: JSON 응답으로 자동 변환은 누가 하나요?
  A: Jackson 등 HTTP Message Converter가 한다. Spring Web 의존성에 포함.

---

### 4.2 @PostMapping, @RequestBody

JSON → DTO로 매핑하는 기본 패턴.

#### 요청 DTO 예시

```java
public class EchoRequest {
    private String message;

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
```

#### 컨트롤러

```java
@RestController
public class EchoController {

    @PostMapping("/echo")
    public EchoRequest echo(@RequestBody EchoRequest request) {
        return request;
    }
}
```

요청:

```http
POST /echo
Content-Type: application/json

{
  "message": "hello"
}
```

응답:

```json
{
  "message": "hello"
}
```

#### 자주 하는 실수 & 팁

* `@RequestBody` 빼먹고 DTO를 파라미터로 받음 → null
* JSON 키 이름과 DTO 필드 이름 불일치
* 기본 생성자, setter 없는 DTO를 만들어놓고 바인딩 안 되는 현상

#### FAQ

* Q: record DTO 써도 되나요?
  A: Spring Boot 3 + Jackson 최신 버전이면 record도 잘 매핑된다.
* Q: @RequestBody 여러 개 받을 수 있나요?
  A: 기본적으로는 하나만. 복합 구조 필요하면 DTO로 감싸는 게 일반적.

---

### 4.3 @PathVariable / @RequestParam

* PathVariable: URL 경로 일부 (`/hello/{name}`)
* RequestParam: 쿼리스트링 (`?a=1&b=2`)

#### 예시

```java
@GetMapping("/hello/{name}")
public String helloPath(@PathVariable String name) {
    return "Hello, " + name;
}
```

```java
@GetMapping("/sum")
public int sum(
        @RequestParam int a,
        @RequestParam int b
) {
    return a + b;
}
```

요청 예:

```http
GET /sum?a=1&b=2
```

#### 자주 하는 실수 & 팁

* `@PathVariable("id")`와 `{noteId}` 같이 이름 불일치
* Optional 파라미터인데 `required = true` 기본값 유지 → 400
* 타입 변환 실패 (문자열 넣었는데 int로 맵핑 시도)

#### FAQ

* Q: PathVariable/RequestParam 중 뭘 써야 하나요?
  A: 리소스 식별(id 등)은 PathVariable, 필터/정렬/검색 조건은 RequestParam.
* Q: 배열/리스트 파라미터는?
  A: `?ids=1&ids=2` 형태로 전달하고 `List<Long>`으로 받는 방식 등 여러 패턴이 있다. 이 문서 범위 밖.

---

## 5. 의존성 주입(DI) 기초

### 5.1 @Service, @Repository, @Component Scan

레이어 역할:

* Controller: HTTP 요청/응답, 검증, DTO 변환
* Service: 비즈니스 로직
* Repository: 데이터 접근 (DB, 인메모리 등)

#### 예시: HelloService

```java
public interface HelloService {
    String hello(String name);
}
```

```java
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "Hello, " + name;
    }
}
```

```java
@RestController
public class HelloController {

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return helloService.hello(name);
    }
}
```

#### 자주 하는 실수 & 팁

* `@Service` 안 붙여서 빈 등록 안 됨 → `NoSuchBeanDefinitionException`
* `@ComponentScan` 범위 밖 패키지에 클래스를 두는 경우
* 인터페이스/구현체 둘 다 빈으로 등록해놓고 주입할 때 어떤 걸 넣어야 할지 애매해지는 경우

#### FAQ

* Q: @Service / @Component / @Repository 차이?
  A: 모두 Component이긴 한데 역할 구분 목적. @Repository는 예외 변환 등 부가 기능 있음.
* Q: new로 직접 생성하면 안 되나요?
  A: DI 컨테이너(Spring) 밖에서 생성하면 DI/Proxy/트랜잭션 같은 기능 못 쓴다.

---

### 5.2 생성자 주입

필드 주입보다 생성자 주입이 권장된다.

```java
@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    // ...
}
```

Lombok 사용 시:

```java
@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
}
```

#### 자주 하는 실수 & 팁

* 필드 주입(`@Autowired` + `private`)으로 시작했다가 테스트/리팩터링 때 문제
* 생성자 여러 개 만들고, @Autowired 어디에 붙여야 하는지 혼란
* Lombok 쓰는데 IDE에 Lombok 플러그인 없어서 생성자 인식 안 됨

#### FAQ

* Q: @Autowired는 안 써도 되나요?
  A: 생성자 하나만 있으면 Spring이 자동으로 주입해준다.
* Q: 필드 주입을 아예 쓰면 안 되나요?
  A: 강한 의존, 테스트 어려움 등 단점 때문에 지양하는 분위기. 프레임워크 코드나 특수한 상황이 아니라면 생성자 주입 기준.

---

## 6. 메모/노트 REST API (인메모리 버전)

### 6.1 요구사항

엔티티: `Note { id, content }`

기능:

* `GET /notes` – 전체 조회
* `GET /notes/{id}` – 단건 조회
* `POST /notes` – 생성
* `DELETE /notes/{id}` – 삭제

---

### 6.2 DTO / 도메인 모델

도메인 모델:

```java
public class Note {
    private Long id;
    private String content;

    // constructor, getter, setter
}
```


요청 DTO:

```java
import jakarta.validation.constraints.NotBlank;

public class NoteCreateRequest {
    @NotBlank(message = "content must not be blank")
    private String content;

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
```


응답 DTO:

```java
public class NoteResponse {
    private Long id;
    private String content;

    public NoteResponse(Long id, String content) {
        this.id = id;
        this.content = content;
    }

    public Long getId() {
        return id;
    }
    public String getContent() {
        return content;
    }

    public static NoteResponse from(Note note) {
        return new NoteResponse(note.getId(), note.getContent());
    }
}
```

#### 자주 하는 실수 & 팁

* 엔티티를 그대로 API 응답으로 내보내다가 나중에 문제(양방향 연관관계, 내부 필드 노출 등)
* setter 없는 DTO + Jackson 설정 미비로 매핑 실패
* Null/빈 문자열 검증 없이 넘어감

#### FAQ

* Q: 엔티티를 그대로 응답에 써도 되나요?
  A: 간단한 학습 프로젝트는 가능하지만, 실무 기준으로는 별도의 응답 DTO가 일반적.
* Q: DTO까지 만드는 게 과한가요?
  A: 이 정도 규모에서도 연습해두는 게 나중에 덜 고통스럽다.

---

### 6.3 인메모리 Repository / Service 구현

`NoteRepository` (인메모리 버전):

```java
@Repository
public class NoteRepository {

    private final Map<Long, Note> storage = new HashMap<>();
    private long sequence = 0L;

    public List<Note> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Note> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Note save(Note note) {
        if (note.getId() == null) {
            note.setId(++sequence);
        }
        storage.put(note.getId(), note);
        return note;
    }

    public void delete(Long id) {
        storage.remove(id);
    }
}
```

`NoteService`:

```java
@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<NoteResponse> getNotes() {
        return noteRepository.findAll()
                .stream()
                .map(NoteResponse::from)
                .toList();
    }

    public NoteResponse getNote(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        return NoteResponse.from(note);
    }

    public NoteResponse create(String content) {
        Note note = new Note();
        note.setContent(content);
        Note saved = noteRepository.save(note);
        return NoteResponse.from(saved);
    }

    public void delete(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        noteRepository.delete(note.getId());
    }
}
```

#### 자주 하는 실수 & 팁

* `Optional.get()`을 아무 생각 없이 호출
* `sequence`를 static으로 두고 테스트 사이드 이펙트 발생
* 인메모리라고 예외 처리 안 함

#### FAQ

* Q: Repository 인터페이스로 안 나눠도 되나요?
  A: JPA로 갈아탈 때 인터페이스/구현체 분리해두면 전환이 편하다.
* Q: 인메모리 저장은 어디까지 안전한가요?
  A: 서버 리스타트하면 다 날아간다. 학습/테스트용으로만 사용.

---

### 6.4 컨트롤러 작성 & Postman 테스트

```java
@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<NoteResponse> getNotes() {
        return noteService.getNotes();
    }

    @GetMapping("/{id}")
    public NoteResponse getNote(@PathVariable Long id) {
        return noteService.getNote(id);
    }

    @PostMapping
    public ResponseEntity<NoteResponse> create(@jakarta.validation.Valid @RequestBody NoteCreateRequest request) {
        NoteResponse created = noteService.create(request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

Postman에서:

* `GET http://localhost:8080/notes`
* `POST http://localhost:8080/notes` + JSON Body

```json
{
  "content": "메모 내용"
}
```



#### cURL로 호출 예시 (Postman 없이도 검증 가능)

```bash
# 전체 조회
curl http://localhost:8080/notes

# 생성
curl -X POST http://localhost:8080/notes \
  -H "Content-Type: application/json" \
  -d '{"content":"메모 내용"}'

# 단건 조회
curl http://localhost:8080/notes/1

# 삭제
curl -X DELETE http://localhost:8080/notes/1 -i
```
#### 자주 하는 실수 & 팁

* `@RequestMapping("/notes")`와 `@GetMapping("/notes")`를 동시에 써서 `/notes/notes`가 되는 경우
* 201 대신 200 반환 등 상태 코드 대충 사용
* 삭제 후 응답에 굳이 삭제된 객체를 돌려주는 등 API 일관성 부족

#### FAQ

* Q: ResponseEntity 꼭 써야 하나요?
  A: 상태 코드/헤더 조절해야 할 때는 ResponseEntity가 편하다. 단순 200 반환이면 반환 타입만으로도 충분.
* Q: 컨트롤러에서 서비스 없이 Repository 바로 써도 되나요?
  A: 예제에서는 가능하지만, 계층 분리 연습을 위해 Service를 두는 걸 기준으로 한다.

---

## 7. 예외 처리 & 응답 구조

### 7.1 도메인 예외

`NoteNotFoundException`:

```java
public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(Long id) {
        super("Note not found: " + id);
    }
}
```

Service/Repository에서 `orElseThrow`로 던진다.

#### 자주 하는 실수 & 팁

* 모든 예외를 `RuntimeException` 그대로 던지고 끝 → 클라이언트 기준에서 의미 없음
* 같은 상황에 대해 여러 예외 클래스를 만들어서 혼란
* 예외 메시지에 민감 정보(DB 정보, 비밀번호 등) 포함

#### FAQ

* Q: 예외를 어디까지 세분화해야 하나요?
  A: 도메인에서 의미 있는 수준 정도 (존재하지 않음, 권한 없음, 상태 불일치 등).
* Q: 예외 메시지는 사용자용인가요, 개발자용인가요?
  A: 보통은 개발자/로그용. 사용자에게 노출할 메시지는 별도로 관리하는 경우가 많다.

---

### 7.2 @ControllerAdvice, @ExceptionHandler

전역 예외 처리.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoteNotFoundException ex,
                                                        HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex,
                                                         HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }


    // Validation 실패(400)도 공통 에러 응답으로 만들기
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            org.springframework.web.bind.MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        ErrorResponse body = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(body);
    }


}
```

### 7.3 (선택) Validation 의존성 추가 체크

Spring Boot 3.x에서는 Validation이 기본 포함이 아닐 수 있으니, `@Valid`가 동작하지 않으면 의존성을 확인한다.

* Gradle 예:

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-validation'
}
```

* Maven 예:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```


`ErrorResponse` 예시:

```java
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String message;
    private String path;

    public ErrorResponse(String timestamp, int status, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.path = path;
    }
    // getter만
}
```

#### 자주 하는 실수 & 팁

* `@ControllerAdvice`만 쓰고 `@ResponseBody` 안 붙임 → view로 해석
* `Exception` 핸들러에서 예외를 다시 던져버리거나, 로그 남기지 않음
* 공통 에러 구조 없이 상황마다 다른 JSON 반환

#### FAQ

* Q: @RestControllerAdvice vs @ControllerAdvice?
  A: @RestControllerAdvice = @ControllerAdvice + @ResponseBody.
* Q: 예외마다 다른 응답 구조를 써야 하나요?
  A: 기본 구조는 통일하고, 필드 추가 정도만 차이를 두는 편이 낫다.

---

## 8. 환경 설정 & 프로파일 (기본)

### 8.1 application.properties / yml

`src/main/resources/application.properties` 예:

```properties
server.port=8081
logging.level.root=INFO
logging.level.com.example.springnotesapi=DEBUG
```

yml 예:

```yaml
server:
  port: 8081

logging:
  level:
    root: INFO
    com.example.springnotesapi: DEBUG
```

#### 자주 하는 실수 & 팁

* properties와 yml 혼용
* 오타 (예: `server.post` → 적용 안 됨)
* `logging.level.*=DEBUG`로 두고 배포까지 그대로 가져가는 경우

#### FAQ

* Q: properties와 yml 우선순위는요?
  A: 일반적으로 같은 위치면 확장자 상관없이 하나만 쓰는 게 낫다. 둘 다 있으면 상황 따라 덮어쓰기 가능성.
* Q: 환경 변수로도 설정 바꿀 수 있나요?
  A: 가능하다. `SPRING_PROFILES_ACTIVE` 등.

---

### 8.2 프로파일 개념

개발/운영 환경 구분용.

`application-dev.yml`:

```yaml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:h2:mem:testdb
```

`application-prod.yml`:

```yaml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:postgresql://...
```

`application.yml`:

```yaml
spring:
  profiles:
    active: dev
```

#### 자주 하는 실수 & 팁

* profile 이름만 만들어 놓고 실제 활성화 안 함
* dev/prod 설정 뒤집힌 채로 배포
* profile별로 설정 중복이 많아 관리 지옥

#### FAQ

* Q: profile은 어디서 선택하나요?
  A: `spring.profiles.active` (properties, yml, 환경변수, JVM 옵션 등).
* Q: profile 여러 개 동시에 쓸 수 있나요?
  A: `spring.profiles.active=dev,local` 등으로 가능.

---

## 9. DB 연동 (선택 단계: H2 or PostgreSQL)

### 9.1 JPA/H2 의존성 추가

Initializr에서 추가했거나, 직접 추가:

**Gradle 예**

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.h2database:h2'
}
```

`application.yml` 예:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

#### 자주 하는 실수 & 팁

* H2 console URL/설정 안 맞아서 접속 실패
* `ddl-auto=create-drop`로 두고 데이터 날리는 상황
* DB 설정 바꿨는데 캐시된 설정 때문에 IDE 재시작 전까지 반영 안 되는 경우

#### FAQ

* Q: H2는 꼭 써야 하나요?
  A: 학습용/테스트용으로 편해서 많이 쓴다. 실운영에선 PostgreSQL/MySQL 등 사용.
* Q: ddl-auto는 뭘 쓰나요?
  A: 학습/개발: `update`, 테스트: `create-drop`도 가능. 운영: 웬만하면 사용 안 하고 마이그레이션 도구 사용.

---

### 9.2 엔티티 & JpaRepository

`Note` 엔티티 변경:

```java
@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    protected Note() {
    }

    public Note(String content) {
        this.content = content;
    }

    // getter/setter
}
```

JPA Repository:

```java
public interface NoteJpaRepository extends JpaRepository<Note, Long> {
    // 필요하면 쿼리 메서드 추가
}
```

Service 수정:

```java
@Service
public class NoteService {

    private final NoteJpaRepository noteJpaRepository;

    public NoteService(NoteJpaRepository noteJpaRepository) {
        this.noteJpaRepository = noteJpaRepository;
    }

    public List<NoteResponse> getNotes() {
        return noteJpaRepository.findAll()
                .stream()
                .map(NoteResponse::from)
                .toList();
    }

    public NoteResponse getNote(Long id) {
        Note note = noteJpaRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        return NoteResponse.from(note);
    }

    public NoteResponse create(String content) {
        Note saved = noteJpaRepository.save(new Note(content));
        return NoteResponse.from(saved);
    }

    public void delete(Long id) {
        Note note = noteJpaRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException(id));
        noteJpaRepository.delete(note);
    }
}
```

#### 자주 하는 실수 & 팁

* 엔티티에 기본 생성자(`protected`) 안 만들어서 JPA가 실패
* 양방향 연관관계를 손쉽게 만들고 순환 참조로 JSON 직렬화 에러
* equals/hashCode 구현 잘못해서 컬렉션에서 이상 동작

#### FAQ

* Q: JpaRepository vs CrudRepository?
  A: JpaRepository가 기능 더 많다. 처음에는 JpaRepository 쓰면 된다.
* Q: Entity에 setter를 다 만들어도 되나요?
  A: 간단한 프로젝트는 괜찮지만, 도메인 모델링 관점에서 setter 제한하는 패턴도 많다.

---

### 9.3 간단한 마이그레이션 개념

* DDL 자동 생성 (`ddl-auto`)에만 의존하면 추적이 안 된다.
* Flyway/Liquibase 같은 마이그레이션 도구로 스키마 버전 관리하는 패턴이 일반적.

대략적인 아이디어만:

* `V1__init.sql`
* `V2__add_column_x.sql`

를 순차 적용.

#### 자주 하는 실수 & 팁

* 수동으로 DDL 여러 번 바꾸면서 "지금 DB 상태가 뭔지" 모르는 상태로 감
* 테스트 DB와 로컬 DB 구조가 달라져서 테스트/로컬 결과가 다르게 나오고 원인 파악 어렵게 됨

#### FAQ

* Q: 학습용에도 마이그레이션 도구 써야 하나요?
  A: 필수는 아니다. 개념만 알고 넘어가도 충분.
* Q: ddl-auto랑 마이그레이션 도구를 같이 써도 되나요?
  A: 혼합하면 꼬이기 쉬워서 운영에선 보통 ddl-auto 끄고 마이그레이션만 사용.

---

## 10. 체크리스트

### 10.1 확인 항목

아래 항목을 직접 체크해보면 된다.

* [ ] Spring Initializr로 Spring Boot 프로젝트를 생성할 수 있다.
* [ ] `./gradlew bootRun` 또는 `./mvnw spring-boot:run` 으로 서버를 실행할 수 있다.
* [ ] `@RestController`, `@GetMapping`, `@PostMapping` 으로 간단한 REST API를 작성할 수 있다.
* [ ] Controller / Service / Repository 레이어를 분리해 코드를 구성할 수 있다.
* [ ] 인메모리 기반 Note REST API (`/notes`) 를 구현하고 Postman으로 호출할 수 있다.
* [ ] 커스텀 예외 + `@RestControllerAdvice` 로 기본적인 에러 응답(JSON)을 만들 수 있다.
* [ ] application.properties / yml 에서 포트, 로그 레벨, DB 설정을 수정할 수 있다.
* [ ] (선택) Spring Data JPA + H2 로 Note를 DB에 저장/조회/삭제하도록 변경할 수 있다.

### 10.2 이후 확장 키워드

앞 단계가 다 되는 시점부터 고려:

* 검증

  * Spring Validation: `@Valid`, `@NotNull`, `@Size` 등
  * Controller에서 BindingResult 처리
* 인증/인가

  * Spring Security (폼 로그인, JWT, 세션)
* DB 고도화

  * PostgreSQL / MySQL 실서비스 DB
  * 마이그레이션 도구(Flyway/Liquibase)
* 테스트

  * 단위 테스트 (Service)
  * `@WebMvcTest` 로 컨트롤러 슬라이스 테스트
  * `@SpringBootTest` 통합 테스트

이 문서에 있는 내용이 막히지 않는 수준이 되면, 위 키워드 하나씩 실제 코드로 확장하면 된다.
