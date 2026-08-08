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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;

import java.util.function.Consumer;

/**
 * {@link MenuBar} component configurator.
 * <p>
 * The builder preserves the natural <code>MenuBar &rarr; MenuItem &rarr; SubMenu</code> hierarchy: each
 * {@link #withMenuItem(Localizable)} call returns a dedicated {@link MenuItemBuilder} bound to the created item, and
 * {@link MenuItemBuilder#withSubMenu(Consumer)} opens a dedicated {@link SubMenuBuilder} for that item. Because every level is a
 * distinct builder instance, multiple items each with their own (possibly nested) sub-menus can be built without any
 * shared mutable state.
 * </p>
 *
 * @param <C> Concrete configurator type
 * @since 5.5.6
 */
public interface MenuBarConfigurator<C extends MenuBarConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasThemeVariantConfigurator<MenuBarVariant, C> {

    /**
     * Click event listener type used for menu items.
     */
    interface MenuItemClickListener extends ClickEventListener<MenuItem, ClickEvent<MenuItem>> {
    }

    /**
     * Create a new menu item with the given localizable text content.
     *
     * @param text Localizable menu item text content
     * @return A {@link MenuItemBuilder} to configure and add the menu item
     * @see LocalizationProvider
     */
    MenuItemBuilder<C> withMenuItem(Localizable text);

    /**
     * Create a new menu item with the given text content.
     *
     * @param text Menu item text content
     * @return A {@link MenuItemBuilder} to configure and add the menu item
     */
    MenuItemBuilder<C> withMenuItem(String text);

    /**
     * Create a new menu item using given <code>messageCode</code> for text content localization.
     *
     * @param defaultText Default text content if no translation is available for given <code>messageCode</code>
     * @param messageCode Menu item text content translation message key
     * @param arguments   Optional translation arguments
     * @return A {@link MenuItemBuilder} to configure and add the menu item
     * @see LocalizationProvider
     */
    MenuItemBuilder<C> withMenuItem(String defaultText, String messageCode, Object... arguments);

    /**
     * Create a new menu item with the given component inside.
     *
     * @param component The menu item component (not null)
     * @return A {@link MenuItemBuilder} to configure and add the menu item
     */
    MenuItemBuilder<C> withMenuItem(Component component);

    /**
     * Create a new menu item with the given {@link HasComponent} component inside.
     *
     * @param component The menu item component (not null)
     * @return A {@link MenuItemBuilder} to configure and add the menu item
     */
    MenuItemBuilder<C> withMenuItem(HasComponent component);

    /**
     * Create a new menu item with the given component inside and the given tooltip text.
     *
     * @param component The menu item component (not null)
     * @param toolTip   The tooltip text
     * @return A {@link MenuItemBuilder} to configure and add the menu item
     */
    MenuItemBuilder<C> withMenuItem(Component component, String toolTip);

    /**
     * Create a new menu item with the given {@link HasComponent} component inside and the given tooltip text.
     *
     * @param component The menu item component (not null)
     * @param toolTip   The tooltip text
     * @return A {@link MenuItemBuilder} to configure and add the menu item
     */
    MenuItemBuilder<C> withMenuItem(HasComponent component, String toolTip);

    /**
     * Add a new menu item using given localizable text content and a click listener.
     *
     * @param text               Menu item text content
     * @param clickEventListener The click listener (not null)
     * @return this
     */
    C withMenuItem(Localizable text, MenuItemClickListener clickEventListener);

    /**
     * Add a new menu item using given text content and a click listener.
     *
     * @param text               Menu item text content
     * @param clickEventListener The click listener (not null)
     * @return this
     */
    C withMenuItem(String text, MenuItemClickListener clickEventListener);

    /**
     * Add a new menu item using given <code>messageCode</code> for text localization and a click listener.
     *
     * @param defaultText        Default text content if no translation is available for given <code>messageCode</code>
     * @param messageCode        Menu item text content translation message key
     * @param clickEventListener The click listener (not null)
     * @return this
     */
    C withMenuItem(String defaultText, String messageCode, MenuItemClickListener clickEventListener);

    /**
     * Add a new menu item with the given component inside and a click listener.
     *
     * @param component          The menu item component (not null)
     * @param clickEventListener The click listener (not null)
     * @return this
     */
    C withMenuItem(Component component, MenuItemClickListener clickEventListener);

