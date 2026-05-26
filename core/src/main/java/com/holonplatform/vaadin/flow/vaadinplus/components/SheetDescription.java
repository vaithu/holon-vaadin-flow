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
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;

/**
 * Description slot of a {@link Sheet} component.
 *
 * <p>Supports plain text, Holon {@link Localizable} (resolved on attach and on explicit set),
 * and arbitrary child components.</p>
 *
 * @see Sheet
 */
public class SheetDescription extends Div implements HasSize, HasStyle {

    private Localizable localizable;

    /**
     * Creates a description with a plain text string.
     *
     * @param text the description text
     */
    public SheetDescription(String text) {
        addClassName("sheet__description");
        setText(text);
    }

    /**
     * Creates a description backed by a {@link Localizable} message.
     * The text is resolved using the current locale; re-resolved on each attach.
     *
     * @param localizable the localizable message
     */
    public SheetDescription(Localizable localizable) {
        addClassName("sheet__description");
        setLocalizableText(localizable);
    }

    /**
     * Creates a description containing arbitrary child components.
     *
     * @param components child components
     */
    public SheetDescription(Component... components) {
        addClassName("sheet__description");
        add(components);
    }

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

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (this.localizable != null) {
            LocalizationProvider.localize(this.localizable).ifPresent(super::setText);
        }
    }
}

