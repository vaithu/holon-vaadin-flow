/*
 * Copyright 2016-2024 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.StepperBuilder;
import com.holonplatform.vaadin.flow.components.builders.StepperConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.FlowStepper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link FlowStepper} component family:
 * {@link FlowStepper}, {@link StepperBuilder} and {@link StepperConfigurator}.
 *
 * <p>Tests run without a live Vaadin UI or VaadinService. Browser-side
 * interactions (JS function calls, DOM events) are not verified here; only the
 * Java-layer attribute/property management and builder fluency are covered.</p>
 */
class TestFlowStepper {

    // =========================================================================
    // FlowStepper — default construction
    // =========================================================================

    @Test
    void stepper_defaultConstructor_stepsAttributeIsEmptyArray() {
        FlowStepper s = new FlowStepper();
        assertEquals("[]", s.getElement().getAttribute("steps"));
    }

    @Test
    void stepper_defaultConstructor_currentStep_isZero() {
        FlowStepper s = new FlowStepper();
        assertEquals(0, s.getCurrentStep());
    }

    @Test
    void stepper_defaultConstructor_orientation_isHorizontal() {
        FlowStepper s = new FlowStepper();
        assertEquals(FlowStepper.Orientation.HORIZONTAL, s.getOrientation());
    }

    @Test
    void stepper_defaultConstructor_variant_isDefault() {
        FlowStepper s = new FlowStepper();
        assertEquals(FlowStepper.Variant.DEFAULT, s.getVariant());
    }

    // =========================================================================
    // FlowStepper — constructors with steps
    // =========================================================================

    @Test
    void stepper_listConstructor_stepsAttributeIsValidJson() {
        FlowStepper s = new FlowStepper(List.of("Account", "Details", "Review"));
        assertEquals("[\"Account\",\"Details\",\"Review\"]",
                s.getElement().getAttribute("steps"));
    }

    @Test
    void stepper_listAndStepConstructor_currentStepIsSet() {
        FlowStepper s = new FlowStepper(List.of("A", "B", "C"), 2);
        assertEquals(2, s.getCurrentStep());
    }

    @Test
    void stepper_listAndStepConstructor_stepsAttributeIsSet() {
        FlowStepper s = new FlowStepper(List.of("A", "B"), 1);
        assertEquals("[\"A\",\"B\"]", s.getElement().getAttribute("steps"));
    }

    // =========================================================================
    // FlowStepper — setSteps
    // =========================================================================

    @Test
    void setSteps_emptyList_setsEmptyJsonArray() {
        FlowStepper s = new FlowStepper();
        s.setSteps(List.of());
        assertEquals("[]", s.getElement().getAttribute("steps"));
    }

    @Test
    void setSteps_singleItem_setsCorrectJson() {
        FlowStepper s = new FlowStepper();
        s.setSteps("OnlyStep");
        assertEquals("[\"OnlyStep\"]", s.getElement().getAttribute("steps"));
    }

    @Test
    void setSteps_multipleItems_setsCorrectJson() {
        FlowStepper s = new FlowStepper();
        s.setSteps("One", "Two", "Three");
        assertEquals("[\"One\",\"Two\",\"Three\"]", s.getElement().getAttribute("steps"));
    }

    @Test
    void setSteps_labelWithDoubleQuote_isEscaped() {
        FlowStepper s = new FlowStepper();
        s.setSteps(List.of("Say \"Hello\""));
        assertEquals("[\"Say \\\"Hello\\\"\"]", s.getElement().getAttribute("steps"));
    }

    @Test
    void setSteps_labelWithBackslash_isEscaped() {
        FlowStepper s = new FlowStepper();
        s.setSteps(List.of("C:\\Users"));
        assertEquals("[\"C:\\\\Users\"]", s.getElement().getAttribute("steps"));
    }

