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

import com.holonplatform.vaadin.flow.components.builders.FilterInputFormBuilder;
import com.holonplatform.vaadin.flow.components.builders.FormLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.HorizontalLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.VerticalLayoutBuilder;
import com.holonplatform.vaadin.flow.internal.components.DefaultFilterInputForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.io.Serializable;

/**
 * A {@link FilterInputGroup} that additionally provides a rendered UI
 * component, making it suitable for direct inclusion in a Vaadin view.
 *
 * <p>
 * Filter input components are arranged on the content layout using the
 * configured {@link FilterFormComposer}. The default composer simply adds each
 * filter input's component to the layout in registration order.
 * </p>
 *
 * <h3>Usage example</h3>
 * <pre>{@code
 * FilterInputForm<FormLayout> form = FilterInputForm.formLayout()
 *     .withFilter(NAME,   FilterInput.string(NAME))
 *     .withFilter(AGE,    FilterInput.number(AGE, Integer.class))
 *     .withFilter(ACTIVE, FilterInput.bool(ACTIVE))
 *     .build();
 *
 * // Add the form to the view
 * content(form.getComponent());
 *
 * // Wire to a listing's DataProvider
 * form.addFilterChangeListener(event ->
 *     dataProvider.setFilter(form.getQueryFilter().orElse(null))
 * );
 * }</pre>
 *
 * @param <C> the content layout type
 * @since 10.0.0
 * @see FilterInputGroup
 * @see FilterInput
 * @see FilterInputFormBuilder
 */
public interface FilterInputForm<C extends Component> extends FilterInputGroup, HasComponent {

    /**
     * Returns the content layout that hosts all filter input components.
     *
     * @return the content component (not null)
     */
    C getContent();

    // -----------------------------------------------------------------------
    // FilterFormComposer
    // -----------------------------------------------------------------------

    /**
     * Strategy for composing {@link FilterInput} components onto a content
     * layout.
     * <p>
     * The default implementation simply calls
     * {@code content.content(filterInput.getComponent())} for each filter input in
     * registration order (requires {@code C} to implement
     * {@link com.vaadin.flow.component.HasComponents}).
     * </p>
     *
     * @param <C> content layout type
     */
    @FunctionalInterface
    interface FilterFormComposer<C extends Component> extends Serializable {

        /**
         * Arranges the filter inputs from the given group onto the content layout.
         *
         * @param content content layout (not null)
         * @param group   the filter input group (not null)
         */
        void compose(C content, FilterInputGroup group);
    }

    // -----------------------------------------------------------------------
    // Factory methods
    // -----------------------------------------------------------------------

    /**
     * Creates a {@link FilterInputFormBuilder} backed by a {@link FormLayout}.
     *
     * @return a new builder
     */
    static FilterInputFormBuilder<FormLayout> formLayout() {
        return new DefaultFilterInputForm.DefaultBuilder<>(FormLayoutBuilder.create().build());
    }

    /**
     * Creates a {@link FilterInputFormBuilder} backed by a
     * {@link VerticalLayout}.
     *
     * @return a new builder
     */
    static FilterInputFormBuilder<VerticalLayout> verticalLayout() {
        return new DefaultFilterInputForm.DefaultBuilder<>(VerticalLayoutBuilder.create().build());
    }

    /**
     * Creates a {@link FilterInputFormBuilder} backed by a
     * {@link HorizontalLayout}.
     *
     * @return a new builder
     */
    static FilterInputFormBuilder<HorizontalLayout> horizontalLayout() {
        return new DefaultFilterInputForm.DefaultBuilder<>(HorizontalLayoutBuilder.create().build());
    }

    /**
     * Creates a {@link FilterInputFormBuilder} backed by a custom content
     * layout.
     *
     * @param <C>     content layout type
     * @param content the content layout component (not null)
     * @return a new builder
     */
    static <C extends Component> FilterInputFormBuilder<C> builder(C content) {
        return new DefaultFilterInputForm.DefaultBuilder<>(content);
    }
}

