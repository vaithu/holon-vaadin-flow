package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.DefaultXPanel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;

public interface ColumnConfigurator<C> extends GridSizeConfigurator<C>, BootstrapColumnSizeConfigurator<C>{

    C column(String... styleNames);

    C add(DefaultXPanel xPanel);

    C add(Component... components);



    Div build();
}
