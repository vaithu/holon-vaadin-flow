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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultButtonGroupConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.ButtonGroup;
import com.vaadin.flow.component.button.Button;

/**
 * Fluent configurator for {@link ButtonGroup} layout components.
 *
 * <p>Covers all ButtonGroup-specific properties (child buttons, orientation) plus the
 * standard Holon Platform component properties inherited from {@link ComponentConfigurator},
 * {@link HasSizeConfigurator}, {@link HasStyleConfigurator}, and {@link HasEnabledConfigurator}.</p>
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 *
 * @see ButtonGroupBuilder
 * @see ButtonGroup
 */
public interface ButtonGroupConfigurator<C extends ButtonGroupConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>,
        HasEnabledConfigurator<C> {

    // -----------------------------------------------------------------------
    // Add buttons
    // -----------------------------------------------------------------------

    /**
     * Appends one or more {@link Button} instances to the group.
     *
     * <p>Null elements are silently skipped.</p>
     *
     * @param buttons the buttons to add (null array is a no-op)
     * @return this configurator for chaining
     */
    C add(Button... buttons);

    // -----------------------------------------------------------------------
    // Orientation
    // -----------------------------------------------------------------------

    /**
     * Sets the orientation of the button group.
     *
     * @param orientation the desired orientation (not null)
     * @return this configurator for chaining
     */
    C orientation(ButtonGroup.Orientation orientation);

    /**
     * Convenience shortcut for {@code orientation(ButtonGroup.Orientation.VERTICAL)}.
     *
     * @return this configurator for chaining
     */
    default C vertical() {
        return orientation(ButtonGroup.Orientation.VERTICAL);
    }

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Returns a configurator for an <strong>existing</strong> {@link ButtonGroup} instance.
     *
     * <pre>{@code
     * ButtonGroupConfigurator.configure(myGroup)
     *     .vertical()
     *     .width("100%");
     * }</pre>
     *
     * @param buttonGroup the group to configure (not null)
     * @return a {@link BaseButtonGroupConfigurator}
     */
    static BaseButtonGroupConfigurator configure(ButtonGroup buttonGroup) {
        return new DefaultButtonGroupConfigurator(buttonGroup);
    }

    /**
     * Base configurator type returned by {@link #configure(ButtonGroup)}.
     */
    interface BaseButtonGroupConfigurator
            extends ButtonGroupConfigurator<BaseButtonGroupConfigurator> {
    }
}

