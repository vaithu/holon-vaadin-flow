package com.holonplatform.vaadin.flow.core.components.types;

import java.io.Serializable;

/**
 * Sealed component type hierarchy for type-safe filter and renderer handling.
 *
 * <p>
 * Java 17+ sealed classes enable exhaustive pattern matching on component types
 * at compile time. This prevents the common error of forgetting to handle a newly
 * added component type in switch statements or if-chains.
 * </p>
 *
 * <p>
 * Usage example:
 * </p>
 * <pre>{@code
 * ComponentType type = ComponentType.Text.INSTANCE;
 * QueryFilter filter = switch (type) {
 *     case ComponentType.Text _ -> NAME.contains(value);
 *     case ComponentType.Number _ -> AGE.eq((Long) value);
 *     case ComponentType.Date _ -> CREATED_DATE.eq((LocalDate) value);
 *     case ComponentType.Boolean _ -> ACTIVE.eq((Boolean) value);
 * };
 * }</pre>
 *
 * @since 10.0.0
 */
public sealed interface ComponentType permits
    ComponentType.Text,
    ComponentType.Number,
    ComponentType.Date,
    ComponentType.Boolean,
    ComponentType.Enumeration,
    ComponentType.Custom {

    /**
     * Get the human-readable name of this component type.
     *
     * @return the type name
     */
    String getTypeName();

    // -----------------------------------------------------------------------
    // Sealed implementations
    // -----------------------------------------------------------------------

    /**
     * Text input component type.
     *
     * <p>String properties, typically rendered as TextField or TextArea.</p>
     */
    final class Text implements ComponentType, Serializable {
        public static final Text INSTANCE = new Text();

        private Text() {}

        @Override
        public String getTypeName() {
            return "text";
        }

        @Override
        public String toString() {
            return "ComponentType.Text{}";
        }
    }

    /**
     * Numeric input component type.
     *
     * <p>Number properties (Integer, Long, Double, BigDecimal), typically
     * rendered as NumberField or Slider.</p>
     */
    final class Number implements ComponentType, Serializable {
        public static final Number INSTANCE = new Number();

        private Number() {}

        @Override
        public String getTypeName() {
            return "number";
        }

        @Override
        public String toString() {
            return "ComponentType.Number{}";
        }
    }

    /**
     * Date/time input component type.
     *
     * <p>Temporal properties (LocalDate, LocalDateTime), typically rendered
     * as DatePicker or DateTimePicker.</p>
     */
    final class Date implements ComponentType, Serializable {
        public static final Date INSTANCE = new Date();

        private Date() {}

        @Override
        public String getTypeName() {
            return "date";
        }

        @Override
        public String toString() {
            return "ComponentType.Date{}";
        }
    }

    /**
     * Boolean input component type.
     *
     * <p>Boolean properties, typically rendered as Checkbox or tri-state Select.</p>
     */
    final class Boolean implements ComponentType, Serializable {
        public static final Boolean INSTANCE = new Boolean();

        private Boolean() {}

        @Override
        public String getTypeName() {
            return "boolean";
        }

        @Override
        public String toString() {
            return "ComponentType.Boolean{}";
        }
    }

    /**
     * Enumeration select component type.
     *
     * <p>Enum properties, rendered as ComboBox or Select with enum constants.</p>
     */
    final class Enumeration implements ComponentType, Serializable {
        public static final Enumeration INSTANCE = new Enumeration();

        private Enumeration() {}

        @Override
        public String getTypeName() {
            return "enumeration";
        }

        @Override
        public String toString() {
            return "ComponentType.Enumeration{}";
        }
    }

    /**
     * Custom component type for user-defined filters/renderers.
     *
     * <p>When the built-in types don't fit your use case, use Custom with
     * a type identifier string.</p>
     */
    final class Custom implements ComponentType, Serializable {
        private final String customTypeName;

        public Custom(String customTypeName) {
            this.customTypeName = customTypeName;
        }

        @Override
        public String getTypeName() {
            return customTypeName;
        }

        @Override
        public String toString() {
            return "ComponentType.Custom{" + "name='" + customTypeName + '\'' + '}';
        }
    }
}