    /**
     * Add a new menu item with the given {@link HasComponent} component inside and a click listener.
     *
     * @param component          The menu item component (not null)
     * @param clickEventListener The click listener (not null)
     * @return this
     */
    C withMenuItem(HasComponent component, MenuItemClickListener clickEventListener);

    /**
     * Add a new menu item with the given component inside, the given tooltip text and a click listener.
     *
     * @param component          The menu item component (not null)
     * @param tooltipText        The tooltip text
     * @param clickEventListener The click listener (not null)
     * @return this
     */
    C withMenuItem(Component component, String tooltipText, MenuItemClickListener clickEventListener);

    /**
     * Add a new menu item that displays the given icon followed by the given text, with a click listener.
     * <p>
     * The icon is the primary component of the menu item; the text label is appended directly after it, matching the
     * canonical Vaadin pattern for icon-and-label menu items.
     * </p>
     *
     * @param icon               The menu item icon (not null)
     * @param text               Menu item text content
     * @param clickEventListener The click listener (not null)
     * @return this
     */
    C withMenuItem(Icon icon, String text, MenuItemClickListener clickEventListener);

    /**
     * Add a new menu item that displays the given icon followed by the given localizable text, with a click listener.
     * <p>
     * The icon is the primary component of the menu item; the text label is appended directly after it, matching the
     * canonical Vaadin pattern for icon-and-label menu items.
     * </p>
     *
     * @param icon               The menu item icon (not null)
     * @param text               Localizable menu item text content
     * @param clickEventListener The click listener (not null)
     * @return this
     * @see com.holonplatform.vaadin.flow.i18n.LocalizationProvider
     */
    C withMenuItem(Icon icon, Localizable text, MenuItemClickListener clickEventListener);

    /**
     * Sets the event which opens the sub menus of the root level buttons.
     *
     * @param openOnHover <code>true</code> to make the sub menus open on hover, <code>false</code> to open on click
     * @return this
     */
    C openOnHover(boolean openOnHover);

    /**
     * Sets reverse collapse order for the menu bar.
     *
     * @param reverseCollapseOrder if <code>true</code>, the buttons collapse into the overflow menu from the start end
     * @return this
     */
    C reverseCollapseOrder(boolean reverseCollapseOrder);

    /**
     * Sets tab navigation for the menu bar.
     *
     * @param tabNavigation if <code>true</code>, the top-level items are traversable by tab instead of arrow keys
     * @return this
     */
    C tabNavigation(boolean tabNavigation);

    /**
     * Builder to configure a single {@link MenuItem} and, optionally, open its {@link SubMenu}.
     *
     * @param <P> Parent builder type returned by {@link #add()}
     * @since 5.5.6
     */
    interface MenuItemBuilder<P> extends HasEnabledConfigurator<MenuItemBuilder<P>>,
            HasStyleConfigurator<MenuItemBuilder<P>>, HasTextConfigurator<MenuItemBuilder<P>> {

        /**
         * Sets the id of the root element of the menu item.
         *
         * @param id the id to set
         * @return this
         */
        MenuItemBuilder<P> id(String id);

        /**
         * Set whether the menu item is checkable. A checkable item toggles a checkmark icon when clicked.
         *
         * @param checkable Whether the menu item is checkable
         * @return this
         */
        MenuItemBuilder<P> checkable(boolean checkable);

        /**
         * Set the menu item as checkable.
         *
         * @return this
         */
        MenuItemBuilder<P> checkable();

        /**
         * Set whether a checkable menu item is checked.
         *
         * @param checked Whether the menu item is checked
         * @return this
         */
        MenuItemBuilder<P> checked(boolean checked);

        /**
         * Sets the keep open state of this menu item. A kept-open item prevents the menu from closing when clicked.
         *
         * @param keepOpen whether clicking this item keeps the menu open
         * @return this
         */
        MenuItemBuilder<P> keepOpen(boolean keepOpen);

        /**
         * Register a menu item click event listener.
         *
         * @param menuItemClickListener The listener (not null)
         * @return this
         */
        MenuItemBuilder<P> withClickListener(MenuItemClickListener menuItemClickListener);

        /**
         * Register a menu item click event listener. Alias for {@link #withClickListener(MenuItemClickListener)}.
         *
         * @param menuItemClickListener The listener (not null)
         * @return this
         */
        MenuItemBuilder<P> onClick(MenuItemClickListener menuItemClickListener);

        /**
         * Highlight this menu item using the primary theme colors.
         *
         * @return this
         */
        MenuItemBuilder<P> highlight();

        /**
         * Open the {@link SubMenu} of this menu item and configure it via a consumer.
         * <p>
         * The consumer receives the submenu builder, so nested menu chains keep a natural indentation.
         * </p>
         *
         * @param config submenu configurator consumer (not null)
         * @return the parent builder
         */
        P withSubMenu(Consumer<SubMenuBuilder<MenuItemBuilder<P>>> config);

        /**
         * Add this menu item to its parent and return the parent builder.
         *
         * @return The parent builder
         */
        P add();

    }

