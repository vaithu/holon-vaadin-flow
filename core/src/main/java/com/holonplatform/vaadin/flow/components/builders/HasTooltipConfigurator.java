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
	 * Sets the tooltip text using a {@link Localizable} message.
	 * <p>
	 * The tooltip is set using the <vaadin-tooltip slot="tooltip"> attribute. Browsers typically use the title to show a tooltip
	 * when hovering an element
	 * <p>
	 * @param tooltip Localizable tooltip message (may be null)
	 * @return this
	 * @see LocalizationProvider
	 */
	C tooltip(Localizable tooltip);

	/**
	 * Sets the tooltip text. This is an alias for {@link #tooltip(String)}.
	 * <p>
	 * The tooltip is set using the <vaadin-tooltip slot="tooltip"> attribute. Browsers typically use the title to show a tooltip
	 * when hovering an element
	 * <p>
	 * @param tooltip The tooltip to set (may be null)
	 * @return this
	 */
	default C tooltip(String tooltip) {
		return tooltip((tooltip == null) ? null : Localizable.builder().message(tooltip).build());
	}

	/**
	 * Sets the tooltip using a localizable <code>messageCode</code>.
	 * <p>
	 * The tooltip is set using the <vaadin-tooltip slot="tooltip"> attribute. Browsers typically use the title to show a tooltip
	 * when hovering an element
	 * <p>
	 * @param defaultTooltip Default tooltip if no translation is available for given <code>messageCode</code>
	 * @param messageCode Description translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 * @see LocalizationProvider
	 */
	default C tooltip(String defaultTooltip, String messageCode, Object... arguments) {
		return tooltip(Localizable.builder().message((defaultTooltip == null) ? "" : defaultTooltip).messageCode(messageCode)
				.messageArguments(arguments).build());
	}





}
