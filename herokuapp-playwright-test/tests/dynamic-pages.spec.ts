import { expect } from '@playwright/test';
import { navigateTo } from './support/navigation';
import { test } from './support/test';

test.describe('Herokuapp dynamic pages and navigation', () => {
  test.describe('DynamicContentTest', () => {
    test('dynamicContentRowsAreRendered: three dynamic content rows are shown', async ({ page }) => {
      await navigateTo(page, '/dynamic_content');
      await expect(page.locator('#content > .row > .large-10')).toHaveCount(3);
    });

    test('contentCanBeRefreshedWithoutLosingPageStructure: refreshing keeps all three content rows', async ({ page }) => {
      await navigateTo(page, '/dynamic_content');
      const rows = page.locator('#content > .row > .large-10');
      await expect(rows).toHaveCount(3);
      await page.reload({ waitUntil: 'commit' });
      await expect(rows).toHaveCount(3);
    });
  });

  test.describe('DynamicLoadingTest', () => {
    test('exampleOneRevealsHiddenContent: starting example one reveals Hello World', async ({ page }) => {
      await navigateTo(page, '/dynamic_loading');
      await page.getByRole('link', { name: 'Example 1: Element on page that is hidden' }).click();
      await page.locator('#start button').click();
      await expect(page.locator('#finish')).toHaveText('Hello World!', { timeout: 30_000 });
    });

    test('exampleTwoLoadsContentAfterStart: starting example two loads Hello World', async ({ page }) => {
      await navigateTo(page, '/dynamic_loading/2');
      await page.locator('#start button').click();
      await expect(page.locator('#finish')).toHaveText('Hello World!', { timeout: 30_000 });
    });
  });

  test.describe('EntryAdTest', () => {
    test('entryAdPageLoads: the entry-ad page heading is visible', async ({ page }) => {
      await navigateTo(page, '/entry_ad');
      await expect(page.locator('h3').first()).toContainText('Entry Ad');
    });

    test('modalCanBeClosedWhenDisplayed: an entry modal can be closed when it appears', async ({ page }) => {
      await navigateTo(page, '/entry_ad');
      const modal = page.locator('#modal');
      if (await modal.isVisible()) {
        await page.locator('#modal .modal-footer p').click();
        await expect(modal).toBeHidden();
      }
      await expect(page.locator('h3').first()).toBeVisible();
    });
  });

  test.describe('ExitIntentTest', () => {
    test('exitIntentModalAppearsAtTopBoundary: moving the pointer out at the top shows the modal', async ({ page }) => {
      await navigateTo(page, '/exit_intent');
      await page.mouse.move(400, -1);
      await expect(page.locator('#ouibounce-modal')).toBeVisible();
    });

    test('exitIntentModalCanBeDismissed: the exit-intent modal can be dismissed', async ({ page }) => {
      await navigateTo(page, '/exit_intent');
      await page.mouse.move(400, -1);
      const modal = page.locator('#ouibounce-modal');
      await expect(modal).toBeVisible();
      await modal.locator('.modal-footer p').click();
      await expect(modal).toBeHidden();
    });
  });

  test.describe('FloatingMenuTest', () => {
    test('floatingMenuContainsNavigationLinks: the floating menu has at least four links', async ({ page }) => {
      await navigateTo(page, '/floating_menu');
      await expect.poll(() => page.locator('#menu a').count()).toBeGreaterThanOrEqual(4);
    });

    test('menuRemainsVisibleAfterScrolling: the floating menu remains visible at the bottom of the page', async ({ page }) => {
      await navigateTo(page, '/floating_menu');
      await page.evaluate(() => window.scrollTo(0, document.body.scrollHeight));
      await expect(page.locator('#menu')).toBeVisible();
    });
  });

  test.describe('HoversTest', () => {
    test('allProfileImagesAreRendered: all three profile images are visible', async ({ page }) => {
      await navigateTo(page, '/hovers');
      await expect(page.locator('.figure img')).toHaveCount(3);
      await expect(page.locator('.figure img').nth(0)).toBeVisible();
      await expect(page.locator('.figure img').nth(1)).toBeVisible();
      await expect(page.locator('.figure img').nth(2)).toBeVisible();
    });

    test('hoveringRevealsProfileCaptionAndLink: hovering the first image reveals its caption and link', async ({ page }) => {
      await navigateTo(page, '/hovers');
      await page.locator('.figure img').nth(0).hover();
      const caption = page.locator('.figure .figcaption').nth(0);
      await expect(caption).toBeVisible();
      await expect(caption).toContainText('name: user1');
      await expect(caption.getByRole('link')).toBeVisible();
    });
  });

  test.describe('InfiniteScrollTest', () => {
    test('initialPageHasContent: the first page displays its heading and content', async ({ page }) => {
      await navigateTo(page, '/infinite_scroll');
      await expect(page.locator('.scroll')).toBeVisible();
      await expect(page.locator('h3')).toHaveText('Infinite Scroll');
      await expect(page.locator(".scroll a[href*='/infinite_scroll/']")).toBeVisible();
    });

    test('scrollingLoadsAdditionalContent: scrolling loads more text and advances the content link', async ({ page }) => {
      await navigateTo(page, '/infinite_scroll');
      const content = page.locator('.scroll');
      const nextPageLink = page.locator(".scroll a[href*='/infinite_scroll/']");
      const initialText = await content.innerText();
      const initialUrl = await nextPageLink.getAttribute('href');
      await page.evaluate(() => window.scrollTo(0, document.body.scrollHeight));
      await expect.poll(async () => ({
        hasMoreText: (await content.innerText()).length > initialText.length,
        hasNewLink: (await nextPageLink.getAttribute('href')) !== initialUrl,
      }), { timeout: 30_000 }).toEqual({ hasMoreText: true, hasNewLink: true });
    });
  });

  test.describe('JQueryUiMenusTest', () => {
    test('enabledMenuCanBeOpened: the enabled menu option is visible', async ({ page }) => {
      await navigateTo(page, '/jqueryui/menu');
      await expect(page.locator('#ui-id-3')).toBeVisible();
    });

    test('submenuCanNavigateToPdf: hovering the menu opens its PDF destination', async ({ page }) => {
      await navigateTo(page, '/jqueryui/menu');
      await page.locator('#ui-id-3').hover();
      await page.locator('#ui-id-4').hover();
      await page.locator('#ui-id-5').click();
      await expect(page).toHaveURL(/pdf/);
    });
  });
});
