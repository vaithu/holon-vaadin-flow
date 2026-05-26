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

abstract class AbstractSideNavItemBuilder implements SideNavItemBuilder {

    protected final SideNavConfigurator<?> parent;
    protected SideNavItem item;

    protected AbstractSideNavItemBuilder(
            SideNavConfigurator<?> parent,
            SideNavItem item
    ) {
        this.parent = parent;
        this.item = item;
    }

    /* ---------- Basic ---------- */

    @Override
    public SideNavItemBuilder expanded(boolean expanded) {
        item.setExpanded(expanded);
        return this;
    }

    @Override
    public SideNavItemBuilder label(String label) {
        item.setLabel(label);
        return this;
    }

    @Override
    public SideNavItemBuilder label(Localizable label) {
        return label(resolve(label));
    }

    @Override
    public SideNavItemBuilder matchNested(boolean value) {
        item.setMatchNested(value);
        return this;
    }

    @Override
    public SideNavItemBuilder openInNewBrowserTab(boolean open) {
        item.setOpenInNewBrowserTab(open);
        return this;
    }

    /* ---------- Path ---------- */

    @Override
    public SideNavItemBuilder path(Class<? extends Component> view) {
        item.setPath(view);
        return this;
    }

    @Override
    public SideNavItemBuilder path(String path) {
        item.setPath(path);
        return this;
    }

    @Override
    public SideNavItemBuilder path(Class<? extends Component> view, RouteParameters params) {
        item.setPath(view, params);
        return this;
    }

    @Override
    public SideNavItemBuilder pathAliases(Set<String> aliases) {
        item.setPathAliases(aliases);
        return this;
    }

    @Override
    public SideNavItemBuilder queryParameters(QueryParameters params) {
        item.setQueryParameters(params);
        return this;
    }

    @Override
    public SideNavItemBuilder routerIgnore(boolean ignore) {
        item.setRouterIgnore(ignore);
        return this;
    }

    @Override
    public SideNavItemBuilder target(String target) {
        item.setTarget(target);
        return this;
    }

    /* ---------- Children ---------- */

    @Override
    public SideNavItemBuilder withItems(SideNavItem... items) {
        item.addItem(items);
        return this;
    }

    @Override
    public SideNavItemBuilder withItemAsFirst(SideNavItem child) {
        item.addItemAsFirst(child);
        return this;
    }

    @Override
    public SideNavItemBuilder withItemAtIndex(int index, SideNavItem child) {
        item.addItemAtIndex(index, child);
        return this;
    }

    @Override
    public SideNavItemBuilder withSubNavItem(String label) {
        SideNavItem child = new SideNavItem(label);
        item.addItem(child);
        this.item = child;
        return this;
    }

    @Override
    public SideNavItemBuilder withSubNavItem(String label, Class<? extends Component> view) {
        SideNavItem child = new SideNavItem(label, view);
        item.addItem(child);
        this.item = child;
        return this;
    }

    @Override
    public SideNavItemBuilder withSubNavItem(String label, Class<? extends Component> view, Component prefixComponent) {
        SideNavItem child = new SideNavItem(label, view, prefixComponent);
        item.addItem(child);
        this.item = child;
        return this;
    }

    @Override
    public SideNavItemBuilder withSubNavItem(String label, Class<? extends Component> view, RouteParameters params) {
        SideNavItem child = new SideNavItem(label, view, params);
        item.addItem(child);
        this.item = child;
        return this;
    }

    @Override
    public SideNavItemBuilder withSubNavItem(
            String label,
            Class<? extends Component> view,
            RouteParameters params,
            Component prefixComponent
    ) {
        SideNavItem child = new SideNavItem(label, view, params, prefixComponent);
        item.addItem(child);
        this.item = child;
        return this;
    }

    @Override
    public SideNavItemBuilder withSubNavItem(String label, String path) {
        SideNavItem child = new SideNavItem(label, path);
        item.addItem(child);
        this.item = child;
        return this;
    }

    @Override
    public SideNavItemBuilder withSubNavItem(String label, String path, Component prefixComponent) {
        SideNavItem child = new SideNavItem(label, path, prefixComponent);
        item.addItem(child);
        this.item = child;
        return this;
    }

    // ── Localizable withSubNavItem overloads ──────────────────────────────────

    @Override
    public SideNavItemBuilder withSubNavItem(Localizable label) {
        return withSubNavItem(resolve(label));
    }

    @Override
    public SideNavItemBuilder withSubNavItem(Localizable label, Class<? extends Component> view) {
        return withSubNavItem(resolve(label), view);
    }

    @Override
    public SideNavItemBuilder withSubNavItem(Localizable label, Class<? extends Component> view, Component prefixComponent) {
        return withSubNavItem(resolve(label), view, prefixComponent);
    }

    @Override
    public SideNavItemBuilder withSubNavItem(Localizable label, Class<? extends Component> view, RouteParameters params) {
        return withSubNavItem(resolve(label), view, params);
    }

    @Override
    public SideNavItemBuilder withSubNavItem(
            Localizable label,
            Class<? extends Component> view,
            RouteParameters params,
            Component prefixComponent
    ) {
        return withSubNavItem(resolve(label), view, params, prefixComponent);
    }

    @Override
    public SideNavItemBuilder withSubNavItem(Localizable label, String path) {
        return withSubNavItem(resolve(label), path);
    }

    @Override
    public SideNavItemBuilder withSubNavItem(Localizable label, String path, Component prefixComponent) {
        return withSubNavItem(resolve(label), path, prefixComponent);
    }

    /* ---------- Read ---------- */

    private static String resolve(Localizable l) {
        return LocalizationProvider.localize(l)
                .orElseGet(() -> l.getMessage() != null ? l.getMessage() : "");
    }

    @Override
    public List<SideNavItem> getItems() {
        return item.getItems();
    }

    /* ---------- Finalize ---------- */
    @Override
    public SideNavConfigurator<?> add() {
        parent.withItem(item);
        return parent;
    }

    @Override
    public SideNavItemBuilder enabled(boolean enabled) {
        item.setEnabled(enabled);
        return this;
    }

    @Override
    public SideNavItemBuilder prefixComponent(Component component) {
        item.setPrefixComponent(component);
        return this;
    }

    @Override
    public SideNavItemBuilder suffixComponent(Component component) {
        item.setSuffixComponent(component);
        return this;
    }
}
