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

import java.time.LocalTime;

import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.LocalTimeInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ValidatableLocalTimeInputBuilder;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Default {@link LocalTimeInputBuilder} implementation using a {@link TextField} as concrete component.
 *
 * @since 5.2.0
 */
public class DefaultLocalTimeInputBuilder extends AbstractLocalTimeInputBuilder<LocalTimeInputBuilder>
		implements LocalTimeInputBuilder {

	public DefaultLocalTimeInputBuilder() {
		super();
	}

	@Override
	protected LocalTimeInputBuilder getConfigurator() {
		return this;
	}

	@Override
	public LocalTimeInputBuilder required(boolean required) {
		getComponent().setRequired(required);
		return getConfigurator();
	}

	@Override
	public Input<LocalTime> build() {
		return buildAsInput();
	}

	@Override
	public ValidatableLocalTimeInputBuilder validatable() {
		return new DefaultValidatableLocalTimeInputBuilder(getComponent(), getValueChangeListeners(),
				getReadonlyChangeListeners(), getAdapters());
	}

}
