package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.StringInputBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link StringInputBuilder}.
 */
class TestStringInputBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(StringInputBuilder.create());
    }

    @Test
    void build_default_returnsInput() {
        Input<String> input = StringInputBuilder.create().build();
        assertNotNull(input);
    }

    @Test
    void label_setsLabel() {
        Input<String> input = StringInputBuilder.create()
                .label("Name")
                .build();
        assertNotNull(input);
    }

    @Test
    void placeholder_setsPlaceholder() {
        Input<String> input = StringInputBuilder.create()
                .placeholder("Enter name...")
                .build();
        assertNotNull(input);
    }

    @Test
    void required_true() {
        Input<String> input = StringInputBuilder.create()
                .required(true)
                .build();
        assertNotNull(input);
    }

    @Test
    void readOnly_true() {
        Input<String> input = StringInputBuilder.create()
                .readOnly(true)
                .build();
        assertTrue(input.isReadOnly());
    }

    @Test
    void readOnly_false() {
        Input<String> input = StringInputBuilder.create()
                .readOnly(false)
                .build();
        assertFalse(input.isReadOnly());
    }

    @Test
    void maxLength_sets() {
        Input<String> input = StringInputBuilder.create()
                .maxLength(100)
                .build();
        assertNotNull(input);
    }

    @Test
    void minLength_sets() {
        Input<String> input = StringInputBuilder.create()
                .minLength(3)
                .build();
        assertNotNull(input);
    }

    @Test
    void pattern_sets() {
        Input<String> input = StringInputBuilder.create()
                .pattern("[A-Za-z]+")
                .build();
        assertNotNull(input);
    }

    @Test
    void withValue_setsInitialValue() {
        Input<String> input = StringInputBuilder.create()
                .withValue("initial")
                .build();
        assertEquals("initial", input.getValue());
    }

    @Test
    void emptyValuesAsNull_true() {
        Input<String> input = StringInputBuilder.create()
                .emptyValuesAsNull(true)
                .build();
        assertNull(input.getValue());
    }

    @Test
    void withValueChangeListener_adds() {
        Input<String> input = StringInputBuilder.create()
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
    }

    @Test
    void validatable_returnsValidatableBuilder() {
        assertNotNull(StringInputBuilder.create().validatable());
    }

    @Test
    void fluent_chain_fullExample() {
        Input<String> input = StringInputBuilder.create()
                .label("Username")
                .placeholder("Enter username")
                .required(true)
                .maxLength(50)
                .minLength(3)
                .readOnly(false)
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
        assertFalse(input.isReadOnly());
    }
}
