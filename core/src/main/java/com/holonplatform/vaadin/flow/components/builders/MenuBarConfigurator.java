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
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.EventListener;

/**
 * {@link MenuBar} component configurator.
 *
 * @param <L> Menu item click listener type
 * @param <M> Concrete Menubar component type
 * @param <I> Menu item type
 * @param <S> Sub menu type
 * @param <C> Concrete configurator type
 * @since 5.5.6
 */
public interface MenuBarConfigurator<L extends EventListener, M extends MenuBar,
        I extends MenuItem,
        S extends SubMenu,
        C extends MenuBarConfigurator<L, M, I, S, C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasThemeVariantConfigurator<MenuBarVariant, C> {

    /**
     * Create a new menu item with the given localizable text content.
     * <p>
     * The {@link MenuItemBuilder#add()} method can be used to add the item to the MenuBar.
     * </p>
     *
     * @param text Localizable menu item text content
     * @return A {@link MenuItemBuilder} to configure and add the menu item
     * @see LocalizationProvider
     */
    MenuItemBuilder<L, M, I, S, C> withMenuItem(Localizable text);

    /**
     * Create a new menu item with the given text content.
     * <p>
     * The {@link MenuItemBuilder#add()} method can be used to add the item to the MenuBar.
     * </p>
     *
     * @param text Menu item text content
     * @return A {@link MenuItemBuilder} to configure and add the menu item
     */
    default MenuItemBuilder<L, M, I, S, C> withMenuItem(String text) {
        return withMenuItem(Localizable.builder().message(text).build());
    }

    /**
     * Create a new menu item using given <code>messageCode</code> for text content localization.
     * <p>
     * The {@link MenuItemBuilder#add()} method can be used to add the item to the MenuBar.
     * </p>
     *
     * @param defaultText Default menu item text content if no translation is available for given
     *                    <code>messageCode</code>.
     * @param messageCode Menu item text content translation message key
     * @param arguments   Optional translation arguments
     * @return A {@link MenuItemBuilder} to configure and add the menu item
     * @see LocalizationProvider
     */
    default MenuItemBuilder<L, M, I, S, C> withMenuItem(String defaultText, String messageCode, Object... arguments) {
        return withMenuItem(Localizable.builder().message((defaultText == null) ? "" : defaultText).messageCode(messageCode)
                .messageArguments(arguments).build());
    }

    /**
     * Create a new menu item with the given component inside.
     * <p>
     * The {@link MenuItemBuilder#add()} method can be used to add the item to the MenuBar.
     * </p>
     *
     * @param component The menu item component (not null)
     * @return A {@link MenuItemBuilder} to configure and add the menu item
     */
    MenuItemBuilder<L, M, I, S, C> withMenuItem(Component component);

    /**
     * Create a new menu item with the given {@link HasComponent} component inside.
     * <p>
     * The {@link MenuItemBuilder#add()} method can be used to add the item to the MenuBar.
     * </p>
     *
     * @param component The menu item component (not null)
     * @return A {@link MenuItemBuilder} to configure and add the menu item
     */
    default MenuItemBuilder<L, M, I, S, C> withMenuItem(HasComponent component) {
        ObjectUtils.argumentNotNull(component, "HasComponent must be not null");
        return withMenuItem(component.getComponent());
    }

    /**
     * Add a new menu item using given localizable text content and a {@link ClickEventListener} for menu item clicks.
     *
     * @param text               Menu item text content
     * @param clickEventListener The listener to use to listen to menu item clicks (not null)
     * @return this
     * @see LocalizationProvider
     */
    default C withMenuItem(Localizable text, L clickEventListener) {
        return withMenuItem(text).withClickListener(clickEventListener).add();
    }

    /**
     * Add a new menu item using given <code>messageCode</code> for text localization and a {@link ClickEventListener}
     * for menu item clicks.
     *
     * @param defaultText        Default menu item text content if no translation is available for given
     *                           <code>messageCode</code>.
     * @param messageCode        Menu item text content translation message key
     * @param clickEventListener The listener to use to listen to menu item clicks (not null)
     * @return this
     * @see LocalizationProvider
     */
    default C withMenuItem(String defaultText, String messageCode, L clickEventListener) {
        return withMenuItem(defaultText, messageCode).withClickListener(clickEventListener).add();
    }

    /**
     * Add a new menu item using given text content and a {@link ClickEventListener} for menu item clicks.
     *
     * @param text               Menu item text content
     * @param clickEventListener The listener to use to listen to menu item clicks (not null)
     * @return this
     */
    default C withMenuItem(String text, L clickEventListener) {
        return withMenuItem(text).withClickListener(clickEventListener).add();
    }

    /**
     * Add a new menu item with the given component inside and a {@link ClickEventListener} for menu item clicks.
     *
     * @param component          The menu item component (not null)
     * @param clickEventListener The listener to use to listen to menu item clicks (not null)
     * @return this
     */
    default C withMenuItem(Component component, L clickEventListener) {
        return withMenuItem(component).withClickListener(clickEventListener).add();
    }

    /**
     * Add a new menu item with the given {@link HasComponent} component inside and a {@link ClickEventListener} for
     * menu item clicks.
     *
     * @param component          The menu item component (not null)
     * @param clickEventListener The listener to use to listen to menu item clicks (not null)
     * @return this
     */
    default C withMenuItem(HasComponent component, L clickEventListener) {
        ObjectUtils.argumentNotNull(component, "HasComponent must be not null");
        return withMenuItem(component.getComponent(), clickEventListener);
    }

    /**
     * Add a new menu item with the given {@link HasComponent} component inside and a {@link String} for
     * menu item tooltips.
     *
     * @param component The menu item component (not null)
     * @param toolTip   The tooltip to use
     * @return this
     */
    MenuItemBuilder<L, M, I, S, C> withMenuItem(HasComponent component, String toolTip);

    MenuItemBuilder<L, M, I, S, C> withMenuItem(Component component, String toolTip);

    default C withMenuItem(Component component,
                           String tooltipText,
                           L clickEventListener) {
        return withMenuItem(component, tooltipText).onClick(clickEventListener).add();
    }


    /**
     * Sets the event which opens the sub menus of the root level buttons.
     *
     * @param openOnHover - true to make the sub menus open on hover (mouseover), false to make them openable by clicking
     * @return this
     */

    C openOnHover(boolean openOnHover);

    /**
     * Sets reverse collapse order for the menu bar.
     *
     * @param reverseCollapseOrder -  If true, the buttons will be collapsed into the overflow menu starting from the "start" end of the bar instead of the "end".
     * @return this
     */
    C reverseCollapseOrder(boolean reverseCollapseOrder);


    /**
     * Sets tab navigation for the menu bar.
     *
     * @param tabNavigation -  If true, the top-level menu items is traversable by tab instead of arrow keys (i.e. disabling roving tabindex)
     * @return this
     */
    C tabNavigation(boolean tabNavigation);

    /**
     * MenuBar item configurator.
     *
     * @param <L> Click listener type
     * @param <M> Concrete MenuBar component type
     * @param <I> Menu item type
     * @param <S> Sub menu type
     * @param <B> Parent configurator type
     * @since 5.5.6
     */
    public interface MenuItemBuilder<L extends EventListener, M extends MenuBar,
            I extends MenuItem, S extends SubMenu, B extends MenuBarConfigurator<L, M, I, S, B>>
            extends HasEnabledConfigurator<MenuItemBuilder<L, M, I, S, B>>, HasStyleConfigurator<MenuItemBuilder<L, M, I, S, B>>,
            HasTextConfigurator<MenuItemBuilder<L, M, I, S, B>> {

        /**
         * Sets the id of the root element of the menu item.
         *
         * @param id the id to set
         * @return this
         */
        MenuItemBuilder<L, M, I, S, B> id(String id);

        /**
         * Set whether the menu item is checkable.
         * <p>
         * A checkable item toggles a checkmark icon when clicked.
         * </p>
         * <p>
         * Changes in the checked state can be handled in the item's click handler with <code>isChecked()</code>.
         * <p>
         *
         * @param checkable Whether the menu item is checkable
         * @return this
         * @since 5.2.3
         */
        MenuItemBuilder<L, M, I, S, B> checkable(boolean checkable);
//        MenuItemBuilder<L, M, I, S, B> toolTip(String toolTip);

        /**
         * Set the menu as checkable.
         * <p>
         * A checkable item toggles a checkmark icon when clicked.
         * </p>
         * <p>
         * Changes in the checked state can be handled in the item's click handler with <code>isChecked()</code>.
         * <p>
         *
         * @return this
         * @since 5.2.3
         */
        default MenuItemBuilder<L, M, I, S, B> checkable() {
            return checkable(true);
        }

        /**
         * Set whether a checkable menu item is checked.
         *
         * @param checked Whether the menu item is checked
         * @return this
         * @see #checkable(boolean)
         * @since 5.2.3
         */
        MenuItemBuilder<L, M, I, S, B> checked(boolean checked);

        /**
         * Register a menu item click event listener.
         *
         * @param menuItemClickListener The listener to add (not null)
         * @return this
         */
        MenuItemBuilder<L, M, I, S, B> withClickListener(L menuItemClickListener);

        /**
         * Register a menu item click event listener.
         * <p>
         * Alias for {@link #withClickListener(EventListener)}.
         * </p>
         *
         * @param menuItemClickListener The listener to add (not null)
         * @return this
         */
        default MenuItemBuilder<L, M, I, S, B> onClick(L menuItemClickListener) {
            return withClickListener(menuItemClickListener);
        }

        /**
         * Sets the keep open state of this menu item. An item that marked as keep open prevents menu from closing when clicked.
         *
         * @param keepOpen whether clicking this item keeps the menu open
         * @return this
         * @since 5.5.6
         */
        MenuItemBuilder<L, M, I, S, B> keepOpen(boolean keepOpen);

        default MenuItemBuilder<L, M, I, S, B> primary() {
            return styleNames(LumoUtility.Background.PRIMARY,
                    LumoUtility.TextColor.PRIMARY_CONTRAST);
        }

       /* SubMenuItemBuilder<L, M, I, S, B> withSubMenu(String text);

        SubMenuItemBuilder<L, M, I, S, B> withSubMenu(Component component);

        default SubMenuItemBuilder<L, M, I, S, B> withSubMenu(String text, L clickListener) {
            return withSubMenu(text).withClickListener(clickListener);
        }

        default SubMenuItemBuilder<L, M, I, S, B> withSubMenu(Component component, L clickListener) {
            return withSubMenu(component).withClickListener(clickListener);
        }*/

        /**
         * Add the menu item to the MenuBar.
         *
         * @return The parent MenuBar builder
         */
        B add();

    }

    /**
     * SubMenu item configurator.
     *
     * @param <L> Click listener type
     * @param <M> Concrete MenuBar component type
     * @param <I> Menu item type
     * @param <S> Sub menu type
     * @param <B> Parent configurator type
     * @since 5.5.6
     */
    interface SubMenuItemBuilder<L extends EventListener, M extends MenuBar,
            I extends MenuItem, S extends SubMenu, B extends MenuBarConfigurator<L, M, I, S, B>> extends MenuItemBuilder<L, M, I, S, B> {

        /**
         * Adds a new item component with the given text content and click listener to the context menu overlay.
         * <p>
         * This is a convenience method for the use case where you have a list of highlightable MenuItems inside the overlay.
         * If you want to configure the contents of the overlay without wrapping them inside MenuItems,
         * or if you just want to add some non-highlightable components between the items, use the ContextMenuBase.add(Component...) method.
         *
         * @param text
         * @return
         */

        SubMenuItemBuilder<L, M, I, S, B> withMenuItem(String text);


        default SubMenuItemBuilder<L, M, I, S, B> withMenuItem(String text, L clickListener) {
            return withMenuItem(text).withClickListener(clickListener);
        }


        /**
         * Adds a new item component with the given component and click listener to the context menu overlay.
         * <p>
         * This is a convenience method for the use case where you have a list of highlightable MenuItems inside the overlay.
         * If you want to configure the contents of the overlay without wrapping them inside MenuItems,
         * or if you just want to add some non-highlightable components between the items,
         * use the ContextMenuBase.add(Component...) method.
         *
         * @param component
         * @return
         */

        SubMenuItemBuilder<L, M, I, S, B> withMenuItem(Component component);

        default SubMenuItemBuilder<L, M, I, S, B> withMenuItem(Component component, L clickListener
        ) {
            return withMenuItem(component).withClickListener(clickListener);
        }

        /**
         * Adds a separator between items.
         *
         * @return
         */
        SubMenuItemBuilder<L, M, I, S, B> separator();
        // Additional methods for SubMenuItemBuilder can be defined here

        /**
         * Register a menu item click event listener.
         *
         * @param menuItemClickListener The listener to add (not null)
         * @return this
         */
        SubMenuItemBuilder<L, M, I, S, B> withClickListener(L menuItemClickListener);

        /**
         * Register a menu item click event listener.
         * <p>
         * Alias for {@link #withClickListener(EventListener)}.
         * </p>
         *
         * @param menuItemClickListener The listener to add (not null)
         * @return this
         */
        default SubMenuItemBuilder<L, M, I, S, B> onClick(L menuItemClickListener) {
            return withClickListener(menuItemClickListener);
        }

        /**
         * Add the sub menu item to the MenuItem.
         *
         * @return The parent MenuItem builder
         */
        B add();

    }
}
