/*
 * Copyright 2016-2024 Axioma srl.
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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.TotalsCardBuilder;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;

import java.io.Serial;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A generic summary/totals card component, made of a stack of label/value
 * {@link TotalsRow}s, optionally ending with a visually emphasised
 * {@link TotalsRow.Variant#GRAND_TOTAL} row.
 *
 * <p>Composition:
 * <pre>
 * TotalsCard
 *  └── TotalsRow (0..N)
 *       ├── TotalsLabel ({@link TotalsRow#setLabel(String)} / {@link TotalsRow#setLabel(Localizable)})
 *       └── TotalsValue ({@link TotalsRow#setValue(String)} / {@link TotalsRow#setValue(Localizable)})
 * </pre>
 *
 * <p>Preferred usage via builder:
 * <pre>{@code
 * TotalsCard totals = TotalsCard.builder()
 *     .row("Revenue YTD", "€1,420,400")
 *     .row("Volume discount (3-yr)", "−€142,040", TotalsRow.Variant.DISCOUNT)
 *     .row("Net revenue YTD", "€1,278,360")
 *     .row("Recurring annual fee", "+ €24,000/yr", TotalsRow.Variant.MUTED)
 *     .row("Open AR (4 invoices)", "€14,810", TotalsRow.Variant.WARNING)
 *     .row("YTD total", "€1,293,170", TotalsRow.Variant.GRAND_TOTAL)
 *     .build();
 * }</pre>
 *
 * <p>All visual styling is handled by {@code totals-card.css}; no inline styles or Lumo tokens
 * are used. Colours are exposed as overridable CSS custom properties (e.g. {@code --totals-card-grand-color}).
 */
@StyleSheet("context://totals-card.css")
public class TotalsCard extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // Internal state
    // -----------------------------------------------------------------------

    private final List<TotalsRow> rows = new LinkedList<>();

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates an empty totals card.
     */
    public TotalsCard() {
        addClassName("totals-card");
    }

    // -----------------------------------------------------------------------
    // Static factory
    // -----------------------------------------------------------------------

    /**
     * Obtain a {@link TotalsCardBuilder} to create a new {@link TotalsCard}.
     *
     * @return a new {@link TotalsCardBuilder}
     */
    public static TotalsCardBuilder builder() {
        return TotalsCardBuilder.create();
    }

    // -----------------------------------------------------------------------
    // Row API
    // -----------------------------------------------------------------------

    /**
     * Returns the current rows, in display order.
     *
     * @return an unmodifiable view of the current rows (never null)
     */
    public List<TotalsRow> getRows() {
        return Collections.unmodifiableList(rows);
    }

    /**
     * Appends a row to the bottom of the card.
     *
     * @param row the row to add (not null)
     */
    public void addRow(TotalsRow row) {
        if (row == null) {
            return;
        }
        rows.add(row);
        add(row);
    }

    /**
     * Appends a {@link TotalsRow.Variant#DEFAULT} row built from plain strings.
     *
     * @param label the row label text (not null)
     * @param value the row value text (not null)
     */
    public void addRow(String label, String value) {
        addRow(new TotalsRow(label, value));
    }

    /**
     * Appends a row built from plain strings with the given variant.
     *
     * @param label   the row label text (not null)
     * @param value   the row value text (not null)
     * @param variant the row visual variant (not null)
     */
    public void addRow(String label, String value, TotalsRow.Variant variant) {
        addRow(new TotalsRow(label, value, variant));
    }

    /**
     * Appends a row built from Holon {@link Localizable} messages with the given variant.
     *
     * @param label   the localizable row label (not null)
     * @param value   the localizable row value (not null)
     * @param variant the row visual variant (not null)
     */
    public void addRow(Localizable label, Localizable value, TotalsRow.Variant variant) {
        addRow(new TotalsRow(label, value, variant));
    }

    /**
     * Removes the given row, if present.
     *
     * @param row the row to remove
     */
    public void removeRow(TotalsRow row) {
        if (row != null && rows.remove(row)) {
            remove(row);
        }
    }

    /**
     * Removes all the rows from the card.
     */
    public void clearRows() {
        rows.forEach(this::remove);
        rows.clear();
    }
}

