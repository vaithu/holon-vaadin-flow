package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.iyensoft.vaadin.flow.components.builders.SideNavConfigurator;
import com.iyensoft.vaadin.flow.components.builders.SideNavItemBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteParameters;

import java.util.List;
import java.util.Set;

abstract class AbstractSideNavItemBuilder<C extends SideNavConfigurator<C>>
        implements SideNavItemBuilder<C> {

    protected final C parent;
    /** The root item that will be registered with the SideNav via {@link #add()}. */
    protected final SideNavItem rootItem;

    protected AbstractSideNavItemBuilder(C parent, SideNavItem rootItem) {
        this.parent = parent;
        this.rootItem = rootItem;
    }

    /* ---------- Basic ---------- */

    @Override
    public SideNavItemBuilder<C> expanded(boolean expanded) {
        rootItem.setExpanded(expanded);
        return this;
    }

    @Override
    public SideNavItemBuilder<C> label(String label) {
        rootItem.setLabel(label);
        return this;
    }

    @Override
    public SideNavItemBuilder<C> label(Localizable label) {
        return label(resolve(label));
    }

    @Override
    public SideNavItemBuilder<C> matchNested(boolean value) {
        rootItem.setMatchNested(value);
        return this;
    }

    @Override
    public SideNavItemBuilder<C> openInNewBrowserTab(boolean open) {
        rootItem.setOpenInNewBrowserTab(open);
        return this;
    }

    /* ---------- Path ---------- */

    @Override
    public SideNavItemBuilder<C> path(Class<? extends Component> view) {
        rootItem.setPath(view);
        return this;
    }

    @Override
    public SideNavItemBuilder<C> path(String path) {
        rootItem.setPath(path);
        return this;
    }

    @Override
    public SideNavItemBuilder<C> path(Class<? extends Component> view, RouteParameters params) {
        rootItem.setPath(view, params);
        return this;
    }

    @Override
    public SideNavItemBuilder<C> pathAliases(Set<String> aliases) {
        rootItem.setPathAliases(aliases);
        return this;
    }

    @Override
    public SideNavItemBuilder<C> queryParameters(QueryParameters params) {
        rootItem.setQueryParameters(params);
        return this;
    }

    @Override
    public SideNavItemBuilder<C> routerIgnore(boolean ignore) {
        rootItem.setRouterIgnore(ignore);
        return this;
    }

    @Override
    public SideNavItemBuilder<C> target(String target) {
        rootItem.setTarget(target);
        return this;
    }

    /* ---------- Children ---------- */

    @Override
    public SideNavItemBuilder<C> withItems(SideNavItem... items) {
        rootItem.addItem(items);
        return this;
    }

    @Override
    public SideNavItemBuilder<C> withItemAsFirst(SideNavItem child) {
        rootItem.addItemAsFirst(child);
        return this;
    }

    @Override
    public SideNavItemBuilder<C> withItemAtIndex(int index, SideNavItem child) {
        rootItem.addItemAtIndex(index, child);
        return this;
    }

    @Override
    public SideNavItemBuilder<C> withSubNavItem(String label) {
        rootItem.addItem(new SideNavItem(label));
        return this;
    }

    @Override
    public SideNavItemBuilder<C> withSubNavItem(String label, Class<? extends Component> view) {
        rootItem.addItem(new SideNavItem(label, view));
        return this;
    }

    @Override
    public SideNavItemBuilder<C> withSubNavItem(String label, Class<? extends Component> view, Component prefixComponent) {
        rootItem.addItem(new SideNavItem(label, view, prefixComponent));
        return this;
    }

    @Override
    public SideNavItemBuilder<C> withSubNavItem(String label, Class<? extends Component> view, RouteParameters params) {
        rootItem.addItem(new SideNavItem(label, view, params));
        return this;
    }

    @Override
    public SideNavItemBuilder<C> withSubNavItem(
            String label,
            Class<? extends Component> view,
            RouteParameters params,
            Component prefixComponent
    ) {
        rootItem.addItem(new SideNavItem(label, view, params, prefixComponent));
        return this;
    }

    @Override
    public SideNavItemBuilder<C> withSubNavItem(String label, String path) {
        rootItem.addItem(new SideNavItem(label, path));
        return this;
    }

    @Override
    public SideNavItemBuilder<C> withSubNavItem(String label, String path, Component prefixComponent) {
        rootItem.addItem(new SideNavItem(label, path, prefixComponent));
        return this;
    }

    // ── Localizable withSubNavItem overloads ──────────────────────────────────

    @Override
    public SideNavItemBuilder<C> withSubNavItem(Localizable label) {
        return withSubNavItem(resolve(label));
    }

    @Override
    public SideNavItemBuilder<C> withSubNavItem(Localizable label, Class<? extends Component> view) {
        return withSubNavItem(resolve(label), view);
    }

    @Override
    public SideNavItemBuilder<C> withSubNavItem(Localizable label, Class<? extends Component> view, Component prefixComponent) {
        return withSubNavItem(resolve(label), view, prefixComponent);
    }

    @Override
    public SideNavItemBuilder<C> withSubNavItem(Localizable label, Class<? extends Component> view, RouteParameters params) {
        return withSubNavItem(resolve(label), view, params);
    }

    @Override
    public SideNavItemBuilder<C> withSubNavItem(
            Localizable label,
            Class<? extends Component> view,
            RouteParameters params,
            Component prefixComponent
    ) {
        return withSubNavItem(resolve(label), view, params, prefixComponent);
    }

    @Override
    public SideNavItemBuilder<C> withSubNavItem(Localizable label, String path) {
        return withSubNavItem(resolve(label), path);
    }

    @Override
    public SideNavItemBuilder<C> withSubNavItem(Localizable label, String path, Component prefixComponent) {
        return withSubNavItem(resolve(label), path, prefixComponent);
    }

    /* ---------- Read ---------- */

    private static String resolve(Localizable l) {
        return LocalizationProvider.localize(l)
                .orElseGet(() -> l.getMessage() != null ? l.getMessage() : "");
    }

    @Override
    public List<SideNavItem> getItems() {
        return rootItem.getItems();
    }

    /* ---------- Finalize ---------- */

    /**
     * Adds the root item to the parent configurator and returns the parent,
     * preserving the concrete type {@code C} in the call chain.
     */
    @Override
    public C add() {
        parent.withItem(rootItem);
        return parent;
    }

    @Override
    public SideNavItemBuilder<C> enabled(boolean enabled) {
        rootItem.setEnabled(enabled);
        return this;
    }

    @Override
    public SideNavItemBuilder<C> prefixComponent(Component component) {
        rootItem.setPrefixComponent(component);
        return this;
    }

    @Override
    public SideNavItemBuilder<C> suffixComponent(Component component) {
        rootItem.setSuffixComponent(component);
        return this;
    }
}
