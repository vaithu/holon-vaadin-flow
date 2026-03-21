package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.css.BadgeColor;
import com.holonplatform.vaadin.flow.components.css.BadgeShape;
import com.holonplatform.vaadin.flow.components.css.BadgeSize;
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
        setTheme(color.getThemeName(), this);
    }

    public Badge(String text, BadgeColor color, BadgeSize size, BadgeShape shape) {
        super(text);
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

    private void setTheme(String theme, Component... components) {
        for (Component component : components) {
            component.getElement().setAttribute("theme", theme);
        }
    }
}
