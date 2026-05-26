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
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;

/**
 * Chart.js web component wrapper for Vaadin Flow.
 *
 * <p>The custom element {@code holon-chartjs} is defined in
 * {@code META-INF/resources/chartjs/holon-chartjs.js}, served as a plain
 * static resource so it is available in every consuming project without
 * requiring a Vite frontend-bundle rebuild. Chart.js itself is loaded lazily
 * from the jsDelivr CDN the first time a chart is rendered.
 *
 * @since 10.0.0
 */
@Tag("holon-chartjs")
@JavaScript(value = "context://chartjs/holon-chartjs.js")
public class ChartJsComponent extends Component implements HasSize {

	/**
	 * Constructor.
	 */
	public ChartJsComponent() {
		super();
	}

	/**
	 * Constructor with initial configuration.
	 * @param config chart configuration
	 */
	public ChartJsComponent(ChartJsConfig config) {
		setConfig(config);
	}

	/**
	 * Set the full chart configuration.
	 * @param config chart configuration
	 */
	public void setConfig(ChartJsConfig config) {
		ObjectUtils.argumentNotNull(config, "Chart configuration must be not null");
		setType(config.chartType());
		setDataJson(config.dataJson());
		setOptionsJson(config.optionsJson());
		refresh();
	}

	/**
	 * Set the chart type.
	 * @param chartType chart type
	 */
	public void setType(ChartType chartType) {
		ObjectUtils.argumentNotNull(chartType, "Chart type must be not null");
		getElement().setProperty("chartType", chartType.getChartTypeId());
	}

	/**
	 * Set chart data as JSON string.
	 * @param dataJson Chart.js data object JSON
	 */
	public void setDataJson(String dataJson) {
		ObjectUtils.argumentNotNull(dataJson, "Chart data JSON must be not null");
		getElement().setProperty("dataJson", dataJson);
	}

	/**
	 * Set chart data using typed model.
	 * @param data chart data model
	 */
	public void setData(ChartJsData data) {
		ObjectUtils.argumentNotNull(data, "Chart data must be not null");
		setDataJson(data.toJson());
	}

	/**
	 * Set chart options as JSON string.
	 * @param optionsJson Chart.js options object JSON, nullable
	 */
	public void setOptionsJson(String optionsJson) {
		if (optionsJson == null) {
			getElement().removeProperty("optionsJson");
			return;
		}
		getElement().setProperty("optionsJson", optionsJson);
	}

	/**
	 * Set chart options using typed model.
	 * @param options chart options model, nullable
	 */
	public void setOptions(ChartJsOptions options) {
		setOptionsJson((options != null) ? options.toJson() : null);
	}

	/**
	 * Trigger chart re-rendering.
	 */
	public void refresh() {
		getElement().callJsFunction("refreshChart");
	}

	/**
	 * On attach, Vaadin batches all pending {@code setProperty()} calls together
	 * with this {@code callJsFunction()} into a single client update, so the
	 * client executes: set chartType → set dataJson → set optionsJson →
	 * refreshChart().  This short-circuits the microtask scheduled by the web
	 * component's {@code connectedCallback} and guarantees the chart is rendered
	 * synchronously after all properties are applied.
	 */
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		refresh();
	}


}


