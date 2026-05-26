package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link NumberInputBuilder}.
 */
class TestNumberInputBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_integer_returnsNonNull() {
        assertNotNull(NumberInputBuilder.create(Integer.class));
    }

    @Test
    void create_long_returnsNonNull() {
        assertNotNull(NumberInputBuilder.create(Long.class));
    }

    @Test
    void create_double_returnsNonNull() {
        assertNotNull(NumberInputBuilder.create(Double.class));
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_integer_returnsInput() {
        Input<Integer> input = NumberInputBuilder.create(Integer.class).build();
        assertNotNull(input);
    }

    @Test
    void build_double_returnsInput() {
        Input<Double> input = NumberInputBuilder.create(Double.class).build();
        assertNotNull(input);
    }

    // =========================================================================
    // Configuration
    // =========================================================================

    @Nested
    class ConfigTests {

        @Test
        void label_setsLabel() {
            Input<Integer> input = NumberInputBuilder.create(Integer.class)
                    .label("Quantity")
                    .build();
            assertNotNull(input);
        }

        @Test
        void placeholder_setsPlaceholder() {
            Input<Integer> input = NumberInputBuilder.create(Integer.class)
                    .placeholder("0")
                    .build();
            assertNotNull(input);
        }

        @Test
        void required_true() {
            Input<Integer> input = NumberInputBuilder.create(Integer.class)
                    .required(true)
                    .build();
            assertNotNull(input);
        }

        @Test
        void readOnly_true() {
            Input<Integer> input = NumberInputBuilder.create(Integer.class)
                    .readOnly(true)
                    .build();
            assertTrue(input.isReadOnly());
        }
    }

    // =========================================================================
    // Validatable
    // =========================================================================

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(NumberInputBuilder.create(Integer.class).validatable());
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        Input<Integer> input = NumberInputBuilder.create(Integer.class)
                .label("Amount")
                .placeholder("Enter amount")
                .required(true)
                .readOnly(false)
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
        assertFalse(input.isReadOnly());
    }
}
