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
package com.holonplatform.vaadin.flow.components.builders;

import com.vaadin.flow.component.shared.HasTooltip;

/**
 * Configurator for {@link HasTooltip} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.5.4
 */
public interface HasTooltipConfigurator<C extends HasTooltipConfigurator<C>> {

	/**
	 * Gets the tooltip handle of the component
	 * @return this
	 */
	C getTooltip();

	/**
	 * Sets a tooltip text for the component.
	 * @param text The CSS style class name to be added to the component
	 * @return this
	 */
	C setTooltipText(String text);

	/**
	 * Base {@link HasTooltipConfigurator}.
	 */
	public interface BaseHasTooltipConfigurator extends HasTooltipConfigurator<BaseHasTooltipConfigurator> {

	}

	/**
	 * Create a new {@link BaseHasTooltipConfigurator}.
	 * @param component Component to configure (not null)
	 * @return A new {@link BaseHasTooltipConfigurator}
	 */
	static BaseHasTooltipConfigurator create(HasTooltip component) {
		return new DefaultHasTooltipConfigurator(component);
	}

}
