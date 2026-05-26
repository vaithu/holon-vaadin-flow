package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.DateInputBuilder;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DateInputBuilder}.
 */
class TestDateInputBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(DateInputBuilder.create());
    }

    @Test
    void build_default_returnsInput() {
        Input<Date> input = DateInputBuilder.create().build();
        assertNotNull(input);
    }

    @Test
    void label_setsLabel() {
        Input<Date> input = DateInputBuilder.create()
                .label("Date")
                .build();
        assertNotNull(input);
    }

    @Test
    void required_true() {
        Input<Date> input = DateInputBuilder.create()
                .required(true)
                .build();
        assertNotNull(input);
    }

    @Test
    void readOnly_true() {
        Input<Date> input = DateInputBuilder.create()
                .readOnly(true)
                .build();
        assertTrue(input.isReadOnly());
    }

    @Test
    void withValue_setsValue() {
        Date now = new Date();
        Input<Date> input = DateInputBuilder.create()
                .withValue(now)
                .build();
        assertNotNull(input);
    }

    @Test
    void withValueChangeListener_adds() {
        Input<Date> input = DateInputBuilder.create()
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
    }

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(DateInputBuilder.create().validatable());
    }

    @Test
    void fluent_chain_fullExample() {
        Input<Date> input = DateInputBuilder.create()
                .label("Birth Date")
                .required(true)
                .readOnly(false)
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
        assertFalse(input.isReadOnly());
    }
}
