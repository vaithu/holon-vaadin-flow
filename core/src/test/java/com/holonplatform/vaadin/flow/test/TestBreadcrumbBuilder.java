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
package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbPage;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.RouterLink;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestBreadcrumbBuilder {

    @Test
    void breadcrumbBuilder_chainedTypedItems_areAddedInOrder() {
        Breadcrumb breadcrumb = Components.breadcrumb()
                .item(new BreadcrumbItem(linkWithHref("home")))
                .separator()
                .item(new BreadcrumbItem(linkWithHref("components")))
                .page(new BreadcrumbPage("Breadcrumb"))
                .build();

        assertEquals(4, olChildCount(breadcrumb));
        assertTrue(childClassContains(breadcrumb, 0, "breadcrumb__item"));
        assertTrue(childClassContains(breadcrumb, 1, "breadcrumb__separator"));
        assertTrue(childClassContains(breadcrumb, 2, "breadcrumb__item"));
        assertTrue(breadcrumb.getElement().getChild(0).getChild(3).getChild(0)
                .getAttribute("class").contains("breadcrumb__page"));
    }

    @Test
    void breadcrumbBuilder_separatorSupplier_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> Components.breadcrumb().separatorSupplier(null));
    }

    @Test
    void breadcrumbBuilder_clearsExistingItems() {
        Breadcrumb breadcrumb = Components.breadcrumb()
                .item(new BreadcrumbItem(linkWithHref("home")))
                .clear()
                .page("Current")
                .build();

        assertEquals(1, olChildCount(breadcrumb));
    }

    private static RouterLink linkWithHref(String href) {
        RouterLink link = new RouterLink();
        link.getElement().setAttribute("href", href);
        link.add(new Span(href));
        return link;
    }

    private static int olChildCount(Breadcrumb breadcrumb) {
        return breadcrumb.getElement().getChild(0).getChildCount();
    }

    private static boolean childClassContains(Breadcrumb breadcrumb, int index, String className) {
        return breadcrumb.getElement().getChild(0).getChild(index).getAttribute("class").contains(className);
    }
}



