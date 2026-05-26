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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultStepperBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.FlowStepper;

/**
 * Fluent builder to create and configure {@link FlowStepper} components.
 *
 * <p><strong>Usage examples:</strong></p>
 *
 * <p>Basic horizontal stepper:</p>
 * <pre>{@code
 * FlowStepper stepper = FlowStepper.builder()
 *     .steps("Account", "Details", "Review", "Confirm")
 *     .build();
 * }</pre>
 *
 * <p>Vertical stepper starting at step 2:</p>
 * <pre>{@code
 * FlowStepper stepper = FlowStepper.builder()
 *     .steps("Choose Plan", "Payment", "Team Setup", "Go Live")
 *     .currentStep(2)
 *     .orientation(FlowStepper.Orientation.VERTICAL)
 *     .build();
 * }</pre>
 *
 * <p>Dot variant with step-changed listener:</p>
 * <pre>{@code
 * FlowStepper stepper = FlowStepper.builder()
 *     .steps("Step 1", "Step 2", "Step 3")
 *     .variant(FlowStepper.Variant.DOT)
 *     .onStepChanged(e -> Notification.show("Now on: " + e.getLabel()))
 *     .build();
 * }</pre>
 *
 * @see StepperConfigurator
 * @see FlowStepper
 */
public interface StepperBuilder
        extends StepperConfigurator<StepperBuilder>,
        ComponentBuilder<FlowStepper, StepperBuilder> {

    /**
     * Creates a new {@link StepperBuilder}.
     *
     * @return a new {@link StepperBuilder}
     */
    static StepperBuilder create() {
        return new DefaultStepperBuilder();
    }
}

