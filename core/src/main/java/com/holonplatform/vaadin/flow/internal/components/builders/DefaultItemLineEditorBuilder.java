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
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ItemLineEditorBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.ItemLineEditor;

/**
 * Default {@link ItemLineEditorBuilder} implementation.
 *
 * <p>Instantiates an {@link ItemLineEditor} for the given item type and delegates all
 * configuration to {@link AbstractItemLineEditorConfigurator}. Returned by
 * {@link ItemLineEditorBuilder#create(Class)}.</p>
 *
 * @param <T> the row bean type
 */
public class DefaultItemLineEditorBuilder<T>
        extends AbstractItemLineEditorConfigurator<T, ItemLineEditorBuilder<T>>
        implements ItemLineEditorBuilder<T> {

    /**
     * Constructor — creates a new {@link ItemLineEditor} instance for the given item type.
     *
     * @param itemType the row bean class (not null)
     */
    public DefaultItemLineEditorBuilder(Class<T> itemType) {
        super(new ItemLineEditor<>(itemType));
    }

    @Override
    protected ItemLineEditorBuilder<T> getConfigurator() {
        return this;
    }

    @Override
    public ItemLineEditor<T> build() {
        getComponent().validateConfiguration();
        return getComponent();
    }
}

