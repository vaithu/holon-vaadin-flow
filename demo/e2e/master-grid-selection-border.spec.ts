import { expect, test, type Page } from '@playwright/test';

/**
 * Regression test for the "double border" visual defect on the master grid
 * of CustomerMasterDetailMaterialView.
 *
 * Root cause: two independent left-edge accent bars could render at once on
 * the currently-selected master row:
 *   1. The true grid-selection accent — an inset box-shadow on
 *      ::part(first-column-cell mdl-selected), drawn at the far left of the
 *      whole grid (x = 0). This is the one true "selection" indicator.
 *   2. A redundant `.mli.on::before` 3px bar rendered ~75px further right,
 *      at the left edge of the mobile-list-item card itself. This bar was
 *      driven by RowVariant.SELECTED, which reflects business/"active"
 *      status (see CustomerMasterDetailMaterialView#rowVariant) rather than
 *      true grid selection, so it could appear on many/most rows at once.
 *
 * When the actually-selected row also happened to be "active" (the default
 * for demo data), both bars rendered close together and read as a doubled
 * border. Since every demo row is "active", the second bar effectively drew
 * a blue line on every row too.
 *
 * The fix removes the `.mli.on::before` bar entirely (mobile-list-lit-renderer.css),
 * leaving only the single true far-left selection accent.
 */

const route = '/customer-master-detail-material';

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
      await expect(page.locator('vaadin-grid.mdl-master-grid')).toBeVisible({ timeout: 15_000 });
      await expect(page.locator('vaadin-grid-cell-content .mli').first()).toBeVisible({ timeout: 15_000 });
      return;
    } catch (error) {
      if (attempt === 3) {
        throw error;
      }
    }
  }
}

