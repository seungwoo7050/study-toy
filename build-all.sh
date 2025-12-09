#!/usr/bin/env bash
set -euo pipefail

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
g++ -std=c++17 -O2 -Wall main.cpp Character.cpp Player.cpp Monster.cpp -o battle-game || true

echo "Building echo-server"
cd ../echo-server
./build.sh || true

echo "Building multi-chat-server"
cd ../multi-chat-server
g++ -std=c++17 -O2 -Wall chat-server.cpp -o chat-server || true

echo "\nBuild-all finished at $(date)"
