package com.holonplatform.vaadin.flow.demo.ui;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.views.*;
import com.iyensoft.vaadin.flow.components.builders.SideNavBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.theme.lumo.LumoIcon;

/**
 * Root application layout -- shared by every demo view.
 *
 * <p>Built entirely via {@link Components#appShell()} -- see
 * {@link com.iyensoft.vaadin.flow.components.builders.AppShellLayoutBuilder} for the full API.
 *
 * <p>Demonstrates the three new AppShellLayoutBuilder features:
 * <ol>
 *   <li><b>Brand logo</b> -- small icon before the brand name in the navbar start slot.</li>
 *   <li><b>DrawerToggle</b> -- hamburger button in the navbar start slot (default; shown explicitly).</li>
 *   <li><b>Desktop MenuBar flip</b> -- grid-icon button in the navbar end slot that switches
 *       between sidebar and a horizontal MenuBar in the navbar middle slot.</li>
 * </ol>
 */
public final class DemoMainLayout extends AppLayout {

    DemoMainLayout() {

        // Sidebar nav
        var nav = SideNavBuilder.create()
                .withSearch("Filter components\u2026")
                .withCollapse();

        // Layout & Structure
        nav.withNavItem("Layout & Structure")
                .prefixComponent(VaadinIcon.GRID_V.create())
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
                        new SideNavItem("ViewMode",          ViewModeDemoView.class,          VaadinIcon.DESKTOP.create())
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
                        new SideNavItem("ListItem",           ListItemDemoView.class,           VaadinIcon.LIST_UL.create()),
                        new SideNavItem("LitRendererBuilder", LitRendererBuilderDemoView.class, VaadinIcon.CODE.create()),
                        new SideNavItem("Header",             HeaderDemoView.class,             VaadinIcon.HEADER.create()),
                        new SideNavItem("GridHeader",         GridHeaderDemoView.class,         VaadinIcon.GRID_BIG.create()),
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
                        new SideNavItem("TransferList", TransferListDemoView.class,VaadinIcon.ARROWS.create())
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
                        new SideNavItem("VaadinIcon",  VaadinIconDemoView.class,  VaadinIcon.STAR.create())
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
                        new SideNavItem("Signals",       SignalsDemoView.class,       VaadinIcon.BOLT.create())
                )
                .add();

        // Build nav: get both the SideNav reference (for MenuBar conversion) and
        // the wrapper Div (for the drawer). build() applies post-processors and returns
        // the SideNav; buildWrapper() wraps that same instance in the sidenav-host Div.
        var sideNav    = nav.build();
        var navWrapper = nav.buildWrapper();

        // Assemble shell
        Components.appShell()
                // Feature 1: Brand logo -- small icon rendered before the brand name.
                .navbarBrandLogo(createNavbarLogo())
                .navbarBrand("Holon Components", "v10", IndexView.class)
                // Feature 2: DrawerToggle -- hamburger auto-injected (default; shown explicitly).
                .drawerToggle(true)
                .search("Search docs, components\u2026")
                .notifications(3,
                        "\uD83D\uDD14 New release: Vaadin 25.2",
                        "\u2705 Build passed -- 847 tests",
                        "\uD83D\uDCE6 3 dependencies outdated")
                .languages(
                        "\uD83C\uDDFA\uD83C\uDDF8 English (US)",
                        "\uD83C\uDDE9\uD83C\uDDEA Deutsch",
                        "\uD83C\uDDEB\uD83C\uDDF7 Fran\u00E7ais",
                        "\uD83C\uDDEF\uD83C\uDDF5 \u65E5\u672C\u8A9E")
                .themeToggle()
                .user(u -> u
                        .name("Jane Smith")
                        .menu(m -> m
                                .item("Profile & settings")
                                .item("Switch workspace")
                                .item("Sign out")))
                .drawerBrand(createDrawerHeader())
                // Feature 3: Desktop MenuBar flip -- nav(wrapper, sideNav) gives the builder
                //             the SideNav items for conversion; desktopMenuBar() adds the
                //             grid-icon toggle button to the navbar end slot.
                //             To hide the button on mobile:
                //             @media(max-width:768px){.app-bar__layout-toggle{display:none!important}}
                .nav(navWrapper, sideNav)
                .desktopMenuBar()
                .configure(this);
    }

    /**
     * Small brand logo for the navbar start slot (20 px, primary colour).
     * Feature 1 demo: rendered immediately before the brand name.
     */
    private static Component createNavbarLogo() {
        var icon = VaadinIcon.CUBES.create();
        icon.setSize("20px");
        icon.setColor("var(--lumo-primary-color)");
        return icon;
    }

    /**
     * Larger logo block for the drawer header -- shown above the navigation items.
     */
    private static Component createDrawerHeader() {
        var appLogo = VaadinIcon.CUBES.create();
        appLogo.setSize("48px");
        appLogo.setColor("var(--lumo-primary-color)");

        var appName = new Span("My Application");
        appName.getStyle().setFontWeight(Style.FontWeight.BOLD);

        var header = new VerticalLayout(appLogo, appName);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        return header;
    }
}