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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;

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
	 * Set the aria-label of the component to the given text.
	 * {@link #ariaLabel(String)}.
	 *
	 * @param ariaLabel the value to set
	 * @return this
	 */
	C ariaLabel(String ariaLabel);

	/**
	 * Set the aria-labelledby of the component.
	 * The value must be a valid id attribute of another element that labels the component. The label element must be in the same DOM scope of the component, otherwise screen readers may fail to announce the label content properly
	 * {@link #ariaLabel(String)}.
	 *
	 * @param ariaLabelledBy the value to set
	 * @return this
	 */
	C ariaLabelledBy(String ariaLabelledBy);

	/**
	 * Sets the AriaLabel using a {@link Localizable} message.
	 * <p>
	 * The ariaLabel is interpret as <em>plain text</em> and the HTML markup is not supported.
	 * </p>
	 * <p>
	 * A <code>null</code> value is interpreted as an empty text.
	 * </p>
	 * @param ariaLabel Localizable ariaLabel content message (may be null)
	 * @return this
	 * @see LocalizationProvider
	 */
	C ariaLabel(Localizable ariaLabel);



	/**
	 * Sets the AriaLabel using a localizable <code>messageCode</code>.
	 * <p>
	 * The AriaLabel is interpret as <em>plain text</em> and the HTML markup is not supported.
	 * </p>
	 * @param defaultAriaLabel Default ariaLabel if no translation is available for given <code>messageCode</code>
	 * @param messageCode AriaLabel translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 * @see LocalizationProvider
	 */
	default C ariaLabel(String defaultAriaLabel, String messageCode, Object... arguments) {
		return ariaLabel(Localizable.builder().message((defaultAriaLabel == null) ? "" : defaultAriaLabel).messageCode(messageCode)
				.messageArguments(arguments).build());
	}



}
