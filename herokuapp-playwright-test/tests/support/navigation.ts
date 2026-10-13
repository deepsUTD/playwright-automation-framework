import type { Page } from '@playwright/test';

/** Opens a page after its document has been parsed, without waiting for slow external resources. */
export async function navigateTo(page: Page, url: string): Promise<void> {
  await page.goto(url, { waitUntil: 'domcontentloaded' });
}
