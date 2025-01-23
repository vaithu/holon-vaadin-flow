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
import com.holonplatform.vaadin.flow.components.builders.DividerBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.components.builders.LayoutBuilder;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.lumo.ColumnSpan;
import com.holonplatform.vaadin.flow.internal.lumo.FlexDirection;
import com.holonplatform.vaadin.flow.internal.lumo.Gap;
import com.holonplatform.vaadin.flow.internal.lumo.RowSpan;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Optional;
import java.util.function.Consumer;


/**
 * Base {@link com.holonplatform.vaadin.flow.components.builders.DivConfigurator} implementation.
 * 
 * @param <C> Concrete configurator type
 *
 * @since 5.5.4
 */
public abstract class AbstractDivConfigurator<C extends DivConfigurator<C>>
		extends AbstractComponentConfigurator<Div, C> implements DivConfigurator<C> {

	private DividerBuilder dividerBuilder;
	private final String backgroundColor = LumoUtility.Background.CONTRAST_10;
	private boolean isGrid;

	public AbstractDivConfigurator(Div component) {
		super(component);
	}

	private void initializeDivider() {
		dividerBuilder = new DefaultDividerBuilder(new Span());
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

	@Override
	public C responsive() {
		getComponent().addClassNames(
				LumoUtility.AlignItems.Breakpoint.Medium.BASELINE,
				LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
			LumoUtility.FlexDirection.Breakpoint.Medium.ROW, LumoUtility.Gap.Column.MEDIUM);
		return getConfigurator();
	}

	@Override
	public C gridLayout(String... styles) {
		getComponent().addClassNames(styles);
		return getConfigurator();
	}

	@Override
	public C gridLayout(int columns) {
		getComponent().addClassNames(
				// < 1024 pixels
				LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
				// > 1024 pixels
				LumoUtility.Display.Breakpoint.Medium.GRID, "grid-cols-" + columns,
				// Horizontal spacing between components
				LumoUtility.Gap.Column.MEDIUM

		);
		return getConfigurator();
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
	public C title(LabelBuilder<H4> title) {
		getComponent().add(
				title
						.build()
		);
		return getConfigurator();
	}

	@Override
	public C title(Component prefix, String title) {
		getComponent().add(
				LayoutBuilder.create().add(prefix).add(title).flexDirection(FlexDirection.ROW)
						.gap(Gap.SMALL)
						.build()
		);
		return getConfigurator();
	}

	@Override
	public C add(int columnSpan, Component... components) {
makeItGrid();
		for (Component component : components) {
			component.addClassName("col-span-" + columnSpan);
		}
		add(components);
		return getConfigurator();
	}

	@Override
	public C add(ColumnSpan columnSpan, Component... components) {
		makeItGrid();
		for (Component component : components) {

			component.addClassName(columnSpan.getClassName());

		}
		add(components);
		return getConfigurator();
	}

	@Override
	public C add(RowSpan rowSpan, Component... components) {
		makeItGrid();
		for (Component component : components) {
			component.addClassName(rowSpan.getValue());
		}

		add(components);
		return getConfigurator();
	}


	@Override
	public C add(ColumnSpan columnSpan, RowSpan rowSpan, Component... components) {
makeItGrid();
		for (Component component : components) {
			component.addClassNames(columnSpan.getClassName(), rowSpan.getValue());
		}
		add(components);
		return getConfigurator();
	}

	@Override
	public C add(int columnSpan, HasComponent... components) {
		makeItGrid();
		for (HasComponent component : components) {
			component.hasStyle().ifPresent(hasStyle -> hasStyle.addClassName("col-span-" + columnSpan));
			getComponent().add(component.getComponent());

		}
		return getConfigurator();
	}

	private void makeItGrid() {
		if (!isGrid) {
			getComponent().addClassName(LumoUtility.Display.GRID);
			isGrid = true;
		}
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
	 * @param title Localizable title message (maybe null)
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

	@Override
	public C horizontalRule() {
		initializeDivider();
		getComponent().add(dividerBuilder.horizontalSeparator()
						.styleNames(backgroundColor)
				.build());
		return getConfigurator();
	}

	private void removeDividerBackgroundClassName() {
		initializeDivider();
		final Span divider = dividerBuilder.horizontalSeparator().build();
		if (divider.getClassNames().contains(backgroundColor)){
			divider.removeClassName(backgroundColor);
		}
	}

	@Override
	public C horizontalRule(String... styles) {
		initializeDivider();
		removeDividerBackgroundClassName();
		getComponent().add(dividerBuilder.horizontalSeparator().styleNames(styles).build());
		return getConfigurator();
	}

	@Override
	public C horizontalRule(int size, String... styles) {
		initializeDivider();
		removeDividerBackgroundClassName();
		getComponent().add(dividerBuilder.horizontalSeparator()
						.size(size)
						.styleNames(styles)
				.build());
		return getConfigurator();
	}

	@Override
	public C withPostProcessor(Consumer<DivConfigurator<C>> postProcessor) {
		postProcessor.accept(this);
		return getConfigurator();
	}
}
