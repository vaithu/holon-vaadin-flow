/*
 * Copyright 2016-2019 Axioma srl.
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
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.Validator;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValidationStatusHandler;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.ValidatableNumberFieldInputBuilder;
import com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter;
import com.holonplatform.vaadin.flow.components.events.ReadonlyChangeListener;
import com.holonplatform.vaadin.flow.components.support.InputAdaptersContainer;
import com.vaadin.flow.component.textfield.NumberField;

import java.util.List;

/**
 * Default validatable NumberField-backed number input builder.
 *
 * @param <T> Number type
 */
public class DefaultValidatableNumberFieldInputBuilder<T extends Number>
		extends AbstractNumberFieldInputBuilder<T, ValidatableNumberFieldInputBuilder<T>>
		implements ValidatableNumberFieldInputBuilder<T> {

	private final DefaultValidatableInputConfigurator<T> validatableInputConfigurator;

	public DefaultValidatableNumberFieldInputBuilder(Class<T> numberType, NumberField component, T initialValue,
			StringToNumberConverter<T> converter,
			List<ValueChangeListener<T, ValueChangeEvent<T>>> valueChangeListeners,
			List<ReadonlyChangeListener> readonlyChangeListeners, InputAdaptersContainer<T> adapters) {
		super(numberType, component, initialValue, converter, valueChangeListeners, readonlyChangeListeners, adapters);
		this.validatableInputConfigurator = new DefaultValidatableInputConfigurator<>();
	}

	@Override
	protected ValidatableNumberFieldInputBuilder<T> getConfigurator() {
		return this;
	}

	@Override
	public ValidatableInput<T> build() {
		return validatableInputConfigurator.configure(buildAsValidatableInput());
	}

	@Override
	public ValidatableNumberFieldInputBuilder<T> withValidator(Validator<T> validator) {
		validatableInputConfigurator.withValidator(validator);
		return this;
	}

	@Override
	public ValidatableNumberFieldInputBuilder<T> validationStatusHandler(
			ValidationStatusHandler<ValidatableInput<T>> validationStatusHandler) {
		validatableInputConfigurator.validationStatusHandler(validationStatusHandler);
		return this;
	}

	@Override
	public ValidatableNumberFieldInputBuilder<T> validateOnValueChange(boolean validateOnValueChange) {
		validatableInputConfigurator.validateOnValueChange(validateOnValueChange);
		return this;
	}

	@Override
	public ValidatableNumberFieldInputBuilder<T> required(Validator<T> validator) {
		validatableInputConfigurator.required(validator);
		return this;
	}

	@Override
	public ValidatableNumberFieldInputBuilder<T> required(Localizable message) {
		validatableInputConfigurator.required(message);
		return this;
	}

	@Override
	public ValidatableNumberFieldInputBuilder<T> required(boolean required) {
		validatableInputConfigurator.required(required);
		return this;
	}
}

