package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultMenuItemBuilder;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.menubar.MenuBar;

public interface MenuItemBuilder extends MenuItemConfigurator<MenuItemBuilder>, ComponentBuilder<MenuBar, MenuItemBuilder> {


    static MenuItemBuilder create() {
        return create(new MenuBar());
    }

    static MenuItemBuilder create(MenuBar menuBar) {
        return new DefaultMenuItemBuilder(menuBar);
    }

    static MenuItemBuilder create(MenuBar menuBar, MenuItem menuItem) {
        return new DefaultMenuItemBuilder(menuBar, menuItem);
    }

}
