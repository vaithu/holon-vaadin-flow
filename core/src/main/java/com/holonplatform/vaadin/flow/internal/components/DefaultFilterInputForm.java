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

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.FilterInput;
import com.holonplatform.vaadin.flow.components.FilterInputForm;
import com.holonplatform.vaadin.flow.components.builders.FilterInputFormBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

import java.io.Serial;

/**
 * Default {@link FilterInputForm} implementation.
 *
 * <p>
 * Extends {@link DefaultFilterInputGroup} and adds a content layout component.
 * During {@link FilterInputFormBuilder#build()}, filter input components are
 * arranged on the content using the configured
 * {@link FilterInputForm.FilterFormComposer}.
 * </p>
 *
 * @param <C> content layout type
 * @since 10.0.0
 */
public class DefaultFilterInputForm<C extends Component>
        extends DefaultFilterInputGroup implements FilterInputForm<C> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final C content;
    private FilterInputForm.FilterFormComposer<C> composer;

    /**
     * Constructor.
     *
     * @param content the content layout component (not null)
     */
    public DefaultFilterInputForm(C content) {
        ObjectUtils.argumentNotNull(content, "Content component must be not null");
        this.content = content;
        this.composer = defaultComposer();
    }

    /**
     * Default composer: adds each filter input's component to a
     * {@link HasComponents} layout in registration order.
     */
    private static <C extends Component> FilterInputForm.FilterFormComposer<C> defaultComposer() {
        return (c, group) -> {
            if (c instanceof HasComponents hc) {
                group.getPropertyBindings()
                        .map(b -> b.getFilterInput().getComponent())
                        .forEach(hc::add);
            }
        };
    }

    // -----------------------------------------------------------------------
    // FilterInputForm implementation
    // -----------------------------------------------------------------------

    @Override
    public C getContent() {
        return content;
    }

    @Override
    public Component getComponent() {
        return content;
    }

    /**
     * Sets the composer used when {@link #compose()} is called.
     *
     * @param composer the composer (not null)
     */
    void setComposer(FilterInputForm.FilterFormComposer<C> composer) {
        ObjectUtils.argumentNotNull(composer, "Composer must be not null");
        this.composer = composer;
    }

    /**
     * Runs the composition pass: arranges all filter input components on the
     * content layout using the configured composer.
     */
    public void compose() {
        composer.compose(content, this);
    }

    // -----------------------------------------------------------------------
    // Builder
    // -----------------------------------------------------------------------

    /**
     * Default {@link FilterInputFormBuilder} implementation.
     *
     * @param <C> content layout type
     */
    public static class DefaultBuilder<C extends Component> implements FilterInputFormBuilder<C> {

        private final DefaultFilterInputForm<C> form;

        /**
         * Constructor.
         *
         * @param content the content layout (not null)
         */
        public DefaultBuilder(C content) {
            this.form = new DefaultFilterInputForm<>(content);
        }

        @Override
        public <T> FilterInputFormBuilder<C> withFilter(Property<T> property, FilterInput<T> filterInput) {
            form.addBinding(property, filterInput);
            return this;
        }

        @Override
        public FilterInputFormBuilder<C> composer(FilterInputForm.FilterFormComposer<C> composer) {
            form.setComposer(composer);
            return this;
        }

        @Override
        public FilterInputForm<C> build() {
            form.compose();
            return form;
        }
    }
}



