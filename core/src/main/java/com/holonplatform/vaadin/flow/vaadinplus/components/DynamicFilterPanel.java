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
package com.holonplatform.vaadin.flow.vaadinplus.components;

import java.io.Serial;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.holonplatform.core.Registration;
import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.internal.utils.BeanUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.FilterInput;
import com.holonplatform.vaadin.flow.components.FilterInputGroup;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.events.FilterChangeListener;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.components.events.DefaultFilterChangeEvent;
import com.iyensoft.vaadin.flow.enums.FilterOperator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasPlaceholder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.function.SerializableSupplier;

/**
 * A dynamic, row-based filter builder component that implements
 * {@link FilterInputGroup}.
 *
 * <p>
 * The panel introspects a Java bean class at construction time and lets the
 * user content any
 * number of filter conditions at runtime. Each row presents:
 * </p>
 * <ul>
 * <li>a <em>property selector</em> ComboBox (all bean properties)</li>
 * <li>an <em>operator selector</em> ComboBox (type-aware: String, Number, Date,
 * Boolean, Enum)</li>
 * <li>a <em>value input</em> that adapts to the selected property type
 * (TextField, NumberField, DatePicker, etc.); BETWEEN shows two inputs</li>
 * <li>a remove (×) button</li>
 * </ul>
 *
 * <p>
 * All rows are combined with AND (default) or OR depending on
 * {@link #setMatchAll(boolean)}.
 * The filter is only committed when the user clicks <em>Apply filter</em>;
 * until then
 * {@link #getQueryFilter()} returns the last applied state.
 * </p>
 *
 * <h3>Usage with Datastore (QueryFilter path)</h3>
 * 
 * <pre>{@code
 * DynamicFilterPanel<Product> panel = DynamicFilterPanel.of(Product.class);
 * content(panel);
 *
 * listing.setItems(panel, (query, filter) -> {
 *     var q = datastore.query(TARGET).restrict(query.getLimit(), query.getOffset());
 *     if (filter != null)
 *         q.filter(filter);
 *     return q.stream(BeanProjection.of(Product.class));
 * });
 * listing.refreshOnFilterChange(panel);
 * }</pre>
 *
 * <h3>Usage with in-memory data</h3>
 * 
 * <pre>{@code
 * panel.addFilterChangeListener(e -> {
 *     shown.clear();
 *     shown.addAll(allProducts.stream().filter(panel.toPredicate()).toList());
 *     listing.getDataProvider().refreshAll();
 * });
 * }</pre>
 *
 * @param <T> bean type
 * @since 10.0.0
 */
@StyleSheet("context://filter-panel.css")
public class DynamicFilterPanel<T> extends Div implements FilterInputGroup {

    @Serial
    private static final long serialVersionUID = 1L;

    // ── Nested types ──────────────────────────────────────────────────────

    /**
     * Resolved input-width tier driven by {@code @Column(length)} or
     * {@code @Size(max)}.
     * <ul>
     * <li>{@code UNCONSTRAINED} — unknown / &gt;100 chars; input stretches freely
     * (flex: 1 1 auto)</li>
     * <li>{@code XS} — 1–10 chars (e.g. country code, flag); max-width: ~7 rem</li>
     * <li>{@code SM} — 11–30 chars (e.g. short code, abbreviation); max-width: ~14
     * rem</li>
     * <li>{@code MD} — 31–100 chars (e.g. name, email); max-width: ~22 rem</li>
     * </ul>
     */
    public enum FieldWidthTier {
        UNCONSTRAINED(null),
        XS("filter-panel__value-input--xs"),
        SM("filter-panel__value-input--sm"),
        MD("filter-panel__value-input--md");

        private final String modifierClass;

        FieldWidthTier(String modifierClass) {
            this.modifierClass = modifierClass;
        }

        /**
         * @return the CSS modifier class to add alongside
         *         {@code filter-panel__value-input}, or {@code null}
         */
        public String getModifierClass() {
            return modifierClass;
        }

        /** Maps a raw column length (0 = unknown) to the appropriate tier. */
        public static FieldWidthTier of(int columnLength) {
            if (columnLength <= 0 || columnLength > 100)
                return UNCONSTRAINED;
            if (columnLength <= 10)
                return XS;
            if (columnLength <= 30)
                return SM;
            return MD;
        }
    }

    /**
     * Lightweight descriptor for a single property discovered either via bean
     * introspection or from a Holon {@link Property} object.
     *
     * <p>
     * When constructed from a {@link Property}, {@code rawProperty} holds the
     * original object so it can be used directly as a
     * {@link com.holonplatform.core.query.QueryFilter}
     * operand and for {@link PropertyBox} value extraction.
     * </p>
     *
     * <p>
     * {@code columnLength} is resolved from {@code @Column(length)} or
     * {@code @Size(max)}
     * annotations and drives the {@link FieldWidthTier} for input sizing.
     * </p>
     */
    public record PropInfo(String name, String label, Class<?> type,
            Property<?> rawProperty, int columnLength)
            implements java.io.Serializable {
        /**
         * Convenience constructor for callers that don't supply a column length
         * (defaults to 0 = unknown).
         */
        public PropInfo(String name, String label, Class<?> type, Property<?> rawProperty) {
            this(name, label, type, rawProperty, 0);
        }
    }

    /**
     * Snapshot of a single filter row captured at "Apply filter" time.
     * Used to build the in-memory Java {@link java.util.function.Predicate} via
     * {@link #toPredicate()}.
     */
    private record AppliedRow(String propName, Class<?> propType, FilterOperator op,
            Object val, Object val2,
            Property<?> rawProperty,
            RowConnector connector)
            implements java.io.Serializable {
    }

    // ── Row connector ─────────────────────────────────────────────────────

    /**
     * Binary logical connector used to join consecutive filter rows in advanced
     * mode.
     *
     * <table border="1">
     * <tr>
     * <th>Connector</th>
     * <th>Expression</th>
     * <th>Meaning</th>
     * </tr>
     * <tr>
     * <td>AND</td>
     * <td>A AND B</td>
     * <td>Both conditions must match</td>
     * </tr>
     * <tr>
     * <td>OR</td>
     * <td>A OR B</td>
     * <td>At least one must match</td>
     * </tr>
     * <tr>
     * <td>AND_NOT</td>
     * <td>A AND NOT B</td>
     * <td>Matches A but excludes B</td>
     * </tr>
     * <tr>
     * <td>OR_NOT</td>
     * <td>A OR NOT B</td>
     * <td>Matches A or anything not in B</td>
     * </tr>
     * <tr>
     * <td>NAND</td>
     * <td>NOT(A AND B)</td>
     * <td>Not both conditions at once</td>
     * </tr>
     * <tr>
     * <td>NOR</td>
     * <td>NOT(A OR B)</td>
     * <td>Neither condition matches</td>
     * </tr>
     * <tr>
     * <td>XOR</td>
     * <td>A XOR B</td>
     * <td>Exactly one condition matches</td>
     * </tr>
     * </table>
     *
     * @see DynamicFilterPanel#setAdvancedMode(boolean)
     */
    public enum RowConnector {
        AND("And"),
        OR("Or"),
        AND_NOT("And Not"),
        OR_NOT("Or Not"),
        NAND("Nand"),
        NOR("Nor"),
        XOR("Xor");

        private final String label;

