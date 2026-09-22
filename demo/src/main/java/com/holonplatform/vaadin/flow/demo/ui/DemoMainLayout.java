package com.holonplatform.vaadin.flow.demo.ui;

import com.iyensoft.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.views.*;
import com.iyensoft.vaadin.flow.components.MaterialAppBar;
import com.iyensoft.vaadin.flow.components.ShellColor;
import com.iyensoft.vaadin.flow.components.builders.SideNavBuilder;
import com.iyensoft.vaadin.flow.utils.responsive.WindowSizeTracker;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoIcon;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * Root application layout -- shared by every demo view.
 *
 * <p>Navbar is a {@link MaterialAppBar} (Material 3 style) rather than the default
 * shell {@code AppBar}, colored with {@link MaterialAppBar.Color#AMBER}; the drawer
 * {@link com.iyensoft.vaadin.flow.components.builders.SideNavBuilder} uses the matching
 * {@link ShellColor#AMBER} theme -- both share the same "amber" palette.
 */
public final class DemoMainLayout extends AppLayout {

    DemoMainLayout() {

        // Keeps ViewModeContext in sync app-wide before any child view builds its content.
        WindowSizeTracker.enable(this);

        // Sidebar nav
        var nav = SideNavBuilder.create()
                .colorTheme(ShellColor.AMBER)
                .withSearch("Filter components")
                .withCollapse();

        // Layout & Structure
        nav.withNavItem("Layout & Structure")
                .prefixComponent(LineAwesomeIcon.TABLE_SOLID.create())
                .withItems(
                        new SideNavItem("Layout",        LayoutDemoView.class,            VaadinIcon.GRID_V.create()),
                        new SideNavItem("SplitLayout",   SplitLayoutDemoView.class,       VaadinIcon.SPLIT.create()),
                        new SideNavItem("Sheet",         SheetDemoView.class,             VaadinIcon.CLIPBOARD_TEXT.create()),
                        new SideNavItem("Scroller",      ScrollerDemoView.class,          VaadinIcon.RESIZE_V.create()),
                        new SideNavItem("RowBuilder",    RowColumnBuilderDemoView.class,  VaadinIcon.GRID_H.create()),
                        new SideNavItem("VaadinLayouts", VaadinLayoutsDemoView.class,     VaadinIcon.CUBES.create()),
                        new SideNavItem("Div",           DivDemoView.class,               VaadinIcon.ANGLE_RIGHT.create())
                )
                .add();

        // Responsive
        nav.withNavItem("Responsive")
                .prefixComponent(VaadinIcon.MOBILE.create())
                .withItems(
                        new SideNavItem("ResponsiveDiv",     ResponsiveDivDemoView.class,     VaadinIcon.RESIZE_V.create()),
                        new SideNavItem("ResponsiveDivSlot", ResponsiveDivSlotDemoView.class, VaadinIcon.RESIZE_V.create()),
                        new SideNavItem("ViewMode",          ViewModeDemoView.class,          VaadinIcon.DESKTOP.create()),
                        new SideNavItem("ViewModeContext",   ViewModeContextDemoView.class,   VaadinIcon.CONNECT.create())
                )
                .add();

        // Navigation
        nav.withNavItem("Navigation")
                .prefixComponent(VaadinIcon.MENU.create())
                .withItems(
                        new SideNavItem("AppBar",           AppBarDemoView.class,           VaadinIcon.TRENDING_UP.create()),
                        new SideNavItem("AppShellLayout",   AppShellLayoutDemoView.class,   VaadinIcon.VIEWPORT.create()),
                        new SideNavItem("Breadcrumb",       BreadcrumbDemoView.class,       VaadinIcon.ARROW_RIGHT.create()),
                        new SideNavItem("SideNav",          SideNavDemoView.class,          VaadinIcon.MENU.create()),
                        new SideNavItem("Pagination",       PaginationDemoView.class,       VaadinIcon.ELLIPSIS_DOTS_H.create()),
                        new SideNavItem("PageSizeSelector", PageSizeSelectorDemoView.class, VaadinIcon.LIST.create())
                )
                .add();

        // Tabs & Steppers
        nav.withNavItem("Tabs & Steppers")
                .prefixComponent(VaadinIcon.FOLDER.create())
                .withItems(
                        new SideNavItem("Tab",             TabsDemoView.class,            VaadinIcon.FOLDER.create()),
                        new SideNavItem("LazyTabs",        LazyTabsDemoView.class,        VaadinIcon.FOLDER_O.create()),
                        new SideNavItem("TabSheet",        TabSheetDemoView.class,        VaadinIcon.FILE.create()),
                        new SideNavItem("FlowStepper",     FlowStepperDemoView.class,     VaadinIcon.PROGRESSBAR.create()),
                        new SideNavItem("TimelineStepper", TimelineStepperDemoView.class, VaadinIcon.CLOCK.create())
                )
                .add();

        // Data & Listings
        nav.withNavItem("Data & Listings")
                .prefixComponent(VaadinIcon.TABLE.create())
                .withItems(
                        new SideNavItem("Product CRUD (JPA)",      ProductCrudDemoView.class,         VaadinIcon.DATABASE.create()),
                        new SideNavItem("BeanListing",             BeanListingDemoView.class,         VaadinIcon.TABLE.create()),
                        new SideNavItem("PropertyListing",         PropertyListingDemoView.class,     VaadinIcon.TABLE.create()),
                        new SideNavItem("KanbanBoard",             KanbanBoardDemoView.class,         VaadinIcon.TASKS.create()),
                        new SideNavItem("LineItemGrid",            LineItemGridDemoView.class,        VaadinIcon.GRID_BIG_O.create()),
                        new SideNavItem("PawnItemGrid",            PawnItemGridDemoView.class,        VaadinIcon.MONEY.create()),
                        new SideNavItem("ListingBundle",           ListingBundleDemoView.class,       VaadinIcon.DATABASE.create()),
                        new SideNavItem("FilterPanel",             FilterPanelDemoView.class,         VaadinIcon.FILTER.create()),
                        new SideNavItem("Chart of Accounts",       ChartOfAccountsDemoView.class,     VaadinIcon.BOOK.create()),
                        new SideNavItem("MasterDetail (Products)", MasterDetailDemoV2.class,          VaadinIcon.SPLIT.create()),
                        new SideNavItem("MasterDetail (Customer 360)", CustomerMasterDetailMaterialView.class, VaadinIcon.OFFICE.create()),
                        new SideNavItem("MasterDetail (Bills AP)", BillsMasterDetailView.class,       VaadinIcon.FILE_TEXT.create()),
                        new SideNavItem("MasterDetail (Orders)",   OrdersMasterDetailView.class,      VaadinIcon.CART.create())
                )
                .add();

        // Forms & Input
        nav.withNavItem("Forms & Input")
                .prefixComponent(LumoIcon.EDIT.create())
                .withItems(
                        new SideNavItem("EntityFormPanel",   EntityFormPanelDemoView.class,          VaadinIcon.FORM.create()),
                        new SideNavItem("Wizard (Desktop)",  WizardDesktopDemoView.class,            VaadinIcon.USER.create()),
                        new SideNavItem("Wizard (Mobile)",   WizardMobileDemoView.class,             VaadinIcon.MOBILE.create()),
                        new SideNavItem("New Customer (CRM)", NewCustomerDemoView.class,             VaadinIcon.OFFICE.create()),
                        new SideNavItem("BeanInputForm",     BeanInputFormDemoView.class,            LumoIcon.EDIT.create()),
                        new SideNavItem("PropertyInputForm", PropertyInputFormDemoView.class,        VaadinIcon.PENCIL.create()),
                        new SideNavItem("CollabFormSupport", CollaborationFormSupportDemoView.class, VaadinIcon.USERS.create()),
                        new SideNavItem("Inputs",            InputsDemoView.class,                   VaadinIcon.KEYBOARD.create()),
                        new SideNavItem("InputGroup",        InputGroupDemoView.class,               VaadinIcon.LIST_UL.create()),
                        new SideNavItem("InputOTP",          InputOTPDemoView.class,                 VaadinIcon.LOCK.create()),
                        new SideNavItem("IntegerField",      IntegerFieldDemoView.class,             VaadinIcon.PLUS.create()),
                        new SideNavItem("NumberField",       NumberFieldDemoView.class,              VaadinIcon.HASH.create()),
                        new SideNavItem("MultiSelect",       MultiSelectDemoView.class,              VaadinIcon.CHECK_SQUARE_O.create()),
                        new SideNavItem("SingleSelect",      SingleSelectDemoView.class,             VaadinIcon.DOT_CIRCLE.create()),
                        new SideNavItem("Select",            SelectDemoView.class,                   VaadinIcon.ANGLE_DOWN.create()),
                        new SideNavItem("ValidatableInput",  ValidatableInputDemoView.class,         VaadinIcon.CHECK.create()),
                        new SideNavItem("FormLayout",        FormLayoutDemoView.class,               VaadinIcon.FORM.create())
                )
                .add();

        // Feedback & Overlay
        nav.withNavItem("Feedback & Overlay")
                .prefixComponent(VaadinIcon.BELL.create())
                .withItems(
                        new SideNavItem("Alert",          AlertDemoView.class,          VaadinIcon.WARNING.create()),
                        new SideNavItem("AlertDialog",    AlertDialogDemoView.class,    VaadinIcon.EXCLAMATION_CIRCLE.create()),
                        new SideNavItem("AlertModal",     AlertModalDemoView.class,     VaadinIcon.EXCLAMATION.create()),
                        new SideNavItem("Notification",   NotificationDemoView.class,   VaadinIcon.BELL.create()),
                        new SideNavItem("Dialog",         DialogDemoView.class,         VaadinIcon.MODAL.create()),
                        new SideNavItem("Popover",        PopoverDemoView.class,        VaadinIcon.COMMENT.create()),
                        new SideNavItem("Tooltip",        TooltipDemoView.class,        VaadinIcon.INFO_CIRCLE.create()),
                        new SideNavItem("Empty",          EmptyDemoView.class,          VaadinIcon.INBOX.create()),
                        new SideNavItem("BulkItemPicker", BulkItemPickerDemoView.class, VaadinIcon.PLUS_CIRCLE.create())
                )
                .add();

        // Content & Display
        nav.withNavItem("Content & Display")
                .prefixComponent(VaadinIcon.FILE_TEXT.create())
                .withItems(
                        new SideNavItem("Highlight",          HighlightDemoView.class,          VaadinIcon.CHART.create()),
                        new SideNavItem("KeyValuePairs",      KeyValuePairsDemoView.class,      VaadinIcon.LIST.create()),
                        new SideNavItem("KeyValueList",       KeyValueListDemoView.class,       VaadinIcon.LIST_UL.create()),
                        new SideNavItem("HeroStrip",          HeroStripDemoView.class,          VaadinIcon.GRID_BIG_O.create()),
                        new SideNavItem("DoubleLabel",        DoubleLabelDemoView.class,        VaadinIcon.TEXT_LABEL.create()),
                        new SideNavItem("PriceList",          PriceListDemoView.class,          VaadinIcon.MONEY.create()),
                        new SideNavItem("TotalsCard",         TotalsCardDemoView.class,         VaadinIcon.FILE_TEXT.create()),
                        new SideNavItem("ListItem",           ListItemDemoView.class,           VaadinIcon.LIST_UL.create()),
                        new SideNavItem("LitRendererBuilder", LitRendererBuilderDemoView.class, VaadinIcon.CODE.create()),
                        new SideNavItem("Header",             HeaderDemoView.class,             VaadinIcon.HEADER.create()),
                        new SideNavItem("MaterialHeader",     MaterialHeaderDemoView.class,     VaadinIcon.HEADER.create()),
                        new SideNavItem("MaterialAppBar",     MaterialAppBarDemoView.class,     VaadinIcon.VIEWPORT.create()),
                        new SideNavItem("GridHeader",         GridHeaderDemoView.class,         VaadinIcon.GRID_BIG.create()),
                        new SideNavItem("GridToolbar",        GridToolbarDemoView.class,        VaadinIcon.TOOLBOX.create()),
                        new SideNavItem("ComponentView",      ComponentViewDemoView.class,      VaadinIcon.EYE.create()),
                        new SideNavItem("Preview",            PreviewDemoView.class,            VaadinIcon.PICTURE.create()),
                        new SideNavItem("Title",              TitleDemoView.class,              VaadinIcon.FONT.create()),
                        new SideNavItem("Label",              LabelDemoView.class,              VaadinIcon.TEXT_LABEL.create())
                )
                .add();

        // Buttons & Controls
        nav.withNavItem("Buttons & Controls")
                .prefixComponent(VaadinIcon.CURSOR.create())
                .withItems(
                        new SideNavItem("Button",       ButtonDemoView.class,      VaadinIcon.CURSOR.create()),
                        new SideNavItem("ButtonGroup",  ButtonGroupDemoView.class, VaadinIcon.SLIDERS.create()),
                        new SideNavItem("NativeButton", NativeButtonDemoView.class,VaadinIcon.HAND.create()),
                        new SideNavItem("ContextMenu",  ContextMenuDemoView.class, VaadinIcon.ELLIPSIS_DOTS_V.create()),
                        new SideNavItem("MenuBar",      MenuBarDemoView.class,     VaadinIcon.MENU.create()),
                        new SideNavItem("TransferList", TransferListDemoView.class,VaadinIcon.ARROWS.create()),
                        new SideNavItem("Fab",          FabDemoView.class,         VaadinIcon.PLUS_CIRCLE.create())
                )
                .add();

        // Rich Components
        nav.withNavItem("Rich Components")
                .prefixComponent(VaadinIcon.CHART_LINE.create())
                .withItems(
                        new SideNavItem("ChartJs",  ChartJsDemoView.class,  VaadinIcon.CHART_LINE.create()),
                        new SideNavItem("Calendar", CalendarDemoView.class, VaadinIcon.CALENDAR.create()),
                        new SideNavItem("Carousel", CarouselDemoView.class, VaadinIcon.PICTURE.create()),
                        new SideNavItem("LiveChat", LiveChatDemoView.class, VaadinIcon.CHAT.create())
                )
                .add();

        // Badges & Indicators
        nav.withNavItem("Badges & Indicators")
                .prefixComponent(VaadinIcon.TAG.create())
                .withItems(
                        new SideNavItem("Badge",       BadgeDemoView.class,       VaadinIcon.TAG.create()),
                        new SideNavItem("IconBadge",   IconBadgeDemoView.class,   VaadinIcon.CIRCLE.create()),
                        new SideNavItem("Tag",         TagDemoView.class,         VaadinIcon.TAGS.create()),
                        new SideNavItem("StatusBadge", StatusBadgeDemoView.class, VaadinIcon.DOT_CIRCLE.create()),
                        new SideNavItem("ArAgingBar",  ArAgingBarDemoView.class,  VaadinIcon.BAR_CHART.create()),
                        new SideNavItem("Chip",        ChipDemoView.class,        VaadinIcon.FILTER.create()),
                        new SideNavItem("Avatar",      AvatarDemoView.class,      VaadinIcon.USER.create()),
                        new SideNavItem("VaadinIcon",  VaadinIconDemoView.class,  VaadinIcon.STAR.create()),
                        new SideNavItem("Ribbons",     RibbonDemoView.class,      VaadinIcon.BOOKMARK.create())
                )
                .add();

        // Containers
        nav.withNavItem("Containers")
                .prefixComponent(VaadinIcon.COPY.create())
                .withItems(
                        new SideNavItem("Card",                     CardDemoView.class,                     VaadinIcon.COPY.create()),
                        new SideNavItem("Panel",                    PanelDemoView.class,                    VaadinIcon.COG.create()),
                        new SideNavItem("MasterDetailConfigurator", MasterDetailConfiguratorDemoView.class, VaadinIcon.SPLIT.create()),
                        new SideNavItem("Details",       DetailsDemoView.class,       VaadinIcon.ANGLE_DOWN.create()),
                        new SideNavItem("Accordion",     AccordionDemoView.class,     VaadinIcon.ALIGN_JUSTIFY.create()),
                        new SideNavItem("LazyComponent", LazyComponentDemoView.class, VaadinIcon.HOURGLASS.create()),
                        new SideNavItem("Separator",     SeparatorDemoView.class,     VaadinIcon.MINUS.create())
                )
                .add();

        // Patterns
        nav.withNavItem("Patterns")
                .prefixComponent(VaadinIcon.BOLT.create())
                .withItems(
                        new SideNavItem("ViewComponent", ViewComponentDemoView.class, VaadinIcon.SITEMAP.create()),
                        new SideNavItem("Signals",       SignalsDemoView.class,       VaadinIcon.BOLT.create()),
                        new SideNavItem("Sign In",       SignInPageDemoView.class,    VaadinIcon.SIGN_IN.create()),
                        new SideNavItem("Sign Up",       SignUpPageDemoView.class,    VaadinIcon.USER.create()),
                        new SideNavItem("Reset Password", ResetPasswordPageDemoView.class, VaadinIcon.KEY.create()),
                        new SideNavItem("Two Step Verification", TwoStepVerificationPageDemoView.class, VaadinIcon.SHIELD.create())
                )
                .add();

        // Build nav wrapper for the drawer.
        var navWrapper = nav.buildWrapper();

        // Navbar: MaterialAppBar (Material 3 style) colored to match the SideNav's
        // ShellColor.AMBER theme via MaterialAppBar.Color.AMBER.
        var appBar = Components.materialAppBar()
                .color(MaterialAppBar.Color.AMBER)
                .leading(new DrawerToggle(), createNavbarLogo())
                .headline("Holon Components")
                .actions(
                        createSearchField(),
                        createNotificationsButton(),
                        createLanguagesButton(),
                        createThemeToggleButton(),
                        createUserAvatar())
                .build();

        // Assemble shell manually: navbar (MaterialAppBar) + drawer (header + SideNav).
        setPrimarySection(AppLayout.Section.DRAWER);
        addToNavbar(true, appBar);
        addToDrawer(createDrawerHeader(), navWrapper);
    }

    /**
     * Small brand logo for the navbar start slot (20 px, primary colour).
     */
    private static Component createNavbarLogo() {
        var icon = LineAwesomeIcon.CUBES_SOLID.create();
        icon.setSize("20px");
        icon.setColor("var(--lumo-primary-color)");
        return icon;
    }

    private static Component createSearchField() {
        var search = new TextField();
        search.setPlaceholder("Search docs, components\u2026");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());
        search.addClassName("app-bar__search");
        return search;
    }

    private static Component createNotificationsButton() {
        var button = new Button(VaadinIcon.BELL.create());
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        var menu = new ContextMenu(button);
        menu.setOpenOnClick(true);
        menu.addItem("\uD83D\uDD14 New release: Vaadin 25.2");
        menu.addItem("\u2705 Build passed -- 847 tests");
        menu.addItem("\uD83D\uDCE6 3 dependencies outdated");
        return button;
    }

    private static Component createLanguagesButton() {
        var button = new Button(VaadinIcon.GLOBE.create());
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        var menu = new ContextMenu(button);
        menu.setOpenOnClick(true);
        menu.addItem("\uD83C\uDDFA\uD83C\uDDF8 English (US)");
        menu.addItem("\uD83C\uDDE9\uD83C\uDDEA Deutsch");
        menu.addItem("\uD83C\uDDEB\uD83C\uDDF7 Fran\u00E7ais");
        menu.addItem("\uD83C\uDDEF\uD83C\uDDF5 \u65E5\u672C\u8A9E");
        return button;
    }

    private static Component createThemeToggleButton() {
        var button = new Button(VaadinIcon.MOON.create());
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        button.addClickListener(e -> {
            var ui = e.getSource().getUI().orElseThrow();
            var themeList = ui.getElement().getThemeList();
            if (themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
                button.setIcon(VaadinIcon.MOON.create());
            } else {
                themeList.add(Lumo.DARK);
                button.setIcon(VaadinIcon.SUN_O.create());
            }
        });
        return button;
    }

    private static Component createUserAvatar() {
        var avatar = new Avatar("Jane Smith");
        var menu = new ContextMenu(avatar);
        menu.setOpenOnClick(true);
        menu.addItem("Jane Smith");
        menu.addItem("Profile & settings");
        menu.addItem("Switch workspace");
        menu.addItem("Sign out");
        return avatar;
    }

    /**
     * Larger logo block for the drawer header -- shown above the navigation items.
     */
    private static Component createDrawerHeader() {
        var appLogo = LineAwesomeIcon.CUBES_SOLID.create();
        appLogo.setSize("48px");
        appLogo.setColor("var(--lumo-primary-color)");

        var appName = new Span("My Application");
        appName.getStyle().setFontWeight(Style.FontWeight.BOLD);

        var header = new VerticalLayout(appLogo, appName);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        return header;
    }

}
