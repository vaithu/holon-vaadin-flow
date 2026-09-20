package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultSideNavBuilder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.sidenav.SideNav;

/**
 * SideNav builder.
 *
 * <p>Use {@link #build()} to get a plain {@link SideNav}, or
 * {@link #buildWrapper()} to get a composite {@link Div} that includes
 * the optional search field and collapse toggle configured via
 * {@link SideNavConfigurator#withSearch()} and {@link SideNavConfigurator#withCollapse()}.
 */
public interface SideNavBuilder
        extends SideNavConfigurator<SideNavBuilder>,
                ComponentBuilder<SideNav, SideNavBuilder> {

    /**
     * Builds a wrapper {@link Div} (CSS class {@code sidenav-host}) that contains:
     * <ul>
     *   <li>an optional search {@link com.vaadin.flow.component.textfield.TextField}
     *       at the top (enabled via {@link #withSearch()})</li>
     *   <li>the {@link SideNav}</li>
     *   <li>an optional collapse/expand toggle
     *       {@link com.vaadin.flow.component.button.Button} at the bottom
     *       (enabled via {@link #withCollapse()})</li>
     * </ul>
     *
     * <p>Visual behaviour of all states is fully driven by {@code menu.css}.
     *
     * @return the composite host {@link Div}
     */
    Div buildWrapper();

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

