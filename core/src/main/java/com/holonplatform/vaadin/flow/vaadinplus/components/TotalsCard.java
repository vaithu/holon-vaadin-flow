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
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.builders.TotalsCardBuilder;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

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
 * <h3>Appraisal variant</h3>
 * <p>Setting {@link Variant#APPRAISAL} turns the card into a dark, self-contained "live appraisal"
 * summary: an eyebrow + editable highlight field header, an optional {@link TotalsGauge} ring, the
 * usual rows, an LTV-style slider and a mutually-exclusive term toggle, and up to two action
 * buttons. Sections appear in the DOM in the order their configuration method is first invoked, so
 * call them in the natural top-to-bottom order:
 * <pre>{@code
 * TotalsCard appraisal = TotalsCard.builder()
 *     .variant(TotalsCard.Variant.APPRAISAL)
 *     .eyebrow("Live Appraisal")
 *     .highlight("24K spot rate", "148.00", "/g", rate -> recompute(rate))
 *     .gauge("$1,475", "Offer @ 65% LTV", 65)
 *     .row("Total appraised value", "$2,289.89")
 *     .row("Testing / condition adj.", "−$23.48", TotalsRow.Variant.DISCOUNT)
 *     .row("Net appraised value", "$2,266.41")
 *     .row("Loan offer", "$1,475.00", TotalsRow.Variant.GRAND_TOTAL)
 *     .slider("LTV", 10, 80, 65, ltv -> recompute(ltv))
 *     .toggleGroup(List.of("15 Days", "30 Days", "60 Days"), 1, days -> setTerm(days))
 *     .termsNote("Interest accrues monthly at 4.0%.")
 *     .primaryAction("Approve & Continue to Signing", this::approve)
 *     .secondaryAction("Reset Ticket", this::reset)
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
    // Variant
    // -----------------------------------------------------------------------

    /**
     * Visual variant of the card as a whole.
     */
    public enum Variant {

        /**
         * The original light "account summary" surface (rows only).
         */
        DEFAULT("default"),
        /**
         * A dark, self-contained "live appraisal" surface: eyebrow + editable highlight field,
         * optional {@link TotalsGauge} ring, rows, an LTV-style slider, a term toggle and action
         * buttons.
         */
        APPRAISAL("appraisal");

        private final String cssModifier;

        Variant(String cssModifier) {
            this.cssModifier = cssModifier;
        }

        /**
         * Returns the CSS BEM modifier class associated with this variant.
         *
         * @return the modifier class name (never null)
         */
        public String getCssClass() {
            return "totals-card--" + cssModifier;
        }
    }

    // -----------------------------------------------------------------------
    // Internal state
    // -----------------------------------------------------------------------

    private final List<TotalsRow> rows = new LinkedList<>();

    private Variant variant = Variant.DEFAULT;

    private Div headerDiv;
    private Span eyebrowSpan;
    private Div highlightDiv;
    private Input<String> highlightInput;

    private Div gaugeWrap;
    private TotalsGauge gauge;

    private Div rowsContainer;

    private Div termsDiv;
    private Div sliderRow;
    private com.vaadin.flow.component.html.Input sliderRange;
    private Span sliderValueSpan;
    private ChipGroup toggleGroup;
    private Span termsNoteSpan;

    private Div actionsDiv;
    private Button primaryActionButton;
    private Button secondaryActionButton;

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
        rowsContainer().add(row);
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
        if (row != null && rows.remove(row) && rowsContainer != null) {
            rowsContainer.remove(row);
        }
    }

    /**
     * Removes all the rows from the card.
     */
    public void clearRows() {
        if (rowsContainer != null) {
            rows.forEach(rowsContainer::remove);
        }
        rows.clear();
    }

    private Div rowsContainer() {
        if (rowsContainer == null) {
            rowsContainer = new Div();
            rowsContainer.addClassName("totals-card__rows");
            add(rowsContainer);
        }
        return rowsContainer;
    }

    // -----------------------------------------------------------------------
    // Variant API
    // -----------------------------------------------------------------------

    /**
     * Returns the current visual variant.
     *
     * @return the current {@link Variant} (never {@code null} after construction)
     */
    public Variant getVariant() {
        return variant;
    }

    /**
     * Changes the visual variant, swapping the corresponding CSS modifier class.
     *
     * @param variant the new variant; {@code null} resets to {@link Variant#DEFAULT}
     * @return this (fluent)
     */
    public TotalsCard setVariant(Variant variant) {
        final Variant v = (variant != null) ? variant : Variant.DEFAULT;
        if (this.variant != null && this.variant != Variant.DEFAULT) {
            removeClassName(this.variant.getCssClass());
        }
        this.variant = v;
        if (v != Variant.DEFAULT) {
            addClassName(v.getCssClass());
        }
        return this;
    }

    // -----------------------------------------------------------------------
    // Header API (eyebrow + editable highlight field)
    // -----------------------------------------------------------------------

    private Div header() {
        if (headerDiv == null) {
            headerDiv = new Div();
            headerDiv.addClassName("totals-card__header");
            add(headerDiv);
        }
        return headerDiv;
    }

    /**
     * Sets (or updates) the small uppercase eyebrow caption shown above the header
     * (e.g. {@code "Live Appraisal"}).
     *
     * @param text the eyebrow text; {@code null} clears it
     * @return this (fluent)
     */
    public TotalsCard setEyebrow(String text) {
        if (eyebrowSpan == null) {
            eyebrowSpan = new Span();
            eyebrowSpan.addClassName("totals-card__eyebrow");
            header().addComponentAsFirst(eyebrowSpan);
        }
        eyebrowSpan.setText(text != null ? text : "");
        return this;
    }

    /**
     * Sets the eyebrow caption from a {@link Localizable} message.
     *
     * @param text the localizable eyebrow text (not null)
     * @return this (fluent)
     */
    public TotalsCard setEyebrow(Localizable text) {
        return setEyebrow(resolve(text));
    }

    /**
     * Sets (or updates) the editable highlight field in the header: an optional caption, an
     * editable text {@link Input}, and an optional trailing suffix (e.g. {@code "24K spot rate"},
     * {@code "148.00"}, {@code "/g"}).
     *
     * @param caption       optional caption text shown before the input (may be null/empty)
     * @param initialValue  the initial field value
     * @param suffix        optional suffix text shown after the input (may be null/empty)
     * @param onValueChange callback invoked with the new value on every user edit (may be null)
     * @return this (fluent)
     */
    public TotalsCard setHighlight(String caption, String initialValue, String suffix,
            Consumer<String> onValueChange) {
        if (highlightDiv == null) {
            highlightDiv = new Div();
            highlightDiv.addClassName("totals-card__highlight");
            header().add(highlightDiv);
            highlightInput = Components.input.string().build();
            highlightInput.getComponent().addClassName("totals-card__highlight-input");
        }
        highlightDiv.removeAll();
        if (caption != null && !caption.isEmpty()) {
            Span captionSpan = new Span(caption);
            captionSpan.addClassName("totals-card__highlight-caption");
            highlightDiv.add(captionSpan);
        }
        highlightInput.setValue(initialValue);
        highlightDiv.add(highlightInput.getComponent());
        if (suffix != null && !suffix.isEmpty()) {
            Span suffixSpan = new Span(suffix);
            suffixSpan.addClassName("totals-card__highlight-suffix");
            highlightDiv.add(suffixSpan);
        }
        if (onValueChange != null) {
            highlightInput.addValueChangeListener(e -> onValueChange.accept(e.getValue()));
        }
        return this;
    }

    // -----------------------------------------------------------------------
    // Gauge API
    // -----------------------------------------------------------------------

    /**
     * Sets (or replaces) the {@link TotalsGauge} ring shown between the header and the rows.
     *
     * @param gauge the gauge component (not null)
     * @return this (fluent)
     */
    public TotalsCard setGauge(TotalsGauge gauge) {
        if (gaugeWrap == null) {
            gaugeWrap = new Div();
            gaugeWrap.addClassName("totals-card__gauge-wrap");
            add(gaugeWrap);
        } else {
            gaugeWrap.removeAll();
        }
        this.gauge = gauge;
        if (gauge != null) {
            gaugeWrap.add(gauge);
        }
        return this;
    }

    /**
     * Convenience shortcut for {@code setGauge(new TotalsGauge(amount, label, percent))}.
     *
     * @param amount  the big centred amount text (e.g. {@code "$1,475"})
     * @param label   the small caption below the amount (e.g. {@code "Offer @ 65% LTV"})
     * @param percent the initial ring fill percentage (0-100, clamped)
     * @return this (fluent)
     */
    public TotalsCard setGauge(String amount, String label, double percent) {
        return setGauge(new TotalsGauge(amount, label, percent));
    }

    /**
     * Returns the current gauge, if any.
     *
     * @return the current {@link TotalsGauge}, or {@code null} if none was set
     */
    public TotalsGauge getGauge() {
        return gauge;
    }

    // -----------------------------------------------------------------------
    // Terms API (slider, term toggle, note)
    // -----------------------------------------------------------------------

    private Div terms() {
        if (termsDiv == null) {
            termsDiv = new Div();
            termsDiv.addClassName("totals-card__terms");
            add(termsDiv);
        }
        return termsDiv;
    }

    /**
     * Sets (or replaces) a percentage-style slider (e.g. a loan-to-value slider), rendered as a
     * labelled native range input with a live percentage readout.
     *
     * @param label    the slider caption (e.g. {@code "LTV"})
     * @param min      the minimum value
     * @param max      the maximum value
     * @param value    the initial value
     * @param onChange callback invoked with the new value on every user drag (may be null)
     * @return this (fluent)
     */
    public TotalsCard setSlider(String label, int min, int max, int value, IntConsumer onChange) {
        if (sliderRow == null) {
            sliderRow = new Div();
            sliderRow.addClassName("totals-card__slider-row");
            terms().addComponentAsFirst(sliderRow);

            Span labelSpan = new Span();
            labelSpan.addClassName("totals-card__slider-label");

            sliderRange = new com.vaadin.flow.component.html.Input();
            sliderRange.getElement().setAttribute("type", "range");
            sliderRange.addClassName("totals-card__slider-input");

            sliderValueSpan = new Span();
            sliderValueSpan.addClassName("totals-card__slider-value");

            sliderRow.add(labelSpan, sliderRange, sliderValueSpan);

            sliderRange.getElement().addPropertyChangeListener("value", "input", event -> {
                int v = (int) Double.parseDouble(sliderRange.getElement().getProperty("value", "0"));
                sliderValueSpan.setText(v + "%");
                if (onChange != null) {
                    onChange.accept(v);
                }
            });
        }
        ((Span) sliderRow.getComponentAt(0)).setText(label != null ? label : "");
        sliderRange.getElement().setAttribute("min", String.valueOf(min));
        sliderRange.getElement().setAttribute("max", String.valueOf(max));
        sliderRange.getElement().setProperty("value", String.valueOf(value));
        sliderValueSpan.setText(value + "%");
        return this;
    }

    /**
     * Sets (or replaces) the mutually-exclusive term toggle (e.g. {@code 15 / 30 / 60 Days}),
     * built on top of {@link ChipGroup}.
     *
     * @param options        the option labels, in display order (not null, not empty)
     * @param selectedIndex  the zero-based index of the initially selected option
     * @param onSelectionChange callback invoked with the newly selected index on every change
     *                          (may be null)
     * @return this (fluent)
     */
    public TotalsCard setToggleGroup(List<String> options, int selectedIndex, IntConsumer onSelectionChange) {
        if (toggleGroup == null) {
            toggleGroup = ChipGroup.create();
            toggleGroup.addClassName("totals-card__toggle");
            terms().add(toggleGroup);
        } else {
            toggleGroup.removeAll();
        }
        if (options != null) {
            for (int i = 0; i < options.size(); i++) {
                toggleGroup.addChip(options.get(i), i == selectedIndex);
            }
            if (onSelectionChange != null) {
                toggleGroup.onSelect(e -> onSelectionChange.accept(toggleGroup.getChips().indexOf(e.getChip())));
            }
        }
        return this;
    }

    /**
     * Sets (or updates) the small note text shown below the slider/toggle (e.g. an interest
     * accrual disclaimer).
     *
     * @param text the note text; {@code null} clears it
     * @return this (fluent)
     */
    public TotalsCard setTermsNote(String text) {
        if (termsNoteSpan == null) {
            termsNoteSpan = new Span();
            termsNoteSpan.addClassName("totals-card__terms-note");
            terms().add(termsNoteSpan);
        }
        termsNoteSpan.setText(text != null ? text : "");
        return this;
    }

    /**
     * Sets the note text from a {@link Localizable} message.
     *
     * @param text the localizable note text (not null)
     * @return this (fluent)
     */
    public TotalsCard setTermsNote(Localizable text) {
        return setTermsNote(resolve(text));
    }

    // -----------------------------------------------------------------------
    // Actions API
    // -----------------------------------------------------------------------

    private Div actions() {
        if (actionsDiv == null) {
            actionsDiv = new Div();
            actionsDiv.addClassName("totals-card__actions");
            add(actionsDiv);
        }
        return actionsDiv;
    }

    /**
     * Sets (or replaces) the primary (emphasised) action button.
     *
     * @param label   the button text
     * @param onClick the click callback (may be null)
     * @return this (fluent)
     */
    public TotalsCard setPrimaryAction(String label, Runnable onClick) {
        if (primaryActionButton == null) {
            primaryActionButton = Components.button()
                    .styleNames("totals-card__action-btn", "totals-card__action-btn--primary").build();
            actions().addComponentAsFirst(primaryActionButton);
        }
        primaryActionButton.setText(label != null ? label : "");
        if (onClick != null) {
            primaryActionButton.addClickListener(e -> onClick.run());
        }
        return this;
    }

    /**
     * Sets (or replaces) the secondary action button.
     *
     * @param label   the button text
     * @param onClick the click callback (may be null)
     * @return this (fluent)
     */
    public TotalsCard setSecondaryAction(String label, Runnable onClick) {
        if (secondaryActionButton == null) {
            secondaryActionButton = Components.button()
                    .styleNames("totals-card__action-btn", "totals-card__action-btn--secondary").build();
            actions().add(secondaryActionButton);
        }
        secondaryActionButton.setText(label != null ? label : "");
        if (onClick != null) {
            secondaryActionButton.addClickListener(e -> onClick.run());
        }
        return this;
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private static String resolve(Localizable localizable) {
        if (localizable == null) {
            return "";
        }
        return LocalizationProvider.localize(localizable)
                .orElseGet(() -> localizable.getMessage() != null ? localizable.getMessage() : "");
    }
}

