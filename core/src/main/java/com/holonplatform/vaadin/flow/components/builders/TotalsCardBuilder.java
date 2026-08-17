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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTotalsCardBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsCard;

/**
 * Builder to create and configure {@link TotalsCard} components.
 *
 * <p>Extends {@link TotalsCardConfigurator} (for all TotalsCard-specific methods) and
 * {@link ComponentBuilder} (for the terminal {@link #build()} method).</p>
 *
 * <p>Usage:
 * <pre>{@code
 * TotalsCard totals = TotalsCard.builder()
 *     .row("Revenue YTD", "€1,420,400")
 *     .row("Volume discount (3-yr)", "−€142,040", TotalsRow.Variant.DISCOUNT)
 *     .row("Net revenue YTD", "€1,278,360")
 *     .row("Open AR (4 invoices)", "€14,810", TotalsRow.Variant.WARNING)
 *     .row("YTD total", "€1,293,170", TotalsRow.Variant.GRAND_TOTAL)
 *     .build();
 * }</pre>
 *
 * <p>Instances are obtained via the static factory method {@link #create()}, or via the
 * convenience shortcut {@link TotalsCard#builder()}.
 *
 * @see TotalsCardConfigurator
 * @see TotalsCard
 */
public interface TotalsCardBuilder extends TotalsCardConfigurator<TotalsCardBuilder>,
        ComponentBuilder<TotalsCard, TotalsCardBuilder> {

    /**
     * Create a new {@link TotalsCardBuilder}.
     *
     * @return a new {@link TotalsCardBuilder}
     */
    static TotalsCardBuilder create() {
        return new DefaultTotalsCardBuilder();
    }
}

