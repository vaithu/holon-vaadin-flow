import { test, expect, Page } from '@playwright/test';
import * as path from 'path';
import * as url from 'url';

/**
 * ShellColor <-> mockup parity.
 *
 * For each of the 14 ShellColor themes this spec:
 *   1. opens the reference mockup in docs/ and reads the COMPUTED style of its
 *      sidebar / item / active item / hover / section label / badge;
 *   2. opens the live demo app, applies that theme's `shell-color-*` class and
 *      reads the COMPUTED style of the equivalent parts of a real
 *      <vaadin-side-nav> rendered by the real component + real stylesheets;
 *   3. asserts the two are identical.
 *
 * Computed values (not screenshots) are compared so a failure names the exact
 * CSS property that drifted, and so the test is immune to font rendering and
 * anti-aliasing noise.
 *
 * Source of truth for the token values is
 * iyen-core/src/main/resources/META-INF/resources/shell-color-themes.css.
 */

const HERE = path.dirname(url.fileURLToPath(import.meta.url));
const DOCS = path.resolve(HERE, '..', '..', 'docs');
const fileUrl = (name: string) => url.pathToFileURL(path.join(DOCS, name)).href;

/** Selectors identifying the equivalent elements inside each mockup. */
interface Mockup {
  file: string;
  /** Index into `.card` when a single file holds several variants. */
  card?: number;
  /** Element whose width + background define the sidebar surface. */
  surface: string;
  /** Element carrying the nav-list padding (often the same as `surface`). */
  nav: string;
  item: string;
  active: string;
  label: string;
  badge?: string;
  /** True when the mockup pins an explicit item height (vs. padding-driven). */
  fixedHeight: boolean;
}

interface Theme {
  /** ShellColor.cssClassName() */
  cls: string;
  mockup: Mockup;
}

/** The eight accent themes all live in one file and share identical chrome. */
const accent = (card: number): Mockup => ({
  file: 'vaadin_applayout_mockups(1).html',
  card,
  surface: '.drawer',
  nav: '.nav',
  item: '.nav-item:not(.active)',
  active: '.nav-item.active',
  label: '.nav-section',
  fixedHeight: true,
});

const THEMES: Theme[] = [
  { cls: 'shell-color-blue',   mockup: accent(0) },
  { cls: 'shell-color-indigo', mockup: accent(1) },
  { cls: 'shell-color-teal',   mockup: accent(2) },
  { cls: 'shell-color-red',    mockup: accent(3) },
  { cls: 'shell-color-cyan',   mockup: accent(4) },
  { cls: 'shell-color-amber',  mockup: accent(5) },
  { cls: 'shell-color-pink',   mockup: accent(6) },
  { cls: 'shell-color-slate',  mockup: accent(7) },

  {
    cls: 'shell-color-modern-saas',
    mockup: {
      file: '04-modern-saas-sidenav.html',
      surface: '.sidenav', nav: '.sidenav',
      item: '.item:not(.active)', active: '.item.active', label: '.group',
      fixedHeight: false,
    },
  },
  {
    cls: 'shell-color-material',
    mockup: {
      file: '05-material-sidenav.html',
      surface: '.sidenav', nav: '.sidenav',
      item: '.item:not(.active)', active: '.item.active', label: '.group',
      badge: '.badge',
      fixedHeight: true,
    },
  },
  {
    cls: 'shell-color-enterprise',
    mockup: {
      file: '06-enterprise-sidenav.html',
      surface: '.sidenav', nav: '.sidenav',
      item: '.item:not(.active)', active: '.item.active', label: '.group',
      badge: '.badge',
      fixedHeight: false,
    },
  },
  {
    cls: 'shell-color-gradient',
    mockup: {
      file: '08-gradient-sidenav.html',
      surface: '.s', nav: '.s',
      item: '.i:not(.a)', active: '.i.a', label: '.g',
      badge: '.bd',
      fixedHeight: false,
    },
  },
  {
    cls: 'shell-color-collapsible-dark',
    mockup: {
      file: '11-collapsible-style-sidenav.html',
      surface: '.s', nav: '.s',
      item: '.i:not(.a)', active: '.i.a', label: '.g',
      fixedHeight: false,
    },
  },
  {
    cls: 'shell-color-premium-blue',
    mockup: {
      file: '12-premium-blue-sidenav.html',
      surface: '.s', nav: '.s',
      item: '.i:not(.a)', active: '.i.a', label: '.g',
      badge: '.bd',
      fixedHeight: false,
    },
  },
];

