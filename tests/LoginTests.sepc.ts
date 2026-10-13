import {chromium, test } from '@playwright/test';

test('Login', async ({ page }) => {
  await page.goto('https://www.saucedemo.com/');
  await page.locator('input[name="user-name"]').fill('standard_user');
  await page.locator('input[name="password"]').fill('secret_sauce');
  await page.locator('input[class="submit-button btn_action"]').click();
  await page.locator('text=Swag Labs').isVisible
  await page.locator('text=Sauce Labs Backpack').click();
  await page.locator('text=footer').isVisible();
  await page.locator('id=add-to-cart').click();
  await page.locator('a[class="remove"]').isVisible();
  await page.locator('id=remove').click();
  await page.locator('text=Add to cart').isVisible
  //await page.locator('input[value="Login"]').click();
  //await expect(page).toHaveURL(/inventory.html/);
  await page.close();
});

test('locator method with options', async ({ page }) => {
  await page.goto('https://www.saucedemo.com/');
  await page.locator(".form_group",{ has: page.locator('input#user-name') }).click();
  await page.locator(".form_group",{ has: page.locator('input#user-name') }).pressSequentially('standard_user');
  await page.locator(".form_group",{ has: page.locator('input#password') }).click();
   await page.locator(".form_group",{ has: page.locator('input#password') }).pressSequentially('secret_sauce');  
   await page.locator('#login-button').click();
   await page.locator('text=Swag Labs').isVisible();
  //await page.locator("//a",{hasText:'Sauce Labs Backpack'}).click();
  await page.locator('.inventory_item_name', { hasNotText: /^Sauce/ }).isVisible();
  await page.locator('.inventory_item_name', { hasNotText: /^Sauce/ }).click();
    //await page.close();
});