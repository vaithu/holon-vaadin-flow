package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.vaadinplus.components.AppBar;
import com.holonplatform.vaadin.flow.vaadinplus.components.AppShellLayout;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultAppShellLayoutBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.sidenav.SideNav;

import java.util.function.Consumer;

/**
 * Fluent builder for {@link AppShellLayout}.
 *
 * <p>Assembles the standard enterprise application shell: an {@link AppBar} in the
 * navbar with optional {@link com.vaadin.flow.component.applayout.DrawerToggle}, brand,
 * search, notification bell, language selector, dark/light theme toggle, and user
 * avatar — plus an optional drawer header and navigation wrapper.
 *
 * <h3>Option A — standalone (demo / prototype)</h3>
 * <pre>{@code
 * AppShellLayout shell = Components.appShell()
 *     .navbarBrand("My App", "v1.0", HomeView.class)
 *     .search("Search…")
 *     .notifications(3, "Deployment done", "New message")
 *     .languages("English", "Deutsch", "Francais")
 *     .themeToggle()
 *     .user(u -> u
 *         .name("Jane Smith")
 *         .avatar("/avatars/jane.png")
 *         .menu(m -> m
 *             .item("Profile")
 *             .item("Sign out")))
 *     .drawerBrand(logoComponent)
 *     .nav(SideNavBuilder.create()...buildWrapper())
 *     .build();
 * }</pre>
 *
 * <h3>Option B — per-user with Spring injection (production)</h3>
 * <p>Because Vaadin creates one layout instance per user session, inject
 * Spring beans and call {@link #configure(AppLayout)} on {@code this}:
 * <pre>{@code
 * @SpringComponent
 * @UIScope
 * public class MyAppLayout extends AppLayout {
 *
 *     @Autowired
 *     public MyAppLayout(SecurityService security,
 *                        NotificationService notifs,
 *                        UserPreferencesService prefs) {
 *
 *         var me = security.currentUser();
 *         Components.appShell()
 *             .navbarBrand(me.getCompanyName(), HomeView.class)
 *             .search("Search...")
 *             .notifications(notifs.countUnread(me),
 *                            notifs.topItems(me, 5).toArray(String[]::new))
 *             .languages(prefs.availableLocaleLabels(me))
 *             .themeToggle()
 *             .user(u -> u
 *                 .name(me.getFullName())
 *                 .avatar(me.getAvatarUrl())
 *                 .menu(m -> m
 *                     .item("Profile")
 *                     .item("Settings")
 *                     .item("Sign out")))
 *             .drawerBrand(buildLogo(me.getLogoUrl()))
 *             .nav(buildNav(me.getRoles()))
 *             .configure(this);   // applies to THIS layout instance
 *     }
 * }
 * }</pre>
 */
public interface AppShellLayoutBuilder {

    // ── Navbar Brand ───────────────────────────────────────────────────────────
    // Placed in the AppBar start slot (top navbar), after the DrawerToggle.

    /**
     * Sets a logo component to display before the navbar brand name.
     *
     * <p>Any component works: an {@code <img>}, an SVG icon, a Vaadin {@code Image}, etc.
     * The component receives the CSS class {@code app-bar__brand-logo} for styling.
     *
     * @param logo the logo component placed in the navbar (not null)
     */
    AppShellLayoutBuilder navbarBrandLogo(Component logo);

    /** Sets the brand title in the navbar start slot (after the DrawerToggle). */
    AppShellLayoutBuilder navbarBrand(String title);

    /** Sets the navbar brand title + version badge. */
    AppShellLayoutBuilder navbarBrand(String title, String version);

    /** Sets the navbar brand title as a router link to a view. */
    AppShellLayoutBuilder navbarBrand(String title, Class<? extends Component> homeView);

    /** Sets the navbar brand title, version badge, and home router link. */
    AppShellLayoutBuilder navbarBrand(String title, String version, Class<? extends Component> homeView);

