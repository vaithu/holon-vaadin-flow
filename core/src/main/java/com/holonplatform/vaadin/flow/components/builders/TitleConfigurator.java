package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultBaseTitleConfigurator;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public interface TitleConfigurator<C extends TitleConfigurator<C>> extends
        ComponentConfigurator<C>,
        HasTextConfigurator<C>,
        HasBorderLayoutConfigurator<C>, HasComponentsConfigurator<C> {


    interface BaseTitleConfigurator extends TitleConfigurator<BaseTitleConfigurator> {

    }


    static BaseTitleConfigurator configure(HorizontalLayout layout) {
        return new DefaultBaseTitleConfigurator(layout);
    }

}
