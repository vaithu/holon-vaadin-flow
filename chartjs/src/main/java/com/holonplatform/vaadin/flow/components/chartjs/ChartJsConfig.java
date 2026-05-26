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
 * Immutable Chart.js configuration holder.
 *
 * <p>
 * {@code dataJson} and {@code optionsJson} are expected to be valid JSON
 * objects. The object model is intentionally left to Chart.js to keep this API
 * lightweight and backend-agnostic.
 * </p>
 *
 * @param chartType chart type
 * @param dataJson Chart.js {@code data} object as JSON
 * @param optionsJson Chart.js {@code options} object as JSON, nullable
 *
 * @since 10.0.0
 */
public record ChartJsConfig(ChartType chartType, String dataJson, String optionsJson) {

	/**
	 * Chart.js configuration holder.
	 */
	public ChartJsConfig {
		ObjectUtils.argumentNotNull(chartType, "Chart type must be not null");
		ObjectUtils.argumentNotNull(dataJson, "Chart data JSON must be not null");
	}

	/**
	 * Build a minimal Chart.js configuration with no explicit options.
	 * @param chartType chart type
	 * @param dataJson Chart.js {@code data} object as JSON
	 * @return configuration instance
	 */
	public static ChartJsConfig of(ChartType chartType, String dataJson) {
		return new ChartJsConfig(chartType, dataJson, null);
	}

	/**
	 * Build a minimal Chart.js configuration with typed data and no explicit options.
	 * @param chartType chart type
	 * @param data chart data
	 * @return configuration instance
	 */
	public static ChartJsConfig of(ChartType chartType, ChartJsData data) {
		ObjectUtils.argumentNotNull(data, "Chart data must be not null");
		return of(chartType, data.toJson());
	}

	/**
	 * Build a Chart.js configuration with typed data and options.
	 * @param chartType chart type
	 * @param data chart data
	 * @param options chart options
	 * @return configuration instance
	 */
	public static ChartJsConfig of(ChartType chartType, ChartJsData data, ChartJsOptions options) {
		ObjectUtils.argumentNotNull(data, "Chart data must be not null");
		ObjectUtils.argumentNotNull(options, "Chart options must be not null");
		return new ChartJsConfig(chartType, data.toJson(), options.toJson());
	}

}


