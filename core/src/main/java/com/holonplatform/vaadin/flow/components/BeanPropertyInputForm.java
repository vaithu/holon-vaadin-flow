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

import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.vaadin.flow.components.builders.BeanPropertyInputFormBuilder;
import com.holonplatform.vaadin.flow.components.builders.FormLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.HorizontalLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.VerticalLayoutBuilder;
import com.holonplatform.vaadin.flow.internal.components.DefaultBeanPropertyInputForm;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * A {@link PropertyInputForm} that is driven by a Java bean class.
 * <p>
 * The form's property set is derived automatically from the bean's fields using
 * {@link com.holonplatform.core.beans.BeanPropertySet}, which respects
 * {@link com.holonplatform.core.beans.Sequence} for field ordering and carries
 * Bean Validation constraint annotations as Holon {@link com.holonplatform.core.Validator}s.
 * </p>
 * <p>
 * Fields annotated with {@link com.holonplatform.core.beans.Identifier} or
 * {@link com.holonplatform.core.beans.Version} are automatically hidden (their values
 * are preserved in the {@link PropertyBox} but no input is rendered).
 * </p>
 * <p>
 * Convenience methods {@link #setBean(Object)} and {@link #getBean()} allow reading
 * from and writing to actual bean instances without manual {@link PropertyBox} handling.
 * </p>
 *
 * @param <T> Bean type
 * @since 10.0.0
 * @see PropertyInputForm
 * @see com.holonplatform.core.beans.BeanPropertySet
 */
public interface BeanPropertyInputForm<T> extends PropertyInputForm {

    /**
     * Populate all form inputs from the given bean instance.
     * <p>
     * The bean field values are read using the underlying
     * {@link com.holonplatform.core.beans.BeanPropertySet} and loaded into the
     * corresponding {@link Input} components without triggering validation.
     * </p>
     *
    * @param bean the bean instance to read values from; {@code null} clears all inputs
     */
    void setBean(T bean);

    /**
     * Collect the current input values, validate them, and write them into a new
     * bean instance.
     * <p>
     * Equivalent to {@link #getBean(boolean) getBean(true)}.
     * </p>
     *
     * @return a new bean instance populated with the current form values
     * @throws ValidationException if validation fails
     */
    T getBean();

    /**
     * Collect the current input values into a new bean instance.
     *
     * @param validate {@code true} to validate all inputs before returning;
     *                 {@code false} to skip validation
     * @return a new bean instance populated with the current form values
     * @throws ValidationException if {@code validate} is {@code true} and
     *                             validation fails
     */
    T getBean(boolean validate);

    // -----------------------------------------------------------------------
    // Factory methods
    // -----------------------------------------------------------------------

    /**
     * Create a builder that produces a {@link BeanPropertyInputForm} backed by a
     * {@link FormLayout}.
     *
     * @param <T>       Bean type
     * @param beanClass the bean class to introspect (not null)
     * @return a new {@link BeanPropertyInputFormBuilder}
     */
    static <T> BeanPropertyInputFormBuilder<FormLayout, T> formLayout(Class<T> beanClass) {
        return new DefaultBeanPropertyInputForm.DefaultBuilder<>(
                FormLayoutBuilder.create().build(), beanClass);
    }

    /**
     * Create a builder that produces a {@link BeanPropertyInputForm} backed by a
     * {@link VerticalLayout}.
     *
     * @param <T>       Bean type
     * @param beanClass the bean class to introspect (not null)
     * @return a new {@link BeanPropertyInputFormBuilder}
     */
    static <T> BeanPropertyInputFormBuilder<VerticalLayout, T> verticalLayout(Class<T> beanClass) {
        return new DefaultBeanPropertyInputForm.DefaultBuilder<>(
                VerticalLayoutBuilder.create().build(), beanClass);
    }

    /**
     * Create a builder that produces a {@link BeanPropertyInputForm} backed by a
     * {@link HorizontalLayout}.
     *
     * @param <T>       Bean type
     * @param beanClass the bean class to introspect (not null)
     * @return a new {@link BeanPropertyInputFormBuilder}
     */
    static <T> BeanPropertyInputFormBuilder<HorizontalLayout, T> horizontalLayout(Class<T> beanClass) {
        return new DefaultBeanPropertyInputForm.DefaultBuilder<>(
                HorizontalLayoutBuilder.create().build(), beanClass);
    }
}

