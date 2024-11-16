package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultSideNavBuilder;
import com.vaadin.flow.component.sidenav.SideNav;

public interface SideNavBuilder extends SideNavConfigurator<SideNavBuilder>,
        ComponentBuilder<SideNav, SideNavBuilder> {


    static SideNavBuilder create() {
        return configure(new SideNav());
    }

    static SideNavBuilder configure(SideNav sideNav) {
        return new DefaultSideNavBuilder(sideNav);
    }


}
