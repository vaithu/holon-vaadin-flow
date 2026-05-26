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
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.StepperConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.FlowStepper;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.List;
import java.util.Optional;

/**
 * Base {@link StepperConfigurator} implementation.
 *
 * <p>Delegates all configuration calls to the wrapped {@link FlowStepper}
 * component and exposes the standard Holon Platform component hooks
 * ({@code id}, {@code visible}, {@code styleName}, {@code width}, {@code enabled})
 * via {@link AbstractComponentConfigurator}.</p>
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractStepperConfigurator<C extends StepperConfigurator<C>>
        extends AbstractComponentConfigurator<FlowStepper, C>
        implements StepperConfigurator<C> {

    public AbstractStepperConfigurator(FlowStepper component) {
        super(component);
    }

    // -----------------------------------------------------------------------
    // StepperConfigurator implementation
    // -----------------------------------------------------------------------

    @Override
    public C steps(List<String> steps) {
        getComponent().setSteps(steps);
        return getConfigurator();
    }

    @Override
    public C steps(String... steps) {
        getComponent().setSteps(steps);
        return getConfigurator();
    }

    @Override
    public C steps(Localizable... steps) {
        getComponent().setSteps(steps);
        return getConfigurator();
    }

    @Override
    public C currentStep(int step) {
        getComponent().setCurrentStep(step);
        return getConfigurator();
    }

    @Override
    public C orientation(FlowStepper.Orientation orientation) {
        getComponent().setOrientation(orientation);
        return getConfigurator();
    }

    @Override
    public C variant(FlowStepper.Variant variant) {
        getComponent().setVariant(variant);
        return getConfigurator();
    }

    @Override
    public C clickNavigation(FlowStepper.ClickNavigation mode) {
        getComponent().setClickNavigation(mode);
        return getConfigurator();
    }

    @Override
    public C onStepChanged(ComponentEventListener<FlowStepper.StepChangedEvent> listener) {
        getComponent().addStepChangedListener(listener);
        return getConfigurator();
    }

    @Override
    public C onStepComplete(ComponentEventListener<FlowStepper.StepCompleteEvent> listener) {
        getComponent().addStepCompleteListener(listener);
        return getConfigurator();
    }

    @Override
    public C onStepError(ComponentEventListener<FlowStepper.StepErrorEvent> listener) {
        getComponent().addStepErrorListener(listener);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // AbstractComponentConfigurator hooks
    // -----------------------------------------------------------------------

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }
}
