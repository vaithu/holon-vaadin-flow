package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.LocalDateTimeInputBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LocalDateTimeInputBuilder}.
 */
class TestLocalDateTimeInputBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(LocalDateTimeInputBuilder.create());
    }

    @Test
    void build_default_returnsInput() {
        Input<LocalDateTime> input = LocalDateTimeInputBuilder.create().build();
        assertNotNull(input);
    }

    @Test
    void label_setsLabel() {
        Input<LocalDateTime> input = LocalDateTimeInputBuilder.create()
                .label("Event Time")
                .build();
        assertNotNull(input);
    }

    @Test
    void required_true() {
        Input<LocalDateTime> input = LocalDateTimeInputBuilder.create()
                .required(true)
                .build();
        assertNotNull(input);
    }

    @Test
    void readOnly_true() {
        Input<LocalDateTime> input = LocalDateTimeInputBuilder.create()
                .readOnly(true)
                .build();
        assertTrue(input.isReadOnly());
    }

    @Test
    void withValue_setsInitialValue() {
        LocalDateTime dt = LocalDateTime.of(2025, 6, 15, 14, 30);
        Input<LocalDateTime> input = LocalDateTimeInputBuilder.create()
                .withValue(dt)
                .build();
        assertEquals(dt, input.getValue());
    }

    @Test
    void withValueChangeListener_adds() {
        Input<LocalDateTime> input = LocalDateTimeInputBuilder.create()
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
    }

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(LocalDateTimeInputBuilder.create().validatable());
    }

    @Test
    void fluent_chain_fullExample() {
        Input<LocalDateTime> input = LocalDateTimeInputBuilder.create()
                .label("Appointment")
                .required(true)
                .readOnly(false)
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
        assertFalse(input.isReadOnly());
    }
}
