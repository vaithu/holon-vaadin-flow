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
package com.holonplatform.vaadin.flow.components;

import com.holonplatform.core.Registration;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.events.FilterChangeListener;
import com.holonplatform.vaadin.flow.internal.components.DefaultFilterInput;
import com.holonplatform.vaadin.flow.internal.components.RangeInputField;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * A UI component that produces an {@code Optional<QueryFilter>} from a
 * user-entered input value.
 *
 * <p>
 * Unlike {@link Input}, which edits a raw domain value (e.g. {@code String},
 * {@code Long}), a {@code FilterInput} translates the current input state into
 * a {@link QueryFilter} that can be applied directly to a Holon
 * {@code Datastore} query.
 * </p>
 * <p>
 * When the input is blank or unset, {@link #getQueryFilter()} returns
 * {@link Optional#empty()}, meaning "no restriction". Only when the user has
 * actually entered a value does the filter become active.
 * </p>
 * <p>
 * {@code FilterInput} instances are typically composed into a
 * {@link FilterInputGroup} or {@link FilterInputForm} to build a search panel
 * that drives a data listing.
 * </p>
 *
 * <h3>Custom filter</h3>
 * <pre>{@code
 * FilterInput<String> custom = FilterInput.from(
 *     Input.string().build(),
 *     value -> (value != null && !value.isBlank())
 *         ? Optional.of(NAME.startsWith(value))
 *         : Optional.empty()
 * );
 * }</pre>
 *
 * @param <T> the raw input value type (e.g. {@code String}, {@code Long},
 *            {@code LocalDate})
 * @since 10.0.0
 * @see FilterInputGroup
 * @see FilterInputForm
 * @see FilterConverter
 */
public interface FilterInput<T> extends HasComponent {

    /**
     * Represents a lower/upper bound range value.
     * <p>
     * Either bound may be {@code null}, which means the range is open-ended on
     * that side. When both bounds are {@code null}, the range is empty and should
     * usually translate to "no filter".
     * </p>
     *
     * @param <T> Bound value type
     * @param from Lower bound (inclusive), may be {@code null}
     * @param to Upper bound (inclusive), may be {@code null}
     * @since 10.0.0
     */
    record Range<T extends Comparable<? super T>>(T from, T to) implements Serializable {

        /**
         * Returns an empty range with no lower or upper bound.
         *
         * @param <T> Bound value type
         * @return an empty range
         */
        public static <T extends Comparable<? super T>> Range<T> empty() {
            return new Range<>(null, null);
        }

        /**
         * Whether the lower bound is present.
         *
         * @return {@code true} if a lower bound is present
         */
        public boolean hasFrom() {
            return from != null;
        }

        /**
         * Whether the upper bound is present.
         *
         * @return {@code true} if an upper bound is present
         */
        public boolean hasTo() {
            return to != null;
        }

        /**
         * Whether no bounds are present.
         *
         * @return {@code true} if both bounds are null
         */
        public boolean isEmpty() {
            return !hasFrom() && !hasTo();
        }

        /**
         * Whether the range ordering is valid.
         * <p>
         * A range is valid when one or both bounds are missing, or when
         * {@code from <= to}.
         * </p>
         *
         * @return {@code true} if the range ordering is valid
         */
        public boolean isValid() {
            return from == null || to == null || from.compareTo(to) <= 0;
        }
    }

    // -----------------------------------------------------------------------
    // Core API
    // -----------------------------------------------------------------------

    /**
     * Returns the {@link QueryFilter} produced from the current input value.
     * <p>
     * Returns {@link Optional#empty()} when the input is blank or unset, which
     * signals "no restriction" to the caller.
     * </p>
     *
     * @return the current query filter, or empty if this filter is inactive
     */
    Optional<QueryFilter> getQueryFilter();

    /**
     * Whether this filter input is currently active, i.e.
     * {@link #getQueryFilter()} returns a non-empty value.
     *
     * @return {@code true} if there is an active filter
     */
    boolean isActive();

    /**
     * Clears the underlying input component, deactivating this filter.
     * <p>
     * After calling this method, {@link #isActive()} returns {@code false} and
     * {@link #getQueryFilter()} returns {@link Optional#empty()}.
     * </p>
     */
    void reset();

    /**
     * Returns the underlying {@link Input} component that backs this filter
     * input. Can be used to configure the visual component (label, placeholder,
     * etc.) after creation.
     *
     * @return the backing input (not null)
     */
    Input<T> getInput();

    /**
     * Registers a listener that is notified whenever this filter's query filter
     * value changes (either becomes active or clears).
     *
     * @param listener the listener to register (not null)
     * @return a {@link Registration} that can be used to remove the listener
     */
    Registration addFilterChangeListener(FilterChangeListener<T> listener);

    // -----------------------------------------------------------------------
    // FilterConverter
    // -----------------------------------------------------------------------

    /**
     * Converts a raw input value of type {@code T} into an
     * {@code Optional<QueryFilter>}.
     * <p>
     * Implementations should return {@link Optional#empty()} whenever the raw
     * value represents "no filter" (e.g. a blank text field, a null selection).
     * </p>
     *
     * @param <T> raw input value type
     */
    @FunctionalInterface
    interface FilterConverter<T> extends Serializable {

        /**
         * Converts the given raw value into a {@link QueryFilter}.
         *
         * @param value the current raw input value (may be {@code null} or
         *              blank for text-based inputs)
         * @return the filter, or empty to signal "no filter"
         */
        Optional<QueryFilter> toQueryFilter(T value);
    }

    // -----------------------------------------------------------------------
    // Custom factory
    // -----------------------------------------------------------------------

    /**
     * Wraps any existing {@link Input}{@code <T>} with a custom
     * {@link FilterConverter}.
     * <p>
     * Use this method for bespoke filter logic not covered by the built-in
     * factory methods (e.g. starts-with, range queries, multi-property AND).
     * </p>
     *
     * @param <T>       raw input value type
     * @param input     the backing input component (not null)
     * @param converter the filter converter (not null)
     * @return a new {@link FilterInput}
     */
    static <T> FilterInput<T> from(Input<T> input, FilterConverter<T> converter) {
        ObjectUtils.argumentNotNull(input, "Input must be not null");
        ObjectUtils.argumentNotNull(converter, "FilterConverter must be not null");
        return new DefaultFilterInput<>(input, converter);
    }

    /**
     * Creates a {@link FilterInput} by inferring the most suitable built-in
     * implementation from the given property value type.
     * <p>
     * Supported mappings:
     * </p>
     * <ul>
     *   <li>{@link String} -&gt; {@link #string(Property)}</li>
     *   <li>{@link Number} (comparable) -&gt; {@link #number(Property, Class)}</li>
     *   <li>{@link Boolean} -&gt; {@link #bool(Property)}</li>
     *   <li>{@link LocalDate} -&gt; {@link #localDate(Property)}</li>
     *   <li>{@link LocalDateTime} -&gt; {@link #localDateTime(Property)}</li>
     *   <li>{@link Enum} -&gt; {@link #enumeration(Property, Class)}</li>
     * </ul>
     *
     * @param <T> property value type
     * @param property the property to infer from (not null)
     * @return a matching built-in {@link FilterInput}
     * @throws IllegalArgumentException when the property type is not supported
     * @since 10.0.0
     */
    @SuppressWarnings("unchecked")
    static <T> FilterInput<T> of(Property<T> property) {
        ObjectUtils.argumentNotNull(property, "Property must be not null");
        final Class<?> propertyType = wrapPrimitive(property.getType());

        if (String.class.equals(propertyType)) {
            return (FilterInput<T>) string((Property<String>) property);
        }
        if (Boolean.class.equals(propertyType)) {
            return (FilterInput<T>) bool((Property<Boolean>) property);
        }
        if (LocalDate.class.equals(propertyType)) {
            return (FilterInput<T>) localDate((Property<LocalDate>) property);
        }
        if (LocalDateTime.class.equals(propertyType)) {
            return (FilterInput<T>) localDateTime((Property<LocalDateTime>) property);
        }
        if (propertyType.isEnum()) {
            return (FilterInput<T>) enumFilter(property, propertyType);
        }
        if (Number.class.isAssignableFrom(propertyType) && Comparable.class.isAssignableFrom(propertyType)) {
            return (FilterInput<T>) numberFilter(property, propertyType);
        }

        throw new IllegalArgumentException("Unsupported property type [" + propertyType.getName()
                + "] for automatic FilterInput inference on property [" + property + "]");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static FilterInput<?> enumFilter(Property<?> property, Class<?> propertyType) {
        return enumeration((Property) property, (Class) propertyType);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static FilterInput<?> numberFilter(Property<?> property, Class<?> propertyType) {
        return number((Property) property, (Class) propertyType);
    }

    private static Class<?> wrapPrimitive(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        if (boolean.class.equals(type)) {
            return Boolean.class;
        }
        if (int.class.equals(type)) {
            return Integer.class;
        }
        if (long.class.equals(type)) {
            return Long.class;
        }
        if (double.class.equals(type)) {
            return Double.class;
        }
        if (float.class.equals(type)) {
            return Float.class;
        }
        if (short.class.equals(type)) {
            return Short.class;
        }
        if (byte.class.equals(type)) {
            return Byte.class;
        }
        if (char.class.equals(type)) {
            return Character.class;
        }
        return type;
    }

    // -----------------------------------------------------------------------
    // Built-in property-bound factories
    // -----------------------------------------------------------------------

    /**
     * Creates a {@code String}-typed filter that restricts rows to those where
     * the property value <em>contains</em> the typed text (case-insensitive).
     * <p>
     * The filter is inactive when the text field is empty or blank.
     * </p>
     *
     * @param property the String property to filter on (not null)
     * @return a new {@link FilterInput} backed by a text field
     */
    static FilterInput<String> string(Property<String> property) {
        return string(property, true);
    }

    /**
     * Creates a {@code String}-typed filter that restricts rows to those where
     * the property value <em>contains</em> the typed text, with configurable
     * case sensitivity.
     * <p>
     * The filter is inactive when the text field is empty or blank.
     * </p>
     *
     * @param property   the String property to filter on (not null)
     * @param ignoreCase {@code true} for a case-insensitive contains check
     * @return a new {@link FilterInput} backed by a text field
     */
    static FilterInput<String> string(Property<String> property, boolean ignoreCase) {
        ObjectUtils.argumentNotNull(property, "Property must be not null");
        Input<String> input = Input.string().build();
        FilterConverter<String> converter = value ->
                (value != null && !value.isBlank())
                        ? Optional.of(QueryFilter.contains(property, value, ignoreCase))
                        : Optional.empty();
        return from(input, converter);
    }

    /**
     * Creates a numeric filter that restricts rows to those where the property
     * value equals the entered number.
     * <p>
     * The filter is inactive when the number field is empty ({@code null}).
     * </p>
     *
     * @param <N>        Number type
     * @param property   the numeric property to filter on (not null)
     * @param numberType the number class (not null)
     * @return a new {@link FilterInput} backed by a number input
     */
    static <N extends Number & Comparable<N>> FilterInput<N> number(
            Property<N> property, Class<N> numberType) {
        ObjectUtils.argumentNotNull(property, "Property must be not null");
        ObjectUtils.argumentNotNull(numberType, "Number type must be not null");
        Input<N> input = Input.number(numberType).build();
        FilterConverter<N> converter = value ->
                value != null ? Optional.of(QueryFilter.eq(property, value)) : Optional.empty();
        return from(input, converter);
    }

    /**
     * Creates a numeric range filter.
     * <ul>
     *   <li>no bounds → no filter</li>
     *   <li>only {@code from} → {@code property >= from}</li>
     *   <li>only {@code to} → {@code property <= to}</li>
     *   <li>both bounds → {@code property >= from AND property <= to}</li>
     * </ul>
     * <p>
     * If both bounds are present but out of order ({@code from > to}), the range
     * is treated as inactive and no filter is produced.
     * </p>
     *
     * @param <N> Number type
     * @param property Numeric property to filter on (not null)
     * @param numberType Number class (not null)
     * @return a new {@link FilterInput} backed by a composite range input
     * @since 10.0.0
     */
    static <N extends Number & Comparable<N>> FilterInput<Range<N>> numberRange(
            Property<N> property, Class<N> numberType) {
        ObjectUtils.argumentNotNull(property, "Property must be not null");
        ObjectUtils.argumentNotNull(numberType, "Number type must be not null");
        final RangeInputField<N> field = new RangeInputField<>(Input.number(numberType).build(), Input.number(numberType).build());
        return from(Input.from(field), range -> toRangeFilter(property, range));
    }

    /**
     * Creates a {@link LocalDate} filter that restricts rows to those where the
     * property value equals the selected date.
     * <p>
     * The filter is inactive when no date is selected ({@code null}).
     * </p>
     *
     * @param property the LocalDate property to filter on (not null)
     * @return a new {@link FilterInput} backed by a date picker
     */
    static FilterInput<LocalDate> localDate(Property<LocalDate> property) {
        ObjectUtils.argumentNotNull(property, "Property must be not null");
        Input<LocalDate> input = Input.localDate().build();
        FilterConverter<LocalDate> converter = value ->
                value != null ? Optional.of(QueryFilter.eq(property, value)) : Optional.empty();
        return from(input, converter);
    }

    /**
     * Creates a {@link LocalDate} range filter.
     *
     * @param property LocalDate property to filter on (not null)
     * @return a new {@link FilterInput} backed by a composite date range input
     * @since 10.0.0
     */
    static FilterInput<Range<LocalDate>> localDateRange(Property<LocalDate> property) {
        ObjectUtils.argumentNotNull(property, "Property must be not null");
        final RangeInputField<LocalDate> field = new RangeInputField<>(Input.localDate().build(), Input.localDate().build());
        return from(Input.from(field), range -> toRangeFilter(property, range));
    }

    /**
     * Creates a {@link LocalDateTime} filter that restricts rows to those where
     * the property value equals the selected date-time.
     * <p>
     * The filter is inactive when no date-time is selected ({@code null}).
     * </p>
     *
     * @param property the LocalDateTime property to filter on (not null)
     * @return a new {@link FilterInput} backed by a date-time picker
     */
    static FilterInput<LocalDateTime> localDateTime(Property<LocalDateTime> property) {
        ObjectUtils.argumentNotNull(property, "Property must be not null");
        Input<LocalDateTime> input = Input.localDateTime().build();
        FilterConverter<LocalDateTime> converter = value ->
                value != null ? Optional.of(QueryFilter.eq(property, value)) : Optional.empty();
        return from(input, converter);
    }

    /**
     * Creates a {@link LocalDateTime} range filter.
     *
     * @param property LocalDateTime property to filter on (not null)
     * @return a new {@link FilterInput} backed by a composite date-time range input
     * @since 10.0.0
     */
    static FilterInput<Range<LocalDateTime>> localDateTimeRange(Property<LocalDateTime> property) {
        ObjectUtils.argumentNotNull(property, "Property must be not null");
        final RangeInputField<LocalDateTime> field = new RangeInputField<>(Input.localDateTime().build(), Input.localDateTime().build());
        return from(Input.from(field), range -> toRangeFilter(property, range));
    }

    /**
     * Creates a tri-state {@link Boolean} filter.
     * <ul>
     *   <li>{@code null} (no selection) — no filter applied</li>
     *   <li>{@code true} — equality filter: property = true</li>
     *   <li>{@code false} — equality filter: property = false</li>
     * </ul>
     *
     * @param property the Boolean property to filter on (not null)
     * @return a new {@link FilterInput} backed by a nullable Boolean select
     */
    static FilterInput<Boolean> bool(Property<Boolean> property) {
        ObjectUtils.argumentNotNull(property, "Property must be not null");
        Input<Boolean> input = Input.singleSelect(Boolean.class)
                .items(Boolean.TRUE, Boolean.FALSE)
                .build();
        FilterConverter<Boolean> converter = value ->
                value != null ? Optional.of(QueryFilter.eq(property, value)) : Optional.empty();
        return from(input, converter);
    }

    /**
     * Creates an enum-typed filter that restricts rows to those where the
     * property equals the selected enum constant.
     * <p>
     * A {@code null} selection (no item chosen) means the filter is inactive.
     * </p>
     *
     * @param <E>      Enum type
     * @param property the enum property to filter on (not null)
     * @param enumType the enum class (not null); its constants are used as
     *                 selectable options
     * @return a new {@link FilterInput} backed by an enum select
     */
    static <E extends Enum<E>> FilterInput<E> enumeration(
            Property<E> property, Class<E> enumType) {
        ObjectUtils.argumentNotNull(property, "Property must be not null");
        ObjectUtils.argumentNotNull(enumType, "Enum type must be not null");
        Input<E> input = Input.singleSelect(enumType)
                .items(enumType.getEnumConstants())
                .build();
        FilterConverter<E> converter = value ->
                value != null ? Optional.of(QueryFilter.eq(property, value)) : Optional.empty();
        return from(input, converter);
    }

    /**
     * Converts a {@link Range} into a bounded {@link QueryFilter}.
     *
     * @param <T> Comparable bound type
     * @param property Property to filter on (not null)
     * @param range Range value, may be {@code null}
     * @return the corresponding {@link QueryFilter}, or empty if the range is not active
     */
    private static <T extends Comparable<? super T>> Optional<QueryFilter> toRangeFilter(Property<T> property, Range<T> range) {
        if (range == null || range.isEmpty() || !range.isValid()) {
            return Optional.empty();
        }
        if (range.hasFrom() && range.hasTo()) {
            return Optional.of(QueryFilter.goe(property, range.from())
                    .and(QueryFilter.loe(property, range.to())));
        }
        if (range.hasFrom()) {
            return Optional.of(QueryFilter.goe(property, range.from()));
        }
        return Optional.of(QueryFilter.loe(property, range.to()));
    }
}

