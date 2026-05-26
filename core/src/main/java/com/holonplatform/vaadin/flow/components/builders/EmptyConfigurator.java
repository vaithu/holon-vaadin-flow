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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultEmptyConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Empty;
import com.holonplatform.vaadin.flow.vaadinplus.components.EmptyAction;
import com.holonplatform.vaadin.flow.vaadinplus.components.EmptyDescription;
import com.holonplatform.vaadin.flow.vaadinplus.components.EmptyTitle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;

/**
 * Fluent configurator for {@link Empty} components.
 *
 * <p>Provides methods to set the icon, title, description, and action of the empty-state
 * component, along with all standard Holon Platform component properties (id, style, size…)
 * inherited from {@link ComponentConfigurator} and {@link HasSizeConfigurator}.</p>
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 * @see EmptyBuilder
 * @see Empty
 */
public interface EmptyConfigurator<C extends EmptyConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    // -----------------------------------------------------------------------
    // Icon
    // -----------------------------------------------------------------------

    /**
     * Sets a Vaadin {@link Icon} in the icon slot above the title.
     *
     * @param icon the icon to display (not null)
     * @return this configurator for chaining
     */
    C icon(Icon icon);

    /**
     * Sets an arbitrary {@link Component} in the icon slot (e.g. an SVG illustration or image).
     *
     * @param illustration the component to display as the illustration (not null)
     * @return this configurator for chaining
     */
    C icon(Component illustration);

    /**
     * Removes any icon or illustration from the icon slot.
     *
     * @return this configurator for chaining
     */
    C clearIcon();

    // -----------------------------------------------------------------------
    // Title
    // -----------------------------------------------------------------------

    /**
     * Sets the title using a pre-built {@link EmptyTitle}.
     *
     * @param title the title component (not null)
     * @return this configurator for chaining
     */
    C title(EmptyTitle title);

    /**
     * Sets the title from a plain string.
     *
     * @param text the title text (not null)
     * @return this configurator for chaining
     */
    C title(String text);

    /**
     * Sets the title from a Holon {@link Localizable}.
     * Resolved immediately if a localization context is active; re-resolved on each attach.
     *
     * @param localizable the localizable message (not null)
     * @return this configurator for chaining
     */
    C title(Localizable localizable);

    // -----------------------------------------------------------------------
    // Description
    // -----------------------------------------------------------------------

    /**
     * Sets the description using a pre-built {@link EmptyDescription}.
     *
     * @param description the description component (not null)
     * @return this configurator for chaining
     */
    C description(EmptyDescription description);

    /**
     * Sets the description from a plain string.
     *
     * @param text the description text (not null)
     * @return this configurator for chaining
     */
    C description(String text);

    /**
     * Sets the description from a Holon {@link Localizable}.
     *
     * @param localizable the localizable message (not null)
     * @return this configurator for chaining
     */
    C description(Localizable localizable);

    // -----------------------------------------------------------------------
    // Action
    // -----------------------------------------------------------------------

    /**
     * Sets the action slot using a pre-built {@link EmptyAction}.
     *
     * @param action the action container (not null)
     * @return this configurator for chaining
     */
    C action(EmptyAction action);

    /**
     * Wraps the given components in an {@link EmptyAction} and sets it as the action slot.
     *
     * @param actions action components (buttons, links, etc.)
     * @return this configurator for chaining
     */
    C action(Component... actions);

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Get a configurator for an existing {@link Empty} instance.
     *
     * @param empty the empty component to configure (not null)
     * @return a {@link BaseEmptyConfigurator}
     */
    static BaseEmptyConfigurator configure(Empty empty) {
        return new DefaultEmptyConfigurator(empty);
    }

    /**
     * Base configurator type returned by {@link #configure(Empty)}.
     */
    interface BaseEmptyConfigurator extends EmptyConfigurator<BaseEmptyConfigurator> {
    }
}

