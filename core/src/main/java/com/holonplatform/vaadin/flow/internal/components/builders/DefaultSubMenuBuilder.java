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
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.builders.MenuBarConfigurator.MenuItemBuilder;
import com.holonplatform.vaadin.flow.components.builders.MenuBarConfigurator.MenuItemClickListener;
import com.holonplatform.vaadin.flow.components.builders.MenuBarConfigurator.SubMenuBuilder;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.icon.Icon;

import java.util.function.Function;

/**
 * Default {@link SubMenuBuilder} implementation.
 *
 * @param <P> Parent builder type
 * @since 5.5.6
 */
public class DefaultSubMenuBuilder<P> implements SubMenuBuilder<P> {

    private final P parent;
    private final SubMenu subMenu;
    private final Function<com.vaadin.flow.component.ClickEvent<MenuItem>, ClickEvent<MenuItem>> clickEventConverter;

    public DefaultSubMenuBuilder(P parent, SubMenu subMenu,
            Function<com.vaadin.flow.component.ClickEvent<MenuItem>, ClickEvent<MenuItem>> clickEventConverter) {
        super();
        ObjectUtils.argumentNotNull(parent, "Parent builder must be not null");
        ObjectUtils.argumentNotNull(subMenu, "Sub menu must be not null");
        ObjectUtils.argumentNotNull(clickEventConverter, "Click event converter must be not null");
        this.parent = parent;
        this.subMenu = subMenu;
        this.clickEventConverter = clickEventConverter;
    }

    @Override
    public MenuItemBuilder<SubMenuBuilder<P>> withMenuItem(Localizable text) {
        ObjectUtils.argumentNotNull(text, "Text must be not null");
        return new DefaultMenuBarItemBuilder<>(this,
                subMenu.addItem(LocalizationProvider.localize(text).orElse("")), clickEventConverter);
    }

    @Override
    public MenuItemBuilder<SubMenuBuilder<P>> withMenuItem(String text) {
        return withMenuItem(Localizable.builder().message(text != null ? text : "").build());
    }

    @Override
    public MenuItemBuilder<SubMenuBuilder<P>> withMenuItem(Component component) {
        ObjectUtils.argumentNotNull(component, "Component must be not null");
        return new DefaultMenuBarItemBuilder<>(this, subMenu.addItem(component), clickEventConverter);
    }

    @Override
    public MenuItemBuilder<SubMenuBuilder<P>> withMenuItem(HasComponent component) {
        ObjectUtils.argumentNotNull(component, "HasComponent must be not null");
        return withMenuItem(component.getComponent());
    }

    @Override
    public SubMenuBuilder<P> withMenuItem(String text, MenuItemClickListener clickEventListener) {
        return withMenuItem(text).withClickListener(clickEventListener).add();
    }

    @Override
    public SubMenuBuilder<P> withMenuItem(Localizable text, MenuItemClickListener clickEventListener) {
        return withMenuItem(text).withClickListener(clickEventListener).add();
    }

    @Override
    public SubMenuBuilder<P> withMenuItem(Component component, MenuItemClickListener clickEventListener) {
        return withMenuItem(component).withClickListener(clickEventListener).add();
    }

    @Override
    public SubMenuBuilder<P> withMenuItem(Icon icon, String text, MenuItemClickListener clickEventListener) {
        ObjectUtils.argumentNotNull(icon, "Icon must be not null");
        ObjectUtils.argumentNotNull(clickEventListener, "Click listener must be not null");
        icon.getStyle().setWidth("1.25rem");
        icon.getStyle().setHeight("1.25rem");
        icon.getStyle().setMarginRight("var(--vaadin-gap-s)");
        MenuItem item = subMenu.addItem(icon);
        if (text != null && !text.isEmpty()) {
            item.add(new Text(text));
        }
        item.addClickListener(e -> clickEventListener.onClickEvent(clickEventConverter.apply(e)));
        return this;
    }

    @Override
    public SubMenuBuilder<P> withMenuItem(Icon icon, Localizable text, MenuItemClickListener clickEventListener) {
        ObjectUtils.argumentNotNull(icon, "Icon must be not null");
        ObjectUtils.argumentNotNull(clickEventListener, "Click listener must be not null");
        icon.getStyle().setWidth("1.25rem");
        icon.getStyle().setHeight("1.25rem");
        icon.getStyle().setMarginRight("var(--vaadin-gap-s)");
        MenuItem item = subMenu.addItem(icon);
        String resolved = (text != null) ? LocalizationProvider.localize(text).orElse(text.getMessage()) : null;
        if (resolved != null && !resolved.isEmpty()) {
            item.add(new Text(resolved));
        }
        item.addClickListener(e -> clickEventListener.onClickEvent(clickEventConverter.apply(e)));
        return this;
    }

    @Override
    public SubMenuBuilder<P> separator() {
        subMenu.addSeparator();
        return this;
    }

    @Override
    public P add() {
        return parent;
    }

}
