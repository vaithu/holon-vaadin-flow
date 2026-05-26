/*
 * Copyright 2016-2017 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.components;

import com.holonplatform.core.Registration;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryFilter;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.Query;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * Utilities to wire {@link FilterInputGroup} (or {@link FilterInputForm}) to
 * item listing data fetch callbacks.
 *
 * <p>
 * Typical usage with {@link BeanListing}:
 * </p>
 * <pre>{@code
 * listing.setItems(FilterInputSupport.fetchCallback(filters, (query, filter) -> {
 *     var datastoreQuery = datastore.query(TARGET).restrict(query.getLimit(), query.getOffset());
 *     if (filter != null) {
 *         datastoreQuery.filter(filter);
 *     }
 *     return datastoreQuery.stream(BeanProjection.of(MyBean.class));
 * }));
 *
 * FilterInputSupport.refreshOnFilterChange(filters, listing);
 * }</pre>
 *
 * @since 10.0.0
 */
public final class FilterInputSupport {

    private FilterInputSupport() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Functional contract for fetch callbacks that also receive the currently
     * combined filter.
     *
     * @param <T> item type
     */
    @FunctionalInterface
    public interface FilteredFetchCallback<T> {

        /**
         * Fetches a stream of items using query paging/sorting metadata and the
         * current optional filter.
         *
         * @param query Vaadin query metadata
         * @param filter current combined filter from the group, or {@code null} when
         *               no filter is active
         * @return stream of items
         */
        Stream<T> fetch(Query<T, Void> query, QueryFilter filter);
    }

    /**
     * Functional contract for count callbacks that also receive the currently
     * combined filter.
     *
     * @param <T> item type
     */
    @FunctionalInterface
    public interface FilteredCountCallback<T> {

        /**
         * Counts items using query metadata and the current optional filter.
         *
         * @param query Vaadin query metadata
         * @param filter current combined filter from the group, or {@code null} when
         *               no filter is active
         * @return total number of matching items
         */
        int count(Query<T, Void> query, QueryFilter filter);
    }

    /**
     * Wraps a {@link FilteredFetchCallback} into a regular listing fetch callback,
     * injecting the current filter from the provided group each time the listing
     * fetches data.
     *
     * @param <T> item type
     * @param filterGroup filter group to read the current combined filter from
     * @param fetchCallback callback that performs the actual backend fetch
     * @return a standard Vaadin fetch callback usable with listing
     *         {@code setItems(...)}
     */
    public static <T> CallbackDataProvider.FetchCallback<T, Void> fetchCallback(
            FilterInputGroup filterGroup,
            FilteredFetchCallback<T> fetchCallback) {
        ObjectUtils.argumentNotNull(filterGroup, "FilterInputGroup must be not null");
        ObjectUtils.argumentNotNull(fetchCallback, "FilteredFetchCallback must be not null");
        return query -> fetchCallback.fetch(query, filterGroup.getQueryFilter().orElse(null));
    }

    /**
     * Wraps a {@link FilteredCountCallback} into a regular listing count callback,
     * injecting the current filter from the provided group each time the listing
     * requests item count.
     *
     * @param <T> item type
     * @param filterGroup filter group to read the current combined filter from
     * @param countCallback callback that performs the backend count
     * @return a standard Vaadin count callback usable with listing
     *         {@code setItems(fetch, count)}
     */
    public static <T> CallbackDataProvider.CountCallback<T, Void> countCallback(
            FilterInputGroup filterGroup,
            FilteredCountCallback<T> countCallback) {
        ObjectUtils.argumentNotNull(filterGroup, "FilterInputGroup must be not null");
        ObjectUtils.argumentNotNull(countCallback, "FilteredCountCallback must be not null");
        return query -> countCallback.count(query, filterGroup.getQueryFilter().orElse(null));
    }

    /**
     * Registers a listener that refreshes the given item set whenever any filter
     * input changes.
     *
     * @param filterGroup filter group to observe
     * @param itemSet target item set to refresh
     * @return registration to remove the listener
     */
    public static Registration refreshOnFilterChange(FilterInputGroup filterGroup, ItemSet itemSet) {
        ObjectUtils.argumentNotNull(filterGroup, "FilterInputGroup must be not null");
        ObjectUtils.argumentNotNull(itemSet, "ItemSet must be not null");
        return filterGroup.addFilterChangeListener(event -> itemSet.refresh());
    }

    /**
     * Registers a signal-based effect that refreshes the given listing whenever the
     * combined filter signal changes.
     * <p>
     * The effect is lifecycle-bound to the listing component and is automatically
     * disposed when the component is detached.
     * </p>
     *
     * @param filterGroup filter group to observe
     * @param listing target listing to refresh
     * @return registration to remove the signal effect
     * @since 10.0.1
     */
    public static Registration refreshOnFilterSignal(FilterInputGroup filterGroup, ItemListing<?, ?> listing) {
        ObjectUtils.argumentNotNull(filterGroup, "FilterInputGroup must be not null");
        ObjectUtils.argumentNotNull(listing, "ItemListing must be not null");

        final AtomicBoolean initialized = new AtomicBoolean(false);
        final Signal<?> filterSignal = filterGroup.queryFilterSignal();
        final com.vaadin.flow.shared.Registration effectRegistration = Signal.effect(listing.getComponent(), () -> {
            filterSignal.get();
            // Keep parity with listener behavior: do not force an eager refresh at bind time.
            if (initialized.getAndSet(true)) {
                listing.refresh();
            }
        });
        return effectRegistration::remove;
    }
}



