import { expect, test } from '@playwright/test';

const passwordRecoveryEmailCases = [
  { name: 'plus-addressed email', email: 'reset+automation@example.invalid', isValid: true },
  { name: 'subdomain email', email: 'Reset.User@mail.example.invalid', isValid: true },
  { name: 'missing at sign', email: 'reset.example.invalid', isValid: false },
  { name: 'empty email', email: '', isValid: false },
];

for (const recoveryCase of passwordRecoveryEmailCases) {
  test(`password recovery handles ${recoveryCase.name}`, async ({ page }) => {
    await page.goto('https://demo.nopcommerce.com/passwordrecovery');

    const emailInput = page.getByRole('textbox', { name: 'Your email address:' });
    await emailInput.fill(recoveryCase.email);

    const isValid = await emailInput.evaluate((input) => (input as HTMLInputElement).checkValidity());
    expect(isValid).toBe(recoveryCase.isValid);

    if (recoveryCase.isValid) {
      await page.getByRole('button', { name: 'Recover' }).click();
      await expect(page.getByText('Email not found.', { exact: true })).toBeVisible();
    }
  });
}