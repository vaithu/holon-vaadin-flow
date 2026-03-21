package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.SideNavBuilder;
import com.vaadin.flow.component.sidenav.SideNav;

public class DefaultSideNavBuilder
        extends AbstractSideNavConfigurator<SideNavBuilder>
        implements SideNavBuilder {
    public DefaultSideNavBuilder(SideNav sideNav) {
        super(sideNav);

    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected SideNavBuilder getConfigurator() {
        return this;
    }

    /**
     * Build and returns the component.
     *
     * @return The component instance
     */
    @Override
    public SideNav build() {
        return getComponent();
    }
}
