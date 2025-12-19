# C++ 네트워크 프로그래밍 기초 학습 문서

## 0. 이 문서로 어디까지 가는가

최종 목표:

1. 리눅스/맥(또는 WSL)에서 C++로 **단일 클라이언트 TCP 에코 서버**를 직접 작성한다.
2. 소켓 호출 흐름 **`socket → bind → listen → accept → recv/send → close`** 를 이해한다.
3. `nc`(netcat) 같은 도구로 **직접 접속해서 요청을 보내고 응답을 확인**할 수 있다.
4. 에코 서버를 **간단한 클래스로 감싸는 구조(헤더/소스 분리)**까지 맛본다.

학습 대상 프로젝트 이름(예시): `tcp-echo-server`

이 문서에서 다루지 않는 것들:

* 멀티 클라이언트, 논블로킹, `select/poll/epoll`
* TLS/SSL, HTTP 같은 고수준 프로토콜
* Windows 전용 Winsock API (WSL 기준만 설명)

---

## 1. 시작 전 준비

### 1.1 C++ 개발 환경

* 목표: 터미널에서 C++ 코드를 컴파일해서 실행할 수 있는 상태 만들기.

#### 설치

* **Linux (Ubuntu 기준)**

  ```bash
  sudo apt update
  sudo apt install build-essential
  g++ --version
  ```
* **macOS (Homebrew 기준)**

  ```bash
  brew install llvm   # 또는 Xcode Command Line Tools
  clang++ --version
  ```
* **Windows**

  * **WSL + Ubuntu** 추천 → 위 Linux와 동일하게 설치
  * 아니면 MinGW, MSYS2, Clang 등 가능하지만, WSL이 가장 간단

#### Hello World 컴파일

`main.cpp`:

```cpp
#include <iostream>

int main() {
    std::cout << "Hello, C++ network\n";
    return 0;
}
```

컴파일 & 실행:

```bash
g++ -std=c++17 main.cpp -o main
./main
```

#### 자주 하는 실수 & 팁

* 컴파일러 설치 안 되어있는데 IDE에만 의존함
  → 터미널에서 `g++ --version` / `clang++ --version` 먼저 확인.
* C 표준이 아니라 C++인데 `gcc`만 사용
  → **C++이면 가능하면 `g++` 사용**.
* 디폴트 표준이 오래된 경우
  → 항상 `-std=c++17` 또는 `-std=c++20` 정도 명시하는 습관:

  ```bash
  g++ -std=c++17 main.cpp -o main
  ```

#### FAQ

* Q: CLion / VSCode / VS 같은 IDE만 써도 되나요?
  A: 가능하다. 그래도 **터미널에서 직접 빌드**하는 방법을 알아두는 게 디버깅/서버 운영에 유리하다.
* Q: 꼭 C++17이어야 하나요?
  A: 필수는 아니지만, 예제는 C++17 기준으로 가정한다.

---

### 1.2 터미널 / 빌드 기본

* 목표: 파일/디렉터리를 이동하고, 여러 파일을 컴파일할 수 있는 수준.

#### 기본 명령

```bash
cd project        # 디렉터리 이동
ls                # 파일 목록 (Windows cmd면 dir)
mkdir src build   # 디렉터리 생성
pwd               # 현재 위치
```

#### 여러 파일 컴파일

예: `main.cpp`, `server.cpp` 두 개를 컴파일해서 `server` 바이너리 만들기:

```bash
g++ -std=c++17 main.cpp server.cpp -o server
./server
```

오브젝트 파일로 나눠 빌드:

```bash
g++ -std=c++17 -c server.cpp   # server.o 생성
g++ -std=c++17 main.cpp server.o -o server
```

#### 자주 하는 실수 & 팁

* 현재 디렉터리와 소스 위치를 헷갈림
  → 항상 `pwd`와 `ls`로 확인.
* 빌드한 바이너리가 어디에 있는지 모르고 `./server`만 반복
  → 빌드 시 `-o ./build/server` 처럼 출력 경로를 명시하는 습관.

#### FAQ

* Q: WSL에서 윈도우 드라이브(C:) 파일은 어떻게 접근하나요?
  A: `/mnt/c/...` 형태로 접근한다. 예: `cd /mnt/c/Users/username/projects`.
