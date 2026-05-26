package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.NumberFieldInputBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link NumberFieldInputBuilder}.
 */
class TestNumberFieldInputBuilder {

    @Test
    void create_integerType_returnsNonNull() {
        assertNotNull(NumberFieldInputBuilder.create(Integer.class));
    }

    @Test
    void create_doubleType_returnsNonNull() {
        assertNotNull(NumberFieldInputBuilder.create(Double.class));
    }

    @Test
    void build_default_returnsInput() {
        Input<Integer> input = NumberFieldInputBuilder.create(Integer.class).build();
        assertNotNull(input);
    }

    @Test
    void label_setsLabel() {
        Input<Integer> input = NumberFieldInputBuilder.create(Integer.class)
                .label("Quantity")
                .build();
        assertNotNull(input);
    }

    @Test
    void required_true() {
        Input<Integer> input = NumberFieldInputBuilder.create(Integer.class)
                .required(true)
                .build();
        assertNotNull(input);
    }

    @Test
    void readOnly_true() {
        Input<Integer> input = NumberFieldInputBuilder.create(Integer.class)
                .readOnly(true)
                .build();
        assertTrue(input.isReadOnly());
    }

    @Test
    void withValueChangeListener_adds() {
        Input<Integer> input = NumberFieldInputBuilder.create(Integer.class)
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
    }

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(NumberFieldInputBuilder.create(Integer.class).validatable());
    }

    @Test
    void fluent_chain_fullExample() {
        Input<Double> input = NumberFieldInputBuilder.create(Double.class)
                .label("Price")
                .required(true)
                .readOnly(false)
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
        assertFalse(input.isReadOnly());
    }
}
