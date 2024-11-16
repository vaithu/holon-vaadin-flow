/*
 * Copyright 2016-2018 Axioma srl.
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
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultDivConfigurator;
import com.holonplatform.vaadin.flow.internal.lumo.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

/**
 * Configurator for {@link com.vaadin.flow.component.html.Div} type components.
 *
 * @param <C> Concrete configurator type
 * @since 5.5.4
 */
public interface DivConfigurator<C extends DivConfigurator<C>> extends HasComponentsConfigurator<C>,
        HasStyleConfigurator<C>, HasSizeConfigurator<C>, ComponentConfigurator<C>,
        HasTitleConfigurator<C> {

    C add(String title, Component... components);

    C add(String title, HasComponent... components);

    default C card() {
        return styleName("card");
    }

    C responsive();

    C cssGridLayout(int columns);
    C cssGridLayout(String... styles);

    C horizontalRule();
    C horizontalRule(String... styles);
    C horizontalRule(int size,String... styles);





    C add(int columnSpan, Component... components);
    C add(int columnSpan, HasComponent... components);

    C add(ColumnSpan columnSpan, Component... components);

    C add(RowSpan rowSpan, Component... components);

    C add(ColumnSpan columnSpan, RowSpan rowSpan, Component... components);

    default C row(GridRow rows) {
        return styleName(rows.getValue());
    }

    default C column(GridColumn column) {
        return styleName(column.getValue());
    }

    default C display(Display display) {
        return styleName(display.getValue());
    }


    /**
     * Get a new {@link DivConfigurator} for given component.
     *
     * @param component The component to configure (not null)
     * @return A new {@link DivConfigurator}
     */
    static BaseDivConfigurator configure(Div component) {
        return new DefaultDivConfigurator(component);
    }

    /**
     * Base configurator.
     */
    public interface BaseDivConfigurator extends DivConfigurator<BaseDivConfigurator> {

    }

}
