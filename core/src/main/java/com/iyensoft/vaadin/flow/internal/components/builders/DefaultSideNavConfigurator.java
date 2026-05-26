package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.SideNavConfigurator;
import com.vaadin.flow.component.sidenav.SideNav;

/**
 * Default implementation of {@link SideNavConfigurator.BaseSideNavConfigurator}
 * for configuring an existing {@link SideNav} instance without building a new one.
 *
 * <p>Use {@link SideNavConfigurator#configure(SideNav)} to obtain an instance.</p>
 */
public class DefaultSideNavConfigurator
        extends AbstractSideNavConfigurator<SideNavConfigurator.BaseSideNavConfigurator>
        implements SideNavConfigurator.BaseSideNavConfigurator {

    public DefaultSideNavConfigurator(SideNav sideNav) {
        super(sideNav);
    }

    @Override
    protected SideNavConfigurator.BaseSideNavConfigurator getConfigurator() {
        return this;
    }
}

