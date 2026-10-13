import { expect } from '@playwright/test';
import { navigateTo } from './support/navigation';
import { test } from './support/test';

/** Reads all visible data rows in a table as comparable strings. */
async function readRows(page: import('@playwright/test').Page, tableId: string): Promise<string[]> {
  return page.locator(`#${tableId} tbody tr`).allTextContents();
}

/** Reads the last-name column from a data table. */
async function readLastNames(page: import('@playwright/test').Page, tableId: string): Promise<string[]> {
  return page.locator(`#${tableId} tbody tr td:first-child`).allTextContents()
    .then(names => names.map(name => name.trim()));
}

test.describe('Herokuapp tables and downloads', () => {
  test.describe('SortableDataTablesTest', () => {
    test('tablesMeetStructureSortingAndDataIntegrityChecks: both tables sort correctly and preserve their rows', async ({ page }) => {
      await navigateTo(page, '/tables');
      await expect(page.locator('table')).toHaveCount(2);

      for (const tableId of ['table1', 'table2']) {
        const table = page.locator(`#${tableId}`);
        await expect(table.locator('thead th')).toHaveCount(6);
        await expect(table.locator('tbody tr')).toHaveCount(4);
        for (const row of await table.locator('tbody tr').all()) {
          await expect(row.locator('td')).toHaveCount(6);
        }

        const initialRows = await readRows(page, tableId);
        const lastNameHeader = table.locator('thead th').filter({ hasText: 'Last Name' });
        const names = await readLastNames(page, tableId);
        const ascending = [...names].sort((left, right) => left.localeCompare(right, undefined, { sensitivity: 'base' }));
        await lastNameHeader.click();
        await expect.poll(() => readLastNames(page, tableId)).toEqual(ascending);

        const descending = [...ascending].reverse();
        await lastNameHeader.click();
        await expect.poll(() => readLastNames(page, tableId)).toEqual(descending);
        await expect.poll(async () => (await readRows(page, tableId)).sort())
          .toEqual([...initialRows].sort());
      }
    });
  });

  test.describe('FileDownloadTest', () => {
    test('downloadableFilesAreListed: the download page lists files', async ({ page }) => {
      await navigateTo(page, '/download');
      await expect.poll(() => page.locator('#content a').count()).toBeGreaterThan(0);
    });

    test('downloadLinkHasAFileTarget: a download link has a filename and download URL', async ({ page }) => {
      await navigateTo(page, '/download');
      const link = page.locator('#content a').first();
      await expect(link).not.toBeEmpty();
      await expect(link).toHaveAttribute('href', /(?:^|\/)download\/[^/]+$/);
    });
  });

  test.describe('SecureFileDownloadTest', () => {
    test.use({ httpCredentials: { username: 'admin', password: 'admin' } });

    test('secureDownloadPageListsFiles: the authenticated page lists files', async ({ page }) => {
      await navigateTo(page, '/download_secure');
      await expect(page.locator('body')).toContainText('Secure File Downloader');
      await expect.poll(() => page.locator('#content a').count()).toBeGreaterThan(0);
    });

    test('secureDownloadLinksHaveFileNames: all secure download links have names', async ({ page }) => {
      await navigateTo(page, '/download_secure');
      const links = page.locator('#content a');
      await expect.poll(() => links.count()).toBeGreaterThan(0);
      for (const link of await links.all()) {
        await expect(link).not.toBeEmpty();
      }
    });
  });
});
