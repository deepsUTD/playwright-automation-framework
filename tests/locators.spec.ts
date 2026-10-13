import {chromium, expect, Page, test } from '@playwright/test';

// Method to log in
async function loginToSauceDemo(page: Page, username = 'standard_user', password = 'secret_sauce') {
  await page.locator('[data-test="username"]').fill(username);
  await page.locator('[data-test="password"]').fill(password);
  await page.locator('[data-test="login-button"]').click();
}

test('Ensure that a user can log in with valid credentials and is redirected to the dashboard', async ({ page }) => {
  await page.goto('https://www.saucedemo.com/');
  await loginToSauceDemo(page);

  await expect(page).toHaveURL('https://www.saucedemo.com/inventory.html');
  await expect(page.getByText('Products', { exact: true })).toBeVisible();
});

test('Login button remains enabled when the password field is empty', async ({ page }) => {
  await page.goto('https://www.saucedemo.com/');
  await page.locator('[data-test="username"]').fill('standard_user');

  await expect(page.locator('[data-test="password"]')).toBeEmpty();
  await expect(page.locator('[data-test="login-button"]')).toBeEnabled();
});

test('Incorrect password displays an error and keeps the user on the login page', async ({ page }) => {
  await page.goto('https://www.saucedemo.com/');
  await loginToSauceDemo(page, 'standard_user', 'wrong_password');

  await expect(page).toHaveURL('https://www.saucedemo.com/');
  await expect(page.locator('[data-test="error"]')).toHaveText(
    'Epic sadface: Username and password do not match any user in this service'
  );
});

test('Empty login fields display a username-required error', async ({ page }) => {
  await page.goto('https://www.saucedemo.com/');
  await page.locator('[data-test="login-button"]').click();

  await expect(page).toHaveURL('https://www.saucedemo.com/');
  await expect(page.locator('[data-test="error"]')).toHaveText('Epic sadface: Username is required');
});

const searchCases = [
  { name: 'product keyword', term: 'laptop', expectedProduct: 'Asus Laptop' },
  { name: 'uppercase keyword', term: 'ASUS', expectedProduct: 'Asus Laptop' },
  { name: 'special characters', term: '!@#$%^&*()', expectedProduct: null },
];

for (const searchCase of searchCases) {
  test(`nopCommerce search handles ${searchCase.name}`, async ({ page }) => {
    await page.goto('https://demo.nopcommerce.com/');
    await page.getByRole('textbox', { name: 'Search store' }).fill(searchCase.term);
    await page.getByRole('button', { name: 'Search', exact: true }).click();

    expect(new URL(page.url()).searchParams.get('q')).toBe(searchCase.term);
    await expect(page.getByRole('heading', { name: 'Search', exact: true })).toBeVisible();

    if (searchCase.expectedProduct) {
      await expect(page.getByRole('link', { name: searchCase.expectedProduct, exact: true })).toBeVisible();
    } else {
      await expect(page.getByText('No products were found that matched your criteria.', { exact: true })).toBeVisible();
    }
  });
}

test('basic locators', async ({ page }) => {
  await page.goto('https://www.google.com/');
  await page.getByLabel('Google apps').click();
  console.log('Clicked on Google apps');
  await page.close();
});

test('example test', async ({ page }) => {
  const browser = await chromium.launch();
  const context = await browser.newContext();
  const page1 = await context.newPage();
  await page1.goto('https://google.com');
  await page1.getByLabel('Google apps').click();
  console.log('Example.com opened');
  await browser.close();
});

test('saucelabs test', async ({ page }) => {
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

test('locator method with getBy method', async ({ page }) => {
  await page.goto('https://demo.nopcommerce.com/login');
  await page.getByLabel('Email:').click();
  await page.getByLabel('Email:').fill('testcodeautomate@gmail.com');
  await page.getByPlaceholder('Search store').click();
  await page.getByPlaceholder('Search store').fill('Mobile');
  console.log(await page.getByText("New Customer").textContent());
  await page.getByAltText("nopCommerce demo store").isVisible();
  //await page.getByAltText("nopCommerce demo store").click();
   //await page.goto('https://demo.nopcommerce.com/electronics');
   await page.getByRole('button', { name: 'Search' }).click();
  //await page.getByTitle("Show products in category Camera & photo").first().click();
  // await page.getByLabel('Password:').click();
  // await page.getByLabel('Password:').fill('secret_sauce');
  // await page.getByRole('button', { name: 'Login' }).click();
  // await page.getByText('Swag Labs').isVisible();
  // await page.getByText('Sauce Labs Backpack').click();
  // await page.getByRole('footer').isVisible();
  // await page.getByRole('button', { name: 'Add to cart' }).click();
  // await page.getByRole('link', { name: 'Remove' }).isVisible();
  // await page.getByRole('button', { name: 'Remove' }).click();
  // await page.getByRole('button', { name: 'Add to cart' }).isVisible();
  await page.goto('https://saucedemo.com/');
  await loginToSauceDemo(page);
  // await page.close();
});
