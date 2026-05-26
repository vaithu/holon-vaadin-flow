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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultHighlightBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Highlight;

/**
 * Builder to create and configure {@link Highlight} KPI card components.
 *
 * <p>Extends {@link HighlightConfigurator} for all slot / layout / accessibility methods
 * and {@link ComponentBuilder} for the terminal {@link #build()} method.</p>
 *
 * <p>Usage:
 * <pre>{@code
 * Highlight card = Highlight.builder("Total Revenue", "$128,430")
 *     .valueFirst()
 *     .accentColor(Highlight.AccentColor.PURPLE)
 *     .valueFontSize(Font.Size.XXLARGE)
 *     .details(trendSpan)
 *     .suffix(IconBadge.of(VaadinIcon.DOLLAR, Alert.Variant.INFO))
 *     .ariaLabel("Total Revenue KPI card")
 *     .build();
 * }</pre>
 *
 * <p>Instances are obtained via {@link #create(String, String)}, the convenience
 * shortcuts on {@link Highlight#builder(String, String)}, or
 * {@code Components.highlight(heading, value)}.
 *
 * @see HighlightConfigurator
 * @see Highlight
 */
public interface HighlightBuilder
        extends HighlightConfigurator<HighlightBuilder>,
                ComponentBuilder<Highlight, HighlightBuilder> {

    // ── Static factories ──────────────────────────────────────────────────────

    /**
     * Creates a new {@link HighlightBuilder} with the given heading and value.
     *
     * @param heading the heading label text (not null)
     * @param value   the value text (not null)
     * @return a new {@link HighlightBuilder}
     */
    static HighlightBuilder create(String heading, String value) {
        return new DefaultHighlightBuilder(heading, value);
    }

    /**
     * Creates a new {@link HighlightBuilder} for a prefix + heading + value card.
     *
     * @param prefix  the prefix component (left slot)
     * @param heading the heading label text (not null)
     * @param value   the value text (not null)
     * @return a new {@link HighlightBuilder}
     */
    static HighlightBuilder create(com.vaadin.flow.component.Component prefix,
                                   String heading, String value) {
        return new DefaultHighlightBuilder(prefix, heading, value);
    }
}

