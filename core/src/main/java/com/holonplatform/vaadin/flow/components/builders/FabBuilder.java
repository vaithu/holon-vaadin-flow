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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFabBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Fab;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Fluent builder for {@link Fab} (Material Design 3 Floating Action Button) components.
 *
 * <p>Usage:
 * <pre>{@code
 * // Shortest — icon-only, surface color, default size
 * Fab fab = Fab.builder(VaadinIcon.PLUS)
 *     .position(Fab.Position.BOTTOM_END)
 *     .onClick(e -> createNewItem())
 *     .build();
 *
 * // Primary, large, extended with a label
 * Fab compose = Fab.builder(VaadinIcon.EDIT, Fab.Color.PRIMARY)
 *     .size(Fab.Size.LARGE)
 *     .extended("Compose")
 *     .position(Fab.Position.BOTTOM_END)
 *     .build();
 * }</pre>
 *
 * @see FabConfigurator
 * @see Fab
 */
public interface FabBuilder extends FabConfigurator<FabBuilder>, ComponentBuilder<Button, FabBuilder> {

    /**
     * Builds and returns the configured {@link Fab} instance.
     *
     * @return the built {@link Fab}
     */
    @Override
    Fab build();

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /**
     * Create a {@link FabBuilder} for a default surface FAB with a {@link VaadinIcon#PLUS} icon.
     *
     * @return a new {@link FabBuilder}
     */
    static FabBuilder create() {
        return new DefaultFabBuilder();
    }

    /**
     * Create a {@link FabBuilder} pre-configured with the given icon.
     *
     * @param icon the icon to display (not null)
     * @return a new {@link FabBuilder}
     */
    static FabBuilder create(VaadinIcon icon) {
        return new DefaultFabBuilder(icon);
    }

    /**
     * Create a {@link FabBuilder} pre-configured with icon and color.
     *
     * @param icon  the icon to display (not null)
     * @param color the color scheme ({@code null} = {@link Fab.Color#SURFACE})
     * @return a new {@link FabBuilder}
     */
    static FabBuilder create(VaadinIcon icon, Fab.Color color) {
        return new DefaultFabBuilder(icon, color);
    }
}



