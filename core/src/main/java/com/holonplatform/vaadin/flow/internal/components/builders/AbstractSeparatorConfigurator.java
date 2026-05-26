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

import com.holonplatform.vaadin.flow.components.builders.SeparatorConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Separator;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

/**
 * Base {@link SeparatorConfigurator} implementation.
 *
 * <p>Delegates configuration calls to the wrapped {@link Separator} component and exposes
 * the standard Holon Platform component hooks ({@code id}, {@code visible},
 * {@code styleName}, {@code width}, {@code enabled}) via
 * {@link AbstractComponentConfigurator}.</p>
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractSeparatorConfigurator<C extends SeparatorConfigurator<C>>
        extends AbstractComponentConfigurator<Separator, C>
        implements SeparatorConfigurator<C> {

    public AbstractSeparatorConfigurator(Separator component) {
        super(component);
    }

    // -----------------------------------------------------------------------
    // SeparatorConfigurator implementation
    // -----------------------------------------------------------------------

    @Override
    public C orientation(Separator.Orientation orientation) {
        getComponent().setOrientation(orientation);
        return getConfigurator();
    }

    @Override
    public C decorative(boolean decorative) {
        getComponent().setDecorative(decorative);
        return getConfigurator();
    }

    @Override
    public C color(Color.Background color) {
        getComponent().setColor(color);
        return getConfigurator();
    }

    @Override
    public C thickness(Separator.Thickness thickness) {
        getComponent().setThickness(thickness);
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

