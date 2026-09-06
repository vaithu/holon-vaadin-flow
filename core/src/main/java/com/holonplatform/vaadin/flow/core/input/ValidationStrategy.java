package com.holonplatform.vaadin.flow.core.input;

/**
 * Sealed interface for input validation strategies.
 *
 * Enables type-safe validation configuration with exhaustive pattern matching.
 * Use for building form validation rules without runtime surprises.
 *
 * Example:
 * <pre>{@code
 * ValidationRule rule = switch (strategy) {
 *     case Required _ -> validateNotNull(value);
 *     case Email _ -> validateEmail(value);
 *     case Range range -> validateRange(value, range.min(), range.max());
 *     case Pattern pattern -> validatePattern(value, pattern.regex());
 *     case Custom custom -> custom.validate(value);
 * };
 * }</pre>
 *
 * @since 10.0.0
 */
public sealed interface ValidationStrategy permits
    ValidationStrategy.Required,
    ValidationStrategy.Email,
    ValidationStrategy.Range,
    ValidationStrategy.Pattern,
    ValidationStrategy.Custom {

    /**
     * Validate the input value.
     *
     * @param value the value to validate (may be null)
     * @return true if valid, false otherwise
     */
    boolean validate(Object value);

    // =========================================================================
    // Required (Non-null validation)
    // =========================================================================

    /**
     * Required/non-null validation.
     */
    final class Required implements ValidationStrategy {
        public static final Required INSTANCE = new Required();

        private Required() {}

        @Override
        public boolean validate(Object value) {
            return value != null;
        }

        @Override
        public String toString() { return "Required"; }
    }

    // =========================================================================
    // Email validation
    // =========================================================================

    /**
     * Email format validation.
     */
    final class Email implements ValidationStrategy {
        public static final Email INSTANCE = new Email();

        private Email() {}

        @Override
        public boolean validate(Object value) {
            if (value == null) return true; // Optional field
            String str = value.toString();
            return str.contains("@") && str.contains(".");
        }

        @Override
        public String toString() { return "Email"; }
    }

    // =========================================================================
    // Numeric range validation
    // =========================================================================

    /**
     * Numeric range validation.
     */
    final class Range implements ValidationStrategy {
        private final Number min;
        private final Number max;

        public Range(Number min, Number max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public boolean validate(Object value) {
            if (value == null) return true;
            if (!(value instanceof Number n)) return false;

            double d = n.doubleValue();
            return d >= min.doubleValue() && d <= max.doubleValue();
        }

        public Number min() { return min; }
        public Number max() { return max; }

        @Override
        public String toString() {
            return String.format("Range[%s..%s]", min, max);
        }
    }

    // =========================================================================
    // Regex pattern validation
    // =========================================================================

    /**
     * Regular expression pattern validation.
     */
    final class Pattern implements ValidationStrategy {
        private final String regex;
        private final java.util.regex.Pattern compiled;

        public Pattern(String regex) {
            this.regex = regex;
            this.compiled = java.util.regex.Pattern.compile(regex);
        }

        @Override
        public boolean validate(Object value) {
            if (value == null) return true;
            return compiled.matcher(value.toString()).matches();
        }

        public String regex() { return regex; }

        @Override
        public String toString() {
            return String.format("Pattern[%s]", regex);
        }
    }

    // =========================================================================
    // Custom validator (pluggable)
    // =========================================================================

    /**
     * Custom validation logic.
     */
    final class Custom implements ValidationStrategy {
        private final java.util.function.Predicate<Object> predicate;
        private final String name;

        public Custom(String name, java.util.function.Predicate<Object> predicate) {
            this.name = name;
            this.predicate = predicate;
        }

        @Override
        public boolean validate(Object value) {
            return predicate.test(value);
        }

        public String name() { return name; }

        @Override
        public String toString() {
            return String.format("Custom[%s]", name);
        }
    }
}

