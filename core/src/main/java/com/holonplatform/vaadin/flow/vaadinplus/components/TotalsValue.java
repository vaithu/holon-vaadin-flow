/*
 * Copyright 2016-2024 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;

/**
 * Value slot of a {@link TotalsRow}.
 *
 * <p>Supports plain text, Holon {@link Localizable} (resolved on attach and on explicit set),
 * and arbitrary child components. The value can be rendered in a muted colour via
 * {@link #setMuted(boolean)}, independently of the owning row's {@link TotalsRow.Variant}.</p>
 */
public class TotalsValue extends Span {

    @Serial
    private static final long serialVersionUID = 1L;

    private Localizable localizable;

    // --- Constructors ---

    /**
     * Creates a value with a plain text string.
     *
     * @param text the value text
     */
    public TotalsValue(String text) {
        addClassName("totals-card__value");
        setText(text);
    }

    /**
     * Creates a value backed by a {@link Localizable} message.
     * The text is resolved using the current locale; re-resolved on each attach.
     *
     * @param localizable the localizable message
     */
    public TotalsValue(Localizable localizable) {
        addClassName("totals-card__value");
        setLocalizableText(localizable);
    }

    /**
     * Creates a value containing arbitrary child components.
     *
     * @param components child components
     */
    public TotalsValue(Component... components) {
        addClassName("totals-card__value");
        add(components);
    }

    // --- Public API ---

    /**
     * Updates the value text from a {@link Localizable} message.
     * Resolves immediately if a locale is available; always re-resolves on the next attach.
     *
     * @param localizable the localizable message (not null)
     */
    public void setLocalizableText(Localizable localizable) {
        this.localizable = localizable;
        LocalizationProvider.localize(localizable).ifPresent(super::setText);
    }

    /**
     * Enables or disables the muted colour style ({@code totals-card__value--muted}).
     *
     * @param muted {@code true} to render the value in the muted colour, {@code false} to restore
     *              the default colour
     */
    public void setMuted(boolean muted) {
        if (muted) {
            addClassName("totals-card__value--muted");
        } else {
            removeClassName("totals-card__value--muted");
        }
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

