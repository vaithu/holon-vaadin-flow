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
import com.holonplatform.vaadin.flow.components.builders.StringAreaInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ValidatableStringAreaInputBuilder;
import com.vaadin.flow.component.textfield.TextArea;

/**
 * Default {@link StringAreaInputBuilder} implementation using a {@link TextArea} as concrete component.
 *
 * @since 5.2.0
 */
public class DefaultStringAreaInputBuilder extends AbstractStringAreaInputBuilder<StringAreaInputBuilder>
		implements StringAreaInputBuilder {

	public DefaultStringAreaInputBuilder() {
		super();
	}

	@Override
	protected StringAreaInputBuilder getConfigurator() {
		return this;
	}

	@Override
	public StringAreaInputBuilder required(boolean required) {
		getComponent().setRequired(required);
		return getConfigurator();
	}

	@Override
	public Input<String> build() {
		return buildAsInput();
	}

	@Override
	public ValidatableStringAreaInputBuilder validatable() {
		return new DefaultValidatableStringAreaInputBuilder(getComponent(), isEmptyValuesAsNull(),
				isBlankValuesAsNull(), getValueChangeListeners(), getReadonlyChangeListeners(), getAdapters());
	}

}
