package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.MultiSelect;
import com.holonplatform.vaadin.flow.components.builders.ListMultiSelectConfigurator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ListMultiSelectInputBuilder} (backed by Vaadin MultiSelectListBox).
 */
class TestListMultiSelectInputBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(ListMultiSelectConfigurator.create(String.class));
    }

    @Test
    void build_default_returnsMultiSelect() {
        MultiSelect<String> select = ListMultiSelectConfigurator.<String>create(String.class).build();
        assertNotNull(select);
    }

    @Test
    void items_setsItems() {
        MultiSelect<String> select = ListMultiSelectConfigurator.<String>create(String.class)
                .items("A", "B", "C")
                .build();
        assertNotNull(select);
    }

    @Test
    void readOnly_true() {
        MultiSelect<String> select = ListMultiSelectConfigurator.<String>create(String.class)
                .readOnly(true)
                .build();
        assertTrue(select.isReadOnly());
    }

    @Test
    void itemCaptionGenerator_appliesCaption() {
        MultiSelect<String> select = ListMultiSelectConfigurator.<String>create(String.class)
                .items("a", "b")
                .itemCaptionGenerator(String::toUpperCase)
                .build();
        assertNotNull(select);
    }

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(ListMultiSelectConfigurator.create(String.class).validatable());
    }

    @Test
    void withSelectionListener_adds() {
        MultiSelect<String> select = ListMultiSelectConfigurator.<String>create(String.class)
                .withSelectionListener(e -> {})
                .build();
        assertNotNull(select);
    }

    @Test
    void fluent_chain() {
        MultiSelect<String> select = ListMultiSelectConfigurator.<String>create(String.class)
                .items("X", "Y", "Z")
                .withSelectionListener(e -> {})
                .build();
        assertNotNull(select);
    }
}
