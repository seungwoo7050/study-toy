#!/usr/bin/env bash
set -euo pipefail

# CLEANUP BUILD RESULTS
# Purpose: 로컬 개발 중 생성된 빌드 산출물(backend build 폴더, frontend dist, C++ 바이너리 등)
# 을 삭제하여 개발 환경을 깨끗하게 되돌릴 수 있도록 돕는 utility 스크립트입니다.
#
# Usage:
#   - 루트에서: `./scripts/cleanup-build-results.sh`
#   - CI나 로컬 테스트에서 임시로 build 아티팩트를 제거해야 할 때 사용합니다.
#
# NOTE:
#   - 이 스크립트는 안전 장치(예: 특정 로그 디렉토리 보존)를 포함하고 있습니다. 보존하려는 build-logs
#     폴더는 코드 내에 하드코딩된 이름(`frontend-20251210T055849`)으로 유지됩니다. 필요시 스크립트를
#     수정하거나 보존 목록을 인자로 전달하도록 확장하세요.

ROOT_DIR=$(cd "$(dirname "$0")/.." && pwd)
echo "Cleaning build outputs and logs (keeping build-logs/frontend-20251210T055849/)"

## Backend build folder
if [ -d "$ROOT_DIR/backend/mini-job-service/build" ]; then
  echo "Removing backend build directory..."
  rm -rf "$ROOT_DIR/backend/mini-job-service/build"
fi

## Frontend dist
if [ -d "$ROOT_DIR/frontend/mini-job-dashboard/dist" ]; then
  echo "Removing frontend dist directory..."
  rm -rf "$ROOT_DIR/frontend/mini-job-dashboard/dist"
fi

## C++ artifacts (binary executables)
echo "Removing C++ binaries (if present)..."
rm -f "$ROOT_DIR/cpp/battle-game/battle-game" || true
rm -f "$ROOT_DIR/cpp/echo-server/server" || true
rm -f "$ROOT_DIR/cpp/echo-server/client" || true
rm -f "$ROOT_DIR/cpp/multi-chat-server/chat-server" || true

## Build logs cleanup: 보존하려는 로그 디렉토리를 제외하고 모두 삭제
echo "Removing build logs except final preserved log..."
for dir in "$ROOT_DIR/build-logs"/*; do
  if [ -d "$dir" ] && [[ "$(basename "$dir")" != "frontend-20251210T055849" ]]; then
    echo "Deleting $dir"
    rm -rf "$dir"
  fi
done

echo "Cleanup finished."
