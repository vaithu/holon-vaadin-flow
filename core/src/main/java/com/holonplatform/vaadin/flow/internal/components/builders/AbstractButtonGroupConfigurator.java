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

import com.holonplatform.vaadin.flow.components.builders.ButtonGroupConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.ButtonGroup;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

/**
 * Base {@link ButtonGroupConfigurator} implementation.
 *
 * <p>Delegates configuration calls to the wrapped {@link ButtonGroup} and exposes
 * the standard Holon Platform component hooks via {@link AbstractComponentConfigurator}.</p>
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractButtonGroupConfigurator<C extends ButtonGroupConfigurator<C>>
        extends AbstractComponentConfigurator<ButtonGroup, C>
        implements ButtonGroupConfigurator<C> {

    public AbstractButtonGroupConfigurator(ButtonGroup component) {
        super(component);
    }

    // -----------------------------------------------------------------------
    // ButtonGroupConfigurator implementation
    // -----------------------------------------------------------------------

    @Override
    public C add(Button... buttons) {
        getComponent().add(buttons);
        return getConfigurator();
    }

    @Override
    public C orientation(ButtonGroup.Orientation orientation) {
        getComponent().setOrientation(orientation);
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

