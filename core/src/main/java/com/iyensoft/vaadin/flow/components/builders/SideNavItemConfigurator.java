package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasPrefixAndSuffixConfigurator;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultSideNavItemConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteParameters;

import java.util.List;
import java.util.Set;

/**
 * Configurator for a single {@link SideNavItem}.
 *
 * @param <B> Concrete configurator type (for fluent chaining)
 */
public interface SideNavItemConfigurator<B extends SideNavItemConfigurator<B>>
        extends HasEnabledConfigurator<B>, HasPrefixAndSuffixConfigurator<B> {

    B expanded(boolean expanded);

    B label(String label);

    B label(Localizable label);

    B matchNested(boolean value);

    B openInNewBrowserTab(boolean open);

    B path(Class<? extends Component> view);

    B path(String path);

    B path(Class<? extends Component> view, RouteParameters routeParameters);

    B pathAliases(Set<String> pathAliases);

    B queryParameters(QueryParameters queryParameters);

    B routerIgnore(boolean ignore);

    B target(String target);

    B withItems(SideNavItem... items);

    B withItemAsFirst(SideNavItem item);

    B withItemAtIndex(int index, SideNavItem item);

    B withSubNavItem(String label);

    B withSubNavItem(String label, Class<? extends Component> view);

    B withSubNavItem(String label, Class<? extends Component> view, Component prefixComponent);

    B withSubNavItem(String label, Class<? extends Component> view, RouteParameters routeParameters);

    B withSubNavItem(String label, Class<? extends Component> view, RouteParameters routeParameters,
            Component prefixComponent);

    B withSubNavItem(String label, String path);

    B withSubNavItem(String label, String path, Component prefixComponent);

    B withSubNavItem(Localizable label);

    B withSubNavItem(Localizable label, Class<? extends Component> view);

    B withSubNavItem(Localizable label, Class<? extends Component> view, Component prefixComponent);

    B withSubNavItem(Localizable label, Class<? extends Component> view, RouteParameters routeParameters);

    B withSubNavItem(Localizable label, Class<? extends Component> view, RouteParameters routeParameters,
            Component prefixComponent);

    B withSubNavItem(Localizable label, String path);

    B withSubNavItem(Localizable label, String path, Component prefixComponent);

    List<SideNavItem> getItems();

    /**
     * Configure an existing {@link SideNavItem}.
     *
     * @param item the item to configure (not null)
     * @return a new {@link BaseSideNavItemConfigurator}
     */
    static BaseSideNavItemConfigurator configure(SideNavItem item) {
        return new DefaultSideNavItemConfigurator(item);
    }

    interface BaseSideNavItemConfigurator extends SideNavItemConfigurator<BaseSideNavItemConfigurator> {}
}
