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
package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.core.Registration;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.FilterInput;
import com.holonplatform.vaadin.flow.components.FilterInputGroup;
import com.holonplatform.vaadin.flow.components.builders.FilterInputGroupBuilder;
import com.holonplatform.vaadin.flow.components.events.FilterChangeEvent;
import com.holonplatform.vaadin.flow.components.events.FilterChangeListener;

import java.io.Serial;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Default {@link FilterInputGroup} implementation.
 *
 * <p>
 * Maintains an ordered map of {@code Property → FilterInput} bindings.
 * The combined query filter is produced by AND-joining all active child filters.
 * Any child filter change is forwarded to group-level listeners.
 * </p>
 *
 * @since 10.0.0
 */
public class DefaultFilterInputGroup implements FilterInputGroup {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Ordered property → FilterInput bindings (insertion order preserved). */
    private final LinkedHashMap<Property<?>, FilterInput<?>> bindings = new LinkedHashMap<>();

    /** Group-level filter change listeners. */
    private final List<FilterChangeListener<?>> groupListeners = new ArrayList<>();

    // -----------------------------------------------------------------------
    // Binding management (used by builder and subclass)
    // -----------------------------------------------------------------------

    /**
     * Registers a property → FilterInput binding.
     * Also wires the child input's changes to all group-level listeners.
     *
     * @param <T>         property value type
     * @param property    the property key (not null)
     * @param filterInput the filter input to bind (not null)
     */
    protected <T> void addBinding(Property<T> property, FilterInput<T> filterInput) {
        ObjectUtils.argumentNotNull(property, "Property must be not null");
        ObjectUtils.argumentNotNull(filterInput, "FilterInput must be not null");
        bindings.put(property, filterInput);
        // Forward child changes to all group-level listeners
        filterInput.addFilterChangeListener(this::forwardToGroupListeners);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void forwardToGroupListeners(FilterChangeEvent<?> event) {
        // ArrayList.forEach() uses direct array access internally — no iterator allocation.
        // No defensive copy needed: Vaadin UI is single-threaded.
        groupListeners.forEach(l -> ((FilterChangeListener) l).filterChanged(event));
    }

    // -----------------------------------------------------------------------
    // FilterInputGroup implementation
    // -----------------------------------------------------------------------

    @Override
    public Optional<QueryFilter> getQueryFilter() {
        return bindings.values().stream()
                .map(FilterInput::getQueryFilter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(QueryFilter::and);
    }

    @Override
    public boolean isAnyActive() {
        return bindings.values().stream().anyMatch(FilterInput::isActive);
    }

    @Override
    public void resetAll() {
        bindings.values().forEach(FilterInput::reset);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<FilterInput<T>> getFilterInput(Property<T> property) {
        ObjectUtils.argumentNotNull(property, "Property must be not null");
        return Optional.ofNullable((FilterInput<T>) bindings.get(property));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Stream<FilterInputGroup.PropertyBinding<?>> getPropertyBindings() {
        return bindings.entrySet().stream()
                .map(e -> new DefaultPropertyBinding<>(
                        (Property<Object>) e.getKey(),
                        (FilterInput<Object>) e.getValue()));
    }

    @Override
    public Registration addFilterChangeListener(FilterChangeListener<?> listener) {
        ObjectUtils.argumentNotNull(listener, "FilterChangeListener must be not null");
        groupListeners.add(listener);
        return () -> groupListeners.remove(listener);
    }

    // -----------------------------------------------------------------------
    // Nested: DefaultPropertyBinding
    // -----------------------------------------------------------------------

    /**
     * Default {@link FilterInputGroup.PropertyBinding} implementation using Java records.
     */
    private record DefaultPropertyBinding<T>(
            Property<T> property,
            FilterInput<T> filterInput
    ) implements FilterInputGroup.PropertyBinding<T> {

        @Override
        public Property<T> getProperty() {
            return property;
        }

        @Override
        public FilterInput<T> getFilterInput() {
            return filterInput;
        }
    }

    // -----------------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------------

    /**
     * Default {@link FilterInputGroupBuilder} implementation.
     */
    public static class GroupBuilder implements FilterInputGroupBuilder {

        private final DefaultFilterInputGroup group = new DefaultFilterInputGroup();

        @Override
        public <T> FilterInputGroupBuilder withFilter(Property<T> property, FilterInput<T> filterInput) {
            group.addBinding(property, filterInput);
            return this;
        }

        @Override
        public FilterInputGroup build() {
            return group;
        }
    }
}





