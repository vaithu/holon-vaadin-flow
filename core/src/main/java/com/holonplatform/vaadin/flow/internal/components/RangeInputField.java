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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.FilterInput;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;

/**
 * Generic composite field used to edit a {@link FilterInput.Range} value by
 * combining two typed {@link Input} components: one for the lower bound and one
 * for the upper bound.
 *
 * @param <T> Bound value type
 * @since 10.0.0
 */
public class RangeInputField<T extends Comparable<? super T>> extends CustomField<FilterInput.Range<T>> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Input<T> fromInput;
    private final Input<T> toInput;
    private final Span separator;

    private static final String DEFAULT_FROM_PLACEHOLDER = "From";
    private static final String DEFAULT_TO_PLACEHOLDER = "To";
    private static final String DEFAULT_SEPARATOR = "-";

    /**
     * Constructor.
     *
     * @param fromInput Lower-bound input (not null)
     * @param toInput   Upper-bound input (not null)
     */
    public RangeInputField(Input<T> fromInput, Input<T> toInput) {
        this(fromInput, toInput, DEFAULT_FROM_PLACEHOLDER, DEFAULT_TO_PLACEHOLDER, DEFAULT_SEPARATOR);
    }

    /**
     * Constructor with configurable placeholders and separator.
     *
     * @param fromInput       Lower-bound input (not null)
     * @param toInput         Upper-bound input (not null)
     * @param fromPlaceholder Lower-bound placeholder, ignored when null
     * @param toPlaceholder   Upper-bound placeholder, ignored when null
     * @param separatorText   Separator text, defaults to "-" when null/blank
     */
    public RangeInputField(Input<T> fromInput, Input<T> toInput, String fromPlaceholder, String toPlaceholder,
            String separatorText) {
        ObjectUtils.argumentNotNull(fromInput, "From input must be not null");
        ObjectUtils.argumentNotNull(toInput, "To input must be not null");
        this.fromInput = fromInput;
        this.toInput = toInput;

        this.fromInput.addValueChangeListener(event -> updateValue());
        this.toInput.addValueChangeListener(event -> updateValue());

        applyPlaceholder(this.fromInput, fromPlaceholder);
        applyPlaceholder(this.toInput, toPlaceholder);

        this.separator = Components.span().text((separatorText == null || separatorText.isBlank())
                ? DEFAULT_SEPARATOR
                : separatorText).build();

        // Structural class names only; visual styling stays in CSS.
        addClassName("range-input-field");
        this.fromInput.getComponent().addClassName("range-input-field-from");
        this.toInput.getComponent().addClassName("range-input-field-to");
        this.separator.addClassName("range-input-field-separator");

        add(this.fromInput.getComponent(), this.separator, this.toInput.getComponent());
    }

    /**
     * Constructor with i18n-aware placeholders and separator.
     *
     * @param fromInput       Lower-bound input (not null)
     * @param toInput         Upper-bound input (not null)
     * @param fromPlaceholder Lower-bound placeholder localizable (nullable)
     * @param toPlaceholder   Upper-bound placeholder localizable (nullable)
     * @param separatorText   Separator text localizable (nullable)
     */
    public RangeInputField(Input<T> fromInput, Input<T> toInput,
            Localizable fromPlaceholder,
            Localizable toPlaceholder,
            Localizable separatorText) {
        this(fromInput, toInput,
                resolve(fromPlaceholder, DEFAULT_FROM_PLACEHOLDER),
                resolve(toPlaceholder, DEFAULT_TO_PLACEHOLDER),
                resolve(separatorText, DEFAULT_SEPARATOR));
    }

    /**
     * Gets the lower-bound input.
     *
     * @return lower-bound input
     */
    public Input<T> getFromInput() {
        return fromInput;
    }

    /**
     * Gets the upper-bound input.
     *
     * @return upper-bound input
     */
    public Input<T> getToInput() {
        return toInput;
    }

    /**
     * Sets lower-bound placeholder.
     *
     * @param placeholder Placeholder text, null to clear
     */
    public void setFromPlaceholder(String placeholder) {
        applyPlaceholder(this.fromInput, placeholder);
    }

    /**
     * Sets lower-bound placeholder using a localizable message.
     *
     * @param placeholder Placeholder localizable (nullable)
     */
    public void setFromPlaceholder(Localizable placeholder) {
        setFromPlaceholder(resolve(placeholder, null));
    }

    /**
     * Sets upper-bound placeholder.
     *
     * @param placeholder Placeholder text, null to clear
     */
    public void setToPlaceholder(String placeholder) {
        applyPlaceholder(this.toInput, placeholder);
    }

    /**
     * Sets upper-bound placeholder using a localizable message.
     *
     * @param placeholder Placeholder localizable (nullable)
     */
    public void setToPlaceholder(Localizable placeholder) {
        setToPlaceholder(resolve(placeholder, null));
    }

    /**
     * Sets separator text.
     *
     * @param separatorText Separator text (nullable)
     */
    public void setSeparatorText(String separatorText) {
        this.separator.setText((separatorText == null || separatorText.isBlank())
                ? DEFAULT_SEPARATOR
                : separatorText);
    }

    /**
     * Sets separator text using a localizable message.
     *
     * @param separatorText Separator localizable (nullable)
     */
    public void setSeparatorText(Localizable separatorText) {
        setSeparatorText(resolve(separatorText, DEFAULT_SEPARATOR));
    }

    private static void applyPlaceholder(Input<?> input, String placeholder) {
        if (placeholder != null) {
            input.hasPlaceholder().ifPresent(p -> p.setPlaceholder(placeholder));
        }
    }

    private static String resolve(Localizable localizable, String fallback) {
        if (localizable == null) {
            return fallback;
        }
        return LocalizationProvider.localize(localizable).orElse(fallback);
    }

    @Override
    protected FilterInput.Range<T> generateModelValue() {
        return new FilterInput.Range<>(fromInput.getValue(), toInput.getValue());
    }

    @Override
    protected void setPresentationValue(FilterInput.Range<T> value) {
        if (value != null) {
            fromInput.setValue(value.from());
            toInput.setValue(value.to());
        } else {
            fromInput.setValue(null);
            toInput.setValue(null);
        }
    }
}