    @Test
    void setSteps_varargs_delegatesToList() {
        FlowStepper s = new FlowStepper();
        s.setSteps("Alpha", "Beta");
        assertEquals("[\"Alpha\",\"Beta\"]", s.getElement().getAttribute("steps"));
    }

    // =========================================================================
    // FlowStepper — currentStep
    // =========================================================================

    @Test
    void setCurrentStep_zero_setsAttributeToZero() {
        FlowStepper s = new FlowStepper(List.of("A", "B", "C"));
        s.setCurrentStep(0);
        assertEquals("0", s.getElement().getAttribute("current-step"));
        assertEquals(0, s.getCurrentStep());
    }

    @Test
    void setCurrentStep_nonZero_setsCorrectAttribute() {
        FlowStepper s = new FlowStepper(List.of("A", "B", "C"));
        s.setCurrentStep(2);
        assertEquals("2", s.getElement().getAttribute("current-step"));
        assertEquals(2, s.getCurrentStep());
    }

    @Test
    void getCurrentStep_whenAttributeAbsent_returnsZero() {
        FlowStepper s = new FlowStepper();
        s.getElement().removeAttribute("current-step");
        assertEquals(0, s.getCurrentStep());
    }

    // =========================================================================
    // FlowStepper — orientation
    // =========================================================================

    @Test
    void setOrientation_horizontal_setsAttributeToHorizontal() {
        FlowStepper s = new FlowStepper();
        s.setOrientation(FlowStepper.Orientation.HORIZONTAL);
        assertEquals("horizontal", s.getElement().getAttribute("orientation"));
    }

    @Test
    void setOrientation_vertical_setsAttributeToVertical() {
        FlowStepper s = new FlowStepper();
        s.setOrientation(FlowStepper.Orientation.VERTICAL);
        assertEquals("vertical", s.getElement().getAttribute("orientation"));
    }

    @Test
    void getOrientation_afterSetVertical_returnsVertical() {
        FlowStepper s = new FlowStepper();
        s.setOrientation(FlowStepper.Orientation.VERTICAL);
        assertEquals(FlowStepper.Orientation.VERTICAL, s.getOrientation());
    }

    @Test
    void getOrientation_afterSetHorizontal_returnsHorizontal() {
        FlowStepper s = new FlowStepper();
        s.setOrientation(FlowStepper.Orientation.VERTICAL);
        s.setOrientation(FlowStepper.Orientation.HORIZONTAL);
        assertEquals(FlowStepper.Orientation.HORIZONTAL, s.getOrientation());
    }

    @Test
    void setOrientation_null_defaultsToHorizontal() {
        FlowStepper s = new FlowStepper();
        s.setOrientation(null);
        assertEquals("horizontal", s.getElement().getAttribute("orientation"));
    }

    @Test
    void getOrientation_whenAttributeAbsent_returnsHorizontal() {
        FlowStepper s = new FlowStepper();
        s.getElement().removeAttribute("orientation");
        assertEquals(FlowStepper.Orientation.HORIZONTAL, s.getOrientation());
    }

    // =========================================================================
    // FlowStepper — variant
    // =========================================================================

    @Test
    void setVariant_default_setsAttributeToDefault() {
        FlowStepper s = new FlowStepper();
        s.setVariant(FlowStepper.Variant.DEFAULT);
        assertEquals("default", s.getElement().getAttribute("variant"));
    }

    @Test
    void setVariant_numbered_setsAttributeToNumbered() {
        FlowStepper s = new FlowStepper();
        s.setVariant(FlowStepper.Variant.NUMBERED);
        assertEquals("numbered", s.getElement().getAttribute("variant"));
    }

    @Test
    void setVariant_dot_setsAttributeToDot() {
        FlowStepper s = new FlowStepper();
        s.setVariant(FlowStepper.Variant.DOT);
        assertEquals("dot", s.getElement().getAttribute("variant"));
    }

