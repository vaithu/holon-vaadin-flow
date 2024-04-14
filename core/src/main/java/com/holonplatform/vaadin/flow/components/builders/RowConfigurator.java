package com.holonplatform.vaadin.flow.components.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

public interface RowConfigurator<C> {

    C add(Component... components);

    C add(ColumnBuilder... columnBuilders);

    C remove(Component... components);
    Div build();

}