test.describe('Customer Master-Detail Material master grid — selection border', () => {
  test.use({ viewport: { width: 420, height: 700 } });

  test.beforeEach(async ({ page }, testInfo) => {
    testInfo.setTimeout(120_000);
    await openStableView(page);
  });

  test('no .mli.on card draws its own left accent bar (::before)', async ({ page }) => {
    const beforeStyles = await page.evaluate(() => {
      const grid = document.querySelector('vaadin-grid.mdl-master-grid') as HTMLElement;
      const cards = Array.from(grid.querySelectorAll('.mli.on')) as HTMLElement[];
      return cards.map((card) => {
        const before = getComputedStyle(card, '::before');
        return { content: before.content, width: before.width };
      });
    });

    expect(beforeStyles.length).toBeGreaterThan(0);
    for (const style of beforeStyles) {
      // 'none' (no pseudo-element rendered) is the only acceptable value —
      // a real bar would report a quoted empty string ('""') plus a fixed width.
      expect(style.content).toBe('none');
    }
  });

  test('only the truly selected row shows the far-left selection accent', async ({ page }) => {
    const accentedFirstColumnCells = await page.evaluate(() => {
      const grid = document.querySelector('vaadin-grid.mdl-master-grid') as any;
      const rows = grid.shadowRoot.querySelectorAll('tr');
      let count = 0;
      for (const row of rows) {
        const firstCell = row.querySelector('[part~="first-column-cell"]');
        if (!firstCell) continue;
        const shadow = getComputedStyle(firstCell).boxShadow;
        if (shadow && shadow !== 'none') {
          count += 1;
        }
      }
      return count;
    });

    // Exactly one row (the selected master item) should carry the accent —
    // never zero, and never more than one (which would indicate the bug is back).
    expect(accentedFirstColumnCells).toBe(1);
  });

  test('only the truly selected row has a full-row background tint; other rows stay plain', async ({ page }) => {
    const backgrounds = await page.evaluate(() => {
      const grid = document.querySelector('vaadin-grid.mdl-master-grid') as any;
      const rows = Array.from(grid.shadowRoot.querySelectorAll('tr')) as HTMLElement[];
      return rows
        .map((row) => {
          const firstCell = row.querySelector('[part~="first-column-cell"]') as HTMLElement | null;
          const lastCell = row.querySelector('[part~="last-column-cell"]') as HTMLElement | null;
          if (!firstCell || !lastCell || !firstCell.getAttribute('part')?.includes('body-cell')) {
            return null;
          }
          return {
            selected: firstCell.getAttribute('part')?.includes('mdl-selected') ?? false,
            firstCellBg: getComputedStyle(firstCell).backgroundColor,
            lastCellBg: getComputedStyle(lastCell).backgroundColor,
          };
        })
        .filter((r): r is NonNullable<typeof r> => r !== null);
    });

    expect(backgrounds.length).toBeGreaterThan(0);

    const selectedRows = backgrounds.filter((r) => r.selected);
    const otherRows = backgrounds.filter((r) => !r.selected);

    expect(selectedRows.length).toBe(1);
    for (const row of selectedRows) {
      // Both the checkbox column and the content column must share the same
      // non-transparent tint — the whole row, not just the card, is highlighted.
      expect(row.firstCellBg).toBe(row.lastCellBg);
      expect(row.firstCellBg).not.toBe('rgba(0, 0, 0, 0)');
    }

    expect(otherRows.length).toBeGreaterThan(0);
    const selectedBg = selectedRows[0]?.firstCellBg;
    for (const row of otherRows) {
      // Non-selected rows (even "active" ones) must NOT carry the selection tint —
      // their background must differ from the truly selected row's tint.
      expect(row.firstCellBg).not.toBe(selectedBg);
      expect(row.lastCellBg).not.toBe(selectedBg);
    }
  });

  test('hovering the selected row keeps its selection tint (card no longer masks it with hover-grey)', async ({
    page,
  }) => {
    // Regression test: .mli used to paint its own opaque background on hover,
    // which visually covered the <td>'s blue selection tint whenever the user
    // hovered the mouse over the selected row's card (as opposed to the
    // checkbox column) — making the row look unselected. .mli must now stay
    // transparent so the row/cell-level background always shows through.
    const selectedCard = page.locator('vaadin-grid-cell-content .mli.on').first();
    await expect(selectedCard).toBeVisible();

    const bgBeforeHover = await selectedCard.evaluate((el) => getComputedStyle(el).backgroundColor);
    await selectedCard.hover();
    const bgDuringHover = await selectedCard.evaluate((el) => getComputedStyle(el).backgroundColor);

    // The card itself must never paint an opaque background, hovered or not —
    // it should stay transparent so the parent cell's background (which
    // already carries the correct selection/hover colour) is what's visible.
    expect(bgBeforeHover).toBe('rgba(0, 0, 0, 0)');
    expect(bgDuringHover).toBe('rgba(0, 0, 0, 0)');

    const selectedCellBgDuringHover = await page.evaluate(() => {
      const grid = document.querySelector('vaadin-grid.mdl-master-grid') as any;
      const rows = Array.from(grid.shadowRoot.querySelectorAll('tr')) as HTMLElement[];
      const selectedRow = rows.find((row) => {
        const firstCell = row.querySelector('[part~="first-column-cell"]');
        return firstCell?.getAttribute('part')?.includes('mdl-selected') ?? false;
      });
      const firstCell = selectedRow?.querySelector('[part~="first-column-cell"]') as HTMLElement | undefined;
      return firstCell ? getComputedStyle(firstCell).backgroundColor : null;
    });

    // The underlying cell must still show the selection tint (not white, not
    // the plain hover-grey) while the card is hovered.
    expect(selectedCellBgDuringHover).not.toBeNull();
    expect(selectedCellBgDuringHover).not.toBe('rgb(255, 255, 255)');
    expect(selectedCellBgDuringHover).not.toBe('rgb(248, 250, 252)');
  });

  test('hovering a non-selected row tints the full row (checkbox + content column)', async ({ page }) => {
    // All demo rows render with the "mli on" class (RowVariant.SELECTED reflects
    // business "active" status, true for most rows) — .mli no longer carries any
    // background itself either way, so any card other than the truly-selected
    // one (index 0) exercises the same "non-selected row hover" behaviour.
    const otherCard = page.locator('vaadin-grid-cell-content .mli').nth(1);
    await expect(otherCard).toBeVisible();
    await otherCard.hover();

    const backgrounds = await page.evaluate(() => {
      const grid = document.querySelector('vaadin-grid.mdl-master-grid') as any;
      const hoveredRow = grid.shadowRoot.querySelector('tr:hover') as HTMLElement | null;
      if (!hoveredRow) return null;
      const firstCell = hoveredRow.querySelector('[part~="first-column-cell"]') as HTMLElement | null;
      const lastCell = hoveredRow.querySelector('[part~="last-column-cell"]') as HTMLElement | null;
      if (!firstCell || !lastCell) return null;
      return {
        firstCellBg: getComputedStyle(firstCell).backgroundColor,
        lastCellBg: getComputedStyle(lastCell).backgroundColor,
      };
    });

    expect(backgrounds).not.toBeNull();
    expect(backgrounds!.firstCellBg).toBe(backgrounds!.lastCellBg);
    expect(backgrounds!.firstCellBg).not.toBe('rgba(0, 0, 0, 0)');
    expect(backgrounds!.firstCellBg).not.toBe('rgb(255, 255, 255)');
  });

  test('matches the master grid visual baseline (single far-left border only)', async ({ page }) => {
    const grid = page.locator('vaadin-grid.mdl-master-grid');
    await expect(grid).toHaveScreenshot('customer-master-detail-material-master-grid-border.png', {
      animations: 'disabled',
      maxDiffPixelRatio: 0.01,
    });
  });
});
