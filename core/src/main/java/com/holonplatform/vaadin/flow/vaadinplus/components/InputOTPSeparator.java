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

import com.holonplatform.vaadin.flow.components.Components;
import com.iyensoft.vaadin.flow.enums.MaterialSymbol;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

/**
 * A visual separator between {@link InputOTPGroup}s inside an {@link InputOTP}.
 *
 * <p>Renders as:
 * <pre>
 * &lt;div class="input-otp__separator" aria-hidden="true" role="presentation"&gt;
 *   &lt;span class="input-otp__separator-icon"&gt;–&lt;/span&gt;  &lt;!-- default --&gt;
 * &lt;/div&gt;
 * </pre>
 *
 * <p>Mirrors the shadcn/ui {@code InputOTPSeparator} element. Swap the default en-dash
 * for any component (e.g. a {@link MaterialSymbol} icon, or a plain {@link Span}).
 *
 * <p>All visual styling is defined in {@code input-otp.css}.
 *
 * @see InputOTP
 * @see InputOTPGroup
 */
public class InputOTPSeparator extends Div {

    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a separator with the default en-dash ({@code –}) glyph.
     */
    public InputOTPSeparator() {
        this((Component) null);
    }

    /**
     * Creates a separator using a {@link MaterialSymbol} icon.
     *
     * @param symbol the icon to render as separator content (not null)
     */
    public InputOTPSeparator(MaterialSymbol symbol) {
        this(symbol != null ? symbol.create("input-otp__separator-icon") : null);
    }

    /**
     * Creates a separator with a custom component as content.
     * Pass {@code null} to use the default en-dash glyph.
     *
     * @param customContent the separator content, or {@code null} for the default glyph
     */
    public InputOTPSeparator(Component customContent) {
        addClassName("input-otp__separator");
        getElement().setAttribute("aria-hidden", "true");
        getElement().setAttribute("role", "presentation");

        if (customContent != null) {
            add(customContent);
        } else {
            Span dash = Components.span().text("–").styleName("input-otp__separator-icon").build();
            add(dash);
        }
    }
}