* Q: IDE 빌드와 터미널 빌드 차이는?
  A: 본질적으로 **터미널에서 g++ 명령을 대신 실행해주는 것**뿐이다.

---

## 2. C++ 언어 기본

### 2.1 포인터와 참조

네트워크 코드에서 **버퍼 주소**, **길이**, **출력값을 돌려주는 인자** 등을 다룰 때 필수.

```cpp
void incrementByPointer(int* p) {
    if (p) {
        (*p)++;
    }
}

void incrementByReference(int& r) {
    r++;
}

int main() {
    int x = 10;
    incrementByPointer(&x);   // 주소 넘김
    incrementByReference(x);  // 참조로 넘김
}
```

* 포인터: **주소를 저장**하는 변수 (`int* p`), 값이 없을 수도 있음 (`nullptr`)
* 참조: **다른 변수의 별칭** (`int& r = x;`), 항상 유효한 값이어야 함

#### 실습 예제

* 길이 3짜리 배열을 함수에 넘겨서, 함수 안에서 값을 2배로 만드는 코드 작성해보기

```cpp
void doubleArray(int* arr, size_t len) {
    if (!arr) return;
    for (size_t i = 0; i < len; ++i) {
        arr[i] *= 2;
    }
}
```

#### 자주 하는 실수 & 팁

* 포인터를 초기화하지 않고 사용 (`int* p; *p = 3;`)
  → 항상 `nullptr`로 초기화하고, 사용 전에 검사:

  ```cpp
  int* p = nullptr;
  if (p) { /* ... */ }
  ```
* 누가 메모리를 해제해야 하는지 불명확
  → `new`를 한 쪽에서 하면 **그쪽에서 책임지고 `delete`** 해야 한다.

---

### 2.2 메모리 관리 & RAII

네트워크 코드에서 **소켓 FD를 열면 닫아야 한다**. RAII로 자동화 가능.

* RAII 개념: 객체 **생성 시 자원 획득**, **소멸자에서 자원 해제**.

```cpp
#include <unistd.h>

class SocketGuard {
public:
    explicit SocketGuard(int fd) : fd_(fd) {}
    ~SocketGuard() {
        if (fd_ >= 0) {
            close(fd_);
        }
    }

    int get() const { return fd_; }

private:
    int fd_;
};
```

`SocketGuard` 객체가 스코프를 벗어나면 자동으로 `close` 호출.

#### 자주 하는 실수 & 팁

* 예외가 발생하거나 중간에 `return` 하면서 `close`를 놓침
  → RAII 객체로 소켓을 감싸서 자동 정리.
* `new`/`delete` 직접 사용하면서 메모리 누수
  → 가능하면 `new` 대신 **스택 객체** 또는 `std::unique_ptr` 사용.

---

### 2.3 클래스/구조체 & STL

서버 설정, 상태, 버퍼 등을 구조화하기 위해 사용

```cpp
#include <string>

struct ServerConfig {
    std::string address = "0.0.0.0";
    uint16_t port = 9000;
};
```

STL 기본 컨테이너:

* `std::string` : 문자열
* `std::vector<T>` : 동적 배열
* `std::map<K, V>` : key-value 맵

#### 실습 예제

* `ServerConfig` 구조체에 `maxClients`, `bufferSize` 필드를 추가하고, 생성 시 초기값을 넣는 코드 작성

```cpp
struct ServerConfig {
    std::string address = "0.0.0.0";
    uint16_t port = 9000;
    size_t maxClients = MAX_CLIENTS;
    size_t bufferSize = MAX_BUFFER_SIZE; // int 보다는 
};
```

#### 자주 하는 실수 & 팁

* C 스타일 문자열(`char*`)과 `std::string`을 섞다가 에러:

  ```cpp
  std::string s = "hello";
  const char* c = s.c_str(); // 필요할 때만 이렇게 변환
  ```

---

## 3. 컴파일 / 빌드 고도화

### 3.1 여러 파일 컴파일

파일 분리 예:

* `tcp_echo_server.h`
* `tcp_echo_server.cpp`
* `main.cpp`

컴파일:

```bash
g++ -std=c++17 -c tcp_echo_server.cpp   # tcp_echo_server.o
g++ -std=c++17 main.cpp tcp_echo_server.o -o tcp-echo-server
```

