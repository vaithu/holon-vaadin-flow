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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFabConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Fab;

/**
 * {@link Fab} (Material Design 3 Floating Action Button) component configurator.
 * <p>
 * Extends {@link ButtonConfigurator} so all the usual button configuration (icon, text,
 * click listeners, tooltip, theme variants, focus shortcuts, ...) is available, adding the
 * FAB-specific size / color / extended / lowered / fixed-position API on top.
 * </p>
 *
 * @param <C> Concrete configurator type
 *
 * @see Fab
 * @see FabBuilder
 */
public interface FabConfigurator<C extends FabConfigurator<C>> extends ButtonConfigurator<C> {

    /**
     * Sets the FAB size preset.
     *
     * @param size the size (not null)
     * @return this configurator
     */
    C size(Fab.Size size);

    /**
     * Sets the FAB color scheme.
     *
     * @param color the color scheme ({@code null} = {@link Fab.Color#SURFACE})
     * @return this configurator
     */
    C color(Fab.Color color);

    /**
     * Extends the FAB, setting the given label text and revealing it next to the icon.
     *
     * @param label the label text to display (not null / not blank)
     * @return this configurator
     */
    C extended(String label);

    /**
     * Extends or collapses the FAB.
     *
     * @param extended {@code true} to switch to the extended pill shape
     * @return this configurator
     */
    C extended(boolean extended);

    /**
     * Extends the FAB (equivalent to {@code extended(true)}).
     *
     * @return this configurator
     */
    default C extended() {
        return extended(true);
    }

    /**
     * Applies (or clears) the M3 "lowered" reduced-elevation variant.
     *
     * @param lowered {@code true} to reduce elevation
     * @return this configurator
     */
    C lowered(boolean lowered);

    /**
     * Applies the M3 "lowered" reduced-elevation variant (equivalent to {@code lowered(true)}).
     *
     * @return this configurator
     */
    default C lowered() {
        return lowered(true);
    }

    /**
     * Docks the FAB at a fixed on-screen position.
     *
     * @param position the position (not null; {@link Fab.Position#NONE} = normal document flow)
     * @return this configurator
     */
    C position(Fab.Position position);

    // -----------------------------------------------------------------------
    // Configure factory
    // -----------------------------------------------------------------------

    /**
     * Get a {@link BaseFabConfigurator} to configure an existing {@link Fab}.
     *
     * @param fab the FAB to configure (not null)
     * @return a new {@link BaseFabConfigurator}
     */
    static BaseFabConfigurator configure(Fab fab) {
        return new DefaultFabConfigurator(fab);
    }

    /**
     * Base (non-generic) {@link FabConfigurator}.
     */
    interface BaseFabConfigurator extends FabConfigurator<BaseFabConfigurator> {
    }
}

