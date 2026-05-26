package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.InputOTPBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputOTP;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link InputOTPBuilder} and the {@link InputOTP} component.
 */
class TestInputOTPBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_returnsNonNull() {
        assertNotNull(InputOTPBuilder.create());
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_empty_returnsInputOTP() {
        InputOTP otp = InputOTPBuilder.create().build();
        assertNotNull(otp);
        assertTrue(otp.getClassNames().contains("input-otp"));
    }

    // =========================================================================
    // Group
    // =========================================================================

    @Nested
    class GroupTests {

        @Test
        void group_single() {
            InputOTP otp = InputOTPBuilder.create()
                    .group(6)
                    .build();
            assertNotNull(otp);
        }

        @Test
        void group_splitWithSeparator() {
            InputOTP otp = InputOTPBuilder.create()
                    .group(3)
                    .separator()
                    .group(3)
                    .build();
            assertNotNull(otp);
        }

        @Test
        void group_customSeparator() {
            InputOTP otp = InputOTPBuilder.create()
                    .group(3)
                    .separator(new Span("-"))
                    .group(3)
                    .build();
            assertNotNull(otp);
        }
    }

    // =========================================================================
    // Pattern
    // =========================================================================

    @Test
    void pattern_setsPattern() {
        InputOTP otp = InputOTPBuilder.create()
                .group(4)
                .pattern("[0-9]")
                .build();
        assertNotNull(otp);
    }

    // =========================================================================
    // ReadOnly
    // =========================================================================

    @Test
    void readOnly_true() {
        InputOTP otp = InputOTPBuilder.create()
                .group(4)
                .readOnly(true)
                .build();
        assertNotNull(otp);
    }

    @Test
    void readOnly_false() {
        InputOTP otp = InputOTPBuilder.create()
                .group(4)
                .readOnly(false)
                .build();
        assertNotNull(otp);
    }

    // =========================================================================
    // Callbacks
    // =========================================================================

    @Test
    void onComplete_acceptsHandler() {
        AtomicReference<String> result = new AtomicReference<>();
        InputOTP otp = InputOTPBuilder.create()
                .group(4)
                .onComplete(result::set)
                .build();
        assertNotNull(otp);
    }

    @Test
    void onValueChange_acceptsListener() {
        InputOTP otp = InputOTPBuilder.create()
                .group(4)
                .onValueChange(val -> {})
                .build();
        assertNotNull(otp);
    }

    @Test
    void validator_acceptsFunction() {
        InputOTP otp = InputOTPBuilder.create()
                .group(4)
                .validator(val -> val.length() == 4 ? null : "Must be 4 digits")
                .build();
        assertNotNull(otp);
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        AtomicReference<String> completed = new AtomicReference<>();
        InputOTP otp = InputOTPBuilder.create()
                .group(3)
                .separator()
                .group(3)
                .pattern("[0-9]")
                .readOnly(false)
                .onComplete(completed::set)
                .onValueChange(val -> {})
                .build();
        assertNotNull(otp);
    }
}
