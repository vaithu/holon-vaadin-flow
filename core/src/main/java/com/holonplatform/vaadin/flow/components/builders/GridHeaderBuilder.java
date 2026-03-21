package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultGridHeaderBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.vaadin.flow.component.html.H3;

public interface GridHeaderBuilder extends GridHeaderConfigurator<GridHeaderBuilder>
        , ComponentBuilder<GridHeader, GridHeaderBuilder> {

   static  GridHeaderBuilder create(String title) {
        return new DefaultGridHeaderBuilder(title);
    }
    static  GridHeaderBuilder create(LabelBuilder<?> labelBuilder) {
        return new DefaultGridHeaderBuilder(labelBuilder);
    }
}
