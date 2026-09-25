import { expect, test, type Locator } from '@playwright/test';

/**
 * Names currently rendered as *visible* grid cells.
 *
 * Vaadin recycles `vaadin-grid-cell-content` elements and leaves stale, hidden
 * copies in the light DOM after a `refreshAll()`. A plain `getByText` matches
 * those too, so every row assertion filters on visibility.
 */
function visibleCells(grid: Locator): Locator {
  return grid.locator('vaadin-grid-cell-content:visible');
}

async function expectRow(grid: Locator, name: string, present: boolean): Promise<void> {
  await expect
    .poll(async () => (await visibleCells(grid).allTextContents()).includes(name), {
      message: `expected "${name}" to be ${present ? 'present' : 'absent'} in the grid`,
    })
    .toBe(present);
}

/**
 * The overflow menu belonging to a given toolbar.
 *
 * Menu items are light-DOM children of `vaadin-context-menu` that get *slotted* into
 * the overlay in its shadow root, so they are not DOM descendants of
 * `vaadin-context-menu-overlay` and cannot be scoped through it. Scoping to the
 * toolbar's own `vaadin-context-menu` also disambiguates the two toolbars.
 */
function overflowMenu(toolbar: Locator): Locator {
  return toolbar.locator('vaadin-context-menu');
}

test.describe('GridToolbar demo', () => {
  // The view renders two independent previews (plain toolbar + toolbar with a
  // DynamicFilterPanel), each with its own Grid, so every assertion must be scoped
  // to the component under test — unscoped locators match both.
  test.beforeEach(async ({ page }) => {
    await page.goto('/grid-toolbar');
    await expect(page.getByTestId('grid-toolbar-demo')).toBeVisible();
    await expectRow(page.getByTestId('grid-toolbar-products'), 'Wireless Mouse Pro', true);
  });

  test('filters products and applies search text', async ({ page }) => {
    const toolbar = page.getByTestId('grid-toolbar-demo');
    const grid = page.getByTestId('grid-toolbar-products');
    const search = toolbar.getByRole('textbox').first();

    await search.fill('Wireless');
    await expectRow(grid, 'Wireless Mouse Pro', true);
    await expectRow(grid, 'Ergonomic Office Chair', false);

    await search.fill('');
    await expectRow(grid, 'Ergonomic Office Chair', true);
  });

  test('search is scoped to its own toolbar', async ({ page }) => {
    // Regression guard: the two previews previously shared a single Grid instance
    // (Vaadin reparents a component added to two containers), and only the filtered
    // toolbar was wired to a value-change listener. Filtering one preview must now
    // leave the other untouched.
    const plainGrid = page.getByTestId('grid-toolbar-products');
    const filteredGrid = page.getByTestId('grid-toolbar-products-filtered');

    await page.getByTestId('grid-toolbar-demo').getByRole('textbox').first().fill('Wireless');

    await expectRow(plainGrid, 'Ergonomic Office Chair', false);
    await expectRow(filteredGrid, 'Ergonomic Office Chair', true);
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
    // Asserting on resolved labels also guards against i18n message codes
    // (e.g. "close.code") leaking into the UI.
    await expect(toolbar.getByText('selected', { exact: true })).toBeVisible();
    await expect(toolbar.getByRole('button', { name: 'Close' })).toBeVisible();

    await page.setViewportSize({ width: 380, height: 900 });
    await page.getByRole('button', { name: 'Simulate mobile width (380px)' }).click();
    await expect(toolbar.getByRole('button', { name: 'More bulk actions' })).toBeVisible();
    await toolbar.getByRole('button', { name: 'More bulk actions' }).click();

    const menu = overflowMenu(toolbar);
    await expect(menu.getByText('Export', { exact: true })).toBeVisible();
    await expect(menu.getByText('Assign', { exact: true })).toBeVisible();
    await expect(menu.getByText('Delete', { exact: true })).toBeVisible();

    await expect(page).toHaveScreenshot('grid-toolbar-selected-mobile.png', {
      animations: 'disabled',
      maxDiffPixelRatio: 0.01,
    });

    // At mobile width the bulk actions live only in the overflow menu.
    await menu.getByText('Export', { exact: true }).click();
    await expect(page.getByText('Exporting 1 products')).toBeVisible();
    await toolbar.getByRole('button', { name: 'Close' }).click();
    await expect(toolbar.getByRole('textbox').first()).toBeVisible();
  });
});
