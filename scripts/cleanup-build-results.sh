#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR=$(cd "$(dirname "$0")/.." && pwd)
echo "Cleaning build outputs and logs (keeping build-logs/frontend-20251210T055849/)"

# Remove backend build folder if present
if [ -d "$ROOT_DIR/backend/mini-job-service/build" ]; then
  echo "Removing backend build directory..."
  rm -rf "$ROOT_DIR/backend/mini-job-service/build"
fi

# Remove frontend dist
if [ -d "$ROOT_DIR/frontend/mini-job-dashboard/dist" ]; then
  echo "Removing frontend dist directory..."
  rm -rf "$ROOT_DIR/frontend/mini-job-dashboard/dist"
fi

# Remove C++ compiled artifacts
echo "Removing C++ binaries (if present)..."
rm -f "$ROOT_DIR/cpp/battle-game/battle-game" || true
rm -f "$ROOT_DIR/cpp/echo-server/server" || true
rm -f "$ROOT_DIR/cpp/echo-server/client" || true
rm -f "$ROOT_DIR/cpp/multi-chat-server/chat-server" || true

# Remove build logs except the final one we keep
echo "Removing build logs except final preserved log..."
for dir in "$ROOT_DIR/build-logs"/*; do
  if [ -d "$dir" ] && [[ "$(basename "$dir")" != "frontend-20251210T055849" ]]; then
    echo "Deleting $dir"
    rm -rf "$dir"
  fi
done

echo "Cleanup finished."
