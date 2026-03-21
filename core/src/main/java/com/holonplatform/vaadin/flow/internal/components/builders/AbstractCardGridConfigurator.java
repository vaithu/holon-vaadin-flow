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

import com.holonplatform.vaadin.flow.components.builders.CardGridConfigurator;
import com.holonplatform.vaadin.flow.internal.components.support.BreakPoint;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Optional;

/**
 * Base {@link CardGridConfigurator} implementation.
 * 
 * @param <C> Concrete configurator type
 *
 * @since 5.5.4
 */
public abstract class AbstractCardGridConfigurator<C extends CardGridConfigurator<C>>
		extends AbstractComponentConfigurator<Div, C> implements CardGridConfigurator<C> {

	public AbstractCardGridConfigurator(Div component) {
		super(component);
		getComponent().addClassNames(LumoUtility.Display.GRID, LumoUtility.Grid.FLOW_ROW, LumoUtility.AlignItems.START);
	}

	/**
	 * If the component supports {@link HasSize}, return the component as {@link HasSize}.
	 *
	 * @return Optional component as {@link HasSize}, if supported
	 */
	@Override
	protected Optional<HasSize> hasSize() {
		return Optional.of(getComponent());
	}

	/**
	 * If the component supports {@link HasStyle}, return the component as {@link HasStyle}.
	 *
	 * @return Optional component as {@link HasStyle}, if supported
	 */
	@Override
	protected Optional<HasStyle> hasStyle() {
		return Optional.of(getComponent());
	}

	/**
	 * If the component supports {@link HasEnabled}, return the component as {@link HasEnabled}.
	 *
	 * @return Optional component as {@link HasEnabled}, if supported
	 */
	@Override
	protected Optional<HasEnabled> hasEnabled() {
		return Optional.of(getComponent());
	}

	/**
	 * If the component supports {@link HasTooltip}, return the component as {@link HasTooltip}.
	 *
	 * @return Optional component as {@link HasTooltip}, if supported
	 */
	@Override
	protected Optional<HasTooltip> hasTooltip() {
		return Optional.empty();
	}
	@Override
	public CardBuilder<C> withCard(Div content) {
		return new DefaultCardBuilder<>(getConfigurator(), content);
	}

	@Override
	public CardBuilder<C> withCard(Component... components) {
		return new DefaultCardBuilder<>(getConfigurator(), components);
	}

	@Override
	public CardBuilder<C> withCard(String title, Component... components) {
		return new DefaultCardBuilder<>(getConfigurator(),title, components);
	}

	@Override
	public C _addToParent(Div component) {
		getComponent().add(component);
		return getConfigurator();
	}

	@Override
	public C gap(boolean gap) {
		if (gap) {
			getComponent().addClassName(LumoUtility.Gap.MEDIUM);
		}
		return getConfigurator();
	}



	@Override
	public C column(int column) {
		getComponent().addClassName("grid-cols-" + column);
		return getConfigurator();
	}

	@Override
	public C row(int row) {
		getComponent().addClassName("grid-rows-" + row);
		return getConfigurator();
	}

	@Override
	public C small(int column) {
		getComponent().addClassName("sm:grid-cols-" + column);
		return getConfigurator();
	}

	@Override
	public C medium(int column) {
		getComponent().addClassName("md:grid-cols-" + column);
		return getConfigurator();
	}

	@Override
	public C large(int column) {
		getComponent().addClassName("lg:grid-cols-" + column);
		return getConfigurator();
	}

	@Override
	public C xLarge(int column) {
		getComponent().addClassName("xl:grid-cols-" + column);
		return getConfigurator();
	}

	@Override
	public C xxLarge(int column) {

		getComponent().addClassName("2xl:grid-cols-" + column);
		return getConfigurator();
	}

	@Override
	public C column(BreakPoint breakPoint, int column) {

		switch (breakPoint) {
            case BREAKPOINT_SM -> {
                return small(column);
			}
			case BREAKPOINT_MD -> {
				return medium(column);
			}
			case BREAKPOINT_LG -> {
				return large(column);
			}
			case BREAKPOINT_XL -> {
				return xLarge(column);
			}
			default -> {
				return xxLarge(column);
			}
		}
	}
}
