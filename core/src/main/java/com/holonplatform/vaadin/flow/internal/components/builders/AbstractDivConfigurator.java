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
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.builders.DivConfigurator;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

/**
 * Base {@link com.holonplatform.vaadin.flow.components.builders.DivConfigurator} implementation.
 * 
 * @param <C> Concrete configurator type
 *
 * @since 5.5.4
 */
public abstract class AbstractDivConfigurator<C extends DivConfigurator<C>>
		extends AbstractComponentConfigurator<Div, C> implements DivConfigurator<C> {

	public AbstractDivConfigurator(Div component) {
		super(component);
	}

	@Override
	protected Optional<HasSize> hasSize() {
		return Optional.of(getComponent());
	}

	@Override
	protected Optional<HasStyle> hasStyle() {
		return Optional.of(getComponent());
	}

	@Override
	protected Optional<HasEnabled> hasEnabled() {
		return Optional.of(getComponent());
	}

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }


	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasComponentsConfigurator#add(com.vaadin.flow.component.
	 * Component[])
	 */
	@Override
	public C add(Component... components) {
		getComponent().add(components);
		return getConfigurator();
	}

	public C title(LabelBuilder<H4> title) {
		getComponent().add(
				title
						.build()
		);
		return getConfigurator();
	}

	/**
	 * Sets the title text using a {@link Localizable} message.
	 * <p>
	 * Browsers typically use the title to show a tooltip when hovering an element
	 * <p>
	 * HTML markup is not supported.
	 * <p>
	 * A <code>null</code> value will remove the title.
	 * </p>
	 *
	 * @param title Localizable title message (may be null)
	 * @return this
	 * @see LocalizationProvider
	 */
	@Override
	public C title(Localizable title) {
		return title(LabelBuilder.h4().title(title));
	}

	@Override
	public C title(String title) {
		return title(UIUtils.createH4(title));
	}

	@Override
	public C add(String title, Component... components) {
		return title(UIUtils.createH4(title)).add(components);
	}

	@Override
	public C add(String title, HasComponent... components) {
		return title(UIUtils.createH4(title)).add(components);
	}
}