헤더에는 **선언**, 소스에는 **구현**을 넣는다.

```cpp
// tcp_echo_server.h
#ifndef TCP_ECHO_SERVER_H
#define TCP_ECHO_SERVER_H

class TcpEchoServer {
public:
    explicit TcpEchoServer(int port);
    bool init();
    void run();
private:
    int port_;
    int listenFd_;
};

#endif
```

#### 자주 하는 실수 & 팁

* include guard 미사용으로 인해 중복 정의 에러
  → 항상 헤더 상단/하단에 `#ifndef / #define / #endif` 사용
  → `#pragma once`도 가능
* 구현을 헤더에 다 넣어두고 여러 번 include해서 link 에러
  → 함수 정의는 `.cpp`로 분리

---

### 3.2 Makefile (또는 CMake 기초)

간단한 Makefile 예시:

```makefile
CXX = g++
CXXFLAGS = -std=c++17 -Wall -Wextra -Werror -O2

TARGET = tcp-echo-server
SRCS = main.cpp tcp_echo_server.cpp
OBJS = $(SRCS:.cpp=.o)

all: $(TARGET)

$(TARGET): $(OBJS)
  $(CXX) $(CXXFLAGS) $(OBJS) -o $(TARGET)

$(OBJS): %.o: %.cpp
  $(CXX) $(CXXFLAGS) -c $< -o $@

clean:
	rm -f $(OBJS)
fclean:
  clean
  rm -f $(TARGET)

re: fclean all
```

빌드:

```bash
make        # all 타겟 실행
./tcp-echo-server
```

#### 자주 하는 실수 & 팁

* 코드를 수정했는데 `make` 안 하고 예전 바이너리 실행
  → 항상 `make` 또는 빌드 명령 다시 실행.
* 탭/스페이스 섞어서 Makefile 깨짐
  → 명령 줄은 **탭(tab)** 이어야 한다.

---

## 4. 네트워크 / 소켓 개념

### 4.1 TCP vs UDP

* **TCP**

  * 연결 지향(3-way handshake)
  * 신뢰성 보장(순서 보장, 재전송)
  * 스트림 기반
* **UDP**

  * 비연결
  * 빠르지만 손실 가능
  * 메시지(데이터그램) 단위

에코 서버는 **TCP**로 진행.

#### 자주 하는 실수 & 팁

* TCP를 "메시지 단위"라고 오해
  → TCP는 **byte stream**이다. 어디까지가 한 메시지인지 애플리케이션이 정해야 한다.

---

### 4.2 IP 주소 / 포트 / 로컬 테스트

* `127.0.0.1` / `localhost`: 자기 자신
* 포트 범위: `0 ~ 65535`

  * 0~1023: well-known (root 권한 필요할 수 있음)
  * 일반적으로 1024 이상 사용 (예: 8080, 9000, 12345)

#### 실습 예제 (netcat)

터미널 1:

```bash
nc -l 9000
```

터미널 2:

```bash
nc 127.0.0.1 9000
```

* 터미널 2에서 입력 → 터미널 1에서 그대로 보임.
* 이 느낌을 **C++ 코드로 구현하는 게 에코 서버**다.

#### 자주 하는 실수 & 팁

* 방화벽/보안 프로그램이 포트를 막는데, 코드만 의심
  → 같은 머신에서 `127.0.0.1`로 테스트하는 단계에서는, 거의 방화벽 문제는 아니다. 리모트 접속 시에는 방화벽도 확인.

### 4.3 포트/프로세스 확인 (트러블슈팅)

`bind: Address already in use` 같은 에러가 나면 “내 코드가 이상한가?”보다 먼저 **포트를 누가 쓰는지** 확인하는 게 빠르다.

* Linux/WSL:

```bash
ss -lntp | grep :9000
# 또는
sudo lsof -i :9000
```

* macOS:

```bash
lsof -i :9000
```

### 4.4 nc(netcat) 옵션이 다를 때

`nc`는 구현체(netcat-openbsd, traditional, ncat 등)에 따라 옵션이 조금 다를 수 있다.
아래 중 하나가 안 되면 `nc -h`(도움말)로 확인해서 “listen + 포트 지정” 조합을 맞추자.

