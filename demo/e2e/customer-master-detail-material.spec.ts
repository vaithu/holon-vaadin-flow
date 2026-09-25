import { expect, test, type Page } from '@playwright/test';

const route = '/customer-master-detail-material';

test.use({
  viewport: { width: 1920, height: 1080 }
});

async function openStableView(page: Page) {
  for (let attempt = 0; attempt < 4; attempt += 1) {
    await page.goto(route, { waitUntil: 'domcontentloaded' });

    const body = page.locator('body');
    await expect(body).toBeVisible({ timeout: 30_000 });

    const bodyText = await body.innerText();
    if (bodyText.includes('There was an exception while trying to navigate')) {
      if (attempt === 3) {
        throw new Error(bodyText);
      }
      continue;
    }

    try {
      await expect(page.getByText('Customers').first()).toBeVisible({ timeout: 15_000 });
      await expect(page.getByRole('tab', { name: /Overview/ })).toBeVisible({ timeout: 15_000 });
      await expect(page.getByRole('button', { name: 'Send via WhatsApp' })).toBeVisible({ timeout: 15_000 });
      return;
    } catch (error) {
      if (attempt === 3) {
        throw error;
      }
    }
  }
}

test.describe('Customer Master-Detail Material View visual comparison', () => {
  test.beforeEach(async ({ page }, testInfo) => {
    testInfo.setTimeout(120_000);
    await openStableView(page);
  });

  test('matches the default overview visual baseline', async ({ page }) => {
    await expect(page).toHaveScreenshot('customer-master-detail-material-overview.png', {
      animations: 'disabled',
      maxDiffPixelRatio: 0.01,
    });
  });

  test('matches the orders tab visual baseline', async ({ page }) => {
    const ordersTab = page.getByRole('tab', { name: /Orders/ });
    await ordersTab.click();
    await expect(ordersTab).toHaveAttribute('aria-selected', 'true');
    await expect(page.getByText('Open orders')).toBeVisible({ timeout: 20_000 });

    await expect(page).toHaveScreenshot('customer-master-detail-material-orders.png', {
      animations: 'disabled',
      maxDiffPixelRatio: 0.01,
    });
  });
});






