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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultSeparatorConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Separator;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;

/**
 * Fluent configurator for {@link Separator} components.
 *
 * <p>Covers all Separator-specific properties (orientation and decorative flag) plus the
 * standard Holon Platform component properties inherited from
 * {@link ComponentConfigurator}, {@link HasSizeConfigurator}, {@link HasStyleConfigurator}
 * and {@link HasEnabledConfigurator}.</p>
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 *
 * @see SeparatorBuilder
 * @see Separator
 */
public interface SeparatorConfigurator<C extends SeparatorConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>,
        HasEnabledConfigurator<C> {

    // -----------------------------------------------------------------------
    // Orientation
    // -----------------------------------------------------------------------

    /**
     * Sets the separator orientation.
     *
     * <pre>{@code
     * Separator.builder().orientation(Separator.Orientation.VERTICAL).build();
     * }</pre>
     *
     * @param orientation the desired orientation (not null; defaults to
     *                    {@link Separator.Orientation#HORIZONTAL})
     * @return this configurator for chaining
     */
    C orientation(Separator.Orientation orientation);

    // -----------------------------------------------------------------------
    // Decorative flag
    // -----------------------------------------------------------------------

    /**
     * Sets whether the separator is purely decorative.
     *
     * <p>When {@code true} the element carries {@code role="none"} and
     * {@code aria-hidden="true"} so assistive technologies skip it.</p>
     * <p>When {@code false} (the default) the element carries
     * {@code role="separator"} and {@code aria-orientation}.</p>
     *
     * @param decorative {@code true} to make the separator decorative
     * @return this configurator for chaining
     */
    C decorative(boolean decorative);

    // -----------------------------------------------------------------------
    // Color
    // -----------------------------------------------------------------------

    /**
     * Sets the separator colour via a predefined {@link Color.Background} CSS class.
     *
     * <p>Replaces any previously applied colour class. Pass {@code null} to revert to
     * the default CSS colour.</p>
     *
     * <pre>{@code
     * Separator.builder().color(Color.Background.PRIMARY).build();
     * }</pre>
     *
     * @param color the colour to apply, or {@code null} to clear
     * @return this configurator for chaining
     */
    C color(Color.Background color);

    // -----------------------------------------------------------------------
    // Thickness
    // -----------------------------------------------------------------------

    /**
     * Sets the separator thickness via a predefined {@link Separator.Thickness} CSS modifier class.
     *
     * <p>Works for both horizontal (controls height) and vertical (controls width)
     * separators. Pass {@code null} to revert to the CSS default (1 px).</p>
     *
     * <pre>{@code
     * Separator.builder().thickness(Separator.Thickness.THICK).build();
     * }</pre>
     *
     * @param thickness the thickness to apply, or {@code null} to clear
     * @return this configurator for chaining
     */
    C thickness(Separator.Thickness thickness);

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Returns a configurator for an <strong>existing</strong> {@link Separator} instance.
     *
     * <pre>{@code
     * SeparatorConfigurator.configure(mySeparator)
     *     .orientation(Separator.Orientation.VERTICAL)
     *     .decorative(true);
     * }</pre>
     *
     * @param separator the separator to configure (not null)
     * @return a {@link BaseSeparatorConfigurator}
     */
    static BaseSeparatorConfigurator configure(Separator separator) {
        return new DefaultSeparatorConfigurator(separator);
    }

    /**
     * Base configurator type returned by {@link #configure(Separator)}.
     */
    interface BaseSeparatorConfigurator extends SeparatorConfigurator<BaseSeparatorConfigurator> {
    }
}

