/**
 * shell-color-gallery.spec.ts
 *
 * Not an assertion test — a *screenshot generator*. It renders a realistic
 * SideNav (brand logo, section labels, icons, an active item, a badge, a
 * footer) once per ShellColor theme inside the running demo app, so the real
 * menu.css + shell-color-themes.css do the painting, and writes one PNG per
 * theme plus a combined contact sheet into `e2e/gallery/`.
 *
 * Run with:
 *   node node_modules/playwright/cli.js test shell-color-gallery.spec.ts
 */
import { test, expect, Page } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';
import * as url from 'url';

const HERE = path.dirname(url.fileURLToPath(import.meta.url));
const OUT = path.resolve(HERE, 'gallery');

/** Every ShellColor constant, with the sidebar width its mockup specifies. */
const THEMES: { cls: string; label: string; width: number }[] = [
  { cls: 'shell-color-blue', label: 'BLUE', width: 225 },
  { cls: 'shell-color-indigo', label: 'INDIGO', width: 225 },
  { cls: 'shell-color-teal', label: 'TEAL', width: 225 },
  { cls: 'shell-color-red', label: 'RED', width: 225 },
  { cls: 'shell-color-cyan', label: 'CYAN', width: 225 },
  { cls: 'shell-color-amber', label: 'AMBER', width: 225 },
  { cls: 'shell-color-pink', label: 'PINK', width: 225 },
  { cls: 'shell-color-slate', label: 'SLATE', width: 225 },
  { cls: 'shell-color-modern-saas', label: 'MODERN_SAAS', width: 275 },
  { cls: 'shell-color-material', label: 'MATERIAL', width: 280 },
  { cls: 'shell-color-enterprise', label: 'ENTERPRISE', width: 285 },
  { cls: 'shell-color-gradient', label: 'GRADIENT', width: 280 },
  { cls: 'shell-color-collapsible-dark', label: 'COLLAPSIBLE_DARK', width: 255 },
  { cls: 'shell-color-premium-blue', label: 'PREMIUM_BLUE', width: 285 },
];

/**
 * Builds the probe sidebar in the page and returns nothing; the caller
 * screenshots `#gallery-probe`. Items mirror the structure the mockups show:
 * a brand logo row, a section label, four items (one current, one badged),
 * a second section, two more items, and a footer.
 */
async function render(page: Page, cls: string) {
  await page.evaluate(async (cls) => {
    document.getElementById('gallery-stage')?.remove();

    const stage = document.createElement('div');
    stage.id = 'gallery-stage';
    // Fixed, on top of the app, so the screenshot is just the sidebar.
    stage.style.cssText =
      'position:fixed;inset:0;z-index:99999;background:#f2f4f7;' +
      'display:flex;align-items:flex-start;padding:0;';

    const host = document.createElement('div');
    host.id = 'gallery-probe';
    host.className = `sidenav-host ${cls}`;
    // Let the content set the height so no theme clips its last item.
    host.style.minHeight = '560px';
    host.style.width = 'var(--sidenav-width, 225px)';
    host.style.display = 'flex';
    host.style.flexDirection = 'column';

    // Brand logo row, the same markup AppBar uses.
    const brand = document.createElement('div');
    brand.className = 'app-bar__brand';
    brand.style.cssText =
      'display:flex;align-items:center;gap:10px;padding:14px 14px 6px;';
    const logo = document.createElement('div');
    logo.className = 'app-bar__brand-logo';
    logo.textContent = 'A';
    logo.style.cssText =
      'display:flex;align-items:center;justify-content:center;font-weight:700;';
    const name = document.createElement('span');
    name.textContent = 'Acme Suite';
    // Hover colour is the right token for brand text: it is #fff on every dark
    // theme and the brand hue on every light one, so the wordmark stays legible
    // against the sidebar surface (the active colour is not — on GRADIENT it is
    // the dark purple used *on* the white active pill).
    name.style.cssText =
      'font-weight:650;font-size:15px;' +
      'color:var(--sidenav-item-hover-color,var(--sidenav-item-color,#101828));';
    brand.append(logo, name);

    const nav = document.createElement('vaadin-side-nav');

    const mkLabel = (t: string) => {
      const s = document.createElement('span');
      s.setAttribute('slot', 'label');
      s.textContent = t;
      return s;
    };

    const mkItem = (
      text: string,
      glyph: string,
      opts: { current?: boolean; badge?: string } = {},
    ) => {
      const it = document.createElement('vaadin-side-nav-item');
      // [current] is derived from the router location, so the only reliable way
      // to make an item genuinely current is to point it at the current URL.
      it.setAttribute(
        'path',
        opts.current ? location.pathname + location.search : '/gallery-' + text,
      );
      const icon = document.createElement('span');
      icon.setAttribute('slot', 'prefix');
      icon.textContent = glyph;
      icon.style.cssText =
        'display:inline-flex;align-items:center;justify-content:center;font-size:15px;';
      it.appendChild(icon);
      it.appendChild(document.createTextNode(text));
      if (opts.badge) {
        const b = document.createElement('span');
        b.setAttribute('slot', 'suffix');
        b.className = 'sidenav-item__badge';
        b.textContent = opts.badge;
        it.appendChild(b);
      }
      return it;
    };

    nav.appendChild(mkLabel('Workspace'));
    nav.appendChild(mkItem('Dashboard', '◈', { current: true }));
    nav.appendChild(mkItem('Orders', '▤', { badge: '24' }));
    nav.appendChild(mkItem('Customers', '◍'));
    nav.appendChild(mkItem('Products', '◆'));

    const nav2 = document.createElement('vaadin-side-nav');
    nav2.appendChild(mkLabel('Insights'));
    nav2.appendChild(mkItem('Reports', '▦'));
    nav2.appendChild(mkItem('Analytics', '◐'));

    const footer = document.createElement('div');
    footer.className = 'sidenav-footer';
    footer.textContent = 'Upgrade to Pro';
    footer.style.cssText += ';text-align:center;font-weight:600;';

    host.append(brand, nav, nav2, footer);
    stage.appendChild(host);
    document.body.appendChild(stage);

    await customElements.whenDefined('vaadin-side-nav-item');
    await new Promise((r) => requestAnimationFrame(() => requestAnimationFrame(r)));
  }, cls);
}

