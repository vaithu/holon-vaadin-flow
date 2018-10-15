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

import com.holonplatform.core.Context;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.vaadin.flow.component.HasText;

/**
 * Configurator for {@link HasText} type components.
 * 
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface HasTextConfigurator<C extends HasTextConfigurator<C>> {

	/**
	 * Sets the text content using a {@link Localizable} message. In order for the localization to work, a
	 * {@link LocalizationContext} must be valid (localized) and as a {@link Context} resource.
	 * <p>
	 * The text value is interpred as <em>plain text</em> and the HTML markup is not supported.
	 * </p>
	 * <p>
	 * A <code>null</code> value is interpreted as an empty text.
	 * </p>
	 * @param text Localizable text content message (may be null)
	 * @return this
	 * @see LocalizationContext#getCurrent()
	 */
	C text(Localizable text);

	/**
	 * Sets the text content, replacing any previous content.
	 * <p>
	 * The text value is interpred as <em>plain text</em> and the HTML markup is not supported.
	 * </p>
	 * <p>
	 * A <code>null</code> text value is interpreted as an empty text.
	 * </p>
	 * @param text The text content to set
	 * @return this
	 */
	default C text(String text) {
		return text((text == null) ? null : Localizable.builder().message(text).build());
	}

	/**
	 * Sets the text content using a localizable <code>messageCode</code>. In order for the localization to work, a
	 * {@link LocalizationContext} must be valid (localized) and as a {@link Context} resource.
	 * <p>
	 * The text value is interpred as <em>plain text</em> and the HTML markup is not supported.
	 * </p>
	 * @param defaultText Default text content if no translation is available for given <code>messageCode</code>.
	 * @param messageCode Text translation message key
	 * @param arguments Optional translation arguments
	 * @return this
	 * @see LocalizationContext#getCurrent()
	 */
	default C text(String defaultText, String messageCode, Object... arguments) {
		return text(Localizable.builder().message((defaultText == null) ? "" : defaultText).messageCode(messageCode)
				.messageArguments(arguments).build());
	}

}
