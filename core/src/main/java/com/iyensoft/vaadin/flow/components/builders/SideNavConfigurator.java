package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultSideNavConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RouteParameters;

import java.util.List;

public interface SideNavConfigurator<C extends SideNavConfigurator<C>>
        extends ComponentConfigurator<C>,
        HasStyleConfigurator<C>,
        HasSizeConfigurator<C> {

    /* ---------- SideNav operations ---------- */

    C withItem(SideNavItem... items);

    C withItemAsFirst(SideNavItem item);

    C withItemAtIndex(int index, SideNavItem item);

    C collapsible(boolean collapsible);

    C expanded(boolean expanded);

    C label(String label);

    /**
     * Sets the navigation group label from a {@link Localizable} descriptor.
     *
     * @param label localizable group label (not null)
     * @return this configurator
     */
    C label(Localizable label);

    List<SideNavItem> getItems();

    void filter(String filter);

    /* ---------- Item builders ---------- */

    SideNavItemBuilder withNavItem(String label);

    SideNavItemBuilder withNavItem(String label, Class<? extends Component> view);

    SideNavItemBuilder withNavItem(
            String label,
            Class<? extends Component> view,
            Component prefixComponent
    );

    SideNavItemBuilder withNavItem(
            String label,
            Class<? extends Component> view,
            RouteParameters routeParameters
    );

    SideNavItemBuilder withNavItem(
            String label,
            Class<? extends Component> view,
            RouteParameters routeParameters,
            Component prefixComponent
    );

    SideNavItemBuilder withNavItem(String label, String path);

    SideNavItemBuilder withNavItem(
            String label,
            String path,
            Component prefixComponent
    );

    // ── Localizable withNavItem overloads ─────────────────────────────────────

    /** Creates a nav item with a localizable label. */
    SideNavItemBuilder withNavItem(Localizable label);

    /** Creates a nav item with a localizable label navigating to the given view. */
    SideNavItemBuilder withNavItem(Localizable label, Class<? extends Component> view);

    /** Creates a nav item with a localizable label, view, and prefix component. */
    SideNavItemBuilder withNavItem(
            Localizable label,
            Class<? extends Component> view,
            Component prefixComponent
    );

    /** Creates a nav item with a localizable label, view, and route parameters. */
    SideNavItemBuilder withNavItem(
            Localizable label,
            Class<? extends Component> view,
            RouteParameters routeParameters
    );

    /** Creates a nav item with a localizable label, view, route parameters, and prefix component. */
    SideNavItemBuilder withNavItem(
            Localizable label,
            Class<? extends Component> view,
            RouteParameters routeParameters,
            Component prefixComponent
    );

    /** Creates a nav item with a localizable label and explicit path string. */
    SideNavItemBuilder withNavItem(Localizable label, String path);

    /** Creates a nav item with a localizable label, path, and prefix component. */
    SideNavItemBuilder withNavItem(
            Localizable label,
            String path,
            Component prefixComponent
    );

    /**
     * Adds a search {@link com.vaadin.flow.component.textfield.TextField} above the navigation
     * with the default placeholder "Search…".
     * The field filters items recursively (parent is shown when a child matches).
     * Use {@code SideNavBuilder.buildWrapper()} to obtain the composite component.
     */
    C withSearch();

    /**
     * Adds a search field with a custom placeholder text.
     * Use {@code SideNavBuilder.buildWrapper()} to obtain the composite component.
     *
     * @param placeholder placeholder shown inside the empty field
     */
    C withSearch(String placeholder);

    /**
     * Adds a search field with a localizable placeholder.
     *
     * @param placeholder localizable placeholder (not null)
     * @return this configurator
     */
    C withSearch(Localizable placeholder);

    /**
     * Adds a collapse/expand toggle button at the bottom of the navigation host.
     * When collapsed, only prefix icons are visible; labels, group headers, and
     * the search field are hidden via CSS.
     * Use {@code SideNavBuilder.buildWrapper()} to obtain the composite component.
     */
    C withCollapse();

    // ── configure factory ─────────────────────────────────────────────────────────────

    /**
     * Configure an existing {@link SideNav} instance using the fluent configurator API.
     *
     * @param sideNav the SideNav instance to configure (not null)
     * @return a {@link BaseSideNavConfigurator}
     */
    static BaseSideNavConfigurator configure(SideNav sideNav) {
        return new DefaultSideNavConfigurator(sideNav);
    }

    /**
     * Base (non-building) configurator for an existing {@link SideNav}.
     */
    interface BaseSideNavConfigurator extends SideNavConfigurator<BaseSideNavConfigurator> {
    }
}