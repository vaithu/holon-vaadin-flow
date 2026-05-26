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
package com.holonplatform.vaadin.flow.components.events;

import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.FilterInput;

import java.util.Optional;

/**
 * Event fired when the filter value of a {@link FilterInput} changes.
 * <p>
 * The event carries both the previous and the new {@link QueryFilter} states.
 * When a filter becomes inactive (e.g. the text field is cleared) the
 * corresponding filter {@link Optional} will be empty.
 * </p>
 *
 * @param <T> the raw input value type
 * @since 10.0.0
 * @see FilterInput
 * @see FilterChangeListener
 */
public interface FilterChangeEvent<T> extends Event<FilterInput<T>> {

    /**
     * Returns the {@link FilterInput} that originated this event.
     *
     * @return the source filter input (not null)
     */
    @Override
    FilterInput<T> getSource();

    /**
     * Returns the {@link QueryFilter} that was active <em>before</em> this change,
     * or {@link Optional#empty()} if the filter was inactive.
     *
     * @return the previous filter state
     */
    Optional<QueryFilter> getOldQueryFilter();

    /**
     * Returns the {@link QueryFilter} that is active <em>after</em> this change,
     * or {@link Optional#empty()} if the filter is now inactive.
     *
     * @return the new filter state
     */
    Optional<QueryFilter> getNewQueryFilter();

    /**
     * Whether this event was triggered by a user interaction on the client side,
     * as opposed to a programmatic value change.
     *
     * @return {@code true} if the change originated from the client
     */
    boolean isUserOriginated();

    /**
     * Convenience method: whether the filter is active after this change.
     *
     * @return {@code true} if {@link #getNewQueryFilter()} is non-empty
     */
    default boolean isFilterActive() {
        return getNewQueryFilter().isPresent();
    }
}

