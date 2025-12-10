# Common Issues & Troubleshooting

이 문서는 학습/개발 과정에서 자주 마주칠 수 있는 오류와 그 해결 방법을 정리합니다.

---

## 1. git 커밋 시 'env: bash\r: No such file or directory' 오류
- **원인:** git hook 스크립트(.husky 등)에 윈도우 스타일(CRLF) 줄바꿈이 남아 있을 때 발생
- **해결:**
  ```bash
  find .husky -type f -exec dos2unix {} \;
  # 또는
  find .husky -type f -exec sed -i '' 's/\r$//' {} \;
  ```
  변환 후 다시 커밋 시도

## 2. npm ci/build 오류 (node-gyp, 권한 등)
- **원인:** node/npm 버전 불일치, 캐시 문제, 권한 문제 등
- **해결:**
  - node/npm 버전 확인: `node -v`, `npm -v`
  - 캐시 삭제: `npm cache clean --force`
  - 권한 문제: `sudo chown -R $(whoami) ~/.npm`

## 3. 포트 충돌 (address already in use)
- **원인:** 이미 해당 포트를 사용하는 프로세스가 있음
- **해결:**
  ```bash
  lsof -i :<포트번호>
  kill -9 <PID>
  ```

## 4. C++ 빌드/실행 권한 문제
- **원인:** 실행 파일/스크립트에 실행 권한이 없음
- **해결:**
  ```bash
  chmod +x <파일명>
  ```

## 5. 기타
- 자세한 오류 메시지는 구글링 + 공식 문서 참고
- 추가로 자주 발생하는 이슈는 PR/이슈로 제보해 주세요.
