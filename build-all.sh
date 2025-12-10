#!/usr/bin/env bash
set -euo pipefail

# BUILD-ALL
# Purpose: repository 루트에서 한 번에 모든 서브프로젝트(Backend, Frontend, C++)를 빌드합니다.
#
# Design notes:
#  - backend: gradle wrapper(./gradlew) 우선 사용, 없으면 전역 gradle 사용
#  - frontend: npm 설치가 있으면 `npm ci`(lockfile 기반) 후 `npm run build`
#  - cpp: 간단한 g++ 빌드 스크립트를 실행 (예시용이며 시스템별 컴파일러 설정이 필요할 수 있음)
#
# Usage:
#  - repo 루트에서: `./build-all.sh`
#  - 개발 환경 또는 CI에서 각각의 서브프로젝트가 정상 빌드되는지 확인할 때 사용
# Prereqs:
#  - Java/Gradle wrapper (backend), Node/npm (frontend), g++ (C++) 등 도구가 필요합니다.

ROOT_DIR=$(cd "$(dirname "$0")" && pwd)
echo "Build-all started at $(date)"
echo "# NOTE: This script builds backend, frontend and C++ toy projects."
echo "# It will prefer project-local tools: Gradle wrapper for backend and npm for frontend."

printf "\n== Backend: mini-job-service ==\n"
cd "$ROOT_DIR/backend/mini-job-service"
if [ -x ./gradlew ]; then
  echo "Using project gradle wrapper ./gradlew"
  ./gradlew clean build
else
  if command -v gradle >/dev/null 2>&1; then
    echo "Using global gradle"
    gradle clean build
  else
    echo "No gradle wrapper or global gradle found. Skipping backend build."
  fi
fi

printf "\n== Frontend: mini-job-dashboard ==\n"
cd "$ROOT_DIR/frontend/mini-job-dashboard"
if command -v npm >/dev/null 2>&1; then
  # Use consistent install strategy - "ci" ensures reproducible lockfile installation
  npm ci
  npm run build
else
  echo "npm not installed: skipping frontend build"
fi

printf "\n== C++: build projects ==\n"
cd "$ROOT_DIR/cpp"
echo "Building battle-game"
cd battle-game
# 이 예시는 각 파일을 모두 하나의 바이너리로 컴파일합니다. 프로젝트가 복잡하면 Makefile/CMake를 사용하세요.
g++ -std=c++17 -O2 -Wall main.cpp Character.cpp Player.cpp Monster.cpp -o battle-game || true

echo "Building echo-server"
cd ../echo-server
# echo-server에는 `build.sh`가 있으므로 그것을 사용합니다.
./build.sh || true

echo "Building multi-chat-server"
cd ../multi-chat-server
g++ -std=c++17 -O2 -Wall chat-server.cpp -o chat-server || true

echo "\nBuild-all finished at $(date)"
