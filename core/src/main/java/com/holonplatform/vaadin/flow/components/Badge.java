package com.holonplatform.vaadin.flow.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.css.BadgeColor;
import com.holonplatform.vaadin.flow.components.css.BadgeShape;
import com.holonplatform.vaadin.flow.components.css.BadgeSize;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;

import java.util.StringJoiner;

import static com.holonplatform.vaadin.flow.components.css.BadgeShape.PILL;

public class Badge extends Span {
    public Badge(String text) {
        this(text, BadgeColor.NORMAL);
    }

    public Badge(String text, BadgeColor color) {
        super(text);
        addClassName("badge");
        setTheme(color.getThemeName(), this);
    }

    public Badge(String text, BadgeColor color, BadgeSize size, BadgeShape shape) {
        super(text);
        addClassName("badge");
        StringJoiner joiner = new StringJoiner(" ");
        joiner.add(color.getThemeName());
        if (shape.equals(PILL)) {
            joiner.add(shape.getThemeName());
        }
        if (size.equals(BadgeSize.S)) {
            joiner.add(size.getThemeName());
        }
       setTheme(joiner.toString(), this);
    }

    // ── Localizable constructors ──────────────────────────────────────────────

    /**
     * Creates a badge with localized text and default color.
     *
     * @param text localizable badge label (not null)
     */
    public Badge(Localizable text) {
        this(resolve(text), BadgeColor.NORMAL);
    }

    /**
     * Creates a badge with localized text and specific color.
     *
     * @param text  localizable badge label (not null)
     * @param color badge color theme
     */
    public Badge(Localizable text, BadgeColor color) {
        this(resolve(text), color);
    }

    /**
     * Creates a badge with localized text, color, size, and shape.
     *
     * @param text  localizable badge label (not null)
     * @param color badge color theme
     * @param size  badge size
     * @param shape badge shape
     */
    public Badge(Localizable text, BadgeColor color, BadgeSize size, BadgeShape shape) {
        this(resolve(text), color, size, shape);
    }

    // ── Text setter ───────────────────────────────────────────────────────────

    /**
     * Sets the badge label from a {@link Localizable} descriptor.
     *
     * @param text localizable label (not null)
     */
    public void setText(Localizable text) {
        setText(resolve(text));
    }

    private static String resolve(Localizable l) {
        return LocalizationProvider.localize(l)
                .orElseGet(() -> l.getMessage() != null ? l.getMessage() : "");
    }

    private void setTheme(String theme, Component... components) {
        for (Component component : components) {
            component.getElement().setAttribute("theme", theme);
        }
    }
}
