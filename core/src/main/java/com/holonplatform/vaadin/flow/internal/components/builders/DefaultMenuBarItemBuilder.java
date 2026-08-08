/*
 * Copyright 2016-2018 Axioma srl.
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
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.MenuBarConfigurator.MenuItemBuilder;
import com.holonplatform.vaadin.flow.components.builders.MenuBarConfigurator.MenuItemClickListener;
import com.holonplatform.vaadin.flow.components.builders.MenuBarConfigurator.SubMenuBuilder;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.contextmenu.MenuItem;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Default {@link MenuItemBuilder} implementation.
 *
 * @param <P> Parent builder type
 * @since 5.5.6
 */
public class DefaultMenuBarItemBuilder<P> implements MenuItemBuilder<P> {

    private final P parent;
    private final MenuItem menuItem;
    private final Function<com.vaadin.flow.component.ClickEvent<MenuItem>, ClickEvent<MenuItem>> clickEventConverter;

    public DefaultMenuBarItemBuilder(P parent, MenuItem menuItem,
            Function<com.vaadin.flow.component.ClickEvent<MenuItem>, ClickEvent<MenuItem>> clickEventConverter) {
        super();
        ObjectUtils.argumentNotNull(parent, "Parent builder must be not null");
        ObjectUtils.argumentNotNull(menuItem, "Menu item must be not null");
        ObjectUtils.argumentNotNull(clickEventConverter, "Click event converter must be not null");
        this.parent = parent;
        this.menuItem = menuItem;
        this.clickEventConverter = clickEventConverter;
    }

    @Override
    public MenuItemBuilder<P> id(String id) {
        menuItem.setId(id);
        return this;
    }

    @Override
    public MenuItemBuilder<P> checkable(boolean checkable) {
        menuItem.setCheckable(checkable);
        return this;
    }

    @Override
    public MenuItemBuilder<P> checkable() {
        return checkable(true);
    }

    @Override
    public MenuItemBuilder<P> checked(boolean checked) {
        if (checked && !menuItem.isCheckable()) {
            menuItem.setCheckable(true);
        }
        menuItem.setChecked(checked);
        return this;
    }

    @Override
    public MenuItemBuilder<P> keepOpen(boolean keepOpen) {
        menuItem.setKeepOpen(keepOpen);
        return this;
    }

    @Override
    public MenuItemBuilder<P> withClickListener(MenuItemClickListener menuItemClickListener) {
        ObjectUtils.argumentNotNull(menuItemClickListener, "Click listener must be not null");
        menuItem.addClickListener(e -> menuItemClickListener.onClickEvent(clickEventConverter.apply(e)));
        return this;
    }

    @Override
    public MenuItemBuilder<P> onClick(MenuItemClickListener menuItemClickListener) {
        return withClickListener(menuItemClickListener);
    }

    @Override
    public MenuItemBuilder<P> highlight() {
        return styleNames("color-bg-primary", "color-text-primary-contrast");
    }

    @Override
    public MenuItemBuilder<P> enabled(boolean enabled) {
        menuItem.setEnabled(enabled);
        return this;
    }

    @Override
    public MenuItemBuilder<P> styleNames(String... styleNames) {
        menuItem.addClassNames(styleNames);
        return this;
    }

    @Override
    public MenuItemBuilder<P> styleName(String styleName) {
        menuItem.addClassName(styleName);
        return this;
    }

    @Override
    public MenuItemBuilder<P> text(Localizable text) {
        menuItem.setText(LocalizationProvider.localize(text).orElse(""));
        return this;
    }

    @Override
    public P withSubMenu(Consumer<SubMenuBuilder<MenuItemBuilder<P>>> config) {
        Objects.requireNonNull(config, "config");
        config.accept(new DefaultSubMenuBuilder<>(this, menuItem.getSubMenu(), clickEventConverter));
        return parent;
    }

    @Override
    public P add() {
        return parent;
    }

}
