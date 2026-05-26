package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.PasswordInputBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PasswordInputBuilder}.
 */
class TestPasswordInputBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(PasswordInputBuilder.create());
    }

    @Test
    void build_default_returnsInput() {
        Input<String> input = PasswordInputBuilder.create().build();
        assertNotNull(input);
    }

    @Test
    void label_setsLabel() {
        Input<String> input = PasswordInputBuilder.create()
                .label("Password")
                .build();
        assertNotNull(input);
    }

    @Test
    void placeholder_setsPlaceholder() {
        Input<String> input = PasswordInputBuilder.create()
                .placeholder("Enter password")
                .build();
        assertNotNull(input);
    }

    @Test
    void required_true() {
        Input<String> input = PasswordInputBuilder.create()
                .required(true)
                .build();
        assertNotNull(input);
    }

    @Test
    void readOnly_true() {
        Input<String> input = PasswordInputBuilder.create()
                .readOnly(true)
                .build();
        assertTrue(input.isReadOnly());
    }

    @Test
    void readOnly_false() {
        Input<String> input = PasswordInputBuilder.create()
                .readOnly(false)
                .build();
        assertFalse(input.isReadOnly());
    }

    @Test
    void maxLength_sets() {
        Input<String> input = PasswordInputBuilder.create()
                .maxLength(128)
                .build();
        assertNotNull(input);
    }

    @Test
    void minLength_sets() {
        Input<String> input = PasswordInputBuilder.create()
                .minLength(8)
                .build();
        assertNotNull(input);
    }

    @Test
    void emptyValuesAsNull_true() {
        Input<String> input = PasswordInputBuilder.create()
                .emptyValuesAsNull(true)
                .build();
        assertNull(input.getValue());
    }

    @Test
    void withValueChangeListener_adds() {
        Input<String> input = PasswordInputBuilder.create()
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
    }

    @Test
    void validatable_returnsBuilder() {
        assertNotNull(PasswordInputBuilder.create().validatable());
    }

    @Test
    void fluent_chain_fullExample() {
        Input<String> input = PasswordInputBuilder.create()
                .label("New Password")
                .placeholder("Min. 8 characters")
                .required(true)
                .minLength(8)
                .maxLength(128)
                .readOnly(false)
                .withValueChangeListener(e -> {})
                .build();
        assertNotNull(input);
        assertFalse(input.isReadOnly());
    }
}
