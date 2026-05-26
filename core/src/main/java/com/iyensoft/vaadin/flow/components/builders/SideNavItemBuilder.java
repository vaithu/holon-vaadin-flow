package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasPrefixAndSuffixConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteParameters;

import java.util.List;
import java.util.Set;

public interface SideNavItemBuilder
        extends HasEnabledConfigurator<SideNavItemBuilder>,
        HasPrefixAndSuffixConfigurator<SideNavItemBuilder> {

    SideNavItemBuilder expanded(boolean expanded);

    SideNavItemBuilder label(String label);

    /**
     * Sets the item label from a {@link Localizable} descriptor.
     *
     * @param label localizable item label (not null)
     * @return this builder
     */
    SideNavItemBuilder label(Localizable label);

    SideNavItemBuilder matchNested(boolean value);

    SideNavItemBuilder openInNewBrowserTab(boolean open);

    SideNavItemBuilder path(Class<? extends Component> view);

    SideNavItemBuilder path(String path);

    SideNavItemBuilder path(
            Class<? extends Component> view,
            RouteParameters routeParameters
    );

    SideNavItemBuilder pathAliases(Set<String> pathAliases);

    SideNavItemBuilder queryParameters(QueryParameters queryParameters);

    SideNavItemBuilder routerIgnore(boolean ignore);

    SideNavItemBuilder target(String target);

    /* ---------- Child items ---------- */

    SideNavItemBuilder withItems(SideNavItem... items);

    SideNavItemBuilder withItemAsFirst(SideNavItem item);

    SideNavItemBuilder withItemAtIndex(int index, SideNavItem item);

    SideNavItemBuilder withSubNavItem(String label);

    SideNavItemBuilder withSubNavItem(
            String label,
            Class<? extends Component> view
    );

    SideNavItemBuilder withSubNavItem(
            String label,
            Class<? extends Component> view,
            Component prefixComponent
    );

    SideNavItemBuilder withSubNavItem(
            String label,
            Class<? extends Component> view,
            RouteParameters routeParameters
    );

    SideNavItemBuilder withSubNavItem(
            String label,
            Class<? extends Component> view,
            RouteParameters routeParameters,
            Component prefixComponent
    );

    SideNavItemBuilder withSubNavItem(String label, String path);

    SideNavItemBuilder withSubNavItem(
            String label,
            String path,
            Component prefixComponent
    );

    // ── Localizable withSubNavItem overloads ──────────────────────────────────

    /** Adds a child item with a localizable label. */
    SideNavItemBuilder withSubNavItem(Localizable label);

    /** Adds a child item with a localizable label and view route. */
    SideNavItemBuilder withSubNavItem(Localizable label, Class<? extends Component> view);

    /** Adds a child item with a localizable label, view, and prefix component. */
    SideNavItemBuilder withSubNavItem(Localizable label, Class<? extends Component> view, Component prefixComponent);

    /** Adds a child item with a localizable label, view, and route parameters. */
    SideNavItemBuilder withSubNavItem(Localizable label, Class<? extends Component> view, RouteParameters routeParameters);

    /** Adds a child item with a localizable label, view, route parameters, and prefix component. */
    SideNavItemBuilder withSubNavItem(
            Localizable label,
            Class<? extends Component> view,
            RouteParameters routeParameters,
            Component prefixComponent
    );

    /** Adds a child item with a localizable label and explicit path string. */
    SideNavItemBuilder withSubNavItem(Localizable label, String path);

    /** Adds a child item with a localizable label, path, and prefix component. */
    SideNavItemBuilder withSubNavItem(Localizable label, String path, Component prefixComponent);

//    SideNavItemBuilder authorizedWhen(Permission... permissions);

    List<SideNavItem> getItems();

    /* ---------- Attach to SideNav ---------- */

    SideNavConfigurator<?> add();
}