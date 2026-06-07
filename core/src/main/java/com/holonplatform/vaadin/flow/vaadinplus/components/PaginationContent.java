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

import com.vaadin.flow.component.html.UnorderedList;

/**
 * The {@code <ul>} flex container that holds {@link PaginationItem}s inside a
 * {@link Pagination} nav.
 *
 * <p>Mirrors the shadcn/ui {@code PaginationContent} element.</p>
 *
 * @see Pagination
 * @see PaginationItem
 */
public class PaginationContent extends UnorderedList {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an empty content list.
     */
    public PaginationContent() {
        addClassName("pagination__content");
    }

    /**
     * Adds one or more {@link PaginationItem}s; {@code null} elements are silently
     * skipped.
     *
     * @param items the items to content
     */
    public void add(PaginationItem... items) {
        if (items == null) return;
        for (PaginationItem item : items) {
            if (item != null) super.add(item);
        }
    }

    /**
     * Removes all child {@link PaginationItem}s, resetting the content list.
     */
    public void clear() {
        removeAll();
    }
}

