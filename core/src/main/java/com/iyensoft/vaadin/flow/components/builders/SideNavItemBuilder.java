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

/**
 * Fluent builder for a single {@link SideNavItem}.
 *
 * <p>The type parameter {@code C} is the parent {@link SideNavConfigurator} that created
 * this builder.  {@link #add()} registers the item with the parent and returns {@code C},
 * so the full builder chain retains its concrete type all the way to
 * {@code build()} / {@code buildWrapper()}.</p>
 *
 * <p>Example (full type is preserved — no cast needed):</p>
 * <pre>{@code
 * Div nav = SideNavBuilder.create()
 *     .withSearch("Filter…")
 *     .withCollapse()
 *     .withNavItem("Products", ProductListView.class, VaadinIcon.PACKAGE.create()).add()
 *     .withNavItem("Customers", CustomerListView.class, VaadinIcon.MALE.create()).add()
 *     .buildWrapper();
 * }</pre>
 *
 * @param <C> the concrete parent {@link SideNavConfigurator} type
 */
public interface SideNavItemBuilder<C extends SideNavConfigurator<C>>
        extends HasEnabledConfigurator<SideNavItemBuilder<C>>,
        HasPrefixAndSuffixConfigurator<SideNavItemBuilder<C>> {

    SideNavItemBuilder<C> expanded(boolean expanded);

    SideNavItemBuilder<C> label(String label);

    /**
     * Sets the item label from a {@link Localizable} descriptor.
     *
     * @param label localizable item label (not null)
     * @return this builder
     */
    SideNavItemBuilder<C> label(Localizable label);

    SideNavItemBuilder<C> matchNested(boolean value);

    SideNavItemBuilder<C> openInNewBrowserTab(boolean open);

    SideNavItemBuilder<C> path(Class<? extends Component> view);

    SideNavItemBuilder<C> path(String path);

    SideNavItemBuilder<C> path(
            Class<? extends Component> view,
            RouteParameters routeParameters
    );

    SideNavItemBuilder<C> pathAliases(Set<String> pathAliases);

    SideNavItemBuilder<C> queryParameters(QueryParameters queryParameters);

    SideNavItemBuilder<C> routerIgnore(boolean ignore);

    SideNavItemBuilder<C> target(String target);

    /* ---------- Child items ---------- */

    SideNavItemBuilder<C> withItems(SideNavItem... items);

    SideNavItemBuilder<C> withItemAsFirst(SideNavItem item);

    SideNavItemBuilder<C> withItemAtIndex(int index, SideNavItem item);

    SideNavItemBuilder<C> withSubNavItem(String label);

    SideNavItemBuilder<C> withSubNavItem(
            String label,
            Class<? extends Component> view
    );

    SideNavItemBuilder<C> withSubNavItem(
            String label,
            Class<? extends Component> view,
            Component prefixComponent
    );

    SideNavItemBuilder<C> withSubNavItem(
            String label,
            Class<? extends Component> view,
            RouteParameters routeParameters
    );

    SideNavItemBuilder<C> withSubNavItem(
            String label,
            Class<? extends Component> view,
            RouteParameters routeParameters,
            Component prefixComponent
    );

    SideNavItemBuilder<C> withSubNavItem(String label, String path);

    SideNavItemBuilder<C> withSubNavItem(
            String label,
            String path,
            Component prefixComponent
    );

    // ── Localizable withSubNavItem overloads ──────────────────────────────────

    /** Adds a child item with a localizable label. */
    SideNavItemBuilder<C> withSubNavItem(Localizable label);

    /** Adds a child item with a localizable label and view route. */
    SideNavItemBuilder<C> withSubNavItem(Localizable label, Class<? extends Component> view);

    /** Adds a child item with a localizable label, view, and prefix component. */
    SideNavItemBuilder<C> withSubNavItem(Localizable label, Class<? extends Component> view, Component prefixComponent);

    /** Adds a child item with a localizable label, view, and route parameters. */
    SideNavItemBuilder<C> withSubNavItem(Localizable label, Class<? extends Component> view, RouteParameters routeParameters);

    /** Adds a child item with a localizable label, view, route parameters, and prefix component. */
    SideNavItemBuilder<C> withSubNavItem(
            Localizable label,
            Class<? extends Component> view,
            RouteParameters routeParameters,
            Component prefixComponent
    );

    /** Adds a child item with a localizable label and explicit path string. */
    SideNavItemBuilder<C> withSubNavItem(Localizable label, String path);

    /** Adds a child item with a localizable label, path, and prefix component. */
    SideNavItemBuilder<C> withSubNavItem(Localizable label, String path, Component prefixComponent);

    List<SideNavItem> getItems();

    /* ---------- Attach to SideNav ---------- */

    /**
     * Registers the built item with the parent {@link SideNavConfigurator} and returns it,
     * preserving the concrete parent type in the call chain.
     *
     * @return the parent configurator (same instance that created this builder)
     */
    C add();
}
