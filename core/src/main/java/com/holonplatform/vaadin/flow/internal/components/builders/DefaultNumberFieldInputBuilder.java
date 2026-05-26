/*
 * Copyright 2016-2018 Axioma srl.
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

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.NumberFieldInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ValidatableNumberFieldInputBuilder;

/**
 * Default NumberField-backed number input builder.
 *
 * @param <T> Number type
 */
public class DefaultNumberFieldInputBuilder<T extends Number>
		extends AbstractNumberFieldInputBuilder<T, NumberFieldInputBuilder<T>>
		implements NumberFieldInputBuilder<T> {

	public DefaultNumberFieldInputBuilder(Class<T> numberType) {
		super(numberType);
	}

	@Override
	protected NumberFieldInputBuilder<T> getConfigurator() {
		return this;
	}

	@Override
	public NumberFieldInputBuilder<T> required(boolean required) {
		getComponent().setRequired(required);
		return getConfigurator();
	}

	@Override
	public Input<T> build() {
		return buildAsInput();
	}

	@Override
	public ValidatableNumberFieldInputBuilder<T> validatable() {
		return new DefaultValidatableNumberFieldInputBuilder<>(getNumberType(), getComponent(), getInitialValue(),
				getConverter(), getValueChangeListeners(), getReadonlyChangeListeners(), getAdapters());
	}
}

