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
import java.util.List;
import java.util.Map;

/**
 * Typed representation of a Chart.js data object.
 *
 * @since 10.0.0
 */
public final class ChartJsData implements ChartJsJsonValue {

	private final List<String> labels;
	private final List<ChartJsDataset> datasets;

	private ChartJsData(List<String> labels, List<ChartJsDataset> datasets) {
		this.labels = List.copyOf(labels);
		this.datasets = List.copyOf(datasets);
	}

	/**
	 * Get a builder to create {@link ChartJsData}.
	 * @return a new {@link Builder}
	 */
	public static Builder create() {
		return new DefaultBuilder();
	}

	/**
	 * Get a builder to create {@link ChartJsData}.
	 * @return a new {@link Builder}
	 */
	public static Builder builder() {
		return create();
	}

	/**
	 * Get a builder to create {@link ChartJsData}.
	 */
	public interface Builder {

		Builder label(String label);

		Builder labels(String... labels);

		/**
		 * Vaadin Charts-like alias for {@link #labels(String...)}.
		 * @param categories category labels
		 * @return this
		 */
		Builder categories(String... categories);

		Builder dataset(ChartJsDataset dataset);

		Builder datasets(ChartJsDataset... datasets);

		/**
		 * Vaadin Charts-like alias for {@link #dataset(ChartJsDataset)}.
		 * @param series data series
		 * @return this
		 */
		Builder series(ChartJsDataset series);

		/**
		 * Vaadin Charts-like alias for {@link #datasets(ChartJsDataset...)}.
		 * @param series data series
		 * @return this
		 */
		Builder series(ChartJsDataset... series);

		ChartJsData build();
	}

	@Override
	public Object toJsonValue() {
		Map<String, Object> value = new LinkedHashMap<>();
		value.put("labels", labels);
		value.put("datasets", datasets);
		return value;
	}

	String toJson() {
		return ChartJsJsonSerializer.toJson(this);
	}

	private static class DefaultBuilder implements Builder {

		private final List<String> labels = new ArrayList<>();
		private final List<ChartJsDataset> datasets = new ArrayList<>();

		@Override
		public Builder label(String label) {
			ObjectUtils.argumentNotNull(label, "Chart label must be not null");
			labels.add(label);
			return this;
		}

		@Override
		public Builder labels(String... labels) {
			ObjectUtils.argumentNotNull(labels, "Chart labels must be not null");
			for (String label : labels) {
				label(label);
			}
			return this;
		}

		@Override
		public Builder categories(String... categories) {
			return labels(categories);
		}

		@Override
		public Builder dataset(ChartJsDataset dataset) {
			ObjectUtils.argumentNotNull(dataset, "Dataset must be not null");
			datasets.add(dataset);
			return this;
		}

		@Override
		public Builder datasets(ChartJsDataset... datasets) {
			ObjectUtils.argumentNotNull(datasets, "Datasets must be not null");
			for (ChartJsDataset dataset : datasets) {
				dataset(dataset);
			}
			return this;
		}

		@Override
		public Builder series(ChartJsDataset series) {
			return dataset(series);
		}

		@Override
		public Builder series(ChartJsDataset... series) {
			return datasets(series);
		}

		@Override
		public ChartJsData build() {
			return new ChartJsData(labels, datasets);
		}
	}

}



