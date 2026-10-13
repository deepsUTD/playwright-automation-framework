import {test} from '@playwright/test';

test("basic locators", async ({page}) => {
  await page.goto("https://www.google.com/");
  await page.getByLabel("Google apps").click();
  console.log("Clicked on Google apps");
  await page.close();
});
