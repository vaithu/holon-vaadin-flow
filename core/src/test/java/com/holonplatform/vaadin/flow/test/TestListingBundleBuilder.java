package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.vaadin.flow.component.grid.Grid;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestListingBundleBuilder {

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
    void listingBuilder_buildsDivBundle() {
        ListingBundle<Product> bundle = Components.listing(Product.class)
                .fetch((q, text, sort) -> java.util.stream.Stream.empty())
                .build();

        assertNotNull(bundle);
        assertInstanceOf(ListingBundle.class, bundle);
        assertInstanceOf(Grid.class, bundle.grid());
        assertNotNull(bundle.toolbar());
        assertNotNull(bundle.footer());
    }
}
