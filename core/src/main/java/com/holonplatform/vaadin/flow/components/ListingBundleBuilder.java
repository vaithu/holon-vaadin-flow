
package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;

/**
 * Fluent builder that assembles a {@link ListingBundle} — a fully pre-wired set of
 * {@link BeanListing}, {@link ItemListingPaginationBar}, {@link ItemListingPageSizeSelector},
 * an optional search field, and an optional filter panel — in a single chained call.
 *
 * <p>All build-time configuration methods are inherited from {@link ListingBundleConfigurer}.
 * Post-build accessors and mutations are inherited from {@link ListingBundleConfigurator}.
 * The terminal operation is {@link #build()}, returning a fully wired {@link ListingBundle}.</p>
 *
 * <p>If you enable {@code withFilterPanel()}, use a filter-aware fetch overload so the panel's
 * filter is actually applied to your query.</p>
 *
 * @param <T> bean item type
 * @see Components#listing(Class)
 * @see ListingBundle
 * @see ListingBundleConfigurer
 * @since 10.0.1
 */
public interface ListingBundleBuilder<T> extends
        ListingBundleConfigurer<T, ListingBundleBuilder<T>>,
        ComponentBuilder<ListingBundle<T>, ListingBundleBuilder<T>> {

    // ── Backward-compatible type aliases ─────────────────────────────────────
    // These extend the canonical types in ListingBundleConfigurer so that
    // existing code referencing ListingBundleBuilder.FetchCallback still compiles.

    /** @see ListingBundleConfigurer.FetchCallback */
    @FunctionalInterface
    interface FetchCallback<T> extends ListingBundleConfigurer.FetchCallback<T> {}

    /** @see ListingBundleConfigurer.FilteredFetchCallback */
    @FunctionalInterface
    interface FilteredFetchCallback<T> extends ListingBundleConfigurer.FilteredFetchCallback<T> {}

    /** @see ListingBundleConfigurer.ColumnAwareFilteredFetchCallback */
    @FunctionalInterface
    interface ColumnAwareFilteredFetchCallback<T> extends ListingBundleConfigurer.ColumnAwareFilteredFetchCallback<T> {}
}
