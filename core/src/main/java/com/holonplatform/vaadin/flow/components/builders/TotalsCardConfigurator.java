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
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTotalsCardConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsCard;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsRow;

/**
 * Configurator for {@link TotalsCard} components.
 *
 * <p>Extends the standard Holon Platform {@link ComponentConfigurator}, {@link HasSizeConfigurator}
 * and {@link HasStyleConfigurator} contracts, adding rows configuration methods.</p>
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 *
 * @see TotalsCardBuilder
 */
public interface TotalsCardConfigurator<C extends TotalsCardConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    // -----------------------------------------------------------------------
    // Rows
    // -----------------------------------------------------------------------

    /**
     * Appends a pre-built row.
     *
     * @param row the row to add (not null)
     * @return this configurator (for chaining)
     */
    C row(TotalsRow row);

    /**
     * Appends a {@link TotalsRow.Variant#DEFAULT} row built from plain strings.
     *
     * @param label the row label text (not null)
     * @param value the row value text (not null)
     * @return this configurator (for chaining)
     */
    C row(String label, String value);

    /**
     * Appends a row built from plain strings with the given variant.
     *
     * @param label   the row label text (not null)
     * @param value   the row value text (not null)
     * @param variant the row visual variant (not null)
     * @return this configurator (for chaining)
     */
    C row(String label, String value, TotalsRow.Variant variant);

    /**
     * Appends a row built from Holon {@link Localizable} messages with the given variant.
     *
     * @param label   the localizable row label (not null)
     * @param value   the localizable row value (not null)
     * @param variant the row visual variant (not null)
     * @return this configurator (for chaining)
     */
    C row(Localizable label, Localizable value, TotalsRow.Variant variant);

    /**
     * Removes any previously added row.
     *
     * @return this configurator (for chaining)
     */
    C clearRows();

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Get a new {@link BaseTotalsCardConfigurator} to configure an existing {@link TotalsCard} component.
     *
     * @param totalsCard the totals card component to configure (not null)
     * @return a new {@link BaseTotalsCardConfigurator}
     */
    static BaseTotalsCardConfigurator configure(TotalsCard totalsCard) {
        return new DefaultTotalsCardConfigurator(totalsCard);
    }

    // -----------------------------------------------------------------------
    // Base configurator
    // -----------------------------------------------------------------------

    /**
     * Base (non-generic) {@link TotalsCardConfigurator}.
     */
    interface BaseTotalsCardConfigurator extends TotalsCardConfigurator<BaseTotalsCardConfigurator> {
    }
}

