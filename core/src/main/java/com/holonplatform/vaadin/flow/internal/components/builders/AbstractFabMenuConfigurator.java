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

import com.holonplatform.vaadin.flow.components.builders.FabMenuConfigurator;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.vaadinplus.components.Fab;
import com.holonplatform.vaadin.flow.vaadinplus.components.FabMenu;
import com.holonplatform.vaadin.flow.vaadinplus.components.FabMenuItem;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

/**
 * Base {@link FabMenuConfigurator} implementation.
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractFabMenuConfigurator<C extends FabMenuConfigurator<C>>
        extends AbstractComponentConfigurator<FabMenu, C> implements FabMenuConfigurator<C> {

    public AbstractFabMenuConfigurator(FabMenu component) {
        super(component);
    }

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
        return Optional.empty();
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }

    @Override
    public C color(Fab.Color color) {
        getComponent().setTriggerColor(color);
        return getConfigurator();
    }

    @Override
    public C size(Fab.Size size) {
        getComponent().setTriggerSize(size);
        return getConfigurator();
    }

    @Override
    public C position(Fab.Position position) {
        getComponent().setFabMenuPosition(position);
        return getConfigurator();
    }

    @Override
    public C orientation(FabMenu.Orientation orientation) {
        getComponent().setOrientation(orientation);
        return getConfigurator();
    }

    @Override
    public C labelPlacement(FabMenu.LabelPlacement labelPlacement) {
        getComponent().setLabelPlacement(labelPlacement);
        return getConfigurator();
    }

    @Override
    public C backdrop(boolean backdrop) {
        getComponent().setBackdrop(backdrop);
        return getConfigurator();
    }

    @Override
    public C icons(VaadinIcon closedIcon, VaadinIcon openIcon) {
        getComponent().setTriggerIcons(closedIcon, openIcon);
        return getConfigurator();
    }

    @Override
    public C item(VaadinIcon icon, String label, ClickEventListener<Button, ClickEvent<Button>> onClick) {
        return item(FabMenuItem.of(icon, label, onClick));
    }

    @Override
    public C item(VaadinIcon icon, String label, Fab.Color color,
            ClickEventListener<Button, ClickEvent<Button>> onClick) {
        return item(FabMenuItem.of(icon, label, color, onClick));
    }

    @Override
    public C item(FabMenuItem item) {
        getComponent().addItem(item);
        return getConfigurator();
    }
}


