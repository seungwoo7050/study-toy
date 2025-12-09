# echo-server (C++ TCP 에코 서버)

> ⚠️ 이 리포지토리는 **학습용 토이 프로젝트**입니다.  
>   실서비스 운영/보안/장애 대응을 전제로 설계되지 않았습니다.

단일 클라이언트 TCP 에코 서버/클라이언트입니다.  
소켓 프로그래밍 기초를 학습합니다.

---

## 기술 스택

- C++17
- POSIX Socket API
- g++ / clang++

---

## Version

| 버전 | 설명 | Git 태그 |
|------|------|----------|
| C0.2 | 단일 클라이언트 TCP 에코 서버 | `CPP-C0.2` |

---

## Implementation Order (Files)

### CPP-C0.2

1. `server.cpp` - 에코 서버
2. `client.cpp` - 에코 클라이언트
3. `build.sh` - 빌드 스크립트 (선택)

---

## 빌드 및 실행

### 서버 빌드 및 실행

```bash
cd cpp/echo-server

# 서버 컴파일
g++ -std=c++17 -O2 -Wall server.cpp -o server

# 서버 실행 (포트 9000)
./server
```

### 클라이언트 빌드 및 실행

```bash
# 다른 터미널에서
cd cpp/echo-server

# 클라이언트 컴파일
g++ -std=c++17 -O2 -Wall client.cpp -o client

# 클라이언트 실행
./client
```

---

## 동작 예시

**서버 측:**
```
Echo Server listening on port 9000...
Client connected!
Received: Hello, Server!
Sent: Hello, Server!
Client disconnected.
```

**클라이언트 측:**
```
Connected to server.
Enter message: Hello, Server!
Server response: Hello, Server!
Enter message: quit
Disconnected.
```

---

## 학습 목표

- POSIX 소켓 API (socket, bind, listen, accept, connect)
- TCP 연결 수립 과정 (3-way handshake)
- 블로킹 I/O 패턴 (recv/send)
- 클라이언트-서버 아키텍처

---

## Troubleshooting

### 컴파일 에러 (std::string 관련)

- 컴파일 옵션에 `-std=c++17`이 포함되어 있는지 확인한다.

### `bind`/`listen` 실패

- 이미 해당 포트를 다른 프로세스가 사용 중인지 확인한다.
- Linux/macOS: `lsof -i :9000` 또는 `ss -lntp | grep 9000`으로 확인 가능.

```bash
# 포트 사용 확인
lsof -i :9000

# 프로세스 종료 후 다시 시도
kill -9 <PID>
```

### 클라이언트가 서버에 연결할 수 없음

- 서버가 먼저 실행되어 있는지 확인한다.
- 서버/클라이언트가 같은 호스트(localhost)와 포트(9000)를 사용하도록 설정했는지 확인한다.

### "Address already in use" 에러

- 서버를 재시작할 때 이전 소켓이 TIME_WAIT 상태일 수 있다.
- SO_REUSEADDR 옵션을 설정하거나, 몇 초 후 다시 시도한다.
