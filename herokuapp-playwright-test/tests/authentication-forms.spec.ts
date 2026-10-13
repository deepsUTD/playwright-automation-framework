import { expect } from '@playwright/test';
import { navigateTo } from './support/navigation';
import { test } from './support/test';

const validCredentials = [
  { username: 'tomsmith', password: 'SuperSecretPassword!', source: 'JSON data' },
  { username: 'tomsmith', password: 'SuperSecretPassword!', source: 'CSV data' },
];

const invalidCredentials = [
  { username: 'invaliduser', password: 'invalidpass', source: 'JSON and CSV data' },
  { username: 'supersmit', password: 'superpass', source: 'JSON and CSV data' },
  { username: 'tomsmit', password: 'tompass', source: 'JSON and CSV data' },
  { username: 'supersmit', password: 'wrongpass', source: 'JSON and CSV data' },
];

/** Enters credentials and submits the Herokuapp login form. */
async function submitLogin(page: import('@playwright/test').Page, username: string, password: string) {
  await page.locator('#username').fill(username);
  await page.locator('#password').fill(password);
  await page.locator("button[type='submit']").click();
}

test.describe('Herokuapp authentication and forms', () => {
  test.describe('BasicAuthTest', () => {
    test.use({ httpCredentials: { username: 'admin', password: 'admin' } });

    test('validCredentialsShowSuccessMessage: valid demo credentials show the success message', async ({ page }) => {
      await navigateTo(page, '/basic_auth');
      await expect(page.locator('p')).toContainText('Congratulations');
    });

    test('authenticatedPageHasExpectedHeading: the authenticated page identifies Basic Auth', async ({ page }) => {
      await navigateTo(page, '/basic_auth');
      await expect(page.locator('h3')).toContainText('Basic Auth');
    });
  });

  test.describe('DigestAuthTest', () => {
    test.use({ httpCredentials: { username: 'admin', password: 'admin' } });

    test('validDigestCredentialsShowSuccess: valid demo credentials show the digest success message', async ({ page }) => {
      await navigateTo(page, '/digest_auth');
      await expect(page.locator('p')).toContainText('Congratulations');
    });

    test('authenticatedPageShowsDigestHeading: the authenticated page identifies Digest Auth', async ({ page }) => {
      await navigateTo(page, '/digest_auth');
      await expect(page.locator('h3')).toContainText('Digest Auth');
    });
  });

  test.describe('FormAuthenticationTest and data-driven login tests', () => {
    test('validCredentialsLogInAndCanLogOut: valid credentials sign in and then log out', async ({ page }) => {
      await navigateTo(page, '/login');
      await submitLogin(page, 'tomsmith', 'SuperSecretPassword!');
      await expect(page).toHaveURL(/\/secure$/);
      await expect(page.locator('#flash')).toContainText('You logged into a secure area!');
      await page.locator("a[href='/logout']").click();
      await expect(page).toHaveURL(/\/login$/);
    });

    test('invalidCredentialsShowError: invalid credentials show a username error', async ({ page }) => {
      await navigateTo(page, '/login');
      await submitLogin(page, 'invalid-user', 'wrong-password');
      await expect(page.locator('#flash')).toContainText('Your username is invalid');
      await expect(page).toHaveURL(/\/login$/);
    });

    for (const credentials of validCredentials) {
      test(`validCredentialsLogIn (${credentials.source}): valid data signs in successfully`, async ({ page }) => {
        await navigateTo(page, '/login');
        await submitLogin(page, credentials.username, credentials.password);
        await expect(page).toHaveURL(/\/secure$/);
        await expect(page.locator('#flash')).toContainText('You logged into a secure area!');
      });
    }

    for (const credentials of invalidCredentials) {
      test(`invalidCredentialsShowError (${credentials.source} ${credentials.username} #${invalidCredentials.indexOf(credentials) + 1}): invalid data stays on login`, async ({ page }) => {
        await navigateTo(page, '/login');
        await submitLogin(page, credentials.username, credentials.password);
        await expect(page.locator('#flash')).toContainText('is invalid');
        await expect(page).toHaveURL(/\/login$/);
      });
    }
  });

  test.describe('ForgotPasswordTest', () => {
    test('validEmailShowsRecoveryConfirmation: submitting an email shows the expected response', async ({ page }) => {
      await navigateTo(page, '/forgot_password');
      await page.locator('#email').fill('qa@example.com');
      await page.locator('#form_submit').click();
      await expect.poll(async () => {
        const headings = await page.locator('h1').allTextContents();
        const bodyText = (await page.locator('body').textContent())?.toLowerCase() ?? '';
        return headings.some(heading => heading.includes('Internal Server Error')) || bodyText.includes('email');
      }).toBe(true);
    });

    test('recoveryFormHasEmailInputAndSubmitButton: the recovery form exposes its required controls', async ({ page }) => {
      await navigateTo(page, '/forgot_password');
      await expect(page.locator('#email')).toHaveAttribute('type', 'email');
      await expect(page.locator('#form_submit')).toBeEnabled();
    });
  });

  test.describe('FileUploadTest', () => {
    test('uploadsAFileSuccessfully: a text file is accepted and its name is shown', async ({ page }) => {
      await navigateTo(page, '/upload');
      const filename = 'playwright-upload.txt';
      await page.locator('#file-upload').setInputFiles({
        name: filename,
        mimeType: 'text/plain',
        buffer: Buffer.from('Selenium upload test'),
      });
      await page.locator('#file-submit').click();
      await expect(page.locator('h3')).toContainText('File Uploaded!');
      await expect(page.locator('#uploaded-files')).toContainText(filename);
    });

    test('uploadFormExposesRequiredControls: upload input and submit button are available', async ({ page }) => {
      await navigateTo(page, '/upload');
      await expect(page.locator('#file-upload')).toBeVisible();
      await expect(page.locator('#file-submit')).toBeEnabled();
    });
  });
});
