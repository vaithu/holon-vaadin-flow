package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.SideNavItemConfigurator;
import com.vaadin.flow.component.sidenav.SideNavItem;

/**
 * Default {@link SideNavItemConfigurator.BaseSideNavItemConfigurator} implementation
 * that configures an existing {@link SideNavItem}.
 */
public class DefaultSideNavItemConfigurator
        extends AbstractSideNavItemConfigurator<SideNavItemConfigurator.BaseSideNavItemConfigurator>
        implements SideNavItemConfigurator.BaseSideNavItemConfigurator {

    public DefaultSideNavItemConfigurator(SideNavItem item) {
        super(item);
    }

    @Override
    protected SideNavItemConfigurator.BaseSideNavItemConfigurator getConfigurator() {
        return this;
    }
}
