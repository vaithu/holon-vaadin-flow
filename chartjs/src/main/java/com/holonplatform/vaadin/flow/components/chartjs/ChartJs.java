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

import com.holonplatform.core.internal.utils.ObjectUtils;

/**
 * Factory utilities for Chart.js Flow components.
 *
 * @since 10.0.0
 */
public final class ChartJs {

	private ChartJs() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Create a new {@link ChartJsBuilder}.
	 * @return a new {@link ChartJsBuilder}
	 */
	public static ChartJsBuilder builder() {
		return ChartJsBuilder.create();
	}

	/**
	 * Create a new {@link ChartJsBuilder} with type and data set.
	 * @param chartType chart type
	 * @param dataJson data object JSON
	 * @return a new {@link ChartJsBuilder}
	 */
	public static ChartJsBuilder builder(ChartType chartType, String dataJson) {
		return ChartJsBuilder.create(chartType, dataJson);
	}

	/**
	 * Create a new {@link ChartJsBuilder} with type and typed data set.
	 * @param chartType chart type
	 * @param data chart data
	 * @return a new {@link ChartJsBuilder}
	 */
	public static ChartJsBuilder builder(ChartType chartType, ChartJsData data) {
		return ChartJsBuilder.create(chartType, data);
	}

	/**
	 * Create a new {@link ChartJsBuilder} with a complete chart configuration.
	 * @param config chart configuration
	 * @return a new {@link ChartJsBuilder}
	 */
	public static ChartJsBuilder builder(ChartJsConfig config) {
		ObjectUtils.argumentNotNull(config, "Chart configuration must be not null");
		return builder().config(config);
	}


}




