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
import com.holonplatform.vaadin.flow.components.Selectable;
import com.holonplatform.vaadin.flow.components.Selectable.SelectionListener;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;

/**
 * Configurator for {@link Selectable} input components.
 * 
 * @param <T> Value type
 * @param <S> Selection type
 * @param <C> Concrete configurator type
 * 
 * @since 5.2.0
 */
public interface SelectableInputConfigurator<T, S, C extends SelectableInputConfigurator<T, S, C>>
		extends InputConfigurator<T, ValueChangeEvent<T>, C>, HasStyleConfigurator<C>, HasEnabledConfigurator<C>,
		DeferrableLocalizationConfigurator<C>, HasAriaLabelConfigurator<C> {

	@Override
	default C ariaLabel(String ariaLabel) {
		return elementConfiguration(element -> element.setAttribute("aria-label", ariaLabel));
	}

	@Override
	default C ariaLabelledBy(String ariaLabelledBy) {
		return elementConfiguration(element -> element.setAttribute("aria-labelledby", ariaLabelledBy));
	}

	@Override
	default C ariaLabel(Localizable ariaLabel) {
		final String defaultAriaLabel = (ariaLabel != null && ariaLabel.getMessage() != null) ? ariaLabel.getMessage() : "";
		if (ariaLabel == null) {
			return ariaLabel(defaultAriaLabel);
		}
		if (isDeferredLocalizationEnabled()) {
			ariaLabel(defaultAriaLabel);
			return withAttachListener(event -> {
				if (event.isInitialAttach()) {
					LocalizationProvider.localize(ariaLabel).ifPresent(this::ariaLabel);
				}
			});
		}
		return ariaLabel(LocalizationProvider.localize(ariaLabel).orElse(defaultAriaLabel));
	}

	/**
	 * Adds a {@link SelectionListener} to listen to selection changes.
	 * @param selectionListener The listener to add (not null)
	 * @return this
	 */
	C withSelectionListener(SelectionListener<S> selectionListener);

}
