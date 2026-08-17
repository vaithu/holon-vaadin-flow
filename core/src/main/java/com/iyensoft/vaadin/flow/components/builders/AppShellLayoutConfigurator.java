package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.vaadinplus.components.AppBar;
import com.holonplatform.vaadin.flow.vaadinplus.components.AppShellLayout;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultAppShellLayoutConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.sidenav.SideNav;

import java.util.function.Consumer;

/**
 * Configurator for {@link AppShellLayout} components.
 *
 * @param <C> Concrete configurator type (for fluent chaining)
 */
public interface AppShellLayoutConfigurator<C extends AppShellLayoutConfigurator<C>> {

    C navbarBrandLogo(Component logo);

    C navbarBrand(String title);

    C navbarBrand(String title, String version);

    C navbarBrand(String title, Class<? extends Component> homeView);

    C navbarBrand(String title, String version, Class<? extends Component> homeView);

    C search(String placeholder);

    C search(String placeholder, Consumer<String> valueListener);

    C notifications(String... items);

    C notifications(int badgeCount, String... items);

    C languages(String... langs);

    C themeToggle();

    C user(Consumer<UserConfig> config);

    C drawerBrand(Component brand);

    C nav(Div navWrapper);

    C nav(Div navWrapper, SideNav sideNav);

    C drawerToggle(boolean show);

    C desktopMenuBar();

    C customizeStart(Consumer<AppBar> customizer);

    C customizeEnd(Consumer<AppBar> customizer);

    /**
     * Fluent configurator for the user avatar block.
     */
    interface UserConfig {

        UserConfig name(String name);

        UserConfig avatar(String imageUrl);

        UserConfig menu(Consumer<MenuConfigurator> config);

        /**
         * Fluent configurator for the profile menu items.
         */
        interface MenuConfigurator {

            MenuConfigurator item(String menuItem);

            default MenuConfigurator items(String... menuItems) {
                if (menuItems != null) {
                    for (String menuItem : menuItems) {
                        item(menuItem);
                    }
                }
                return this;
            }
        }
    }

    /**
     * Configure an existing {@link AppShellLayout}.
     *
     * @param layout the layout to configure (not null)
     * @return a new {@link BaseAppShellLayoutConfigurator}
     */
    static BaseAppShellLayoutConfigurator configure(AppShellLayout layout) {
        return new DefaultAppShellLayoutConfigurator(layout);
    }

    interface BaseAppShellLayoutConfigurator extends AppShellLayoutConfigurator<BaseAppShellLayoutConfigurator> {}
}