    @Test
    void getVariant_afterSetNumbered_returnsNumbered() {
        FlowStepper s = new FlowStepper();
        s.setVariant(FlowStepper.Variant.NUMBERED);
        assertEquals(FlowStepper.Variant.NUMBERED, s.getVariant());
    }

    @Test
    void getVariant_afterSetDot_returnsDot() {
        FlowStepper s = new FlowStepper();
        s.setVariant(FlowStepper.Variant.DOT);
        assertEquals(FlowStepper.Variant.DOT, s.getVariant());
    }

    @Test
    void setVariant_null_defaultsToDefault() {
        FlowStepper s = new FlowStepper();
        s.setVariant(null);
        assertEquals("default", s.getElement().getAttribute("variant"));
    }

    @Test
    void getVariant_whenAttributeAbsent_returnsDefault() {
        FlowStepper s = new FlowStepper();
        s.getElement().removeAttribute("variant");
        assertEquals(FlowStepper.Variant.DEFAULT, s.getVariant());
    }

    // =========================================================================
    // FlowStepper — enum values
    // =========================================================================

    @Test
    void orientation_enumHasTwoValues() {
        assertEquals(2, FlowStepper.Orientation.values().length);
    }

    @Test
    void variant_enumHasThreeValues() {
        assertEquals(3, FlowStepper.Variant.values().length);
    }

    @Test
    void orientation_horizontal_nameIsHORIZONTAL() {
        assertEquals("HORIZONTAL", FlowStepper.Orientation.HORIZONTAL.name());
    }

    @Test
    void orientation_vertical_nameIsVERTICAL() {
        assertEquals("VERTICAL", FlowStepper.Orientation.VERTICAL.name());
    }

    @Test
    void variant_dot_nameIsDOT() {
        assertEquals("DOT", FlowStepper.Variant.DOT.name());
    }

    // =========================================================================
    // FlowStepper — event listener registration (no UI needed)
    // =========================================================================

    @Test
    void addStepChangedListener_doesNotThrow() {
        FlowStepper s = new FlowStepper(List.of("A", "B"));
        assertDoesNotThrow(() ->
                s.addStepChangedListener(e -> { /* no-op */ }));
    }

    @Test
    void addStepCompleteListener_doesNotThrow() {
        FlowStepper s = new FlowStepper(List.of("A", "B"));
        assertDoesNotThrow(() ->
                s.addStepCompleteListener(e -> {}));
    }

    @Test
    void addStepErrorListener_doesNotThrow() {
        FlowStepper s = new FlowStepper(List.of("A", "B"));
        assertDoesNotThrow(() ->
                s.addStepErrorListener(e -> {}));
    }

    // =========================================================================
    // FlowStepper.builder() — factory method
    // =========================================================================

    @Test
    void builder_staticFactory_returnsNonNull() {
        assertNotNull(FlowStepper.builder());
    }

    @Test
    void builder_create_returnsNonNull() {
        assertNotNull(StepperBuilder.create());
    }

    // =========================================================================
    // StepperBuilder — fluent API
    // =========================================================================

    @Test
    void builder_steps_list_setsAttribute() {
        FlowStepper s = FlowStepper.builder()
                .steps(List.of("Account", "Details"))
                .build();
        assertEquals("[\"Account\",\"Details\"]", s.getElement().getAttribute("steps"));
    }

    @Test
    void builder_steps_varargs_setsAttribute() {
        FlowStepper s = FlowStepper.builder()
                .steps("X", "Y", "Z")
                .build();
        assertEquals("[\"X\",\"Y\",\"Z\"]", s.getElement().getAttribute("steps"));
    }

    @Test
    void builder_currentStep_setsAttribute() {
        FlowStepper s = FlowStepper.builder()
                .steps("A", "B", "C")
                .currentStep(2)
                .build();
        assertEquals(2, s.getCurrentStep());
    }

