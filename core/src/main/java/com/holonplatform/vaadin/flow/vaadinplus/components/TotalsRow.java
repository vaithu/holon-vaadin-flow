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
import com.vaadin.flow.component.html.Div;

import java.io.Serial;

/**
 * A single label/value row of a {@link TotalsCard}.
 *
 * <p>Composition:
 * <pre>
 * TotalsRow
 *  ├── TotalsLabel ({@link #setLabel(String)} / {@link #setLabel(Localizable)})
 *  └── TotalsValue ({@link #setValue(String)} / {@link #setValue(Localizable)})
 * </pre>
 */
public class TotalsRow extends Div {

    @Serial
    private static final long serialVersionUID = 1L;

    // -----------------------------------------------------------------------
    // Variant
    // -----------------------------------------------------------------------

    /**
     * Visual variant of a {@link TotalsRow}.
     *
     * <p>When a row is created via the plain {@link TotalsRow#TotalsRow(String, String)} convenience
     * constructor (i.e. no variant is explicitly requested), the variant is automatically inferred
     * from the leading character(s) of the value text:
     * <ul>
     * <li>a leading {@code -} (hyphen-minus) or {@code −} (Unicode minus sign), or a value fully
     * wrapped in accounting-style parentheses (e.g. {@code "(€1,240)"}) &rarr; {@link #NEGATIVE}</li>
     * <li>a leading {@code +} &rarr; {@link #POSITIVE}</li>
     * <li>anything else &rarr; {@link #DEFAULT}</li>
     * </ul>
     * Explicitly passing a variant (including {@link #DEFAULT}) always takes precedence and disables
     * this auto-detection.
     */
    public enum Variant {

