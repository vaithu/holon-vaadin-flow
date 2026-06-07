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

import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultInputGroupLayoutConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputGroup;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputGroupText;
import com.vaadin.flow.component.Component;

/**
 * Fluent configurator for {@link InputGroup} layout components.
 *
 * <p>Covers all InputGroup-specific properties (child components, responsive mode) plus the
 * standard Holon Platform component properties inherited from {@link ComponentConfigurator},
 * {@link HasSizeConfigurator}, {@link HasStyleConfigurator} and {@link HasEnabledConfigurator}.</p>
 *
 * <p>Named <em>InputGroupLayout</em> to avoid collision with the existing
 * {@link InputGroupConfigurator}, which configures property-bound form input groups.</p>
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 *
 * @see InputGroupBuilder
 * @see InputGroup
 */
public interface InputGroupLayoutConfigurator<C extends InputGroupLayoutConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>,
        HasEnabledConfigurator<C> {

    // -----------------------------------------------------------------------
    // Add — Vaadin components
    // -----------------------------------------------------------------------

    /**
     * Appends one or more Vaadin {@link Component} instances (text fields, buttons,
     * {@link InputGroupText} addons, etc.) to the group.
     *
     * <p>Null elements in the array are silently skipped.</p>
     *
     * @param components the components to content (null array is a no-op)
     * @return this configurator for chaining
     */
    C add(Component... components);

    // -----------------------------------------------------------------------
    // Add — Holon HasComponent / Input<T>
    // -----------------------------------------------------------------------

    /**
     * Appends one or more Holon {@link HasComponent} wrappers to the group by extracting
     * the underlying {@link Component} via {@link HasComponent#getComponent()}.
     *
     * <p>Null elements and wrappers returning a null component are silently skipped.</p>
     *
     * @param inputs the wrappers to content (null array is a no-op)
     * @return this configurator for chaining
     */
    C add(HasComponent... inputs);

    /**
     * Appends one or more Holon {@link Input} instances to the group.
     *
     * <p>This is a typed convenience overload of {@link #add(HasComponent...)}.</p>
     *
     * @param inputs the Holon inputs to content (null array is a no-op)
     * @return this configurator for chaining
     */
    @SuppressWarnings("varargs")
    C add(Input<?>... inputs);

    // -----------------------------------------------------------------------
    // Responsive modifier
    // -----------------------------------------------------------------------

    /**
     * Toggles the {@code input-group--responsive} CSS modifier that stacks children
     * vertically on screens ≤ 480 px.
     *
     * @param responsive {@code true} to content the modifier; {@code false} to remove it
     * @return this configurator for chaining
     */
    C responsive(boolean responsive);

    /**
     * Convenience shortcut for {@code responsive(true)}.
     *
     * @return this configurator for chaining
     */
    default C responsive() {
        return responsive(true);
    }

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Returns a configurator for an <strong>existing</strong> {@link InputGroup} instance.
     *
     * <pre>{@code
     * InputGroupLayoutConfigurator.configure(myGroup)
     *     .responsive(true)
     *     .width("100%");
     * }</pre>
     *
     * @param inputGroup the group to configure (not null)
     * @return a {@link BaseInputGroupLayoutConfigurator}
     */
    static BaseInputGroupLayoutConfigurator configure(InputGroup inputGroup) {
        return new DefaultInputGroupLayoutConfigurator(inputGroup);
    }

    /**
     * Base configurator type returned by {@link #configure(InputGroup)}.
     */
    interface BaseInputGroupLayoutConfigurator
            extends InputGroupLayoutConfigurator<BaseInputGroupLayoutConfigurator> {
    }
}

