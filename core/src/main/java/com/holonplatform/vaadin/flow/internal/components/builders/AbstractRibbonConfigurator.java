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

import java.util.Optional;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.RibbonConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Ribbon;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

/**
 * Base {@link RibbonConfigurator} implementation.
 *
 * <p>Delegates every configuration call to the wrapped {@link Ribbon} component and exposes the
 * standard Holon Platform component hooks ({@code id}, {@code visible}, {@code styleName},
 * {@code width}, {@code enabled}, etc.) via {@link AbstractComponentConfigurator}.
 *
 * @param <C> concrete configurator type
 */
public abstract class AbstractRibbonConfigurator<C extends RibbonConfigurator<C>>
        extends AbstractComponentConfigurator<Ribbon, C>
        implements RibbonConfigurator<C> {

    public AbstractRibbonConfigurator(Ribbon component) {
        super(component);
    }

    @Override
    public C variant(Ribbon.Variant variant) {
        getComponent().setVariant(variant);
        return getConfigurator();
    }

    @Override
    public C color(Ribbon.Color color) {
        getComponent().setColor(color);
        return getConfigurator();
    }

    @Override
    public C label(String label) {
        getComponent().setLabel(label);
        return getConfigurator();
    }

    @Override
    public C label(Localizable label) {
        getComponent().setLabel(label);
        return getConfigurator();
    }

    @Override
    public C content(Component... components) {
        getComponent().setContent(components);
        return getConfigurator();
    }

    @Override
    public C addContent(Component... components) {
        getComponent().addContent(components);
        return getConfigurator();
    }

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
