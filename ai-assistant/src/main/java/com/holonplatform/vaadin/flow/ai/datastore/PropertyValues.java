package com.holonplatform.vaadin.flow.ai.datastore;

import tools.jackson.databind.JsonNode;
import com.holonplatform.core.property.Property;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Best-effort conversion of a Jackson {@link JsonNode} value (as produced by the LLM tool-call
 * arguments) into the Java type declared by a Holon {@link Property}.
 *
 * <p>Used by the Datastore-backed AI controllers ({@code BeanListingAIController},
 * {@code BeanPropertyInputFormAIController}) to turn LLM-supplied, JSON-typed values into the
 * strongly-typed values the Holon property/bean model expects, without ever handing the LLM
 * direct write access to the underlying persistence layer.
 */
public final class PropertyValues {

    private PropertyValues() {
    }

    /**
     * Converts the given JSON node to the type declared by {@code property}.
     *
     * @param property the target property (not null)
     * @param node     the JSON value supplied by the LLM (not null, not a JSON null)
     * @return the converted value
     * @throws IllegalArgumentException if the node cannot be converted to the property type
     */
    public static Object convert(Property<?> property, JsonNode node) {
        Class<?> type = property.getType();
        try {
            if (type == String.class) {
                return node.asText();
            }
            if (type == Integer.class || type == int.class) {
                return node.asInt();
            }
            if (type == Long.class || type == long.class) {
                return node.asLong();
            }
            if (type == Double.class || type == double.class) {
                return node.asDouble();
            }
            if (type == Boolean.class || type == boolean.class) {
                return node.asBoolean();
            }
            if (type == BigDecimal.class) {
                return new BigDecimal(node.asText());
            }
            if (type == LocalDate.class) {
                return LocalDate.parse(node.asText());
            }
            if (type == LocalDateTime.class) {
                return LocalDateTime.parse(node.asText());
            }
            if (Enum.class.isAssignableFrom(type)) {
                @SuppressWarnings({ "unchecked", "rawtypes" })
                Object value = Enum.valueOf((Class<Enum>) type, node.asText());
                return value;
            }
            // Fallback: plain text representation.
            return node.asText();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(
                    "Could not convert value [" + node + "] to type [" + type.getName()
                            + "] for property [" + property + "]", e);
        }
    }

}
