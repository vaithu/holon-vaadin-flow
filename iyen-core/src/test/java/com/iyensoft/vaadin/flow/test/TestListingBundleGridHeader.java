/*
 * Copyright 2016-2017 Axioma srl.
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
package com.iyensoft.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.Components;
import com.iyensoft.vaadin.flow.components.ListingBundle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.button.Button;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link com.iyensoft.vaadin.flow.components.ListingBundleBuilder#gridHeader(String)}
 * and the {@code gridHeader(Component...)} context-action wiring — the title is a plain heading and
 * context actions are wired into the {@link ListingBundle#toolbar()}'s bulk-actions row.
 */
class TestListingBundleGridHeader {

    static class Product {
        private final String name;

        Product(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    void gridHeader_withContextActions_attachesActionsToToolbar() {
        var deleteButton = new Button("Delete");

        ListingBundle<Product> bundle = Components.listing(Product.class)
                .gridHeader("Products")
                .gridHeader(deleteButton)
                .fetch((q, text, sort) -> Stream.empty())
                .build();

        Component header = bundle.header();
        assertNotNull(header);
        assertInstanceOf(HasText.class, header);
        assertEquals("Products", ((HasText) header).getText());
        assertTrue(isAttachedToBundle(bundle, deleteButton), "context action must be attached somewhere under the ListingBundle hierarchy");
    }

    @Test
    void standaloneBundle_addsHeaderAsChildWhenConfigured() {
        ListingBundle<Product> bundle = Components.listing(Product.class)
                .gridHeader("Products")
                .fetch((q, text, sort) -> Stream.empty())
                .build();

        Component header = bundle.header();
        assertNotNull(header);
        assertSame(header, bundle.header(), "bundle.header() should return the cached header instance");
        assertTrue(isAttachedToBundle(bundle, header), "standalone ListingBundle must add its non-null header as a child");
    }

    @Test
    void gridHeader_contextActionsWithoutTitle_stillAttachToToolbar() {
        var deleteButton = new Button("Delete");

        ListingBundle<Product> bundle = Components.listing(Product.class)
                .gridHeader(deleteButton)
                .fetch((q, text, sort) -> Stream.empty())
                .build();

        assertNull(bundle.header());
        assertTrue(isAttachedToBundle(bundle, deleteButton),
                "context actions are wired into the toolbar regardless of whether a title is configured");
    }

    private static boolean isAttachedToBundle(ListingBundle<?> bundle, Component child) {
        var current = child.getParent().orElse(null);
        while (current != null) {
            if (current == bundle) {
                return true;
            }
            current = current.getParent().orElse(null);
        }
        return false;
    }
}