    /**
     * Builder to add items to a {@link SubMenu}. Child items may themselves open nested sub-menus, allowing an
     * arbitrarily deep and repeatable <code>MenuItem &rarr; SubMenu</code> hierarchy.
     *
     * @param <P> Parent builder type returned by {@link #add()}
     * @since 5.5.6
     */
    interface SubMenuBuilder<P> {

        /**
         * Add a child item with the given localizable text content to this sub-menu.
         *
         * @param text Localizable item text content
         * @return A {@link MenuItemBuilder} bound to the child item; its {@link MenuItemBuilder#add()} returns to this
         *         sub-menu builder
         */
        MenuItemBuilder<SubMenuBuilder<P>> withMenuItem(Localizable text);

        /**
         * Add a child item with the given text content to this sub-menu.
         *
         * @param text Item text content
         * @return A {@link MenuItemBuilder} bound to the child item
         */
        MenuItemBuilder<SubMenuBuilder<P>> withMenuItem(String text);

        /**
         * Add a child item with the given component inside to this sub-menu.
         *
         * @param component The item component (not null)
         * @return A {@link MenuItemBuilder} bound to the child item
         */
        MenuItemBuilder<SubMenuBuilder<P>> withMenuItem(Component component);

        /**
         * Add a child item with the given {@link HasComponent} component inside to this sub-menu.
         *
         * @param component The item component (not null)
         * @return A {@link MenuItemBuilder} bound to the child item
         */
        MenuItemBuilder<SubMenuBuilder<P>> withMenuItem(HasComponent component);

        /**
         * Add a child item with the given text content and click listener to this sub-menu.
         *
         * @param text               Item text content
         * @param clickEventListener The click listener (not null)
         * @return this
         */
        SubMenuBuilder<P> withMenuItem(String text, MenuItemClickListener clickEventListener);

        /**
         * Add a child item with the given localizable text content and click listener to this sub-menu.
         *
         * @param text               Localizable item text content
         * @param clickEventListener The click listener (not null)
         * @return this
         */
        SubMenuBuilder<P> withMenuItem(Localizable text, MenuItemClickListener clickEventListener);

        /**
         * Add a child item with the given component inside and click listener to this sub-menu.
         *
         * @param component          The item component (not null)
         * @param clickEventListener The click listener (not null)
         * @return this
         */
        SubMenuBuilder<P> withMenuItem(Component component, MenuItemClickListener clickEventListener);

        /**
         * Add a child item that displays the given icon followed by the given text, with a click listener, to this
         * sub-menu.
         * <p>
         * The icon is the primary component of the item; the text label is appended directly after it, matching the
         * canonical Vaadin pattern for icon-and-label menu items. The icon is automatically sized for sub-menu use.
         * </p>
         *
         * @param icon               The menu item icon (not null)
         * @param text               Item text content
         * @param clickEventListener The click listener (not null)
         * @return this
         */
        SubMenuBuilder<P> withMenuItem(Icon icon, String text, MenuItemClickListener clickEventListener);

        /**
         * Add a child item that displays the given icon followed by the given localizable text, with a click listener,
         * to this sub-menu.
         * <p>
         * The icon is the primary component of the item; the text label is appended directly after it, matching the
         * canonical Vaadin pattern for icon-and-label menu items. The icon is automatically sized for sub-menu use.
         * </p>
         *
         * @param icon               The menu item icon (not null)
         * @param text               Localizable item text content
         * @param clickEventListener The click listener (not null)
         * @return this
         * @see com.holonplatform.vaadin.flow.i18n.LocalizationProvider
         */
        SubMenuBuilder<P> withMenuItem(Icon icon, Localizable text, MenuItemClickListener clickEventListener);

        /**
         * Add a separator between items.
         *
         * @return this
         */
        SubMenuBuilder<P> separator();

        /**
         * Close this sub-menu and return to the parent builder.
         *
         * @return The parent builder
         */
        P add();

    }

}
