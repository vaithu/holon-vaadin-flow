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
import com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator;
import com.holonplatform.vaadin.flow.components.builders.MenuBarConfigurator;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Abstract {@link ContextMenuConfigurator}.
 *
 * @param <M> Concrete MenuBar component type
 * @param <I> Menu item type
 * @param <S> Sub menu type
 * @param <C> Concrete configurator type
 * @since 5.5.6
 */
public abstract class AbstractMenuBarConfigurator<M extends MenuBar, I extends MenuItem, S extends SubMenu
        , C extends MenuBarConfigurator<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, C>>
        extends AbstractComponentConfigurator<M, C>
        implements MenuBarConfigurator<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, C> {

    private final M instance;
    private final BiFunction<M, String, MenuItem> textMenuItemProvider;
    private final BiFunction<M, Component, MenuItem> componentMenuItemProvider;
    private final Function<com.vaadin.flow.component.ClickEvent<MenuItem>, ClickEvent<MenuItem>> clickEventConverter;

    public AbstractMenuBarConfigurator(M instance, BiFunction<M, String, MenuItem> textMenuItemProvider,
                                       BiFunction<M, Component, MenuItem> componentMenuItemProvider,
                                       Function<com.vaadin.flow.component.ClickEvent<MenuItem>, ClickEvent<MenuItem>> clickEventConverter) {
        super(instance);
        this.instance = instance;
        this.textMenuItemProvider = textMenuItemProvider;
        this.componentMenuItemProvider = componentMenuItemProvider;
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
     * Get the context menu instance.
     *
     * @return the context menu instance
     */
    protected M getInstance() {
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#withItem(com.holonplatform.core.i18n.
     * Localizable)
     */
    @Override
    public MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, C> withMenuItem(Localizable text) {
        ObjectUtils.argumentNotNull(text, "Text must be not null");
        return new DefaultMenuBarItemBuilder<>(getConfigurator(),
                textMenuItemProvider.apply(instance, LocalizationProvider.localize(text).orElse("")),
                clickEventConverter);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.holonplatform.vaadin.flow.components.builders.ContextMenuConfigurator#withItem(com.vaadin.flow.component.
     * Component)
     */
    @Override
    public MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, C> withMenuItem(
            Component component) {
        ObjectUtils.argumentNotNull(component, "Component must be not null");
        return new DefaultMenuBarItemBuilder<>(getConfigurator(),
                componentMenuItemProvider.apply(instance, component), clickEventConverter);
    }

    /**
     * Add a new menu item with the given {@link HasComponent} component inside and a {@link String} for
     * menu item tooltips.
     *
     * @param component The menu item component (not null)
     * @param toolTip   The tooltip to use
     * @return this
     */
    @Override
    public MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, C> withMenuItem(HasComponent component, String toolTip) {
        return withMenuItem(component.getComponent(), toolTip);
    }

    @Override
    public MenuItemBuilder<ClickEventListener<MenuItem, ClickEvent<MenuItem>>, M, I, S, C> withMenuItem(Component component, String toolTip) {
        MenuItem menuItem = componentMenuItemProvider.apply(instance, component);
        getInstance().setTooltipText(menuItem,toolTip);
        return new DefaultMenuBarItemBuilder<>(getConfigurator(),menuItem, clickEventConverter);
    }



    /**
     * Sets the event which opens the sub menus of the root level buttons.
     *
     * @param openOnHover - true to make the sub menus open on hover (mouseover), false to make them openable by clicking
     * @return this
     */
    @Override
    public C openOnHover(boolean openOnHover) {
        getInstance().setOpenOnHover(openOnHover);
        return getConfigurator();
    }

    /**
     * Sets reverse collapse order for the menu bar.
     *
     * @param reverseCollapseOrder -  If true, the buttons will be collapsed into the overflow menu starting from the "start" end of the bar instead of the "end".
     * @return this
     */
    @Override
    public C reverseCollapseOrder(boolean reverseCollapseOrder) {
        getInstance().setReverseCollapseOrder(reverseCollapseOrder);
        return getConfigurator();
    }

    /**
     * Sets tab navigation for the menu bar.
     *
     * @param tabNavigation -  If true, the top-level menu items is traversable by tab instead of arrow keys (i.e. disabling roving tabindex)
     * @return this
     */
    @Override
    public C tabNavigation(boolean tabNavigation) {
        getInstance().getElement().setProperty("tabNavigation", tabNavigation);
        return getConfigurator();
    }

    /**
     * Add given theme variants to the component.
     *
     * @param variants The theme variants to add
     * @return this
     */
    @Override
    public C withThemeVariants(MenuBarVariant... variants) {
        getInstance().addThemeVariants(variants);
        return getConfigurator();
    }




}
