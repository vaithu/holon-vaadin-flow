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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultSeparatorBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Separator;

/**
 * Fluent builder to create and configure {@link Separator} components.
 *
 * <p><strong>Usage examples:</strong></p>
 *
 * <p>Default horizontal separator:</p>
 * <pre>{@code
 * Separator sep = Separator.builder().build();
 * }</pre>
 *
 * <p>Vertical separator with custom id:</p>
 * <pre>{@code
 * Separator sep = Separator.builder()
 *     .orientation(Separator.Orientation.VERTICAL)
 *     .id("my-divider")
 *     .build();
 * }</pre>
 *
 * <p>Decorative separator (hidden from assistive technologies):</p>
 * <pre>{@code
 * Separator sep = Separator.builder()
 *     .decorative(true)
 *     .styleName("section-break")
 *     .build();
 * }</pre>
 *
 * @see SeparatorConfigurator
 * @see Separator
 */
public interface SeparatorBuilder
        extends SeparatorConfigurator<SeparatorBuilder>,
        ComponentBuilder<Separator, SeparatorBuilder> {

    /**
     * Creates a new {@link SeparatorBuilder}.
     *
     * @return a new {@link SeparatorBuilder}
     */
    static SeparatorBuilder create() {
        return new DefaultSeparatorBuilder();
    }
}

