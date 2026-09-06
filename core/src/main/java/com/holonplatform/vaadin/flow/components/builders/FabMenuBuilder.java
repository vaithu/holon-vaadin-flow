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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFabMenuBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Fab;
import com.holonplatform.vaadin.flow.vaadinplus.components.FabMenu;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Fluent builder for {@link FabMenu} (Material Design 3 FAB Menu / "speed-dial") components.
 *
 * <p>Usage:
 * <pre>{@code
 * FabMenu menu = FabMenu.builder(VaadinIcon.PLUS, Fab.Color.PRIMARY)
 *     .position(Fab.Position.BOTTOM_END)
 *     .item(VaadinIcon.EDIT, "Compose", e -> compose())
 *     .item(VaadinIcon.CAMERA, "Photo", e -> takePhoto())
 *     .item(VaadinIcon.UPLOAD, "Attach", e -> attach())
 *     .build();
 * }</pre>
 *
 * @see FabMenuConfigurator
 * @see FabMenu
 */
public interface FabMenuBuilder extends FabMenuConfigurator<FabMenuBuilder>, ComponentBuilder<FabMenu, FabMenuBuilder> {

    // -----------------------------------------------------------------------
    // Static factories
    // -----------------------------------------------------------------------

    /**
     * Create a {@link FabMenuBuilder} with a {@link VaadinIcon#PLUS} primary trigger.
     *
     * @return a new {@link FabMenuBuilder}
     */
    static FabMenuBuilder create() {
        return new DefaultFabMenuBuilder();
    }

    /**
     * Create a {@link FabMenuBuilder} pre-configured with the given trigger icon.
     *
     * @param icon the trigger's closed-state icon (not null)
     * @return a new {@link FabMenuBuilder}
     */
    static FabMenuBuilder create(VaadinIcon icon) {
        return new DefaultFabMenuBuilder(icon);
    }

    /**
     * Create a {@link FabMenuBuilder} pre-configured with trigger icon and color.
     *
     * @param icon  the trigger's closed-state icon (not null)
     * @param color the trigger (and default item) color scheme
     * @return a new {@link FabMenuBuilder}
     */
    static FabMenuBuilder create(VaadinIcon icon, Fab.Color color) {
        return new DefaultFabMenuBuilder(icon, color);
    }
}


