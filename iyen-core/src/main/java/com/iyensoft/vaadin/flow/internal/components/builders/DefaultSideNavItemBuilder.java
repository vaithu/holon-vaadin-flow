package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.SideNavConfigurator;
import com.vaadin.flow.component.sidenav.SideNavItem;

public class DefaultSideNavItemBuilder<C extends SideNavConfigurator<C>>
        extends AbstractSideNavItemBuilder<C> {

    public DefaultSideNavItemBuilder(C parent, SideNavItem rootItem) {
        super(parent, rootItem);
    }
}
