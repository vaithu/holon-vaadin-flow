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

import com.holonplatform.vaadin.flow.components.builders.TotalsCardConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsCard;

/**
 * Default {@link TotalsCardConfigurator.BaseTotalsCardConfigurator} implementation.
 *
 * <p>Used by {@link TotalsCardConfigurator#configure(TotalsCard)} to configure an already-existing
 * {@link TotalsCard} instance without building a new one.</p>
 */
public class DefaultTotalsCardConfigurator
        extends AbstractTotalsCardConfigurator<TotalsCardConfigurator.BaseTotalsCardConfigurator>
        implements TotalsCardConfigurator.BaseTotalsCardConfigurator {

    /**
     * Constructor.
     *
     * @param totalsCard the existing {@link TotalsCard} component to configure (not null)
     */
    public DefaultTotalsCardConfigurator(TotalsCard totalsCard) {
        super(totalsCard);
    }

    @Override
    protected TotalsCardConfigurator.BaseTotalsCardConfigurator getConfigurator() {
        return this;
    }
}

