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
import com.holonplatform.vaadin.flow.components.builders.BooleanInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ValidatableBooleanInputBuilder;
import com.vaadin.flow.component.checkbox.Checkbox;

/**
 * Default {@link BooleanInputBuilder} implementation using a {@link Checkbox} as concrete component.
 *
 * @since 5.2.0
 */
public class DefaultBooleanInputBuilder extends AbstractBooleanInputBuilder<BooleanInputBuilder>
		implements BooleanInputBuilder {

	public DefaultBooleanInputBuilder() {
		super();
	}

	@Override
	protected BooleanInputBuilder getConfigurator() {
		return this;
	}

	@Override
	public BooleanInputBuilder required(boolean required) {
		getComponent().setRequiredIndicatorVisible(required);
		return getConfigurator();
	}

    @Override
    public BooleanInputBuilder asSwitch() {
        getComponent().addClassName("switch");
        return getConfigurator();
    }

    @Override
	public Input<Boolean> build() {
		return buildAsInput();
	}

	@Override
	public ValidatableBooleanInputBuilder validatable() {
		return new DefaultValidatableBooleanInputBuilder(getComponent(), getValueChangeListeners(),
				getReadonlyChangeListeners(), getAdapters());
	}

}
