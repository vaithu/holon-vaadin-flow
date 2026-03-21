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
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFlexLayoutConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

/**
 * Configurator for {@link com.vaadin.flow.component.orderedlayout.FlexLayout} type components.
 *
 * @param <C> Concrete configurator type
 * @since 5.5.4
 */
public interface FlexLayoutConfigurator<C extends FlexLayoutConfigurator<C>> extends HasComponentsConfigurator<C>,
        HasStyleConfigurator<C>, HasSizeConfigurator<C>, ComponentConfigurator<C> {

    C alignContent(FlexLayout.ContentAlignment alignment);

    C flexBasis(String width, HasElement... components);

    C flexDirection(FlexLayout.FlexDirection flexDirection);

    C flexWrap(FlexLayout.FlexWrap flexWrap);

    C order(int order, HasElement component);

    C title(LabelBuilder<H4> title);

    C title(String title);

    default C card() {
        return styleName("card");
    }

    C add(String title, Component... components);
    C add(String title, HasComponent... components);
    /**
     * Get a new {@link FlexLayoutConfigurator} for given component.
     *
     * @param component The component to configure (not null)
     * @return A new {@link FlexLayoutConfigurator}
     */
    static BaseFlexLayoutConfigurator configure(FlexLayout component) {
        return new DefaultFlexLayoutConfigurator(component);
    }

    /**
     * Base configurator.
     */
    public interface BaseFlexLayoutConfigurator extends FlexLayoutConfigurator<BaseFlexLayoutConfigurator> {

    }

}
