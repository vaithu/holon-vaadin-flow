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
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * The "Previous page" control inside a {@link PaginationItem}.
 *
 * <p>Renders as:</p>
 * <pre>
 * &lt;div class="pagination__previous [pagination__link--disabled]"
 *      aria-label="Go to previous page"&gt;
 *   &lt;vaadin-icon class="pagination__nav-icon" icon="vaadin:chevron-left"&gt;&lt;/vaadin-icon&gt;
 *   &lt;span class="pagination__nav-label"&gt;Previous&lt;/span&gt;
 * &lt;/div&gt;
 * </pre>
 *
 * <p>Mirrors the shadcn/ui {@code PaginationPrevious} element.</p>
 *
 * @see PaginationItem
 * @see PaginationNext
 */
public class PaginationPrevious extends Div {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the previous-page control with a chevron-left icon and "Previous" label.
     */
    public PaginationPrevious() {
        addClassName("pagination__previous");
        getElement().setAttribute("aria-label", "Go to previous page");
        getElement().setAttribute("tabindex", "0");

        Icon chevron = new Icon(VaadinIcon.CHEVRON_LEFT);
        chevron.addClassName("pagination__nav-icon");
        add(chevron);

        Span label = Components.span().text("Previous").styleName("pagination__nav-label").build();
        add(label);
    }

    /**
     * Enables or disables this control.
     *
     * @param disabled {@code true} to content the disabled class and {@code aria-disabled}
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

