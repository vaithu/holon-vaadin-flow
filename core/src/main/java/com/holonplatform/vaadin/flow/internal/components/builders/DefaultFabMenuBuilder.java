/*
 * Copyright 2016-2026 Axioma srl.
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

import com.holonplatform.vaadin.flow.components.builders.FabMenuBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Fab;
import com.holonplatform.vaadin.flow.vaadinplus.components.FabMenu;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Default {@link FabMenuBuilder} implementation.
 */
public class DefaultFabMenuBuilder extends AbstractFabMenuConfigurator<FabMenuBuilder> implements FabMenuBuilder {

    public DefaultFabMenuBuilder() {
        super(new FabMenu());
    }

    public DefaultFabMenuBuilder(VaadinIcon icon) {
        super(new FabMenu(icon));
    }

    public DefaultFabMenuBuilder(VaadinIcon icon, Fab.Color color) {
        super(new FabMenu(icon, color));
    }

    @Override
    protected FabMenuBuilder getConfigurator() {
        return this;
    }

    @Override
    public FabMenu build() {
        applyPostProcessors();
        return getComponent();
    }
}

