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
import com.holonplatform.vaadin.flow.components.builders.MenuBarConfigurator;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;
import java.util.function.Function;

/**
 * Abstract {@link MenuBarConfigurator}.
 *
 * @param <C> Concrete configurator type
 * @since 5.5.6
 */
public abstract class AbstractMenuBarConfigurator<C extends MenuBarConfigurator<C>>
        extends AbstractComponentConfigurator<MenuBar, C> implements MenuBarConfigurator<C> {

    private final MenuBar instance;
    private final Function<com.vaadin.flow.component.ClickEvent<MenuItem>, ClickEvent<MenuItem>> clickEventConverter;

    public AbstractMenuBarConfigurator(MenuBar instance,
            Function<com.vaadin.flow.component.ClickEvent<MenuItem>, ClickEvent<MenuItem>> clickEventConverter) {
        super(instance);
        this.instance = instance;
        this.clickEventConverter = clickEventConverter;
    }

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.empty();
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

    /**
     * Get the menu bar instance.
     *
     * @return the menu bar instance
     */
    protected MenuBar getInstance() {
        return instance;
    }

    @Override
    public MenuItemBuilder<C> withMenuItem(Localizable text) {
        ObjectUtils.argumentNotNull(text, "Text must be not null");
        return new DefaultMenuBarItemBuilder<>(getConfigurator(),
                instance.addItem(LocalizationProvider.localize(text).orElse("")), clickEventConverter);
    }

    @Override
    public MenuItemBuilder<C> withMenuItem(String text) {
        return withMenuItem(Localizable.builder().message(text != null ? text : "").build());
    }

    @Override
    public MenuItemBuilder<C> withMenuItem(String defaultText, String messageCode, Object... arguments) {
        return withMenuItem(Localizable.builder().message((defaultText == null) ? "" : defaultText)
                .messageCode(messageCode).messageArguments(arguments).build());
    }

    @Override
    public MenuItemBuilder<C> withMenuItem(Component component) {
        ObjectUtils.argumentNotNull(component, "Component must be not null");
        return new DefaultMenuBarItemBuilder<>(getConfigurator(), instance.addItem(component), clickEventConverter);
    }

    @Override
    public MenuItemBuilder<C> withMenuItem(HasComponent component) {
        ObjectUtils.argumentNotNull(component, "HasComponent must be not null");
        return withMenuItem(component.getComponent());
    }

    @Override
    public MenuItemBuilder<C> withMenuItem(Component component, String toolTip) {
        ObjectUtils.argumentNotNull(component, "Component must be not null");
        MenuItem menuItem = instance.addItem(component);
        menuItem.setTooltipText(toolTip);
        return new DefaultMenuBarItemBuilder<>(getConfigurator(), menuItem, clickEventConverter);
    }

    @Override
    public MenuItemBuilder<C> withMenuItem(HasComponent component, String toolTip) {
        ObjectUtils.argumentNotNull(component, "HasComponent must be not null");
        return withMenuItem(component.getComponent(), toolTip);
    }

    @Override
    public C withMenuItem(Localizable text, MenuItemClickListener clickEventListener) {
        return withMenuItem(text).withClickListener(clickEventListener).add();
    }

    @Override
    public C withMenuItem(String text, MenuItemClickListener clickEventListener) {
        return withMenuItem(text).withClickListener(clickEventListener).add();
    }

    @Override
    public C withMenuItem(String defaultText, String messageCode, MenuItemClickListener clickEventListener) {
        return withMenuItem(defaultText, messageCode).withClickListener(clickEventListener).add();
    }

    @Override
    public C withMenuItem(Component component, MenuItemClickListener clickEventListener) {
        return withMenuItem(component).withClickListener(clickEventListener).add();
    }

    @Override
    public C withMenuItem(HasComponent component, MenuItemClickListener clickEventListener) {
        ObjectUtils.argumentNotNull(component, "HasComponent must be not null");
        return withMenuItem(component.getComponent(), clickEventListener);
    }

    @Override
    public C withMenuItem(Component component, String tooltipText, MenuItemClickListener clickEventListener) {
        return withMenuItem(component, tooltipText).withClickListener(clickEventListener).add();
    }

    @Override
    public C withMenuItem(Icon icon, String text, MenuItemClickListener clickEventListener) {
        ObjectUtils.argumentNotNull(icon, "Icon must be not null");
        ObjectUtils.argumentNotNull(clickEventListener, "Click listener must be not null");
        MenuItem item = instance.addItem(icon);
        if (text != null && !text.isEmpty()) {
            item.add(new Text(text));
        }
        item.addClickListener(e -> clickEventListener.onClickEvent(clickEventConverter.apply(e)));
        return getConfigurator();
    }

    @Override
    public C withMenuItem(Icon icon, Localizable text, MenuItemClickListener clickEventListener) {
        ObjectUtils.argumentNotNull(icon, "Icon must be not null");
        ObjectUtils.argumentNotNull(clickEventListener, "Click listener must be not null");
        MenuItem item = instance.addItem(icon);
        String resolved = (text != null) ? LocalizationProvider.localize(text).orElse(text.getMessage()) : null;
        if (resolved != null && !resolved.isEmpty()) {
            item.add(new Text(resolved));
        }
        item.addClickListener(e -> clickEventListener.onClickEvent(clickEventConverter.apply(e)));
        return getConfigurator();
    }

    @Override
    public C openOnHover(boolean openOnHover) {
        instance.setOpenOnHover(openOnHover);
        return getConfigurator();
    }

    @Override
    public C reverseCollapseOrder(boolean reverseCollapseOrder) {
        instance.setReverseCollapseOrder(reverseCollapseOrder);
        return getConfigurator();
    }

    @Override
    public C tabNavigation(boolean tabNavigation) {
        instance.getElement().setProperty("tabNavigation", tabNavigation);
        return getConfigurator();
    }

    @Override
    public C withThemeVariants(MenuBarVariant... variants) {
        instance.addThemeVariants(variants);
        return getConfigurator();
    }

}