    @Test
    void builder_orientation_vertical_setsAttribute() {
        FlowStepper s = FlowStepper.builder()
                .orientation(FlowStepper.Orientation.VERTICAL)
                .build();
        assertEquals(FlowStepper.Orientation.VERTICAL, s.getOrientation());
    }

    @Test
    void builder_variant_dot_setsAttribute() {
        FlowStepper s = FlowStepper.builder()
                .variant(FlowStepper.Variant.DOT)
                .build();
        assertEquals(FlowStepper.Variant.DOT, s.getVariant());
    }

    @Test
    void builder_variant_numbered_setsAttribute() {
        FlowStepper s = FlowStepper.builder()
                .variant(FlowStepper.Variant.NUMBERED)
                .build();
        assertEquals(FlowStepper.Variant.NUMBERED, s.getVariant());
    }

    @Test
    void builder_id_setsIdAttribute() {
        FlowStepper s = FlowStepper.builder()
                .id("my-stepper")
                .build();
        assertTrue(s.getId().isPresent());
        assertEquals("my-stepper", s.getId().get());
    }

    @Test
    void builder_width_setsWidth() {
        FlowStepper s = FlowStepper.builder()
                .width("600px")
                .build();
        assertEquals("600px", s.getWidth());
    }

    @Test
    void builder_fullWidth_setsWidth() {
        FlowStepper s = FlowStepper.builder()
                .fullWidth()
                .build();
        assertEquals("100%", s.getWidth());
    }

    @Test
    void builder_styleName_addsClass() {
        FlowStepper s = FlowStepper.builder()
                .styleName("stepper--compact")
                .build();
        assertTrue(s.getClassNames().contains("stepper--compact"));
    }

    @Test
    void builder_visible_false_hidesComponent() {
        FlowStepper s = FlowStepper.builder()
                .visible(false)
                .build();
        assertFalse(s.isVisible());
    }

    @Test
    void builder_enabled_false_disablesComponent() {
        FlowStepper s = FlowStepper.builder()
                .enabled(false)
                .build();
        assertFalse(s.isEnabled());
    }

    @Test
    void builder_onStepChanged_doesNotThrow() {
        assertDoesNotThrow(() -> FlowStepper.builder()
                .steps("A", "B")
                .onStepChanged(e -> {})
                .build());
    }

    @Test
    void builder_onStepComplete_doesNotThrow() {
        assertDoesNotThrow(() -> FlowStepper.builder()
                .steps("A", "B")
                .onStepComplete(e -> {})
                .build());
    }

    @Test
    void builder_onStepError_doesNotThrow() {
        assertDoesNotThrow(() -> FlowStepper.builder()
                .steps("A", "B")
                .onStepError(e -> {})
                .build());
    }

    @Test
    void builder_fluent_chainReturnsSameBuilder() {
        StepperBuilder b = FlowStepper.builder();
        assertSame(b, b.steps("A", "B"));
        assertSame(b, b.currentStep(0));
        assertSame(b, b.orientation(FlowStepper.Orientation.HORIZONTAL));
        assertSame(b, b.variant(FlowStepper.Variant.DEFAULT));
    }

    @Test
    void builder_fullChain_buildsCorrectComponent() {
        FlowStepper s = FlowStepper.builder()
                .steps("Account", "Details", "Review", "Confirm")
                .currentStep(1)
                .orientation(FlowStepper.Orientation.VERTICAL)
                .variant(FlowStepper.Variant.NUMBERED)
                .id("full-stepper")
                .width("400px")
                .styleName("my-stepper")
                .build();

        assertEquals("[\"Account\",\"Details\",\"Review\",\"Confirm\"]",
                s.getElement().getAttribute("steps"));
        assertEquals(1, s.getCurrentStep());
        assertEquals(FlowStepper.Orientation.VERTICAL, s.getOrientation());
        assertEquals(FlowStepper.Variant.NUMBERED, s.getVariant());
        assertTrue(s.getId().isPresent());
        assertEquals("full-stepper", s.getId().get());
        assertEquals("400px", s.getWidth());
        assertTrue(s.getClassNames().contains("my-stepper"));
    }

