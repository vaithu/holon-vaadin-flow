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
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;

/**
 * A circular donut-style gauge showing a big centred amount, a small caption label, and a
 * percentage-driven ring fill (e.g. an offer amount at a given loan-to-value percentage).
 *
 * <p>Typically embedded inside a {@link TotalsCard} configured with
 * {@link TotalsCard.Variant#APPRAISAL TotalsCard.Variant.APPRAISAL} (see
 * {@code TotalsCardConfigurator.gauge(...)}), but it is a
 * fully standalone component and can be used on its own.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * TotalsGauge gauge = new TotalsGauge("$1,475", "Offer @ 65% LTV", 65);
 * add(gauge);
 *
 * // Later, react to a slider change:
 * gauge.setPercent(70).setLabel("Offer @ 70% LTV");
 * }</pre>
 *
 * <p>The ring fill is driven by the {@code --totals-gauge-percent} CSS custom property (0-100),
 * updated via {@link #setPercent(double)}. Colours are exposed as overridable CSS custom
 * properties in {@code totals-gauge.css} (e.g. {@code --totals-gauge-fill-start},
 * {@code --totals-gauge-fill-end}, {@code --totals-gauge-bg}).</p>
 */
@StyleSheet("context://totals-gauge.css")
public class TotalsGauge extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Div ring = new Div();
    private final Span amountSpan = new Span();
    private final Span labelSpan = new Span();

    private double percent;

    /**
     * Creates an empty gauge (0%, no amount/label text).
     */
    public TotalsGauge() {
        addClassName("totals-gauge");
        ring.addClassName("totals-gauge__ring");

        Div center = new Div();
        center.addClassName("totals-gauge__center");
        amountSpan.addClassName("totals-gauge__amount");
        labelSpan.addClassName("totals-gauge__label");
        center.add(amountSpan, labelSpan);

        ring.add(center);
        add(ring);
        setPercent(0);
    }

    /**
     * Creates a gauge with the given initial amount, label and fill percentage.
     *
     * @param amount  the big centred amount text (e.g. {@code "$1,475"})
     * @param label   the small caption below the amount (e.g. {@code "Offer @ 65% LTV"})
     * @param percent the initial ring fill percentage (0-100, clamped)
     */
    public TotalsGauge(String amount, String label, double percent) {
        this();
        setAmount(amount);
        setLabel(label);
        setPercent(percent);
    }

    /**
     * Updates the big centred amount text.
     *
     * @param amount amount text; {@code null} clears it
     * @return this (fluent)
     */
    public TotalsGauge setAmount(String amount) {
        amountSpan.setText(amount != null ? amount : "");
        return this;
    }

    /**
     * Updates the big centred amount text from a {@link Localizable} message.
     *
     * @param amount the localizable amount (not null)
     * @return this (fluent)
     */
    public TotalsGauge setAmount(Localizable amount) {
        amountSpan.setText(resolve(amount));
        return this;
    }

    /**
     * Updates the small caption label below the amount.
     *
     * @param label label text; {@code null} clears it
     * @return this (fluent)
     */
    public TotalsGauge setLabel(String label) {
        labelSpan.setText(label != null ? label : "");
        return this;
    }

    /**
     * Updates the small caption label from a {@link Localizable} message.
     *
     * @param label the localizable label (not null)
     * @return this (fluent)
     */
    public TotalsGauge setLabel(Localizable label) {
        labelSpan.setText(resolve(label));
        return this;
    }

    /**
     * Updates the ring fill percentage, clamped to the {@code [0, 100]} range.
     *
     * @param percent the new fill percentage
     * @return this (fluent)
     */
    public TotalsGauge setPercent(double percent) {
        this.percent = Math.max(0, Math.min(100, percent));
        ring.getStyle().set("--totals-gauge-percent", String.valueOf(this.percent));
        return this;
    }

    /**
     * Returns the current ring fill percentage.
     *
     * @return the current percentage (0-100)
     */
    public double getPercent() {
        return percent;
    }

    private static String resolve(Localizable localizable) {
        if (localizable == null) {
            return "";
        }
        return LocalizationProvider.localize(localizable)
                .orElseGet(() -> localizable.getMessage() != null ? localizable.getMessage() : "");
    }
}