        RowConnector(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    // ── State ─────────────────────────────────────────────────────────────

    private final List<PropInfo> availableProps;
    private final List<FilterRow> rows = new ArrayList<>();
    private final Div rowsContainer;

    /**
     * "+ Add filter" button — kept as a field so {@link #updateAddButton()} can
     * enable/disable it.
     */
    private final Button addBtn;

    /** "Clear all" button — kept as a field so locale changes can update its text. */
    private final Button clearBtn;

    /** "Apply filter" button — kept as a field so locale changes can update its text. */
    private final Button applyBtn;

    /**
     * Panel-level validation error message — shown when Apply is clicked with no
     * complete criteria.
     */
    private final Div validationError;

    /** Text span inside the validation-error bar — updated on locale change. */
    private final Span validationErrorText;

    /**
     * {@code true} → AND-combine rows; {@code false} → OR-combine rows (simple
     * mode).
     */
    private boolean matchAll = true;

    /**
     * When {@code true} each row carries its own AND/OR connector and an optional
     * NOT toggle.
     * The global {@link #matchAll} flag is ignored while advanced mode is active.
     */
    private boolean advancedMode = false;

    /**
     * When {@code true} (the <strong>default</strong>), each value input gets a CSS
     * width-tier
     * modifier class derived from {@code @Column(length)}, {@code @Size(max)}, or
     * {@code @Min}/{@code @Max}.
     * Set to {@code false} for uniform-width value inputs.
     */
    private boolean columnLengthAwareWidth = true;

    private QueryFilter appliedFilter = null;
    private List<AppliedRow> appliedRows = List.of();
    private final List<FilterChangeListener<?>> listeners = new ArrayList<>();

    /**
     * Callbacks fired only when the built-in <em>Apply filter</em> button is
     * clicked —
     * NOT when a row is removed via the × button. Use this to close an enclosing
     * {@link com.vaadin.flow.component.dialog.Dialog} on explicit Apply.
     */
    private final List<SerializableRunnable> applyListeners = new ArrayList<>();

    /**
     * Per-property item providers for {@link FilterOperator#IN} /
     * {@link FilterOperator#NOT_IN} rows.
     * Key = property name. Value = {@link DataProvider} typed as
     * {@code <Object, String>}.
     * Populated by {@link #setItems} and {@link #setLazyItems}.
     */
    @SuppressWarnings("rawtypes")
    private final Map<String, DataProvider> multiSelectDataProviders = new HashMap<>();

    // ── Constructor ───────────────────────────────────────────────────────

    private DynamicFilterPanel(List<PropInfo> props) {
        this.availableProps = props;
        addClassName("filter-panel");

        rowsContainer = Components.div().styleName("filter-panel__rows").build();

        addBtn = Components.button().text(LocalizationProvider.localize("+ Add filter", "filter.add_filter")).styleName("filter-panel__add")
                .withClickListener(e -> addRow()).build();

        clearBtn = Components.button().text(LocalizationProvider.localize("Clear all", "filter.clear_all")).styleName("filter-panel__clear")
                .withClickListener(e -> resetAll()).build();

        applyBtn = Components.button().text(LocalizationProvider.localize("Apply filter", "filter.apply_filter")).styleName("filter-panel__apply").primary()
                .withClickListener(e -> applyFilterFromButton()).build();

        Div actions = Components.div().add(addBtn, clearBtn, applyBtn).styleName("filter-panel__actions").build();

        // Validation error bar — hidden until Apply is clicked with no complete
        // criteria.
        var errIcon = new Icon(VaadinIcon.EXCLAMATION_CIRCLE_O);
        errIcon.addClassName("filter-panel__validation-error-icon");
        validationErrorText = Components.span().text(LocalizationProvider.localize(
                "Please fill in at least one filter condition before applying.", "filter.validation_error")).build();
        validationError = Components.div().add(errIcon, validationErrorText).styleName("filter-panel__validation-error").build();

        add(rowsContainer, validationError, actions);

        // Show the first row immediately so the user can start filtering without
        // having to click "+ Add filter" first.
        addRow();
    }

    // ── Factory ────���──────────────────────────────────────────────────────

    /**
     * Creates a new {@code DynamicFilterPanel} by introspecting the given bean
     * class.
     * All readable+writable bean properties are offered as filter fields.
     *
     * <p>
     * Use this factory when working with
     * {@link com.holonplatform.vaadin.flow.components.BeanListing}.
     * </p>
     *
     * @param <T>      bean type
     * @param beanType bean class (not null)
     * @return a fully configured panel ready to be embedded in a view
     */
    public static <T> DynamicFilterPanel<T> of(Class<T> beanType) {
        Objects.requireNonNull(beanType, "beanType must not be null");
        return new DynamicFilterPanel<>(introspect(beanType));
    }

    /**
     * Creates a new {@code DynamicFilterPanel} from an explicit set of Holon
     * {@link Property} objects.
     *
     * <p>
     * Use this factory when working with
     * {@link com.holonplatform.vaadin.flow.components.PropertyListing},
     * where the property set is already known. The actual {@link Property} objects
     * are
     * retained so they are used directly as query filter operands and for
     * {@link PropertyBox} value extraction in {@link #toPredicate()}.
     * </p>
     *
     * @param properties the properties to offer as filter fields (not null, not
     *                   empty)
     * @return a fully configured panel typed as
     *         {@code DynamicFilterPanel<PropertyBox>}
     * @since 10.0.0
     */
    public static DynamicFilterPanel<PropertyBox> ofProperties(Property<?>... properties) {
        Objects.requireNonNull(properties, "properties must not be null");
        return new DynamicFilterPanel<>(fromProperties(Arrays.asList(properties)));
    }

    /**
     * Creates a new {@code DynamicFilterPanel} from a Holon {@link PropertySet}.
     *
     * <p>
     * Use this factory when working with
     * {@link com.holonplatform.vaadin.flow.components.PropertyListing}.
     * The actual {@link Property} objects are retained as filter operands.
     * </p>
     *
     * @param propertySet the property set to offer as filter fields (not null)
     * @return a fully configured panel typed as
     *         {@code DynamicFilterPanel<PropertyBox>}
     * @since 10.0.0
     */
    @SuppressWarnings("unused") // public API — called by external consumers (PropertyListing integration)
    public static DynamicFilterPanel<PropertyBox> ofPropertySet(PropertySet<?> propertySet) {
        Objects.requireNonNull(propertySet, "propertySet must not be null");
        @SuppressWarnings("unchecked")
        Iterable<Property<?>> it = (Iterable<Property<?>>) propertySet;
        return new DynamicFilterPanel<>(fromProperties(it));
    }

    // ── Dialog Utilities ──────────────────────────────────────────────────

    /**
     * Makes a {@link Dialog} resizable and draggable by the header.
     *
     * <p>
     * Enables users to:
     * <ul>
     * <li>Resize the dialog by dragging its bottom-right corner</li>
     * <li>Drag the dialog by its header area</li>
     * </ul>
     *
     * <p>
     * <strong>Usage:</strong>
     * 
     * <pre>{@code
     * Dialog dialog = new Dialog();
     * dialog.add(filterPanel);
     * DynamicFilterPanel.makeDialogResizableAndDraggable(dialog);
     * dialog.open();
     * }</pre>
     *
     * @param dialog the dialog to enhance (not null)
     */
    public static void makeDialogResizableAndDraggable(Dialog dialog) {
        Objects.requireNonNull(dialog, "dialog must not be null");
        dialog.setResizable(true);
        dialog.setDraggable(true);

    }

    // ── Configuration ─────────────────────────────────────────────────────

    /**
     * Sets the row combination mode.
     *
     * @param matchAll {@code true} to AND-combine all rows (default);
     *                 {@code false} to OR-combine them
     * @return this panel (for fluent chaining)
     */
    public DynamicFilterPanel<T> setMatchAll(boolean matchAll) {
        this.matchAll = matchAll;
        return this;
    }

    /**
     * Enables or disables <em>advanced filtering mode</em>.
     *
     * <p>
     * When advanced mode is active every row gains two extra controls:
     * <ul>
     * <li>A <strong>connector selector</strong> (And / Or) placed before the
     * property selector
     * on rows 2 and above — determines how that row is joined to the previous
     * result.</li>
     * <li>A <strong>NOT toggle</strong> — when activated, the row's condition is
     * negated
     * before being combined: {@code AND NOT (price > 100)}.</li>
     * </ul>
     * The global {@link #setMatchAll(boolean)} flag is ignored while advanced mode
     * is on.
     * </p>
     *
     * <p>
     * Opt-in usage:
     * 
     * <pre>{@code
     * DynamicFilterPanel<Product> panel = DynamicFilterPanel.of(Product.class);
     * panel.setAdvancedMode(true);
     * }</pre>
     * </p>
     *
     * @param advancedMode {@code true} to enable per-row connectors and NOT toggles
     * @return this panel (for fluent chaining)
     */
    public DynamicFilterPanel<T> setAdvancedMode(boolean advancedMode) {
        this.advancedMode = advancedMode;
        refreshRowModes();
        return this;
    }

    /**
     * Enable or disable annotation-driven input-width sizing for value inputs.
     *
     * <p>
     * When {@code true} (the <strong>default</strong>), each value input gets a CSS
     * width-tier modifier class derived from {@code @Column(length)},
     * {@code @Size(max)},
     * or {@code @Min}/{@code @Max} so short-code fields (country code, SKU) render
     * narrower
     * than long-text fields (description, notes).
     * </p>
     *
     * <p>
     * Set to {@code false} for uniform-width value inputs across all rows.
     * </p>
     *
     * @param enable {@code true} to activate annotation-driven width tiers
     *               (default)
     * @return this panel (for fluent chaining)
     */
    public DynamicFilterPanel<T> setColumnLengthAwareWidth(boolean enable) {
        this.columnLengthAwareWidth = enable;
        return this;
    }

    // ── Multi-select item registration ────────────────────────────────────

    /**
     * Registers a <strong>static</strong> list of items for the given property's
     * {@link FilterOperator#IN} / {@link FilterOperator#NOT_IN} value input.
     *
     * <p>
     * Use this for in-memory data or when all options are known upfront:
     * </p>
     * 
     * <pre>{@code
     * panel.setItems("team", List.of("Engineering", "Design", "Product"));
     * }</pre>
     *
     * @param <V>          item type
     * @param propertyName property name as returned by {@link PropInfo#name()}
     * @param items        list of items to display in the multi-select (not null)
     * @return this panel (for fluent chaining)
     */
    public <V> DynamicFilterPanel<T> setItems(String propertyName, List<V> items) {
        Objects.requireNonNull(propertyName, "propertyName must not be null");
        Objects.requireNonNull(items, "items must not be null");
        multiSelectDataProviders.put(propertyName,
                DataProvider.ofCollection(new ArrayList<>(items)));
        return this;
    }

    /**
     * Registers a <strong>lazy</strong> (backend-driven) data provider for the
     * given property's
     * {@link FilterOperator#IN} / {@link FilterOperator#NOT_IN} value input.
     *
     * <p>
     * Use this for database-backed dropdowns where filtering is done server-side:
     * </p>
     * 
     * <pre>{@code
     * panel.setLazyItems("team",
     *         query -> teamService.find(query.getFilter().orElse(""), query.getOffset(), query.getLimit()),
     *         query -> teamService.count(query.getFilter().orElse("")));
     * }</pre>
     *
     * <p>
     * The filter string passed to the callbacks is the text the user typed in the
     * search box.
     * </p>
     *
     * @param <V>           item type
     * @param propertyName  property name as returned by {@link PropInfo#name()}
     * @param fetchCallback callback that fetches items for a given filter string
     * @param countCallback callback that counts items for a given filter string
     * @return this panel (for fluent chaining)
     */
    public <V> DynamicFilterPanel<T> setLazyItems(String propertyName,
            CallbackDataProvider.FetchCallback<V, String> fetchCallback,
            CallbackDataProvider.CountCallback<V, String> countCallback) {
        Objects.requireNonNull(propertyName, "propertyName must not be null");
        Objects.requireNonNull(fetchCallback, "fetchCallback must not be null");
        Objects.requireNonNull(countCallback, "countCallback must not be null");
        multiSelectDataProviders.put(propertyName,
                DataProvider.fromFilteringCallbacks(fetchCallback, countCallback));
        return this;
    }

    // ── FilterInputGroup ──────────────────────────────────────────────────

    /**
     * Returns the {@link QueryFilter} committed by the last "Apply filter" click.
     */
    @Override
    public Optional<QueryFilter> getQueryFilter() {
        return Optional.ofNullable(appliedFilter);
    }

    /** {@code true} if at least one filter was committed via "Apply filter". */
    @Override
    public boolean isAnyActive() {
        return appliedFilter != null;
    }

    /**
     * Returns the number of active (committed) filter rows — i.e. the count of rows
     * that were present and complete when the last "Apply filter" click occurred.
     *
     * <p>
     * Use this to show a badge on the button that opens the filter dialog:
     * </p>
     * 
     * <pre>{@code
     * panel.addFilterChangeListener(e -> {
     *     int n = panel.getActiveFilterCount();
     *     badge.setVisible(n > 0);
     *     badge.setText(String.valueOf(n));
     * });
     * }</pre>
     *
     * @return number of committed filter rows (0 when no filter has been applied)
     */
    public int getActiveFilterCount() {
        return appliedRows.size();
    }

    /**
     * Registers a callback that is fired only when the built-in <em>Apply
     * filter</em>
     * button is clicked — and <strong>not</strong> when a row is removed via the ×
     * button
     * (which also re-applies automatically).
     *
     * <p>
     * The primary use-case is closing an enclosing
     * {@link com.vaadin.flow.component.dialog.Dialog} after the user explicitly
     * commits
     * the filter, while keeping the dialog open when rows are interactively
     * removed:
     * </p>
     * 
     * <pre>{@code
     * panel.addApplyListener(dialog::close);
     * }</pre>
     *
     * @param listener callback fired after the filter has been committed (not null)
     * @return a {@link Registration} that removes the listener when invoked
     */
    public Registration addApplyListener(SerializableRunnable listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        applyListeners.add(listener);
        return () -> applyListeners.remove(listener);
    }

    /**
     * Programmatically applies the given {@link QueryFilter} without changing the
     * row UI.
     *
     * <p>
     * This is useful when restoring a previously persisted filter state or when
     * driving
     * the panel from tests. The filter is stored as the current applied filter, the
     * active
     * filter snapshot is cleared, and all registered {@link FilterChangeListener}s
     * are
     * notified.
     * </p>
     *
     * <p>
     * <strong>Note:</strong> this method does not synchronize the visual filter
     * rows with the
     * supplied filter. It only updates the committed filter returned by
     * {@link #getQueryFilter()}.
     * If you need the UI to reflect a filter expression, rebuild the rows
     * explicitly.
     * </p>
     *
     * @param filter the filter to apply, or {@code null} to clear the current
     *               filter
     */
    public void applyFilterProgrammatically(QueryFilter filter) {
        QueryFilter prev = appliedFilter;
        appliedFilter = filter;
        appliedRows = List.of();
        clearValidationErrors();
        fireChange(prev);
    }

    /**
     * Removes all rows, clears the applied filter, fires a change event, and
     * re-adds the first row.
     */
    @Override
    public void resetAll() {
        QueryFilter prev = appliedFilter;
        rows.clear();
        rowsContainer.removeAll();
        appliedFilter = null;
        appliedRows = List.of();
        updateAddButton(); // all rows gone → re-enable the button
        clearValidationErrors();
        fireChange(prev);
        // Restore the initial UX: always show at least one empty row after reset.
        addRow();
    }
    /**
     * Not supported for this dynamic panel (no static property bindings).
     *
     * @return always {@link Optional#empty()}
     */
    @Override
    public <V> Optional<FilterInput<V>> getFilterInput(Property<V> property) {
        return Optional.empty();
    }

    /**
     * Not supported for this dynamic panel (no static property bindings).
     *
     * @return always an empty stream
     */
    @Override
    public Stream<FilterInputGroup.PropertyBinding<?>> getPropertyBindings() {
        return Stream.empty();
    }

    @Override
    public Registration addFilterChangeListener(FilterChangeListener<?> listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    // ── In-memory predicate ───────────────────────────────────────────────

    /**
     * Returns a {@link Predicate} that evaluates the <em>last applied</em> filter
     * conditions against a bean instance using reflection.
     *
     * <p>
     * Useful for in-memory data sources where no Holon {@code Datastore} is
     * involved:
     * </p>
     * 
     * <pre>{@code
     * panel.addFilterChangeListener(e -> {
     *     shown.clear();
     *     shown.addAll(allProducts.stream().filter(panel.toPredicate()).toList());
     *     listing.getDataProvider().refreshAll();
     * });
     * }</pre>
     *
     * @return a predicate that matches everything when no filter is applied
     */
    public Predicate<T> toPredicate() {
        var snapshot = new ArrayList<>(appliedRows);
        if (snapshot.isEmpty()) {
            return bean -> true;
        }
        if (advancedMode) {
            return buildAdvancedPredicate(snapshot);
        }
        return bean -> matchAll
                ? snapshot.stream().allMatch(r -> matchRow(bean, r))
                : snapshot.stream().anyMatch(r -> matchRow(bean, r));
    }

    /**
     * Builds an in-memory {@link Predicate} that evaluates each row individually,
     * applying the row's connector in the same order as
     * {@link #buildAdvancedQueryFilter(List)}.
     */
    private Predicate<T> buildAdvancedPredicate(List<AppliedRow> snapshot) {
        Predicate<T> pred = null;
        for (AppliedRow row : snapshot) {
            Predicate<T> rowPred = bean -> matchRow(bean, row);

            if (pred == null) {
                pred = rowPred;
                continue;
            }

            // snapshot for XOR (pred appears on both sides)
            final Predicate<T> acc = pred;
            final Predicate<T> cond = rowPred;

            pred = switch (row.connector()) {
                case AND -> acc.and(cond);
                case OR -> acc.or(cond);
                case AND_NOT -> acc.and(cond.negate());
                case OR_NOT -> acc.or(cond.negate());
                case NAND -> acc.and(cond).negate();
                case NOR -> acc.or(cond).negate();
                case XOR -> acc.and(cond.negate()).or(acc.negate().and(cond));
            };
        }
        return pred != null ? pred : bean -> true;
    }

    // ── Private: UI ───────────────────────────────────────────────────────

    /**
     * Programmatically adds a new (empty) filter row. The row starts with
     * "Select filter" as placeholder and "Equals" pre-selected as the default
     * operator.
     *
     * <p>
     * The "+ Add filter" button is disabled while the last row is incomplete,
     * but callers may always invoke this method directly to pre-populate rows
     * (e.g., in demo views or when restoring saved filter state).
     * </p>
     */
    public void addRow() {
        var row = new FilterRow(availableProps, this::removeRow, this::updateAddButton,
                multiSelectDataProviders, columnLengthAwareWidth);
        rows.add(row);
        rowsContainer.add(row);
        row.applyAdvancedMode(advancedMode, true);
        updateAddButton();
    }

    private void removeRow(FilterRow row) {
        rows.remove(row);
        rowsContainer.remove(row);
        updateAddButton();
        refreshRowModes();
        clearValidationErrors();
        applyFilter(); // immediately re-apply so removing a condition takes effect at once
    }

    /** Propagates the current advanced-mode state to all existing rows. */
    private void refreshRowModes() {
        rows.forEach(r -> r.applyAdvancedMode(advancedMode, true));
    }

    /**
     * Enables the "+ Add filter" button only when there are no rows yet,
     * or when the last (most recently added) row is fully configured.
     * Also clears row-level error highlights as soon as any row becomes complete.
     */
    private void updateAddButton() {
        addBtn.setEnabled(rows.isEmpty() || rows.getLast().isComplete());
        // Auto-dismiss row errors once the user has filled in a value.
        rows.stream()
                .filter(FilterRow::isComplete)
                .forEach(r -> r.removeClassName("filter-panel__row--error"));
        // Hide the panel error if at least one row is now complete.
        if (rows.stream().anyMatch(FilterRow::isComplete)) {
            validationError.removeClassName("filter-panel__validation-error--visible");
        }
    }

    /**
     * Called by the Apply button — validates first, then applies the filter and
     * notifies apply-only listeners.
     */
    private void applyFilterFromButton() {
        // Determine which rows are incomplete (have a property selected but missing
        // operator/value).
        // Rows that are entirely untouched (no property selected) are also counted as
        // incomplete.
        List<FilterRow> incompleteRows = rows.stream()
                .filter(r -> !r.isComplete())
                .toList();

        // If EVERY row is incomplete (nothing actionable has been configured), show
        // validation errors.
        boolean anyComplete = rows.stream().anyMatch(FilterRow::isComplete);
        if (!anyComplete) {
            showValidationErrors(incompleteRows);
            return;
        }

        // At least one row is complete — clear any previous validation state and apply.
        clearValidationErrors();
        applyFilter();
        int size = applyListeners.size();
        for (int i = 0; i < size; i++) {
            applyListeners.get(i).run();
        }
    }

    /**
     * Marks incomplete rows with the error CSS class and makes the panel-level
     * error message visible.
     */
    private void showValidationErrors(List<FilterRow> incompleteRows) {
        incompleteRows.forEach(r -> r.addClassName("filter-panel__row--error"));
        validationError.addClassName("filter-panel__validation-error--visible");
    }

    /**
     * Removes error styling from all rows and hides the panel-level error message.
     */
    private void clearValidationErrors() {
        rows.forEach(r -> r.removeClassName("filter-panel__row--error"));
        validationError.removeClassName("filter-panel__validation-error--visible");
    }

    private void applyFilter() {
        QueryFilter prev = appliedFilter;

        appliedRows = rows.stream()
                .map(FilterRow::getAppliedRow)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        if (advancedMode) {
            appliedFilter = buildAdvancedQueryFilter(appliedRows);
        } else {
            appliedFilter = rows.stream()
                    .map(FilterRow::toQueryFilter)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce(matchAll ? QueryFilter::and : QueryFilter::or)
                    .orElse(null);
        }

        fireChange(prev);
    }

    /**
     * Builds a combined {@link QueryFilter} from an ordered list of applied rows,
     * honouring each row's connector (AND / OR / AND NOT / OR NOT / NAND / NOR /
     * XOR)
     * and optional NOT negation.
     *
     * <p>
     * The row's own {@code negated} flag is applied first; the connector then
     * combines
     * the accumulated result with that (possibly negated) condition:
     * </p>
     * <ul>
     * <li>AND → {@code acc AND cond}</li>
     * <li>OR → {@code acc OR cond}</li>
     * <li>AND_NOT → {@code acc AND NOT cond}</li>
     * <li>OR_NOT → {@code acc OR NOT cond}</li>
     * <li>NAND → {@code NOT(acc AND cond)}</li>
     * <li>NOR → {@code NOT(acc OR cond)}</li>
     * <li>XOR → {@code (acc AND NOT cond) OR (NOT acc AND cond)}</li>
     * </ul>
     */
    private static QueryFilter buildAdvancedQueryFilter(List<AppliedRow> rows) {
        QueryFilter result = null;
        for (AppliedRow row : rows) {
            PropInfo propInfo = new PropInfo(
                    row.propName(), row.propName(), row.propType(), row.rawProperty());
            Optional<QueryFilter> rowQf = buildFilter(propInfo, row.op(), row.val(), row.val2());
            if (rowQf.isEmpty())
                continue;

            QueryFilter cond = rowQf.get();

            if (result == null) {
                result = cond;
                continue;
            }

            result = switch (row.connector()) {
                case AND -> result.and(cond);
                case OR -> result.or(cond);
                case AND_NOT -> result.and(cond.not());
                case OR_NOT -> result.or(cond.not());
                case NAND -> result.and(cond).not();
                case NOR -> result.or(cond).not();
                case XOR -> result.and(cond.not()).or(result.not().and(cond));
            };
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void fireChange(QueryFilter prev) {
        if (listeners.isEmpty())
            return;
        var event = new DefaultFilterChangeEvent<>(STUB_SOURCE,
                Optional.ofNullable(prev), Optional.ofNullable(appliedFilter), true);
        // Indexed loop with size snapshot: same ConcurrentModificationException safety
        // as new ArrayList<>(listeners) but without allocating a defensive copy.
        int size = listeners.size();
        for (int i = 0; i < size; i++) {
            ((FilterChangeListener) listeners.get(i)).filterChanged(event);
        }
    }

    // ── Private: bean introspection ───────────────────────────────────────

    /**
     * Introspects the bean class using Holon's {@link BeanPropertySet}.
     * Each discovered {@link PathProperty} is stored as the {@code rawProperty}
     * so it can be used directly as a query-filter operand and for
     * {@link PropertyBox#getValue(Property)} value extraction via
     * {@link BeanUtils#readFromBean(Object)}.
     * <p>
     * The human-readable label for each property is resolved via
     * {@link LocalizationProvider#localize(com.holonplatform.core.i18n.Localizable)},
     * which honours {@code @Caption} annotations and the active i18n context.
     * </p>
     */
    private static List<PropInfo> introspect(Class<?> type) {
        var bps = BeanPropertySet.create(type);
        var list = new ArrayList<PropInfo>();
        for (PathProperty<?> prop : bps) {
            String name = prop.relativeName();
            // localize() → i18n messageCode → @Caption default message → camelCase fallback
            String label = LocalizationProvider.localize(prop).orElseGet(() -> toLabel(name));
            int colLen = resolveColumnLength(type, name);
            list.add(new PropInfo(name, label, wrapPrimitive(prop.getType()), prop, colLen));
        }
        return list;
    }

    /**
     * Resolves the effective column length for a bean field by reading, via
     * reflection,
     * the first matching annotation with a {@code length()}, {@code max()}, or
     * {@code value()} method.
     * Walks the class hierarchy for inherited fields.
     *
     * <p>
     * <strong>Resolution order (first match wins at each tier):</strong>
     * </p>
     * <ol>
     * <li>{@code @Column(length=N)} where N &gt; 0 and N ≠ 255 — authoritative
     * schema width;
     * 255 is the JPA spec default and is skipped to avoid false positives.</li>
     * <li>{@code @Size(max=N)} where N ≠ {@link Integer#MAX_VALUE} — Bean
     * Validation string length.</li>
     * <li>{@code @Max(value=N)} / {@code @Min(value=N)} — numeric bounds; the digit
     * count of the
     * boundary value is used as the effective length (e.g. {@code @Max(9999)} → 4
     * chars → XS).
     * Negative {@code @Min} values add one extra char for the minus sign.</li>
     * </ol>
     *
     * <p>
     * Uses annotation reflection (no compile-time dependency on JPA or Bean
     * Validation APIs)
     * so the {@code core} module stays dependency-free. Any reflective failure
     * returns {@code 0}.
     * </p>
     *
     * @param beanClass the entity / bean class to inspect
     * @param fieldName the simple field name (e.g. {@code "firstName"})
     * @return resolved length, or {@code 0} if not determinable
     */
    private static int resolveColumnLength(Class<?> beanClass, String fieldName) {
        Class<?> cls = beanClass;
        while (cls != null && cls != Object.class) {
            try {
                java.lang.reflect.Field f = cls.getDeclaredField(fieldName);
                int sizeMax = 0; // from @Size(max)
                int numDigits = 0; // from @Max / @Min digit count

                for (java.lang.annotation.Annotation ann : f.getAnnotations()) {
                    String name = ann.annotationType().getSimpleName();
                    try {
                        switch (name) {
                            case "Column" -> {
                                int len = (int) ann.annotationType().getMethod("length").invoke(ann);
                                if (len > 0 && len != 255)
                                    return len; // authoritative; skip JPA default
                            }
                            case "Size" -> {
                                int max = (int) ann.annotationType().getMethod("max").invoke(ann);
                                if (max != Integer.MAX_VALUE)
                                    sizeMax = max;
                            }
                            case "Max" -> {
                                long val = (long) ann.annotationType().getMethod("value").invoke(ann);
                                if (val < Long.MAX_VALUE) {
                                    numDigits = Math.max(numDigits, Long.toString(val).length());
                                }
                            }
                            case "Min" -> {
                                long val = (long) ann.annotationType().getMethod("value").invoke(ann);
                                int digits = Long.toString(Math.abs(val)).length() + (val < 0 ? 1 : 0);
                                numDigits = Math.max(numDigits, digits);
                            }
                            default -> {
                                /* other annotations ignored */ }
                        }
                    } catch (Exception ignored) {
                    }
                }

                if (sizeMax > 0)
                    return sizeMax; // @Size beats @Min/@Max
                if (numDigits > 0)
                    return numDigits; // @Min/@Max fallback
                break; // field found in this class; stop climbing
            } catch (NoSuchFieldException e) {
                cls = cls.getSuperclass();
            } catch (Exception e) {
                break;
            }
        }
        return 0;
    }

    /**
     * Builds a {@link PropInfo} list directly from Holon {@link Property} objects.
     * The raw property reference is preserved for direct query-filter building.
     * <p>
     * Labels are resolved the same way as in {@link #introspect}: via
     * {@link LocalizationProvider#localize(com.holonplatform.core.i18n.Localizable)}.
     * </p>
     */
    private static List<PropInfo> fromProperties(Iterable<? extends Property<?>> properties) {
        var list = new ArrayList<PropInfo>();
        for (Property<?> prop : properties) {
            // Use relativeName() for PathProperty (e.g. "addedOn") so toLabel() can
            // produce "Added On" — same strategy as introspect(). Fall back to
            // toString() only for non-path property types.
            String name = prop instanceof PathProperty<?> pp ? pp.relativeName() : prop.toString();
            String label = LocalizationProvider.localize(prop).orElseGet(() -> toLabel(name));
            list.add(new PropInfo(name, label, prop.getType(), prop));
        }
        return list;
    }

    private static String toLabel(String name) {
        var sb = new StringBuilder(name.length() + 4);
        // charAt avoids the char[] allocation of toCharArray()
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c) && !sb.isEmpty())
                sb.append(' ');
            sb.append(sb.isEmpty() ? Character.toUpperCase(c) : c);
        }
        return sb.toString();
    }

    // ── Package: QueryFilter builder ──────────────────────────────────────

    /**
     * Returns the effective {@link Property} to use as filter operand.
     * Raw property from bean introspection or user-provided property is always
     * present;
     * the {@link PathProperty} fallback handles edge cases only.
     */
    @SuppressWarnings("rawtypes")
    private static Property effectiveProperty(PropInfo prop) {
        return prop.rawProperty() != null
                ? prop.rawProperty()
                : PathProperty.create(prop.name(), wrapPrimitive(prop.type()));
    }

    /**
     * Builds a {@link QueryFilter} from a single filter row's state.
     *
     * <p>
     * Uses the same Holon Core type-detection pattern as
     * {@link com.holonplatform.core.datastore.beans.BeanDatastoreHelper}:
     * {@link TypeUtils} for type checks,
     * {@link StringProperty} fluent API for string operations,
     * and generic {@link QueryFilter} factory methods for numeric/temporal
     * comparisons.
     * </p>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    static Optional<QueryFilter> buildFilter(PropInfo prop, FilterOperator op,
            Object val, Object val2) {
        final Class<?> type = wrapPrimitive(prop.type());
        final Property effProp = effectiveProperty(prop);

        // ── IS_EMPTY / IS_NOT_EMPTY ────────────────────────────────────────────
        // For strings: null-OR-blank / (not-null AND not-blank)
        // For other types: null / not-null
        if (op.isNullaryCheck()) {
            if (TypeUtils.isString(type) && effProp instanceof StringProperty sp) {
                QueryFilter isNull = QueryFilter.isNull(sp);
                QueryFilter isEmpty = QueryFilter.eq(sp, "");
                return op == FilterOperator.IS_EMPTY
                        ? Optional.of(isNull.or(isEmpty))
                        : Optional.of(QueryFilter.isNotNull(sp).and(isEmpty.not()));
            }
            return Optional.of(op == FilterOperator.IS_EMPTY
                    ? QueryFilter.isNull(effProp)
                    : QueryFilter.isNotNull(effProp));
        }

        // ── STRING operators ───────────────────────────────────────────────────
        // Uses StringProperty fluent API (same pattern as BeanDatastoreHelper query
        // filter building)
        if (TypeUtils.isString(type)) {
            String sVal = val instanceof String s ? s : null;
            if (sVal == null || sVal.isBlank())
                return Optional.empty();
            // BeanPropertySet gives StringProperty for String fields; fall back for
            // hand-built properties
            StringProperty sp = effProp instanceof StringProperty existing
                    ? existing
                    : StringProperty.create(prop.name());
            return switch (op) {
                case EQUALS -> Optional.of(QueryFilter.eq(sp, sVal));
                case NOT_EQUALS -> Optional.of(QueryFilter.eq(sp, sVal).not());
                case CONTAINS -> Optional.of(sp.containsIgnoreCase(sVal)); // typed fluent API
                case NOT_CONTAINS -> Optional.of(sp.containsIgnoreCase(sVal).not());
                case STARTS_WITH -> Optional.of(sp.startsWithIgnoreCase(sVal));
                case ENDS_WITH -> Optional.of(sp.endsWithIgnoreCase(sVal));
                default -> Optional.empty();
            };
        }

        // ── BETWEEN ────────────────────────────────────────────────────────────
        // Works for both numeric (goe/loe) and temporal (goe/loe accept Comparable)
        if (op == FilterOperator.BETWEEN) {
            QueryFilter from = val != null ? QueryFilter.goe(effProp, val) : null;
            QueryFilter to = val2 != null ? QueryFilter.loe(effProp, val2) : null;
            if (from != null && to != null)
                return Optional.of(from.and(to));
            if (from != null)
                return Optional.of(from);
            if (to != null)
                return Optional.of(to);
            return Optional.empty();
        }

        if (val == null)
            return Optional.empty();

        // ── IN / NOT_IN ────────────────────────────────────────────────────────
        // val is a Set<Object> from MultiSelectComboBox.getValue()
        if (op == FilterOperator.IN || op == FilterOperator.NOT_IN) {
            if (!(val instanceof Collection<?> coll) || coll.isEmpty())
                return Optional.empty();
            List<QueryFilter> eqFilters = coll.stream()
                    .map(v -> (QueryFilter) QueryFilter.eq(effProp, v))
                    .toList();
            return QueryFilter.anyOf(eqFilters)
                    .map(f -> op == FilterOperator.NOT_IN ? f.not() : f);
        }

        // ── NUMERIC, TEMPORAL, BOOLEAN, ENUM comparisons ───────────────────────
        // Generic QueryFilter factory methods work for any Comparable-valued
        // Property<T>
        return switch (op) {
            case EQUALS -> Optional.of(QueryFilter.eq(effProp, val));
            case NOT_EQUALS -> Optional.of(QueryFilter.eq(effProp, val).not());
            case GREATER_THAN, AFTER -> Optional.of(QueryFilter.gt(effProp, val));
            case LESS_THAN, BEFORE -> Optional.of(QueryFilter.lt(effProp, val));
            case GREATER_OR_EQUALS, ON_OR_AFTER -> Optional.of(QueryFilter.goe(effProp, val));
            case LESS_OR_EQUALS, ON_OR_BEFORE -> Optional.of(QueryFilter.loe(effProp, val));
            default -> Optional.empty();
        };
    }

    // ── Private: in-memory predicate helpers ──────────────────────────────

    /**
     * Extracts the property value from the given item and evaluates the filter
     * operator.
     * <ul>
     * <li>If the item is already a {@link PropertyBox}, the raw {@link Property}
     * reference
     * is used directly for type-safe value extraction.</li>
     * <li>For regular beans, {@link BeanUtils#readFromBean(Object)} converts the
     * bean to a
     * {@link PropertyBox} via {@link com.holonplatform.core.beans.BeanIntrospector}
     * —
     * the canonical Holon Core bean → PropertyBox conversion — then the same
     * {@link PropertyBox#getValue(Property)} call is used.</li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    private static boolean matchRow(Object bean, AppliedRow row) {
        try {
            if (row.rawProperty() == null)
                return true;
            final PropertyBox box = bean instanceof PropertyBox pb
                    ? pb
                    : BeanUtils.readFromBean(bean); // Bean → PropertyBox via BeanIntrospector
            Object actual = box.getValue((Property<Object>) row.rawProperty());
            return evalOp(row.op(), actual, row.val(), row.val2());
        } catch (Exception ignored) {
            return true;
        }
    }

    private static boolean evalOp(FilterOperator op, Object actual, Object val, Object val2) {
        return switch (op) {
            case EQUALS -> Objects.equals(actual, val);
            case NOT_EQUALS -> !Objects.equals(actual, val);
            case IN -> val instanceof Collection<?> c && c.contains(actual);
            case NOT_IN -> !(val instanceof Collection<?> c && c.contains(actual));
            case CONTAINS -> actual instanceof String a && val instanceof String v
                    && a.toLowerCase().contains(v.toLowerCase());
            case NOT_CONTAINS -> !(actual instanceof String a && val instanceof String v
                    && a.toLowerCase().contains(v.toLowerCase()));
            case STARTS_WITH -> actual instanceof String a && val instanceof String v
                    && a.toLowerCase().startsWith(v.toLowerCase());
            case ENDS_WITH -> actual instanceof String a && val instanceof String v
                    && a.toLowerCase().endsWith(v.toLowerCase());
            case IS_EMPTY -> actual == null || (actual instanceof String s && s.isBlank());
            case IS_NOT_EMPTY -> actual != null && !(actual instanceof String s && s.isBlank());
            case GREATER_THAN, AFTER -> cmp(actual, val) > 0;
            case LESS_THAN, BEFORE -> cmp(actual, val) < 0;
            case GREATER_OR_EQUALS, ON_OR_AFTER -> cmp(actual, val) >= 0;
            case LESS_OR_EQUALS, ON_OR_BEFORE -> cmp(actual, val) <= 0;
            case BETWEEN -> {
                boolean fromOk = val == null || cmp(actual, val) >= 0;
                boolean toOk = val2 == null || cmp(actual, val2) <= 0;
                yield fromOk && toOk;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static int cmp(Object a, Object b) {
        if (a == null && b == null)
            return 0;
        if (a == null)
            return -1;
        if (b == null)
            return 1;
        if (a instanceof Comparable c)
            return c.compareTo(b);
        return 0;
    }

    // ── Private: type helpers ─────────────────────────────────────────────

    static Class<?> wrapPrimitive(Class<?> type) {
        if (TypeUtils.isInteger(type))
            return Integer.class;
        if (TypeUtils.isLong(type))
            return Long.class;
        if (TypeUtils.isDouble(type))
            return Double.class;
        if (TypeUtils.isFloat(type))
            return Float.class;
        if (TypeUtils.isShort(type))
            return Short.class;
        if (TypeUtils.isByte(type))
            return Byte.class;
        if (TypeUtils.isBoolean(type))
            return Boolean.class;
        return type;
    }

    /**
     * Creates a type-appropriate {@link Input} for the given property type by
     * delegating
     * to {@link Input#create(Class)}, which backs the Holon
     * {@code DefaultInputPropertyRenderer}
     * pipeline. This covers String, Boolean (checkbox), Enum (with caption
     * generator),
     * LocalDate, LocalTime, LocalDateTime, legacy Date, and Number — more types
     * than
     * a hand-rolled switch, with correct per-type defaults (e.g.
     * {@code emptyValuesAsNull}
     * for String inputs so {@code val1Supplier.get()} reliably returns {@code null}
     * on clear).
     * Falls back to a plain text field for unrecognised types.
     *
     * <p>
     * <strong>LocalTime handling:</strong> For LocalTime properties, creates a
     * combined
     * date+time picker (DatePicker + TimePicker) displayed horizontally. The filter
     * uses
     * the selected date at midnight (00:00) combined with the selected time. Users
     * can
     * filter by specific times across any date context.
     * </p>
     */
    @SuppressWarnings("unchecked")
    static Input<?> createInput(Class<?> rawType) {
        Class<?> type = wrapPrimitive(rawType);

        // Boolean: a "True / False" ComboBox is much clearer than the default checkbox.
        // The ComboBox value (Boolean.TRUE / Boolean.FALSE) plugs straight into
        // QueryFilter.eq()
        // and evalOp(EQUALS/NOT_EQUALS) without any extra handling.
        if (Boolean.class.equals(type)) {
            var cb = new ComboBox<Boolean>();
            cb.setItems(Boolean.TRUE, Boolean.FALSE);
            cb.setItemLabelGenerator(b -> Boolean.TRUE.equals(b) ? "True" : "False");
            return Input.builder(cb).build();
        }

        // LocalTime: create a combined date+time picker returning LocalDateTime
        // (date at selected date, time at selected time).
        if (LocalTime.class.equals(type)) {
            return createLocalTimeInput();
        }

        Optional<Input<Object>> created = Input.create((Class<Object>) type);
        return created.isPresent()
                ? created.get()
                : Input.string().placeholder(LocalizationProvider.localize("Enter a value", "filter.enter_value")).build();
    }

    /**
     * Creates a time input for LocalTime properties.
     * Returns a TimePicker wrapped as an Input<LocalTime>.
     */
    private static Input<LocalTime> createLocalTimeInput() {
        TimePicker timePicker = new TimePicker();
        timePicker.setPlaceholder(LocalizationProvider.localize("Time", "filter.time_placeholder"));
        timePicker.addClassName("filter-panel__time-input");
        return Input.builder(timePicker).build();
    }

    /**
     * Builds a {@link MultiSelectComboBox} for {@link FilterOperator#IN} /
     * {@link FilterOperator#NOT_IN}.
     *
     * <p>
     * Population strategy (first match wins):
     * </p>
     * <ol>
     * <li>A {@link DataProvider} registered via {@link #setItems} or
     * {@link #setLazyItems}.</li>
     * <li>For {@link Enum} types: the enum constants are used automatically — no
     * registration needed.</li>
     * </ol>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    static MultiSelectComboBox<Object> createMultiSelectInput(PropInfo prop,
            Map<String, DataProvider> providers) {
        MultiSelectComboBox<Object> msb = new MultiSelectComboBox<>();
        msb.setPlaceholder(LocalizationProvider.localize("Select values", "filter.select_values"));

        DataProvider<Object, String> dp = providers.get(prop.name());
        if (dp != null) {
            // Registered provider — works for both static lists and lazy callbacks.
            msb.setItems(dp);
        } else if (TypeUtils.isEnum(wrapPrimitive(prop.type()))) {
            // Enum: auto-populate from constants; label = humanised constant name.
            Object[] constants = prop.type().getEnumConstants();
            if (constants != null) {
                msb.setItems(Arrays.asList(constants));
                msb.setItemLabelGenerator(
                        v -> v instanceof Enum<?> e ? toLabel(e.name()) : String.valueOf(v));
            }
        }
        // else: no items — the drop-down is empty until the user registers a provider.
        return msb;
    }

    // ── Stub FilterInput (event source) ────────────────────────────────────

    /**
     * Minimal stub that satisfies the {@link DefaultFilterChangeEvent} source
     * requirement.
     * {@code getInput()} throws; all other methods are no-ops / empty.
     */
    private static final FilterInput<Void> STUB_SOURCE = new FilterInput<>() {
        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public Optional<QueryFilter> getQueryFilter() {
            return Optional.empty();
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public void reset() {
        }

        @Override
        public Input<Void> getInput() {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public Component getComponent() {
            return Components.div().build();
        }

        @Override
        public Registration addFilterChangeListener(FilterChangeListener<Void> l) {
            return () -> {
            };
        }
    };

    // ── Inner class: FilterRow ────────────────────────────────────────────

    /**
     * A single filter condition row: [property] [operator] [value(s)] [×].
     */
    private static final class FilterRow extends Div {

        @Serial
        private static final long serialVersionUID = 1L;

        private PropInfo selectedProp;
        private FilterOperator selectedOp;

        /** {@code true} once the primary value input contains a non-blank value. */
        private boolean valueProvided = false;
        /** Callback fired whenever this row's completeness state may have changed. */
        private final SerializableRunnable onStateChange;

        /**
         * Whether to apply annotation-driven width-tier CSS classes to value inputs.
         */
        private final boolean columnLengthAwareWidth;

        /** Supplies the primary (or only) value from the current value input. */
        private SerializableSupplier<Object> val1Supplier = () -> null;
        /** Supplies the secondary bound value (BETWEEN only). */
        private SerializableSupplier<Object> val2Supplier = () -> null;
        /** Builds the complete QueryFilter for this row from current state. */
        private SerializableSupplier<Optional<QueryFilter>> filterSupplier = Optional::empty;

        /** AND/OR connector — only visible in advanced mode for rows 2 and above. */
        private final ComboBox<RowConnector> connectorSel;

        /** Logical category of the current value-input widget. */
        private enum InputMode {
            NULLARY, SINGLE, MULTI, BETWEEN
        }

        private static InputMode inputModeOf(FilterOperator op) {
            if (op == null)
                return null;
            if (op.isNullaryCheck())
                return InputMode.NULLARY;
            if (op.isMultiValue())
                return InputMode.MULTI;
            if (op.isBetween())
                return InputMode.BETWEEN;
            return InputMode.SINGLE;
        }

        private final ComboBox<FilterOperator> opSel;
        private final Div valueContainer;

        /** Per-property DataProviders forwarded from the outer panel. */
        @SuppressWarnings("rawtypes")
        private final Map<String, DataProvider> multiSelectDataProviders;

        @SuppressWarnings("rawtypes")
        FilterRow(List<PropInfo> props, SerializableConsumer<FilterRow> onRemove, SerializableRunnable onStateChange,
                Map<String, DataProvider> multiSelectDataProviders, boolean columnLengthAwareWidth) {
            this.onStateChange = onStateChange;
            this.multiSelectDataProviders = multiSelectDataProviders;
            this.columnLengthAwareWidth = columnLengthAwareWidth;
            addClassName("filter-panel__row");

            // ── Advanced-mode controls (hidden by default) ─────────────────
            connectorSel = new ComboBox<>();
            connectorSel.setItems(RowConnector.values());
            connectorSel.setItemLabelGenerator(RowConnector::getLabel);
            connectorSel.setValue(RowConnector.AND);
            connectorSel.addClassName("filter-panel__connector");
            // Start hidden; shown by applyAdvancedMode() when advanced mode is active.
            connectorSel.addClassName("filter-panel__connector--hidden");

            // ── Property selector ──────────────────────────────────────────
            var propSel = new ComboBox<PropInfo>();
            propSel.setItems(props);
            propSel.setItemLabelGenerator(PropInfo::label);
            propSel.setPlaceholder(LocalizationProvider.localize("Select filter", "filter.select_filter"));
            propSel.addClassName("filter-panel__prop-sel");

            opSel = new ComboBox<>();
            opSel.setItemLabelGenerator(FilterOperator::getLabel);
            opSel.addClassName("filter-panel__op-sel");
            opSel.setEnabled(false);

            valueContainer = Components.div().styleName("filter-panel__value-container").build();

            Button removeBtn = Components.button()
                    .icon(VaadinIcon.CLOSE_SMALL)
                    .error()
                    .tooltip("Remove selected filter")
                    .styleName("filter-panel__remove")
                    .withClickListener(e -> onRemove.accept(this))
                    .build();

            // ── Listeners ──────────────────────────────────────────────────

            // When property changes → repopulate operator list and auto-select Equals
            propSel.addValueChangeListener(e -> {
                selectedProp = e.getValue();
                clearValue();
                if (selectedProp != null) {
                    List<FilterOperator> operators = FilterOperator.forType(selectedProp.type());
                    opSel.setItems(operators);
                    opSel.setEnabled(true);
                    // Always default to EQUALS (valid for every type).
                    // If the value is already EQUALS (retained by setItems), no change event fires
                    // —
                    // so we also update selectedOp and rebuild the value input manually.
                    if (FilterOperator.EQUALS.equals(opSel.getValue())) {
                        selectedOp = FilterOperator.EQUALS;
                        rebuildValueInput();
                    } else {
                        // setValue fires the opSel value-change listener which calls rebuildValueInput.
                        opSel.setValue(FilterOperator.EQUALS);
                    }
                } else {
                    // No property selected: restore display-only EQUALS placeholder
                    opSel.setItems(FilterOperator.EQUALS);
                    opSel.setValue(FilterOperator.EQUALS);
                    opSel.setEnabled(false);
                }
                onStateChange.run();
            });

            // When operator changes → rebuild value input only when the widget category
            // changes.
            // filterSupplier lambdas read `this.selectedOp` (instance field, not a closure
            // copy),
            // so updating selectedOp is sufficient when the same widget type is reused —
            // no DOM removal/re-creation needed (preserves the user's typed value, avoids
            // flicker).
            opSel.addValueChangeListener(e -> {
                FilterOperator prevOp = selectedOp;
                selectedOp = e.getValue();
                if (selectedProp != null && selectedOp != null) {
                    if (inputModeOf(selectedOp) != inputModeOf(prevOp)) {
                        // Widget category changed (e.g. single → between, single → multi) — full
                        // rebuild.
                        clearValue();
                        rebuildValueInput();
                    }
                    // else: same widget stays alive; filterSupplier already reads the new
                    // selectedOp.
                } else {
                    clearValue();
                }
                onStateChange.run();
            });

            // ── Initial visual state ─────────────────────────────────────
            // Show "Equals" in the disabled operator ComboBox before any property is
            // picked.
            // The listeners above are already registered, so setValue fires opSel listener
            // →
            // sets selectedOp = EQUALS (harmless since selectedProp is still null).
            opSel.setItems(FilterOperator.EQUALS);
            opSel.setValue(FilterOperator.EQUALS);

            add(propSel, opSel, valueContainer, connectorSel, removeBtn);
        }

        /**
         * Returns {@code true} when this row has enough information to be considered
         * complete:
         * <ul>
         * <li>property and operator are both selected</li>
         * <li>if the operator requires a value (not IS_EMPTY/IS_NOT_EMPTY), a non-blank
         * value is present</li>
         * </ul>
         */
        boolean isComplete() {
            if (selectedProp == null || selectedOp == null)
                return false;
            if (selectedOp.isNullaryCheck())
                return true;
            return valueProvided;
        }

        /**
         * Shows or hides the connector pill at the end of the row.
         * The connector appears on every row in advanced mode — it signals
         * how this row will be joined to the next one.
         *
         * @param advanced      {@code true} when advanced mode is active
         * @param showConnector unused — kept for API compatibility; all rows show
         *                      the connector when advanced mode is on
         */
        void applyAdvancedMode(boolean advanced, boolean showConnector) {
            if (advanced) {
                connectorSel.removeClassName("filter-panel__connector--hidden");
            } else {
                connectorSel.addClassName("filter-panel__connector--hidden");
            }
        }

        /** Builds the {@link QueryFilter} for this row from the current UI state. */
        Optional<QueryFilter> toQueryFilter() {
            return filterSupplier.get();
        }

        /**
         * Captures the current state as an {@link AppliedRow} snapshot,
         * now including the raw {@link Property} reference (if any) so that
         * {@link PropertyBox} evaluation uses {@link PropertyBox#getValue(Property)}.
         */
        Optional<AppliedRow> getAppliedRow() {
            // Use isComplete() — mirrors the toQueryFilter() path and excludes rows where
            // a property/operator is selected but the required value has not been entered
            // yet.
            // Without this guard, a null val causes evalOp(EQUALS, actual, null) → false
            // for
            // every bean in AND mode, silently hiding all results.
            if (!isComplete())
                return Optional.empty();
            RowConnector connector = connectorSel.getValue() != null
                    ? connectorSel.getValue()
                    : RowConnector.AND;
            return Optional.of(new AppliedRow(
                    selectedProp.name(), selectedProp.type(), selectedOp,
                    val1Supplier.get(), val2Supplier.get(),
                    selectedProp.rawProperty(),
                    connector));
        }

        private void clearValue() {
            valueContainer.removeAll();
            val1Supplier = () -> null;
            val2Supplier = () -> null;
            filterSupplier = Optional::empty;
            valueProvided = false;
            showValuePlaceholder(); // always keep a visible field in the container
        }

        /**
         * Adds a non-interactive placeholder TextField that looks identical to a
         * real value input. The field is blocked from user interaction purely via
         * CSS ({@code pointer-events: none} on {@code ::part(input-field)}), so it
         * never appears disabled or read-only, yet no keystroke reaches it.
         * Replaced by the appropriate typed input as soon as a property is chosen.
         */
        private void showValuePlaceholder() {
            String enterValue = LocalizationProvider.localize("Enter a value", "filter.enter_value");
            valueContainer.add(Components.input.string()
                    .placeholder(enterValue)
                    .ariaLabel(enterValue)
                    .styleNames("filter-panel__value-input", "filter-panel__value-placeholder")
                    .build().getComponent());

        }

        private void showNullaryPlaceholder() {
            valueContainer.add(
                    Components.input.string()
                            .placeholder("No value needed")
                            .disabled()
                            .styleNames("filter-panel__value-input", "filter-panel__value-placeholder")
                            .build().getComponent());
        }

        private void rebuildValueInput() {
            if (selectedOp.isNullaryCheck()) {
                // IS_EMPTY / IS_NOT_EMPTY: show a disabled value slot so the row reads
                // correctly.
                valueContainer.removeAll();
                showNullaryPlaceholder();
                filterSupplier = () -> buildFilter(selectedProp, selectedOp, null, null);
                return;
            }

            // Swap placeholder for the typed input — removeAll() prevents double-content.
            valueContainer.removeAll();
            Class<?> type = selectedProp.type();

            if (selectedOp.isMultiValue()) {
                // ── IN / NOT_IN: MultiSelectComboBox ─────────────────────────
                MultiSelectComboBox<Object> msb = createMultiSelectInput(selectedProp, multiSelectDataProviders);
                msb.addClassName("filter-panel__value-multi-select");
                valueContainer.add(msb);
                val1Supplier = () -> msb.getValue().isEmpty() ? null : msb.getValue();
                filterSupplier = () -> buildFilter(selectedProp, selectedOp,
                        msb.getValue().isEmpty() ? null : msb.getValue(), null);
                msb.addValueChangeListener(e -> {
                    valueProvided = !e.getValue().isEmpty();
                    onStateChange.run();
                });
                if (TypeUtils.isString(type) && !multiSelectDataProviders.containsKey(selectedProp.name())) {
                    msb.setAllowCustomValue(true);
                    msb.addCustomValueSetListener(e -> {
                        String custom = e.getDetail() != null ? e.getDetail().trim() : "";
                        if (custom.isEmpty()) {
                            return;
                        }
                        if (!msb.getListDataView().getItems().anyMatch(custom::equals)) {
                            var items = new ArrayList<>(msb.getListDataView().getItems().toList());
                            items.add(custom);
                            msb.setItems(items);
                        }
                        msb.select(custom);
                        valueProvided = !msb.getValue().isEmpty();
                        onStateChange.run();
                    });
                }
            } else if (selectedOp.isBetween()) {
                Input<?> from = createInput(type);
                Input<?> to = createInput(type);
                applyInputPlaceholder(from);
                applyInputPlaceholder(to);
                applyValueInputClasses(from.getComponent(), selectedProp);
                applyValueInputClasses(to.getComponent(), selectedProp);

                // Create a grouped container for BETWEEN inputs
                var betweenContainer = Components.div()
                        .styleName("filter-panel__between-container")
                        .build();
                betweenContainer.add(from.getComponent());

                var sep = Components.span().text("–").styleName("filter-panel__between-sep").build();
                betweenContainer.add(sep);

                betweenContainer.add(to.getComponent());
                valueContainer.add(betweenContainer);

                val1Supplier = from::getValue;
                val2Supplier = to::getValue;
                filterSupplier = () -> buildFilter(selectedProp, selectedOp,
                        from.getValue(), to.getValue());
                attachValueListener(from);
                attachValueListener(to);
            } else {
                Input<?> input = createInput(type);
                applyInputPlaceholder(input);
                applyValueInputClasses(input.getComponent(), selectedProp);
                valueContainer.add(input.getComponent());
                val1Supplier = input::getValue;
                filterSupplier = () -> buildFilter(selectedProp, selectedOp, input.getValue(), null);
                attachValueListener(input);
            }
        }

        /**
         * Sets "Enter a value" as placeholder on the input's component if it
         * implements {@link HasPlaceholder}.
         */
        private static void applyInputPlaceholder(Input<?> input) {
            Component comp = input.getComponent();
            if (comp instanceof HasPlaceholder hp) {
                hp.setPlaceholder(LocalizationProvider.localize("Enter a value", "filter.enter_value"));
            }
        }

        /**
         * Applies the base {@code filter-panel__value-input} class plus an optional
         * width-tier modifier derived from the property's resolved
         * {@link FieldWidthTier}.
         * The modifier is only added when {@link #columnLengthAwareWidth} is
         * {@code true}.
         *
         * <p>
         * This keeps Java code annotation-free: only CSS class names are assigned;
         * all visual sizing lives exclusively in {@code filter-panel.css}.
         * </p>
         */
        private void applyValueInputClasses(Component comp, PropInfo prop) {
            comp.addClassName("filter-panel__value-input");
            if (columnLengthAwareWidth) {
                String modifier = FieldWidthTier.of(prop.columnLength()).getModifierClass();
                if (modifier != null) {
                    comp.addClassName(modifier);
                }
            }
        }

        /**
         * Attaches a value-change listener that updates {@link #valueProvided} and
         * notifies the panel so the "+ Add filter" button can be updated.
         * Uses a raw-type cast because {@code Input<?>} prevents direct listener
         * attachment.
         */
        private void attachValueListener(Input<?> input) {
            input.addValueChangeListener(e -> {
                // Always evaluate completeness against the primary (val1) supplier —
                // that covers both single-input and BETWEEN rows.
                Object v = val1Supplier.get();
                valueProvided = v != null && !(v instanceof String s && s.isBlank());
                onStateChange.run();
            });
        }
    }
}
