package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.vaadinplus.components.AppShellLayout;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultAppShellLayoutBuilder;
import com.vaadin.flow.component.applayout.AppLayout;

/**
 * Fluent builder for {@link AppShellLayout}.
 *
 * <p>Assembles the standard enterprise application shell: an {@link com.holonplatform.vaadin.flow.vaadinplus.components.AppBar} in the
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
public interface AppShellLayoutBuilder extends AppShellLayoutConfigurator<AppShellLayoutBuilder> {

    /**
     * Builds and returns a new {@link AppShellLayout}.
     *
     * @return a fully configured {@link AppShellLayout}
     */
    AppShellLayout build();

    /**
     * Applies this configuration to an <em>existing</em> {@link AppLayout}.
     *
     * @param target the {@link AppLayout} to configure — typically {@code this} (not null)
     */
    void configure(AppLayout target);

    /**
     * Creates a new {@link AppShellLayoutBuilder}.
     *
     * @return a fresh builder instance
     */
    static AppShellLayoutBuilder create() {
        return new DefaultAppShellLayoutBuilder();
    }
}
