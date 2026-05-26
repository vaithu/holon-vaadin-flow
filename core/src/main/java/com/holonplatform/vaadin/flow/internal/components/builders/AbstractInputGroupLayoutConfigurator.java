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

import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.InputGroupLayoutConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputGroup;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

/**
 * Base {@link InputGroupLayoutConfigurator} implementation.
 *
 * <p>Delegates every configuration call to the wrapped {@link InputGroup} component
 * and exposes the standard Holon Platform component hooks ({@code id}, {@code visible},
 * {@code styleName}, {@code width}, {@code enabled}, etc.) via
 * {@link AbstractComponentConfigurator}.</p>
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractInputGroupLayoutConfigurator<C extends InputGroupLayoutConfigurator<C>>
        extends AbstractComponentConfigurator<InputGroup, C>
        implements InputGroupLayoutConfigurator<C> {

    public AbstractInputGroupLayoutConfigurator(InputGroup component) {
        super(component);
    }

    // -----------------------------------------------------------------------
    // InputGroupLayoutConfigurator implementation
    // -----------------------------------------------------------------------

    @Override
    public C add(Component... components) {
        getComponent().add(components);
        return getConfigurator();
    }

    @Override
    public C add(HasComponent... inputs) {
        getComponent().add(inputs);
        return getConfigurator();
    }

    @Override
    @SuppressWarnings("varargs")
    public C add(Input<?>... inputs) {
        getComponent().add(inputs);
        return getConfigurator();
    }

    @Override
    public C responsive(boolean responsive) {
        if (responsive) {
            getComponent().addClassName("input-group--responsive");
        } else {
            getComponent().removeClassName("input-group--responsive");
        }
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

