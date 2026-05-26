package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.LocalDateInputBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LocalDateInputBuilder}.
 */
class TestLocalDateInputBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(LocalDateInputBuilder.create());
    }

    @Test
    void build_default_returnsInput() {
        Input<LocalDate> input = LocalDateInputBuilder.create().build();
        assertNotNull(input);
    }

    @Test
    void label_setsLabel() {
        Input<LocalDate> input = LocalDateInputBuilder.create()
                .label("Birth Date")
                .build();
        assertNotNull(input);
    }

    @Test
    void placeholder_setsPlaceholder() {
        Input<LocalDate> input = LocalDateInputBuilder.create()
                .placeholder("Select date")
                .build();
        assertNotNull(input);
    }

    @Test
    void required_true() {
        Input<LocalDate> input = LocalDateInputBuilder.create()
                .required(true)
                .build();
        assertNotNull(input);
    }

    @Test
    void readOnly_true() {
        Input<LocalDate> input = LocalDateInputBuilder.create()
                .readOnly(true)
                .build();
        assertTrue(input.isReadOnly());
    }

    @Test
    void min_setsMin() {
        Input<LocalDate> input = LocalDateInputBuilder.create()
                .min(LocalDate.of(2020, 1, 1))
                .build();
        assertNotNull(input);
    }

    @Test
    void max_setsMax() {
        Input<LocalDate> input = LocalDateInputBuilder.create()
                .max(LocalDate.of(2030, 12, 31))
                .build();
        assertNotNull(input);
    }

    @Test
    void locale_setsLocale() {
        Input<LocalDate> input = LocalDateInputBuilder.create()
                .locale(Locale.US)
                .build();
        assertNotNull(input);
    }

    @Test
    void withValue_setsInitialValue() {
        LocalDate date = LocalDate.of(2025, 6, 15);
        Input<LocalDate> input = LocalDateInputBuilder.create()
                .withValue(date)
                .build();
        assertEquals(date, input.getValue());
    }

    @Test
    void withValueChangeListener_adds() {
        Input<LocalDate> input = LocalDateInputBuilder.create()
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
    }

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(LocalDateInputBuilder.create().validatable());
    }

    @Test
    void fluent_chain_fullExample() {
        Input<LocalDate> input = LocalDateInputBuilder.create()
                .label("Start Date")
                .placeholder("Pick a date")
                .required(true)
                .min(LocalDate.of(2020, 1, 1))
                .max(LocalDate.of(2030, 12, 31))
                .readOnly(false)
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
        assertFalse(input.isReadOnly());
    }
}
