package com.holonplatform.vaadin.flow.components.builders;

import java.util.Optional;

import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultBaseListingBundleConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Fluent post-build configurator for a {@link ListingBundle} — exposes
 * grid-header mutations (via the inherited {@link GridHeaderConfigurator}),
 * a row-click hook, and read-only accessors to the bundle's sub-components.
 *
 * <p>Build-time-only operations such as {@code columns()}, {@code fetch()},
 * {@code pageSizes()}, {@code search()}, {@code withFilterPanel()}, and
 * {@code mobileViewColumn()} live exclusively on
 * {@link com.holonplatform.vaadin.flow.components.ListingBundleBuilder}.</p>
 *
 * @param <T> item type carried by the bundle
 * @param <C> concrete configurator type (self-type)
 * @since 10.0.1
 */
public interface ListingBundleConfigurator<T, C extends ListingBundleConfigurator<T, C>>
        extends GridHeaderConfigurator<C> {

    // ---------- Row click ----------

    C onItemClick(ComponentEventListener<ItemClickEvent<T>> listener);

    // ---------- Read-only accessors ----------

    ListingBundle<T> getBundle();

    Grid<T> grid();

    Div toolbar();

    Div footer();

    Optional<TextField> getSearchOptional();

    Optional<DynamicFilterPanel<T>> getFilterPanelOptional();

    // ── configure factory ─────────────────────────────────────────────────────

    /**
     * Configure an existing {@link ListingBundle} using the fluent API.
     *
     * <p>The returned configurator exposes the full {@link GridHeaderConfigurator}
     * surface (heading, actions, …) plus {@link #onItemClick} and the read-only
     * accessors.  Build-time operations ({@code columns()}, {@code fetch()},
     * {@code pageSizes()}, etc.) are only available on
     * {@link com.holonplatform.vaadin.flow.components.ListingBundleBuilder}.</p>
     *
     * @param bundle the {@link ListingBundle} to configure (not null)
     * @param <T>    item type
     * @return a {@link BaseListingBundleConfigurator}
     */
    static <T> BaseListingBundleConfigurator<T> configure(ListingBundle<T> bundle) {
        return new DefaultBaseListingBundleConfigurator<>(bundle);
    }

    /**
     * Non-building, post-build configurator for an existing {@link ListingBundle}.
     *
     * @param <T> item type
     */
    interface BaseListingBundleConfigurator<T>
            extends ListingBundleConfigurator<T, BaseListingBundleConfigurator<T>> {
    }
}