/** Normalised style snapshot, shared shape for both mockup and live readings. */
interface Snapshot {
  surfaceWidth: string;
  surfaceBackground: string;
  surfaceShadow: string;
  navPadding: string;
  itemPadding: string;
  itemRadius: string;
  itemGap: string;
  itemFontSize: string;
  itemColor: string;
  itemMargin: string;
  itemHeight: string | null;
  activeBackground: string;
  activeColor: string;
  activeFontWeight: string;
  activeShadow: string;
  activeBorderLeft: string;
  activeBorderRight: string;
  hoverBackground: string;
  hoverColor: string;
  labelColor: string;
  labelFontSize: string;
  labelLetterSpacing: string;
  labelPadding: string;
  badge: Record<string, string> | null;
}

/* ───────────────────────── mockup side ───────────────────────── */

async function readMockup(page: Page, m: Mockup): Promise<Snapshot> {
  await page.goto(fileUrl(m.file));
  return page.evaluate((mm) => {
    const root: ParentElement =
      mm.card === undefined
        ? document
        : (document.querySelectorAll('.card')[mm.card] as unknown as ParentElement);
    type ParentElement = Document | Element;

    const q = (sel: string): Element => {
      const el = (root as Element).querySelector(sel);
      if (!el) throw new Error(`mockup selector not found: ${sel} in ${mm.file}`);
      return el;
    };
    const cs = (sel: string) => getComputedStyle(q(sel));

    const surface = cs(mm.surface);
    const nav = cs(mm.nav);
    const item = cs(mm.item);
    const active = cs(mm.active);
    const label = cs(mm.label);

    // The mockups express hover in a stylesheet rule rather than inline, so the
    // hover appearance is read by matching the `:hover` rule in the document's
    // own CSS rather than by synthesising a pointer event.
    const hoverDecl = (() => {
      const itemHoverSelectors = [mm.item.replace(/:not\([^)]*\)/g, '')];
      for (const sheet of Array.from(document.styleSheets)) {
        let rules: CSSRuleList;
        try { rules = (sheet as CSSStyleSheet).cssRules; } catch { continue; }
        for (const rule of Array.from(rules)) {
          const r = rule as CSSStyleRule;
          if (!r.selectorText || !r.selectorText.includes(':hover')) continue;
          const base = r.selectorText.split(':hover')[0].trim();
          if (itemHoverSelectors.some((s) => s.trim() === base)) return r.style;
        }
      }
      return null;
    })();

    const toRgb = (v: string) => {
      if (!v) return '';
      const d = document.createElement('div');
      d.style.color = v;
      document.body.appendChild(d);
      const out = getComputedStyle(d).color;
      d.remove();
      return out;
    };
    const toBg = (v: string) => {
      if (!v) return '';
      const d = document.createElement('div');
      d.style.background = v;
      document.body.appendChild(d);
      const out = getComputedStyle(d).backgroundColor;
      d.remove();
      return out;
    };

    const background = (s: CSSStyleDeclaration) =>
      s.backgroundImage && s.backgroundImage !== 'none' ? s.backgroundImage : s.backgroundColor;

    const badge = mm.badge
      ? (() => {
          const b = cs(mm.badge!);
          return {
            background: b.backgroundColor,
            color: b.color,
            radius: b.borderTopLeftRadius,
            padding: `${b.paddingTop} ${b.paddingRight} ${b.paddingBottom} ${b.paddingLeft}`,
            fontSize: b.fontSize,
            fontWeight: b.fontWeight,
          };
        })()
      : null;

    return {
      surfaceWidth: surface.width,
      surfaceBackground: background(surface),
      surfaceShadow: surface.boxShadow,
      navPadding: `${nav.paddingTop} ${nav.paddingRight} ${nav.paddingBottom} ${nav.paddingLeft}`,
      itemPadding: `${item.paddingTop} ${item.paddingRight} ${item.paddingBottom} ${item.paddingLeft}`,
      itemRadius: item.borderTopLeftRadius,
      itemGap: item.columnGap,
      itemFontSize: item.fontSize,
      itemColor: item.color,
      itemMargin: `${item.marginTop} ${item.marginBottom}`,
      itemHeight: mm.fixedHeight ? item.height : null,
      activeBackground: background(active),
      activeColor: active.color,
      activeFontWeight: active.fontWeight,
      activeShadow: active.boxShadow,
      activeBorderLeft: `${active.borderLeftWidth} ${active.borderLeftStyle} ${active.borderLeftColor}`,
      activeBorderRight: `${active.borderRightWidth} ${active.borderRightStyle} ${active.borderRightColor}`,
      hoverBackground: hoverDecl ? toBg(hoverDecl.background || hoverDecl.backgroundColor) : '',
      hoverColor: hoverDecl && hoverDecl.color ? toRgb(hoverDecl.color) : '',
      labelColor: label.color,
      labelFontSize: label.fontSize,
      labelLetterSpacing: label.letterSpacing,
      labelPadding: `${label.paddingTop} ${label.paddingRight} ${label.paddingBottom} ${label.paddingLeft}`,
      badge,
    } as any;
  }, m);
}

