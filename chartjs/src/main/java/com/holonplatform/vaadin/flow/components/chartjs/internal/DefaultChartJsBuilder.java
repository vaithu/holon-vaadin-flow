/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.flow.components.chartjs.internal;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsBuilder;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsComponent;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsConfig;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsData;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsDataset;
import com.holonplatform.vaadin.flow.components.chartjs.ChartJsOptions;
import com.holonplatform.vaadin.flow.components.chartjs.ChartType;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.DomEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Default {@link ChartJsBuilder} implementation.
 */
public class DefaultChartJsBuilder implements ChartJsBuilder {

	private final ChartJsComponent component;
	private final ChartJsData.Builder fluentDataBuilder = ChartJsData.create();
	private final ChartJsOptions.Builder fluentOptionsBuilder = ChartJsOptions.create();

	private long orderCounter;
	private long explicitDataOrder = -1;
	private long explicitOptionsOrder = -1;
	private long fluentDataOrder = -1;
	private long fluentOptionsOrder = -1;

	/** Post-processors applied just before {@code build()} returns. */
	private final List<Consumer<ChartJsComponent>> postProcessors = new ArrayList<>();

	public DefaultChartJsBuilder() {
		this.component = new ChartJsComponent();
	}

	@Override
	public ChartJsBuilder type(ChartType chartType) {
		component.setType(chartType);
		return this;
	}

