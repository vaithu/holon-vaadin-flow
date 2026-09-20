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
package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;

import com.holonplatform.core.i18n.Localizable;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultRibbonConfigurator;
import com.iyensoft.vaadin.flow.components.Ribbon;
import com.vaadin.flow.component.Component;

/**
 * Fluent configurator for {@link Ribbon} components.
 *
 * <p>Covers all ribbon-specific properties (shape, colour, label and card content) plus the
 * standard Holon Platform component properties inherited from {@link ComponentConfigurator},
 * {@link HasSizeConfigurator}, {@link HasStyleConfigurator} and {@link HasEnabledConfigurator}.
 *
 * @param <C> concrete configurator type (for fluent chaining)
 *
 * @see RibbonBuilder
 * @see Ribbon
 */
public interface RibbonConfigurator<C extends RibbonConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>,
        HasEnabledConfigurator<C> {

    /**
     * Sets the ribbon shape.
     *
     * @param variant the ribbon shape
     * @return this configurator for chaining
     */
    C variant(Ribbon.Variant variant);

    /**
     * Sets the ribbon colour.
     *
     * @param color the ribbon colour
     * @return this configurator for chaining
     */
    C color(Ribbon.Color color);

    /**
     * Sets the ribbon label.
     *
     * @param label the ribbon label
     * @return this configurator for chaining
     */
    C label(String label);

    /**
     * Sets the ribbon label from a {@link Localizable} (resolved at call time).
     *
     * @param label the localizable ribbon label
     * @return this configurator for chaining
     */
    C label(Localizable label);

    /**
     * Replaces the card content with the given components.
     *
     * @param components the card content
     * @return this configurator for chaining
     */
    C content(Component... components);

    /**
     * Appends the given components to the card content.
     *
     * @param components the components to append
     * @return this configurator for chaining
     */
    C addContent(Component... components);

    /**
     * Returns a configurator for an <strong>existing</strong> {@link Ribbon} instance.
     *
     * @param ribbon the ribbon to configure (not null)
     * @return a {@link BaseRibbonConfigurator}
     */
    static BaseRibbonConfigurator configure(Ribbon ribbon) {
        return new DefaultRibbonConfigurator(ribbon);
    }

    /**
     * Base configurator type returned by {@link #configure(Ribbon)}.
     */
    interface BaseRibbonConfigurator extends RibbonConfigurator<BaseRibbonConfigurator> {
    }
}
