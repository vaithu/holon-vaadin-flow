package com.holonplatform.vaadin.flow.vaadinplus.components;

import com.iyensoft.vaadin.flow.components.builders.AppShellLayoutBuilder;
import com.vaadin.flow.component.applayout.AppLayout;

/**
 * A fully-configured application shell that extends {@link AppLayout}.
 *
 * <p>Encapsulates the standard SaaS / enterprise shell pattern:
 * <ul>
 *   <li><b>Navbar</b> — {@link AppBar} with optional
 *       {@link com.vaadin.flow.component.applayout.DrawerToggle}, brand, search,
 *       notification bell, language selector, dark/light toggle, and user-avatar menu</li>
 *   <li><b>Drawer</b> — optional drawer header + navigation wrapper
 *       (typically from {@code SideNavBuilder.buildWrapper()})</li>
 *   <li><b>Content</b> — AppLayout's {@code <main>} slot (no extra wrappers)</li>
 * </ul>
 *
 * <p>Always created through the fluent builder:
 * <pre>{@code
 * // Option A — standalone
 * AppShellLayout shell = Components.appShell()
 *     .navbarBrand("My App", "v1.0", HomeView.class)
 *     .search("Search…")
 *     .notifications(3, "Deployment done", "New message")
 *     .languages("English", "Deutsch")
 *     .themeToggle()
 *     .user(u -> u
 *         .name("Jane Smith")
 *         .avatar("/avatars/jane.png")
 *         .menu(m -> m
 *             .item("Profile")
 *             .item("Sign out")))
 *     .nav(SideNavBuilder.create()...buildWrapper())
 *     .build();
 *
 * // Option B — per-user with Spring @UIScope
 * @SpringComponent @UIScope
 * public class MyLayout extends AppLayout {
 *     @Autowired
 *     public MyLayout(SecurityService sec) {
 *         var me = sec.currentUser();
 *         Components.appShell()
 *             .navbarBrand(me.getCompanyName())
 *             .user(u -> u
 *                 .name(me.getFullName())
 *                 .avatar(me.getAvatarUrl())
 *                 .menu(m -> m
 *                     .item("Profile")
 *                     .item("Sign out")))
 *             .nav(buildNav(me.getRoles()))
 *             .configure(this);   // applies to THIS AppLayout
 *     }
 * }
 * }</pre>
 *
 * @see AppShellLayoutBuilder
 * @see com.holonplatform.vaadin.flow.components.Components#appShell()
 */
public final class AppShellLayout extends AppLayout {

    /**
     * Creates a new {@link AppShellLayout}.
     * Prefer using the fluent builder via {@link #builder()} or {@code Components.appShell()}.
     */
    public AppShellLayout() {}

    /**
     * Returns a new fluent {@link AppShellLayoutBuilder}.
     * Equivalent to {@code Components.appShell()}.
     *
     * @return a fresh builder
     */
    public static AppShellLayoutBuilder builder() {
        return AppShellLayoutBuilder.create();
    }
}