	@Override
	public ChartJsBuilder data(String dataJson) {
		component.setDataJson(dataJson);
		explicitDataOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder data(ChartJsData data) {
		component.setData(data);
		explicitDataOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder categories(String... categories) {
		fluentDataBuilder.categories(categories);
		fluentDataOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder series(ChartJsDataset series) {
		fluentDataBuilder.series(series);
		fluentDataOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder series(String name, Number... values) {
		fluentDataBuilder.series(ChartJsDataset.create().name(name).values(values).build());
		fluentDataOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder addSeries(ChartJsDataset series) {
		return series(series);
	}

	@Override
	public ChartJsBuilder addSeries(String name, Number... values) {
		return series(name, values);
	}

	@Override
	public ChartJsBuilder options(String optionsJson) {
		component.setOptionsJson(optionsJson);
		explicitOptionsOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder options(ChartJsOptions options) {
		component.setOptions(options);
		explicitOptionsOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder title(String title) {
		fluentOptionsBuilder.title(title);
		fluentOptionsOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder chartTitle(String title) {
		return title(title);
	}

	@Override
	public ChartJsBuilder subtitle(String subtitle) {
		fluentOptionsBuilder.subtitle(subtitle);
		fluentOptionsOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder chartSubTitle(String subtitle) {
		return subtitle(subtitle);
	}

	@Override
	public ChartJsBuilder responsive(boolean responsive) {
		fluentOptionsBuilder.responsive(responsive);
		fluentOptionsOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder maintainAspectRatio(boolean maintainAspectRatio) {
		fluentOptionsBuilder.maintainAspectRatio(maintainAspectRatio);
		fluentOptionsOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder legendEnabled(boolean enabled) {
		fluentOptionsBuilder.legendEnabled(enabled);
		fluentOptionsOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder tooltipShared(boolean shared) {
		fluentOptionsBuilder.tooltipShared(shared);
		fluentOptionsOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder xAxisTitle(String title) {
		fluentOptionsBuilder.xAxisTitle(title);
		fluentOptionsOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder yAxisTitle(String title) {
		fluentOptionsBuilder.yAxisTitle(title);
		fluentOptionsOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder xAxis(Consumer<AxisBuilder> configurator) {
		ObjectUtils.argumentNotNull(configurator, "X axis configurator must be not null");
		configurator.accept(new DefaultAxisBuilder("x"));
		fluentOptionsOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder yAxis(Consumer<AxisBuilder> configurator) {
		ObjectUtils.argumentNotNull(configurator, "Y axis configurator must be not null");
		configurator.accept(new DefaultAxisBuilder("y"));
		fluentOptionsOrder = nextOrder();
		return this;
	}

	@Override
	public ChartJsBuilder config(ChartJsConfig config) {
		component.setConfig(config);
		explicitDataOrder = nextOrder();
		explicitOptionsOrder = explicitDataOrder;
		return this;
	}

	@Override
	public ChartJsBuilder id(String id) {
		component.setId(id);
		return this;
	}

	@Override
	public ChartJsBuilder visible(boolean visible) {
		component.setVisible(visible);
		return this;
	}

	@Override
	public ChartJsBuilder elementConfiguration(Consumer<Element> element) {
		ObjectUtils.argumentNotNull(element, "Element consumer must be not null");
		element.accept(component.getElement());
		return this;
	}

	@Override
	public ChartJsBuilder withAttachListener(ComponentEventListener<AttachEvent> listener) {
		component.addAttachListener(listener);
		return this;
	}

	@Override
	public ChartJsBuilder withDetachListener(ComponentEventListener<DetachEvent> listener) {
		component.addDetachListener(listener);
		return this;
	}

	@Override
	public ChartJsBuilder withThemeName(String themeName) {
		component.getElement().getThemeList().add(themeName);
		return this;
	}

	@Override
	public ChartJsBuilder withEventListener(String eventType, DomEventListener listener) {
		component.getElement().addEventListener(eventType, listener);
		return this;
	}

	@Override
	public ChartJsBuilder withEventListener(String eventType, DomEventListener listener, String filter) {
		component.getElement().addEventListener(eventType, listener).setFilter(filter);
		return this;
	}

	@Override
	public ChartJsBuilder width(String width) {
		component.setWidth(width);
		return this;
	}

	@Override
	public ChartJsBuilder height(String height) {
		component.setHeight(height);
		return this;
	}

	@Override
	public ChartJsBuilder minWidth(String minWidth) {
		component.setMinWidth(minWidth);
		return this;
	}

	@Override
	public ChartJsBuilder maxWidth(String maxWidth) {
		component.setMaxWidth(maxWidth);
		return this;
	}

	@Override
	public ChartJsBuilder minHeight(String minHeight) {
		component.setMinHeight(minHeight);
		return this;
	}

	@Override
	public ChartJsBuilder maxHeight(String maxHeight) {
		component.setMaxHeight(maxHeight);
		return this;
	}

	@Override
	public ChartJsBuilder styleNames(String... styleNames) {
		component.addClassNames(styleNames);
		return this;
	}

	@Override
	public ChartJsBuilder styleName(String styleName) {
		component.addClassName(styleName);
		return this;
	}

	@Override
	public ChartJsBuilder withBuildPostProcessor(Consumer<ChartJsComponent> postProcessor) {
		Objects.requireNonNull(postProcessor, "Post-processor must not be null");
		this.postProcessors.add(postProcessor);
		return this;
	}

	@Override
	public ChartJsComponent build() {
		if (fluentDataOrder > explicitDataOrder) {
			component.setData(fluentDataBuilder.build());
		}
		if (fluentOptionsOrder > explicitOptionsOrder) {
			component.setOptions(fluentOptionsBuilder.build());
		}
		postProcessors.forEach(pp -> pp.accept(component));
		return component;
	}

	private long nextOrder() {
		return ++orderCounter;
	}

	private class DefaultAxisBuilder implements AxisBuilder {

		private final String axis;

		private DefaultAxisBuilder(String axis) {
			this.axis = axis;
		}

		@Override
		public AxisBuilder title(String title) {
			if ("x".equals(axis)) {
				xAxisTitle(title);
			} else {
				yAxisTitle(title);
			}
			return this;
		}

		@Override
		public AxisBuilder categories(String... categories) {
			if ("x".equals(axis)) {
				DefaultChartJsBuilder.this.categories(categories);
			} else {
				fluentOptionsBuilder.nestedProperty("scales.y.labels", java.util.List.of(categories));
				fluentOptionsOrder = nextOrder();
			}
			return this;
		}
	}

}






