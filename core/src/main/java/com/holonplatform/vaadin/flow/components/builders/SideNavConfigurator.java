package com.holonplatform.vaadin.flow.components.builders;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteParameters;

import java.util.List;
import java.util.Set;

public interface SideNavConfigurator<C extends SideNavConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasSizeConfigurator<C>, HasElementConfigurator<C> {

    C addItem(SideNavItem... items);

    C addItemAsFirst(SideNavItem item);

    C addItemAtIndex(int index,
                     SideNavItem item);

    C collapsible(boolean collapsible);

    C expanded(boolean expanded);

    C label(String label);

    List<SideNavItem>
    getItems();

    void filter(String filter);

    SideNavItemBuilder<C> withNavItem(String  label);
    SideNavItemBuilder<C> withNavItem(String  label, Class<? extends Component>  view);
    SideNavItemBuilder<C> withNavItem(String  label, Class<? extends Component>  view, Component  prefixComponent);
    SideNavItemBuilder<C> withNavItem(String  label, Class<? extends Component>  view, RouteParameters  routeParameters);
    SideNavItemBuilder<C> withNavItem(String  label, Class<? extends Component>  view, RouteParameters  routeParameters, Component  prefixComponent);
    SideNavItemBuilder<C> withNavItem(String  label, String  path);

    SideNavItemBuilder<C> withNavItem(String label, String path, Component prefixComponent);

    interface BaseSideNavConfigurator extends SideNavConfigurator<BaseSideNavConfigurator> {

    }

    interface SideNavItemBuilder<B extends SideNavConfigurator<B>> extends HasEnabledConfigurator<SideNavItemBuilder<B>>
            , HasPrefixAndSuffixConfigurator<SideNavItemBuilder<B>> {


        SideNavItemBuilder<B> expanded(boolean expanded);

        SideNavItemBuilder<B> label(String label);

        SideNavItemBuilder<B> matchNested(boolean value);

        SideNavItemBuilder<B> openInNewBrowserTab(boolean openInNewBrowserTab);

        SideNavItemBuilder<B> path(Class<? extends Component> view);

        SideNavItemBuilder<B> path(String path);

        SideNavItemBuilder<B> path(Class<? extends Component> view,
                                   RouteParameters routeParameters);

        SideNavItemBuilder<B> pathAliases(Set<String> pathAliases);

        SideNavItemBuilder<B> queryParameters(QueryParameters queryParameters);

        SideNavItemBuilder<B> routerIgnore(boolean ignore);

        SideNavItemBuilder<B> target(String target);

        SideNavItemBuilder<B> withItem(SideNavItem... items);

        SideNavItemBuilder<B> withItemAsFirst(SideNavItem item);

        SideNavItemBuilder<B> withItemAtIndex(int index,
                                              SideNavItem item);

        SideNavItemBuilder<B> withSubNavItem(String  label);
        SideNavItemBuilder<B> withSubNavItem(String  label, Class<? extends Component>  view);
        SideNavItemBuilder<B> withSubNavItem(String  label, Class<? extends Component>  view, Component  prefixComponent);
        SideNavItemBuilder<B> withSubNavItem(String  label, Class<? extends Component>  view, RouteParameters  routeParameters);
        SideNavItemBuilder<B> withSubNavItem(String  label, Class<? extends Component>  view, RouteParameters  routeParameters, Component  prefixComponent);
        SideNavItemBuilder<B> withSubNavItem(String  label, String  path);
        SideNavItemBuilder<B> withSubNavItem(String  label, String  path, Component  prefixComponent);

        List<SideNavItem>
        getItems();


        /**
         * Add the SideNavItem to the SideNav.
         *
         * @return The parent SideNav builder
         */
        B add();

        interface BaseSideNavItemBuilder extends SideNavConfigurator<BaseSideNavItemBuilder> {

        }
    }


}
