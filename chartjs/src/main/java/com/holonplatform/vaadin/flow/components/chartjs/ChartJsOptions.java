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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Typed representation of a Chart.js options object.
 *
 * @since 10.0.0
 */
public final class ChartJsOptions implements ChartJsJsonValue {

	private final Map<String, Object> values;

	private ChartJsOptions(Map<String, Object> values) {
		this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
	}

	/**
	 * Get a builder to create {@link ChartJsOptions}.
	 * @return a new {@link Builder}
	 */
	public static Builder create() {
		return new DefaultBuilder();
	}

	/**
	 * Get a builder to create {@link ChartJsOptions}.
	 * @return a new {@link Builder}
	 */
	public static Builder builder() {
		return create();
	}

	/**
	 * Get a builder to create {@link ChartJsOptions}.
	 */
	public interface Builder {

		/**
		 * Chart title.
		 * @param title title text
		 * @return this
		 */
		Builder title(String title);

		/**
		 * Chart subtitle.
		 * @param subtitle subtitle text
		 * @return this
		 */
		Builder subtitle(String subtitle);

		Builder responsive(boolean responsive);

		Builder maintainAspectRatio(boolean maintainAspectRatio);

		/**
		 * Set legend visibility.
		 * @param display {@code true} to show legend
		 * @return this
		 */
		Builder legend(boolean display);

		/**
		 * Vaadin Charts-like alias for {@link #legend(boolean)}.
		 * @param enabled {@code true} to show legend
		 * @return this
		 */
		Builder legendEnabled(boolean enabled);

		/**
		 * Set shared-tooltip style interaction, similar to Vaadin Charts defaults.
		 * @param shared {@code true} to use shared-tooltip behavior
		 * @return this
		 */
		Builder sharedTooltip(boolean shared);

		/**
		 * Vaadin Charts-like alias for {@link #sharedTooltip(boolean)}.
		 * @param shared {@code true} to use shared-tooltip behavior
		 * @return this
		 */
		Builder tooltipShared(boolean shared);

		/**
		 * Set x axis title text.
		 * @param title axis title
		 * @return this
		 */
		Builder xAxisTitle(String title);

		/**
		 * Set y axis title text.
		 * @param title axis title
		 * @return this
		 */
		Builder yAxisTitle(String title);

		Builder property(String name, Object value);

		Builder nestedProperty(String path, Object value);

		ChartJsOptions build();
	}

	@Override
	public Object toJsonValue() {
		return values;
	}

	String toJson() {
		return ChartJsJsonSerializer.toJson(this);
	}

	private static class DefaultBuilder implements Builder {

		private final Map<String, Object> values = new LinkedHashMap<>();

		private DefaultBuilder() {
			// Keep defaults close to Vaadin Charts/Highcharts behavior (responsive + shared tooltip-like interaction).
			responsive(true);
			maintainAspectRatio(false);
			legend(true);
			sharedTooltip(true);
		}

		@Override
		public Builder responsive(boolean responsive) {
			return property("responsive", responsive);
		}

		@Override
		public Builder title(String title) {
			ObjectUtils.argumentNotNull(title, "Chart title must be not null");
			nestedProperty("plugins.title.display", !title.isBlank());
			return nestedProperty("plugins.title.text", title);
		}

		@Override
		public Builder subtitle(String subtitle) {
			ObjectUtils.argumentNotNull(subtitle, "Chart subtitle must be not null");
			nestedProperty("plugins.subtitle.display", !subtitle.isBlank());
			return nestedProperty("plugins.subtitle.text", subtitle);
		}

		@Override
		public Builder maintainAspectRatio(boolean maintainAspectRatio) {
			return property("maintainAspectRatio", maintainAspectRatio);
		}

		@Override
		public Builder legend(boolean display) {
			return nestedProperty("plugins.legend.display", display);
		}

		@Override
		public Builder legendEnabled(boolean enabled) {
			return legend(enabled);
		}

		@Override
		public Builder sharedTooltip(boolean shared) {
			if (shared) {
				nestedProperty("plugins.tooltip.mode", "index");
				nestedProperty("plugins.tooltip.intersect", false);
				nestedProperty("interaction.mode", "index");
				nestedProperty("interaction.intersect", false);
			} else {
				nestedProperty("plugins.tooltip.mode", "nearest");
				nestedProperty("plugins.tooltip.intersect", true);
				nestedProperty("interaction.mode", "nearest");
				nestedProperty("interaction.intersect", true);
			}
			return this;
		}

		@Override
		public Builder tooltipShared(boolean shared) {
			return sharedTooltip(shared);
		}

		@Override
		public Builder xAxisTitle(String title) {
			ObjectUtils.argumentNotNull(title, "X axis title must be not null");
			nestedProperty("scales.x.title.display", !title.isBlank());
			return nestedProperty("scales.x.title.text", title);
		}

		@Override
		public Builder yAxisTitle(String title) {
			ObjectUtils.argumentNotNull(title, "Y axis title must be not null");
			nestedProperty("scales.y.title.display", !title.isBlank());
			return nestedProperty("scales.y.title.text", title);
		}

		@Override
		public Builder property(String name, Object value) {
			ObjectUtils.argumentNotNull(name, "Chart options property name must be not null");
			values.put(name, value);
			return this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Builder nestedProperty(String path, Object value) {
			ObjectUtils.argumentNotNull(path, "Chart options nested path must be not null");
			if (path.isBlank()) {
				throw new IllegalArgumentException("Chart options nested path must be not empty");
			}
			String[] parts = path.split("\\.");
			if (parts.length == 0) {
				throw new IllegalArgumentException("Chart options nested path must be not empty");
			}
			for (String part : parts) {
				if (part.isBlank()) {
					throw new IllegalArgumentException("Chart options nested path must not contain empty segments");
				}
			}
			Map<String, Object> current = values;
			for (int i = 0; i < parts.length - 1; i++) {
				String part = parts[i];
				Object existing = current.get(part);
				if (!(existing instanceof Map<?, ?> existingMap)) {
					Map<String, Object> newMap = new LinkedHashMap<>();
					current.put(part, newMap);
					current = newMap;
				} else {
					current = (Map<String, Object>) existingMap;
				}
			}
			current.put(parts[parts.length - 1], value);
			return this;
		}

		@Override
		public ChartJsOptions build() {
			return new ChartJsOptions(values);
		}
	}

}