test.describe.configure({ mode: 'serial' });

test('render the ShellColor gallery', async ({ page }) => {
  fs.mkdirSync(OUT, { recursive: true });

  await page.goto('/');
  await page.waitForSelector('vaadin-app-layout', { timeout: 30_000 });

  const shots: { label: string; file: string; width: number }[] = [];

  for (const theme of THEMES) {
    await render(page, theme.cls);

    const probe = page.locator('#gallery-probe');
    await expect(probe).toBeVisible();

    // Confirm the theme actually took effect before we immortalise it.
    const width = await probe.evaluate(
      (el) => getComputedStyle(el).getPropertyValue('--sidenav-width').trim(),
    );
    expect(width, `${theme.cls} width token`).toBe(`${theme.width}px`);

    const file = `${theme.cls}.png`;
    await probe.screenshot({ path: path.join(OUT, file) });
    shots.push({ label: theme.label, file, width: theme.width });
  }

  await page.evaluate(() => document.getElementById('gallery-stage')?.remove());

  // Contact sheet: all 14 side by side, each captioned with its enum constant.
  const cards = shots
    .map(
      (s) => `
    <figure style="margin:0;display:flex;flex-direction:column;gap:8px;">
      <img src="${s.file}" width="${s.width}"
           style="display:block;border:1px solid #d0d5dd;border-radius:10px;
                  box-shadow:0 6px 16px rgb(16 24 40 / .10);" />
      <figcaption style="font:600 12px/1.3 ui-monospace,Menlo,monospace;
                         color:#344054;text-align:center;">
        ShellColor.${s.label}<br />
        <span style="font-weight:400;color:#667085;">${s.width}px</span>
      </figcaption>
    </figure>`,
    )
    .join('');

  fs.writeFileSync(
    path.join(OUT, 'index.html'),
    `<!doctype html><meta charset="utf-8"><title>ShellColor gallery</title>
<body style="margin:0;padding:32px;background:#f9fafb;
             font:14px/1.5 system-ui,-apple-system,Segoe UI,sans-serif;">
  <h1 style="margin:0 0 4px;font-size:20px;color:#101828;">ShellColor navigation themes</h1>
  <p style="margin:0 0 28px;color:#667085;">
    Rendered from the live demo app — real &lt;vaadin-side-nav&gt; painted by
    menu.css + shell-color-themes.css.
  </p>
  <div style="display:flex;flex-wrap:wrap;gap:28px;align-items:flex-start;">${cards}</div>
</body>`,
    'utf8',
  );

  // Single wide strip, handy for pasting into a PR or chat.
  await page.setViewportSize({ width: 1600, height: 900 });
  await page.goto(url.pathToFileURL(path.join(OUT, 'index.html')).href);
  await page.waitForLoadState('networkidle');
  await page.screenshot({ path: path.join(OUT, 'all-themes.png'), fullPage: true });
});
