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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Typed representation of a Chart.js dataset object.
 *
 * @since 10.0.0
 */
public final class ChartJsDataset implements ChartJsJsonValue {

	private final Map<String, Object> values;

	private ChartJsDataset(Map<String, Object> values) {
		this.values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
	}

	/**
	 * Get a builder to create a {@link ChartJsDataset}.
	 * @return a new {@link Builder}
	 */
	public static Builder create() {
		return new DefaultBuilder();
	}

	/**
	 * Get a builder to create a {@link ChartJsDataset}.
	 * @return a new {@link Builder}
	 */
	public static Builder builder() {
		return create();
	}

	/**
	 * Get a builder to create a {@link ChartJsDataset}.
	 * @return a new {@link Builder}
	 */
	public interface Builder {

		Builder label(String label);

		/**
		 * Vaadin Charts-like alias for {@link #label(String)}.
		 * @param name series name
		 * @return this
		 */
		Builder name(String name);

		Builder data(Number... values);

		/**
		 * Vaadin Charts-like alias for {@link #data(Number...)}.
		 * @param values data values
		 * @return this
		 */
		Builder values(Number... values);

		Builder backgroundColor(String... colors);

		Builder borderColor(String... colors);

		/**
		 * Vaadin Charts-like single series color.
		 * @param color series color
		 * @return this
		 */
		Builder color(String color);

		Builder borderWidth(Number width);

		Builder tension(Number tension);

		Builder fill(boolean fill);

		Builder property(String name, Object value);

		ChartJsDataset build();
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

		@Override
		public Builder label(String label) {
			return property("label", label);
		}

		@Override
		public Builder name(String name) {
			return label(name);
		}

		@Override
		public Builder data(Number... values) {
			ObjectUtils.argumentNotNull(values, "Dataset values must be not null");
			return property("data", List.of(values));
		}

		@Override
		public Builder values(Number... values) {
			return data(values);
		}

		@Override
		public Builder backgroundColor(String... colors) {
			ObjectUtils.argumentNotNull(colors, "Dataset colors must be not null");
			return property("backgroundColor", List.of(colors));
		}

		@Override
		public Builder borderColor(String... colors) {
			ObjectUtils.argumentNotNull(colors, "Dataset colors must be not null");
			return property("borderColor", List.of(colors));
		}

		@Override
		public Builder color(String color) {
			ObjectUtils.argumentNotNull(color, "Dataset color must be not null");
			property("backgroundColor", color);
			return property("borderColor", color);
		}

		@Override
		public Builder borderWidth(Number width) {
			return property("borderWidth", width);
		}

		@Override
		public Builder tension(Number tension) {
			return property("tension", tension);
		}

		@Override
		public Builder fill(boolean fill) {
			return property("fill", fill);
		}

		@Override
		public Builder property(String name, Object value) {
			ObjectUtils.argumentNotNull(name, "Dataset property name must be not null");
			values.put(name, normalize(value));
			return this;
		}

		@Override
		public ChartJsDataset build() {
			return new ChartJsDataset(values);
		}

		private Object normalize(Object value) {
			if (value instanceof Number[] numbers) {
				return Arrays.asList(numbers);
			}
			if (value instanceof String[] strings) {
				return Arrays.asList(strings);
			}
			return value;
		}
	}

}



