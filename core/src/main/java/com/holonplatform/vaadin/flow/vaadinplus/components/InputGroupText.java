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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;

/**
 * A static text or icon addon for use inside an {@link InputGroup}.
 *
 * <p>Renders as a {@code <span class="input-group__text">} that shares the
 * group's border line and height. Place it before or after an input field
 * to provide a prefix or suffix label, symbol, or icon.
 *
 * <p>Common prefixes: {@code @}, {@code $}, {@code https://}, {@code +1}
 * <br>Common suffixes: {@code .com}, {@code .00}, {@code kg}, a Material Symbol icon
 *
 * <p><strong>Examples:</strong>
 *
 * <p>Plain text prefix:
 * <pre>{@code
 * new InputGroupText("@")          // -> <span class="input-group__text">@</span>
 * new InputGroupText("https://")   // -> <span class="input-group__text">https://</span>
 * }</pre>
 *
 * <p>Icon prefix (Material Symbol):
 * <pre>{@code
 * new InputGroupText(MaterialSymbol.SEARCH.create("input-group__text-icon"))
 * }</pre>
 *
 * <p>Multiple content pieces:
 * <pre>{@code
 * new InputGroupText(currencyIcon, new Span("USD"))
 * }</pre>
 *
 * <p>All visual styling is defined in {@code input-group.css}. No inline styles
 * or Lumo tokens are used.
 *
 * @see InputGroup
 */
public class InputGroupText extends Span {

    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates an empty addon cell.
     *
     * <p>Use {@link #setText(String)} or {@link #add(Component...)} to populate it.
     */
    public InputGroupText() {
        addClassName("input-group__text");
    }

    /**
     * Creates an addon cell with the given plain-text label.
     *
     * @param text the text to display (e.g. {@code "@"}, {@code "$"}, {@code ".com"})
     */
    public InputGroupText(String text) {
        this();
        if (text != null) {
            setText(text);
        }
    }

    /**
     * Creates an addon cell containing the given child component(s).
     *
     * <p>Use this constructor to place an icon, avatar, or any other Vaadin
     * component inside the addon cell.
     *
     * @param components the child components (not null; individual elements may be null and are skipped)
     */
    public InputGroupText(Component... components) {
        this();
        if (components != null) {
            for (Component c : components) {
                if (c != null) {
                    add(c);
                }
            }
        }
    }
}

