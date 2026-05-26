package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;

/**
 * Description slot of an {@link Alert}.
 *
 * <p>Supports plain text, Holon {@link Localizable} (resolved on attach and on explicit set),
 * and arbitrary child components.</p>
 */
public class AlertDescription extends Div implements HasSize, HasStyle {

    private Localizable localizable;

    // --- Constructors ---

    /**
     * Creates a description with a plain text string.
     *
     * @param text the description text
     */
    public AlertDescription(String text) {
        addClassName("alert__description");
        setText(text);
    }

    /**
     * Creates a description backed by a {@link Localizable} message.
     * The text is resolved using the current locale; re-resolved on each attach.
     *
     * @param localizable the localizable message
     */
    public AlertDescription(Localizable localizable) {
        addClassName("alert__description");
        setLocalizableText(localizable);
    }

    /**
     * Creates a description containing arbitrary child components.
     *
     * @param components child components
     */
    public AlertDescription(Component... components) {
        addClassName("alert__description");
        add(components);
    }

    // --- Public API ---

    /**
     * Updates the description text from a {@link Localizable} message.
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