* 예시(많이 쓰이는 패턴):

```bash
# 서버처럼 listen
nc -l 9000
# (안 되면) nc -l -p 9000

# 클라이언트 접속
nc 127.0.0.1 9000
```


---

## 5. BSD 소켓 API 기초

공통 헤더 (리눅스/맥/WSL):

```cpp
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
```

### 5.1 socket() – 소켓 생성

```cpp
int fd = socket(AF_INET, SOCK_STREAM, 0);
if (fd < 0) {
    perror("socket failed");
    return 1;
}
```

* 첫 번째 인자: 주소 체계 (IPv4 → `AF_INET`)
* 두 번째 인자: 타입 (TCP → `SOCK_STREAM`)
* 세 번째 인자: 프로토콜 (보통 0)

#### 실습 예제

* `socket()`만 호출하는 프로그램을 만들고, 성공/실패 여부를 로그로 출력해본다.
* 성공 시 `close(fd);` 호출해보기.

#### 자주 하는 실수 & 팁

* 에러 체크 없이 다음 단계로 진행
  → 모든 소켓 호출 후 **반환값 검사** 필수.

---

### 5.2 sockaddr_in, htons, bind()

서버가 **어떤 주소/포트에서 대기할지**를 OS에 알려준다.

```cpp
int port = 9000;
int serverFd = socket(AF_INET, SOCK_STREAM, 0);

sockaddr_in addr{};
addr.sin_family = AF_INET;
addr.sin_addr.s_addr = htonl(INADDR_ANY);  // 0.0.0.0
addr.sin_port = htons(port);

if (bind(serverFd, (sockaddr*)&addr, sizeof(addr)) < 0) {
    perror("bind failed");
    close(serverFd);
    return 1;
}
```

* `htonl`, `htons`: host → network byte order 변환
* `INADDR_ANY`: 모든 인터페이스에서 받겠다는 의미

#### 실습 예제

* 포트 8080으로 bind 시도 후 성공/실패 로그 찍기.
* 이미 사용 중인 포트로 두 번 실행해서 **에러 메시지 확인**하기.

#### 자주 하는 실수 & 팁

* 포트가 이미 사용 중인데 에러 무시
  → `EADDRINUSE`가 뜨면 **이미 다른 프로세스가 그 포트를 쓰는 중**이다.

---

### 5.3 listen()과 accept()

클라이언트 연결을 받을 준비.

```cpp
if (listen(serverFd, SOMAXCONN) < 0) {
    perror("listen failed");
    close(serverFd);
    return 1;
}

sockaddr_in clientAddr{};
socklen_t clientLen = sizeof(clientAddr);

int clientFd = accept(serverFd, (sockaddr*)&clientAddr, &clientLen);
if (clientFd < 0) {
    perror("accept failed");
    close(serverFd);
    return 1;
}
```

* `listen`: 서버 소켓을 "연결 대기 상태"로 변경.
* `accept`: **클라이언트가 접속할 때까지 블로킹**.

#### 실습 예제

* 클라이언트가 연결되면 `"client connected"` 출력 후 바로 종료하는 코드 작성.
* `nc 127.0.0.1 9000`으로 한 번 접속해보기.

#### 자주 하는 실수 & 팁

* 리슨 소켓(`serverFd`)과 클라이언트 소켓(`clientFd`)을 구분하지 못함
  → `serverFd`는 계속 `accept` 용, `clientFd`는 실제 데이터 송수신용이다.

---

### 5.4 recv()/send() (또는 read()/write())

클라이언트와 데이터 주고받기.

```cpp
char buffer[1024];

ssize_t received = recv(clientFd, buffer, sizeof(buffer), 0);
if (received < 0) {
    perror("recv failed");
} else if (received == 0) {
    // 클라이언트가 정상 종료
} else {
    // 받은 만큼 그대로 다시 보내기
    ssize_t sent = send(clientFd, buffer, received, 0);
    if (sent < 0) {
        perror("send failed");
    }
}
```

* recv 반환값:

  * > 0: 받은 바이트 수
  * = 0: 연결 종료
  * < 0: 에러
* send도 부분 전송 가능 → 실제로는 루프 돌면서 전체 전송해야 안전.

#### 실습 예제

