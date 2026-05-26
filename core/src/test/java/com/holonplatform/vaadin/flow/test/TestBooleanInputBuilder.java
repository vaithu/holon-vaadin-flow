package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.BooleanInputBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BooleanInputBuilder}.
 */
class TestBooleanInputBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(BooleanInputBuilder.create());
    }

    @Test
    void build_default_returnsInput() {
        Input<Boolean> input = BooleanInputBuilder.create().build();
        assertNotNull(input);
    }

    @Test
    void build_default_valueIsFalse() {
        Input<Boolean> input = BooleanInputBuilder.create().build();
        assertEquals(Boolean.FALSE, input.getValue());
    }

    @Test
    void label_setsLabel() {
        Input<Boolean> input = BooleanInputBuilder.create()
                .label("Active")
                .build();
        assertNotNull(input);
    }

    @Test
    void required_true() {
        Input<Boolean> input = BooleanInputBuilder.create()
                .required(true)
                .build();
        assertNotNull(input);
    }

    @Test
    void readOnly_true() {
        Input<Boolean> input = BooleanInputBuilder.create()
                .readOnly(true)
                .build();
        assertTrue(input.isReadOnly());
    }

    @Test
    void readOnly_false() {
        Input<Boolean> input = BooleanInputBuilder.create()
                .readOnly(false)
                .build();
        assertFalse(input.isReadOnly());
    }

    @Test
    void withValue_true() {
        Input<Boolean> input = BooleanInputBuilder.create()
                .withValue(true)
                .build();
        assertEquals(Boolean.TRUE, input.getValue());
    }

    @Test
    void withValueChangeListener_adds() {
        Input<Boolean> input = BooleanInputBuilder.create()
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
    }

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(BooleanInputBuilder.create().validatable());
    }

    @Test
    void fluent_chain_fullExample() {
        Input<Boolean> input = BooleanInputBuilder.create()
                .label("Subscribe")
                .withValue(false)
                .readOnly(false)
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
        assertEquals(Boolean.FALSE, input.getValue());
        assertFalse(input.isReadOnly());
    }
}
