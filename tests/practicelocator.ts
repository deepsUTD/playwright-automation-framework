import { test, expect } from '@playwright/test';

test('Practice of locators', async ({ page }) => {
    await page.goto("https://saucedemo.come/");
    await page.locator("//input[@name='user-name']").fill("standard_user");
    await page.locator("//input[@name='password']']").fill("secret_sauce");
    page.locator("//input[@name='login-button']").click;
    
});
