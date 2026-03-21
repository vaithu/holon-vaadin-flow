package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultSideNavBuilder;
import com.vaadin.flow.component.sidenav.SideNav;

/**
 * SideNav builder.
 */
public interface SideNavBuilder
        extends SideNavConfigurator<SideNavBuilder>,
                ComponentBuilder<SideNav, SideNavBuilder> {

    /**
     * Create a new {@link SideNav} builder.
     *
     * @return a new {@link SideNavBuilder}
     */
    static SideNavBuilder create() {
        return new DefaultSideNavBuilder(new SideNav());
    }

    /**
     * Configure an existing {@link SideNav}.
     *
     * @param sideNav the SideNav instance (not null)
     * @return a {@link SideNavBuilder}
     */
    static SideNavBuilder configure(SideNav sideNav) {
        return new DefaultSideNavBuilder(sideNav);
    }
}