package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.MenuItemConfigurator;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.shared.Tooltip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class AbstractMenuItemConfigurator<C extends MenuItemConfigurator<C>>
        extends AbstractComponentConfigurator<MenuBar, C>
        implements MenuItemConfigurator<C> {

    private static final Logger log = LoggerFactory.getLogger(AbstractMenuItemConfigurator.class);
    private MenuItem menuItem ;
    private SubMenu subMenuItem ;
    private MenuItem parentMenuItem;

    /**
     * Constructor.
     *
     * @param menuBar The menuBar instance (not null)
     */
    public AbstractMenuItemConfigurator(MenuBar menuBar) {
        super(menuBar);
    }

    public AbstractMenuItemConfigurator(MenuBar menuBar, MenuItem menuItem) {
        super(menuBar);
        this.menuItem = parentMenuItem = menuItem;
    }

    @Override
    public C themeNames(String... themeNames) {

        menuItem.addThemeNames(themeNames);
        return getConfigurator();
    }

    @Override
    public C keepOpen(boolean keepOpen) {
        menuItem.setKeepOpen(keepOpen);
        return getConfigurator();
    }

    @Override
    public C toolTip(String toolTip) {
        Tooltip.forComponent(menuItem).setText(toolTip);
        return getConfigurator();
    }

    @Override
    public C checkable(boolean checkable) {
        menuItem.setCheckable(checkable);
        return getConfigurator();
    }

    @Override
    public C checked(boolean checked) {
        menuItem.setChecked(checked);
        return getConfigurator();
    }

    @Override
    public C icon(Icon icon) {
        menuItem = parentMenuItem = getComponent().addItem(icon);
        return getConfigurator();
    }

    @Override
    public C withMenuItem(Icon icon, String text) {
//        menuItem = Optional.ofNullable(parentMenuItem = getComponent().addItem(icon).a);
        return icon(icon).add(text);
    }

    @Override
    public C withMenuItem(String text) {
        log.info("Creating new menu item");
        menuItem = parentMenuItem = getComponent().addItem(text);
//        printMenuText(menuItem.get());
        return getConfigurator();
    }

    @Override
    public C menuItemStyleNames(String... styleNames) {
        menuItem.addClassNames(styleNames);
        return getConfigurator();
    }

    @Override
    public C subMenuStyleNames(String... styleNames) {
        return menuItemStyleNames(styleNames);
    }

    @Override
    public C separator() {
        subMenuItem.addSeparator();
        return getConfigurator();
    }

    @Override
    public C withSubMenu(Icon icon, String text) {
        icon.addClassName("menu-item__icon");
        icon.getStyle().set("height", "var(--lumo-icon-size-s)");
        icon.getStyle().set("marginRight", "var(--lumo-space-s)");
        return withSubMenu(icon).add(text);
    }

    @Override
    public C withMenuItem(Component component) {
        menuItem = parentMenuItem = getComponent().addItem(component);
        return getConfigurator();
    }

    @Override
    public C withSubMenu(String text) {
        subMenuItem = parentMenuItem.getSubMenu();
        menuItem = subMenuItem.addItem(text);
        subMenuItem = menuItem.getSubMenu();
//        printMenuText(menuItem.get());
        return getConfigurator();
    }

    @Override
    public C withSubMenu(Component component) {
        subMenuItem = parentMenuItem.getSubMenu();
        menuItem = subMenuItem.addItem(component);
        subMenuItem = menuItem.getSubMenu();

        return getConfigurator();
    }

    @Override
    public C withSubMenuItem(String text) {
        final SubMenu subMenu = subMenuItem;
        menuItem = subMenu.addItem(text);
//        printMenuText(menuItem.get());
        return getConfigurator();
    }

    @Override
    public C withSubMenuItem(Component component) {
        final SubMenu subMenu = subMenuItem;
        menuItem = subMenu.addItem(component);
        return getConfigurator();
    }

    @Override
    public C withClickListener(ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        menuItem.addClickListener(clickListener);
        return getConfigurator();
    }

    @Override
    public C add(Component... components) {
        parentMenuItem.getSubMenu().addComponent(components);
        return getConfigurator();
    }


    @Override
    public C addComponentAsFirst(Component component) {
        menuItem.addComponentAsFirst(component);
        return getConfigurator();
    }

    @Override
    public C addComponentAtIndex(int index, Component component) {
//        menuItem.ifPresent(menuItem1 -> menuItem1.addComponentAtIndex(index, component));
        parentMenuItem.getSubMenu().addComponentAtIndex(index, component);
        return getConfigurator();
    }

    @Override
    public C add(String text) {
        menuItem.add(text);
        return getConfigurator();
    }

    @Override
    public C withThemeVariants(MenuBarVariant... variants) {
        getComponent().addThemeVariants(variants);
        return getConfigurator();
    }

    @Override
    public MenuItem getMenuItem() {
        return menuItem;
    }

    @Override
    public SubMenu getSubMenu() {
        return subMenuItem;
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
        return Optional.of(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }

}
