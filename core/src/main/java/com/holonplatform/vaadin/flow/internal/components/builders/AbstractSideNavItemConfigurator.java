package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.SideNavConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteParameters;

import java.util.List;
import java.util.Set;

public abstract class AbstractSideNavItemConfigurator<B extends SideNavConfigurator<B>>
         implements SideNavConfigurator.SideNavItemBuilder<B> {

    private final B parentBuilder;
    private SideNavItem sideNavItem;




    /**
     * Constructor.
     *
     * @param parentBuilder The parentBuilder instance (not null)
     * @param sideNavItem The sideNavItem instance (not null)
     */
    public AbstractSideNavItemConfigurator(B parentBuilder,SideNavItem sideNavItem) {
        ObjectUtils.argumentNotNull(parentBuilder, "Parent builder must be not null");
        ObjectUtils.argumentNotNull(sideNavItem, "SideNavItem must be not null");
        this.parentBuilder = parentBuilder;
        this.sideNavItem = sideNavItem;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> expanded(boolean expanded) {
        sideNavItem.setExpanded(expanded);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> label(String label) {
        sideNavItem.setLabel(label);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> matchNested(boolean value) {
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> openInNewBrowserTab(boolean openInNewBrowserTab) {
        sideNavItem.setOpenInNewBrowserTab(openInNewBrowserTab);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> path(Class<? extends Component> view) {
        sideNavItem.setPath(view);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> path(String path) {
        sideNavItem.setPath(path);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> path(Class<? extends Component> view, RouteParameters routeParameters) {
        sideNavItem.setPath(view, routeParameters);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> pathAliases(Set<String> pathAliases) {
        sideNavItem.setPathAliases(pathAliases);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> queryParameters(QueryParameters queryParameters) {
        sideNavItem.setQueryParameters(queryParameters);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> routerIgnore(boolean ignore) {
        sideNavItem.setRouterIgnore(ignore);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> target(String target) {
        sideNavItem.setTarget(target);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> withItem(SideNavItem... items) {
        sideNavItem.addItem(items);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> withItemAsFirst(SideNavItem item) {
        sideNavItem.addItemAsFirst(item);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> withItemAtIndex(int index, SideNavItem item) {
        sideNavItem.addItemAtIndex(index, item);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> withSubNavItem(String label) {
        sideNavItem = new SideNavItem(label);
        parentBuilder.addItem(sideNavItem);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> withSubNavItem(String label, Class<? extends Component> view) {
        sideNavItem = new SideNavItem(label, view);
        parentBuilder.addItem(sideNavItem);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> withSubNavItem(String label, Class<? extends Component> view, Component prefixComponent) {
        sideNavItem = new SideNavItem(label, view, prefixComponent);
        parentBuilder.addItem(sideNavItem);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> withSubNavItem(String label, Class<? extends Component> view, RouteParameters routeParameters) {
        sideNavItem = new SideNavItem(label, view, routeParameters);
        parentBuilder.addItem(sideNavItem);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> withSubNavItem(String label, Class<? extends Component> view, RouteParameters routeParameters, Component prefixComponent) {
        sideNavItem = new SideNavItem(label, view, routeParameters, prefixComponent);
        parentBuilder.addItem(sideNavItem);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> withSubNavItem(String label, String path) {
        sideNavItem = new SideNavItem(label, path);
        parentBuilder.addItem(sideNavItem);
        return this;
    }

    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> withSubNavItem(String label, String path, Component prefixComponent) {
        sideNavItem = new SideNavItem(label, path, prefixComponent);
        parentBuilder.addItem(sideNavItem);
        return this;
    }

    @Override
    public List<SideNavItem> getItems() {
        return sideNavItem.getItems();
    }

    /**
     * Add the SideNavItem to the SideNav.
     *
     * @return The parent SideNav builder
     */
    @Override
    public B add() {
        return parentBuilder;
    }

    /**
     * Set whether the component is enabled.
     *
     * @param enabled if <code>false</code> then explicitly disables the object, if <code>true</code> then enables the
     *                object so that its state depends on parent
     * @return this
     */
    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> enabled(boolean enabled) {
        throw new IllegalArgumentException("Not implemented yet");
    }

    /**
     * Adds the given component into this field before the content, replacing any existing prefix component.
     *
     * @param component the component to set, can be <code>null</code> to remove existing prefix component
     * @return this
     */
    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> prefixComponent(Component component) {
        sideNavItem.setPrefixComponent(component);
        return this;
    }

    /**
     * Adds the given component into this field after the content, replacing any existing suffix component.
     *
     * @param component the component to set, can be <code>null</code> to remove existing suffix component
     * @return this
     */
    @Override
    public SideNavConfigurator.SideNavItemBuilder<B> suffixComponent(Component component) {
        sideNavItem.setSuffixComponent(component);
        return this;
    }
}
