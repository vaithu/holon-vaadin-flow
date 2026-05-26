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

/**
 * Supported Chart.js chart types.
 *
 * <p>Built-in Chart.js types require no additional dependencies.
 * Plugin-based types require the corresponding npm package to be on the
 * classpath and registered in {@code holon-chartjs.js}; see each constant's
 * Javadoc for the required package.</p>
 *
 * @since 10.0.0
 */
public enum ChartType {

	// ── Built-in Chart.js types ───────────────────────────────────────────

	BAR("bar"),
	LINE("line"),
	PIE("pie"),
	DOUGHNUT("doughnut"),
	POLAR_AREA("polarArea"),
	RADAR("radar"),
	BUBBLE("bubble"),
	SCATTER("scatter"),

	// ── Plugin-based types ────────────────────────────────────────────────

	/**
	 * Matrix / heatmap chart.
	 *
	 * <p>Use cases: CRM call-centre activity grid (agent × hour-of-day);
	 * Pawn seasonal demand heatmap (category × month).</p>
	 *
	 * <p>Requires npm package {@code chartjs-chart-matrix}.</p>
	 */
	MATRIX("matrix"),

	/**
	 * Treemap chart.
	 *
	 * <p>Use cases: Pawn inventory portfolio breakdown by category and value
	 * (jewelry, electronics, watches…); CRM revenue breakdown by product line
	 * or region.</p>
	 *
	 * <p>Requires npm package {@code chartjs-chart-treemap}.</p>
	 */
	TREEMAP("treemap");

	private final String chartTypeId;

	ChartType(String chartTypeId) {
		this.chartTypeId = chartTypeId;
	}

	/**
	 * Get the Chart.js type identifier.
	 * @return Chart.js type identifier
	 */
	public String getChartTypeId() {
		return chartTypeId;
	}

}

