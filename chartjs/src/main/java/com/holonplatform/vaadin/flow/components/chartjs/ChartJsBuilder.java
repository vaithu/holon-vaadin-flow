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
import com.holonplatform.vaadin.flow.components.chartjs.internal.DefaultChartJsBuilder;

/**
 * Builder to create {@link ChartJsComponent} components.
 *
 * @since 10.0.0
 */
public interface ChartJsBuilder extends ChartJsConfigurator<ChartJsBuilder>,
		ComponentBuilder<ChartJsComponent, ChartJsBuilder> {

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

}




