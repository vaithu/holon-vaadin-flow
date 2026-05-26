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
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.HighlightBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Highlight;
import com.vaadin.flow.component.Component;

/**
 * Default {@link HighlightBuilder} implementation.
 *
 * <p>Instantiates a {@link Highlight} from the given seed parameters and delegates all
 * configuration to {@link AbstractHighlightConfigurator}.
 * Returned by {@link HighlightBuilder#create(String, String)} and the convenience
 * shortcuts on {@link Highlight} and {@link com.holonplatform.vaadin.flow.components.Components}.</p>
 */
public class DefaultHighlightBuilder
        extends AbstractHighlightConfigurator<HighlightBuilder>
        implements HighlightBuilder {

    /**
     * Creates a builder seeded with an {@code heading + value} Highlight.
     *
     * @param heading the heading label text (not null)
     * @param value   the value text (not null)
     */
    public DefaultHighlightBuilder(String heading, String value) {
        super(new Highlight(heading, value));
    }

    /**
     * Creates a builder seeded with a {@code prefix + heading + value} Highlight.
     *
     * @param prefix  the prefix component (not null)
     * @param heading the heading label text (not null)
     * @param value   the value text (not null)
     */
    public DefaultHighlightBuilder(Component prefix, String heading, String value) {
        super(new Highlight(prefix, heading, value));
    }

    @Override
    protected HighlightBuilder getConfigurator() {
        return this;
    }

    @Override
    public Highlight build() {
        return getComponent();
    }
}

