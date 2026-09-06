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

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultItemLineEditorBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.ItemLineEditor;

/**
 * Builder to create and configure {@link ItemLineEditor} components.
 *
 * <p>Extends {@link ItemLineEditorConfigurator} for all ItemLineEditor-specific configuration
 * methods and {@link ComponentBuilder} for the terminal {@link #build()} method, which validates
 * that a row factory and at least one column have been configured.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * ItemLineEditor<InvoiceLine> editor = ItemLineEditor.builder(InvoiceLine.class)
 *     .title("Invoice Items")
 *     .rowFactory(InvoiceLine::new)
 *     .addColumn(Column.of("description", "Description", line -> descriptionField(line)).flexGrow(2))
 *     .addColumn(Column.of("qty", "Qty", line -> qtyField(line)).width("90px"))
 *     .footer(lines -> TotalsCard.builder()
 *         .row("Total", format(total(lines)), TotalsRow.Variant.GRAND_TOTAL)
 *         .build())
 *     .build();
 * }</pre>
 *
 * <p>Instances are obtained via {@link #create(Class)}, the convenience shortcut
 * {@link ItemLineEditor#builder(Class)}, or {@code Components.itemLineEditor(Class)}.</p>
 *
 * @param <T> the row bean type
 * @see ItemLineEditorConfigurator
 * @see ItemLineEditor
 */
public interface ItemLineEditorBuilder<T> extends ItemLineEditorConfigurator<T, ItemLineEditorBuilder<T>>,
        ComponentBuilder<ItemLineEditor<T>, ItemLineEditorBuilder<T>> {

    /**
     * Create a new {@link ItemLineEditorBuilder} for the given row bean type.
     *
     * @param <T> the row bean type
     * @param itemType the row bean class (not null)
     * @return a new {@link ItemLineEditorBuilder}
     */
    static <T> ItemLineEditorBuilder<T> create(Class<T> itemType) {
        return new DefaultItemLineEditorBuilder<>(itemType);
    }
}

