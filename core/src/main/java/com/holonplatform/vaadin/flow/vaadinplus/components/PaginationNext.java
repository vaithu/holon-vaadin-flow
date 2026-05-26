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
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * The "Next page" control inside a {@link PaginationItem}.
 *
 * <p>Renders as:</p>
 * <pre>
 * &lt;div class="pagination__next [pagination__link--disabled]"
 *      aria-label="Go to next page"&gt;
 *   &lt;span class="pagination__nav-label"&gt;Next&lt;/span&gt;
 *   &lt;vaadin-icon class="pagination__nav-icon" icon="vaadin:chevron-right"&gt;&lt;/vaadin-icon&gt;
 * &lt;/div&gt;
 * </pre>
 *
 * <p>Mirrors the shadcn/ui {@code PaginationNext} element.</p>
 *
 * @see PaginationItem
 * @see PaginationPrevious
 */
public class PaginationNext extends Div implements ClickNotifier<Div> {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the next-page control with "Next" label and a chevron-right icon.
     */
    public PaginationNext() {
        addClassName("pagination__next");
        getElement().setAttribute("aria-label", "Go to next page");
        getElement().setAttribute("tabindex", "0");

        Span label = Components.span().text("Next").styleName("pagination__nav-label").build();
        add(label);

        Icon chevron = new Icon(VaadinIcon.CHEVRON_RIGHT);
        chevron.addClassName("pagination__nav-icon");
        add(chevron);
    }

    /**
     * Enables or disables this control.
     *
     * @param disabled {@code true} to add the disabled class and {@code aria-disabled}
     */
    public void setDisabled(boolean disabled) {
        if (disabled) {
            addClassName("pagination__link--disabled");
            getElement().setAttribute("aria-disabled", "true");
            getElement().setAttribute("tabindex", "-1");
        } else {
            removeClassName("pagination__link--disabled");
            getElement().removeAttribute("aria-disabled");
            getElement().setAttribute("tabindex", "0");
        }
    }
}

