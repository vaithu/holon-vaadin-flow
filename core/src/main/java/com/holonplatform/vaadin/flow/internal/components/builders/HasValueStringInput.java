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

import com.holonplatform.vaadin.flow.internal.components.HasValueInput;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;

/**
 * A {@link String} type {@link HasValueInput}.
 *
 * @since 5.2.0
 */
public class HasValueStringInput<F extends HasValue.ValueChangeEvent<String>> extends HasValueInput<F, String> {

	private static final long serialVersionUID = -1095204513726707911L;

	private boolean emptyValuesAsNull;

	private boolean blankValuesAsNull;

	public <H extends Component & HasValue<F, String>> HasValueStringInput(H field) {
		super(field);
	}

	public HasValueStringInput(HasValue<F, String> field, Component component) {
		super(field, component);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.HasValueInput#getValue()
	 */
	@Override
	public String getValue() {
		final String value = super.getValue();
		if (value != null) {
			if (value.length() == 0 && isEmptyValuesAsNull()) {
				return null;
			}
			if (value.trim().length() == 0 && isBlankValuesAsNull()) {
				return null;
			}
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.HasValueInput#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		final String value = super.getValue();
		if (value == null) {
			return true;
		}
		if (value.trim().length() == 0 && isBlankValuesAsNull()) {
			return true;
		}
		if (value.length() == 0 && isEmptyValuesAsNull()) {
			return true;
		}
		return super.isEmpty();
	}

	/**
	 * Get whether to treat empty String values as <code>null</code> values.
	 * @return whether to treat empty String values as <code>null</code> values
	 */
	public boolean isEmptyValuesAsNull() {
		return emptyValuesAsNull;
	}

	/**
	 * Enable or disable treating empty String values as <code>null</code> values.
	 * @param emptyValuesAsNull whether to treat empty String values as <code>null</code> values
	 */
	public void setEmptyValuesAsNull(boolean emptyValuesAsNull) {
		this.emptyValuesAsNull = emptyValuesAsNull;
	}

	/**
	 * Get whether to treat blank String values as <code>null</code> values.
	 * @return whether to treat blank String values as <code>null</code> values
	 */
	public boolean isBlankValuesAsNull() {
		return blankValuesAsNull;
	}

	/**
	 * Set whether to treat blank String values as <code>null</code> values.
	 * @param blankValuesAsNull whether to treat blank String values as <code>null</code> values
	 */
	public void setBlankValuesAsNull(boolean blankValuesAsNull) {
		this.blankValuesAsNull = blankValuesAsNull;
	}

}
