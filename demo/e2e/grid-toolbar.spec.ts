import { expect, test } from '@playwright/test';

test.describe('GridToolbar demo', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/grid-toolbar');
    await expect(page.getByTestId('grid-toolbar-demo')).toBeVisible();
    await expect(page.getByText('Wireless Mouse Pro')).toBeVisible();
  });

  test('filters products and applies search text', async ({ page }) => {
    const toolbar = page.getByTestId('grid-toolbar-demo');
    const search = toolbar.getByRole('textbox').first();
    await search.fill('Wireless');
    await expect(page.getByText('Wireless Mouse Pro')).toBeVisible();
    await expect(page.getByText('Ergonomic Office Chair')).toBeHidden();

    await search.fill('');
    await expect(page.getByText('Ergonomic Office Chair')).toBeVisible();
  });

  test('matches the default toolbar visual state', async ({ page }) => {
    await expect(page).toHaveScreenshot('grid-toolbar-default.png', {
      animations: 'disabled',
      maxDiffPixelRatio: 0.01,
    });
  });

  test('switches to selection mode and exposes mobile bulk actions', async ({ page }) => {
    const toolbar = page.getByTestId('grid-toolbar-demo');
    const grid = page.getByTestId('grid-toolbar-products');
    await grid.getByRole('checkbox').nth(1).check();
    await expect(toolbar.getByText('1', { exact: true })).toBeVisible();
    await expect(toolbar.getByRole('button', { name: 'Clear' })).toBeVisible();

    await page.setViewportSize({ width: 380, height: 900 });
    await page.getByRole('button', { name: 'Simulate mobile width (380px)' }).click();
    await expect(toolbar.getByRole('button', { name: 'More bulk actions' })).toBeVisible();
    await toolbar.getByRole('button', { name: 'More bulk actions' }).click();
    await expect(page.getByText('Export', { exact: true })).toBeVisible();
    await expect(page.getByText('Assign', { exact: true })).toBeVisible();
    await expect(page.getByText('Delete', { exact: true })).toBeVisible();

    await expect(page).toHaveScreenshot('grid-toolbar-selected-mobile.png', {
      animations: 'disabled',
      maxDiffPixelRatio: 0.01,
    });

    await toolbar.getByRole('button', { name: 'Export' }).click();
    await expect(page.getByText('Exporting 1 products')).toBeVisible();
    await toolbar.getByRole('button', { name: 'Clear' }).click();
    await expect(toolbar.getByRole('textbox').first()).toBeVisible();
  });
});