/* ───────────────────────── live side ───────────────────────── */

/**
 * Builds a probe inside the running demo page: a real `.sidenav-host` wrapper
 * containing a real <vaadin-side-nav> with a label, a plain item, an active
 * item and a badge. Using freshly created components rather than the demo's own
 * menu keeps the assertions independent of how DemoMainLayout happens to be
 * structured, while still exercising the real custom elements and the real
 * menu.css / shell-color-themes.css served by the app.
 */
async function readLive(page: Page, cls: string, hasBadge: boolean): Promise<Snapshot> {
  return page.evaluate(
    async ({ cls, hasBadge }) => {
      document.getElementById('shellcolor-probe')?.remove();

      const host = document.createElement('div');
      host.id = 'shellcolor-probe';
      host.className = `sidenav-host ${cls}`;
      host.style.position = 'absolute';
      host.style.left = '-9999px';
      host.style.top = '0';
      host.style.height = '600px';
      host.style.width = 'var(--sidenav-width)';

      const nav = document.createElement('vaadin-side-nav');

      const label = document.createElement('span');
      label.setAttribute('slot', 'label');
      label.textContent = 'Workspace';
      nav.appendChild(label);

      const mkItem = (text: string, current: boolean) => {
        const it = document.createElement('vaadin-side-nav-item');
        // vaadin-side-nav-item derives [current] from the router location, and
        // overwrites whatever we set by hand. Pointing the active item at the
        // page we are already on is the only way to make it genuinely current.
        it.setAttribute(
          'path',
          current ? location.pathname + location.search : '/probe-' + text,
        );
        const icon = document.createElement('span');
        icon.setAttribute('slot', 'prefix');
        icon.textContent = 'o';
        it.appendChild(icon);
        it.appendChild(document.createTextNode(text));
        return it;
      };

      const activeItem = mkItem('active', true);
      const plainItem = mkItem('plain', false);
      const hoverItem = mkItem('hover', false);
      nav.appendChild(activeItem);
      nav.appendChild(plainItem);
      nav.appendChild(hoverItem);

      if (hasBadge) {
        const badge = document.createElement('span');
        badge.setAttribute('slot', 'suffix');
        badge.className = 'sidenav-item__badge';
        badge.textContent = '24';
        plainItem.appendChild(badge);
      }

      host.appendChild(nav);
      document.body.appendChild(host);

      await (customElements.whenDefined('vaadin-side-nav-item') as Promise<void>);
      await new Promise((r) => requestAnimationFrame(() => requestAnimationFrame(r)));

      if (!activeItem.hasAttribute('current')) {
        throw new Error(
          'probe active item did not become [current] (path=' +
            activeItem.getAttribute('path') +
            ', location=' +
            location.pathname +
            ')',
        );
      }

      const linkOf = (it: Element) =>
        it.shadowRoot!.querySelector('[part~="link"]') as HTMLElement;

      const hostCs = getComputedStyle(host);
      const navCs = getComputedStyle(nav);
      const item = getComputedStyle(linkOf(plainItem));
      const active = getComputedStyle(linkOf(activeItem));
      const label_ = getComputedStyle(label);

      // Hover is read from the cascade by resolving the rule against the real
      // element, since :hover cannot be forced from script.
      const hoverBg = hostCs.getPropertyValue('--sidenav-item-hover-bg').trim();
      const hoverFg = hostCs.getPropertyValue('--sidenav-item-hover-color').trim();
      const resolve = (v: string, prop: 'color' | 'background') => {
        if (!v) return '';
        const d = document.createElement('div');
        (d.style as any)[prop] = v;
        document.body.appendChild(d);
        const out =
          prop === 'color'
            ? getComputedStyle(d).color
            : getComputedStyle(d).backgroundColor;
        d.remove();
        return out;
      };

      const background = (s: CSSStyleDeclaration) =>
        s.backgroundImage && s.backgroundImage !== 'none' ? s.backgroundImage : s.backgroundColor;

      const badgeEl = hasBadge
        ? (plainItem.querySelector('.sidenav-item__badge') as HTMLElement)
        : null;
      const badge = badgeEl
        ? (() => {
            const b = getComputedStyle(badgeEl);
            return {
              background: b.backgroundColor,
              color: b.color,
              radius: b.borderTopLeftRadius,
              padding: `${b.paddingTop} ${b.paddingRight} ${b.paddingBottom} ${b.paddingLeft}`,
              fontSize: b.fontSize,
              fontWeight: b.fontWeight,
            };
          })()
        : null;

      const snap = {
        // The sidebar width is a token, because the live host lives in the
        // AppLayout drawer whose box is sized from --sidenav-width.
        surfaceWidth: hostCs.getPropertyValue('--sidenav-width').trim(),
        surfaceBackground: background(hostCs),
        surfaceShadow: hostCs.boxShadow,
        navPadding: `${navCs.paddingTop} ${navCs.paddingRight} ${navCs.paddingBottom} ${navCs.paddingLeft}`,
        itemPadding: `${item.paddingTop} ${item.paddingRight} ${item.paddingBottom} ${item.paddingLeft}`,
        itemRadius: item.borderTopLeftRadius,
        itemGap: item.columnGap,
        itemFontSize: item.fontSize,
        itemColor: item.color,
        itemMargin: `${getComputedStyle(plainItem).marginTop} ${getComputedStyle(plainItem).marginBottom}`,
        itemHeight: item.minHeight,
        activeBackground: background(active),
        activeColor: active.color,
        activeFontWeight: active.fontWeight,
        activeShadow: active.boxShadow,
        activeBorderLeft: `${active.borderLeftWidth} ${active.borderLeftStyle} ${active.borderLeftColor}`,
        activeBorderRight: `${active.borderRightWidth} ${active.borderRightStyle} ${active.borderRightColor}`,
        hoverBackground: resolve(hoverBg, 'background'),
        hoverColor: resolve(hoverFg, 'color'),
        labelColor: label_.color,
        labelFontSize: label_.fontSize,
        labelLetterSpacing: label_.letterSpacing,
        labelPadding: `${label_.paddingTop} ${label_.paddingRight} ${label_.paddingBottom} ${label_.paddingLeft}`,
        badge,
      };

      host.remove();
      return snap as any;
    },
    { cls, hasBadge },
  );
}

