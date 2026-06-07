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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultStepperConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.FlowStepper;
import com.vaadin.flow.component.ComponentEventListener;

import java.util.List;

/**
 * Fluent configurator for {@link FlowStepper} components.
 *
 * <p>Covers all stepper-specific properties plus the standard Holon Platform
 * component properties inherited from {@link ComponentConfigurator},
 * {@link HasSizeConfigurator}, {@link HasStyleConfigurator} and
 * {@link HasEnabledConfigurator}.</p>
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 *
 * @see StepperBuilder
 * @see FlowStepper
 */
public interface StepperConfigurator<C extends StepperConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>,
        HasEnabledConfigurator<C> {

    // -----------------------------------------------------------------------
    // Steps
    // -----------------------------------------------------------------------

    /**
     * Sets the ordered list of step labels.
     *
     * @param steps step labels (not null)
     * @return this configurator for chaining
     */
    C steps(List<String> steps);

    /**
     * Sets the ordered step labels from a varargs array.
     *
     * @param steps step labels (not null)
     * @return this configurator for chaining
     */
    C steps(String... steps);

    /**
     * Sets the ordered step labels from a varargs array of {@link Localizable} descriptors.
     * Each label is resolved at the time of this call using the current locale.
     *
     * @param steps localizable step labels (not null)
     * @return this configurator for chaining
     */
    C steps(Localizable... steps);

    // -----------------------------------------------------------------------
    // Current step
    // -----------------------------------------------------------------------

    /**
     * Sets the initially active step (0-based index).
     *
     * @param step 0-based step index
     * @return this configurator for chaining
     */
    C currentStep(int step);

    // -----------------------------------------------------------------------
    // Orientation
    // -----------------------------------------------------------------------

    /**
     * Sets the layout orientation.
     *
     * @param orientation the desired orientation (not null)
     * @return this configurator for chaining
     */
    C orientation(FlowStepper.Orientation orientation);

    // -----------------------------------------------------------------------
    // Variant
    // -----------------------------------------------------------------------

    /**
     * Sets the visual variant of the step indicator node.
     *
     * @param variant the desired variant (not null)
     * @return this configurator for chaining
     */
    C variant(FlowStepper.Variant variant);

    // -----------------------------------------------------------------------
    // Click navigation
    // -----------------------------------------------------------------------

    /**
     * Sets which steps respond to a user click or keyboard-activate gesture.
     *
     * <ul>
     *   <li>{@link FlowStepper.ClickNavigation#COMPLETED} <em>(default)</em> — only
     *       completed and error steps are clickable (back-nav / retry).</li>
     *   <li>{@link FlowStepper.ClickNavigation#ALL} — every step is directly
     *       jumpable (free-nav wizard).</li>
     *   <li>{@link FlowStepper.ClickNavigation#NONE} — no step is clickable; use
     *       external Next / Back buttons to drive navigation.</li>
     * </ul>
     *
     * @param mode the desired navigation mode (not null)
     * @return this configurator for chaining
     */
    C clickNavigation(FlowStepper.ClickNavigation mode);

    // -----------------------------------------------------------------------
    // Event listeners
    // -----------------------------------------------------------------------

    /**
     * Adds a listener that is notified whenever the active step changes.
     *
     * @param listener the listener (not null)
     * @return this configurator for chaining
     */
    C onStepChanged(ComponentEventListener<FlowStepper.StepChangedEvent> listener);

    /**
     * Adds a listener that is notified when a step is completed via
     * {@link FlowStepper#completeStep()}.
     *
     * @param listener the listener (not null)
     * @return this configurator for chaining
     */
    C onStepComplete(ComponentEventListener<FlowStepper.StepCompleteEvent> listener);

    /**
     * Adds a listener that is notified when a step is marked as failed via
     * {@link FlowStepper#markStepError(int)}.
     *
     * @param listener the listener (not null)
     * @return this configurator for chaining
     */
    C onStepError(ComponentEventListener<FlowStepper.StepErrorEvent> listener);

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Returns a configurator for an <strong>existing</strong> {@link FlowStepper} instance.
     *
     * <pre>{@code
     * StepperConfigurator.configure(myStepper)
     *     .orientation(FlowStepper.Orientation.VERTICAL)
     *     .variant(FlowStepper.Variant.DOT);
     * }</pre>
     *
     * @param stepper the stepper to configure (not null)
     * @return a {@link BaseStepperConfigurator}
     */
    static BaseStepperConfigurator configure(FlowStepper stepper) {
        return new DefaultStepperConfigurator(stepper);
    }

    /**
     * Base configurator type returned by {@link #configure(FlowStepper)}.
     */
    interface BaseStepperConfigurator extends StepperConfigurator<BaseStepperConfigurator> {
    }
}
