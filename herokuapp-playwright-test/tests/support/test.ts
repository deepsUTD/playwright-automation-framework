import { test as base } from '@playwright/test';

export const test = base.extend<{ captureFailureScreenshot: void }>({
  captureFailureScreenshot: [async ({ page }, use, testInfo) => {
    await use();
    if (testInfo.status !== testInfo.expectedStatus) {
      await testInfo.attach('failure-screenshot', {
        body: await page.screenshot({ fullPage: true }),
        contentType: 'image/png',
      });
    }
  }, { auto: true }],
});
