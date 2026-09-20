package com.iyensoft.vaadin.flow.components.builders;

import com.vaadin.flow.component.sidenav.SideNavItem;


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
