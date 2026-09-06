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

import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFabMenuConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Fab;
import com.holonplatform.vaadin.flow.vaadinplus.components.FabMenu;
import com.holonplatform.vaadin.flow.vaadinplus.components.FabMenuItem;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * {@link FabMenu} (Material Design 3 FAB Menu / "speed-dial") component configurator.
 *
 * @param <C> Concrete configurator type
 *
 * @see FabMenu
 * @see FabMenuBuilder
 */
public interface FabMenuConfigurator<C extends FabMenuConfigurator<C>> extends ComponentConfigurator<C>,
        HasSizeConfigurator<C>, HasStyleConfigurator<C>, HasEnabledConfigurator<C> {

    /**
     * Sets the trigger color scheme.
     *
     * @param color the color scheme ({@code null} = {@link Fab.Color#SURFACE})
     * @return this configurator
     */
    C color(Fab.Color color);

    /**
     * Sets the trigger size. Item FABs are always {@link Fab.Size#SMALL} per the M3 spec.
     *
     * @param size the trigger size (not null)
     * @return this configurator
     */
    C size(Fab.Size size);

    /**
     * Docks the whole menu (trigger + item stack + scrim) at a fixed position on screen.
     *
     * @param position the position (not null; {@link Fab.Position#NONE} = normal document flow)
     * @return this configurator
     */
    C position(Fab.Position position);

    /**
     * Sets the item stack orientation (vertical stack vs. horizontal row).
     *
     * @param orientation the orientation (not null; {@link FabMenu.Orientation#VERTICAL} is the default)
     * @return this configurator
     */
    C orientation(FabMenu.Orientation orientation);

    /**
     * Sets how item labels are rendered — baked into the item's pill shape
     * ({@link FabMenu.LabelPlacement#INLINE}, the default), as an independently-fading side chip
     * ({@link FabMenu.LabelPlacement#SIDE}), or hidden entirely ({@link FabMenu.LabelPlacement#NONE}).
     * Only affects items added after this call.
     *
     * @param labelPlacement the label placement mode (not null)
     * @return this configurator
     */
    C labelPlacement(FabMenu.LabelPlacement labelPlacement);

    /**
     * Enables or disables the full-screen scrim shown behind the item stack while open.
     *
     * @param backdrop {@code false} to disable the scrim entirely
     * @return this configurator
     */
    C backdrop(boolean backdrop);

    /**
     * Sets the trigger's closed-state and open-state icons.
     *
     * @param closedIcon icon shown when the menu is closed (not null)
     * @param openIcon   icon shown when the menu is open (not null)
     * @return this configurator
     */
    C icons(VaadinIcon closedIcon, VaadinIcon openIcon);

    /**
     * Adds an action item with the default (surface) color.
     *
     * @param icon    the item icon (not null)
     * @param label   the item label, shown next to the icon
     * @param onClick the action to run when the item is selected (may be null)
     * @return this configurator
     */
    C item(VaadinIcon icon, String label, ClickEventListener<Button, ClickEvent<Button>> onClick);

    /**
     * Adds a fully-specified action item.
     *
     * @param icon    the item icon (not null)
     * @param label   the item label, shown next to the icon
     * @param color   the item color ({@code null} = {@link Fab.Color#SURFACE})
     * @param onClick the action to run when the item is selected (may be null)
     * @return this configurator
     */
    C item(VaadinIcon icon, String label, Fab.Color color, ClickEventListener<Button, ClickEvent<Button>> onClick);

    /**
     * Adds a pre-built {@link FabMenuItem} descriptor.
     *
     * @param item the item to add (not null)
     * @return this configurator
     */
    C item(FabMenuItem item);

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Get a {@link BaseFabMenuConfigurator} to configure an existing {@link FabMenu}.
     *
     * @param menu the menu to configure (not null)
     * @return a new {@link BaseFabMenuConfigurator}
     */
    static BaseFabMenuConfigurator configure(FabMenu menu) {
        return new DefaultFabMenuConfigurator(menu);
    }

    /**
     * Base (non-generic) {@link FabMenuConfigurator}.
     */
    interface BaseFabMenuConfigurator extends FabMenuConfigurator<BaseFabMenuConfigurator> {
    }
}


