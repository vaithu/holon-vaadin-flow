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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.Logger;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.HasDeferrableLocalization;
import com.holonplatform.vaadin.flow.components.builders.HasHelperTextConfigurator;
import com.holonplatform.vaadin.flow.internal.VaadinLogger;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasHelper;

import java.util.function.Consumer;

/**
 * Default {@link com.holonplatform.vaadin.flow.components.builders.HasHelperTextConfigurator} implementation.
 *
 * @since 5.5.8
 */
public class DefaultHasHelperTextConfigurator<C extends HasHelper> extends AbstractLocalizationSupportConfigurator<C>
		implements HasHelperTextConfigurator<DefaultHasHelperTextConfigurator<C>> {

	private static final Logger LOGGER = VaadinLogger.create();

	private final C component;

	/**
	 * Constructor.
	 * @param component The component to configure (not null)
	 * @param setHelperText Actual operation to set the helper text (not null)
	 */
	public DefaultHasHelperTextConfigurator(C component, Consumer<String> setHelperText) {
		this(component, setHelperText, null);
	}

	/**
	 * Constructor.
	 * @param component The component to configure (not null)
	 * @param setHelperText Actual operation to set the helper text (not null)
	 * @param deferrableLocalization Optional {@link HasDeferrableLocalization} reference
	 */
	public DefaultHasHelperTextConfigurator(C component, Consumer<String> setHelperText,
                                            HasDeferrableLocalization deferrableLocalization) {
		super(text -> setHelperText.accept(text), deferrableLocalization);
		ObjectUtils.argumentNotNull(component, "The component to configure must be not null");
		this.component = component;
	}


	/**
	 * String used for the helper text. It shows a text adjacent to the field that can be used, e.g., to inform to the
	 * users which values it expects. Example: a text "The password must contain numbers" for the PasswordField.
	 * In case both setHelperText(String) and setHelperComponent(Component) are used, only the element defined by
	 * setHelperComponent(Component) will be visible, regardless of the order on which they are defined.
	 *
	 * @param helperText
	 * @return
	 */
	@Override
	public DefaultHasHelperTextConfigurator<C> helperText(String helperText) {
		component.setHelperText(helperText);
		return this;
	}

	@Override
	public DefaultHasHelperTextConfigurator<C> helperText(Localizable helperText) {
		if (localize(component, helperText)) {
			LOGGER.debug(() -> "Component [" + component + "] helperText localization was deferred");
		}
		return this;
	}

	/**
	 * Adds the given component into helper slot of component, replacing any existing helper component.
	 * It adds the component adjacent to the field that can be used,
	 * e.g., to inform to the users which values it expects.
	 * Example: a component that shows the password strength for the PasswordField.
	 *
	 * @param component the component to set, can be null to remove existing helper component
	 * @return
	 */
	@Override
	public DefaultHasHelperTextConfigurator<C> helperComponent(Component component) {
		this.component.setHelperComponent(component);
		return this;
	}
}
