import { expect } from '@playwright/test';
import { navigateTo } from './support/navigation';
import { test } from './support/test';

test.describe('Herokuapp content and navigation examples', () => {
  test.describe('ABTestingTest', () => {
    test('pageShowsExperimentHeading: the page shows the A/B test heading', async ({ page }) => {
      await navigateTo(page, '/abtest');
      await expect(page.locator('h3')).toContainText('A/B Test');
    });

    test('pageRendersExperimentContent: the page contains experiment content', async ({ page }) => {
      await navigateTo(page, '/abtest');
      await expect(page.locator('body')).not.toBeEmpty();
    });
  });

  test.describe('ChallengingDomTest', () => {
    test('tableHasRowsAndColumns: the table has rows and seven columns', async ({ page }) => {
      await navigateTo(page, '/challenging_dom');
      await expect(page.locator('table tbody tr').first()).toBeVisible();
      await expect(page.locator('table thead th')).toHaveCount(7);
    });

    test('actionButtonsAreAvailable: three actions and the canvas are present', async ({ page }) => {
      await navigateTo(page, '/challenging_dom');
      await expect(page.locator('a.button')).toHaveCount(3);
      await expect(page.locator('#canvas')).toBeVisible();
    });
  });

  test.describe('BrokenImagesTest', () => {
    test('pageContainsTheSampleImages: the page contains three sample images', async ({ page }) => {
      await navigateTo(page, '/broken_images');
      await expect(page.locator('img')).toHaveCount(3);
    });

    test('brokenImageExamplesAreDetectable: at least one sample image is broken', async ({ page }) => {
      await navigateTo(page, '/broken_images');
      await expect.poll(async () => page.locator('img').evaluateAll(
        images => images.filter(image => !(image as HTMLImageElement).complete
          || (image as HTMLImageElement).naturalWidth === 0).length,
      )).toBeGreaterThanOrEqual(1);
    });
  });

  test.describe('DisappearingElementsTest', () => {
    test('stableNavigationLinksArePresent: stable navigation links are shown', async ({ page }) => {
      await navigateTo(page, '/disappearing_elements');
      for (const linkName of ['Home', 'About', 'Contact Us']) {
        await expect(page.getByRole('link', { name: linkName })).toBeVisible();
      }
    });

    test('navigationRendersAtLeastFourItems: at least four navigation items are present', async ({ page }) => {
      await navigateTo(page, '/disappearing_elements');
      await expect.poll(() => page.locator('ul li a').count()).toBeGreaterThanOrEqual(4);
    });
  });

  test.describe('LargeDeepDomTest', () => {
    test('largeTableContainsManyRowsAndColumns: the table has at least 50 rows and 10 columns', async ({ page }) => {
      await navigateTo(page, '/large');
      await expect.poll(() => page.locator('#large-table tbody tr').count()).toBeGreaterThanOrEqual(50);
      await expect.poll(() => page.locator('#large-table tbody tr:first-child td').count())
        .toBeGreaterThanOrEqual(10);
    });

    test('deeplyNestedTargetCanBeLocated: a deeply nested element is visible and has text', async ({ page }) => {
      await navigateTo(page, '/large');
      const target = page.locator('#sibling-50\\.3');
      await expect(target).toBeVisible();
      await expect(target).not.toBeEmpty();
    });
  });

  test.describe('ShiftingContentTest', () => {
    test('shiftingContentExamplesAreListed: at least three examples are listed', async ({ page }) => {
      await navigateTo(page, '/shifting_content');
      await expect.poll(() => page.locator('#content ul li a').count()).toBeGreaterThanOrEqual(3);
    });

    test('imageShiftExampleRendersContent: the image example shows an image and page content', async ({ page }) => {
      await navigateTo(page, '/shifting_content');
      await page.getByRole('link', { name: 'Example 2: An image' }).click();
      await expect(page.locator('#content img')).toBeVisible();
      await expect(page.locator('#content')).not.toBeEmpty();
    });
  });

  test.describe('SlowResourcesTest', () => {
    test('slowResourcesPageRendersItsHeading: the slow resources heading is shown', async ({ page }) => {
      await navigateTo(page, '/slow');
      await expect(page.locator('h3')).toContainText('Slow Resources');
    });

    test('slowPageKeepsMainNavigationUsable: the main navigation link remains available', async ({ page }) => {
      await navigateTo(page, '/slow');
      await expect(page.getByRole('link', { name: 'Elemental Selenium' })).toBeVisible();
    });
  });

  test.describe('StatusCodesTest', () => {
    test('statusCodeLinksAreListed: the four documented status links are available', async ({ page }) => {
      await navigateTo(page, '/status_codes');
      for (const code of ['200', '301', '404', '500']) {
        await expect(page.getByRole('link', { name: code, exact: true })).toBeVisible();
      }
    });

    test('successAndNotFoundResponsesRenderTheirStatusText: success and not-found pages show their status', async ({ page }) => {
      await navigateTo(page, '/status_codes');
      await page.getByRole('link', { name: '200', exact: true }).click();
      await expect(page.locator('#content')).toContainText('200 status code');
      await navigateTo(page, '/status_codes/404');
      await expect(page.locator('#content')).toContainText('404 status code');
    });
  });

  test.describe('RedirectLinkTest', () => {
    test('redirectLinkIsAvailable: the redirect link is visible', async ({ page }) => {
      await navigateTo(page, '/redirector');
      await expect(page.locator('#redirect')).toBeVisible();
    });

    test('redirectReachesStatusCodesPage: clicking the link opens the status codes page', async ({ page }) => {
      await navigateTo(page, '/redirector');
      await page.locator('#redirect').click();
      await expect(page).toHaveURL(/\/status_codes$/);
    });
  });

  test.describe('TyposTest', () => {
    test('pageExplainsTheTypoExample: the page heading identifies the typo example', async ({ page }) => {
      await navigateTo(page, '/typos');
      await expect(page.locator('h3')).toContainText('Typos');
    });

    test('expectedParagraphIsPresent: the documented paragraph is present', async ({ page }) => {
      await navigateTo(page, '/typos');
      await expect(page.locator('#content')).toContainText("Sometimes you'll see a typo");
    });
  });

  test.describe('NotificationMessagesTest', () => {
    test('notificationLinkCanBeClicked: the notification action link is available', async ({ page }) => {
      await navigateTo(page, '/notification_message');
      await expect(page.getByRole('link', { name: 'Click here' })).toBeVisible();
    });

    test('notificationDisplaysOneDocumentedOutcome: clicking the link shows a documented outcome', async ({ page }) => {
      await navigateTo(page, '/notification_message');
      await page.getByRole('link', { name: 'Click here' }).click();
      const message = page.locator('#flash');
      await expect(message).toContainText(/Action successful|Action unsuccesful/);
    });
  });
});
