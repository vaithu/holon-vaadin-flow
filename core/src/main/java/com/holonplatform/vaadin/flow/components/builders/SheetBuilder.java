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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultSheetBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;

/**
 * Builder to create and configure {@link Sheet} slide-in panel components.
 *
 * <p>Usage:</p>
 * <pre>{@code
 * Sheet sheet = Sheet.builder(Sheet.Side.BOTTOM)
 *     .title("Filter options")
 *     .description("Narrow down your results.")
 *     .content(myFilterForm)
 *     .closeOnBackdropClick(true)
 *     .onClose(() -> applyFilters())
 *     .build();
 * sheet.open();
 * }</pre>
 *
 * <p>Or use the {@link #open()} shortcut:</p>
 * <pre>{@code
 * Sheet.builder().title("Settings").content(settingsForm).open();
 * }</pre>
 *
 * @see SheetConfigurator
 * @see Sheet
 */
public interface SheetBuilder extends SheetConfigurator<SheetBuilder>, ComponentBuilder<Sheet, SheetBuilder> {

    /**
     * Builds the {@link Sheet} and immediately opens it in the current UI.
     *
     * @return the opened {@link Sheet} instance
     */
    default Sheet open() {
        Sheet sheet = build();
        sheet.open();
        return sheet;
    }

    /**
     * Create a new {@link SheetBuilder} for a {@link Sheet.Side#BOTTOM} sheet.
     *
     * @return a new {@link SheetBuilder}
     */
    static SheetBuilder create() {
        return new DefaultSheetBuilder(Sheet.Side.BOTTOM);
    }

    /**
     * Create a new {@link SheetBuilder} for the given side.
     *
     * @param side the slide direction (not null)
     * @return a new {@link SheetBuilder}
     */
    static SheetBuilder create(Sheet.Side side) {
        return new DefaultSheetBuilder(side);
    }
}

