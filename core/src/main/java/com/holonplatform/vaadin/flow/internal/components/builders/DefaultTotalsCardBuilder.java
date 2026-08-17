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

import com.holonplatform.vaadin.flow.components.builders.TotalsCardBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsCard;

/**
 * Default {@link TotalsCardBuilder} implementation.
 *
 * <p>Instantiates a new {@link TotalsCard} and delegates all configuration to
 * {@link AbstractTotalsCardConfigurator}. Returned by {@link TotalsCardBuilder#create()}.</p>
 */
public class DefaultTotalsCardBuilder
        extends AbstractTotalsCardConfigurator<TotalsCardBuilder>
        implements TotalsCardBuilder {

    /**
     * Constructor.
     */
    public DefaultTotalsCardBuilder() {
        super(new TotalsCard());
    }

    @Override
    protected TotalsCardBuilder getConfigurator() {
        return this;
    }

    @Override
    public TotalsCard build() {
        applyPostProcessors();
        return getComponent();
    }
}

