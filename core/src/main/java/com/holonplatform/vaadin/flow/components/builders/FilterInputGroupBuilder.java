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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.FilterInput;
import com.holonplatform.vaadin.flow.components.FilterInputGroup;

/**
 * Builder for {@link FilterInputGroup}.
 *
 * <p>
 * Bindings are retained in insertion order, which determines both the evaluation
 * order when combining filters and the rendering order in any form that uses
 * this group.
 * </p>
 *
 * @since 10.0.0
 * @see FilterInputGroup
 */
public interface FilterInputGroupBuilder {

    /**
     * Binds a {@link FilterInput} to the given property.
     *
     * @param <T>         property value type
     * @param property    the property key (not null)
     * @param filterInput the filter input to associate with the property (not null)
     * @return this builder
     */
    <T> FilterInputGroupBuilder withFilter(Property<T> property, FilterInput<T> filterInput);

    /**
     * Binds a property using an automatically inferred {@link FilterInput}
     * based on the property value type.
     *
     * @param <T> property value type
     * @param property the property key (not null)
     * @return this builder
     * @throws IllegalArgumentException if the property type is not supported
     */
    default <T> FilterInputGroupBuilder withFilter(Property<T> property) {
        return withFilter(property, FilterInput.of(property));
    }

    /**
     * Builds and returns the configured {@link FilterInputGroup}.
     *
     * @return a new {@link FilterInputGroup}
     */
    FilterInputGroup build();
}

