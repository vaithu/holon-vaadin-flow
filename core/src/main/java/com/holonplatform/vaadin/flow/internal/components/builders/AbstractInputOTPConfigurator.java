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

import com.holonplatform.vaadin.flow.components.builders.InputOTPConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputOTP;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputOTPGroup;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputOTPSeparator;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputOTPSlot;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableFunction;

import java.util.Optional;

/**
 * Base {@link InputOTPConfigurator} implementation.
 *
 * <p>Delegates every configuration call to the wrapped {@link InputOTP} component and exposes
 * the standard Holon Platform component hooks ({@code id}, {@code visible}, {@code styleName},
 * {@code width}, {@code enabled}, etc.) via {@link AbstractComponentConfigurator}.</p>
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractInputOTPConfigurator<C extends InputOTPConfigurator<C>>
        extends AbstractComponentConfigurator<InputOTP, C>
        implements InputOTPConfigurator<C> {

    public AbstractInputOTPConfigurator(InputOTP component) {
        super(component);
    }

    // -----------------------------------------------------------------------
    // InputOTPConfigurator implementation
    // -----------------------------------------------------------------------

    @Override
    public C group(int count) {
        InputOTPGroup group = new InputOTPGroup();
        for (int i = 0; i < count; i++) {
            group.add(new InputOTPSlot());
        }
        getComponent().add(group);
        return getConfigurator();
    }

    @Override
    public C separator() {
        getComponent().add(new InputOTPSeparator());
        return getConfigurator();
    }

    @Override
    public C separator(Component content) {
        getComponent().add(new InputOTPSeparator(content));
        return getConfigurator();
    }

    @Override
    public C pattern(String pattern) {
        getComponent().setPattern(pattern);
        return getConfigurator();
    }

    @Override
    public C readOnly(boolean readOnly) {
        getComponent().setReadOnly(readOnly);
        return getConfigurator();
    }

    @Override
    public C onComplete(SerializableConsumer<String> handler) {
        getComponent().setOnComplete(handler);
        return getConfigurator();
    }

    @Override
    public C onValueChange(SerializableConsumer<String> listener) {
        getComponent().addValueChangeListener(listener);
        return getConfigurator();
    }

    @Override
    public C validator(SerializableFunction<String, String> validator) {
        getComponent().setValidator(validator);
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

