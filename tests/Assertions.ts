import {test, expect } from '@playwright/test';

test('Assertions Practice', async ({ page }) => {    
 await page.goto('https://www.saucedemo.com/');
 await expect(page.locator('data-set=login-button')).toHaveCount(1);
 //await page.locator('input[name="user-name"]').fill('standard_user');
  //await page.close();
})