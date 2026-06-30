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
package com.iyensoft.vaadin.flow.enums;

import com.holonplatform.core.internal.utils.TypeUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Operators available in a {@code DynamicFilterPanel} filter row.
 *
 * <p>
 * Each operator has a display label and a set of applicable Java types.
 * Use {@link #forType(Class)} to retrieve the operators valid for a given
 * property type.
 * </p>
 *
 * @since 10.0.0
 */
public enum FilterOperator {

    // ── Universal ──────────────────────────────────────────────────────────
    EQUALS("Equals"),
    NOT_EQUALS("Not Equals"),

    // ── Multi-value set membership (String, Enum) ─────────────────────────
    IN("Is One Of"),
    NOT_IN("Is Not One Of"),

    // ── String-only ────────────────────────────────────────────────────────
    CONTAINS("Contains"),
    NOT_CONTAINS("Not Contains"),
    STARTS_WITH("Starts With"),
    ENDS_WITH("Ends With"),
    IS_EMPTY("Is Empty"),
    IS_NOT_EMPTY("Is Not Empty"),

    // ── Numeric / date comparison ──────────────────────────────────────────
    GREATER_THAN("Greater Than (>)"),
    LESS_THAN("Less Than (<)"),
    GREATER_OR_EQUALS("Greater or Equals (≥)"),
    LESS_OR_EQUALS("Less or Equals (≤)"),
    BETWEEN("Between"),

    // ── Date-specific aliases (same semantics, friendlier names) ───────────
    BEFORE("Before"),
    AFTER("After"),
    ON_OR_BEFORE("On or Before"),
    ON_OR_AFTER("On or After");

    // -----------------------------------------------------------------------

    private final String label;

    FilterOperator(String label) {
        this.label = label;
    }

    /** Human-readable label shown in the operator ComboBox. */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the operators applicable to the given Java property type.
     *
     * @param type Java type of the bean property (not null)
     * @return ordered list of applicable operators
     */
    public static List<FilterOperator> forType(Class<?> type) {
        if (type == null) {
            return List.of();
        }
        final Class<?> t = wrapPrimitive(type);

        // String — same as TypeUtils.isString(t)
        if (TypeUtils.isString(t)) {
            return List.of(
                    EQUALS, NOT_EQUALS,
                    IN, NOT_IN,
                    CONTAINS, NOT_CONTAINS,
                    STARTS_WITH, ENDS_WITH,
                    IS_EMPTY, IS_NOT_EMPTY);
        }

        // Boolean — equality operators; value input is a True/False ComboBox
        if (TypeUtils.isBoolean(t)) {
            return List.of(EQUALS, NOT_EQUALS);
        }

        // Enum — same as TypeUtils.isEnum(t)
        if (TypeUtils.isEnum(t)) {
            return List.of(EQUALS, NOT_EQUALS, IN, NOT_IN);
        }

        // LocalDate / LocalDateTime — use TypeUtils.isLocalTemporal
        if (LocalDate.class.equals(t) || LocalDateTime.class.equals(t)) {
            return List.of(
                    EQUALS, NOT_EQUALS,
                    BEFORE, AFTER,
                    ON_OR_BEFORE, ON_OR_AFTER,
                    BETWEEN);
        }

        // Numeric — same as TypeUtils.isNumber(t)
        if (TypeUtils.isNumber(t)) {
            return List.of(
                    EQUALS, NOT_EQUALS,
                    GREATER_THAN, LESS_THAN,
                    GREATER_OR_EQUALS, LESS_OR_EQUALS,
                    BETWEEN);
        }

        // Fallback: only equality operators
        return List.of(EQUALS, NOT_EQUALS);
    }

    /**
     * Whether this operator requires a multi-value input (a {@code MultiSelectComboBox}).
     *
     * @return {@code true} for {@link #IN} and {@link #NOT_IN}
     */
    public boolean isMultiValue() {
        return this == IN || this == NOT_IN;
    }

    /**
     * Whether this operator requires two value inputs (the range's lower and upper bounds).
     *
     * @return {@code true} for {@link #BETWEEN} only
     */
    public boolean isBetween() {
        return this == BETWEEN;
    }

    /**
     * Whether this operator requires no value input at all.
     *
     * @return {@code true} for {@link #IS_EMPTY} and {@link #IS_NOT_EMPTY}
     */
    public boolean isNullaryCheck() {
        return this == IS_EMPTY || this == IS_NOT_EMPTY;
    }

    // -----------------------------------------------------------------------
    // Internal helper
    // -----------------------------------------------------------------------

    private static Class<?> wrapPrimitive(Class<?> type) {
        if (int.class.equals(type))    return Integer.class;
        if (long.class.equals(type))   return Long.class;
        if (double.class.equals(type)) return Double.class;
        if (float.class.equals(type))  return Float.class;
        if (short.class.equals(type))  return Short.class;
        if (byte.class.equals(type))   return Byte.class;
        return type;
    }
}


