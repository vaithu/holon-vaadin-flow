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

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.ItemLineEditorConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.ItemLineEditor;
import com.holonplatform.vaadin.flow.vaadinplus.components.ItemLineEditor.Column;
import com.holonplatform.vaadin.flow.vaadinplus.components.TotalsCard;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base {@link ItemLineEditorConfigurator} implementation.
 *
 * <p>Extends {@link AbstractComponentConfigurator} to inherit the standard Holon Platform
 * component lifecycle hooks ({@code id}, {@code visible}, {@code styleName}, {@code width},
 * etc.) and provides ItemLineEditor-specific configuration logic by delegating to the wrapped
 * {@link ItemLineEditor} component's mutators.</p>
 *
 * @param <T> the row bean type
 * @param <C> Concrete configurator type
 */
public abstract class AbstractItemLineEditorConfigurator<T, C extends ItemLineEditorConfigurator<T, C>>
        extends AbstractComponentConfigurator<ItemLineEditor<T>, C>
        implements ItemLineEditorConfigurator<T, C> {

    /**
     * Constructor.
     *
     * @param component the {@link ItemLineEditor} component instance to configure (not null)
     */
    public AbstractItemLineEditorConfigurator(ItemLineEditor<T> component) {
        super(component);
    }

    // -----------------------------------------------------------------------
    // ItemLineEditorConfigurator implementation
    // -----------------------------------------------------------------------

    @Override
    public C title(String title) {
        getComponent().setTitle(title);
        return getConfigurator();
    }

    @Override
    public C title(Localizable title) {
        getComponent().setTitle(title);
        return getConfigurator();
    }

    @Override
    public C rowFactory(Supplier<T> rowFactory) {
        getComponent().setRowFactory(rowFactory);
        return getConfigurator();
    }

    @Override
    public C addColumn(Column<T> column) {
        getComponent().addColumn(column);
        return getConfigurator();
    }

    @Override
    public C addColumn(String key, String header, Function<T, Component> renderer) {
        getComponent().addColumn(key, header, renderer);
        return getConfigurator();
    }

    @Override
    public C initialRows(int count) {
        getComponent().addInitialRows(count);
        return getConfigurator();
    }

    @Override
    public C maxRows(int maxRows) {
        getComponent().setMaxRows(maxRows);
        return getConfigurator();
    }

    @Override
    public C footer(Function<List<T>, TotalsCard> footer) {
        getComponent().setFooter(footer);
        return getConfigurator();
    }

    @Override
    public C emptyState(String title, String description) {
        getComponent().setEmptyState(title, description);
        return getConfigurator();
    }

    @Override
    public C emptyIcon(VaadinIcon icon) {
        getComponent().setEmptyIcon(icon);
        return getConfigurator();
    }

    @Override
    public C addButtonText(String text) {
        getComponent().setAddButtonText(text);
        return getConfigurator();
    }

    @Override
    public C onChange(Consumer<List<T>> listener) {
        getComponent().setOnChangeListener(listener);
        return getConfigurator();
    }

    @Override
    public C mobileCard(Function<T, String> title, Function<T, String> subtitle, Function<T, String> value) {
        getComponent().setMobileCard(title, subtitle, value);
        return getConfigurator();
    }

    @Override
    public C bulkAddEnabled(boolean enabled) {
        getComponent().setBulkAddEnabled(enabled);
        return getConfigurator();
    }

    @Override
    public C addButtonVisible(boolean visible) {
        getComponent().setAddButtonVisible(visible);
        return getConfigurator();
    }

    @Override
    public <S> C itemPicker(String buttonText, List<S> catalog, Function<S, String> primaryLabel,
            Function<S, String> secondaryLabel, BiFunction<S, Integer, T> rowMapper) {
        getComponent().setItemPicker(buttonText, catalog, primaryLabel, secondaryLabel, rowMapper);
        return getConfigurator();
    }

    // -----------------------------------------------------------------------
    // AbstractComponentConfigurator hooks
    // -----------------------------------------------------------------------

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }
}

