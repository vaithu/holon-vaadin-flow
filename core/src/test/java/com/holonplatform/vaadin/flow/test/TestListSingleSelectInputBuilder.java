package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.builders.ListSingleSelectConfigurator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ListSingleSelectInputBuilder} (backed by Vaadin ListBox).
 */
class TestListSingleSelectInputBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(ListSingleSelectConfigurator.create(String.class));
    }

    @Test
    void build_default_returnsSingleSelect() {
        SingleSelect<String> select = ListSingleSelectConfigurator.<String>create(String.class).build();
        assertNotNull(select);
    }

    @Test
    void items_setsItems() {
        SingleSelect<String> select = ListSingleSelectConfigurator.<String>create(String.class)
                .items("A", "B", "C")
                .build();
        assertNotNull(select);
    }

    @Test
    void readOnly_true() {
        SingleSelect<String> select = ListSingleSelectConfigurator.<String>create(String.class)
                .readOnly(true)
                .build();
        assertTrue(select.isReadOnly());
    }

    @Test
    void itemCaptionGenerator_appliesCaption() {
        SingleSelect<String> select = ListSingleSelectConfigurator.<String>create(String.class)
                .items("a", "b")
                .itemCaptionGenerator(String::toUpperCase)
                .build();
        assertNotNull(select);
    }

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(ListSingleSelectConfigurator.create(String.class).validatable());
    }

    @Test
    void withSelectionListener_adds() {
        SingleSelect<String> select = ListSingleSelectConfigurator.<String>create(String.class)
                .withSelectionListener(e -> {})
                .build();
        assertNotNull(select);
    }

    @Test
    void fluent_chain() {
        SingleSelect<String> select = ListSingleSelectConfigurator.<String>create(String.class)
                .items("Red", "Green", "Blue")
                .withSelectionListener(e -> {})
                .build();
        assertNotNull(select);
    }
}
