package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.SideNavConfigurator;
import com.iyensoft.vaadin.flow.components.builders.SideNavItemBuilder;
import com.vaadin.flow.component.sidenav.SideNavItem;

abstract class AbstractSideNavItemBuilder<C extends SideNavConfigurator<C>>
        extends AbstractSideNavItemConfigurator<SideNavItemBuilder<C>>
        implements SideNavItemBuilder<C> {

    protected final C parent;

    protected AbstractSideNavItemBuilder(C parent, SideNavItem rootItem) {
        super(rootItem);
        this.parent = parent;
    }

    @Override
    protected SideNavItemBuilder<C> getConfigurator() {
        return this;
    }

    @Override
    public C add() {
        parent.withItem(rootItem);
        return parent;
    }
}
