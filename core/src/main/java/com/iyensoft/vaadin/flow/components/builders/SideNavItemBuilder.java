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
        extends SideNavItemConfigurator<SideNavItemBuilder<C>> {

    /**
     * Registers the built item with the parent {@link SideNavConfigurator} and returns it,
     * preserving the concrete parent type in the call chain.
     *
     * @return the parent configurator (same instance that created this builder)
     */
    C add();
}
