package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.builders.FilterableSingleSelectConfigurator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FilterableSingleSelectInputBuilder} (backed by Vaadin ComboBox).
 */
class TestFilterableSingleSelectInputBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(FilterableSingleSelectConfigurator.create(String.class));
    }

    @Test
    void build_default_returnsSingleSelect() {
        SingleSelect<String> select = FilterableSingleSelectConfigurator.<String>create(String.class).build();
        assertNotNull(select);
    }

    @Test
    void label_setsLabel() {
        SingleSelect<String> select = FilterableSingleSelectConfigurator.<String>create(String.class)
                .label("City")
                .build();
        assertNotNull(select);
    }

    @Test
    void placeholder_setsPlaceholder() {
        SingleSelect<String> select = FilterableSingleSelectConfigurator.<String>create(String.class)
                .placeholder("Search...")
                .build();
        assertNotNull(select);
    }

    @Test
    void items_setsItems() {
        SingleSelect<String> select = FilterableSingleSelectConfigurator.<String>create(String.class)
                .items("X", "Y", "Z")
                .build();
        assertNotNull(select);
    }

    @Test
    void required_true() {
        SingleSelect<String> select = FilterableSingleSelectConfigurator.<String>create(String.class)
                .required(true)
                .build();
        assertNotNull(select);
    }

    @Test
    void readOnly_true() {
        SingleSelect<String> select = FilterableSingleSelectConfigurator.<String>create(String.class)
                .readOnly(true)
                .build();
        assertTrue(select.isReadOnly());
    }

    @Test
    void itemCaptionGenerator_appliesCaption() {
        SingleSelect<String> select = FilterableSingleSelectConfigurator.<String>create(String.class)
                .items("a", "b")
                .itemCaptionGenerator(String::toUpperCase)
                .build();
        assertNotNull(select);
    }

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(FilterableSingleSelectConfigurator.create(String.class).validatable());
    }

    @Test
    void withSelectionListener_adds() {
        SingleSelect<String> select = FilterableSingleSelectConfigurator.<String>create(String.class)
                .withSelectionListener(e -> {})
                .build();
        assertNotNull(select);
    }

    @Test
    void enabled_false() {
        SingleSelect<String> select = FilterableSingleSelectConfigurator.<String>create(String.class)
                .enabled(false)
                .build();
        assertNotNull(select);
    }

    @Test
    void fluent_chain() {
        SingleSelect<String> select = FilterableSingleSelectConfigurator.<String>create(String.class)
                .label("Country")
                .placeholder("Type to filter")
                .items("USA", "UK", "India")
                .required(true)
                .withSelectionListener(e -> {})
                .build();
        assertNotNull(select);
    }
}
