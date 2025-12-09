# multi-chat-server (C++ 다중 클라이언트 채팅)

> ⚠️ 이 리포지토리는 **학습용 토이 프로젝트**입니다.  
>   실서비스 운영/보안/장애 대응을 전제로 설계되지 않았습니다.

다중 클라이언트 TCP 채팅 서버입니다.  
멀티스레드 프로그래밍과 공유 자원 관리를 학습합니다.

---

## 기술 스택

- C++17
- POSIX Socket API
- std::thread
- std::mutex
- g++ / clang++

---

## Version

| 버전 | 설명 | Git 태그 |
|------|------|----------|
| C0.3 | 다중 클라이언트 채팅 서버 (선택) | `CPP-C0.3` |

---

## Implementation Order (Files)

### CPP-C0.3

1. `Server.h` / `Server.cpp` - 채팅 서버 클래스
2. `ClientHandler.h` / `ClientHandler.cpp` - 클라이언트 핸들러
3. `main.cpp` - 서버 메인
4. `chat_client.cpp` - 채팅 클라이언트

---

## 빌드 및 실행

### 서버 빌드 및 실행

```bash
cd cpp/multi-chat-server

# 서버 컴파일
g++ -std=c++17 -O2 -Wall -pthread Server.cpp ClientHandler.cpp main.cpp -o chat-server

# 서버 실행 (포트 9001)
./chat-server
```

### 클라이언트 빌드 및 실행

```bash
# 다른 터미널에서
cd cpp/multi-chat-server

# 클라이언트 컴파일
g++ -std=c++17 -O2 -Wall -pthread chat_client.cpp -o chat-client

# 클라이언트 실행
./chat-client
```

---

## 동작 예시

**서버 측:**
```
Chat Server listening on port 9001...
[User1] connected.
[User2] connected.
[User1]: Hello everyone!
[User2]: Hi User1!
[User1] disconnected.
```

**클라이언트 측:**
```
Enter your name: User1
Connected to chat server!
> Hello everyone!
[User2]: Hi User1!
> 
```

---

## 학습 목표

- std::thread를 이용한 멀티스레드 서버
- std::mutex를 이용한 공유 자원 동기화
- 브로드캐스트 패턴 (모든 클라이언트에게 메시지 전송)
- 클라이언트 연결/해제 관리

---

## Troubleshooting

### 컴파일 에러 (std::thread 관련)

- `-pthread` 플래그를 컴파일 옵션에 추가한다.

```bash
g++ -std=c++17 -O2 -Wall -pthread *.cpp -o chat-server
```

### `bind`/`listen` 실패

- 이미 해당 포트를 다른 프로세스가 사용 중인지 확인한다.
- Linux/macOS: `lsof -i :9001`로 확인 가능.

### 클라이언트가 서버에 연결할 수 없음

- 서버가 먼저 실행되어 있는지 확인한다.
- 서버/클라이언트가 같은 호스트와 포트를 사용하도록 설정했는지 확인한다.

### 메시지가 다른 클라이언트에게 전달되지 않음

- 서버의 브로드캐스트 로직이 올바른지 확인한다.
- 클라이언트 목록에 대한 mutex 잠금이 제대로 되어 있는지 확인한다.

### 세그멘테이션 폴트

- 스레드 간 공유 자원 접근 시 mutex로 보호하고 있는지 확인한다.
- 클라이언트 연결 해제 시 리스트에서 제거가 올바르게 되는지 확인한다.
