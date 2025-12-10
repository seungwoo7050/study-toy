#!/usr/bin/env bash
set -euo pipefail

# FRONTEND SMOKE TEST SCRIPT
# Purpose: 손쉬운 로컬/CI용 프론트엔드 정적 빌드 확인 스크립트입니다.
# - `dist/`에 빌드 산출물이 존재하는지 확인하고, 간이 HTTP 서버를 띄워
#   루트 페이지로 요청하여 정적 리소스가 정상적으로 제공되는지 검증합니다.
# Usage:
#   - 로컬: frontend/mini-job-dashboard 위치에서 `npm run build` 후 레포트 디렉토리 루트에서 이 스크립트를 실행하세요.
#   - CI: 빌드 파이프라인에서 `npm run build` 후 이 스크립트를 호출하면 정적 번들이 잘 생성되었는지 검증합니다.
# Prereqs:
#   - node/npm이 설치되어 있어야 하며, `npx http-server` 사용 시에 http-server가 글로벌로 설치되어 있지 않아도 npx로 실행됩니다.
#   - 기본 포트: 8081 (환경변수/스크립트 인자 추가 필요시 수정하시면 됩니다)

ROOT_DIR=$(pwd)
cd "$ROOT_DIR/frontend/mini-job-dashboard"

## 빌드 산출물이 존재하는지 확인
if [ ! -d dist ]; then
  echo "dist not found; run npm run build first" >&2
  exit 1
fi

echo "Serving frontend dist on :8081 and checking /"
## 간단한 정적 서버를 백그라운드로 실행합니다. 로그는 /tmp/http-server.log에 기록됩니다.
npx http-server dist -p 8081 >/tmp/http-server.log 2>&1 &
PID=$!
sleep 1

# HTTP 상태 코드가 200인지 확인합니다. 실패 시에는 http-server 로그를 출력하고 에러로 종료합니다.
curl -sSf http://localhost:8081 || (cat /tmp/http-server.log && kill $PID && exit 1)

echo "Frontend smoke test success"
# 정상 종료를 위해 실행한 서버를 종료합니다.
kill $PID
exit 0
