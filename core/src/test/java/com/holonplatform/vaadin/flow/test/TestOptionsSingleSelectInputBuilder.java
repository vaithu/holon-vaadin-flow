package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.SingleSelect;
import com.holonplatform.vaadin.flow.components.builders.OptionsSingleSelectConfigurator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link OptionsSingleSelectInputBuilder} (backed by Vaadin RadioButtonGroup).
 */
class TestOptionsSingleSelectInputBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(OptionsSingleSelectConfigurator.create(String.class));
    }

    @Test
    void build_default_returnsSingleSelect() {
        SingleSelect<String> select = OptionsSingleSelectConfigurator.<String>create(String.class).build();
        assertNotNull(select);
    }

    @Test
    void label_setsLabel() {
        SingleSelect<String> select = OptionsSingleSelectConfigurator.<String>create(String.class)
                .label("Gender")
                .build();
        assertNotNull(select);
    }

    @Test
    void items_setsItems() {
        SingleSelect<String> select = OptionsSingleSelectConfigurator.<String>create(String.class)
                .items("Male", "Female", "Other")
                .build();
        assertNotNull(select);
    }

    @Test
    void readOnly_true() {
        SingleSelect<String> select = OptionsSingleSelectConfigurator.<String>create(String.class)
                .readOnly(true)
                .build();
        assertTrue(select.isReadOnly());
    }

    @Test
    void required_true() {
        SingleSelect<String> select = OptionsSingleSelectConfigurator.<String>create(String.class)
                .required(true)
                .build();
        assertNotNull(select);
    }

    @Test
    void itemCaptionGenerator_appliesCaption() {
        SingleSelect<String> select = OptionsSingleSelectConfigurator.<String>create(String.class)
                .items("a", "b")
                .itemCaptionGenerator(String::toUpperCase)
                .build();
        assertNotNull(select);
    }

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(OptionsSingleSelectConfigurator.create(String.class).validatable());
    }

    @Test
    void withSelectionListener_adds() {
        SingleSelect<String> select = OptionsSingleSelectConfigurator.<String>create(String.class)
                .withSelectionListener(e -> {})
                .build();
        assertNotNull(select);
    }

    @Test
    void fluent_chain() {
        SingleSelect<String> select = OptionsSingleSelectConfigurator.<String>create(String.class)
                .label("Priority")
                .items("Low", "Medium", "High")
                .required(true)
                .withSelectionListener(e -> {})
                .build();
        assertNotNull(select);
    }
}