    // ── Search ─────────────────────────────────────────────────────────────────

    /** Enables the middle-slot search field with the given placeholder. */
    AppShellLayoutBuilder search(String placeholder);

    /**
     * Enables the middle-slot search field with a value-change listener.
     *
     * @param placeholder   placeholder text (not null)
     * @param valueListener called on every (lazy) value change; receives the typed string
     */
    AppShellLayoutBuilder search(String placeholder, Consumer<String> valueListener);

    // ── Notifications ──────────────────────────────────────────────────────────

    /** Enables the notification bell with the given dropdown items. */
    AppShellLayoutBuilder notifications(String... items);

    /**
     * Enables the notification bell with a numeric badge and dropdown items.
     *
     * @param badgeCount number shown on the bell icon (0 = no badge)
     * @param items      dropdown text items (e.g. recent notification summaries)
     */
    AppShellLayoutBuilder notifications(int badgeCount, String... items);

    // ── Languages ──────────────────────────────────────────────────────────────

    /**
     * Adds a language / locale selector to the end slot.
     *
     * @param langs locale display labels, e.g. {@code "English (US)"}
     */
    AppShellLayoutBuilder languages(String... langs);

    // ── Theme toggle ───────────────────────────────────────────────────────────

    /**
     * Enables the dark / light theme toggle button.
     * Preference is persisted to {@code localStorage} and restored on the next
     * page load, falling back to the OS colour-scheme preference.
     */
    AppShellLayoutBuilder themeToggle();

    // ── User avatar ────────────────────────────────────────────────────────────

    /**
     * Configures the user avatar and profile menu via a fluent lambda.
     *
     * <pre>{@code
     * // minimal — initials only
     * .user(u -> u.name("Jane Smith"))
     *
     * // with photo + menu
     * .user(u -> u
     *     .name(me.getFullName())
     *     .avatar(me.getAvatarUrl())
     *     .menu(m -> m
     *         .item("Profile")
     *         .item("Settings")
     *         .item("Sign out")))
     * }</pre>
     *
     * @param config consumer that configures the user block (not null)
     */
    AppShellLayoutBuilder user(Consumer<UserConfig> config);

    /**
     * Fluent configurator for the user avatar block.
     * Obtain an instance via {@link AppShellLayoutBuilder#user(Consumer)}.
     */
    interface UserConfig {

        /**
         * Sets the user's display name.
         * Shown as initials in the avatar and as the first (non-interactive)
         * label in the context menu.
         *
         * @param name display name (not null)
         * @return this configurator
         */
        UserConfig name(String name);

        /**
         * Sets the user's profile photo URL.
         * When non-null the photo is shown instead of initials.
         *
         * @param imageUrl absolute or context-relative URL, or {@code null} for initials
         * @return this configurator
         */
        UserConfig avatar(String imageUrl);

        /**
         * Adds clickable items to the context menu shown on avatar click.
         * The user's name is always prepended automatically as a non-interactive header.
         *
         * @param config menu configurator consumer (not null)
         * @return this configurator
         */
        UserConfig menu(Consumer<MenuConfigurator> config);

        /**
         * Fluent configurator for the profile menu items.
         */
        interface MenuConfigurator {

            /**
             * Adds a menu item label.
             *
             * @param menuItem item label (nulls silently skipped)
             * @return this configurator
             */
            MenuConfigurator item(String menuItem);

            /**
             * Adds multiple menu item labels.
             *
             * @param menuItems item labels (nulls silently skipped)
             * @return this configurator
             */
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

    // ── Drawer ─────────────────────────────────────────────────────────────────

    /**
     * Adds a brand/logo component at the top of the drawer sidebar (e.g. app logo + name).
     *
     * <p>This is the correct place for a prominent logo at the top-left of the sidebar —
     * distinct from {@link #navbarBrand} which places the brand title in the top navbar.
     *
     * @param brand the component shown at the top of the drawer (not null)
     */
    AppShellLayoutBuilder drawerBrand(Component brand);

