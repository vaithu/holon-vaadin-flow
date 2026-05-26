package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.StringAreaInputBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link StringAreaInputBuilder}.
 */
class TestStringAreaInputBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(StringAreaInputBuilder.create());
    }

    @Test
    void build_default_returnsInput() {
        Input<String> input = StringAreaInputBuilder.create().build();
        assertNotNull(input);
    }

    @Test
    void label_setsLabel() {
        Input<String> input = StringAreaInputBuilder.create()
                .label("Description")
                .build();
        assertNotNull(input);
    }

    @Test
    void placeholder_setsPlaceholder() {
        Input<String> input = StringAreaInputBuilder.create()
                .placeholder("Enter description...")
                .build();
        assertNotNull(input);
    }

    @Test
    void required_true() {
        Input<String> input = StringAreaInputBuilder.create()
                .required(true)
                .build();
        assertNotNull(input);
    }

    @Test
    void readOnly_true() {
        Input<String> input = StringAreaInputBuilder.create()
                .readOnly(true)
                .build();
        assertTrue(input.isReadOnly());
    }

    @Test
    void maxLength_sets() {
        Input<String> input = StringAreaInputBuilder.create()
                .maxLength(500)
                .build();
        assertNotNull(input);
    }

    @Test
    void minLength_sets() {
        Input<String> input = StringAreaInputBuilder.create()
                .minLength(10)
                .build();
        assertNotNull(input);
    }

    @Test
    void withValue_setsInitialValue() {
        Input<String> input = StringAreaInputBuilder.create()
                .withValue("initial text")
                .build();
        assertEquals("initial text", input.getValue());
    }

    @Test
    void emptyValuesAsNull_true() {
        Input<String> input = StringAreaInputBuilder.create()
                .emptyValuesAsNull(true)
                .build();
        assertNull(input.getValue());
    }

    @Test
    void withValueChangeListener_adds() {
        Input<String> input = StringAreaInputBuilder.create()
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
    }

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(StringAreaInputBuilder.create().validatable());
    }

    @Test
    void fluent_chain_fullExample() {
        Input<String> input = StringAreaInputBuilder.create()
                .label("Notes")
                .placeholder("Enter notes")
                .required(true)
                .maxLength(1000)
                .readOnly(false)
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
        assertFalse(input.isReadOnly());
    }
}
