import { test, expect } from '@playwright/test';

// E2E 홈 페이지 테스트
// 설명: 기본적인 정적 파일 제공 및 핵심 UI(타이틀, Job 목록) 표시 여부를 확인합니다.
// 확장 포인트: 로그인 및 Job 생성/변경 흐름, 백엔드 API 연동 검증 등을 여기에 추가하세요.
// 실행:
//   - 로컬에 프론트엔드 dist가 서빙 중 (e.g., `npx http-server dist -p 8081`)일 때 실행: `PW_BASE_URL=http://localhost:8081 npx playwright test`
//   - 또는 CI에서 base URL을 환경변수로 설정하여 실행합니다.

test('homepage shows title and style', async ({ page }) => {
  await page.goto('/');
  await expect(page.locator('h1')).toContainText('Mini Job Dashboard');
  // job list or no jobs text
  await expect(page.locator('.job-list h2')).toContainText('Job 목록');
});
