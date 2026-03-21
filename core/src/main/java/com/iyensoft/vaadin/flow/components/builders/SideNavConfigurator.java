package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RouteParameters;

import java.util.List;

public interface SideNavConfigurator<C extends SideNavConfigurator<C>>
        extends ComponentConfigurator<C>,
        HasStyleConfigurator<C>,
        HasSizeConfigurator<C> {

    /* ---------- SideNav operations ---------- */

    C addItem(SideNavItem... items);

    C addItemAsFirst(SideNavItem item);

    C addItemAtIndex(int index, SideNavItem item);

    C collapsible(boolean collapsible);

    C expanded(boolean expanded);

    C label(String label);

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

    /**
     * Filter navigation items using a recursive strategy.
     *
     * @param filter the filter text
     * @param includeUnauthorized whether unauthorized items should be considered
     *//*
    void filterRecursive(String filter, boolean includeUnauthorized);

    VerticalLayout getFilterLayout();

    *//**
     * Adds a filter TextField above the SideNav.
     *//*
    SideNavConfigurator<C> withFilterField();



    *//**
     * Adds a filter TextField above the SideNav using the given mode.
     *//*
    SideNavConfigurator<C> withFilterField(FilterMode mode);

    public enum FilterMode {
        SIMPLE,                 // uses filter(String)
        RECURSIVE,              // filterRecursive(text, false)
        RECURSIVE_WITH_AUTH     // filterRecursive(text, true)
    }*/
}