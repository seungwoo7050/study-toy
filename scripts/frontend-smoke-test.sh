#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR=$(pwd)
cd "$ROOT_DIR/frontend/mini-job-dashboard"

if [ ! -d dist ]; then
  echo "dist not found; run npm run build first" >&2
  exit 1
fi

echo "Serving frontend dist on 8081 and checking /"
npx http-server dist -p 8081 >/tmp/http-server.log 2>&1 &
PID=$!
sleep 1

curl -sSf http://localhost:8081 || (cat /tmp/http-server.log && kill $PID && exit 1)

echo "Frontend smoke test success"
kill $PID
exit 0
