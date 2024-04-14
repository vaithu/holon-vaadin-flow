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

/**
 * Configurator for components and other user interface objects that may have an aria-label and an aria-labelledby
 * DOM attributes to set the accessible name of the component.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.5.4
 */
public interface HasAriaLabelConfigurator<C extends HasAriaLabelConfigurator<C>> {


	/**
	 * Sets the visibility of the button which clears the field.
	 * {@link #ariaLabel(String)}.
	 *
	 * @param ariaLabel the value to set
	 * @return this
	 */
	C ariaLabel(String ariaLabel);

	/**
	 * Sets the visibility of the button which clears the field.
	 * {@link #ariaLabel(String)}.
	 *
	 * @param ariaLabelledBy the value to set
	 * @return this
	 */
	C ariaLabelledBy(String ariaLabelledBy);



}
