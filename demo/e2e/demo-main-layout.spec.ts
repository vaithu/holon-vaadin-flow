/**
 * demo-main-layout.spec.ts
 *
 * Screenshot generator for the real DemoMainLayout shell (MaterialAppBar navbar +
 * GRADIENT-themed drawer), as opposed to shell-color-gallery.spec.ts which renders a
 * synthetic probe. This one proves the *actual* application layout looks right.
 *
 * Also asserts the two things the CSS cleanup is supposed to guarantee:
 *   - the drawer adopts the theme's 280px width (not the old hard-pinned 220px), and
 *   - the drawer paints nothing itself, so .sidenav-host is the only surface.
 */
import { test, expect } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';
import * as url from 'url';

const HERE = path.dirname(url.fileURLToPath(import.meta.url));
const OUT = path.resolve(HERE, 'gallery');

test('DemoMainLayout renders the GRADIENT shell', async ({ page }) => {
  fs.mkdirSync(OUT, { recursive: true });

  await page.setViewportSize({ width: 1440, height: 900 });
  await page.goto('/');
  await page.waitForSelector('vaadin-app-layout', { timeout: 30_000 });
  await page.waitForSelector('.sidenav-host', { timeout: 30_000 });
  // Let the gradient, fonts and the nav items settle before capturing.
  await page.waitForTimeout(1500);

  const info = await page.evaluate(() => {
    const al = document.querySelector('vaadin-app-layout')!;
    const drawer = al.shadowRoot!.querySelector('[part~="drawer"]') as HTMLElement;
    const host = document.querySelector('.sidenav-host') as HTMLElement;
    const ds = getComputedStyle(drawer);
    const hs = getComputedStyle(host);
    return {
      appLayoutClasses: al.className,
      drawerWidth: ds.width,
      drawerBackground: ds.backgroundColor,
      drawerPadding: ds.padding,
      hostBackground: hs.backgroundImage,
      hostClasses: host.className,
      toggleBackground: (() => {
        const t = document.querySelector('.sidenav-collapse-toggle');
        return t ? getComputedStyle(t).backgroundColor : 'absent';
      })(),
    };
  });

  console.log('shell:', JSON.stringify(info, null, 2));

  // The theme class must reach the app-layout, or ::part(drawer) cannot see --sidenav-width.
  expect(info.appLayoutClasses, 'theme mirrored onto app-layout')
    .toContain('shell-color-gradient');
  expect(info.drawerWidth, 'drawer takes the GRADIENT width').toBe('280px');
  expect(info.drawerBackground, 'drawer paints nothing').toBe('rgba(0, 0, 0, 0)');
  expect(info.hostBackground, 'host paints the gradient').toContain('linear-gradient');
  // vaadin-button's Lumo host background would otherwise paint a light strip
  // across the bottom of every dark theme.
  expect(info.toggleBackground, 'collapse toggle is transparent').toBe('rgba(0, 0, 0, 0)');

  await page.screenshot({ path: path.join(OUT, 'demo-main-layout.png') });
  await page
    .locator('.sidenav-host')
    .screenshot({ path: path.join(OUT, 'demo-main-layout-drawer.png') });
});

test('active group + avatar stay legible on the themed shell', async ({ page }) => {
  fs.mkdirSync(OUT, { recursive: true });

  await page.setViewportSize({ width: 1440, height: 900 });
  // Navigate straight to a nested route: its SideNavItem becomes [current] and
  // its enclosing group becomes the "parent of active child". Going by URL is
  // deterministic, unlike clicking through a collapsed group.
  await page.goto('/layout');
  await page.waitForSelector('.sidenav-host', { timeout: 30_000 });
  await page.waitForSelector('vaadin-side-nav-item[current]', { timeout: 30_000 });
  await page.waitForTimeout(1200);

  const nav = await page.evaluate(() => {
    const items = [...document.querySelectorAll('vaadin-side-nav-item')];
    const child = items.find((i) => i.hasAttribute('current'));
    if (!child) return { error: 'no current item' };
    const parent = items.find((i) => i.querySelector('[slot="children"][current]'));
    const linkOf = (el: Element) =>
      getComputedStyle(el.shadowRoot!.querySelector('[part~="link"]')!);
    const c = linkOf(child);
    const p = parent ? linkOf(parent) : null;
    return {
      childColor: c.color,
      childBg: c.backgroundColor,
      parentExpanded: parent ? (parent as Element).hasAttribute('expanded') : null,
      parentColor: p ? p.color : null,
      parentBg: p ? p.backgroundColor : null,
    };
  });

  console.log('nav:', JSON.stringify(nav, null, 2));

  // The child owns the active pill: white background, dark GRADIENT ink.
  expect(nav.childColor, 'active child uses the active foreground').toBe('rgb(76, 29, 149)');

  // The expanded parent must NOT duplicate the pill — that was the bug: it took
  // the white active background but kept the light #dbeafe item colour, leaving
  // near-invisible text on a white pill.
  expect(nav.parentExpanded, 'parent group is expanded').toBe(true);
  expect(nav.parentBg, 'expanded parent does not wear the active pill')
    .toBe('rgba(0, 0, 0, 0)');

  // Avatar must be visible against the coloured MaterialAppBar.
  const avatar = await page.evaluate(() => {
    const a = document.querySelector('vaadin-avatar');
    if (!a) return { error: 'no avatar' };
    const s = getComputedStyle(a);
    return { background: s.backgroundColor, color: s.color };
  });
  console.log('avatar:', JSON.stringify(avatar));
  expect(avatar.background, 'avatar has a visible chip on the colored bar')
    .not.toBe('rgba(0, 0, 0, 0)');
  expect(avatar.color, 'avatar initials use the on-container colour')
    .toBe('rgb(255, 255, 255)');

  await page
    .locator('.sidenav-host')
    .screenshot({ path: path.join(OUT, 'demo-drawer-active-group.png') });
  await page
    .locator('.material-app-bar')
    .screenshot({ path: path.join(OUT, 'demo-appbar.png') });
});
