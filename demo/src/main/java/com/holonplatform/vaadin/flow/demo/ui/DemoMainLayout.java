package com.holonplatform.vaadin.flow.demo.ui;

import com.holonplatform.vaadin.flow.demo.ui.views.*;
import com.holonplatform.vaadin.flow.vaadinplus.components.AppBar;
import com.iyensoft.vaadin.flow.components.builders.SideNavBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
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
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoIcon;

/**
 * Root application layout — shared by every demo view.
 *
 * <p>Uses {@link AppLayout} natively:
 * <ul>
 *   <li>Navbar  → {@link AppBar} (logo / global search / user actions)</li>
 *   <li>Drawer  → {@link SideNavBuilder#buildWrapper()} (sidenav-host, styled by menu.css)</li>
 *   <li>Content → AppLayout's {@code <main>} slot (single scroller, no wrapper div)</li>
 * </ul>
 *
 * <p>No custom CSS classes are assigned. All visual structure comes from the component
 * library's own stylesheets: {@code app-bar.css}, {@code menu.css}, and AppLayout shadow parts.
 */
public final class DemoMainLayout extends AppLayout {

    DemoMainLayout() {
        // ── Top bar ─────────────────────────────────────────────────────────
        var appBar = new AppBar();

        // Start: logo / home link
        var homeLink = new RouterLink("", IndexView.class);
        homeLink.add(new Span("Holon Components"));
        appBar.addToStart(homeLink, new Span("v10"));

        // Middle: global search
        var globalSearch = new TextField();
        globalSearch.setPlaceholder("Search docs, components…");
        globalSearch.setPrefixComponent(VaadinIcon.SEARCH.create());
        globalSearch.addClassName("app-bar__search");
        appBar.addToMiddle(globalSearch);

        // End: notifications ─────────────────────────────────
        var notifBtn = new Button(VaadinIcon.BELL.create());
        notifBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        notifBtn.addClassName("app-bar__action-btn");
        notifBtn.getElement().setAttribute("data-badge", "3");  // CSS ::after badge, no JS
        var notifMenu = new ContextMenu(notifBtn);
        notifMenu.setOpenOnClick(true);
        notifMenu.addItem("🔔 New release: Vaadin 25.2");
        notifMenu.addItem("✅ Build passed — 847 tests");
        notifMenu.addItem("📦 3 dependencies outdated");

        // End: language selector ─────────────────────────────
        var langBtn = new Button(VaadinIcon.GLOBE.create());
        langBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        langBtn.addClassName("app-bar__action-btn");
        var langMenu = new ContextMenu(langBtn);
        langMenu.setOpenOnClick(true);
        langMenu.addItem("🇺🇸 English (US)");
        langMenu.addItem("🇩🇪 Deutsch");
        langMenu.addItem("🇫🇷 Français");
        langMenu.addItem("🇯🇵 日本語");

        // End: theme toggle ───────────────────────────────────
        var themeBtn = new Button(VaadinIcon.MOON.create());
        themeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        themeBtn.addClassName("app-bar__action-btn");
        var darkMode = new boolean[]{false};
        themeBtn.addClickListener(e -> {
            darkMode[0] = !darkMode[0];
            themeBtn.setIcon(darkMode[0] ? VaadinIcon.SUN_O.create() : VaadinIcon.MOON.create());
            themeBtn.getUI().ifPresent(ui -> {
                if (darkMode[0]) {
                    ui.getElement().getThemeList().add("dark");
                    ui.getPage().executeJs("document.documentElement.style.colorScheme='dark';");
                } else {
                    ui.getElement().getThemeList().remove("dark");
                    ui.getPage().executeJs("document.documentElement.style.colorScheme='light';");
                }
            });
        });

        // End: user avatar with profile menu ──────────────────
        var userAvatar = new Avatar("Jane Smith");
        var userMenu = new ContextMenu(userAvatar);
        userMenu.setOpenOnClick(true);
        userMenu.addItem("Jane Smith");
        userMenu.addItem("Profile & settings");
        userMenu.addItem("Switch workspace");
        userMenu.addItem("Sign out");

        appBar.addToEnd(notifBtn, langBtn, themeBtn, userAvatar);

        // ── Sidebar nav ──────────────────────────────────────────────────────
        var nav = SideNavBuilder.create()
                .withSearch("Filter components…")
                .withCollapse();

        // ── Layout & Structure ────────────────────────────────────────────────
        nav.withNavItem("Layout & Structure")
                .prefixComponent(VaadinIcon.GRID_V.create())
                .withItems(
                        new SideNavItem("Layout",       LayoutDemoView.class,      VaadinIcon.GRID_V.create()),
                        new SideNavItem("SplitLayout",  SplitLayoutDemoView.class, VaadinIcon.SPLIT.create()),
                        new SideNavItem("Sheet",        SheetDemoView.class,       VaadinIcon.CLIPBOARD_TEXT.create()),
                        new SideNavItem("Scroller",     ScrollerDemoView.class,    VaadinIcon.RESIZE_V.create()),
                        new SideNavItem("RowBuilder",   RowColumnBuilderDemoView.class, VaadinIcon.GRID_H.create()),
                        new SideNavItem("VaadinLayouts",VaadinLayoutsDemoView.class, VaadinIcon.CUBES.create()),
                        new SideNavItem("Div",          DivDemoView.class,         VaadinIcon.ANGLE_RIGHT.create())
                )
                .add();

        // ── Responsive ───────────────────────────────────────────────────────
        nav.withNavItem("Responsive")
                .prefixComponent(VaadinIcon.MOBILE.create())
                .withItems(
                        new SideNavItem("ResponsiveDiv",  ResponsiveDivDemoView.class,  VaadinIcon.RESIZE_V.create()),
                        new SideNavItem("ResponsiveDivSlot",  ResponsiveDivSlotDemoView.class,  VaadinIcon.RESIZE_V.create()),
                        new SideNavItem("ViewMode",       ViewModeDemoView.class,       VaadinIcon.DESKTOP.create())
                )
                .add();

        // ── Navigation ───────────────────────────────────────────────────────
        nav.withNavItem("Navigation")
                .prefixComponent(VaadinIcon.MENU.create())
                .withItems(
                        new SideNavItem("AppBar",          AppBarDemoView.class,       VaadinIcon.TRENDING_UP.create()),
                        new SideNavItem("Breadcrumb",      BreadcrumbDemoView.class,   VaadinIcon.ARROW_RIGHT.create()),
                        new SideNavItem("SideNav",         SideNavDemoView.class,      VaadinIcon.MENU.create()),
                        new SideNavItem("Pagination",      PaginationDemoView.class,   VaadinIcon.ELLIPSIS_DOTS_H.create()),
                        new SideNavItem("PageSizeSelector",PageSizeSelectorDemoView.class, VaadinIcon.LIST.create())
                )
                .add();

        // ── Tabs & Steppers ──────────────────────────────────────────────────
        nav.withNavItem("Tabs & Steppers")
                .prefixComponent(VaadinIcon.FOLDER.create())
                .withItems(
                        new SideNavItem("Tab",            TabsDemoView.class,       VaadinIcon.FOLDER.create()),
                        new SideNavItem("LazyTabs",       LazyTabsDemoView.class,   VaadinIcon.FOLDER_O.create()),
                        new SideNavItem("TabSheet",       TabSheetDemoView.class,   VaadinIcon.FILE.create()),
                        new SideNavItem("FlowStepper",    FlowStepperDemoView.class,  VaadinIcon.PROGRESSBAR.create()),
                        new SideNavItem("TimelineStepper",TimelineStepperDemoView.class, VaadinIcon.CLOCK.create())
                )
                .add();

        // ── Data & Listings ──────────────────────────────────────────────────
        nav.withNavItem("Data & Listings")
                .prefixComponent(VaadinIcon.TABLE.create())
                .withItems(
                        new SideNavItem("Product CRUD (JPA)",ProductCrudDemoView.class,   VaadinIcon.DATABASE.create()),
                        new SideNavItem("BeanListing",    BeanListingDemoView.class,     VaadinIcon.TABLE.create()),
                        new SideNavItem("PropertyListing",PropertyListingDemoView.class, VaadinIcon.TABLE.create()),
                        new SideNavItem("KanbanBoard",    KanbanBoardDemoView.class,     VaadinIcon.TASKS.create()),
                        new SideNavItem("LineItemGrid",   LineItemGridDemoView.class,    VaadinIcon.GRID_BIG_O.create()),
                        new SideNavItem("PawnItemGrid",   PawnItemGridDemoView.class,    VaadinIcon.MONEY.create()),
                        new SideNavItem("ListingBundle",  ListingBundleDemoView.class,   VaadinIcon.DATABASE.create()),
                        new SideNavItem("FilterPanel",    FilterPanelDemoView.class,     VaadinIcon.FILTER.create()),
                        new SideNavItem("Chart of Accounts", ChartOfAccountsDemoView.class, VaadinIcon.BOOK.create()),
                        new SideNavItem("MasterDetail (Products)", MasterDetailDemoV2.class,      VaadinIcon.SPLIT.create()),
                        new SideNavItem("MasterDetail (Bills AP)", BillsMasterDetailView.class,   VaadinIcon.FILE_TEXT.create()),
                        new SideNavItem("MasterDetail (Orders)",   OrdersMasterDetailView.class,  VaadinIcon.CART.create())
                )
                .add();

        // ── Forms & Input ────────────────────────────────────────────────────
        nav.withNavItem("Forms & Input")
                .prefixComponent(LumoIcon.EDIT.create())
                .withItems(
                        new SideNavItem("EntityFormPanel",  EntityFormPanelDemoView.class,  VaadinIcon.FORM.create()),
                        new SideNavItem("BeanInputForm",    BeanInputFormDemoView.class,    LumoIcon.EDIT.create()),
                        new SideNavItem("PropertyInputForm",PropertyInputFormDemoView.class, VaadinIcon.PENCIL.create()),
                        new SideNavItem("CollabFormSupport",CollaborationFormSupportDemoView.class, VaadinIcon.USERS.create()),
                        new SideNavItem("Inputs",           InputsDemoView.class,           VaadinIcon.KEYBOARD.create()),
                        new SideNavItem("InputGroup",       InputGroupDemoView.class,       VaadinIcon.LIST_UL.create()),
                        new SideNavItem("InputOTP",         InputOTPDemoView.class,         VaadinIcon.LOCK.create()),
                        new SideNavItem("IntegerField",     IntegerFieldDemoView.class,     VaadinIcon.PLUS.create()),
                        new SideNavItem("NumberField",      NumberFieldDemoView.class,      VaadinIcon.HASH.create()),
                        new SideNavItem("MultiSelect",      MultiSelectDemoView.class,      VaadinIcon.CHECK_SQUARE_O.create()),
                        new SideNavItem("SingleSelect",     SingleSelectDemoView.class,     VaadinIcon.DOT_CIRCLE.create()),
                        new SideNavItem("Select",           SelectDemoView.class,           VaadinIcon.ANGLE_DOWN.create()),
                        new SideNavItem("ValidatableInput", ValidatableInputDemoView.class, VaadinIcon.CHECK.create()),
                        new SideNavItem("FormLayout",       FormLayoutDemoView.class,       VaadinIcon.FORM.create())
                )
                .add();

        // ── Feedback & Overlay ───────────────────────────────────────────────
        nav.withNavItem("Feedback & Overlay")
                .prefixComponent(VaadinIcon.BELL.create())
                .withItems(
                        new SideNavItem("Alert",       AlertDemoView.class,       VaadinIcon.WARNING.create()),
                        new SideNavItem("AlertDialog", AlertDialogDemoView.class, VaadinIcon.EXCLAMATION_CIRCLE.create()),
                        new SideNavItem("AlertModal",  AlertModalDemoView.class,  VaadinIcon.EXCLAMATION.create()),
                        new SideNavItem("Notification",NotificationDemoView.class,VaadinIcon.BELL.create()),
                        new SideNavItem("Dialog",      DialogDemoView.class,      VaadinIcon.MODAL.create()),
                        new SideNavItem("Popover",     PopoverDemoView.class,     VaadinIcon.COMMENT.create()),
                        new SideNavItem("Tooltip",     TooltipDemoView.class,     VaadinIcon.INFO_CIRCLE.create()),
                        new SideNavItem("Empty",       EmptyDemoView.class,       VaadinIcon.INBOX.create()),
                        new SideNavItem("BulkItemPicker", BulkItemPickerDemoView.class, VaadinIcon.PLUS_CIRCLE.create())
                )
                .add();

        // ── Content & Display ────────────────────────────────────────────────
        nav.withNavItem("Content & Display")
                .prefixComponent(VaadinIcon.FILE_TEXT.create())
                .withItems(
                        new SideNavItem("Highlight",     HighlightDemoView.class,    VaadinIcon.CHART.create()),
                        new SideNavItem("KeyValuePairs", KeyValuePairsDemoView.class,VaadinIcon.LIST.create()),
                        new SideNavItem("KeyValueList",  KeyValueListDemoView.class, VaadinIcon.LIST_UL.create()),
                        new SideNavItem("DoubleLabel",   DoubleLabelDemoView.class,  VaadinIcon.TEXT_LABEL.create()),
                        new SideNavItem("PriceList",     PriceListDemoView.class,    VaadinIcon.MONEY.create()),
                        new SideNavItem("ListItem",          ListItemDemoView.class,            VaadinIcon.LIST_UL.create()),
                        new SideNavItem("LitRendererBuilder", LitRendererBuilderDemoView.class,  VaadinIcon.CODE.create()),
                        new SideNavItem("Header",        HeaderDemoView.class,       VaadinIcon.HEADER.create()),
                        new SideNavItem("GridHeader",    GridHeaderDemoView.class,   VaadinIcon.GRID_BIG.create()),
                        new SideNavItem("ComponentView", ComponentViewDemoView.class,VaadinIcon.EYE.create()),
                        new SideNavItem("Preview",       PreviewDemoView.class,      VaadinIcon.PICTURE.create()),
                        new SideNavItem("Title",         TitleDemoView.class,        VaadinIcon.FONT.create()),
                        new SideNavItem("Label",         LabelDemoView.class,        VaadinIcon.TEXT_LABEL.create())
                )
                .add();

        // ── Buttons & Controls ───────────────────────────────────────────────
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

        // ── Rich Components ──────────────────────────────────────────────────
        nav.withNavItem("Rich Components")
                .prefixComponent(VaadinIcon.CHART_LINE.create())
                .withItems(
                        new SideNavItem("ChartJs",   ChartJsDemoView.class,   VaadinIcon.CHART_LINE.create()),
                        new SideNavItem("Calendar",  CalendarDemoView.class,  VaadinIcon.CALENDAR.create()),
                        new SideNavItem("Carousel",  CarouselDemoView.class,  VaadinIcon.PICTURE.create()),
                        new SideNavItem("LiveChat",  LiveChatDemoView.class,  VaadinIcon.CHAT.create())
                )
                .add();

        // ── Badges & Indicators ──────────────────────────────────────────────
        nav.withNavItem("Badges & Indicators")
                .prefixComponent(VaadinIcon.TAG.create())
                .withItems(
                        new SideNavItem("Badge",          BadgeDemoView.class,         VaadinIcon.TAG.create()),
                        new SideNavItem("IconBadge",      IconBadgeDemoView.class,     VaadinIcon.CIRCLE.create()),
                        new SideNavItem("Tag",            TagDemoView.class,           VaadinIcon.TAGS.create()),
                        new SideNavItem("StatusBadge",    StatusBadgeDemoView.class,   VaadinIcon.DOT_CIRCLE.create()),
                        new SideNavItem("Chip",           ChipDemoView.class,          VaadinIcon.FILTER.create()),
                        new SideNavItem("Avatar",         AvatarDemoView.class,        VaadinIcon.USER.create()),
                        new SideNavItem("MaterialSymbol", MaterialSymbolDemoView.class,VaadinIcon.STAR.create())
                )
                .add();

        // ── Containers ───────────────────────────────────────────────────────
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

        // ── Patterns ─────────────────────────────────────────────────────────
        nav.withNavItem("Patterns")
                .prefixComponent(VaadinIcon.BOLT.create())
                .withItems(
                        new SideNavItem("ViewComponent",      ViewComponentDemoView.class,      VaadinIcon.SITEMAP.create()),
                        new SideNavItem("Signals",            SignalsDemoView.class,            VaadinIcon.BOLT.create())
                )
                .add();

        // AppLayout slots — no extra wrappers or CSS classes needed:
        // • AppBar loads app-bar.css for its own layout
        // • buildWrapper() produces a sidenav-host div styled by menu.css
        // • AppLayout's <main> is the single content scroller (no contentSlot wrapper)
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, appBar);
        addToDrawer(createHeader(), nav.buildWrapper());
    }

    private Component createHeader() {
        // TODO Replace with real application logo and name
        var appLogo = VaadinIcon.CUBES.create();
        appLogo.setSize("48px");
        appLogo.setColor("green");

        var appName = new Span("My Application");
        appName.getStyle().setFontWeight(Style.FontWeight.BOLD);

        var header = new VerticalLayout(appLogo, appName);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        return header;
    }
}
