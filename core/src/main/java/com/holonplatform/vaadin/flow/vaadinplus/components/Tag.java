package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.iyensoft.vaadin.flow.enums.MaterialSymbol;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;

@StyleSheet("context://material-symbols.css")
@StyleSheet("context://tag.css")
public class Tag extends Span {

    private final Span prefix;
    private Color.Text color;

    public Tag(Component prefix, String text, Color.Text color) {
        addClassName("tag");
        this.prefix = Components.span().styleName("tag__prefix").visible(false).build();
        setPrefix(prefix);
        add(this.prefix, new Text(text));
        setTextColor(color);
    }

    public Tag(Component prefix, String text) {
        this(prefix, text, Color.Text.SECONDARY);
    }

    public Tag(MaterialSymbol symbol, String text, Color.Text color) {
        this(createIcon(symbol), text, color);
    }

    public Tag(MaterialSymbol symbol, String text) {
        this(symbol, text, Color.Text.SECONDARY);
    }

    public Tag(String text) {
        this((Component) null, text, Color.Text.SECONDARY);
    }

    // ── Localizable constructors ──────────────────────────────────────────────

    public Tag(Localizable text) {
        this((Component) null, resolve(text), Color.Text.SECONDARY);
    }

    public Tag(Component prefix, Localizable text) {
        this(prefix, resolve(text), Color.Text.SECONDARY);
    }

    public Tag(Component prefix, Localizable text, Color.Text color) {
        this(prefix, resolve(text), color);
    }

    public Tag(MaterialSymbol symbol, Localizable text) {
        this(createIcon(symbol), resolve(text), Color.Text.SECONDARY);
    }

    public Tag(MaterialSymbol symbol, Localizable text, Color.Text color) {
        this(createIcon(symbol), resolve(text), color);
    }

    // ── Text setter ───────────────────────────────────────────────────────────

    /**
     * Sets the tag label text from a {@link Localizable} descriptor.
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

    private static Span createIcon(MaterialSymbol symbol) {
        return symbol.create("tag__icon");
    }

    /**
     * Sets the prefix.
     */
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