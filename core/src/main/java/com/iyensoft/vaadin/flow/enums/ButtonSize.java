package com.iyensoft.vaadin.flow.enums;

/**
 * Semantic size variants for buttons.
 *
 * <p>Each constant maps to a CSS class defined in {@code buttons.css} which controls
 * font-size, padding, and min-height of the entire button — not just its text.</p>
 *
 * <ul>
 *   <li>{@link #SMALL}  → {@code btn--small}  (0.8125rem / 0.25rem 0.75rem / 2rem height)</li>
 *   <li>{@link #NORMAL} → no class            (default shell-theme sizing, ~36px height)</li>
 *   <li>{@link #LARGE}  → {@code btn--large}  (1rem / 0.75rem 1.5rem / 3.25rem height)</li>
 * </ul>
 */
public enum ButtonSize {

    /** Compact button — reduced font-size, padding and min-height. */
    SMALL("btn--small"),

    /** Default button size — no extra class applied. */
    NORMAL(null),

    /** Spacious button — larger font-size, padding and min-height. */
    LARGE("btn--large");

    private final String cssClass;

    ButtonSize(String cssClass) {
        this.cssClass = cssClass;
    }

    /**
     * Returns the CSS class to apply, or {@code null} for the default size.
     *
     * @return CSS class name, or {@code null}
     */
    public String getCssClass() {
        return cssClass;
    }
}

