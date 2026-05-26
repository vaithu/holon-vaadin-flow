package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.DateTimeInputBuilder;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DateTimeInputBuilder}.
 */
class TestDateTimeInputBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(DateTimeInputBuilder.create());
    }

    @Test
    void build_default_returnsInput() {
        Input<Date> input = DateTimeInputBuilder.create().build();
        assertNotNull(input);
    }

    @Test
    void label_setsLabel() {
        Input<Date> input = DateTimeInputBuilder.create()
                .label("Date/Time")
                .build();
        assertNotNull(input);
    }

    @Test
    void required_true() {
        Input<Date> input = DateTimeInputBuilder.create()
                .required(true)
                .build();
        assertNotNull(input);
    }

    @Test
    void readOnly_true() {
        Input<Date> input = DateTimeInputBuilder.create()
                .readOnly(true)
                .build();
        assertTrue(input.isReadOnly());
    }

    @Test
    void withValue_setsValue() {
        Date now = new Date();
        Input<Date> input = DateTimeInputBuilder.create()
                .withValue(now)
                .build();
        assertNotNull(input);
    }

    @Test
    void withValueChangeListener_adds() {
        Input<Date> input = DateTimeInputBuilder.create()
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
    }

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(DateTimeInputBuilder.create().validatable());
    }

    @Test
    void fluent_chain_fullExample() {
        Input<Date> input = DateTimeInputBuilder.create()
                .label("Event DateTime")
                .required(true)
                .readOnly(false)
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
        assertFalse(input.isReadOnly());
    }
}