/* ───────────────────────── the test ───────────────────────── */

/**
 * Normalises a `"<width> <style> <color>"` triple so that two borders that
 * paint nothing compare equal.
 *
 * A zero-width border renders identically no matter what its style and colour
 * say, and the two sides express "no accent" differently: the mockups simply
 * omit the declaration (so the UA default `none` / `currentColor` survives),
 * whereas the token-driven rule in menu.css always writes a shorthand and
 * zeroes only the width. Comparing the raw triples would flag that as a
 * mismatch even though the rendered pixels are identical.
 */
function border(v: string): string {
  const width = v.trim().split(/\s+/)[0];
  return parseFloat(width) === 0 ? 'none' : v;
}

test.describe('ShellColor themes match their docs/ mockups', () => {
  for (const theme of THEMES) {
    test(`${theme.cls} == ${theme.mockup.file}${
      theme.mockup.card !== undefined ? ` [card ${theme.mockup.card + 1}]` : ''
    }`, async ({ page }) => {
      const expected = await readMockup(page, theme.mockup);

      await page.goto('/');
      await page.waitForSelector('vaadin-app-layout');
      const actual = await readLive(page, theme.cls, !!theme.mockup.badge);

      // Surface
      expect.soft(actual.surfaceWidth, 'sidebar width').toBe(expected.surfaceWidth);
      expect.soft(actual.surfaceBackground, 'sidebar background').toBe(expected.surfaceBackground);

      // Item geometry
      expect.soft(actual.itemPadding, 'item padding').toBe(expected.itemPadding);
      expect.soft(actual.itemRadius, 'item border-radius').toBe(expected.itemRadius);
      expect.soft(actual.itemGap, 'item gap').toBe(expected.itemGap);
      expect.soft(actual.itemFontSize, 'item font-size').toBe(expected.itemFontSize);
      expect.soft(actual.itemColor, 'item color').toBe(expected.itemColor);
      expect.soft(actual.itemMargin, 'item margin').toBe(expected.itemMargin);
      expect.soft(actual.navPadding, 'nav padding').toBe(expected.navPadding);
      if (expected.itemHeight) {
        expect.soft(actual.itemHeight, 'item height').toBe(expected.itemHeight);
      }

      // Active item
      expect.soft(actual.activeBackground, 'active background').toBe(expected.activeBackground);
      expect.soft(actual.activeColor, 'active color').toBe(expected.activeColor);
      expect.soft(actual.activeFontWeight, 'active font-weight').toBe(expected.activeFontWeight);
      expect.soft(actual.activeShadow, 'active box-shadow').toBe(expected.activeShadow);
      expect.soft(border(actual.activeBorderLeft), 'active border-left')
        .toBe(border(expected.activeBorderLeft));
      expect.soft(border(actual.activeBorderRight), 'active border-right')
        .toBe(border(expected.activeBorderRight));

      // Hover (only where the mockup actually declares one)
      if (expected.hoverBackground) {
        expect.soft(actual.hoverBackground, 'hover background').toBe(expected.hoverBackground);
      }
      if (expected.hoverColor) {
        expect.soft(actual.hoverColor, 'hover color').toBe(expected.hoverColor);
      }

      // Section label
      expect.soft(actual.labelColor, 'label color').toBe(expected.labelColor);
      expect.soft(actual.labelFontSize, 'label font-size').toBe(expected.labelFontSize);
      expect.soft(actual.labelLetterSpacing, 'label letter-spacing').toBe(expected.labelLetterSpacing);
      expect.soft(actual.labelPadding, 'label padding').toBe(expected.labelPadding);

      // Badge
      if (expected.badge) {
        expect.soft(actual.badge, 'badge').toEqual(expected.badge);
      }
    });
  }

  test('drawer width follows the theme applied to the SideNav builder', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('vaadin-app-layout');
    await page.waitForSelector('.sidenav-host');

    // DemoMainLayout applies a theme via SideNavBuilder#colorTheme. The
    // configurator mirrors the class onto <vaadin-app-layout> so ::part(drawer)
    // can resolve --sidenav-width; without the mirror the drawer stays 225px.
    //
    // Which theme the demo happens to use is a product decision that changes, so
    // this asserts the *wiring* instead of a specific palette: whatever theme is
    // on the host must also be on the app-layout, and the drawer must be exactly
    // the width that theme declares.
    const info = await page.evaluate(() => {
      const al = document.querySelector('vaadin-app-layout')!;
      const host = document.querySelector('.sidenav-host') as HTMLElement;
      const drawer = al.shadowRoot!.querySelector('[part~="drawer"]') as HTMLElement;
      const ds = getComputedStyle(drawer);
      const hs = getComputedStyle(host);
      return {
        appLayoutClasses: [...al.classList].filter((c) => c.startsWith('shell-color-')),
        hostClasses: [...host.classList].filter((c) => c.startsWith('shell-color-')),
        themeWidth: hs.getPropertyValue('--sidenav-width').trim(),
        drawerWidth: ds.width,
        drawerBackground: ds.backgroundColor,
        drawerPadding: ds.paddingTop,
        // A themed sidebar paints either a colour or a gradient; either way the
        // host must not be left at the default transparent/white.
        hostSurface: hs.backgroundImage !== 'none' ? hs.backgroundImage : hs.backgroundColor,
      };
    });

    expect(info.hostClasses, 'demo applies a ShellColor').toHaveLength(1);
    expect(info.appLayoutClasses, 'theme class mirrored onto vaadin-app-layout')
      .toEqual(info.hostClasses);
    expect(info.drawerWidth, 'drawer width follows --sidenav-width')
      .toBe(info.themeWidth);
    // The drawer must not paint its own surface — .sidenav-host owns it, so a
    // light drawer can never show through behind a dark sidebar.
    expect(info.drawerBackground, 'drawer is transparent').toBe('rgba(0, 0, 0, 0)');
    expect(info.drawerPadding, 'drawer has no padding').toBe('0px');
    expect(info.hostSurface, 'sidenav-host paints the themed surface')
      .not.toBe('rgba(0, 0, 0, 0)');
  });
});
