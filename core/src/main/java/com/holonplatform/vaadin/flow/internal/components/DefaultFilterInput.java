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
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.FilterInput;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.events.FilterChangeListener;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultFilterChangeEvent;
import com.vaadin.flow.component.Component;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Default {@link FilterInput} implementation.
 *
 * <p>
 * Wraps an {@link Input}{@code <T>} and applies a {@link FilterConverter} to
 * translate every value change into an {@link Optional}{@code <QueryFilter>}.
 * </p>
 *
 * @param <T> raw input value type
 * @since 10.0.0
 */
public class DefaultFilterInput<T> implements FilterInput<T> {

    @Serial
    private static final long serialVersionUID = 1L;

    /** The backing Vaadin input component. */
    private final Input<T> input;

    /** Converts raw input values to QueryFilters. */
    private final FilterConverter<T> converter;

    /** Registered listeners. */
    private final List<FilterChangeListener<T>> listeners = new ArrayList<>();

    /** Last computed filter (kept for change events). Null means no active filter. */
    @Nullable
    private QueryFilter currentFilter;

    /**
     * Constructor.
     *
     * @param input     the backing input component (not null)
     * @param converter the filter converter (not null)
     */
    public DefaultFilterInput(Input<T> input, FilterConverter<T> converter) {
        ObjectUtils.argumentNotNull(input, "Input must be not null");
        ObjectUtils.argumentNotNull(converter, "FilterConverter must be not null");
        this.input = input;
        this.converter = converter;
        this.currentFilter = converter.toQueryFilter(input.getValue()).orElse(null);

        // Forward every value change to registered filter-change listeners.
        this.input.addValueChangeListener(event ->
                onValueChanged(event.getValue(), event.isUserOriginated()));
    }

    // -----------------------------------------------------------------------
    // FilterInput implementation
    // -----------------------------------------------------------------------

    @Override
    public Optional<QueryFilter> getQueryFilter() {
        return converter.toQueryFilter(input.getValue());
    }

    @Override
    public boolean isActive() {
        return getQueryFilter().isPresent();
    }

    @Override
    public void reset() {
        input.clear();
    }

    @Override
    public Input<T> getInput() {
        return input;
    }

    @Override
    public Component getComponent() {
        return input.getComponent();
    }

    @Override
    public Registration addFilterChangeListener(FilterChangeListener<T> listener) {
        ObjectUtils.argumentNotNull(listener, "FilterChangeListener must be not null");
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    // -----------------------------------------------------------------------
    // Internal
    // -----------------------------------------------------------------------

    private void onValueChanged(T newValue, boolean userOriginated) {
        Optional<QueryFilter> oldFilter = Optional.ofNullable(currentFilter);
        currentFilter = converter.toQueryFilter(newValue).orElse(null);
        Optional<QueryFilter> newFilter = Optional.ofNullable(currentFilter);
        if (!listeners.isEmpty()) {
            DefaultFilterChangeEvent<T> event =
                    new DefaultFilterChangeEvent<>(this, oldFilter, newFilter, userOriginated);
            // Indexed loop with size snapshot: avoids allocating a defensive ArrayList copy
            // on every value-change event while preserving ConcurrentModificationException safety.
            int size = listeners.size();
            for (int i = 0; i < size; i++) {
                listeners.get(i).filterChanged(event);
            }
        }
    }
}





