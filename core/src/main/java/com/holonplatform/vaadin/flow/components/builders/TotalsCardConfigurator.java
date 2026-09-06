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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTotalsCardConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsCard;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsGauge;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsRow;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * Configurator for {@link TotalsCard} components.
 *
 * <p>Extends the standard Holon Platform {@link ComponentConfigurator}, {@link HasSizeConfigurator}
 * and {@link HasStyleConfigurator} contracts, adding rows configuration methods, plus the
 * {@link TotalsCard.Variant#APPRAISAL TotalsCard.Variant.APPRAISAL} sections (header, gauge, terms,
 * actions).</p>
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 *
 * @see TotalsCardBuilder
 */
public interface TotalsCardConfigurator<C extends TotalsCardConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    // -----------------------------------------------------------------------
    // Rows
    // -----------------------------------------------------------------------

    /**
     * Appends a pre-built row.
     *
     * @param row the row to add (not null)
     * @return this configurator (for chaining)
     */
    C row(TotalsRow row);

    /**
     * Appends a {@link TotalsRow.Variant#DEFAULT} row built from plain strings.
     *
     * @param label the row label text (not null)
     * @param value the row value text (not null)
     * @return this configurator (for chaining)
     */
    C row(String label, String value);

    /**
     * Appends a row built from plain strings with the given variant.
     *
     * @param label   the row label text (not null)
     * @param value   the row value text (not null)
     * @param variant the row visual variant (not null)
     * @return this configurator (for chaining)
     */
    C row(String label, String value, TotalsRow.Variant variant);

    /**
     * Appends a row built from Holon {@link Localizable} messages with the given variant.
     *
     * @param label   the localizable row label (not null)
     * @param value   the localizable row value (not null)
     * @param variant the row visual variant (not null)
     * @return this configurator (for chaining)
     */
    C row(Localizable label, Localizable value, TotalsRow.Variant variant);

    /**
     * Removes any previously added row.
     *
     * @return this configurator (for chaining)
     */
    C clearRows();

    // -----------------------------------------------------------------------
    // Variant
    // -----------------------------------------------------------------------

    /**
     * Sets the visual variant of the card (see {@link TotalsCard.Variant}).
     *
     * @param variant the variant to apply; {@code null} resets to {@link TotalsCard.Variant#DEFAULT}
     * @return this configurator (for chaining)
     */
    C variant(TotalsCard.Variant variant);

    // -----------------------------------------------------------------------
    // Header (eyebrow + editable highlight field) — TotalsCard.Variant.APPRAISAL
    // -----------------------------------------------------------------------

    /**
     * Sets the small uppercase eyebrow caption shown above the header (e.g. {@code "Live Appraisal"}).
     *
     * @param text the eyebrow text (not null)
     * @return this configurator (for chaining)
     */
    C eyebrow(String text);

    /**
     * Sets the eyebrow caption from a {@link Localizable} message.
     *
     * @param text the localizable eyebrow text (not null)
     * @return this configurator (for chaining)
     */
    C eyebrow(Localizable text);

    /**
     * Sets an editable highlight field in the header: an optional caption, an editable text input,
     * and an optional trailing suffix (e.g. {@code "24K spot rate"}, {@code "148.00"}, {@code "/g"}).
     *
     * @param caption       optional caption text shown before the input (may be null/empty)
     * @param initialValue  the initial field value
     * @param suffix        optional suffix text shown after the input (may be null/empty)
     * @param onValueChange callback invoked with the new value on every user edit (may be null)
     * @return this configurator (for chaining)
     */
    C highlight(String caption, String initialValue, String suffix, Consumer<String> onValueChange);

    // -----------------------------------------------------------------------
    // Gauge — TotalsCard.Variant.APPRAISAL
    // -----------------------------------------------------------------------

    /**
     * Sets a pre-built {@link TotalsGauge} ring shown between the header and the rows.
     *
     * @param gauge the gauge component (not null)
     * @return this configurator (for chaining)
     */
    C gauge(TotalsGauge gauge);

    /**
     * Convenience shortcut for {@code gauge(new TotalsGauge(amount, label, percent))}.
     *
     * @param amount  the big centred amount text (e.g. {@code "$1,475"})
     * @param label   the small caption below the amount (e.g. {@code "Offer @ 65% LTV"})
     * @param percent the initial ring fill percentage (0-100, clamped)
     * @return this configurator (for chaining)
     */
    C gauge(String amount, String label, double percent);

    // -----------------------------------------------------------------------
    // Terms (slider, term toggle, note) — TotalsCard.Variant.APPRAISAL
    // -----------------------------------------------------------------------

    /**
     * Sets a percentage-style slider (e.g. a loan-to-value slider), rendered as a labelled native
     * range input with a live percentage readout.
     *
     * @param label    the slider caption (e.g. {@code "LTV"})
     * @param min      the minimum value
     * @param max      the maximum value
     * @param value    the initial value
     * @param onChange callback invoked with the new value on every user drag (may be null)
     * @return this configurator (for chaining)
     */
    C slider(String label, int min, int max, int value, IntConsumer onChange);

    /**
     * Sets the mutually-exclusive term toggle (e.g. {@code 15 / 30 / 60 Days}), built on top of
     * {@code ChipGroup}.
     *
     * @param options           the option labels, in display order (not null, not empty)
     * @param selectedIndex     the zero-based index of the initially selected option
     * @param onSelectionChange callback invoked with the newly selected index on every change
     *                          (may be null)
     * @return this configurator (for chaining)
     */
    C toggleGroup(List<String> options, int selectedIndex, IntConsumer onSelectionChange);

    /**
     * Sets the small note text shown below the slider/toggle (e.g. an interest accrual disclaimer).
     *
     * @param text the note text (not null)
     * @return this configurator (for chaining)
     */
    C termsNote(String text);

    /**
     * Sets the note text from a {@link Localizable} message.
     *
     * @param text the localizable note text (not null)
     * @return this configurator (for chaining)
     */
    C termsNote(Localizable text);

    // -----------------------------------------------------------------------
    // Actions — TotalsCard.Variant.APPRAISAL
    // -----------------------------------------------------------------------

    /**
     * Sets the primary (emphasised) action button.
     *
     * @param label   the button text
     * @param onClick the click callback (may be null)
     * @return this configurator (for chaining)
     */
    C primaryAction(String label, Runnable onClick);

    /**
     * Sets the secondary action button.
     *
     * @param label   the button text
     * @param onClick the click callback (may be null)
     * @return this configurator (for chaining)
     */
    C secondaryAction(String label, Runnable onClick);

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Get a new {@link BaseTotalsCardConfigurator} to configure an existing {@link TotalsCard} component.
     *
     * @param totalsCard the totals card component to configure (not null)
     * @return a new {@link BaseTotalsCardConfigurator}
     */
    static BaseTotalsCardConfigurator configure(TotalsCard totalsCard) {
        return new DefaultTotalsCardConfigurator(totalsCard);
    }

    // -----------------------------------------------------------------------
    // Base configurator
    // -----------------------------------------------------------------------

    /**
     * Base (non-generic) {@link TotalsCardConfigurator}.
     */
    interface BaseTotalsCardConfigurator extends TotalsCardConfigurator<BaseTotalsCardConfigurator> {
    }
}

