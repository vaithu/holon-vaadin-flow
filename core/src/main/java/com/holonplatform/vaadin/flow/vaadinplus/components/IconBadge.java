package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.vaadin.flow.components.builders.IconBadgeBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Circular tinted icon badge — a {@link Div} wrapper that renders a vaadin-icon
 * inside a round, semantically-colored circle, matching the AlertDialog header
 * icon pattern and the shadcn/ui icon-badge convention.
 *
 * <p>DOM structure:</p>
 * <pre>
 * &lt;div class="icon-badge icon-badge--{variant} [icon-badge--{size}]"&gt;
 *   &lt;vaadin-icon icon="…"/&gt;
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
public class IconBadge extends Div implements HasSize, HasStyle {

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
    public static IconBadgeBuilder builder(VaadinIcon icon) {
        return IconBadgeBuilder.create(icon);
    }

    /**
     * Obtain an {@link IconBadgeBuilder} pre-configured with icon and variant.
     *
     * <pre>{@code
     * IconBadge badge = IconBadge.builder(VaadinIcon.CHECK_CIRCLE, Alert.Variant.SUCCESS)
     *     .size(IconBadge.Size.LG)
     *     .build();
     * }</pre>
     *
     * @param icon    the VaadinIcon to display (not null)
     * @param variant the semantic color variant (null = neutral)
     * @return a new {@link IconBadgeBuilder}
     */
    public static IconBadgeBuilder builder(VaadinIcon icon, Alert.Variant variant) {
        return IconBadgeBuilder.create(icon, variant);
    }

    /**
     * Obtain a fully-specified {@link IconBadgeBuilder}.
     *
     * @param icon    the VaadinIcon to display (not null)
     * @param variant the semantic color variant (null = neutral)
     * @param size    the size preset (not null)
     * @return a new {@link IconBadgeBuilder}
     */
    public static IconBadgeBuilder builder(VaadinIcon icon, Alert.Variant variant, Size size) {
        return IconBadgeBuilder.create(icon, variant, size);
    }

    // -----------------------------------------------------------------------
    // Size enum
    // -----------------------------------------------------------------------

    /**
     * Size preset for the badge circle.
     *
     * <ul>
     *   <li>{@link #DEFAULT} — 2.75 rem / 44 px — matches AlertDialog header icon</li>
     *   <li>{@link #SM}      — 2 rem   / 32 px — compact, for inline / list use</li>
     *   <li>{@link #LG}      — 3.5 rem / 56 px — hero / empty-state use</li>
     * </ul>
     */
    public enum Size {
        DEFAULT(null),
        SM("icon-badge--sm"),
        LG("icon-badge--lg");

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

    private String currentVariantClass;
    private String currentSizeClass;

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
        getElement().setAttribute("aria-hidden", "true");
        add(icon);
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
        return currentVariantClass == null ? null :
               switch (currentVariantClass) {
                   case "icon-badge--destructive" -> Alert.Variant.DESTRUCTIVE;
                   case "icon-badge--warning"     -> Alert.Variant.WARNING;
                   case "icon-badge--success"     -> Alert.Variant.SUCCESS;
                   case "icon-badge--info"        -> Alert.Variant.INFO;
                   default                        -> Alert.Variant.DEFAULT;
               };
    }

    /**
     * Applies (or clears) the semantic variant, swapping the CSS modifier class.
     *
     * @param variant the new variant ({@code null} or {@link Alert.Variant#DEFAULT} = neutral)
     */
    public void setVariant(Alert.Variant variant) {
        if (currentVariantClass != null) {
            removeClassName(currentVariantClass);
            currentVariantClass = null;
        }
        if (variant != null && variant != Alert.Variant.DEFAULT) {
            currentVariantClass = switch (variant) {
                case DESTRUCTIVE -> "icon-badge--destructive";
                case WARNING     -> "icon-badge--warning";
                case SUCCESS     -> "icon-badge--success";
                case INFO        -> "icon-badge--info";
                default          -> null;
            };
            if (currentVariantClass != null) {
                addClassName(currentVariantClass);
            }
        }
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
            case "icon-badge--sm" -> Size.SM;
            case "icon-badge--lg" -> Size.LG;
            default               -> Size.DEFAULT;
        };
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
        removeAll();
        add(icon);
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



