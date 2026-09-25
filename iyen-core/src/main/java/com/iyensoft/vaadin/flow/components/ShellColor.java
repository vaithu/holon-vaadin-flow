package com.iyensoft.vaadin.flow.components;

/**
 * Predefined color themes for {@link AppShellLayout} and
 * {@link com.iyensoft.vaadin.flow.components.builders.SideNavBuilder}.
 *
 * <p>Applied via {@code Components.appShell().colorTheme(ShellColor.INDIGO)...}
 * or {@code SideNavBuilder.create().colorTheme(ShellColor.MODERN_SAAS)}.
 *
 * <p>Each constant maps 1:1 to a reference mockup in {@code docs/}. The backing
 * stylesheet {@code shell-color-themes.css} is the <b>single source of truth</b>
 * for every theme and contains <em>token values only</em> — the painting rules
 * live in {@code menu.css} (SideNav) and {@code app-bar.css} (AppBar), which are
 * the sole owners of their respective selectors. Themes carry the full mockup
 * appearance: surface, item geometry (width, padding, radius, gap, font size),
 * accent shape, badge shape and footer treatment — not just an accent hue.
 *
 * <p>Fidelity is enforced by {@code demo/e2e/shell-color-themes.spec.ts}, which
 * asserts per theme that the computed style of the live {@code <vaadin-side-nav>}
 * equals the computed style of the corresponding mockup element.
 *
 * <p>The first eight constants are <b>accent</b> themes sharing the neutral
 * white 225px chrome of {@code docs/vaadin_applayout_mockups(1).html}; only the
 * brand color and active-item pill differ. The remaining six are <b>surface</b>
 * presets that replace the sidebar background, item shape and accent geometry
 * outright, each matching one reference layout one-for-one.
 */
public enum ShellColor {

    /** Vivid blue — "StaffOS" style enterprise shells. Mockup card 1. */
    BLUE,

    /** Cool indigo/violet — SaaS / analytics products. Mockup card 2. */
    INDIGO,

    /** Teal — operations / productivity dashboards. Mockup card 3. */
    TEAL,

    /** Red — service-desk / support tooling. Mockup card 4. */
    RED,

    /** Cyan — analytics / monitoring dashboards. Mockup card 5. */
    CYAN,

    /** Amber — sales / CRM tooling. Mockup card 6. */
    AMBER,

    /** Pink/rose — people / HR tooling. Mockup card 7. */
    PINK,

    /** Neutral slate — dense admin / enterprise consoles. Mockup card 8. */
    SLATE,

    /**
     * Dark navy 275px sidebar with a warm orange accent: the active item carries an
     * inset 3px orange bar (a box-shadow, not a border), 7px-radius rows and uppercase
     * section labels. Pair with {@code withFooter(...)} for the "Upgrade to Pro" promo
     * card, which this theme renders as a bordered card rather than a divider.
     * Reference: {@code docs/04-modern-saas-sidenav.html}.
     */
    MODERN_SAAS,

    /**
     * Light/white 280px Material Design sidebar: 48px-tall square-cornered rows, a
     * filled indigo active item with a <em>right</em>-side 3px accent border instead
     * of a left one, and solid indigo pill badges.
     * Reference: {@code docs/05-material-sidenav.html}.
     */
    MATERIAL,

    /**
     * Dark corporate-navy 285px sidebar with a flat solid-blue active fill (no accent
     * border at all), tight 4px-radius 13px rows and amber square-ish badges — denser
     * and more "enterprise console" than {@link #MODERN_SAAS}.
     * Reference: {@code docs/06-enterprise-sidenav.html}.
     */
    ENTERPRISE,

    /**
     * Violet-to-rose diagonal gradient 280px sidebar; the active item is a white,
     * elevated 12px-radius pill that "floats" above the gradient background, with
     * amber badges.
     * Reference: {@code docs/08-gradient-sidenav.html}.
     */
    GRADIENT,

    /**
     * Dark slate 255px sidebar with an emerald-green accent, intended for use with
     * {@code withCollapse()} and {@code withFooter(...)} — the footer renders as a
     * top divider above pinned Settings / Sign out items.
     * Reference: {@code docs/11-collapsible-style-sidenav.html}.
     */
    COLLAPSIBLE_DARK,

    /**
     * Deep navy-blue 285px sidebar with a vivid blue, shadow-elevated active item and
     * white "NEW"-style text badges.
     * Reference: {@code docs/12-premium-blue-sidenav.html}.
     */
    PREMIUM_BLUE;

    /**
     * @return the CSS class applied to the {@link AppShellLayout} root and/or the
     *         {@code .sidenav-host} wrapper, e.g. {@code shell-color-modern-saas}
     */
    public String cssClassName() {
        return "shell-color-" + name().toLowerCase().replace('_', '-');
    }
}



