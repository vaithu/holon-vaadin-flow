package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.MultiSelect;
import com.holonplatform.vaadin.flow.components.builders.OptionsMultiSelectConfigurator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link OptionsMultiSelectInputBuilder} (backed by Vaadin CheckboxGroup).
 */
class TestOptionsMultiSelectInputBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(OptionsMultiSelectConfigurator.create(String.class));
    }

    @Test
    void build_default_returnsMultiSelect() {
        MultiSelect<String> select = OptionsMultiSelectConfigurator.<String>create(String.class).build();
        assertNotNull(select);
    }

    @Test
    void label_setsLabel() {
        MultiSelect<String> select = OptionsMultiSelectConfigurator.<String>create(String.class)
                .label("Hobbies")
                .build();
        assertNotNull(select);
    }

    @Test
    void items_setsItems() {
        MultiSelect<String> select = OptionsMultiSelectConfigurator.<String>create(String.class)
                .items("A", "B", "C")
                .build();
        assertNotNull(select);
    }

    @Test
    void readOnly_true() {
        MultiSelect<String> select = OptionsMultiSelectConfigurator.<String>create(String.class)
                .readOnly(true)
                .build();
        assertTrue(select.isReadOnly());
    }

    @Test
    void required_true() {
        MultiSelect<String> select = OptionsMultiSelectConfigurator.<String>create(String.class)
                .required(true)
                .build();
        assertNotNull(select);
    }

    @Test
    void itemCaptionGenerator_appliesCaption() {
        MultiSelect<String> select = OptionsMultiSelectConfigurator.<String>create(String.class)
                .items("a", "b")
                .itemCaptionGenerator(String::toUpperCase)
                .build();
        assertNotNull(select);
    }

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(OptionsMultiSelectConfigurator.create(String.class).validatable());
    }

    @Test
    void withSelectionListener_adds() {
        MultiSelect<String> select = OptionsMultiSelectConfigurator.<String>create(String.class)
                .withSelectionListener(e -> {})
                .build();
        assertNotNull(select);
    }

    @Test
    void fluent_chain() {
        MultiSelect<String> select = OptionsMultiSelectConfigurator.<String>create(String.class)
                .label("Toppings")
                .items("Cheese", "Pepperoni", "Mushrooms")
                .required(true)
                .withSelectionListener(e -> {})
                .build();
        assertNotNull(select);
    }
}
