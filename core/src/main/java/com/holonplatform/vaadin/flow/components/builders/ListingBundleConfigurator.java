package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultBaseListingBundleConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Fluent configurator for a {@link ListingBundle} — exposes the bundle's
 * grid header, search field, filter panel, pagination, and row-click hooks.
 *
 * <p>Extends {@link GridHeaderConfigurator}, so every header / grid-header
 * fluent method (heading, breadcrumb, default/context actions, …) is also
 * available here and is forwarded to the bundle's {@code header()}.</p>
 *
 * @param <T> item type carried by the bundle
 * @param <C> concrete configurator type
 * @since 10.0.1
 */
public interface ListingBundleConfigurator<T, C extends ListingBundleConfigurator<T, C>> extends
        GridHeaderConfigurator<C> {

    // ---------- Row click ----------

    C onItemClick(ComponentEventListener<ItemClickEvent<T>> listener);

    // ---------- Read-only accessors ----------

    ListingBundle<T> getBundle();

    /** May be {@code null} when the bundle was built without a search field. */
    TextField getSearchField();

    /** May be {@code null} when the bundle was built without a filter panel. */
    DynamicFilterPanel<T> getFilterPanel();

    Div getToolbar();

    Div getFooter();

    // ── configure factory ────────────────────────────────────────────────────

    /**
     * Configure an existing {@link ListingBundle} instance using the fluent configurator API.
     *
     * @param bundle the ListingBundle instance to configure (not null)
     * @param <T> item type
     * @return a {@link BaseListingBundleConfigurator}
     */
    static <T> BaseListingBundleConfigurator<T> configure(ListingBundle<T> bundle) {
        return new DefaultBaseListingBundleConfigurator<>(bundle);
    }

    /**
     * Base (non-building) configurator for an existing {@link ListingBundle}.
     */
    interface BaseListingBundleConfigurator<T>
            extends ListingBundleConfigurator<T, BaseListingBundleConfigurator<T>> {
    }
}

