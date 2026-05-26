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
import com.vaadin.flow.component.html.Span;

/**
 * A gap indicator ({@code …}) shown in a {@link PaginationItem} when page numbers
 * are skipped.
 *
 * <p>Renders as:</p>
 * <pre>
 * &lt;span class="pagination__ellipsis" aria-hidden="true"&gt;
 *   &lt;span class="pagination__ellipsis-icon"&gt;&amp;hellip;&lt;/span&gt;
 * &lt;/span&gt;
 * </pre>
 *
 * <p>Mirrors the shadcn/ui {@code PaginationEllipsis} element.</p>
 *
 * @see PaginationItem
 */
public class PaginationEllipsis extends Span {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the ellipsis indicator ({@code …}).
     */
    public PaginationEllipsis() {
        addClassName("pagination__ellipsis");
        // Decorative — screen readers should skip it
        getElement().setAttribute("aria-hidden", "true");

        Span dots = Components.span().text("\u2026").styleName("pagination__ellipsis-icon").build();   // Unicode HORIZONTAL ELLIPSIS
        add(dots);
    }
}

