package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.components.builders.IconBadgeBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.io.Serial;

/**
 * Circular tinted icon badge — a {@link Div} wrapper that renders a vaadin-icon
 * inside a round, semantically-colored circle, matching the AlertDialog header
 * icon pattern and the shadcn/ui icon-badge convention.
 *
 * <p>DOM structure:</p>
 * <pre>
 * &lt;div class="icon-badge icon-badge--{variant} [icon-badge--{size}] [icon-badge--has-text]"&gt;
 *   &lt;vaadin-icon icon="…"/&gt;
 *   &lt;span class="icon-badge__text"&gt;…&lt;/span&gt;
 * &lt;/div&gt;
 * </pre>
 *
 * <p>All visual styling lives in {@code utilities.css} (section 36).
 * No inline styles are used.</p>
 *
 * <p>Preferred usage:</p>
 * <pre>{@code
 * // Via Components factory (shortest)
 * IconBadge badge = Components.iconBadge(VaadinIcon.CHECK_CIRCLE, Alert.Variant.SUCCESS);
 *
 * // Direct construction
 * IconBadge badge = new IconBadge(VaadinIcon.TRUCK.create(), Alert.Variant.WARNING);
 *
 * // With explicit size
 * IconBadge badge = new IconBadge(VaadinIcon.INFO_CIRCLE.create(),
 *                                  Alert.Variant.INFO,
 *                                  IconBadge.Size.LG);
 * }</pre>
 */
