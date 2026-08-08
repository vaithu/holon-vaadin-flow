package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * A small labelled chip with an optional icon/avatar prefix.
 *
 * <p>All {@link Localizable} constructor/setter overloads resolve the string via
 * {@link LocalizationProvider#localize(Localizable)} at call time. Because locale is
 * fixed per session and components are recreated on navigation, no
 * {@code LocaleChangeObserver} is required.</p>
 */
@StyleSheet("context://tag.css")
public class Tag extends Span {

    private final Span prefix;
    /** Text node stored so setText() updates only the text, not the prefix child. */
    private final Text textNode;
    private Color.Text color;

    public Tag(Component prefix, String text, Color.Text color) {
        addClassName("tag");
        this.prefix = Components.span().styleName("tag__prefix").visible(false).build();
        // Prefix is purely decorative - hide it from assistive technology
        this.prefix.getElement().setAttribute("aria-hidden", "true");
        setPrefix(prefix);
        this.textNode = new Text(text != null ? text : "");
        add(this.prefix, this.textNode);
        setTextColor(color);
    }

    public Tag(Component prefix, String text) {
        this(prefix, text, Color.Text.SECONDARY);
    }

    public Tag(VaadinIcon icon, String text, Color.Text color) {
        this(icon.create(), text, color);
    }

    public Tag(VaadinIcon icon, String text) {
        this(icon, text, Color.Text.SECONDARY);
    }

    public Tag(String text) {
        this((Component) null, text, Color.Text.SECONDARY);
    }

    // Localizable constructors - text resolved at construction time

    public Tag(Localizable text) {
        this((Component) null, resolve(text), Color.Text.SECONDARY);
    }

    public Tag(Component prefix, Localizable text) {
        this(prefix, resolve(text), Color.Text.SECONDARY);
    }

    public Tag(Component prefix, Localizable text, Color.Text color) {
        this(prefix, resolve(text), color);
    }

    public Tag(VaadinIcon icon, Localizable text) {
        this(icon.create(), resolve(text), Color.Text.SECONDARY);
    }

    public Tag(VaadinIcon icon, Localizable text, Color.Text color) {
        this(icon.create(), resolve(text), color);
    }

    // Text setters

    @Override
    public void setText(String text) {
        this.textNode.setText(text != null ? text : "");
    }

    /** Sets the tag label from a {@link Localizable} descriptor (resolved at call time). */
    public void setText(Localizable text) {
        this.textNode.setText(resolve(text));
    }

    private static String resolve(Localizable l) {
        return LocalizationProvider.localize(l)
                .orElseGet(() -> l.getMessage() != null ? l.getMessage() : "");
    }

    public void setPrefix(Component... components) {
        this.prefix.removeAll();
        if (components != null) {
            for (Component component : components) {
                if (component != null) {
                    if (component instanceof Icon) {
                        component.addClassName("tag__icon");
                    } else if (component instanceof Avatar avatar) {
                        avatar.addClassName("tag__avatar");
                    }
                    this.prefix.add(component);
                }
            }
        }
        this.prefix.setVisible(this.prefix.getChildren().findFirst().isPresent());
    }

    public void setTextColor(Color.Text color) {
        if (this.color != null) {
            removeClassName(this.color.getClassName());
        }
        if (color != null) {
            addClassName(color.getClassName());
        }
        this.color = color;
    }

}