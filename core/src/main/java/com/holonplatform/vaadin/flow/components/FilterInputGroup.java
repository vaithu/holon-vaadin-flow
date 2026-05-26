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
import com.holonplatform.core.property.Property;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.builders.FilterInputGroupBuilder;
import com.holonplatform.vaadin.flow.components.events.FilterChangeListener;
import com.holonplatform.vaadin.flow.internal.components.DefaultFilterInputGroup;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Composes multiple {@link FilterInput} instances, each keyed by a
 * {@link Property}, and exposes a single combined {@link QueryFilter} obtained
 * by AND-joining all currently active individual filters.
 *
 * <p>
 * Whenever any child {@link FilterInput} changes, registered
 * {@link FilterChangeListener}s at the group level are notified.
 * </p>
 *
 * <h3>Usage example</h3>
 * <pre>{@code
 * FilterInputGroup group = FilterInputGroup.builder()
 *     .withFilter(NAME,   FilterInput.string(NAME))
 *     .withFilter(AGE,    FilterInput.number(AGE, Integer.class))
 *     .withFilter(ACTIVE, FilterInput.bool(ACTIVE))
 *     .build();
 *
 * // Wire the group to a listing's DataProvider
 * group.addFilterChangeListener(event ->
 *     dataProvider.setFilter(group.getQueryFilter().orElse(null))
 * );
 * }</pre>
 *
 * @since 10.0.0
 * @see FilterInput
 * @see FilterInputForm
 */
public interface FilterInputGroup extends Serializable {

    // -----------------------------------------------------------------------
    // Filter API
    // -----------------------------------------------------------------------

    /**
     * Returns the combined {@link QueryFilter} produced by AND-joining all
     * currently active child {@link FilterInput} filters.
     * <p>
     * Returns {@link Optional#empty()} when no child filter is active.
     * </p>
     *
     * @return the combined filter, or empty if no child is active
     */
    Optional<QueryFilter> getQueryFilter();

    /**
     * Whether at least one {@link FilterInput} in this group is currently
     * active (i.e. has a non-empty filter).
     *
     * @return {@code true} if any filter is active
     */
    boolean isAnyActive();

    /**
     * Clears all {@link FilterInput} components in this group, deactivating
     * every filter. Fires a {@link FilterChangeListener} event for each input
     * that was active.
     */
    void resetAll();

    // -----------------------------------------------------------------------
    // Component access
    // -----------------------------------------------------------------------

    /**
     * Returns the {@link FilterInput} bound to the given property, if present.
     *
     * @param <T>      property value type
     * @param property the property key (not null)
     * @return the filter input bound to that property, or empty
     */
    <T> Optional<FilterInput<T>> getFilterInput(Property<T> property);

    /**
     * Streams all property-to-{@link FilterInput} bindings in this group, in
     * the order they were registered.
     *
     * @return a stream of bindings
     */
    Stream<PropertyBinding<?>> getPropertyBindings();

    // -----------------------------------------------------------------------
    // Listeners
    // -----------------------------------------------------------------------

    /**
     * Registers a listener that is notified whenever any child
     * {@link FilterInput} in this group changes its filter value.
     *
     * @param listener the listener to register (not null)
     * @return a {@link Registration} to remove the listener
     */
    Registration addFilterChangeListener(FilterChangeListener<?> listener);

    /**
     * Exposes the combined query filter as a read-only {@link Signal}.
     * <p>
     * The signal emits the current {@link #getQueryFilter()} value immediately and
     * on each subsequent filter change.
     * </p>
     * <p>
     * Note: create once and reuse the returned signal, to avoid adding multiple
     * internal listener bridges.
     * </p>
     *
     * @return read-only signal of the current combined filter
     * @since 10.0.1
     */
    default Signal<Optional<QueryFilter>> queryFilterSignal() {
        final ValueSignal<Optional<QueryFilter>> signal = new ValueSignal<>(getQueryFilter());
        addFilterChangeListener(event -> signal.set(getQueryFilter()));
        return signal.asReadonly();
    }

    /**
     * Exposes whether any filter is active as a read-only {@link Signal}.
     * <p>
     * The signal emits the current {@link #isAnyActive()} state immediately and on
     * each subsequent filter change.
     * </p>
     * <p>
     * Note: create once and reuse the returned signal, to avoid adding multiple
     * internal listener bridges.
     * </p>
     *
     * @return read-only signal of the active-state flag
     * @since 10.0.1
     */
    default Signal<Boolean> anyActiveSignal() {
        final ValueSignal<Boolean> signal = new ValueSignal<>(isAnyActive());
        addFilterChangeListener(event -> signal.set(isAnyActive()));
        return signal.asReadonly();
    }

    // -----------------------------------------------------------------------
    // Nested: PropertyBinding
    // -----------------------------------------------------------------------

    /**
     * Represents the binding between a {@link Property} and its
     * {@link FilterInput} within a {@link FilterInputGroup}.
     *
     * @param <T> property value type
     */
    interface PropertyBinding<T> extends Serializable {

        /**
         * The property key.
         *
         * @return the property (not null)
         */
        Property<T> getProperty();

        /**
         * The filter input associated with this property.
         *
         * @return the filter input (not null)
         */
        FilterInput<T> getFilterInput();
    }

    // -----------------------------------------------------------------------
    // Builder factory
    // -----------------------------------------------------------------------

    /**
     * Creates a new {@link FilterInputGroupBuilder}.
     *
     * @return a new builder
     */
    static FilterInputGroupBuilder builder() {
        return new DefaultFilterInputGroup.GroupBuilder();
    }
}

