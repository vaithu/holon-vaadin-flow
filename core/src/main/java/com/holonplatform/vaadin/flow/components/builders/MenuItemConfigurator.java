package com.holonplatform.vaadin.flow.components.builders;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBarVariant;

/**
 * Item component used inside ContextMenu and SubMenu. This component can be created and added to a menu overlay with
 * HasMenuItems.withMenuItem(String, ComponentEventListener) and similar methods.
 *
 * @param <C>
 */
public interface MenuItemConfigurator<C extends MenuItemConfigurator<C>> extends ComponentConfigurator<C>
        , HasStyleConfigurator<C>, HasComponentsConfigurator<C>, HasSizeConfigurator<C>, HasEnabledConfigurator<C>,HasThemeVariantConfigurator<MenuBarVariant,C> {


    /**
     * Adds a new item component with the given component and click listener to the context menu overlay.
     *
     * This is a convenience method for the use case where you have a list of highlightable MenuItems inside the overlay. If you want to configure the contents of the overlay without wrapping them inside MenuItems, or if you just want to add some non-highlightable components between the items, use the ContextMenuBase.add(Component...) method.
     * @param component
     * @param clickListener
     * @return
     */
  default   C withMenuItem(Component component,
              ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
      return withMenuItem(component).withClickListener(clickListener);
  }

    /**
     * Adds a new item component with the given text content and click listener to the context menu overlay.
     *
     * This is a convenience method for the use case where you have a list of highlightable MenuItems inside the overlay. If you want to configure the contents of the overlay without wrapping them inside MenuItems, or if you just want to add some non-highlightable components between the items, use the ContextMenuBase.add(Component...) method
     * @param text
     * @param clickListener
     * @return
     */

   default C withMenuItem(String text,
              ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
       return withMenuItem(text).withClickListener(clickListener);
   }

    /**
     * Adds one or more theme names to this item. Multiple theme names can be specified by using multiple parameters.
     *
     * @param themeNames
     * @return
     */
    C themeNames(String... themeNames);


    /**
     * Sets the keep open state of this menu item. An item that marked as keep open prevents menu from closing when clicked.
     *
     * @param keepOpen
     * @return
     */
    C keepOpen(boolean keepOpen);

    /**
     * Sets the checkable state of this menu item. A checkable item toggles a checkmark icon when clicked. Changes in the checked state can be handled in the item's click handler with isChecked().
     * <p>
     * Setting a checked item un-checkable also makes it un-checked.
     *
     * @param checkable
     * @return
     */
    C checkable(boolean checkable);

    /**
     * Sets the checked state of this item. A checked item displays a checkmark icon next to it. The checked state is also toggled by clicking the item.
     * <p>
     * Note that the item needs to be explicitly set as checkable via setCheckable(boolean) in order to check it.
     *
     * @param checked
     * @return
     */
    C checked(boolean checked);

    C toolTip(String toolTip);

    C icon(Icon icon);

    C withMenuItem(String text);
    C withMenuItem(Component component);

    C withMenuItem(Icon icon, String text);
    default C withMenuItem(Icon icon, String text,ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        return withMenuItem(icon, text).withClickListener(clickListener);
    }

    C withSubMenu(Icon icon, String text);
    default C withSubMenu(Icon icon, String text,ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        return withSubMenu(icon, text).withClickListener(clickListener);
    }

    C withSubMenu(String text);
    C withSubMenu(Component component);

    C withSubMenuItem(String text);

    C subMenuStyleNames(String... styleNames);
    C menuItemStyleNames(String... styleNames);

    C withSubMenuItem(Component component);

    default C withSubMenuItem(String text, ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        return withSubMenuItem(text).withClickListener(clickListener);
    }

    default C withSubMenuItem(Component component, ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        return withSubMenuItem(component).withClickListener(clickListener);
    }

    C withClickListener(ComponentEventListener<ClickEvent<MenuItem>> clickListener);

   default C withSubMenu(String text, ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
       return withSubMenu(text).withClickListener(clickListener);
   }

    default C withSubMenu(Component component, ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        return withSubMenu(component).withClickListener(clickListener);
    }



    C separator();

    MenuItem getMenuItem();

    SubMenu getSubMenu();

}
