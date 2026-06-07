package com.iyensoft.vaadin.flow.utils.responsive;

/**
 * Represents the device/window posture.
 * <p>
 * This enum is used by the Responsive utility to tag components with:
 *  - a CSS class: "portrait" or "landscape"
 *  - a data attribute: data-orientation="portrait|landscape"
 *
 * Orientation is computed from window inner width/height:
 *   width >= height → LANDSCAPE
 *   width <  height → PORTRAIT
 */
public enum Orientation {
    PORTRAIT,
    LANDSCAPE;

    /**
     * Computes orientation from width/height values.
     * @param width  the window/client width in pixels
     * @param height the window/client height in pixels
     * @return LANDSCAPE if width >= height, otherwise PORTRAIT
     */
    public static Orientation of(int width, int height) {
        return (width >= height) ? LANDSCAPE : PORTRAIT;
    }

    /**
     * @return the CSS class name to content to a component: "portrait" or "landscape".
     */
    public String toClassName() {
        return this == PORTRAIT ? "portrait" : "landscape";
    }

    /**
     * @return the attribute value used by Responsive: "portrait" or "landscape".
     */
    public String toAttrValue() {
        return this == PORTRAIT ? "portrait" : "landscape";
    }

    /**
     * Parse a string (e.g., from data attributes) into Orientation.
     * Accepts "portrait" or "landscape" case-insensitively.
     * @throws IllegalArgumentException if value is not recognized
     */
    public static Orientation fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Orientation value cannot be null");
        }
        String v = value.trim().toLowerCase();
        return switch (v) {
            case "portrait" -> PORTRAIT;
            case "landscape" -> LANDSCAPE;
            default -> throw new IllegalArgumentException("Unknown orientation: " + value);
        };
    }

    /**
     * Convenience helpers.
     */
    public boolean isPortrait() { return this == PORTRAIT; }
    public boolean isLandscape() { return this == LANDSCAPE; }
}