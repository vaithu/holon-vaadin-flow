package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.builders.SingleSelectConfigurator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SingleSelectInputBuilder} (backed by Vaadin Select).
 */
class TestSingleSelectInputBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(SingleSelectConfigurator.create(String.class));
    }

    @Test
    void build_default_returnsSingleSelect() {
        SingleSelect<String> select = SingleSelectConfigurator.<String>create(String.class).build();
        assertNotNull(select);
    }

    @Test
    void label_setsLabel() {
        SingleSelect<String> select = SingleSelectConfigurator.<String>create(String.class)
                .label("Country")
                .build();
        assertNotNull(select);
    }

    @Test
    void placeholder_setsPlaceholder() {
        SingleSelect<String> select = SingleSelectConfigurator.<String>create(String.class)
                .placeholder("Select...")
                .build();
        assertNotNull(select);
    }

    @Test
    void items_setsItems() {
        SingleSelect<String> select = SingleSelectConfigurator.<String>create(String.class)
                .items("A", "B", "C")
                .build();
        assertNotNull(select);
    }

    @Test
    void required_true() {
        SingleSelect<String> select = SingleSelectConfigurator.<String>create(String.class)
                .required(true)
                .build();
        assertNotNull(select);
    }

    @Test
    void readOnly_true() {
        SingleSelect<String> select = SingleSelectConfigurator.<String>create(String.class)
                .readOnly(true)
                .build();
        assertTrue(select.isReadOnly());
    }

    @Test
    void emptySelectionAllowed() {
        SingleSelect<String> select = SingleSelectConfigurator.<String>create(String.class)
                .emptySelectionAllowed(true)
                .build();
        assertNotNull(select);
    }

    @Test
    void itemCaptionGenerator_appliesCaption() {
        SingleSelect<String> select = SingleSelectConfigurator.<String>create(String.class)
                .items("a", "b")
                .itemCaptionGenerator(String::toUpperCase)
                .build();
        assertNotNull(select);
    }

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(SingleSelectConfigurator.create(String.class).validatable());
    }

    @Test
    void withSelectionListener_adds() {
        SingleSelect<String> select = SingleSelectConfigurator.<String>create(String.class)
                .withSelectionListener(e -> {})
                .build();
        assertNotNull(select);
    }

    @Test
    void enabled_false() {
        SingleSelect<String> select = SingleSelectConfigurator.<String>create(String.class)
                .enabled(false)
                .build();
        assertNotNull(select);
    }

    @Test
    void fluent_chain() {
        SingleSelect<String> select = SingleSelectConfigurator.<String>create(String.class)
                .label("Status")
                .placeholder("Choose")
                .items("Active", "Inactive")
                .required(true)
                .emptySelectionAllowed(false)
                .withSelectionListener(e -> {})
                .build();
        assertNotNull(select);
    }
}
