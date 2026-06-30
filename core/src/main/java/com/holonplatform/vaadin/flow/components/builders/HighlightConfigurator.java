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
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHighlightConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Highlight;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.iyensoft.vaadin.flow.enums.HeadingLevel;
import com.vaadin.flow.component.Component;

/**
 * Fluent configurator for {@link Highlight} KPI cards.
 *
 * <p>Extends the standard Holon Platform {@link ComponentConfigurator}, {@link HasSizeConfigurator}
 * and {@link HasStyleConfigurator} contracts, adding Highlight-specific configuration methods.
 * All text-bearing methods have plain {@code String} and Holon {@link Localizable} overloads.</p>
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 *
 * @see HighlightBuilder
 */
public interface HighlightConfigurator<C extends HighlightConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    // ── Accent & layout ───────────────────────────────────────────────────────

    /**
     * Sets a coloured left-border accent stripe.
     *
     * @param color the accent colour (not null)
     * @return this configurator
     */
    C accentColor(Highlight.AccentColor color);

    /**
     * When {@code true}, the value is rendered visually above the heading
     * (large KPI number focal-point layout).
     *
     * @param valueFirst {@code true} to show value first
     * @return this configurator
     */
    C valueFirst(boolean valueFirst);

    /**
     * Shorthand for {@link #valueFirst(boolean) valueFirst(true)}.
     *
     * @return this configurator
     */
    default C valueFirst() {
        return valueFirst(true);
    }

    // ── Heading & subheading ──────────────────────────────────────────────────

    /**
     * Sets the heading text.
     *
     * @param text the heading text (not null)
     * @return this configurator
     */
    C heading(String text);

    /**
     * Sets the heading from a Holon {@link Localizable}.
     * The text is resolved on first attach and on locale change.
     *
     * @param localizable the localizable message (not null)
     * @return this configurator
     */
    C heading(Localizable localizable);

    /**
     * Sets the heading semantic level.
     *
     * @param level the heading element level (H1–H6 or NONE)
     * @return this configurator
     */
    C headingLevel(HeadingLevel level);

    /**
     * Sets a muted secondary line below the heading.
     *
     * @param text the subheading text
     * @return this configurator
     */
    C subheading(String text);

    /**
     * Sets the subheading from a Holon {@link Localizable}.
     *
     * @param localizable the localizable message (not null)
     * @return this configurator
     */
    C subheading(Localizable localizable);

    // ── Value ─────────────────────────────────────────────────────────────────

    /**
     * Sets the value text.
     *
     * @param text the value text (not null)
     * @return this configurator
     */
    C value(String text);

    /**
     * Sets the value from a Holon {@link Localizable}.
     *
     * @param localizable the localizable value (not null)
     * @return this configurator
     */
    C value(Localizable localizable);

    /**
     * Applies a {@link Font.Size} class to the value span.
     *
     * @param fontSize the font size to apply (not null)
     * @return this configurator
     */
    C valueFontSize(Font.Size fontSize);

    /**
     * Adds components displayed inline to the right of the value
     * (e.g. trend badge, delta percentage).
     *
     * @param components metric component(s)
     * @return this configurator
     */
    C inlineMetric(Component... components);

    // ── Details ───────────────────────────────────────────────────────────────

    /**
     * Sets the details row components shown below the value.
     *
     * @param components detail component(s)
     * @return this configurator
     */
    C details(Component... components);

    // ── Prefix / suffix ───────────────────────────────────────────────────────

    /**
     * Sets the prefix slot (left side of the body row).
     *
     * @param components prefix component(s)
     * @return this configurator
     */
    C prefix(Component... components);

    /**
     * Sets the suffix slot (right side of the body row).
     *
     * @param components suffix component(s)
     * @return this configurator
     */
    C suffix(Component... components);

    // ── Card header ───────────────────────────────────────────────────────────

    /**
     * Populates the optional card-header row.
     * {@code icon} goes to the left slot, {@code action} to the right slot.
     *
     * @param icon   left icon component (may be null)
     * @param action right action component (may be null)
     * @return this configurator
     */
    C cardHeader(Component icon, Component action);

    // ── Progress ──────────────────────────────────────────────────────────────

    /**
     * Sets the progress bar value in the range {@code [0.0, 1.0]}.
     *
     * @param percent progress value 0.0–1.0
     * @return this configurator
     */
    C progress(double percent);

    /**
     * Switches the progress bar to indeterminate (animated) mode.
     *
     * @param indeterminate {@code true} for indeterminate mode
     * @return this configurator
     */
    C progressIndeterminate(boolean indeterminate);

    /**
     * Sets the label shown above the progress bar.
     *
     * @param label the progress label text
     * @return this configurator
     */
    C progressLabel(String label);

    /**
     * Sets the progress label from a Holon {@link Localizable}.
     *
     * @param localizable the localizable label (not null)
     * @return this configurator
     */
    C progressLabel(Localizable localizable);

    // ── Sparkline ─────────────────────────────────────────────────────────────

    /**
     * Places a component (typically a {@code ChartJsComponent}) in the full-bleed
     * sparkline slot at the very bottom of the card.
     *
     * @param chart the sparkline chart component (null to clear)
     * @return this configurator
     */
    C sparkline(Component chart);

    // ── Accessibility ─────────────────────────────────────────────────────────

    /**
     * Sets the {@code aria-label} attribute on the card root element.
     *
     * <p>Useful when the heading alone is not sufficient for screen-reader context,
     * e.g. when multiple Highlight cards are on the same page with similar headings.</p>
     *
     * @param label the accessible label text
     * @return this configurator
     */
    C ariaLabel(String label);

    /**
     * Sets the {@code aria-label} from a Holon {@link Localizable}.
     *
     * @param localizable the localizable label (not null)
     * @return this configurator
     */
    C ariaLabel(Localizable localizable);

    // ── Configure factory ─────────────────────────────────────────────────────

    /**
     * Get a new {@link BaseHighlightConfigurator} to configure an existing {@link Highlight}.
     *
     * @param highlight the highlight component to configure (not null)
     * @return a new {@link BaseHighlightConfigurator}
     */
    static BaseHighlightConfigurator configure(Highlight highlight) {
        return new DefaultHighlightConfigurator(highlight);
    }

    // ── Base configurator ─────────────────────────────────────────────────────

    /**
     * Base (non-generic) {@link HighlightConfigurator}.
     */
    interface BaseHighlightConfigurator extends HighlightConfigurator<BaseHighlightConfigurator> {
    }
}

