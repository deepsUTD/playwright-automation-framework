import { test, expect, chromium } from '@playwright/test';

test('has title', async ({ page }) => {
  await page.goto('https://playwright.dev/');

  // Expect a title "to contain" a substring.
  await expect(page).toHaveTitle(/Playwright/);
});

test('get started link', async ({ page }) => {
  await page.goto('https://playwright.dev/');

  // Click the get started link.
  await page.getByRole('link', { name: 'Get started' }).click();

  // Expects page to have a heading with the name of Installation.
  await expect(page.getByRole('heading', { name: 'Installation' })).toBeVisible();
});

test('Basic steps to start with PlayWright', async()=>{

  const browser = await chromium.launch();
  const context = await browser. newContext();
  const page = await context.newPage();
await page.goto("https://www.google.com")
await page.getByLabel("google Apps").click();
      console.log("My frist Test");
})