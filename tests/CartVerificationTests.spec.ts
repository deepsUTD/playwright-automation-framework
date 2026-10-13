import { test, expect, Page } from '@playwright/test';
let page:Page;

test.beforeEach(async ({ page }) => {
    page = await browser.newPage();
    // Code to run before each test
    console.log('Starting a new test');
    //Login
    await page.goto('https://www.saucedemo.com/');
    await page.fill('[data-test="username"]', 'standard_user');
    await page.fill('[data-test="password"]', 'secret_sauce');
    await page.click('[data-test="login-button"]');
    await expect(page.locator('#shopping_cart_container')).toBeVisible();
    //await expect(page).toHaveURL('https://www.saucedemo.com/inventory.html');

});

test.afterEach(async ({ page }) => {
    // Code to run after each test
    console.log('Test completed');
    //logout
    await page.getByRole('button', { name: 'Open Menu' }).click();
    await page.getByRole('link', { name: 'Logout' }).click();
    await expect(page).toHaveURL('https://www.saucedemo.com/');
});


test('Adding items to cart verification', async ({ page }) => {

   //Add items to cart
    await page.getByText('Sauce Labs Backpack').click();
    await page.locator('button[id="add-to-cart-sauce-labs-backpack"]').isVisible();
    await page.locator('button[id="add-to-cart-sauce-labs-backpack"]').click();
    await expect(page.locator('.shopping_cart_badge')).toHaveText('1');
    await page.locator('a[class="shopping_cart_link"]').click();
    await expect(page.locator('[data-test="remove-sauce-labs-backpack"]')).toBeVisible();
    await page.locator('[data-test="remove-sauce-labs-backpack"]').click();
    await expect(page.getByRole('link', { name: 'Sauce Labs Backpack' })).toBeVisible();


});

test("Empty cart verification", async ({ page }) => {

    //Verify empty cart
    await page.locator('a[class="shopping_cart_link"]').click();
    await expect(page.locator('.cart_item')).toHaveCount(0);


});