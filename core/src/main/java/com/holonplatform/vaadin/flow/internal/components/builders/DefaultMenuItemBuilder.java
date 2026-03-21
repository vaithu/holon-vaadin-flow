package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.MenuItemBuilder;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.menubar.MenuBar;

public class DefaultMenuItemBuilder extends AbstractMenuItemConfigurator<MenuItemBuilder> implements MenuItemBuilder{

    /**
     * Constructor.
     *
     * @param menuBar The menuBar instance (not null)
     */
    public DefaultMenuItemBuilder(MenuBar menuBar) {
        super(menuBar);
    }

    /**
     * Constructor.
     *
     * @param menuBar The menuBar instance (not null)
     * @param menuItem The MenuItem instance (not null)
     */
    public DefaultMenuItemBuilder(MenuBar menuBar, MenuItem menuItem) {
        super(menuBar,menuItem);
    }

    @Override
    public MenuBar build() {
        return getComponent();
    }

    @Override
    protected MenuItemBuilder getConfigurator() {
        return this;
    }


}
