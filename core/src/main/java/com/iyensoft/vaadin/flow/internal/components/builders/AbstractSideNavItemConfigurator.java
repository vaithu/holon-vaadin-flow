package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.iyensoft.vaadin.flow.components.builders.SideNavItemConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteParameters;

import java.util.List;
import java.util.Set;

/**
 * Base {@link SideNavItemConfigurator} implementation holding the root {@link SideNavItem}.
 *
 * @param <B> Concrete configurator type
 */
abstract class AbstractSideNavItemConfigurator<B extends SideNavItemConfigurator<B>>
        implements SideNavItemConfigurator<B> {

    protected final SideNavItem rootItem;

    protected AbstractSideNavItemConfigurator(SideNavItem rootItem) {
        this.rootItem = rootItem;
    }

    protected abstract B getConfigurator();

    // ── Basic ──────────────────────────────────────────────────────────────────

    @Override
    public B expanded(boolean expanded) {
        rootItem.setExpanded(expanded);
        return getConfigurator();
    }

    @Override
    public B label(String label) {
        rootItem.setLabel(label);
        return getConfigurator();
    }

    @Override
    public B label(Localizable label) {
        return label(resolve(label));
    }

    @Override
    public B matchNested(boolean value) {
        rootItem.setMatchNested(value);
        return getConfigurator();
    }

    @Override
    public B openInNewBrowserTab(boolean open) {
        rootItem.setOpenInNewBrowserTab(open);
        return getConfigurator();
    }

    // ── Path ──────────────────────────────────────────────────────────────────

    @Override
    public B path(Class<? extends Component> view) {
        rootItem.setPath(view);
        return getConfigurator();
    }

    @Override
    public B path(String path) {
        rootItem.setPath(path);
        return getConfigurator();
    }

    @Override
    public B path(Class<? extends Component> view, RouteParameters params) {
        rootItem.setPath(view, params);
        return getConfigurator();
    }

    @Override
    public B pathAliases(Set<String> aliases) {
        rootItem.setPathAliases(aliases);
        return getConfigurator();
    }

    @Override
    public B queryParameters(QueryParameters params) {
        rootItem.setQueryParameters(params);
        return getConfigurator();
    }

    @Override
    public B routerIgnore(boolean ignore) {
        rootItem.setRouterIgnore(ignore);
        return getConfigurator();
    }

    @Override
    public B target(String target) {
        rootItem.setTarget(target);
        return getConfigurator();
    }

    // ── Children ──────────────────────────────────────────────────────────────

    @Override
    public B withItems(SideNavItem... items) {
        rootItem.addItem(items);
        return getConfigurator();
    }

    @Override
    public B withItemAsFirst(SideNavItem child) {
        rootItem.addItemAsFirst(child);
        return getConfigurator();
    }

    @Override
    public B withItemAtIndex(int index, SideNavItem child) {
        rootItem.addItemAtIndex(index, child);
        return getConfigurator();
    }

    @Override
    public B withSubNavItem(String label) {
        rootItem.addItem(new SideNavItem(label));
        return getConfigurator();
    }

    @Override
    public B withSubNavItem(String label, Class<? extends Component> view) {
        rootItem.addItem(new SideNavItem(label, view));
        return getConfigurator();
    }

    @Override
    public B withSubNavItem(String label, Class<? extends Component> view, Component prefixComponent) {
        rootItem.addItem(new SideNavItem(label, view, prefixComponent));
        return getConfigurator();
    }

    @Override
    public B withSubNavItem(String label, Class<? extends Component> view, RouteParameters params) {
        rootItem.addItem(new SideNavItem(label, view, params));
        return getConfigurator();
    }

    @Override
    public B withSubNavItem(String label, Class<? extends Component> view, RouteParameters params,
            Component prefixComponent) {
        rootItem.addItem(new SideNavItem(label, view, params, prefixComponent));
        return getConfigurator();
    }

    @Override
    public B withSubNavItem(String label, String path) {
        rootItem.addItem(new SideNavItem(label, path));
        return getConfigurator();
    }

    @Override
    public B withSubNavItem(String label, String path, Component prefixComponent) {
        rootItem.addItem(new SideNavItem(label, path, prefixComponent));
        return getConfigurator();
    }

    // ── Localizable withSubNavItem overloads ──────────────────────────────────

    @Override
    public B withSubNavItem(Localizable label) {
        return withSubNavItem(resolve(label));
    }

    @Override
    public B withSubNavItem(Localizable label, Class<? extends Component> view) {
        return withSubNavItem(resolve(label), view);
    }

    @Override
    public B withSubNavItem(Localizable label, Class<? extends Component> view, Component prefixComponent) {
        return withSubNavItem(resolve(label), view, prefixComponent);
    }

    @Override
    public B withSubNavItem(Localizable label, Class<? extends Component> view, RouteParameters params) {
        return withSubNavItem(resolve(label), view, params);
    }

    @Override
    public B withSubNavItem(Localizable label, Class<? extends Component> view, RouteParameters params,
            Component prefixComponent) {
        return withSubNavItem(resolve(label), view, params, prefixComponent);
    }

    @Override
    public B withSubNavItem(Localizable label, String path) {
        return withSubNavItem(resolve(label), path);
    }

    @Override
    public B withSubNavItem(Localizable label, String path, Component prefixComponent) {
        return withSubNavItem(resolve(label), path, prefixComponent);
    }

    // ── HasEnabledConfigurator ─────────────────────────────────────────────────

    @Override
    public B enabled(boolean enabled) {
        rootItem.setEnabled(enabled);
        return getConfigurator();
    }

    // ── HasPrefixAndSuffixConfigurator ─────────────────────────────────────────

    @Override
    public B prefixComponent(Component component) {
        rootItem.setPrefixComponent(component);
        return getConfigurator();
    }

    @Override
    public B suffixComponent(Component component) {
        rootItem.setSuffixComponent(component);
        return getConfigurator();
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    public List<SideNavItem> getItems() {
        return rootItem.getItems();
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    protected static String resolve(Localizable l) {
        return LocalizationProvider.localize(l)
                .orElseGet(() -> l.getMessage() != null ? l.getMessage() : "");
    }
}
