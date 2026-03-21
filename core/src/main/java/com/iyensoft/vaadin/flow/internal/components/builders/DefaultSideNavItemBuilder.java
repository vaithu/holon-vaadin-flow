package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.SideNavConfigurator;
import com.vaadin.flow.component.sidenav.SideNavItem;

public class DefaultSideNavItemBuilder
        extends AbstractSideNavItemBuilder {

    public DefaultSideNavItemBuilder(
            SideNavConfigurator<?> parent,
            SideNavItem item
    ) {
        super(parent, item);
    }
}