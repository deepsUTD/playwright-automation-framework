import { test, expect } from '@playwright/test';

test('Title Verification', async ({ page, browserName }) => {
    test.slow(browserName === 'chromium');
  await page.goto('https://www.saucedemo.com/');
  await expect(page).toHaveTitle('Swag Labs');
});

test('Successfully login Verification', async ({ page }) => {
  await page.goto('https://www.saucedemo.com/');
  await page.fill('[data-test="username"]', 'standard_user');
  await page.fill('[data-test="password"]', 'secret_sauce');
  await page.click('[data-test="login-button"]');
  await expect(page.locator('#shopping_cart_container')).toBeVisible();
  await expect(page).toHaveURL('https://www.saucedemo.com/inventory.html');
});

