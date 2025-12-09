import { test, expect } from '@playwright/test';

test('homepage shows title and style', async ({ page }) => {
  await page.goto('/');
  await expect(page.locator('h1')).toContainText('Mini Job Dashboard');
  // job list or no jobs text
  await expect(page.locator('.job-list h2')).toContainText('Job 목록');
});