* 한 번 recv → 한 번 send만 하는 코드 작성 후, `nc`로 "hello"를 보내서 반환되는지 확인.

#### 자주 하는 실수 & 팁

* recv 한 번 호출하면 "한 메시지 전부" 온다고 착각
  → TCP는 스트림이므로, 여러 번 나눠서 도착할 수 있다. 데모 단계에서는 단순히 한 번만 받는 방식을 쓰되, 이 한계를 인지할 것.

* 상대가 연결을 끊은 뒤 send를 하면 프로그램이 “갑자기 죽는” 것처럼 보일 수 있음
  → 일부 환경에서는 `SIGPIPE` 시그널로 프로세스가 종료될 수 있다. 학습 단계에서는 `SIGPIPE`를 무시하고 `send()`의 반환값/errno로 에러를 처리하는 편이 디버깅에 유리하다. (아래 6.3 코드에서 처리)


---

## 6. 단일 클라이언트 TCP 에코 서버 만들기

### 6.1 요구사항 정리

* 서버 시작 (`./server 9000`)
* 클라이언트 1명 접속 (`nc 127.0.0.1 9000`)
* 클라이언트가 보낸 문자열을 **그대로 다시 보내기**
* 클라이언트가 종료하면 서버도 종료

### 6.2 main 흐름 설계

1. `socket()` → 서버 소켓 생성
2. `bind()` → 포트에 바인딩
3. `listen()` → 연결 대기
4. `accept()` → 클라이언트 1명 수락
5. 루프:

   * `recv()`로 데이터 읽기
   * 0이면 종료
   * > 0이면 `send()`로 echo
6. `close()` 정리:

   * `clientFd` 닫기
   * `serverFd` 닫기

### 6.3 코드 작성 & 빌드 (단일 파일 버전)

`server.cpp`:


```cpp
#include <iostream>
#include <cstring>
#include <cstdlib>
#include <string>
#include <csignal>

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>

static bool parsePort(const char* s, int& outPort) {
    try {
        int port = std::stoi(std::string(s));
        if (port < 1 || port > 65535) return false;
        outPort = port;
        return true;
    } catch (...) {
        return false;
    }
}

int main(int argc, char* argv[]) {
    if (argc != 2) {
        std::cerr << "Usage: " << argv[0] << " <port>\n";
        return 1;
    }

    // send()가 상대 종료로 실패할 때 SIGPIPE로 죽는 상황을 피하기 위해 무시
    std::signal(SIGPIPE, SIG_IGN);

    int port = 0;
    if (!parsePort(argv[1], port)) {
        std::cerr << "Invalid port: " << argv[1] << " (1~65535)\n";
        return 1;
    }

    int serverFd = socket(AF_INET, SOCK_STREAM, 0);
    if (serverFd < 0) {
        perror("socket");
        return 1;
    }

    // SO_REUSEADDR 설정 (개발 편의)
    int opt = 1;
    if (setsockopt(serverFd, SOL_SOCKET, SO_REUSEADDR, &opt, sizeof(opt)) < 0) {
        perror("setsockopt");
        close(serverFd);
        return 1;
    }

    sockaddr_in addr{};
    addr.sin_family = AF_INET;
    addr.sin_addr.s_addr = htonl(INADDR_ANY);
    addr.sin_port = htons(port);

    if (bind(serverFd, (sockaddr*)&addr, sizeof(addr)) < 0) {
        perror("bind");
        close(serverFd);
        return 1;
    }

    if (listen(serverFd, SOMAXCONN) < 0) {
        perror("listen");
        close(serverFd);
        return 1;
    }

    std::cout << "Listening on port " << port << "...\n";

    sockaddr_in clientAddr{};
    socklen_t clientLen = sizeof(clientAddr);
    int clientFd = accept(serverFd, (sockaddr*)&clientAddr, &clientLen);
    if (clientFd < 0) {
        perror("accept");
        close(serverFd);
        return 1;
    }

    char clientIp[INET_ADDRSTRLEN]{};
    inet_ntop(AF_INET, &clientAddr.sin_addr, clientIp, sizeof(clientIp));
    std::cout << "Client connected from " << clientIp << ":" << ntohs(clientAddr.sin_port) << "\n";

    char buffer[1024];

    while (true) {
        ssize_t received = recv(clientFd, buffer, sizeof(buffer), 0);
        if (received < 0) {
            perror("recv");
            break;
        }
        if (received == 0) {
            std::cout << "Client disconnected\n";
            break;
        }

        // 그대로 돌려보내기
        ssize_t totalSent = 0;
        bool sendFailed = false;
        while (totalSent < received) {
            ssize_t sent = send(clientFd, buffer + totalSent, received - totalSent, 0);
            if (sent < 0) {
                perror("send");
                sendFailed = true;
                break;
            }
            totalSent += sent;
        }
        if (sendFailed) break;
    }

    close(clientFd);
    close(serverFd);
    return 0;
}

```


