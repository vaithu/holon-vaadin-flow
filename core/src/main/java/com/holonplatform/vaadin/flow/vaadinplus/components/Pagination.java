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

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Nav;

/**
 * Root navigation element for a shadcn/ui-inspired Pagination control.
 *
 * <p>Mirrors the shadcn/ui {@code Pagination} component
 * (<a href="https://ui.shadcn.com/docs/components/radix/pagination">docs</a>).</p>
 *
 * <p>HTML output:</p>
 * <pre>
 * &lt;nav class="pagination" role="navigation" aria-label="pagination"&gt;
 *   &lt;ul class="pagination__content"&gt;
 *     &lt;li class="pagination__item"&gt;...previous...&lt;/li&gt;
 *     &lt;li class="pagination__item"&gt;...links...&lt;/li&gt;
 *     &lt;li class="pagination__item"&gt;...next...&lt;/li&gt;
 *   &lt;/ul&gt;
 * &lt;/nav&gt;
 * </pre>
 *
 * <p>Component hierarchy:</p>
 * <ul>
 *   <li>{@link Pagination} — {@code <nav>} landmark wrapper</li>
 *   <li>{@link PaginationContent} — {@code <ul>} flex container of items</li>
 *   <li>{@link PaginationItem} — {@code <li>} wrapper for each control</li>
 *   <li>{@link PaginationLink} — numbered page button</li>
 *   <li>{@link PaginationPrevious} — previous-page control</li>
 *   <li>{@link PaginationNext} — next-page control</li>
 *   <li>{@link PaginationEllipsis} — gap indicator ({@code …})</li>
 * </ul>
 *
 * <p>All styling is defined in {@code pagination.css}. No inline styles or Lumo tokens.</p>
 *
 * @see PaginationContent
 * @see PaginationItem
 * @see PaginationLink
 * @see PaginationPrevious
 * @see PaginationNext
 * @see PaginationEllipsis
 */
@StyleSheet("context://pagination.css")
public class Pagination extends Nav implements HasSize, HasStyle {

    private static final long serialVersionUID = 1L;

    private final PaginationContent content;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates an empty Pagination nav with an inner {@link PaginationContent}.
     */
    public Pagination() {
        addClassName("pagination");
        getElement().setAttribute("role", "navigation");
        getElement().setAttribute("aria-label", "pagination");
        this.content = new PaginationContent();
        add(content);
    }

    // -----------------------------------------------------------------------
    // Content access
    // -----------------------------------------------------------------------

    /**
     * Returns the inner {@link PaginationContent} ({@code <ul>}) where
     * {@link PaginationItem}s should be added.
     *
     * @return the content list (never null)
     */
    public PaginationContent getContent() {
        return content;
    }
}