    /**
     * Provides the navigation wrapper from {@code SideNavBuilder.buildWrapper()}.
     *
     * @param navWrapper the sidenav host {@link Div} (not null)
     */
    AppShellLayoutBuilder nav(Div navWrapper);

    /**
     * Provides both the navigation wrapper and the underlying {@link SideNav}.
     *
     * <p>Use this overload (instead of {@link #nav(Div)}) when you also call
     * {@link #desktopMenuBar()}, because the {@code SideNav} reference is required to
     * build the horizontal MenuBar from the configured nav items.
     *
     * @param navWrapper the sidenav host {@link Div} from {@code SideNavBuilder.buildWrapper()} (not null)
     * @param sideNav    the underlying {@link SideNav} (not null)
     */
    AppShellLayoutBuilder nav(Div navWrapper, SideNav sideNav);

    /**
     * Controls whether the {@link com.vaadin.flow.component.applayout.DrawerToggle}
     * hamburger button is auto-injected as the first item in the start slot.
     *
     * <p>Default is {@code true}. Set to {@code false} when providing a custom toggle
     * via {@link #customizeStart(Consumer)}.
     *
     * @param show {@code false} to suppress the auto-injected toggle
     */
    AppShellLayoutBuilder drawerToggle(boolean show);

    /**
     * Enables a toggle button in the AppBar (CSS class {@code app-bar__layout-toggle}) that
     * switches between sidebar navigation (drawer) and a horizontal
     * {@link com.vaadin.flow.component.menubar.MenuBar} in the navbar — ideal for desktop mode.
     *
     * <p>Requires navigation to be provided via
     * {@link #nav(Div, com.vaadin.flow.component.sidenav.SideNav)} so the builder has access
     * to the SideNav items for conversion.
     *
     * <p>Hide the button on small screens with CSS:
     * <pre>{@code
     * @media (max-width: 768px) { .app-bar__layout-toggle { display: none !important; } }
     * }</pre>
     */
    AppShellLayoutBuilder desktopMenuBar();

    // ── Escape hatches ─────────────────────────────────────────────────────────

    /**
     * Provides direct access to the {@link AppBar} after the start slot is built.
     * Use to inject components not covered by the builder API.
     */
    AppShellLayoutBuilder customizeStart(Consumer<AppBar> customizer);

    /**
     * Provides direct access to the {@link AppBar} after the end slot is built.
     * Use to inject components not covered by the builder API.
     */
    AppShellLayoutBuilder customizeEnd(Consumer<AppBar> customizer);

    // ── Terminal ───────────────────────────────────────────────────────────────

    /**
     * Builds and returns a new {@link AppShellLayout}.
     * Use this for standalone / demo scenarios.
     *
     * @return a fully configured {@link AppShellLayout}
     */
    AppShellLayout build();

    /**
     * Applies this configuration to an <em>existing</em> {@link AppLayout}.
     *
     * <p>This is the correct terminal method for a {@code @SpringComponent @UIScope}
     * layout class — call it as the last statement in the constructor:
     * <pre>{@code
     * Components.appShell()
     *     .navbarBrand(user.getCompanyName())
     *     .user(u -> u
     *         .name(user.getName())
     *         .avatar(user.getAvatarUrl())
     *         .menu(m -> m
     *             .item("Profile")
     *             .item("Sign out")))
     *     .nav(buildNav(user.getRoles()))
     *     .configure(this);
     * }</pre>
     *
     * @param target the {@link AppLayout} to configure — typically {@code this} (not null)
     */
    void configure(AppLayout target);

    // ── Factory ────────────────────────────────────────────────────────────────

    /**
     * Creates a new {@link AppShellLayoutBuilder}.
     *
     * @return a fresh builder instance
     */
    static AppShellLayoutBuilder create() {
        return new DefaultAppShellLayoutBuilder();
    }
}
