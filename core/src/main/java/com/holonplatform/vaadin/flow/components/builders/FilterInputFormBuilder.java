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
import com.holonplatform.vaadin.flow.components.FilterInputForm;
import com.vaadin.flow.component.Component;

/**
 * Builder for {@link FilterInputForm}.
 *
 * <p>
 * Bindings are retained in insertion order, which determines both the filter
 * evaluation order and the rendering order of the input components on the
 * content layout.
 * </p>
 *
 * @param <C> content layout type
 * @since 10.0.0
 * @see FilterInputForm
 */
public interface FilterInputFormBuilder<C extends Component> {

    /**
     * Binds a {@link FilterInput} to the given property and registers it for
     * rendering on the form layout.
     *
     * @param <T>         property value type
     * @param property    the property key (not null)
     * @param filterInput the filter input to add (not null)
     * @return this builder
     */
    <T> FilterInputFormBuilder<C> withFilter(Property<T> property, FilterInput<T> filterInput);

    /**
     * Binds a property using an automatically inferred {@link FilterInput}
     * based on the property value type.
     *
     * @param <T> property value type
     * @param property the property key (not null)
     * @return this builder
     * @throws IllegalArgumentException if the property type is not supported
     */
    default <T> FilterInputFormBuilder<C> withFilter(Property<T> property) {
        return withFilter(property, FilterInput.of(property));
    }

    /**
     * Sets the {@link FilterInputForm.FilterFormComposer} used to arrange
     * {@link FilterInput} components on the content layout.
     * <p>
     * The default composer adds each filter input component to the layout in
     * registration order (requires the content to implement
     * {@link com.vaadin.flow.component.HasComponents}).
     * </p>
     *
     * @param composer the composer to use (not null)
     * @return this builder
     */
    FilterInputFormBuilder<C> composer(FilterInputForm.FilterFormComposer<C> composer);

    /**
     * Builds and returns the configured {@link FilterInputForm}.
     * <p>
     * This method triggers the composition pass: all registered
     * {@link FilterInput} components are arranged on the content layout using
     * the configured {@link FilterInputForm.FilterFormComposer}.
     * </p>
     *
     * @return a new {@link FilterInputForm}
     */
    FilterInputForm<C> build();
}