        /**
         * Neutral row, no particular emphasis.
         */
        DEFAULT("default"),
        /**
         * A row representing a discount / deduction; the value is rendered in the success colour.
         */
        DISCOUNT("discount"),
        /**
         * A row that requires attention (e.g. an open balance); the value is rendered in the warning colour.
         */
        WARNING("warning"),
        /**
         * A row representing an explicit gain, credit or otherwise favourable amount; the value is
         * rendered in the success colour. Also applied automatically (see {@link Variant}) when a
         * plain-string value starts with {@code +}.
         */
        POSITIVE("positive"),
        /**
         * A row representing a loss, overage or otherwise unfavourable amount; the value is rendered
         * in the danger/red colour. Also applied automatically (see {@link Variant}) when a
         * plain-string value is negative (leading {@code -} / {@code −}, or wrapped in parentheses).
         */
        NEGATIVE("negative"),
        /**
         * A secondary, informational / de-emphasised row (e.g. a recurring fee annotation); both the
         * label and the value are rendered in the muted colour.
         */
        MUTED("muted"),
        /**
         * The final, emphasised grand-total row: bold, larger font, top border and accent value colour.
         */
        GRAND_TOTAL("grand");

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
            return "totals-card__row--" + cssModifier;
        }
    }

    // -----------------------------------------------------------------------
    // Internal state
    // -----------------------------------------------------------------------

    private TotalsLabel currentLabel;
    private TotalsValue currentValue;
    private Variant currentVariant;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a row from plain strings, automatically inferring the visual variant from the value's
     * leading sign (see {@link Variant}): a negative-looking value (leading {@code -}/{@code −}, or
     * wrapped in parentheses) is rendered as {@link Variant#NEGATIVE}, a leading {@code +} as
     * {@link Variant#POSITIVE}, otherwise {@link Variant#DEFAULT}.
     *
     * @param label the label text (not null)
     * @param value the value text (not null)
     */
    public TotalsRow(String label, String value) {
        this(new TotalsLabel(label), new TotalsValue(value), detectSignVariant(value));
    }

    /**
     * Creates a row from plain strings with the given variant. Unlike
     * {@link #TotalsRow(String, String)}, the given variant is always honoured as-is, with no
     * automatic sign-based inference.
     *
     * @param label   the label text (not null)
     * @param value   the value text (not null)
     * @param variant the visual variant (not null)
     */
    public TotalsRow(String label, String value, Variant variant) {
        this(new TotalsLabel(label), new TotalsValue(value), variant);
    }

    /**
     * Creates a row from Holon {@link Localizable} messages with the given variant.
     *
     * @param label   the localizable label (not null)
     * @param value   the localizable value (not null)
     * @param variant the visual variant (not null)
     */
    public TotalsRow(Localizable label, Localizable value, Variant variant) {
        this(new TotalsLabel(label), new TotalsValue(value), variant);
    }

    /**
     * Creates a row from pre-built label/value components with the given variant.
     *
     * @param label   the label component (not null)
     * @param value   the value component (not null)
     * @param variant the visual variant (not null)
     */
    public TotalsRow(TotalsLabel label, TotalsValue value, Variant variant) {
        addClassName("totals-card__row");
        add(label, value);
        this.currentLabel = label;
        this.currentValue = value;
        setVariant(variant);
    }

    // -----------------------------------------------------------------------
    // Variant API
    // -----------------------------------------------------------------------

    /**
     * Unicode minus sign, as sometimes used in typeset negative amounts (e.g. {@code "−€142,040"})
     * instead of the plain ASCII hyphen-minus.
     */
    private static final char UNICODE_MINUS_SIGN = '\u2212';

    /**
     * Infers a {@link Variant} from the leading sign of a plain-text value, defaulting to
     * {@link Variant#DEFAULT} when the value is {@code null}, empty, or has no recognisable sign.
     *
     * @param value the value text to inspect
     * @return the inferred {@link Variant} (never null)
     */
    private static Variant detectSignVariant(String value) {
        if (value == null) {
            return Variant.DEFAULT;
        }
        final String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return Variant.DEFAULT;
        }
        // Accounting-style negative notation, e.g. "(€1,240)"
        if (trimmed.length() > 1 && trimmed.charAt(0) == '(' && trimmed.charAt(trimmed.length() - 1) == ')') {
            return Variant.NEGATIVE;
        }
        final char first = trimmed.charAt(0);
        if (first == '-' || first == UNICODE_MINUS_SIGN) {
            return Variant.NEGATIVE;
        }
        if (first == '+') {
            return Variant.POSITIVE;
        }
        return Variant.DEFAULT;
    }

    /**
     * Returns the current visual variant.
     *
     * @return the current {@link Variant} (never null after construction)
     */
    public Variant getVariant() {
        return currentVariant;
    }

    /**
     * Changes the visual variant, swapping the corresponding CSS modifier class.
     *
     * @param variant the new variant (not null)
     */
    public void setVariant(Variant variant) {
        if (this.currentVariant != null) {
            removeClassName(this.currentVariant.getCssClass());
        }
        this.currentVariant = variant;
        if (variant != null) {
            addClassName(variant.getCssClass());
        }
    }

    // -----------------------------------------------------------------------
    // Label API
    // -----------------------------------------------------------------------

    /**
     * Returns the current {@link TotalsLabel}.
     *
     * @return the current label component
     */
    public TotalsLabel getLabel() {
        return currentLabel;
    }

    /**
     * Sets (or replaces) the {@link TotalsLabel}.
     *
     * @param label the label component (not null)
     */
    public void setLabel(TotalsLabel label) {
        if (this.currentLabel != null) {
            remove(this.currentLabel);
        }
        this.currentLabel = label;
        addComponentAtIndex(0, label);
    }

    /**
     * Sets the label from a plain string.
     *
     * @param text the label text (not null)
     */
    public void setLabel(String text) {
        setLabel(new TotalsLabel(text));
    }

    /**
     * Sets the label from a Holon {@link Localizable}.
     *
     * @param localizable the localizable message (not null)
     */
    public void setLabel(Localizable localizable) {
        setLabel(new TotalsLabel(localizable));
    }

    // -----------------------------------------------------------------------
    // Value API
    // -----------------------------------------------------------------------

    /**
     * Returns the current {@link TotalsValue}.
     *
     * @return the current value component
     */
    public TotalsValue getValue() {
        return currentValue;
    }

    /**
     * Sets (or replaces) the {@link TotalsValue}.
     *
     * @param value the value component (not null)
     */
    public void setValue(TotalsValue value) {
        if (this.currentValue != null) {
            remove(this.currentValue);
        }
        this.currentValue = value;
        add(value);
    }

    /**
     * Sets the value from a plain string.
     *
     * @param text the value text (not null)
     */
    public void setValue(String text) {
        setValue(new TotalsValue(text));
    }

    /**
     * Sets the value from a Holon {@link Localizable}.
     *
     * @param localizable the localizable message (not null)
     */
    public void setValue(Localizable localizable) {
        setValue(new TotalsValue(localizable));
    }

    // -----------------------------------------------------------------------
    // Muted value API
    // -----------------------------------------------------------------------

    /**
     * Enables or disables the muted colour style on this row's value (e.g. for an informational,
     * non-monetary-total value such as a recurring fee annotation).
     *
     * @param muted {@code true} to render the value in the muted colour, {@code false} to restore
     *              the variant's default value colour
     */
    public void setValueMuted(boolean muted) {
        if (currentValue != null) {
            currentValue.setMuted(muted);
        }
    }
}

