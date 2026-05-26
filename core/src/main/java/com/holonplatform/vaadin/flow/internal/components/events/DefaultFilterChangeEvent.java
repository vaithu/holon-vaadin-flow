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
package com.holonplatform.vaadin.flow.internal.components.events;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.FilterInput;
import com.holonplatform.vaadin.flow.components.events.FilterChangeEvent;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.Optional;

/**
 * Default {@link FilterChangeEvent} implementation.
 *
 * @param <T> raw input value type
 * @since 10.0.0
 */
public class DefaultFilterChangeEvent<T> implements FilterChangeEvent<T> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final FilterInput<T> source;
    @Nullable
    private final QueryFilter oldFilter;
    @Nullable
    private final QueryFilter newFilter;
    private final boolean userOriginated;

    /**
     * Constructor.
     *
     * @param source         the source FilterInput (not null)
     * @param oldFilter      the filter before the change (may be empty)
     * @param newFilter      the filter after the change (may be empty)
     * @param userOriginated whether triggered by user interaction
     */
    public DefaultFilterChangeEvent(FilterInput<T> source,
                                    Optional<QueryFilter> oldFilter,
                                    Optional<QueryFilter> newFilter,
                                    boolean userOriginated) {
        ObjectUtils.argumentNotNull(source, "Source must be not null");
        this.source = source;
        this.oldFilter = oldFilter != null ? oldFilter.orElse(null) : null;
        this.newFilter = newFilter != null ? newFilter.orElse(null) : null;
        this.userOriginated = userOriginated;
    }

    @Override
    public FilterInput<T> getSource() {
        return source;
    }

    @Override
    public Optional<QueryFilter> getOldQueryFilter() {
        return Optional.ofNullable(oldFilter);
    }

    @Override
    public Optional<QueryFilter> getNewQueryFilter() {
        return Optional.ofNullable(newFilter);
    }

    @Override
    public boolean isUserOriginated() {
        return userOriginated;
    }
}
