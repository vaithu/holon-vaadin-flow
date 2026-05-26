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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultButtonGroupBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.ButtonGroup;

/**
 * Fluent builder to create and configure {@link ButtonGroup} layout components.
 *
 * <p><strong>Usage examples:</strong>
 *
 * <p>Basic horizontal group:
 * <pre>{@code
 * ButtonGroup group = ButtonGroup.builder()
 *     .add(new Button("Day"), new Button("Week"), new Button("Month"))
 *     .build();
 * }</pre>
 *
 * <p>Full-width group:
 * <pre>{@code
 * ButtonGroup group = ButtonGroup.builder()
 *     .add(new Button("Export"), new Button("Import"))
 *     .fullWidth()
 *     .build();
 * }</pre>
 *
 * <p>Vertical group with id:
 * <pre>{@code
 * ButtonGroup group = ButtonGroup.builder()
 *     .add(new Button("Profile"), new Button("Security"))
 *     .vertical()
 *     .id("settings-nav")
 *     .build();
 * }</pre>
 *
 * @see ButtonGroupConfigurator
 * @see ButtonGroup
 */
public interface ButtonGroupBuilder
        extends ButtonGroupConfigurator<ButtonGroupBuilder>,
        ComponentBuilder<ButtonGroup, ButtonGroupBuilder> {

    /**
     * Create a new {@link ButtonGroupBuilder}.
     *
     * @return a new {@link ButtonGroupBuilder}
     */
    static ButtonGroupBuilder create() {
        return new DefaultButtonGroupBuilder();
    }
}

