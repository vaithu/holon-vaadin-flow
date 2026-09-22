package com.iyensoft.vaadin.flow.components;

/**
 * Predefined color themes for {@link AppShellLayout} — recolors the brand accent,
 * active {@code SideNav} item, notification/nav badges, and brand logo tile.
 *
 * <p>Applied via {@code Components.appShell().colorTheme(ShellColor.INDIGO)...}.
 * Backed by {@code shell-color-themes.css}, which only overrides CSS custom
 * properties already consumed by {@code app-bar.css} and {@code menu.css}
 * (e.g. {@code --sidenav-item-active-bg}) — no selectors are duplicated, so a
 * consuming app's own token overrides still win if declared with higher
 * specificity.
 *
 * <p>The first eight constants are <b>accent-only</b> themes: they assume the
 * default light/white {@code SideNav} surface and just recolor the active item,
 * icon, badges and brand logo tile. The remaining constants are <b>surface</b>
 * presets: they also change the sidebar's own background (dark or gradient),
 * text colors, active-item shape (inset border, right border, or elevated pill)
 * and — where applicable — badge/logo tile colors, matching a specific
 * reference layout style one-for-one.
 */
public enum ShellColor {

    /** Vivid blue — e.g. "StaffOS" style enterprise shells. */
    BLUE,

    /** Cool indigo/violet — SaaS / analytics products. */
    INDIGO,

    /** Teal — operations / productivity dashboards. */
    TEAL,

    /** Red — service-desk / support tooling. */
    RED,

    /** Cyan — analytics / monitoring dashboards. */
    CYAN,

    /** Amber — sales / CRM tooling. */
    AMBER,

    /** Pink/rose — people / HR tooling. */
    PINK,

    /** Neutral slate — dense admin / enterprise consoles. */
    SLATE,

    /**
     * Dark navy sidebar with a warm orange accent: inset left-border highlight on the
     * active item, a rounded orange brand logo tile, and uppercase section group labels.
     * Pair with {@code withFooter(...)} for an "Upgrade to Pro" promo card.
     */
    MODERN_SAAS,

    /**
     * Light/white sidebar (Material Design style): active item gets a filled indigo
     * background with a right-side accent border instead of the default left border,
     * and numeric badges are solid indigo pills.
     */
    MATERIAL,

    /**
     * Dark corporate-navy sidebar with a solid blue active-item fill and amber badges —
     * a denser, more "enterprise console" look than {@link #MODERN_SAAS}.
     */
    ENTERPRISE,

    /**
     * Violet-to-rose diagonal gradient sidebar; the active item is rendered as a
     * white, elevated pill that "floats" above the gradient background.
     */
    GRADIENT,

    /**
     * Dark slate sidebar with an emerald-green accent, intended for use together with
     * {@code withCollapse()} and {@code withFooter(...)} (pinned Settings / Sign out items).
     */
    COLLAPSIBLE_DARK,

    /**
     * Deep navy-blue sidebar with a vivid blue, elevated (shadowed) active item and a
     * white "NEW"-style text badge.
     */
    PREMIUM_BLUE;

    /**
     * @return the CSS class applied to the {@link AppShellLayout} root, e.g. {@code shell-color-blue}
     */
    public String cssClassName() {
        return "shell-color-" + name().toLowerCase().replace('_', '-');
    }
}



