package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.SideNavBuilder;
import com.vaadin.flow.component.sidenav.SideNav;

public final class DefaultSideNavBuilder
        extends AbstractSideNavConfigurator<SideNavBuilder>
        implements SideNavBuilder {

    public DefaultSideNavBuilder(SideNav sideNav) {
        super(sideNav);
    }

    @Override
    protected SideNavBuilder getConfigurator() {
        return this;
    }

    @Override
    public SideNav build() {
        return sideNav;
    }
}