    // =========================================================================
    // StepperConfigurator — configure(existing)
    // =========================================================================

    @Test
    void configurator_configure_returnsNonNull() {
        FlowStepper s = new FlowStepper();
        assertNotNull(StepperConfigurator.configure(s));
    }

    @Test
    void configurator_configure_changesOrientation() {
        FlowStepper s = new FlowStepper();
        StepperConfigurator.configure(s)
                .orientation(FlowStepper.Orientation.VERTICAL);
        assertEquals(FlowStepper.Orientation.VERTICAL, s.getOrientation());
    }

    @Test
    void configurator_configure_changesVariant() {
        FlowStepper s = new FlowStepper();
        StepperConfigurator.configure(s)
                .variant(FlowStepper.Variant.DOT);
        assertEquals(FlowStepper.Variant.DOT, s.getVariant());
    }

    @Test
    void configurator_configure_setsSteps() {
        FlowStepper s = new FlowStepper();
        StepperConfigurator.configure(s)
                .steps("P", "Q", "R");
        assertEquals("[\"P\",\"Q\",\"R\"]", s.getElement().getAttribute("steps"));
    }

    @Test
    void configurator_staticFactory_fromComponent_returnsNonNull() {
        FlowStepper s = new FlowStepper();
        assertNotNull(FlowStepper.configure(s));
    }

    @Test
    void configurator_staticFactory_fromComponent_changesCurrentStep() {
        FlowStepper s = new FlowStepper(List.of("A", "B", "C"));
        FlowStepper.configure(s).currentStep(2);
        assertEquals(2, s.getCurrentStep());
    }

    // =========================================================================
    // Components entry-point integration
    // =========================================================================

    @Test
    void components_stepper_returnsNonNull() {
        assertNotNull(Components.stepper());
    }

    @Test
    void components_stepper_buildsComponent() {
        FlowStepper s = Components.stepper()
                .steps("One", "Two")
                .build();
        assertNotNull(s);
        assertEquals("[\"One\",\"Two\"]", s.getElement().getAttribute("steps"));
    }

    @Test
    void components_configure_stepper_returnsNonNull() {
        FlowStepper s = new FlowStepper();
        assertNotNull(Components.configure(s));
    }

    // =========================================================================
    // Edge cases
    // =========================================================================

    @Test
    void setSteps_singleItemWithBothSpecialChars_isCorrectlyEscaped() {
        FlowStepper s = new FlowStepper();
        // label: backslash then double-quote: \"
        s.setSteps(List.of("path\\\"value"));
        String attr = s.getElement().getAttribute("steps");
        // Expected JSON: ["path\\\"value"]
        assertEquals("[\"path\\\\\\\"value\"]", attr);
    }

    @Test
    void multipleSetSteps_callsOverwritePreviousAttribute() {
        FlowStepper s = new FlowStepper(List.of("A", "B"));
        s.setSteps("X", "Y", "Z");
        assertEquals("[\"X\",\"Y\",\"Z\"]", s.getElement().getAttribute("steps"));
    }

    @Test
    void setCurrentStep_overwritesPreviousValue() {
        FlowStepper s = new FlowStepper(List.of("A", "B", "C"), 0);
        s.setCurrentStep(2);
        assertEquals(2, s.getCurrentStep());
    }

    @Test
    void setVariant_overwritesPreviousVariant() {
        FlowStepper s = new FlowStepper();
        s.setVariant(FlowStepper.Variant.DOT);
        s.setVariant(FlowStepper.Variant.NUMBERED);
        assertEquals(FlowStepper.Variant.NUMBERED, s.getVariant());
    }

    @Test
    void tag_isFlowStepper() {
        FlowStepper s = new FlowStepper();
        assertEquals("flow-stepper", s.getElement().getTag());
    }
}


