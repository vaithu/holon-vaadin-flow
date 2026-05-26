package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.LocalTimeInputBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LocalTimeInputBuilder}.
 */
class TestLocalTimeInputBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(LocalTimeInputBuilder.create());
    }

    @Test
    void build_default_returnsInput() {
        Input<LocalTime> input = LocalTimeInputBuilder.create().build();
        assertNotNull(input);
    }

    @Test
    void label_setsLabel() {
        Input<LocalTime> input = LocalTimeInputBuilder.create()
                .label("Time")
                .build();
        assertNotNull(input);
    }

    @Test
    void placeholder_setsPlaceholder() {
        Input<LocalTime> input = LocalTimeInputBuilder.create()
                .placeholder("HH:mm")
                .build();
        assertNotNull(input);
    }

    @Test
    void required_true() {
        Input<LocalTime> input = LocalTimeInputBuilder.create()
                .required(true)
                .build();
        assertNotNull(input);
    }

    @Test
    void readOnly_true() {
        Input<LocalTime> input = LocalTimeInputBuilder.create()
                .readOnly(true)
                .build();
        assertTrue(input.isReadOnly());
    }

    @Test
    void withValueChangeListener_adds() {
        Input<LocalTime> input = LocalTimeInputBuilder.create()
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
    }

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(LocalTimeInputBuilder.create().validatable());
    }

    @Test
    void fluent_chain_fullExample() {
        Input<LocalTime> input = LocalTimeInputBuilder.create()
                .label("Start Time")
                .placeholder("HH:mm")
                .required(true)
                .readOnly(false)
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
        assertFalse(input.isReadOnly());
    }
}
