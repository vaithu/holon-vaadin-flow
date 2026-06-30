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
package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link com.holonplatform.vaadin.flow.components.ListingBundleBuilder#gridHeader(String)}
 * and the new fluent {@code gridHeader(Component...)} context-action wiring.
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
    void gridHeader_withContextActions_attachesActionsToHeader() {
        var deleteButton = new Button("Delete");

        ListingBundle<Product> bundle = Components.listing(Product.class)
                .gridHeader("Products")
                .gridHeader(deleteButton)
                .fetch((q, text, sort) -> Stream.empty())
                .build();

        GridHeader header = bundle.header();
        assertNotNull(header);
        assertEquals("Products", header.getTitle().orElseThrow());
        assertTrue(header.getColumnLayout().isVisible(), "listing grid header should keep the actions column visible");
        assertTrue(isAttachedToHeader(deleteButton, header), "context action must be attached somewhere under the GridHeader hierarchy");
    }

    @Test
    void standaloneBundle_addsHeaderAsChildWhenConfigured() {
        ListingBundle<Product> bundle = Components.listing(Product.class)
                .gridHeader("Products")
                .fetch((q, text, sort) -> Stream.empty())
                .build();

        GridHeader header = bundle.header();
        assertNotNull(header);
        assertSame(header, bundle.header(), "bundle.header() should return the cached header instance");
        assertTrue(isAttachedToBundle(bundle, header), "standalone ListingBundle must add its non-null GridHeader as a child");
    }

    @Test
    void gridHeader_contextActionsWithoutTitle_doNotCreateHeader() {
        var deleteButton = new Button("Delete");

        ListingBundle<Product> bundle = Components.listing(Product.class)
                .gridHeader(deleteButton)
                .fetch((q, text, sort) -> Stream.empty())
                .build();

        assertNull(bundle.header());
        assertTrue(deleteButton.getParent().isEmpty(), "context actions should stay unattached without a GridHeader title");
    }

    private static boolean isAttachedToHeader(Button button, GridHeader header) {
        var current = button.getParent().orElse(null);
        while (current != null) {
            if (current == header) {
                return true;
            }
            current = current.getParent().orElse(null);
        }
        return false;
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


