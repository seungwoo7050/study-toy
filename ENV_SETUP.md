# 개발 환경 설정 가이드 (ENV_SETUP.md)

이 문서는 Toy Project Suite를 실행하기 위한 개발 환경 설정 방법을 안내합니다.

---

## 1. 필수 도구 및 권장 버전

| 도구 | 권장 버전 | 용도 |
|------|----------|------|
| JDK | 21 | 백엔드 (Spring Boot) |
| Gradle | 8.x | 백엔드 빌드 |
| Node.js | 20.x LTS | 프론트엔드 (React) |
| npm | 10.x | 프론트엔드 패키지 관리 |
| Docker | 24.x | PostgreSQL 컨테이너 |
| Docker Compose | 2.x | 멀티 컨테이너 관리 |
| g++ / clang++ | C++17 지원 | C++ 토이 프로젝트 |
| Git | 2.x | 버전 관리 |

---

## 2. OS 안내

이 프로젝트는 **Linux / macOS / WSL**을 기준으로 작성되었습니다.

- **Linux (Ubuntu 등)**: 대부분의 명령어가 그대로 동작합니다.
- **macOS**: Homebrew를 통해 도구를 설치합니다.
- **Windows**: WSL2를 사용하면 Linux와 동일하게 진행할 수 있습니다.  
  순수 Windows 환경에서는 경로 구분자(`\` vs `/`)와 패키지 매니저만 다릅니다.

---

## 3. 설치 확인 명령어

### JDK

```bash
java -version
```

**기대 출력 예시**:
```
openjdk version "21.0.1" 2023-10-17
OpenJDK Runtime Environment (build 21.0.1+12)
OpenJDK 64-Bit Server VM (build 21.0.1+12, mixed mode, sharing)
```

### Node.js / npm

```bash
node -v
npm -v
```

**기대 출력 예시**:
```
v20.10.0
10.2.3
```

### Docker

```bash
docker --version
docker-compose --version
```

**기대 출력 예시**:
```
Docker version 24.0.7, build afdd53b
Docker Compose version v2.23.0
```

### C++ 컴파일러

```bash
g++ --version
```

**기대 출력 예시**:
```
g++ (Ubuntu 13.2.0-4ubuntu3) 13.2.0
Copyright (C) 2023 Free Software Foundation, Inc.
```

macOS의 경우:
```bash
clang++ --version
```

**기대 출력 예시**:
```
Apple clang version 15.0.0 (clang-1500.0.40.1)
```

### Git

```bash
git --version
```

**기대 출력 예시**:
```
git version 2.43.0
```

---

## 4. 설치 가이드

### macOS (Homebrew)

```bash
# Homebrew 설치 (없는 경우)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# 도구 설치
brew install openjdk@21
brew install node@20
brew install docker docker-compose
brew install git

# JDK 환경변수 설정
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc
source ~/.zshrc

### Gradle wrapper 사용 (필요시)

이 저장소는 Gradle wrapper를 사용하도록 권장합니다. Gradle을 전역으로 설치하지 않아도 프로젝트별로 제공되는 `gradlew` 스크립트를 통해 빌드/테스트를 실행할 수 있습니다.

```bash
# 예: backend 빌드
cd /Users/woopinbell/work/toy/backend/mini-job-service
./gradlew clean build
```

만약 `gradle-wrapper.jar`가 누락되어 있거나 wrapper를 재생성해야 하면, 전역 Gradle을 설치한 뒤 다음 명령을 실행하세요.

```bash
gradle wrapper
```

```

### Ubuntu / Debian

```bash
# JDK 21
sudo apt update
sudo apt install openjdk-21-jdk

# Node.js 20.x
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs

# Docker
sudo apt install docker.io docker-compose-plugin
sudo usermod -aG docker $USER

# C++ 컴파일러
sudo apt install build-essential

# Git
sudo apt install git
```

### Windows (WSL2)

1. WSL2를 설치합니다: `wsl --install`
2. Ubuntu를 선택하고 위의 Ubuntu 가이드를 따릅니다.

---

## 5. 포트 / 리소스 사용 계획

| 서비스 | 포트 | 설명 |
|--------|------|------|
| 백엔드 (Spring Boot) | 8080 | REST API 서버 |
| 프론트엔드 (Vite) | 5173 | 개발 서버 (기본) |
| 프론트엔드 (Vite) | 3000 | 개발 서버 (대안) |
| 프론트엔드 (Vite) | 8081 | 개발 서버 (추가 허용, CORS)
| PostgreSQL | 5432 | 데이터베이스 |
| C++ echo-server | 9000 | TCP 에코 서버 |
| C++ multi-chat-server | 9001 | TCP 채팅 서버 |

### 포트 충돌 해결 방법

1. **사용 중인 포트 확인**:
   ```bash
   # Linux / macOS
   lsof -i :8080
   
   # 또는
   netstat -an | grep 8080
   ```

2. **포트 변경 방법**:
   - 백엔드: `application.yml`에서 `server.port` 수정
   - 프론트엔드: `vite.config.ts`에서 `server.port` 수정 (8081도 허용됨)
   > 백엔드 CORS 설정에서 8081 포트도 허용되므로, 프론트엔드 개발 서버를 8081로 띄워도 정상적으로 동작합니다.
   - C++: 소스 코드 내 포트 상수 수정

---

## 6. 빠른 검증

모든 도구가 설치되었는지 한 번에 확인:

```bash
echo "=== JDK ===" && java -version && \
echo "=== Node.js ===" && node -v && \
echo "=== npm ===" && npm -v && \
echo "=== Docker ===" && docker --version && \
echo "=== g++ ===" && g++ --version | head -1 && \
echo "=== Git ===" && git --version
```

모든 항목이 정상 출력되면 환경 설정 완료입니다!
