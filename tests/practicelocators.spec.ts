import { test, expect } from '@playwright/test';

test('Practice of locators', async ({ page }) => {
  await page.goto('https://www.saucedemo.com/');

  await page.locator('input[name="user-name"]').fill('standard_user');
  await page.locator('input[name="password"]').fill('secret_sauce');
  await page.locator('input[name="login-button"]').click();

  await expect(page).toHaveURL(/inventory.html/);
  await page.close();
});
