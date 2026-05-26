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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility methods to map backend data structures into fluent chart builder calls.
 *
 * @since 10.0.0
 */
public final class ChartJsSeriesMapper {

	private ChartJsSeriesMapper() {
		throw new UnsupportedOperationException("Utility class");
	}

	/**
	 * Strategy used when a series is missing one or more categories.
	 */
	public enum MissingCategoryStrategy {
		STRICT,
		FILL_NULL,
		FILL_ZERO
	}

	/**
	 * Long-form row representation for dynamic multi-series data.
	 * @param category x axis category
	 * @param seriesName series name
	 * @param value y value
	 */
	public record SeriesPoint(String category, String seriesName, Number value) {
		public SeriesPoint {
			ObjectUtils.argumentNotNull(category, "Series point category must be not null");
			ObjectUtils.argumentNotNull(seriesName, "Series point name must be not null");
			ObjectUtils.argumentNotNull(value, "Series point value must be not null");
		}
	}

	/**
	 * Aligned chart matrix generated from row data.
	 * @param categories aligned categories
	 * @param series aligned series values
	 */
	public record ChartMatrix(String[] categories, List<SeriesData> series) {
		public ChartMatrix {
			ObjectUtils.argumentNotNull(categories, "Categories must be not null");
			ObjectUtils.argumentNotNull(series, "Series must be not null");
		}
	}

	/**
	 * Series values aligned to chart categories.
	 * @param name series name
	 * @param values values aligned to categories
	 */
	public record SeriesData(String name, Number[] values) {
		public SeriesData {
			ObjectUtils.argumentNotNull(name, "Series name must be not null");
			ObjectUtils.argumentNotNull(values, "Series values must be not null");
		}
	}

	/**
	 * Extract categories from an ordered map.
	 * @param categoryValues ordered category-value map
	 * @return categories in insertion order
	 */
	public static String[] categories(Map<String, ? extends Number> categoryValues) {
		ObjectUtils.argumentNotNull(categoryValues, "Category values must be not null");
		return categoryValues.keySet().toArray(String[]::new);
	}

	/**
	 * Extract values from an ordered map.
	 * @param categoryValues ordered category-value map
	 * @return values in insertion order
	 */
	public static Number[] values(Map<String, ? extends Number> categoryValues) {
		ObjectUtils.argumentNotNull(categoryValues, "Category values must be not null");
		return categoryValues.values().toArray(Number[]::new);
	}

	/**
	 * Apply one named series from a category-value map to the builder.
	 * @param builder chart builder
	 * @param seriesName series name
	 * @param categoryValues ordered category-value map
	 * @return builder
	 */
	public static ChartJsBuilder applySeries(ChartJsBuilder builder, String seriesName,
			Map<String, ? extends Number> categoryValues) {
		ObjectUtils.argumentNotNull(builder, "Chart builder must be not null");
		ObjectUtils.argumentNotNull(seriesName, "Series name must be not null");
		ObjectUtils.argumentNotNull(categoryValues, "Category values must be not null");
		return builder.categories(categories(categoryValues)).addSeries(seriesName, values(categoryValues));
	}

	/**
	 * Build aligned chart data from long-form rows.
	 * @param points rows
	 * @param strategy missing category strategy
	 * @return aligned chart matrix
	 */
	public static ChartMatrix matrix(List<SeriesPoint> points, MissingCategoryStrategy strategy) {
		ObjectUtils.argumentNotNull(points, "Series points must be not null");
		ObjectUtils.argumentNotNull(strategy, "Missing category strategy must be not null");

		Set<String> categories = new LinkedHashSet<>();
		Map<String, Map<String, Number>> bySeries = new LinkedHashMap<>();
		for (SeriesPoint point : points) {
			ObjectUtils.argumentNotNull(point, "Series point must be not null");
			categories.add(point.category());
			bySeries.computeIfAbsent(point.seriesName(), key -> new LinkedHashMap<>())
				.put(point.category(), point.value());
		}

		List<String> categoryList = new ArrayList<>(categories);
		List<SeriesData> alignedSeries = new ArrayList<>();
		for (Map.Entry<String, Map<String, Number>> entry : bySeries.entrySet()) {
			String seriesName = entry.getKey();
			Map<String, Number> seriesValues = entry.getValue();
			Number[] aligned = new Number[categoryList.size()];
			for (int i = 0; i < categoryList.size(); i++) {
				String category = categoryList.get(i);
				Number value = seriesValues.get(category);
				if (value == null) {
					aligned[i] = defaultValue(strategy, seriesName, category);
				} else {
					aligned[i] = value;
				}
			}
			alignedSeries.add(new SeriesData(seriesName, aligned));
		}

		return new ChartMatrix(categoryList.toArray(String[]::new), alignedSeries);
	}

	/**
	 * Apply a matrix to the fluent chart builder.
	 * @param builder chart builder
	 * @param matrix chart matrix
	 * @return builder
	 */
	public static ChartJsBuilder applyMatrix(ChartJsBuilder builder, ChartMatrix matrix) {
		ObjectUtils.argumentNotNull(builder, "Chart builder must be not null");
		ObjectUtils.argumentNotNull(matrix, "Chart matrix must be not null");
		builder.categories(matrix.categories());
		for (SeriesData series : matrix.series()) {
			builder.addSeries(series.name(), series.values());
		}
		return builder;
	}

	private static Number defaultValue(MissingCategoryStrategy strategy, String seriesName, String category) {
		return switch (strategy) {
		case FILL_NULL -> null;
		case FILL_ZERO -> 0d;
		case STRICT -> throw new IllegalArgumentException(
			"Missing category ['" + category + "'] for series ['" + seriesName + "']");
		};
	}

}


