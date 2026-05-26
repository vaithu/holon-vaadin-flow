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
import com.holonplatform.vaadin.flow.components.builders.AlertConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertAction;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertDescription;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertTitle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

/**
 * Base {@link AlertConfigurator} implementation.
 *
 * <p>Extends {@link AbstractComponentConfigurator} to inherit the standard Holon Platform
 * component lifecycle hooks ({@code id}, {@code visible}, {@code styleName}, {@code width},
 * etc.) and provides Alert-specific configuration logic.</p>
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractAlertConfigurator<C extends AlertConfigurator<C>>
        extends AbstractComponentConfigurator<Alert, C>
        implements AlertConfigurator<C> {

    /**
     * Constructor.
     *
     * @param component the {@link Alert} component instance to configure (not null)
     */
    public AbstractAlertConfigurator(Alert component) {
        super(component);
    }

    // -----------------------------------------------------------------------
    // AlertConfigurator implementation
    // -----------------------------------------------------------------------

    @Override
    public C variant(Alert.Variant variant) {
        getComponent().setVariant(variant);
        return getConfigurator();
    }

    @Override
    public C icon(Icon icon) {
        getComponent().setIcon(icon);
        return getConfigurator();
    }

    @Override
    public C clearIcon() {
        getComponent().clearIcon();
        return getConfigurator();
    }

    @Override
    public C title(AlertTitle title) {
        getComponent().setTitle(title);
        return getConfigurator();
    }

    @Override
    public C title(String text) {
        getComponent().setTitle(text);
        return getConfigurator();
    }

    @Override
    public C title(Localizable localizable) {
        getComponent().setTitle(localizable);
        return getConfigurator();
    }

    @Override
    public C description(AlertDescription description) {
        getComponent().setDescription(description);
        return getConfigurator();
    }

    @Override
    public C description(String text) {
        getComponent().setDescription(text);
        return getConfigurator();
    }

    @Override
    public C description(Localizable localizable) {
        getComponent().setDescription(localizable);
        return getConfigurator();
    }

    @Override
    public C action(AlertAction action) {
        getComponent().setAction(action);
        return getConfigurator();
    }

    @Override
    public C action(Component... actions) {
        getComponent().setAction(actions);
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

