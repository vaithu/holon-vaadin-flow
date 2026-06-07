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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultInputGroupBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.InputGroup;

/**
 * Fluent builder to create and configure {@link InputGroup} layout components.
 *
 * <p><strong>Usage examples:</strong>
 *
 * <p>Text prefix + text field:
 * <pre>{@code
 * InputGroup group = InputGroup.builder()
 *     .content(new InputGroupText("@"))
 *     .content(new TextField())
 *     .build();
 * }</pre>
 *
 * <p>Search field + button:
 * <pre>{@code
 * InputGroup group = InputGroup.builder()
 *     .content(new TextField(), new Button("Search"))
 *     .build();
 * }</pre>
 *
 * <p>Price input with Holon Input:
 * <pre>{@code
 * Input<BigDecimal> price = Components.input.bigDecimal().build();
 *
 * InputGroup group = InputGroup.builder()
 *     .content(new InputGroupText("$"))
 *     .content(price)
 *     .content(new InputGroupText(".00"))
 *     .build();
 * }</pre>
 *
 * <p>Responsive group:
 * <pre>{@code
 * InputGroup group = InputGroup.builder()
 *     .content(searchField, searchButton)
 *     .responsive()
 *     .build();
 * }</pre>
 *
 * @see InputGroupLayoutConfigurator
 * @see InputGroup
 */
public interface InputGroupBuilder
        extends InputGroupLayoutConfigurator<InputGroupBuilder>,
        ComponentBuilder<InputGroup, InputGroupBuilder> {

    /**
     * Create a new {@link InputGroupBuilder}.
     *
     * @return a new {@link InputGroupBuilder}
     */
    static InputGroupBuilder create() {
        return new DefaultInputGroupBuilder();
    }
}

