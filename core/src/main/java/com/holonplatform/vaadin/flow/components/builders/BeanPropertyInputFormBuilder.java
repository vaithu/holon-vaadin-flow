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

import com.holonplatform.core.property.PathProperty;
import com.holonplatform.vaadin.flow.components.BeanPropertyInputForm;
import com.vaadin.flow.component.Component;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Builder for {@link BeanPropertyInputForm}.
 *
 * <p>
 * Bean-level convenience methods ({@link #excludeFields}, {@link #readOnlyFields},
 * {@link #configure}) are layered on top of the standard
 * {@link PropertyInputFormBuilder} pipeline. Once the bean-specific configuration
 * is complete, call {@link #build()} to obtain a fully functional
 * {@link BeanPropertyInputForm}.
 * </p>
 *
 * <h3>Field ordering</h3>
 * Fields are rendered in the order determined by the
 * {@link com.holonplatform.core.beans.Sequence} annotation on the bean class.
 * Fields without a {@code @Sequence} value follow after sequenced fields, in
 * reflection-declaration order.
 *
 * <h3>Auto-hidden fields</h3>
 * Fields annotated with {@link com.holonplatform.core.beans.Identifier} or
 * {@link com.holonplatform.core.beans.Version} are automatically hidden unless
 * {@link #showIdentifiers()} / {@link #showVersions()} is called.
 *
 * <h3>Advanced configuration</h3>
 * Use {@link #configure(Consumer)} to access the underlying
 * {@link PropertyInputFormBuilder} directly for validator registration, custom
 * renderer binding, ENTER-navigation settings, and anything else the standard
 * form builder supports.
 *
 * @param <C> Vaadin layout component type used as the form content
 * @param <T> Bean type
 * @since 10.0.0
 * @see BeanPropertyInputForm
 */
public interface BeanPropertyInputFormBuilder<C extends Component, T> {

    /**
     * Exclude the named fields entirely from the form.
     * <p>
     * Excluded fields are removed from the property set before the underlying
     * {@link PropertyInputFormBuilder} is created; therefore they are neither
     * rendered nor tracked in the resulting {@link com.holonplatform.core.property.PropertyBox}.
     * </p>
     * <p>
     * This method may be called multiple times; the exclusion sets are merged.
     * </p>
     *
     * @param fieldNames bean field names to exclude (not null)
     * @return this builder
     */
    BeanPropertyInputFormBuilder<C, T> excludeFields(String... fieldNames);

    /**
     * Mark the named fields as read-only.
     * <p>
     * The corresponding inputs are rendered but their value cannot be changed by
     * the user. Validators bound to read-only properties are ignored.
     * </p>
     *
     * @param fieldNames bean field names to make read-only (not null)
     * @return this builder
     */
    BeanPropertyInputFormBuilder<C, T> readOnlyFields(String... fieldNames);

    /**
     * Override the automatic hiding of {@link com.holonplatform.core.beans.Identifier}
     * fields: they will be shown as regular input fields.
     *
     * @return this builder
     */
    BeanPropertyInputFormBuilder<C, T> showIdentifiers();

    /**
     * Override the automatic hiding of {@link com.holonplatform.core.beans.Version}
     * fields: they will be shown as regular input fields.
     *
     * @return this builder
     */
    BeanPropertyInputFormBuilder<C, T> showVersions();

    /**
     * Retrieve the {@link PathProperty} that corresponds to the given bean field
     * name, for use in typed validator / renderer configuration via
     * {@link #configure(Consumer)}.
     *
     * <p>
     * The returned {@code Optional} is empty when the field name does not match
     * any field in the bean class or the field was previously
     * {@link #excludeFields(String...) excluded}.
     * </p>
     *
     * @param fieldName bean field name (not null)
     * @return optional {@link PathProperty} for the field
     */
    Optional<PathProperty<?>> property(String fieldName);

    /**
     * Apply additional configuration to the underlying {@link PropertyInputFormBuilder}.
     *
     * <p>
     * This is the escape hatch for anything not covered by the bean-aware methods
     * above, e.g.:
     * <pre>{@code
     * BeanPropertyInputForm.formLayout(Customer.class)
     *     .excludeFields("id", "version")
     *     .configure(fb -> fb
     *         .enterMovesFocusToNext(true)
     *         .validateOnEnterFocusMove(true))
     *     .build();
     * }</pre>
     * </p>
     *
     * <p>
     * Multiple calls to {@code configure()} are supported; the consumers are
     * applied in order at {@link #build()} time.
     * </p>
     *
     * @param config consumer that receives the {@link PropertyInputFormBuilder} (not null)
     * @return this builder
     */
    BeanPropertyInputFormBuilder<C, T> configure(Consumer<PropertyInputFormBuilder<C>> config);

    /**
     * Build and return the {@link BeanPropertyInputForm}.
     *
     * @return a fully configured {@link BeanPropertyInputForm}
     */
    BeanPropertyInputForm<T> build();
}

