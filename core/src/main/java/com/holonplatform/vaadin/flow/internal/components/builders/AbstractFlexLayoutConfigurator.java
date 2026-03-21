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

import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.builders.FlexLayoutConfigurator;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

/**
 * Base {@link com.holonplatform.vaadin.flow.components.builders.FlexLayoutConfigurator} implementation.
 * 
 * @param <C> Concrete configurator type
 *
 * @since 5.5.4
 */
public abstract class AbstractFlexLayoutConfigurator<C extends FlexLayoutConfigurator<C>>
		extends AbstractComponentConfigurator<FlexLayout, C> implements FlexLayoutConfigurator<C> {

	public AbstractFlexLayoutConfigurator(FlexLayout component) {
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

	@Override
	public C addComponentAsFirst(Component component) {
		getComponent().addComponentAsFirst(component);
		return getConfigurator();
	}

	@Override
	public C addComponentAtIndex(int index, Component component) {
		getComponent().addComponentAtIndex(index, component);
		return getConfigurator();
	}

	@Override
	public C add(String text) {
		getComponent().add(text);
		return getConfigurator();
	}

	@Override
	public C alignContent(FlexLayout.ContentAlignment alignment) {
		getComponent().setAlignContent(alignment);
		return getConfigurator();
	}

	@Override
	public C flexBasis(String width, HasElement... components) {
		getComponent().setFlexBasis(width,components);
		return getConfigurator();
	}

	@Override
	public C flexDirection(FlexLayout.FlexDirection flexDirection) {
		getComponent().setFlexDirection(flexDirection);
		return getConfigurator();
	}

	@Override
	public C flexWrap(FlexLayout.FlexWrap flexWrap) {
		getComponent().setFlexWrap(flexWrap);
		return getConfigurator();
	}

	@Override
	public C order(int order, HasElement component) {
		getComponent().setOrder(order, component);
		return getConfigurator();
	}

	@Override
	public C title(LabelBuilder<H4> title) {
		getComponent().add(
				title
						.build()
		);
		return getConfigurator();
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