@StyleSheet("context://utilities.css")
public class IconBadge extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // Static builder factories
    // -----------------------------------------------------------------------

    /**
     * Obtain an {@link IconBadgeBuilder} for a neutral badge.
     *
     * @return a new {@link IconBadgeBuilder}
     */
    public static IconBadgeBuilder builder() {
        return IconBadgeBuilder.create();
    }

    /**
     * Obtain an {@link IconBadgeBuilder} pre-configured with the given icon.
     *
     * @param icon the VaadinIcon to display (not null)
     * @return a new {@link IconBadgeBuilder}
     */
    public static IconBadgeBuilder builder(Icon icon) {
        return IconBadgeBuilder.create(icon);
    }

    /**
     * Obtain an {@link IconBadgeBuilder} pre-configured with icon and variant.
     *
     * <pre>{@code
     * IconBadge badge = IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS)
     *     .size(IconBadge.Size.LG)
     *     .build();
     * }</pre>
     *
     * @param icon    the VaadinIcon to display (not null)
     * @param variant the semantic color variant (null = neutral)
     * @return a new {@link IconBadgeBuilder}
     */
    public static IconBadgeBuilder builder(Icon icon, Alert.Variant variant) {
        return IconBadgeBuilder.create(icon, variant);
    }

    /**
     * Obtain a fully-specified {@link IconBadgeBuilder}.
     *
     * @param icon    the Icon to display (not null)
     * @param variant the semantic color variant (null = neutral)
     * @param size    the size preset (not null)
     * @return a new {@link IconBadgeBuilder}
     */
    public static IconBadgeBuilder builder(Icon icon, Alert.Variant variant, Size size) {
        return IconBadgeBuilder.create(icon, variant, size);
    }

    /**
     * Convenience factory for creating a badge with icon, variant, size and text.
     *
     * @param icon the icon to display (not null)
     * @param variant the semantic color variant (null = neutral)
     * @param size the size preset (not null)
     * @param text the text to display, or {@code null} / blank to keep icon-only mode
     * @return a new {@link IconBadgeBuilder}
     */
    public static IconBadgeBuilder builder(Icon icon, Alert.Variant variant, Size size, String text) {
        return IconBadgeBuilder.create(icon, variant, size).text(text);
    }

    // -----------------------------------------------------------------------
    // Size enum
    // -----------------------------------------------------------------------

    /**
     * Size preset for the badge circle.
     *
     * <ul>
     *   <li>{@link #XS}      — 1.75 rem / 28 px — dense inline chips</li>
     *   <li>{@link #DEFAULT} — 2.75 rem / 44 px — matches AlertDialog header icon</li>
     *   <li>{@link #SM}      — 2 rem   / 32 px — compact, for inline / list use</li>
     *   <li>{@link #LG}      — 3.5 rem / 56 px — hero / empty-state use</li>
     *   <li>{@link #XL}      — 4.5 rem / 72 px — large status / dashboard use</li>
     * </ul>
     */
    public enum Size {
        XS("icon-badge--xs"),
        DEFAULT(null),
        SM("icon-badge--sm"),
        LG("icon-badge--lg"),
        XL("icon-badge--xl");

        private final String cssClass;

        Size(String cssClass) {
            this.cssClass = cssClass;
        }

        /** Returns the CSS modifier class, or {@code null} for {@link #DEFAULT}. */
        public String getCssClass() {
            return cssClass;
        }
    }

    // -----------------------------------------------------------------------
    // Internal state
    // -----------------------------------------------------------------------

    private final Div iconSlot;
    private final Span textSlot;
    private Alert.Variant currentVariant;
    private String currentSizeClass;
    private String currentText = "";

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a neutral {@link Size#DEFAULT} badge wrapping the given icon.
     *
     * @param icon the icon component to display (not null)
     */
    public IconBadge(Component icon) {
        this(icon, null, Size.DEFAULT);
    }

    /**
     * Creates a badge with the given semantic variant wrapping the given icon.
     *
     * @param icon    the icon component to display (not null)
     * @param variant the color variant ({@code null} = neutral default)
     */
    public IconBadge(Component icon, Alert.Variant variant) {
        this(icon, variant, Size.DEFAULT);
    }

    /**
     * Creates a fully-specified badge.
     *
     * @param icon    the icon component to display (not null)
     * @param variant the color variant ({@code null} = neutral default)
     * @param size    the size preset (not null)
     */
    public IconBadge(Component icon, Alert.Variant variant, Size size) {
        addClassName("icon-badge");
        this.iconSlot = new Div();
        this.textSlot = new Span();

        iconSlot.addClassName("icon-badge__icon");
        iconSlot.add(icon);

        textSlot.addClassName("icon-badge__text");
        textSlot.setVisible(false);

        add(iconSlot, textSlot);
        getElement().setAttribute("aria-hidden", "true");
        setVariant(variant);
        setSize(size != null ? size : Size.DEFAULT);
    }

    // -----------------------------------------------------------------------
    // VaadinIcon convenience constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a neutral badge from a {@link VaadinIcon} enum constant.
     *
     * @param icon the VaadinIcon (not null)
     */
    public IconBadge(VaadinIcon icon) {
        this(icon.create(), null, Size.DEFAULT);
    }

    /**
     * Creates a badge from a {@link VaadinIcon} enum constant with the given variant.
     *
     * @param icon    the VaadinIcon (not null)
     * @param variant the color variant ({@code null} = neutral default)
     */
    public IconBadge(VaadinIcon icon, Alert.Variant variant) {
        this(icon.create(), variant, Size.DEFAULT);
    }

    /**
     * Creates a fully-specified badge from a {@link VaadinIcon} enum constant.
     *
     * @param icon    the VaadinIcon (not null)
     * @param variant the color variant ({@code null} = neutral default)
     * @param size    the size preset (not null)
     */
    public IconBadge(VaadinIcon icon, Alert.Variant variant, Size size) {
        this(icon.create(), variant, size);
    }

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /** @see #IconBadge(Component) */
    public static IconBadge of(Component icon) {
        return new IconBadge(icon);
    }

    /** @see #IconBadge(Component, Alert.Variant) */
    public static IconBadge of(Component icon, Alert.Variant variant) {
        return new IconBadge(icon, variant);
    }

    /** @see #IconBadge(Component, Alert.Variant, Size) */
    public static IconBadge of(Component icon, Alert.Variant variant, Size size) {
        return new IconBadge(icon, variant, size);
    }

    /** @see #IconBadge(VaadinIcon) */
    public static IconBadge of(VaadinIcon icon) {
        return new IconBadge(icon);
    }

    /** @see #IconBadge(VaadinIcon, Alert.Variant) */
    public static IconBadge of(VaadinIcon icon, Alert.Variant variant) {
        return new IconBadge(icon, variant);
    }

    /** @see #IconBadge(VaadinIcon, Alert.Variant, Size) */
    public static IconBadge of(VaadinIcon icon, Alert.Variant variant, Size size) {
        return new IconBadge(icon, variant, size);
    }

    // -----------------------------------------------------------------------
    // Variant API
    // -----------------------------------------------------------------------

    /**
     * Returns the current variant, or {@code null} if none (neutral default).
     *
     * @return the current {@link Alert.Variant}, or {@code null}
     */
    public Alert.Variant getVariant() {
        return currentVariant;
    }

    /**
     * Applies (or clears) the semantic variant, swapping the CSS modifier class.
     *
     * @param variant the new variant ({@code null} or {@link Alert.Variant#DEFAULT} = neutral)
     */
    public void setVariant(Alert.Variant variant) {
        if (currentVariant != null && !currentVariant.isDefault()) {
            removeClassName(currentVariant.getCssClass("icon-badge"));
        }
        currentVariant = (variant != null && !variant.isDefault()) ? variant : null;
        if (currentVariant != null) {
            addClassName(currentVariant.getCssClass("icon-badge"));
        }
    }

    // -----------------------------------------------------------------------
    // Text API
    // -----------------------------------------------------------------------

    /**
     * Returns the current badge text.
     *
     * @return the current text, never {@code null}
     */
    @Override
    public String getText() {
        return currentText;
    }

    /**
     * Sets or clears the badge text.
     *
     * <p>When text is present, the badge switches to a pill-like layout and is no
     * longer aria-hidden. Passing {@code null} or blank text clears the text slot
     * and restores the icon-only circular badge.</p>
     *
     * @param text the new text, or {@code null} / blank to clear
     */
    @Override
    public void setText(String text) {
        String normalized = text != null && !text.isBlank() ? text : "";
        this.currentText = normalized;
        textSlot.setText(normalized);
        textSlot.setVisible(!normalized.isEmpty());

        if (normalized.isEmpty()) {
            removeClassName("icon-badge--has-text");
            getElement().setAttribute("aria-hidden", "true");
        } else {
            addClassName("icon-badge--has-text");
            getElement().removeAttribute("aria-hidden");
        }
    }

    /**
     * Clears any badge text and restores the icon-only layout.
     */
    public void clearText() {
        setText(null);
    }

    /**
     * Fluent shortcut to set the badge text.
     *
     * @param text the text to display, or {@code null} / blank to clear
     * @return this badge instance
     */
    public IconBadge text(String text) {
        setText(text);
        return this;
    }

    // -----------------------------------------------------------------------
    // Size API
    // -----------------------------------------------------------------------

    /**
     * Returns the current size preset.
     *
     * @return the current {@link Size} (never null)
     */
    public Size getBadgeSize() {
        if (currentSizeClass == null) return Size.DEFAULT;
        return switch (currentSizeClass) {
            case "icon-badge--xs" -> Size.XS;
            case "icon-badge--sm" -> Size.SM;
            case "icon-badge--lg" -> Size.LG;
            case "icon-badge--xl" -> Size.XL;
            default               -> Size.DEFAULT;
        };
    }

    /**
     * Returns whether badge text is currently visible.
     *
     * @return true if text is present
     */
    public boolean hasText() {
        return !currentText.isEmpty();
    }

    /**
     * Sets the badge size, swapping the CSS modifier class.
     *
     * @param size the new size (not null; {@link Size#DEFAULT} removes any size modifier)
     */
    public void setBadgeSize(Size size) {
        if (currentSizeClass != null) {
            removeClassName(currentSizeClass);
            currentSizeClass = null;
        }
        if (size != null && size.getCssClass() != null) {
            currentSizeClass = size.getCssClass();
            addClassName(currentSizeClass);
        }
    }

    private void setSize(Size size) {
        setBadgeSize(size);
    }

    // -----------------------------------------------------------------------
    // Icon API
    // -----------------------------------------------------------------------

    /**
     * Replaces the current icon with a new one.
     *
     * @param icon the replacement icon (not null)
     */
    public void setIcon(Icon icon) {
        iconSlot.removeAll();
        iconSlot.add(icon);
    }

    /**
     * Replaces the current icon with the given {@link VaadinIcon} constant.
     *
     * @param icon the VaadinIcon constant (not null)
     */
    public void setIcon(VaadinIcon icon) {
        setIcon(icon.create());
    }
}