빌드 & 실행:

```bash
g++ -std=c++17 server.cpp -o server
./server 9000
```

다른 터미널에서:

```bash
nc 127.0.0.1 9000
hello
hello
abc
abc
```

#### 자주 하는 실수 & 팁

* `nc`에서 엔터(줄바꿈)를 입력 안 해서, 화면에 결과가 헷갈림
  → 메시지 뒤에 `\n`이 포함되어야 눈에 잘 보인다.
* 서버를 이미 실행 중인데 또 실행해서 `bind: Address already in use`
  → 기존 서버 프로세스를 종료하거나 포트를 바꾸거나, `SO_REUSEADDR` 옵션을 사용.

---

## 7. 코드 구조 정리 (Server 클래스로 캡슐화)

### 7.1 Server 클래스 도입

단일 파일 → 클래스로 감싸서 구조 정리.

```cpp
// tcp_echo_server.h (대략적인 형태)
#ifndef TCP_ECHO_SERVER_H
#define TCP_ECHO_SERVER_H

class TcpEchoServer {
public:
    explicit TcpEchoServer(int port);
    ~TcpEchoServer();

    bool init();
    void run();

private:
    int port_;
    int listenFd_;
};

#endif
```

```cpp
// tcp_echo_server.cpp (개략)
#include "tcp_echo_server.h"
#include <iostream>
#include <cstring>
#include <cstdlib>

// 소켓 관련 헤더 포함...

TcpEchoServer::TcpEchoServer(int port)
    : port_(port), listenFd_(-1) {}

TcpEchoServer::~TcpEchoServer() {
    if (listenFd_ >= 0) {
        close(listenFd_);
    }
}

bool TcpEchoServer::init() {
    // socket, bind, listen 처리
    // 실패 시 false
    return true;
}

void TcpEchoServer::run() {
    // accept 후 루프 돌면서 recv/send
}
```

`main.cpp`:

```cpp
#include "tcp_echo_server.h"
#include <iostream>
#include <cstdlib>

int main(int argc, char* argv[]) {
    if (argc != 2) {
        std::cerr << "Usage: " << argv[0] << " <port>\n";
        return 1;
    }

    int port = std::atoi(argv[1]);
    TcpEchoServer server(port);

    if (!server.init()) {
        std::cerr << "Failed to init server\n";
        return 1;
    }

    server.run();
    return 0;
}
```

#### 실습 예제

* 위 구조를 직접 구현해서 단일 파일 버전과 동일하게 동작하도록 만들기.

#### 자주 하는 실수 & 팁

* 소켓 close를 여러 군데에서 호출해 이중 close 발생
  → `TcpEchoServer`의 소멸자에서 **listenFd만 책임지고** 닫게 두고, clientFd는 `run()` 내부에서 관리.

---

### 7.2 헤더/소스 분리

정리해야 할 포인트:

* **헤더**:

  * 클래스 선언, 함수 프로토타입만
  * include guard 필수
* **소스(.cpp)**:

  * 실제 구현
  * 필요한 시스템 헤더 포함

#### 자주 하는 실수 & 팁

* 헤더에서 `<sys/socket.h>` 같은 시스템 헤더를 과도하게 include
  → 필요할 때만 포함. 그래도 네트워크 관련 헤더는 서버 구현에 거의 필수라 자주 들어가긴 한다.
* 네임스페이스 사용 시 헤더에서 `using namespace std;` 사용
  → 헤더에서는 가급적 사용하지 않는다.

---

## 8. 에러 처리와 로깅 (기본)

### 8.1 에러 출력

소켓 함수 실패 시 즉시 원인 확인.

