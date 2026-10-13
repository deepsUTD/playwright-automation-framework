import { expect } from '@playwright/test';
import { navigateTo } from './support/navigation';
import { test } from './support/test';

test.describe('Herokuapp browser interaction examples', () => {
  test.describe('FramesTest', () => {
    test('framesPageLinksToBothFrameExamples: the frames page links to both frame demos', async ({ page }) => {
      await navigateTo(page, '/frames');
      await expect(page.locator("a[href='/nested_frames']")).toBeVisible();
      await expect(page.locator("a[href='/iframe']")).toBeVisible();
    });

    test('iframeEditorCanBeReadAndEdited: the frame editor text can be changed', async ({ page }) => {
      await navigateTo(page, '/frames');
      await page.locator("a[href='/iframe']").click();
      const editor = page.frameLocator('#mce_0_ifr').locator('#tinymce');
      await expect(editor).not.toBeEmpty({ timeout: 30_000 });
      await editor.evaluate(element => {
        element.innerHTML = '<p>Selenium frame test</p>';
      });
      await expect(editor).toContainText('Selenium frame test');
    });
  });

  test.describe('NestedFramesTest', () => {
    test('topFrameContainsLeftRightAndMiddleFrames: each nested top frame contains text', async ({ page }) => {
      await navigateTo(page, '/nested_frames');
      const topFrame = page.frameLocator('iframe[name="frame-top"]');
      for (const frameName of ['frame-left', 'frame-middle', 'frame-right']) {
        await expect(topFrame.frameLocator(`iframe[name="${frameName}"]`).locator('body'))
          .not.toBeEmpty({ timeout: 20_000 });
      }
    });

    test('bottomFrameContainsItsText: the bottom frame contains its label', async ({ page }) => {
      await navigateTo(page, '/nested_frames');
      await expect(page.frameLocator('iframe[name="frame-bottom"]').locator('body'))
        .toHaveText('BOTTOM', { timeout: 20_000 });
    });
  });

  test.describe('WysiwygEditorTest', () => {
    test('editorFrameIsAvailable: the rich-text editor frame is visible', async ({ page }) => {
      await navigateTo(page, '/tinymce');
      await expect(page.locator('#mce_0_ifr')).toBeVisible();
    });

    test('editorAcceptsTextInput: text can be entered in the rich-text editor', async ({ page }) => {
      await navigateTo(page, '/tinymce');
      const editor = page.frameLocator('#mce_0_ifr').locator('#tinymce');
      await editor.fill('Herokuapp Selenium editor test');
      await expect(editor).toHaveText('Herokuapp Selenium editor test');
    });
  });

  test.describe('JavaScriptAlertsTest', () => {
    test('alertCanBeAccepted: accepting the alert updates the result message', async ({ page }) => {
      await navigateTo(page, '/javascript_alerts');
      const dialogPromise = page.waitForEvent('dialog');
      await page.getByRole('button', { name: 'Click for JS Alert' }).click();
      const dialog = await dialogPromise;
      expect(dialog.message()).toBe('I am a JS Alert');
      await dialog.accept();
      await expect(page.locator('#result')).toContainText('You successfully clicked an alert');
    });

    test('confirmCanBeAccepted: accepting the confirmation reports Ok', async ({ page }) => {
      await navigateTo(page, '/javascript_alerts');
      const dialogPromise = page.waitForEvent('dialog');
      await page.getByRole('button', { name: 'Click for JS Confirm' }).click();
      const dialog = await dialogPromise;
      expect(dialog.message()).toBe('I am a JS Confirm');
      await dialog.accept();
      await expect(page.locator('#result')).toHaveText('You clicked: Ok');
    });

    test('confirmCanBeDismissed: dismissing the confirmation reports Cancel', async ({ page }) => {
      await navigateTo(page, '/javascript_alerts');
      const dialogPromise = page.waitForEvent('dialog');
      await page.getByRole('button', { name: 'Click for JS Confirm' }).click();
      const dialog = await dialogPromise;
      expect(dialog.message()).toBe('I am a JS Confirm');
      await dialog.dismiss();
      await expect(page.locator('#result')).toHaveText('You clicked: Cancel');
    });

    test('promptCanBeAcceptedWithText: the prompt accepts entered text', async ({ page }) => {
      await navigateTo(page, '/javascript_alerts');
      const dialogPromise = page.waitForEvent('dialog');
      await page.getByRole('button', { name: 'Click for JS Prompt' }).click();
      const dialog = await dialogPromise;
      expect(dialog.message()).toBe('I am a JS prompt');
      await dialog.accept('Selenium prompt');
      await expect(page.locator('#result')).toContainText('Selenium prompt');
    });

    test('promptCanBeDismissed: dismissing the prompt reports a null entry', async ({ page }) => {
      await navigateTo(page, '/javascript_alerts');
      const dialogPromise = page.waitForEvent('dialog');
      await page.getByRole('button', { name: 'Click for JS Prompt' }).click();
      const dialog = await dialogPromise;
      expect(dialog.message()).toBe('I am a JS prompt');
      await dialog.dismiss();
      await expect(page.locator('#result')).toContainText('You entered: null');
    });
  });

  test.describe('MultipleWindowsTest', () => {
    test('linkOpensNewWindow: clicking the link opens a page with the new-window heading', async ({ page, context }) => {
      await navigateTo(page, '/windows');
      const newPagePromise = context.waitForEvent('page');
      await page.getByRole('link', { name: 'Click Here' }).click();
      const newPage = await newPagePromise;
      await expect(newPage.locator('h3')).toContainText('New Window');
    });

    test('newWindowCanBeClosedAndOriginalRestored: closing the new window leaves the original page', async ({ page, context }) => {
      await navigateTo(page, '/windows');
      const newPagePromise = context.waitForEvent('page');
      await page.getByRole('link', { name: 'Click Here' }).click();
      const newPage = await newPagePromise;
      await newPage.close();
      await expect(page).toHaveURL(/\/windows$/);
      expect(context.pages()).toHaveLength(1);
    });
  });

  test.describe('GeolocationTest', () => {
    test('geolocationButtonIsAvailable: the geolocation action is available', async ({ page }) => {
      await navigateTo(page, '/geolocation');
      await expect(page.locator("button[onclick*='getLocation']")).toBeVisible();
    });

    test('allowLocationDisplaysCoordinates: granting location permission displays the selected coordinates', async ({ page, context, baseURL }) => {
      await context.grantPermissions(['geolocation'], { origin: baseURL });
      await context.setGeolocation({ latitude: 37.422, longitude: -122.084, accuracy: 50 });
      await navigateTo(page, '/geolocation');
      await page.locator("button[onclick*='getLocation']").click();
      await expect(page.locator('#lat-value')).toContainText('37.422');
      await expect(page.locator('#long-value')).toContainText('-122.084');
    });
  });

  test.describe('ShadowDomTest', () => {
    test('shadowHostElementsExist: the page contains two shadow hosts', async ({ page }) => {
      await navigateTo(page, '/shadowdom');
      await expect(page.locator('my-paragraph')).toHaveCount(2);
    });

    test('openShadowRootsExposeTheirText: the open shadow roots expose their text', async ({ page }) => {
      await navigateTo(page, '/shadowdom');
      await expect.poll(() => page.locator('my-paragraph').evaluateAll(hosts =>
        hosts.map(host => (host as HTMLElement & { shadowRoot: ShadowRoot }).shadowRoot.textContent ?? '').join(' '),
      )).toContain("Let's have some different text!");
    });
  });

  test.describe('JavascriptErrorTest', () => {
    test('pageStillRendersDespiteItsJavascriptError: the page and heading render despite the script error', async ({ page }) => {
      await navigateTo(page, '/javascript_error');
      await expect(page).toHaveTitle(/The Internet/);
      await expect(page.locator('body')).toContainText('JavaScript');
    });

    test('errorDemonstrationPageHasHeading: the error demonstration heading is visible', async ({ page }) => {
      await navigateTo(page, '/javascript_error');
      await expect(page.locator('h3')).toContainText('JavaScript');
    });
  });

  test.describe('JavaScriptError console tests', () => {
    test('pageLogsSevereJavaScriptError: loading the page raises its expected script error', async ({ page }) => {
      const pageErrors: string[] = [];
      page.on('pageerror', error => pageErrors.push(`${error.message}\n${error.stack ?? ''}`));
      await navigateTo(page, '/javascript_error');
      await expect.poll(() => pageErrors.length).toBeGreaterThan(0);
      expect(pageErrors[0]).toContain('javascript_error');
      expect(pageErrors[0]).toContain('Cannot read properties of undefined');
    });

    test('errorIsReportedAfterLogsAreCleared: reloading after clearing errors reports the script error again', async ({ page }) => {
      const pageErrors: string[] = [];
      page.on('pageerror', error => pageErrors.push(error.message));
      await navigateTo(page, '/javascript_error');
      await expect.poll(() => pageErrors.length).toBeGreaterThan(0);
      pageErrors.length = 0;
      const reloadError = page.waitForEvent('pageerror');
      await page.reload({ waitUntil: 'domcontentloaded' });
      expect((await reloadError).message).not.toBe('');
    });

    test('pageWithoutScriptErrorHasNoConsoleErrors: the checkbox page has no non-resource console errors', async ({ page }) => {
      const errors: string[] = [];
      page.on('console', message => {
        if (message.type() === 'error' && !message.text().includes('Failed to load resource')) {
          errors.push(message.text());
        }
      });
      page.on('pageerror', error => errors.push(error.message));
      await navigateTo(page, '/checkboxes');
      errors.length = 0;
      await page.reload({ waitUntil: 'domcontentloaded' });
      await expect(page.locator("input[type='checkbox']")).toHaveCount(2);
      expect(errors).toEqual([]);
    });
  });
});
