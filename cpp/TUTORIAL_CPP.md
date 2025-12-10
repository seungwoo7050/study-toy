[FILE] cpp/echo-server/server.cpp
[Order 1]
# C++ 튜토리얼: echo-server, battle-game, multi-chat-server

이 문서는 `cpp/` 디렉터리 내의 예제 프로젝트들을 로컬에서 빌드하고 실행하는 방법을 설명합니다.

사전 요구사항
- macOS에서 `g++`(또는 `clang++`)가 설치되어 있어야 합니다. Xcode Command Line Tools가 설치되어 있으면 충분합니다: `xcode-select --install`.


프로젝트별 단계별 실습 및 커밋 예시

1) echo-server (TCP 에코 서버)
 - 경로: `cpp/echo-server`
 - 빌드 스크립트: `build.sh` (간단한 g++ 컴파일 명령 포함)
 - 빌드 방법:
   - `cd cpp/echo-server`
   - `./build.sh` (실행 권한이 없으면 `chmod +x build.sh`)
 - **[Git Commit Tip]**
   ```bash
   GIT_AUTHOR_DATE="2025-01-08 10:00:00" GIT_COMMITTER_DATE="2025-01-08 10:00:00" git commit -m "chore: create echo-server project and implement TCP server/client"
   ```
 - 실행:
   - 서버: `./server 9000`
   - 클라이언트(간단 확인): `./client 127.0.0.1 9000` 또는 `nc 127.0.0.1 9000`
 - 확인: 클라이언트에서 텍스트를 입력하면 서버가 같은 메시지를 반환합니다.
  - 빠른 스모크 테스트: `echo hello | nc 127.0.0.1 9000`을 실행해 `hello`가 그대로 출력되면 OK입니다.

2) battle-game (콘솔 턴제 배틀)
 - 경로: `cpp/battle-game`
 - 빌드: 소스가 이미 포함되어 있으며 간단한 `g++` 빌드 명령으로 컴파일합니다.
 - 예시 빌드:
   - `cd cpp/battle-game`
   - `g++ -std=c++17 -O2 -o battle-game main.cpp Player.cpp Monster.cpp BattleSystem.cpp`
 - **[Git Commit Tip]**
   ```bash
   GIT_AUTHOR_DATE="2025-01-05 10:00:00" GIT_COMMITTER_DATE="2025-01-05 10:00:00" git commit -m "chore: create battle-game project and base classes"
   GIT_AUTHOR_DATE="2025-01-06 18:00:00" GIT_COMMITTER_DATE="2025-01-06 18:00:00" git commit -m "feat: implement battle system, player and monster"
   ```
 - 실행: `./battle-game`
 - 확인: 콘솔 출력으로 전투 로그가 표시됩니다.

3) multi-chat-server (다중 채팅 서버)
 - 경로: `cpp/multi-chat-server`
 - 빌드/실행 예시:
   - `cd cpp/multi-chat-server`
   - `g++ -std=c++17 -O2 -o chat-server src/Server.cpp src/main.cpp`
   - 서버 실행: `./chat-server 9001`
   - telnet 또는 nc로 여러 클라이언트 연결: `nc 127.0.0.1 9001`
 - **[Git Commit Tip]**
   ```bash
   GIT_AUTHOR_DATE="2025-01-10 10:00:00" GIT_COMMITTER_DATE="2025-01-10 10:00:00" git commit -m "chore: create multi-chat-server project and implement multithreaded chat server"
   GIT_AUTHOR_DATE="2025-01-12 17:00:00" GIT_COMMITTER_DATE="2025-01-12 17:00:00" git commit -m "refactor: code cleanup and add comments"
   ```
 - 확인: 한 클라이언트에서 보낸 메시지가 다른 연결된 클라이언트에 브로드캐스트됩니다.
  - 포트 안내: 기본 포트는 8080이지만 백엔드(Spring Boot)와 충돌할 수 있으므로 위 예시처럼 9001 등 별도 포트를 권장합니다.
  - 확인 팁: `nc`를 두 개 띄워 한쪽에 입력한 메시지가 다른 터미널에도 보이면 정상입니다. 연결이 안 된다면 포트 충돌 또는 방화벽 설정을 확인하세요.

공통 디버깅 팁
- 포트 충돌: 같은 포트를 이미 사용하는 프로세스가 있으면 `lsof -i :<port>`로 확인하고 필요 시 종료하세요.
- 권한 문제: 실행 스크립트가 없다면 `chmod +x <file>`로 실행 권한을 추가하세요.
- 컴파일 오류: 누락된 헤더나 함수 시그니처 불일치 등은 컴파일러 메시지를 보고 소스 파일 라인을 점검하세요.

실행 환경 메모
- macOS/Linux 모두 POSIX 소켓을 사용합니다. Windows WSL에서는 동일 명령을 사용할 수 있으나, 네트워크 방화벽/포트 허용 여부를 먼저 확인하세요.
- g++가 여러 버전이 설치된 경우 `g++ --version`으로 17 표준 지원 여부를 확인하고 필요 시 `clang++`으로 대체하세요.

자동화
- 루트의 `build-all.sh`가 C++ 빌드도 포함하므로 전체 레포를 한 번에 빌드하려면 루트에서 `./build-all.sh`를 실행하세요.

참고: 스크립트 사용법 및 목적 설명은 `DOCS/SCRIPTS.md`에 정리되어 있습니다. 각 스크립트의 안전한 사용 방법을 확인하세요.

[LEARN]
- 이 튜토리얼은 C++ 입문과 네트워크/콘솔 예제 실행을 빠르게 확인할 수 있도록 설계되었습니다.
