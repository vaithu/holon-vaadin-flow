package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTitleBuilder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public interface TitleBuilder extends TitleConfigurator<TitleBuilder>,
        ComponentBuilder<HorizontalLayout,TitleBuilder>
{

    static TitleBuilder create() {
        return new DefaultTitleBuilder(new HorizontalLayout());
    }

}