```cpp
int fd = socket(AF_INET, SOCK_STREAM, 0);
if (fd < 0) {
    perror("socket");
    return false;
}
```

* `perror`는 `errno`를 읽어서 의미 있는 메시지를 출력.

에러 처리 패턴:

* 에러 → 로그 출력 → 자원 정리 → 함수에서 false/에러 코드 반환

#### 실습 예제

* `bind`에서 일부러 잘못된 포트를 넣어 실패시키고, `perror("bind")`로 어떤 메시지가 나오는지 확인.

---

### 8.2 간단한 로그

초기에는 `std::cout`으로 로그 찍어도 충분하다.

* 접속 로그:

  * `"Client connected from ..."`
* 종료 로그:

  * `"Client disconnected"`
* 에러 로그:

  * `"recv failed: ..."`

```cpp
#include <iostream>

void logInfo(const std::string& msg) {
    std::cout << "[INFO] " << msg << '\n';
}

void logError(const std::string& msg) {
    std::cerr << "[ERROR] " << msg << '\n';
}
```

#### 자주 하는 실수 & 팁

* 로그가 없어서 어디까지 진행됐는지 파악이 안 됨
  → 최소한 단계별로 `"socket ok"`, `"bind ok"`, `"listen ok"`, `"accept ok"` 정도는 찍어두면 디버깅 속도가 확 줄어든다.

---

## 9. 확장 아이디어 (선택)

### 9.1 다중 클라이언트 개념

한 번에 한 클라이언트만 받는 대신:

* `select`, `poll`, `epoll` 로 여러 소켓을 감시
* 단일 스레드에서 **여러 클라이언트 소켓을 동시에 처리**

개념 흐름:

1. 리슨 소켓 + 클라이언트 소켓들을 fd set에 넣음
2. `select()` 호출
3. 읽을 준비가 된 소켓만 골라서 처리

---

### 9.2 UDP 에코 서버

TCP 대신 UDP로 에코 서버를 작성하면:

```cpp
int fd = socket(AF_INET, SOCK_DGRAM, 0);

sockaddr_in client{};
socklen_t clientLen = sizeof(client);
char buf[1024];

ssize_t received = recvfrom(fd, buf, sizeof(buf), 0,
                            (sockaddr*)&client, &clientLen);

sendto(fd, buf, received, 0, (sockaddr*)&client, clientLen);
```

* `connect`가 없고, `accept`도 없다.
* 패킷이 유실될 수 있고, 순서도 보장되지 않는다.

---

### 9.3 간단 프로토콜 설계

TCP는 스트림이므로 **메시지 경계를 직접 만들어야 한다**.

예시:

1. **길이 + 데이터**

   * 4바이트 정수(네트워크 바이트 순서)로 길이 전송
   * 그 길이만큼 읽어서 하나의 메시지로 처리
2. **구분자 사용**

   * 예: `\n` 단위로 한 줄을 하나의 메시지로 간주

---

## 10. 체크리스트

### 10.1 스스로 점검

* [ ] 터미널에서 C++ 코드를 컴파일하고 실행할 수 있다.
* [ ] 포인터/참조, 기본 STL(`std::string`, `std::vector`)을 이해한다.
* [ ] TCP vs UDP, 포트/IP 개념을 설명할 수 있다.
* [ ] `socket → bind → listen → accept → recv/send → close` 흐름을 구두로 설명할 수 있다.
* [ ] 단일 클라이언트 TCP 에코 서버를 직접 작성해 `nc`로 테스트할 수 있다.
* [ ] 소켓 관련 에러 코드(예: bind 실패, 포트 사용 중)를 로그로 확인하고 대처할 수 있다.
* [ ] 간단한 `TcpEchoServer` 클래스로 구조를 정리할 수 있다.

### 10.2 추가로 공부하면 좋은 키워드

* non-blocking 소켓, `fcntl`, `select/poll/epoll`
* 멀티 스레드 서버 (`std::thread`)
* CMake로 빌드 시스템 정리
* 단위 테스트, 통합 테스트 환경 구축

필요하면 여기에 맞춰서 **실제 예제 코드 뼈대(`tcp_echo_server.{h,cpp}`, `main.cpp`)** 까지 이어서 작성해 줄 수 있다.
