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
package com.holonplatform.vaadin.flow.components.chartjs;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.holonplatform.vaadin.flow.components.chartjs.internal.DefaultChartJsBuilder;

import java.util.function.Consumer;

/**
 * Builder to create {@link ChartJsComponent} components.
 *
 * @since 10.0.0
 */
public interface ChartJsBuilder extends ComponentBuilder<ChartJsComponent, ChartJsBuilder>,
		HasSizeConfigurator<ChartJsBuilder>, HasStyleConfigurator<ChartJsBuilder> {

	/**
	 * Create a new {@link ChartJsBuilder}.
	 * @return a new {@link ChartJsBuilder} instance
	 */
	static ChartJsBuilder create() {
		return new DefaultChartJsBuilder();
	}

	/**
	 * Create a new {@link ChartJsBuilder} with type and data set.
	 * @param chartType chart type
	 * @param dataJson Chart.js {@code data} object as JSON
	 * @return a new {@link ChartJsBuilder} instance
	 */
	static ChartJsBuilder create(ChartType chartType, String dataJson) {
		return create().type(chartType).data(dataJson);
	}

	/**
	 * Create a new {@link ChartJsBuilder} with type and typed data set.
	 * @param chartType chart type
	 * @param data chart data
	 * @return a new {@link ChartJsBuilder} instance
	 */
	static ChartJsBuilder create(ChartType chartType, ChartJsData data) {
		return create().type(chartType).data(data);
	}

	/**
	 * Set chart type.
	 * @param chartType chart type
	 * @return this
	 */
	ChartJsBuilder type(ChartType chartType);

	/**
	 * Set chart data as JSON string.
	 * @param dataJson Chart.js {@code data} object as JSON
	 * @return this
	 */
	ChartJsBuilder data(String dataJson);

	/**
	 * Set chart data using typed model.
	 * @param data Chart.js data model
	 * @return this
	 */
	ChartJsBuilder data(ChartJsData data);

	/**
	 * Add chart categories (x axis labels).
	 * @param categories chart categories
	 * @return this
	 */
	ChartJsBuilder categories(String... categories);

	/**
	 * Add a chart series/dataset.
	 * @param series series definition
	 * @return this
	 */
	ChartJsBuilder series(ChartJsDataset series);

	/**
	 * Add a chart series/dataset with name and values.
	 * @param name series name
	 * @param values series values
	 * @return this
	 */
	ChartJsBuilder series(String name, Number... values);

	/**
	 * Vaadin Charts-like alias for {@link #series(ChartJsDataset)}.
	 * @param series series definition
	 * @return this
	 */
	ChartJsBuilder addSeries(ChartJsDataset series);

	/**
	 * Vaadin Charts-like alias for {@link #series(String, Number...)}.
	 * @param name series name
	 * @param values series values
	 * @return this
	 */
	ChartJsBuilder addSeries(String name, Number... values);

	/**
	 * Set chart options as JSON string.
	 * @param optionsJson Chart.js {@code options} object as JSON
	 * @return this
	 */
	ChartJsBuilder options(String optionsJson);

	/**
	 * Set chart options using typed model.
	 * @param options Chart.js options model
	 * @return this
	 */
	ChartJsBuilder options(ChartJsOptions options);

	/**
	 * Set chart title.
	 * @param title title text
	 * @return this
	 */
	ChartJsBuilder title(String title);

	/**
	 * Vaadin Charts-like alias for {@link #title(String)}.
	 * @param title title text
	 * @return this
	 */
	ChartJsBuilder chartTitle(String title);

	/**
	 * Set chart subtitle.
	 * @param subtitle subtitle text
	 * @return this
	 */
	ChartJsBuilder subtitle(String subtitle);

	/**
	 * Vaadin Charts-like alias for {@link #subtitle(String)}.
	 * @param subtitle subtitle text
	 * @return this
	 */
	ChartJsBuilder chartSubTitle(String subtitle);

	/**
	 * Set responsive mode.
	 * @param responsive responsive mode
	 * @return this
	 */
	ChartJsBuilder responsive(boolean responsive);

	/**
	 * Set maintain aspect ratio.
	 * @param maintainAspectRatio maintain aspect ratio
	 * @return this
	 */
	ChartJsBuilder maintainAspectRatio(boolean maintainAspectRatio);

	/**
	 * Set legend visibility.
	 * @param enabled legend visibility
	 * @return this
	 */
	ChartJsBuilder legendEnabled(boolean enabled);

	/**
	 * Set shared tooltip interaction.
	 * @param shared shared tooltip
	 * @return this
	 */
	ChartJsBuilder tooltipShared(boolean shared);

	/**
	 * Set x axis title.
	 * @param title x axis title
	 * @return this
	 */
	ChartJsBuilder xAxisTitle(String title);

	/**
	 * Set y axis title.
	 * @param title y axis title
	 * @return this
	 */
	ChartJsBuilder yAxisTitle(String title);

	/**
	 * Configure X axis in a nested fluent step.
	 * @param configurator axis configurator
	 * @return this
	 */
	ChartJsBuilder xAxis(Consumer<AxisBuilder> configurator);

	/**
	 * Configure Y axis in a nested fluent step.
	 * @param configurator axis configurator
	 * @return this
	 */
	ChartJsBuilder yAxis(Consumer<AxisBuilder> configurator);

	/**
	 * Axis mini-builder for fluent axis configuration.
	 */
	interface AxisBuilder {

		AxisBuilder title(String title);

		AxisBuilder categories(String... categories);
	}

	/**
	 * Configure the chart using a complete {@link ChartJsConfig}.
	 * @param config chart configuration
	 * @return this
	 */
	ChartJsBuilder config(ChartJsConfig config);

}




