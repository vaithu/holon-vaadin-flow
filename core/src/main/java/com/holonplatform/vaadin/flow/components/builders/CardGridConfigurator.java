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
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultCardGridConfigurator;
import com.holonplatform.vaadin.flow.internal.components.support.BreakPoint;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

/**
 * Configurator for {@link Div} type components.
 *
 * @param <C> Concrete configurator type
 * @since 5.5.4
 */
public interface CardGridConfigurator<C extends CardGridConfigurator<C>> extends
        ComponentConfigurator<C>,
        HasStyleConfigurator<C>,HasSizeConfigurator<C> {

    CardBuilder<C> withCard(Div content);
    CardBuilder<C> withCard(Component... components);
    CardBuilder<C> withCard(String title,Component... components);

    default CardBuilder<C> withCard(String title, HasComponent... components) {
        return withCard(title, UIUtils.toComponents(components));
    }

    default CardBuilder<C> withCard( HasComponent... components) {
        return withCard(UIUtils.toComponents(components));
    }


    C _addToParent(Div component);

    /**
     * Get a new {@link CardGridConfigurator} for given component.
     *
     * @param component The component to configure (not null)
     * @return A new {@link CardGridConfigurator}
     */
    static BaseCardGridConfigurator configure(Div component) {
        return new DefaultCardGridConfigurator(component);
    }

    C gap(boolean gap);

    default C withoutGap() {
        return gap(false);
    }

    C column(int column);

    C row(int row);

    C small(int column);

    C medium(int column);

    C large(int column);

    C xLarge(int column);

    C xxLarge(int column);

    C column(BreakPoint breakPoint, int column);

    /**
     * Base configurator.
     */
    public interface BaseCardGridConfigurator extends CardGridConfigurator<BaseCardGridConfigurator> {
        Div build();
    }

    interface CardBuilder<B extends CardGridConfigurator<B> > extends
            HasStyleConfigurator<CardBuilder<B>>{

        B add();

        CardBuilder<B> colSpan(int colSpan);
        CardBuilder<B> rowSpan(int rowSpan);

        CardBuilder<B> cardStyle(boolean enabled);

        CardBuilder<B> largeBorder();

        default CardBuilder<B> background(String... styles) {
            return styleNames(styles);
        }

        default CardBuilder<B> withoutCardStyle() {
            return cardStyle(false);
        }

        CardBuilder<B> titleColor(String... styles);

        Div build();

    }


}
