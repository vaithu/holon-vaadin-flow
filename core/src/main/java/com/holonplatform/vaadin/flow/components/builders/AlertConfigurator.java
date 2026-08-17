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
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultAlertConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertAction;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertDescription;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertTitle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;

/**
 * Configurator for {@link Alert} components.
 *
 * <p>Extends the standard Holon Platform {@link ComponentConfigurator}, {@link HasSizeConfigurator}
 * and {@link HasStyleConfigurator} contracts, adding Alert-specific configuration methods.</p>
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 *
 * @see AlertBuilder
 */
public interface AlertConfigurator<C extends AlertConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    // -----------------------------------------------------------------------
    // Variant
    // -----------------------------------------------------------------------

    /**
     * Sets the visual variant of the alert.
     *
     * @param variant the variant to apply (not null)
     * @return this configurator (for chaining)
     */
    C variant(Alert.Variant variant);

    // -----------------------------------------------------------------------
    // Icon
    // -----------------------------------------------------------------------

    /**
     * Sets the leading icon. Switches the layout to a two-column CSS grid
     * (icon column + content column) via the {@code alert--has-icon} modifier class.
     *
     * @param icon the Vaadin icon to display (not null)
     * @return this configurator (for chaining)
     */
    C icon(Icon icon);

    /**
     * Removes the leading icon and reverts to the single-column layout.
     *
     * @return this configurator (for chaining)
     */
    C clearIcon();

    // -----------------------------------------------------------------------
    // Title
    // -----------------------------------------------------------------------

    /**
     * Sets the alert title using a pre-built {@link AlertTitle} component.
     * Replaces any previously set title.
     *
     * @param title the title component (not null)
     * @return this configurator (for chaining)
     */
    C title(AlertTitle title);

    /**
     * Sets the alert title from a plain string.
     * Replaces any previously set title.
     *
     * @param text the title text (not null)
     * @return this configurator (for chaining)
     */
    C title(String text);

    /**
     * Sets the alert title from a Holon {@link Localizable}.
     * The text is resolved on first attach and re-resolved on subsequent attaches.
     * Replaces any previously set title.
     *
     * @param localizable the localizable message (not null)
     * @return this configurator (for chaining)
     */
    C title(Localizable localizable);

    // -----------------------------------------------------------------------
    // Description
    // -----------------------------------------------------------------------

    /**
     * Sets the alert description using a pre-built {@link AlertDescription} component.
     * Replaces any previously set description.
     *
     * @param description the description component (not null)
     * @return this configurator (for chaining)
     */
    C description(AlertDescription description);

    /**
     * Sets the alert description from a plain string.
     * Replaces any previously set description.
     *
     * @param text the description text (not null)
     * @return this configurator (for chaining)
     */
    C description(String text);

    /**
     * Sets the alert description from a Holon {@link Localizable}.
     * The text is resolved on first attach and re-resolved on subsequent attaches.
     * Replaces any previously set description.
     *
     * @param localizable the localizable message (not null)
     * @return this configurator (for chaining)
     */
    C description(Localizable localizable);

    // -----------------------------------------------------------------------
    // Action
    // -----------------------------------------------------------------------

    /**
     * Sets the action slot using a pre-built {@link AlertAction} container.
     * Replaces any previously set action.
     *
     * @param action the action container (not null)
     * @return this configurator (for chaining)
     */
    C action(AlertAction action);

    /**
     * Wraps the given components in an {@link AlertAction} and sets the action slot.
     * Replaces any previously set action.
     *
     * @param actions action components (buttons, links, etc.)
     * @return this configurator (for chaining)
     */
    C action(Component... actions);

    // -----------------------------------------------------------------------
    // Inline-action layout
    // -----------------------------------------------------------------------

    /**
     * Enables the inline-action layout ({@code alert--inline}).
     *
     * <p>The action is placed to the right of the title/description instead of below.
     * Useful for compact list rows such as low-stock alert items.</p>
     *
     * @return this configurator (for chaining)
     */
    C inlineAction();

    // -----------------------------------------------------------------------
    // Left-border-only style
    // -----------------------------------------------------------------------

    /**
     * Enables the left-border-only style ({@code alert--left-border}).
     *
     * <p>Replaces the full perimeter border with a 3 px left accent whose colour
     * is inherited from the active variant.</p>
     *
     * @return this configurator (for chaining)
     */
    C leftBorder();

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Get a new {@link BaseAlertConfigurator} to configure an existing {@link Alert} component.
     *
     * @param alert the alert component to configure (not null)
     * @return a new {@link BaseAlertConfigurator}
     */
    static BaseAlertConfigurator configure(Alert alert) {
        return new DefaultAlertConfigurator(alert);
    }

    // -----------------------------------------------------------------------
    // Base configurator
    // -----------------------------------------------------------------------

    /**
     * Base (non-generic) {@link AlertConfigurator}.
     */
    interface BaseAlertConfigurator extends AlertConfigurator<BaseAlertConfigurator> {
    }
}

