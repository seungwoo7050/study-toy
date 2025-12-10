import { test, expect } from '@playwright/test';

// E2E Test: Job 생성 플로우 (Mocked backend)
// 목적: UI에서 Job 생성 폼을 통해 POST /api/jobs 요청을 보낼 때,
//       프론트엔드가 올바르게 요청을 전송하고 응답 내용을 UI에 반영하는지 확인합니다.
// 학습 포인트:
// - page.route로 네트워크 요청을 모킹하는 방법
// - 폼 입력, 제출, 결과 렌더링 검증

test('creates a job and displays it in the list (mocked)', async ({ page }) => {
  // Intercept POST to /api/jobs and return a mocked created job
  await page.route('**/api/jobs', async (route) => {
    const request = route.request();
    if (request.method() === 'POST') {
      const body = await request.postDataJSON();
      // return a minimal Job object
      const job = {
        id: 123,
        title: body.title ?? 'Mocked Job',
        description: body.description ?? 'Mocked Description',
        status: 'PENDING',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };
      await route.fulfill({ status: 201, body: JSON.stringify(job), headers: { 'Content-Type': 'application/json' } });
    } else {
      await route.continue();
    }
  });

  await page.goto('/');

  // Fill the create form
  await page.fill('#title', 'Automated Job');
  await page.fill('#description', 'Created by Playwright Mock');
  await page.click('.submit-btn');

  // After submit, check specifically for a heading that matches the new job
  await expect(page.getByRole('heading', { name: 'Automated Job' })).toBeVisible();
  await expect(page.locator('.job-card', { hasText: 'Created by Playwright Mock' })).toBeVisible();
});
