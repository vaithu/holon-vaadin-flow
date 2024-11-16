package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.SideNavConfigurator;
import com.vaadin.flow.component.sidenav.SideNavItem;

public class DefaultSideNavItemConfigurator<B extends SideNavConfigurator<B>> extends AbstractSideNavItemConfigurator<B> {
    /**
     * Constructor.
     *
     * @param parentBuilder The parentBuilder instance (not null)
     */
    public DefaultSideNavItemConfigurator(B parentBuilder) {
        this(parentBuilder,new SideNavItem(""));
    }

    /**
     * Constructor.
     *
     * @param parentBuilder The parentBuilder instance (not null)
     * @param sideNavItem   The sideNavItem instance (not null)
     */
    public DefaultSideNavItemConfigurator(B parentBuilder, SideNavItem sideNavItem) {
        super(parentBuilder, sideNavItem);
    }


}
