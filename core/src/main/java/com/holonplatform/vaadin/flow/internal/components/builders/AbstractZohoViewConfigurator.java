package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ZohoViewConfigurator;
import com.holonplatform.vaadin.flow.internal.lumo.FlexDirection;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;

public abstract class AbstractZohoViewConfigurator<C extends ZohoViewConfigurator<C>>
        implements ZohoViewConfigurator<C> {

    /**
     * Constructor.
     *
     * @param layout The layout instance (not null)
     */
    public AbstractZohoViewConfigurator(Layout layout) {

        layout.setId("Root Layout");
        layout.setSizeFull();
        layout.setFlexDirection(FlexDirection.ROW);
        layout.addClassName("iven-view-root");
    }

}
