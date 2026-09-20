package com.iyensoft.vaadin.flow.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.RibbonBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

/**
 * A card container decorated with a corner/edge ribbon label, inspired by the TailAdmin
 * "Ribbons" template.
 *
 * <p>The ribbon sits over the card and comes in three shapes ({@link Variant}):
 * <ul>
 *   <li>{@link Variant#ROUNDED} — a rounded pill anchored to the top-left edge.</li>
 *   <li>{@link Variant#SHAPE} — a flag-shaped ribbon with a notched tail and a folded corner.</li>
 *   <li>{@link Variant#FILED} — a diagonal banner across the top-right corner.</li>
 * </ul>
 *
 * <p>The colour is selected via {@link Color}. Card content is supplied through
 * {@link #setContent(Component...)} (or {@link #addContent(Component...)}); the ribbon itself is
 * kept separate so it always renders on top.
 *
 * <p>All {@link Localizable} overloads resolve the label via
 * {@link LocalizationProvider#localize(Localizable)} at call time. Because locale is fixed per
 * session and components are recreated on navigation, no {@code LocaleChangeObserver} is required.
 *
 * <p>All styling lives in {@code context://ribbon.css} so the component works with any theme.
 */
@StyleSheet("context://ribbon.css")
public class Ribbon extends Div {

    private static final String CLASS_ROOT = "ribbon-card";

    /** Ribbon shape. */
    public enum Variant {
        /** Rounded pill anchored to the top-left edge. */
        ROUNDED("ribbon--rounded"),
        /** Flag-shaped ribbon with a notched tail and a folded corner. */
        SHAPE("ribbon--shape"),
        /** Diagonal banner across the top-right corner. */
        FILED("ribbon--filed");

        private final String className;

        Variant(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }
    }

    /** Ribbon colour. */
    public enum Color {
        PRIMARY("ribbon--primary"),
        SUCCESS("ribbon--success"),
        WARNING("ribbon--warning"),
        ERROR("ribbon--error"),
        INFO("ribbon--info"),
        DARK("ribbon--dark");

        private final String className;

        Color(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }
    }

    private final Span ribbon = new Span();
    private final Div content = new Div();

    private Variant variant;
    private Color color;

    /** Creates a ribbon with the {@link Variant#ROUNDED} shape, an empty label and the primary colour. */
    public Ribbon() {
        this(Variant.ROUNDED, "", Color.PRIMARY);
    }

    public Ribbon(Variant variant, String label) {
        this(variant, label, Color.PRIMARY);
    }

    public Ribbon(Variant variant, String label, Color color) {
        addClassName(CLASS_ROOT);

        ribbon.addClassName("ribbon");
        // The ribbon is purely decorative - hide it from assistive technology.
        ribbon.getElement().setAttribute("aria-hidden", "true");
        ribbon.setText(label != null ? label : "");

        content.addClassName("ribbon-card__content");

        super.add(ribbon, content);

        setVariant(variant);
        setColor(color);
    }

    public Ribbon(Variant variant, Localizable label) {
        this(variant, resolve(label), Color.PRIMARY);
    }

    public Ribbon(Variant variant, Localizable label, Color color) {
        this(variant, resolve(label), color);
    }

    // ---------------------------------------------------------------------
    // Configuration
    // ---------------------------------------------------------------------

    /** Sets the ribbon label. */
    public void setLabel(String label) {
        ribbon.setText(label != null ? label : "");
    }

    /** Sets the ribbon label from a {@link Localizable} (resolved at call time). */
    public void setLabel(Localizable label) {
        ribbon.setText(resolve(label));
    }

    /** Sets the ribbon shape. */
    public void setVariant(Variant variant) {
        if (this.variant != null) {
            ribbon.removeClassName(this.variant.getClassName());
        }
        if (variant != null) {
            ribbon.addClassName(variant.getClassName());
        }
        this.variant = variant;
    }

    /** Sets the ribbon colour. */
    public void setColor(Color color) {
        if (this.color != null) {
            ribbon.removeClassName(this.color.getClassName());
        }
        if (color != null) {
            ribbon.addClassName(color.getClassName());
        }
        this.color = color;
    }

    /** Replaces the card content with the given components. */
    public void setContent(Component... components) {
        content.removeAll();
        addContent(components);
    }

    /** Appends the given components to the card content. */
    public void addContent(Component... components) {
        if (components != null) {
            for (Component component : components) {
                if (component != null) {
                    content.add(component);
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    // Accessors
    // ---------------------------------------------------------------------

    /** Returns the ribbon label element. */
    public Span getRibbon() {
        return ribbon;
    }

    /** Returns the card content container. */
    public Div getContent() {
        return content;
    }

    public Variant getVariant() {
        return variant;
    }

    public Color getColor() {
        return color;
    }

    private static String resolve(Localizable label) {
        return LocalizationProvider.localize(label)
                .orElseGet(() -> label.getMessage() != null ? label.getMessage() : "");
    }

    /**
     * Returns a new {@link RibbonBuilder} to fluently create a {@link Ribbon}.
     *
     * @return a new {@link RibbonBuilder}
     */
    public static RibbonBuilder builder() {
        return RibbonBuilder.create();
    }
}
