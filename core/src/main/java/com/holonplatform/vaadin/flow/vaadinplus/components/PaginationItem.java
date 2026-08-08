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

import java.io.Serial;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.ListItem;

/**
 * A {@code <li>} wrapper for a single pagination control
 * ({@link PaginationLink}, {@link PaginationPrevious}, {@link PaginationNext},
 * or {@link PaginationEllipsis}) inside a {@link PaginationContent}.
 *
 * <p>Mirrors the shadcn/ui {@code PaginationItem} element.</p>
 *
 * @see PaginationContent
 */
public class PaginationItem extends ListItem {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates an empty pagination item.
     */
    public PaginationItem() {
        addClassName("pagination__item");
    }

    /**
     * Creates a pagination item wrapping the given content component.
     *
     * @param content the control to wrap (null-safe; empty item created if null)
     */
    public PaginationItem(Component content) {
        this();
        if (content != null) {
            add(content);
        }
    }
}
