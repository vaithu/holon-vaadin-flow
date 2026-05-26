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

/**
 * A clickable page-number button inside a {@link PaginationItem}.
 *
 * <p>Renders as:</p>
 * <pre>
 * &lt;div class="pagination__link [pagination__link--active]"
 *      aria-label="Go to page N" [aria-current="page"]&gt;
 *   &lt;span&gt;N&lt;/span&gt;
 * &lt;/div&gt;
 * </pre>
 *
 * <p>Mirrors the shadcn/ui {@code PaginationLink} element.</p>
 *
 * @see PaginationItem
 */
public class PaginationLink extends Div implements ClickNotifier<Div> {

    private static final long serialVersionUID = 1L;

    private final int page;
    private boolean active;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates a page-number link.
     *
     * @param page   the 1-based page number to display
     * @param active {@code true} if this link represents the current page
     */
    public PaginationLink(int page, boolean active) {
        this.page = page;
        this.active = active;

        addClassName("pagination__link");
        getElement().setAttribute("aria-label", "Go to page " + page);
        getElement().setAttribute("tabindex", "0");

        if (active) {
            addClassName("pagination__link--active");
            getElement().setAttribute("aria-current", "page");
        }

        Span label = Components.span().text(String.valueOf(page)).styleName("pagination__link-label").build();
        add(label);
    }

    // -----------------------------------------------------------------------
    // State
    // -----------------------------------------------------------------------

    /** Returns the 1-based page number this link navigates to. */
    public int getPage() {
        return page;
    }

    /** Returns {@code true} if this link represents the currently active page. */
    public boolean isActive() {
        return active;
    }

    /**
     * Marks this link as disabled (non-navigable), adding
     * {@code pagination__link--disabled} and {@code aria-disabled="true"}.
     *
     * @param disabled {@code true} to disable
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

