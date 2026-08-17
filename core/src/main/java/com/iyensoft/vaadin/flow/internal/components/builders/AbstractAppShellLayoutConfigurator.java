package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.vaadinplus.components.AppBar;
import com.holonplatform.vaadin.flow.vaadinplus.components.AppShellLayout;
import com.iyensoft.vaadin.flow.components.builders.AppShellLayoutConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Base {@link AppShellLayoutConfigurator} implementation that holds all state and
 * provides the {@link #applyTo(AppLayout)} logic shared by builder and configurator.
 *
 * @param <C> Concrete configurator type
 */
public abstract class AbstractAppShellLayoutConfigurator<C extends AppShellLayoutConfigurator<C>>
        implements AppShellLayoutConfigurator<C> {

    // brand
    private String brandTitle;
    private String brandVersion;
    private Class<? extends Component> brandView;
    private Component brandLogo;

    // search
    private String searchPlaceholder;
    private Consumer<String> searchListener;

    // notifications
    private boolean showNotifications = false;
    private final List<String> notificationItems = new ArrayList<>();
    private int notificationBadge = 0;

    // languages
    private final List<String> languageItems = new ArrayList<>();

    // theme toggle
    private boolean themeToggle = false;

    // user
    private String userName;
    private String userAvatarUrl;
    private final List<String> userMenuItems = new ArrayList<>();

    // drawer
    private Component drawerHeader;
    private Div navWrapper;
    private boolean showDrawerToggle = true;
    private SideNav sideNavRef;

    // desktop menubar flip
    private boolean desktopMenuBarEnabled = false;

    // escape hatches
    private Consumer<AppBar> startCustomizer;
    private Consumer<AppBar> endCustomizer;

    protected abstract C getConfigurator();

    // ── Navbar Brand ──────────────────────────────────────────────────────────────

    @Override
    public C navbarBrandLogo(Component logo) {
        this.brandLogo = Objects.requireNonNull(logo, "logo");
        return getConfigurator();
    }

    @Override
    public C navbarBrand(String title) {
        this.brandTitle = Objects.requireNonNull(title);
        return getConfigurator();
    }

    @Override
    public C navbarBrand(String title, String version) {
        this.brandTitle   = Objects.requireNonNull(title);
        this.brandVersion = version;
        return getConfigurator();
    }

    @Override
    public C navbarBrand(String title, Class<? extends Component> homeView) {
        this.brandTitle = Objects.requireNonNull(title);
        this.brandView  = homeView;
        return getConfigurator();
    }

    @Override
    public C navbarBrand(String title, String version, Class<? extends Component> homeView) {
        this.brandTitle   = Objects.requireNonNull(title);
        this.brandVersion = version;
        this.brandView    = homeView;
        return getConfigurator();
    }

    // ── Search ─────────────────────────────────────────────────────────────────────

    @Override
    public C search(String placeholder) {
        this.searchPlaceholder = Objects.requireNonNull(placeholder);
        return getConfigurator();
    }

    @Override
    public C search(String placeholder, Consumer<String> valueListener) {
        this.searchPlaceholder = Objects.requireNonNull(placeholder);
        this.searchListener    = valueListener;
        return getConfigurator();
    }

    // ── Notifications ──────────────────────────────────────────────────────────────

    @Override
    public C notifications(String... items) {
        this.showNotifications = true;
        addAll(notificationItems, items);
        return getConfigurator();
    }

    @Override
    public C notifications(int badgeCount, String... items) {
        this.showNotifications = true;
        this.notificationBadge = Math.max(0, badgeCount);
        addAll(notificationItems, items);
        return getConfigurator();
    }

    // ── Languages ──────────────────────────────────────────────────────────────────

    @Override
    public C languages(String... langs) {
        addAll(languageItems, langs);
        return getConfigurator();
    }

    // ── Theme toggle ───────────────────────────────────────────────────────────────

    @Override
    public C themeToggle() {
        this.themeToggle = true;
        return getConfigurator();
    }

    // ── User ───────────────────────────────────────────────────────────────────────

    @Override
    public C user(Consumer<AppShellLayoutConfigurator.UserConfig> config) {
        var cfg = new DefaultUserConfig();
        Objects.requireNonNull(config, "config").accept(cfg);
        this.userName      = cfg.userName;
        this.userAvatarUrl = cfg.userAvatarUrl;
        addAll(userMenuItems, cfg.userMenuItems.toArray(String[]::new));
        return getConfigurator();
    }

    // ── Drawer ─────────────────────────────────────────────────────────────────────

    @Override
    public C drawerBrand(Component brand) {
        this.drawerHeader = Objects.requireNonNull(brand);
        return getConfigurator();
    }

    @Override
    public C nav(Div navWrapper) {
        this.navWrapper = Objects.requireNonNull(navWrapper);
        return getConfigurator();
    }

    @Override
    public C nav(Div navWrapper, SideNav sideNav) {
        this.navWrapper  = Objects.requireNonNull(navWrapper, "navWrapper");
        this.sideNavRef  = Objects.requireNonNull(sideNav, "sideNav");
        return getConfigurator();
    }

    @Override
    public C drawerToggle(boolean show) {
        this.showDrawerToggle = show;
        return getConfigurator();
    }

    @Override
    public C desktopMenuBar() {
        this.desktopMenuBarEnabled = true;
        return getConfigurator();
    }

    // ── Escape hatches ─────────────────────────────────────────────────────────────

    @Override
    public C customizeStart(Consumer<AppBar> customizer) {
        this.startCustomizer = Objects.requireNonNull(customizer);
        return getConfigurator();
    }

    @Override
    public C customizeEnd(Consumer<AppBar> customizer) {
        this.endCustomizer = Objects.requireNonNull(customizer);
        return getConfigurator();
    }

    // ── Core wiring ────────────────────────────────────────────────────────────────

    protected void applyTo(AppLayout layout) {
        var appBar = new AppBar();

        // DrawerToggle — always first in the start slot when a drawer is configured.
        boolean hasDrawer = navWrapper != null || drawerHeader != null;
        if (hasDrawer && showDrawerToggle) {
            var toggle = new DrawerToggle();
            toggle.addClassName("app-bar__action-btn");
            appBar.addToStart(toggle);
        }

        // Brand (start slot) — optional logo + name + optional version badge
        if (brandLogo != null || brandTitle != null) {
            var brandWrap = new Div();
            brandWrap.addClassName("app-bar__brand");

            if (brandLogo != null) {
                brandLogo.addClassName("app-bar__brand-logo");
                brandWrap.add(brandLogo);
            }

            if (brandTitle != null) {
                Component nameComp;
                if (brandView != null) {
                    var link = new RouterLink("", brandView);
                    link.add(new Span(brandTitle));
                    link.addClassName("app-bar__brand-name");
                    nameComp = link;
                } else {
                    var nameSpan = new Span(brandTitle);
                    nameSpan.addClassName("app-bar__brand-name");
                    nameComp = nameSpan;
                }
                brandWrap.add(nameComp);

                if (brandVersion != null) {
                    var versionSpan = new Span(brandVersion);
                    versionSpan.addClassName("app-bar__brand-version");
                    brandWrap.add(versionSpan);
                }
            }

            appBar.addToStart(brandWrap);
        }
        if (startCustomizer != null) {
            startCustomizer.accept(appBar);
        }

        // Search (middle slot)
        if (searchPlaceholder != null) {
            var search = new TextField();
            search.setPlaceholder(searchPlaceholder);
            search.setPrefixComponent(VaadinIcon.SEARCH.create());
            search.addClassName("app-bar__search");
            if (searchListener != null) {
                search.addValueChangeListener(e -> searchListener.accept(e.getValue()));
            }
            appBar.addToMiddle(search);
        }

        // ── End slot ─────────────────────────────────────────────────────────────────
        // 1. Desktop layout-toggle (leftmost in end slot)
        if (desktopMenuBarEnabled && sideNavRef != null) {
            var navMenuBar = buildNavMenuBar(sideNavRef);
            navMenuBar.addClassName("app-bar__nav-menubar");
            navMenuBar.addClassName("app-bar__hidden");

            var inMenuBarMode = new boolean[]{false};
            var layoutToggleBtn = new Button(VaadinIcon.GRID.create());
            layoutToggleBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
            layoutToggleBtn.addClassName("app-bar__action-btn");
            layoutToggleBtn.addClassName("app-bar__layout-toggle");
            layoutToggleBtn.getElement().setAttribute("title", "Switch to top navigation bar");

            layoutToggleBtn.addClickListener(e -> {
                inMenuBarMode[0] = !inMenuBarMode[0];
                if (inMenuBarMode[0]) {
                    navMenuBar.removeClassName("app-bar__hidden");
                    if (navWrapper != null) navWrapper.addClassName("app-bar__hidden");
                    layout.setDrawerOpened(false);
                    layoutToggleBtn.setIcon(VaadinIcon.MENU.create());
                    layoutToggleBtn.getElement().setAttribute("title", "Switch to sidebar navigation");
                    appBar.addToBottom(navMenuBar);
                } else {
                    navMenuBar.addClassName("app-bar__hidden");
                    if (navWrapper != null) navWrapper.removeClassName("app-bar__hidden");
                    layoutToggleBtn.setIcon(VaadinIcon.GRID.create());
                    layoutToggleBtn.getElement().setAttribute("title", "Switch to top navigation bar");
                }
            });

            appBar.addToEnd(layoutToggleBtn);
        }

        // 2. Notification bell
        if (showNotifications) {
            var notifBtn = new Button(VaadinIcon.BELL.create());
            notifBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
            notifBtn.addClassName("app-bar__action-btn");

            var notifWrap = new Div(notifBtn);
            notifWrap.addClassName("app-bar__badge-wrap");
            if (notificationBadge > 0) {
                notifWrap.getElement().setAttribute("data-badge",
                        notificationBadge > 99 ? "99+" : String.valueOf(notificationBadge));
            }

            var menu = new ContextMenu(notifBtn);
            menu.setOpenOnClick(true);
            notificationItems.forEach(menu::addItem);
            appBar.addToEnd(notifWrap);
        }

        // 3. Language selector
        if (!languageItems.isEmpty()) {
            var langBtn = new Button(VaadinIcon.GLOBE.create());
            langBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
            langBtn.addClassName("app-bar__action-btn");
            var menu = new ContextMenu(langBtn);
            menu.setOpenOnClick(true);
            languageItems.forEach(menu::addItem);
            appBar.addToEnd(langBtn);
        }

        // 4. Theme toggle
        if (themeToggle) {
            var themeBtn = new Button(VaadinIcon.MOON.create());
            themeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
            themeBtn.addClassName("app-bar__action-btn");

            themeBtn.addClickListener(e -> {
                var ui = e.getSource().getUI().orElseThrow();
                var themeList = ui.getElement().getThemeList();
                if (themeList.contains(Lumo.DARK)) {
                    themeList.remove(Lumo.DARK);
                    themeBtn.setIcon(VaadinIcon.MOON.create());
                    ui.getPage().executeJs(
                            "document.documentElement.style.colorScheme='light';" +
                            "try{localStorage.setItem('vaadin-theme','light')}catch(_){}");
                } else {
                    themeList.add(Lumo.DARK);
                    themeBtn.setIcon(VaadinIcon.SUN_O.create());
                    ui.getPage().executeJs(
                            "document.documentElement.style.colorScheme='dark';" +
                            "try{localStorage.setItem('vaadin-theme','dark')}catch(_){}");
                }
            });

            themeBtn.addAttachListener(e -> e.getUI().getPage().executeJs(
                    """
                    (function(){
                      var t='';
                      try{t=localStorage.getItem('vaadin-theme')||''}catch(_){}
                      if(!t) t=window.matchMedia('(prefers-color-scheme:dark)').matches?'dark':'light';
                      return t;
                    })()
                    """).then(String.class, theme -> {
                if (Lumo.DARK.equals(theme)) {
                    e.getUI().getElement().getThemeList().add(Lumo.DARK);
                    e.getUI().getPage().executeJs("document.documentElement.style.colorScheme='dark'");
                    themeBtn.setIcon(VaadinIcon.SUN_O.create());
                }
            }));

            appBar.addToEnd(themeBtn);
        }

        // 5. User avatar (rightmost in end slot)
        if (userName != null) {
            var avatar = new Avatar(userName);
            if (userAvatarUrl != null) {
                avatar.setImage(userAvatarUrl);
            }
            if (!userMenuItems.isEmpty()) {
                var menu = new ContextMenu(avatar);
                menu.setOpenOnClick(true);
                menu.addItem(userName);
                userMenuItems.forEach(menu::addItem);
            }
            appBar.addToEnd(avatar);
        }

        // End-slot escape hatch
        if (endCustomizer != null) {
            endCustomizer.accept(appBar);
        }

        // Assemble
        layout.setPrimarySection(AppLayout.Section.DRAWER);
        layout.addToNavbar(true, appBar);

        List<Component> drawerContent = new ArrayList<>();
        if (drawerHeader != null) drawerContent.add(drawerHeader);
        if (navWrapper   != null) drawerContent.add(navWrapper);
        if (!drawerContent.isEmpty()) {
            layout.addToDrawer(drawerContent.toArray(Component[]::new));
        }
    }

    // ── Desktop MenuBar helpers ────────────────────────────────────────────────────

    private static MenuBar buildNavMenuBar(SideNav nav) {
        var menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
        for (SideNavItem item : nav.getItems()) {
            addNavMenuItem(menuBar, item);
        }
        return menuBar;
    }

    private static void addNavMenuItem(MenuBar menuBar, SideNavItem item) {
        boolean hasChildren = !item.getItems().isEmpty();
        var mi = menuBar.addItem(navItemLabel(item.getLabel(), hasChildren));
        if (hasChildren) {
            addSubNavItems(mi.getSubMenu(), item.getItems());
        } else {
            String path = item.getPath();
            if (path != null && !path.isBlank()) {
                mi.addClickListener(e -> e.getSource().getUI().ifPresent(ui -> ui.navigate(path)));
            }
        }
    }

    private static Span navItemLabel(String text, boolean hasChildren) {
        var wrap = new Span();
        wrap.addClassName("app-bar__nav-item-label");
        wrap.add(new Span(text));
        if (hasChildren) {
            var chevron = VaadinIcon.CHEVRON_DOWN_SMALL.create();
            chevron.addClassName("app-bar__nav-chevron");
            wrap.add(chevron);
        }
        return wrap;
    }

    private static void addSubNavItems(SubMenu subMenu, List<SideNavItem> items) {
        for (SideNavItem item : items) {
            var mi = subMenu.addItem(item.getLabel());
            if (item.getItems().isEmpty()) {
                String path = item.getPath();
                if (path != null && !path.isBlank()) {
                    mi.addClickListener(e -> e.getSource().getUI().ifPresent(ui -> ui.navigate(path)));
                }
            } else {
                addSubNavItems(mi.getSubMenu(), item.getItems());
            }
        }
    }

    // ── Internal helpers ──────────────────────────────────────────────────────────

    protected static void addAll(List<String> target, String... values) {
        if (values == null) return;
        for (var v : values) {
            if (v != null) target.add(v);
        }
    }

    /** Default implementation of {@link AppShellLayoutConfigurator.UserConfig}. */
    private static final class DefaultUserConfig implements AppShellLayoutConfigurator.UserConfig {

        String userName;
        String userAvatarUrl;
        final List<String> userMenuItems = new ArrayList<>();

        @Override
        public AppShellLayoutConfigurator.UserConfig name(String name) {
            this.userName = Objects.requireNonNull(name, "name");
            return this;
        }

        @Override
        public AppShellLayoutConfigurator.UserConfig avatar(String imageUrl) {
            this.userAvatarUrl = imageUrl;
            return this;
        }

        @Override
        public AppShellLayoutConfigurator.UserConfig menu(Consumer<AppShellLayoutConfigurator.UserConfig.MenuConfigurator> config) {
            var menuConfig = new DefaultMenuConfig();
            Objects.requireNonNull(config, "config").accept(menuConfig);
            addAll(userMenuItems, menuConfig.userMenuItems.toArray(String[]::new));
            return this;
        }

        private static final class DefaultMenuConfig
                implements AppShellLayoutConfigurator.UserConfig.MenuConfigurator {

            final List<String> userMenuItems = new ArrayList<>();

            @Override
            public AppShellLayoutConfigurator.UserConfig.MenuConfigurator item(String menuItem) {
                if (menuItem != null) {
                    userMenuItems.add(menuItem);
                }
                return this;
            }
        }
    }
}
