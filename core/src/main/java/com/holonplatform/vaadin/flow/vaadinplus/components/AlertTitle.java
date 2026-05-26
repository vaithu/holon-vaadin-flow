package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;

/**
 * Title slot of an {@link Alert}.
 *
 * <p>Supports plain text, Holon {@link Localizable} (resolved on attach and on explicit set),
 * and arbitrary child components.</p>
 */
public class AlertTitle extends Div implements HasSize, HasStyle {

    private Localizable localizable;

    // --- Constructors ---

    /**
     * Creates a title with a plain text string.
     *
     * @param text the title text
     */
    public AlertTitle(String text) {
        addClassName("alert__title");
        setText(text);
    }

    /**
     * Creates a title backed by a {@link Localizable} message.
     * The text is resolved using the current locale; re-resolved on each attach.
     *
     * @param localizable the localizable message
     */
    public AlertTitle(Localizable localizable) {
        addClassName("alert__title");
        setLocalizableText(localizable);
    }

    /**
     * Creates a title containing arbitrary child components.
     *
     * @param components child components
     */
    public AlertTitle(Component... components) {
        addClassName("alert__title");
        add(components);
    }

    // --- Public API ---

    /**
     * Updates the title text from a {@link Localizable} message.
     * Resolves immediately if a locale is available; always re-resolves on the next attach.
     *
     * @param localizable the localizable message (not null)
     */
    public void setLocalizableText(Localizable localizable) {
        this.localizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(super::setText);
    }

    // --- Lifecycle ---

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (this.localizable != null) {
            LocalizationProvider.localize(this.localizable).ifPresent(super::setText);
        }
    }
}

