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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultRibbonBuilder;
import com.iyensoft.vaadin.flow.components.Ribbon;

/**
 * Fluent builder to create and configure {@link Ribbon} components.
 *
 * <p><strong>Usage examples:</strong>
 *
 * <p>Rounded ribbon:
 * <pre>{@code
 * Ribbon card = Ribbon.builder()
 *     .variant(Ribbon.Variant.ROUNDED)
 *     .label("Popular")
 *     .content(new H3("Rounded Ribbon"), new Paragraph("..."))
 *     .build();
 * }</pre>
 *
 * <p>Filed corner ribbon with a colour:
 * <pre>{@code
 * Ribbon card = Ribbon.builder()
 *     .variant(Ribbon.Variant.FILED)
 *     .color(Ribbon.Color.SUCCESS)
 *     .label("New")
 *     .content(new Paragraph("..."))
 *     .build();
 * }</pre>
 *
 * @see RibbonConfigurator
 * @see Ribbon
 */
public interface RibbonBuilder
        extends RibbonConfigurator<RibbonBuilder>,
        ComponentBuilder<Ribbon, RibbonBuilder> {

    /**
     * Create a new {@link RibbonBuilder}.
     *
     * @return a new {@link RibbonBuilder}
     */
    static RibbonBuilder create() {
        return new DefaultRibbonBuilder();
    }

    /**
     * Create a new {@link RibbonBuilder} for the given {@link Ribbon}.
     *
     * @param ribbon the ribbon to build (not null)
     * @return a new {@link RibbonBuilder}
     */
    static RibbonBuilder create(Ribbon ribbon) {
        return new DefaultRibbonBuilder(ribbon);
    }
}
