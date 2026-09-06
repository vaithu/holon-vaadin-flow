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
package com.holonplatform.vaadin.flow.vaadinplus.components;

import java.io.Serial;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.StepperBuilder;
import com.holonplatform.vaadin.flow.components.builders.StepperConfigurator;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.component.HasEnabled;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A multi-step progress indicator that wraps the {@code <flow-stepper>}
 * Shadow-DOM web component.
 *
 * <p>The component communicates with its JavaScript counterpart exclusively
 * through HTML attributes (for observable state) and
 * {@link com.vaadin.flow.dom.Element#callJsFunction} calls (for server-driven
 * navigation). No inline styles are produced by Java code.</p>
 *
 * <h3>Orientations</h3>
 * <ul>
 *   <li>{@link Orientation#HORIZONTAL} (default)  steps laid out in a row.</li>
 *   <li>{@link Orientation#VERTICAL}  steps stacked in a column.</li>
 * </ul>
 *
 * <h3>Variants</h3>
 * <ul>
 *   <li>{@link Variant#DEFAULT}  step number or icon in a circle.</li>
 *   <li>{@link Variant#NUMBERED}  always shows the step number.</li>
 *   <li>{@link Variant#DOT}  minimal dot instead of a labelled circle.</li>
 * </ul>
 *
 * <h3>Fluent builder (recommended)</h3>
 * <pre>{@code
 * FlowStepper stepper = FlowStepper.builder()
 *     .steps("Account", "Details", "Review", "Confirm")
 *     .currentStep(1)
 *     .orientation(FlowStepper.Orientation.VERTICAL)
 *     .onStepChanged(e -> log.info("Now on step {}", e.getStep()))
 *     .build();
 * }</pre>
 *
 * @see StepperBuilder
 * @see StepperConfigurator
 */
@com.vaadin.flow.component.Tag("flow-stepper")
@JsModule("./stepper-component.js")
@StyleSheet("context://stepper.css")
public class FlowStepper extends Component implements HasSize, HasEnabled {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Stored localizable steps; re-resolved when locale changes. */
    private Localizable[] localizableSteps;

    /** Stored localizable aria-label; re-resolved on locale change. */
    private Localizable ariaLabelLocalizable;

    // -----------------------------------------------------------------------
    // Enums
    // -----------------------------------------------------------------------

    /** Layout orientation of the stepper. */
    public enum Orientation {
        /** Steps are displayed left-to-right in a single row. */
        HORIZONTAL,
        /** Steps are stacked top-to-bottom in a column. */
        VERTICAL
    }

    /** Visual style of the step indicator node. */
    public enum Variant {
        /** Step number shown in a circle; icon replaces number when completed/errored. */
        DEFAULT,
        /** Step number always visible. */
        NUMBERED,
        /** Minimal dot indicator  no number or icon. */
        DOT
    }

    /**
     * Controls which steps respond to a click/keyboard-activate gesture.
     *
     * <p>The value is written to the {@code click-nav} HTML attribute and read by the
     * web component on every render. It can be changed at runtime  call
     * {@link #setClickNavigation(ClickNavigation)} at any point and the component
     * re-evaluates on the next attribute change.</p>
     *
     * <h4>When to use each mode</h4>
     * <ul>
     *   <li>{@link #COMPLETED} <em>(default)</em>  linear wizard where the user must
     *       complete steps in order, but may go back to a finished step to review or
     *       change answers. Pending / active steps are non-interactive.</li>
     *   <li>{@link #ALL}  free-navigation form where all steps are reachable at any
     *       time (e.g. a settings screen with multiple tabs displayed as steps).</li>
     *   <li>{@link #NONE}  purely decorative progress indicator; no step is clickable.
     *       Use this when navigation is driven only by Next / Back buttons.</li>
     * </ul>
     */
    public enum ClickNavigation {
        /**
         * Only already-visited (completed) steps and steps in error state are
         * clickable. This is the default: the user can jump back to revise previous
         * answers or retry a failed step, but cannot skip ahead.
         */
        COMPLETED,
        /**
         * Every step is directly clickable regardless of its state. Suitable for
         * non-linear wizards or tabbed forms where free navigation is intended.
         */
        ALL,
        /**
         * No step is clickable. The stepper acts as a pure read-only progress
         * display; navigation must be driven by external controls (Next/Back buttons).
         */
        NONE
    }

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /** Creates a stepper with no steps and default settings. */
    public FlowStepper() {
        setSteps(List.of());
        getElement().setAttribute("role", "group");
    }

    /**
     * Creates a stepper with the given step labels starting at step 0.
     *
     * @param steps ordered list of step labels (not null)
     */
    public FlowStepper(List<String> steps) {
        setSteps(steps);
    }

    /**
     * Creates a stepper with the given step labels and an explicit initial step.
     *
     * @param steps       ordered list of step labels (not null)
     * @param initialStep 0-based index of the initially active step
     */
    public FlowStepper(List<String> steps, int initialStep) {
        setSteps(steps);
        setCurrentStep(initialStep);
    }

    // -----------------------------------------------------------------------
    // Steps
    // -----------------------------------------------------------------------

    /**
     * Sets the step labels from a list.
     *
     * <p>Labels are JSON-encoded and written to the {@code steps} attribute so that
     * the web component can parse them. Special characters ({@code "} and
     * {@code \}) are properly escaped.</p>
     *
     * @param steps ordered list of step labels (not null; empty list clears all steps)
     */
    public void setSteps(List<String> steps) {
        String json = "[" + steps.stream()
                .map(s -> "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"")
                .collect(Collectors.joining(",")) + "]";
        getElement().setAttribute("steps", json);
    }

    /**
     * Sets the step labels from a varargs array.
     *
     * @param steps step labels (not null)
     */
    public void setSteps(String... steps) {
        setSteps(Arrays.asList(steps));
    }

    /**
     * Sets the step labels from a varargs array of {@link Localizable} descriptors.
     * Each label is resolved using {@link LocalizationProvider#localize(Localizable)},
     * falling back to the descriptor's default message. Labels are stored and
     * re-resolved automatically on locale change.
     *
     * @param steps localizable step labels (not null)
     */
    public void setSteps(Localizable... steps) {
        this.localizableSteps = steps;
        setSteps(Arrays.stream(steps)
                .map(l -> LocalizationProvider.localize(l)
                        .orElseGet(() -> l.getMessage() != null ? l.getMessage() : ""))
                .collect(Collectors.toList()));
    }

    // -----------------------------------------------------------------------
    // A11Y  accessible label
    // -----------------------------------------------------------------------

    /**
     * Sets the accessible name for this stepper group.
     *
     * @param label the ARIA label (not null)
     */
    public void setAriaLabel(String label) {
        this.ariaLabelLocalizable = null;
        if (label != null && !label.isBlank()) {
            getElement().setAttribute("aria-label", label);
        }
    }

    /**
     * Sets the accessible name from a {@link Localizable} descriptor.
     * Re-resolved on each locale change.
     *
     * @param label the localizable accessible name (not null)
     */
    public void setAriaLabel(Localizable label) {
        this.ariaLabelLocalizable = label;
        applyAriaLabel();
    }
    private void applyAriaLabel() {
        if (ariaLabelLocalizable == null) return;
        String resolved = LocalizationProvider.localize(ariaLabelLocalizable)
                .orElseGet(() -> ariaLabelLocalizable.getMessage() != null
                        ? ariaLabelLocalizable.getMessage() : "");
        if (!resolved.isBlank()) {
            getElement().setAttribute("aria-label", resolved);
        }
    }

    // -----------------------------------------------------------------------
    // Current step
    // -----------------------------------------------------------------------

    /**
     * Sets the active step (0-based index).
     *
     * @param step the step index to activate
     */
    public void setCurrentStep(int step) {
        getElement().setAttribute("current-step", String.valueOf(step));
    }

    /**
     * Returns the currently active step index (0-based).
     *
     * @return active step index; {@code 0} if the attribute is absent
     */
    public int getCurrentStep() {
        String val = getElement().getAttribute("current-step");
        return val != null ? Integer.parseInt(val) : 0;
    }

    // -----------------------------------------------------------------------
    // Orientation
    // -----------------------------------------------------------------------

    /**
     * Sets the layout orientation of the stepper.
     *
     * @param orientation the desired orientation (not null; defaults to
     *                    {@link Orientation#HORIZONTAL} when null is passed)
     */
    public void setOrientation(Orientation orientation) {
        if (orientation == null) orientation = Orientation.HORIZONTAL;
        getElement().setAttribute("orientation",
                orientation == Orientation.VERTICAL ? "vertical" : "horizontal");
    }

    /**
     * Returns the current layout orientation.
     *
     * @return {@link Orientation#VERTICAL} if the {@code orientation} attribute
     *         is {@code "vertical"}, {@link Orientation#HORIZONTAL} otherwise
     */
    public Orientation getOrientation() {
        return "vertical".equals(getElement().getAttribute("orientation"))
                ? Orientation.VERTICAL
                : Orientation.HORIZONTAL;
    }

    // -----------------------------------------------------------------------
    // Variant
    // -----------------------------------------------------------------------

    /**
     * Sets the visual variant of the step indicator node.
     *
     * @param variant the desired variant (not null)
     */
    public void setVariant(Variant variant) {
        if (variant == null) variant = Variant.DEFAULT;
        getElement().setAttribute("variant", variant.name().toLowerCase());
    }

    /**
     * Returns the current visual variant.
     *
     * @return the active {@link Variant}; defaults to {@link Variant#DEFAULT}
     *         when the attribute is absent
     */
    public Variant getVariant() {
        String val = getElement().getAttribute("variant");
        if (val == null) return Variant.DEFAULT;
        return switch (val) {
            case "numbered" -> Variant.NUMBERED;
            case "dot"      -> Variant.DOT;
            default         -> Variant.DEFAULT;
        };
    }

    // -----------------------------------------------------------------------
    // Click navigation
    // -----------------------------------------------------------------------

    /**
     * Sets which steps respond to a user click or keyboard-activate gesture.
     *
     * <p>The value is forwarded to the {@code click-nav} HTML attribute and picked up
     * by the web component on the next render cycle. It may be changed at runtime.</p>
     *
     * <p>Defaults to {@link ClickNavigation#COMPLETED} when not explicitly set.</p>
     *
     * @param mode the desired navigation mode (not null)
     * @see ClickNavigation
     */
    public void setClickNavigation(ClickNavigation mode) {
        if (mode == null) mode = ClickNavigation.COMPLETED;
        getElement().setAttribute("click-nav", mode.name().toLowerCase());
    }

    /**
     * Returns the current click-navigation mode.
     *
     * @return the active {@link ClickNavigation}; defaults to {@link ClickNavigation#COMPLETED}
     *         when the attribute is absent
     */
    public ClickNavigation getClickNavigation() {
        String val = getElement().getAttribute("click-nav");
        if (val == null) return ClickNavigation.COMPLETED;
        return switch (val) {
            case "all"  -> ClickNavigation.ALL;
            case "none" -> ClickNavigation.NONE;
            default     -> ClickNavigation.COMPLETED;
        };
    }

    // -----------------------------------------------------------------------
    // Server-side navigation
    // -----------------------------------------------------------------------

    /**
     * Navigates to a specific step from the server side.
     *
     * @param index 0-based step index
     */
    public void goToStep(int index) {
        getElement().callJsFunction("goToStep", index);
    }

    /** Advances to the next step from the server side. */
    public void nextStep() {
        getElement().callJsFunction("nextStep");
    }

    /** Goes back one step from the server side. */
    public void prevStep() {
        getElement().callJsFunction("prevStep");
    }

    /** Marks the current step as complete and advances to the next step. */
    public void completeStep() {
        getElement().callJsFunction("completeStep");
    }

    /**
     * Marks a step as failed (shows an error icon on the node).
     *
     * @param index 0-based step index to fail
     */
    public void markStepError(int index) {
        getElement().callJsFunction("markStepError", index);
    }

    /**
     * Clears the error state on a specific step.
     *
     * @param index 0-based step index
     */
    public void clearStepError(int index) {
        getElement().callJsFunction("clearStepError", index);
    }

    /** Clears all error states across all steps. */
    public void clearAllErrors() {
        getElement().callJsFunction("clearAllErrors");
    }

    // -----------------------------------------------------------------------
    // Events
    // -----------------------------------------------------------------------

    /**
     * Fired whenever the active step changes (user click or programmatic navigation).
     */
    @DomEvent("step-changed")
    public static class StepChangedEvent extends ComponentEvent<FlowStepper> {
        private final int    step;
        private final String label;

        public StepChangedEvent(
                FlowStepper source,
                boolean fromClient,
                @EventData("event.detail.step")  int    step,
                @EventData("event.detail.label") String label) {
            super(source, fromClient);
            this.step  = step;
            this.label = label;
        }

        /** Returns the 0-based index of the now-active step. */
        public int getStep() { return step; }

        /** Returns the label of the now-active step. */
        public String getLabel() { return label; }
    }

    /**
     * Registers a listener for {@link StepChangedEvent}.
     *
     * @param listener the listener (not null)
     * @return a {@link Registration} to remove the listener
     */
    public Registration addStepChangedListener(
            ComponentEventListener<StepChangedEvent> listener) {
        return addListener(StepChangedEvent.class, listener);
    }

    /**
     * Fired when a step is explicitly completed via {@link #completeStep()}.
     */
    @DomEvent("step-complete")
    public static class StepCompleteEvent extends ComponentEvent<FlowStepper> {
        private final int step;

        public StepCompleteEvent(
                FlowStepper source,
                boolean fromClient,
                @EventData("event.detail.step") int step) {
            super(source, fromClient);
            this.step = step;
        }

        /** Returns the 0-based index of the completed step. */
        public int getStep() { return step; }
    }

    /**
     * Registers a listener for {@link StepCompleteEvent}.
     *
     * @param listener the listener (not null)
     * @return a {@link Registration} to remove the listener
     */
    public Registration addStepCompleteListener(
            ComponentEventListener<StepCompleteEvent> listener) {
        return addListener(StepCompleteEvent.class, listener);
    }

    /**
     * Fired when a step is marked as failed via {@link #markStepError(int)}.
     */
    @DomEvent("step-error")
    public static class StepErrorEvent extends ComponentEvent<FlowStepper> {
        private final int    step;
        private final String label;

        public StepErrorEvent(
                FlowStepper source,
                boolean fromClient,
                @EventData("event.detail.step")  int    step,
                @EventData("event.detail.label") String label) {
            super(source, fromClient);
            this.step  = step;
            this.label = label;
        }

        /** Returns the 0-based index of the failed step. */
        public int getStep() { return step; }

        /** Returns the label of the failed step. */
        public String getLabel() { return label; }
    }

    /**
     * Registers a listener for {@link StepErrorEvent}.
     *
     * @param listener the listener (not null)
     * @return a {@link Registration} to remove the listener
     */
    public Registration addStepErrorListener(
            ComponentEventListener<StepErrorEvent> listener) {
        return addListener(StepErrorEvent.class, listener);
    }

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /**
     * Returns a new fluent {@link StepperBuilder}.
     *
     * <pre>{@code
     * FlowStepper stepper = FlowStepper.builder()
     *     .steps("Account", "Details", "Review")
     *     .currentStep(0)
     *     .orientation(FlowStepper.Orientation.HORIZONTAL)
     *     .build();
     * }</pre>
     *
     * @return a new {@link StepperBuilder}
     */
    public static StepperBuilder builder() {
        return StepperBuilder.create();
    }

    /**
     * Returns a fluent configurator for an <strong>existing</strong>
     * {@link FlowStepper} instance.
     *
     * @param stepper the stepper to configure (not null)
     * @return a {@link StepperConfigurator.BaseStepperConfigurator}
     */
    public static StepperConfigurator.BaseStepperConfigurator configure(FlowStepper stepper) {
        return StepperConfigurator.configure(stepper);
    }
}
