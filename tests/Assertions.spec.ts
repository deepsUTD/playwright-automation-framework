import {chromium } from '@playwright/test';
import { test, expect } from '@playwright/test';

test('Assertions Practice', async ({ page }) => {
  await page.goto('https://www.saucedemo.com/');
  // use the configured test attribute `data-test` in selectors
  await expect(page.locator('[data-test="login-button"]')).toHaveCount(1);
  console.log('Login button is present with count 1');
    await expect(page.locator('[data-test="login-button"]')).toBeVisible();
    console.log('Login button is visible on the page');
    await expect(page.locator('[data-test="login-button"]')).toBeEnabled();
    console.log('Login button is enabled on the page');
    await expect(page.locator('[data-test="login-button"]')).toHaveText('Login');
    console.log('Login logo text is verified as Login');
    await expect(page.locator('[data-test="login-button"]')).toHaveAttribute('class','submit-button btn_action');
    console.log('Login button class attribute is verified as submit-button btn_action');
    await expect(page.locator('[data-test="login-button"]')).toHaveAttribute('name','login-button');
    console.log('Login button name attribute is verified as login-button');
    await expect(page.locator('[data-test="login-button"]')).toHaveId('login-button');
    console.log('Login button id attribute is verified as login-button');
    await expect(page).toHaveURL('https://www.saucedemo.com/');
    console.log('URL is verified as https://www.saucedemo.com/');
    await expect(page).toHaveTitle('Swag Labs');
    console.log('Page title is verified as Swag Labs');
    await expect(page).toHaveTitle(/Swag Lab/);
    console.log('Page title is verified with regex /Swag Lab/');
    await expect(5).toBeGreaterThan(2);
    await expect(5).toBe(5);
    await expect.soft(page.locator('[data-test="login-button"]')).toBeDisabled();
    console.log('Login button is disabled on the page');
    await expect(page.locator('[data-test="login-button"]')).not.toBeDisabled();
    console.log('Login button is not disabled on the page');
    await expect(page.locator('[data-test="login-button"]')).not.toHaveText('Sign In');
    console.log('Login button text is not Sign In');
    await expect(page.locator('[data-test="login-button"]')).not.toHaveAttribute('name','signin-button');   
  // if you want to fill the username, uncomment below
  // await page.locator('input[name="user-name"]').fill('standard_user');
  //await page.close();
});

