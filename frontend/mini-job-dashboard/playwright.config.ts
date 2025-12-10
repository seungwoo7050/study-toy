/*
 * Playwright Configuration
 * Purpose: End-to-End (E2E) 테스트 설정(기본 구성)
 * - testDir: e2e 테스트가 저장된 디렉토리
 * - baseURL: 기본 테스트 대상 URL; CI에서는 환경변수 `PW_BASE_URL`로 변경 가능
 * - headless: CI에서는 headless 모드로 실행됩니다
 * Usage:
 *   - 로컬: `npx playwright test` 또는 `npx playwright test --project=chromium`
 *   - CI: `npx playwright test` (CI 워크플로에서 `npx playwright install --with-deps` 필요)
 */
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests/e2e',
  timeout: 30_000,
  expect: { timeout: 5_000 },
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  use: {
    baseURL: process.env.PW_BASE_URL ?? 'http://localhost:8081',
    headless: true,
    viewport: { width: 1280, height: 720 },
  },
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
  ],
  webServer: {
    command: 'npx http-server dist -p 8081',
    url: 'http://localhost:8081',
    reuseExistingServer: !process.env.CI,
  },
});
