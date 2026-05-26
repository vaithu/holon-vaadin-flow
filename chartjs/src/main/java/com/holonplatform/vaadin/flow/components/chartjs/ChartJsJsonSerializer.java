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

import java.util.Iterator;
import java.util.Map;

/**
 * Minimal JSON serializer for the Chart.js typed model.
 */
final class ChartJsJsonSerializer {

	private ChartJsJsonSerializer() {
		throw new UnsupportedOperationException("Utility class");
	}

	static String toJson(Object value) {
		StringBuilder builder = new StringBuilder();
		writeValue(builder, value);
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	private static void writeValue(StringBuilder builder, Object value) {
		if (value == null) {
			builder.append("null");
			return;
		}
		if (value instanceof ChartJsJsonValue jsonValue) {
			writeValue(builder, jsonValue.toJsonValue());
			return;
		}
		if (value instanceof String text) {
			writeString(builder, text);
			return;
		}
		if (value instanceof Boolean || value instanceof Integer || value instanceof Long || value instanceof Short
				|| value instanceof Byte) {
			builder.append(value);
			return;
		}
		if (value instanceof Float floatValue) {
			writeFloatingPoint(builder, floatValue);
			return;
		}
		if (value instanceof Double doubleValue) {
			writeFloatingPoint(builder, doubleValue);
			return;
		}
		if (value instanceof Map<?, ?> mapValue) {
			writeMap(builder, (Map<String, Object>) mapValue);
			return;
		}
		if (value instanceof Iterable<?> iterable) {
			writeArray(builder, iterable);
			return;
		}
		if (value instanceof Object[] values) {
			writeArray(builder, java.util.List.of(values));
			return;
		}
		writeString(builder, String.valueOf(value));
	}

	private static void writeFloatingPoint(StringBuilder builder, Number value) {
		double doubleValue = value.doubleValue();
		if (!Double.isFinite(doubleValue)) {
			throw new IllegalArgumentException("Chart.js JSON does not support NaN or Infinity values");
		}
		builder.append(value);
	}

	private static void writeMap(StringBuilder builder, Map<String, Object> value) {
		builder.append('{');
		Iterator<Map.Entry<String, Object>> iterator = value.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			writeString(builder, entry.getKey());
			builder.append(':');
			writeValue(builder, entry.getValue());
			if (iterator.hasNext()) {
				builder.append(',');
			}
		}
		builder.append('}');
	}

	private static void writeArray(StringBuilder builder, Iterable<?> value) {
		builder.append('[');
		Iterator<?> iterator = value.iterator();
		while (iterator.hasNext()) {
			writeValue(builder, iterator.next());
			if (iterator.hasNext()) {
				builder.append(',');
			}
		}
		builder.append(']');
	}

	private static void writeString(StringBuilder builder, String value) {
		builder.append('"');
		for (int i = 0; i < value.length(); i++) {
			char character = value.charAt(i);
			switch (character) {
			case '"':
				builder.append("\\\"");
				break;
			case '\\':
				builder.append("\\\\");
				break;
			case '\b':
				builder.append("\\b");
				break;
			case '\f':
				builder.append("\\f");
				break;
			case '\n':
				builder.append("\\n");
				break;
			case '\r':
				builder.append("\\r");
				break;
			case '\t':
				builder.append("\\t");
				break;
			default:
				if (character < 0x20) {
					builder.append(String.format("\\u%04x", (int) character));
				} else {
					builder.append(character);
				}
			}
		}
		builder.append('"');
	}

}

