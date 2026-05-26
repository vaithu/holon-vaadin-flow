package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.StepperBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.FlowStepper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link StepperBuilder}.
 */
class TestStepperBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_returnsNonNull() {
        assertNotNull(StepperBuilder.create());
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_default_returnsFlowStepper() {
        FlowStepper stepper = StepperBuilder.create().build();
        assertNotNull(stepper);
    }

    // =========================================================================
    // Steps
    // =========================================================================

    @Nested
    class StepsTests {

        @Test
        void steps_varargs() {
            FlowStepper stepper = StepperBuilder.create()
                    .steps("Step 1", "Step 2", "Step 3")
                    .build();
            assertNotNull(stepper);
        }

        @Test
        void steps_list() {
            FlowStepper stepper = StepperBuilder.create()
                    .steps(List.of("Account", "Details", "Confirm"))
                    .build();
            assertNotNull(stepper);
        }
    }

    // =========================================================================
    // CurrentStep
    // =========================================================================

    @Test
    void currentStep_sets() {
        FlowStepper stepper = StepperBuilder.create()
                .steps("A", "B", "C")
                .currentStep(2)
                .build();
        assertNotNull(stepper);
    }

    // =========================================================================
    // Orientation
    // =========================================================================

    @Test
    void orientation_horizontal() {
        FlowStepper stepper = StepperBuilder.create()
                .steps("A", "B")
                .orientation(FlowStepper.Orientation.HORIZONTAL)
                .build();
        assertNotNull(stepper);
    }

    @Test
    void orientation_vertical() {
        FlowStepper stepper = StepperBuilder.create()
                .steps("A", "B")
                .orientation(FlowStepper.Orientation.VERTICAL)
                .build();
        assertNotNull(stepper);
    }

    // =========================================================================
    // Variant
    // =========================================================================

    @Nested
    class VariantTests {

        @Test
        void variant_default() {
            FlowStepper stepper = StepperBuilder.create()
                    .steps("A", "B")
                    .variant(FlowStepper.Variant.DEFAULT)
                    .build();
            assertNotNull(stepper);
        }

        @Test
        void variant_numbered() {
            FlowStepper stepper = StepperBuilder.create()
                    .steps("A", "B")
                    .variant(FlowStepper.Variant.NUMBERED)
                    .build();
            assertNotNull(stepper);
        }

        @Test
        void variant_dot() {
            FlowStepper stepper = StepperBuilder.create()
                    .steps("A", "B")
                    .variant(FlowStepper.Variant.DOT)
                    .build();
            assertNotNull(stepper);
        }
    }

    // =========================================================================
    // ClickNavigation
    // =========================================================================

    @Nested
    class ClickNavigationTests {

        @Test
        void clickNavigation_completed() {
            FlowStepper stepper = StepperBuilder.create()
                    .steps("A", "B")
                    .clickNavigation(FlowStepper.ClickNavigation.COMPLETED)
                    .build();
            assertNotNull(stepper);
        }

        @Test
        void clickNavigation_all() {
            FlowStepper stepper = StepperBuilder.create()
                    .steps("A", "B")
                    .clickNavigation(FlowStepper.ClickNavigation.ALL)
                    .build();
            assertNotNull(stepper);
        }

        @Test
        void clickNavigation_none() {
            FlowStepper stepper = StepperBuilder.create()
                    .steps("A", "B")
                    .clickNavigation(FlowStepper.ClickNavigation.NONE)
                    .build();
            assertNotNull(stepper);
        }
    }

    // =========================================================================
    // Event listeners
    // =========================================================================

    @Test
    void onStepChanged_setsListener() {
        FlowStepper stepper = StepperBuilder.create()
                .steps("A", "B")
                .onStepChanged(e -> {})
                .build();
        assertNotNull(stepper);
    }

    @Test
    void onStepComplete_setsListener() {
        FlowStepper stepper = StepperBuilder.create()
                .steps("A", "B")
                .onStepComplete(e -> {})
                .build();
        assertNotNull(stepper);
    }

    @Test
    void onStepError_setsListener() {
        FlowStepper stepper = StepperBuilder.create()
                .steps("A", "B")
                .onStepError(e -> {})
                .build();
        assertNotNull(stepper);
    }

    // =========================================================================
    // Component configurator
    // =========================================================================

    @Test
    void id_setsId() {
        FlowStepper stepper = StepperBuilder.create()
                .id("wizard-stepper")
                .build();
        assertEquals("wizard-stepper", stepper.getId().orElse(null));
    }

    @Test
    void styleName_addsClass() {
        FlowStepper stepper = StepperBuilder.create()
                .styleName("checkout")
                .build();
        assertTrue(stepper.getClassNames().contains("checkout"));
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        FlowStepper stepper = StepperBuilder.create()
                .steps("Account", "Payment", "Review", "Confirm")
                .currentStep(1)
                .orientation(FlowStepper.Orientation.HORIZONTAL)
                .variant(FlowStepper.Variant.DEFAULT)
                .clickNavigation(FlowStepper.ClickNavigation.COMPLETED)
                .onStepChanged(e -> {})
                .id("checkout-stepper")
                .styleName("wizard")
                .build();
        assertNotNull(stepper);
        assertEquals("checkout-stepper", stepper.getId().orElse(null));
        assertTrue(stepper.getClassNames().contains("wizard"));
    }
}
