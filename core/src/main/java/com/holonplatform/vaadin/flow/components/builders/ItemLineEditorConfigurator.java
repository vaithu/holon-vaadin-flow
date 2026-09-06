/*
 * Copyright 2016-2026 Axioma srl.
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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.vaadinplus.components.ItemLineEditor;
import com.holonplatform.vaadin.flow.vaadinplus.components.ItemLineEditor.Column;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsCard;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Fluent configurator for {@link ItemLineEditor} components.
 *
 * <p>Provides methods to set the title, the row factory, the columns, the row cap, the empty
 * state, the computed totals footer and the change listener of the editable line-item list,
 * along with all standard Holon Platform component properties (id, style, size…) inherited from
 * {@link ComponentConfigurator} and {@link HasSizeConfigurator}.</p>
 *
 * @param <T> the row bean type
 * @param <C> Concrete configurator type (for fluent chaining)
 * @see ItemLineEditorBuilder
 * @see ItemLineEditor
 */
public interface ItemLineEditorConfigurator<T, C extends ItemLineEditorConfigurator<T, C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    /**
     * Sets the section title shown in the toolbar. Default: {@code "Items"}.
     *
     * @param title the title text
     * @return this configurator for chaining
     */
    C title(String title);

    /**
     * Sets the section title from a {@link Localizable} descriptor.
     *
     * @param title the localizable title (not null)
     * @return this configurator for chaining
     */
    C title(Localizable title);

    /**
     * Sets the factory used to create a new, empty row instance whenever the user clicks
     * "Add item" (required).
     *
     * @param rowFactory the row factory (not null)
     * @return this configurator for chaining
     */
    C rowFactory(Supplier<T> rowFactory);

    /**
     * Adds a pre-configured {@link Column}. Build it via
     * {@link Column#of(String, String, java.util.function.Function)} and chain
     * {@link Column#width(String)} / {@link Column#flexGrow(double)} / {@link Column#detail()}
     * on it before passing it here.
     *
     * <pre>{@code
     * .addColumn(Column.of("qty", "Qty", line -> qtyField(line)).width("90px"))
     * }</pre>
     *
     * @param column the column to add (not null)
     * @return this configurator for chaining
     */
    C addColumn(Column<T> column);

    /**
     * Convenience shortcut for {@code addColumn(Column.of(key, header, renderer))} — a simple,
     * full-flex-grow, always-visible column with no width customisation.
     *
     * @param key      unique column key
     * @param header   column header text
     * @param renderer receives the row instance and returns the component to render/edit that
     *                 cell
     * @return this configurator for chaining
     */
    C addColumn(String key, String header, Function<T, Component> renderer);

    /**
     * Appends {@code count} initial rows created via the configured {@link #rowFactory(Supplier)}
     * (must be called after {@code rowFactory(...)}).
     *
     * @param count number of rows to pre-populate
     * @return this configurator for chaining
     */
    C initialRows(int count);

    /**
     * Caps the maximum number of rows. Default {@value ItemLineEditor#DEFAULT_MAX_ROWS}.
     *
     * @param maxRows the maximum number of rows
     * @return this configurator for chaining
     */
    C maxRows(int maxRows);

    /**
     * Sets a function that (re)builds the footer {@link TotalsCard} from the current row list,
     * called after every add/remove/replace and every row-change notification. If never set, no
     * footer is shown.
     *
     * @param footer the footer-building function
     * @return this configurator for chaining
     */
    C footer(Function<List<T>, TotalsCard> footer);

    /**
     * Sets the empty-state title and description shown when there are no rows.
     *
     * @param title       the empty-state title
     * @param description the empty-state description
     * @return this configurator for chaining
     */
    C emptyState(String title, String description);

    /**
     * Sets the empty-state icon. Default {@link VaadinIcon#STOCK}.
     *
     * @param icon the empty-state icon
     * @return this configurator for chaining
     */
    C emptyIcon(VaadinIcon icon);

    /**
     * Sets the "Add item" button text. Default {@code "Add item"}.
     *
     * @param text the button text
     * @return this configurator for chaining
     */
    C addButtonText(String text);

    /**
     * Registers a listener invoked whenever the row list changes (added, removed, replaced or
     * edited via {@code notifyRowChanged}).
     *
     * @param listener the change listener
     * @return this configurator for chaining
     */
    C onChange(Consumer<List<T>> listener);

    // -----------------------------------------------------------------------
    // Responsive mobile card + edit Sheet (generic, opt-in)
    // -----------------------------------------------------------------------

    /**
     * Enables a responsive mobile card list — shown in place of the grid below the mobile
     * breakpoint — with a bottom Sheet edit form built automatically from the configured
     * {@link Column} renderers.
     *
     * @param title    extracts the card's primary (bold) text from a row
     * @param subtitle extracts the card's secondary (muted) text from a row
     * @param value    extracts the card's trailing value text from a row
     * @return this configurator for chaining
     */
    C mobileCard(Function<T, String> title, Function<T, String> subtitle, Function<T, String> value);

    // -----------------------------------------------------------------------
    // Bulk "Add rows" toolbar stepper (generic, opt-in)
    // -----------------------------------------------------------------------

    /**
     * Enables (or disables) the bulk row-count stepper + "Add Rows" toolbar button.
     *
     * @param enabled {@code true} to show the bulk stepper controls
     * @return this configurator for chaining
     */
    C bulkAddEnabled(boolean enabled);

    /**
     * Sets whether the toolbar's single "Add item" button is visible. Default {@code true}.
     *
     * @param visible {@code false} to hide the single "Add item" button
     * @return this configurator for chaining
     */
    C addButtonVisible(boolean visible);

    // -----------------------------------------------------------------------
    // Generic catalog / bulk-content picker dialog (opt-in)
    // -----------------------------------------------------------------------

    /**
     * Configures a generic two-panel "picker" dialog for bulk-creating rows from an arbitrary
     * catalog/suggestion type {@code S}.
     *
     * @param <S>            the catalog/suggestion item type
     * @param buttonText     the toolbar button text (e.g. {@code "Pick Items"})
     * @param catalog        the catalog to search/select from
     * @param primaryLabel   extracts the catalog entry's primary (bold) label
     * @param secondaryLabel extracts the catalog entry's secondary (muted) label (may be null)
     * @param rowMapper      builds a new row from a selected catalog entry and its quantity
     * @return this configurator for chaining
     */
    <S> C itemPicker(String buttonText, List<S> catalog, Function<S, String> primaryLabel,
            Function<S, String> secondaryLabel, BiFunction<S, Integer, T> rowMapper);

}

