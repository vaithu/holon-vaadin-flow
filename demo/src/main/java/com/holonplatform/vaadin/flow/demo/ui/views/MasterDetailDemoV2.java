package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.data.entity.Product;
import com.holonplatform.vaadin.flow.demo.data.service.ProductService;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.internal.lumo.FlexDirection;
import com.iyensoft.vaadin.flow.components.KeyValueList;
import com.iyensoft.vaadin.flow.components.ResponsiveDiv;
import com.iyensoft.vaadin.flow.components.*;
import com.iyensoft.vaadin.flow.components.Alert.Variant;
import com.iyensoft.vaadin.flow.components.IconBadge.Size;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.builders.PanelBuilder;
import com.holonplatform.vaadin.flow.components.support.ButtonPreset;
import com.holonplatform.vaadin.flow.components.support.ViewMode;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Master-Detail demo — showcases the two-mode strategy with URL sync:
 *
 * <ul>
 *   <li><b>Desktop</b> — detail panel built <em>eagerly</em> (lazyDetail degrades);
 *       first row is pre-selected on load (or the row matching {@code ?id=}) via
 *       {@link MasterDetailLayout#selectFirst} / {@link MasterDetailLayout#restoreFromUrl};
 *       every row click pushes {@code ?id=<id>} to the browser URL via
 *       {@code history.replaceState} (no navigation round-trip).</li>
 *   <li><b>Mobile</b>  — detail panel built <em>lazily on first tap only</em>;
 *       a right-side {@link Sheet} slides in; URL is never mutated on mobile.</li>
 * </ul>
 *
 * <h3>URL sync lifecycle</h3>
 * <pre>
 *   Initial load (?id absent)   → withAutoSelect() → selectFirst()  → detail populated; URL set to first item's id
 *   Initial load (?id=123)      → withAutoSelect() → restoreFromUrl("123") → that item highlighted + detail populated
 *   Row click                   → syncDetail() → pushUrlState() → URL reflects current selection
 *   Re-navigation (?id changed) → withAutoSelect() re-resolves → restoreFromUrl() or selectFirst()
 * </pre>
 */
@Route(value = "master-detail-v2", layout = DemoMainLayout.class)
public class MasterDetailDemoV2 extends Div {

    private static final Logger log = LoggerFactory.getLogger(MasterDetailDemoV2.class);
    private final transient ProductService productService;
    private Product currentProduct;

    // ── URL sync ──────────────────────────────────────────────────────────────

    /**
     * Injected from the {@code ?id=} query parameter on every navigation.
     */
    /**
     * Desktop layout reference — set when the DESKTOP slot is built by slotOnce.
     * Null on mobile (only the MOBILE slot is ever built on small screens).
     *
     * <p>Used in {@link #syncDetail} as a guard: pushUrlState is only called when
     * this is non-null, keeping the URL clean on mobile.</p>
     */

    // ── Live detail refs ──────────────────────────────────────────────────────

    /*
     * Null until lazyDetail's setup consumer runs.
     *
     * Desktop : assigned during buildLayout() (lazyDetail degrades to eager).
     * Mobile  : assigned on the FIRST grid tap (inside the lazy lambda).
     *
     * syncDetail() is only ever called AFTER the setup consumer, so these
     * are always non-null by the time syncDetail() executes.
     */
    private Avatar avatar;
    private Span headingSpan;
    private BreadcrumbPage currentPage;
    private IconBadge detailCategorySpan;
    private EntityFormPanel<Product> overviewForm;
    private IconBadge detailActiveSpan;
    private KeyValueList overviewKeyValueList;

    // ── Constructor ───────────────────────────────────────────────────────────

    public MasterDetailDemoV2(ProductService productService) {
        this.productService = productService;
        ResponsiveDiv.configure(this)
                .slotOnce(ViewMode.MOBILE, () -> buildLayout(ViewMode.MOBILE))
                .slotOnce(ViewMode.DESKTOP, () -> buildLayout(ViewMode.DESKTOP))
                .fullHeight()
                .build();
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    /**
     * Builds the master-detail layout for the given viewport mode.
     *
     * <p>{@code lazyDetail()} + {@code withMobileSheet()} drive the two behaviours:</p>
     * <ul>
     *   <li>Mobile  → truly lazy; Sheet opens on first tap; URL untouched.</li>
     *   <li>Desktop → degrades to eager; the deferred attach listener triggers the
     *       initial selection (restore from URL or select first row).</li>
     * </ul>
     */
    private MasterDetailLayout<Product> buildLayout(ViewMode viewMode) {

        PanelBuilder tabContentPanelBuilder = Components.panel();


        MasterDetailLayout<Product> layout = Components.masterDetail(Product.class)
                .viewMode(viewMode)
                .withMobileSheet(Sheet.Side.RIGHT)
                // ignored on desktop
                /*
                 * URL sync — desktop only at runtime:
                 *   idExtractor  : Product → "42"  (pushed to ?id= on every click)
                 *   itemLoader   : "42" → Optional<Product>  (used by restoreFromUrl)
                 * pushId() and clearId() are no-ops when viewMode is mobile,
                 * so wiring withUrlSync() for both modes is safe.
                 */
                .withUrlSync(
                        product -> String.valueOf(product.getId()),
                        id -> productService.findById(Long.parseLong(id)))
                .withInitialItem(productService::findFirst)
                .withAutoSelect()
                .master(m -> m
                        .header(h -> h
                                .heading("All Products")
                                .details(masterBadges())
                                .actions(masterActions()))
                        .listing(l -> l
                                .autoCreateColumns(false)
                                .columns("id", "name", "category", "price", "active")
                                .multiSelect()
                                .mobileViewHeader(mobileHeader())
                                .mobileViewColumn(mobileColumn())
                                .search("Search products…")
                                .withFilterPanel()
                                .fetch((q, text, filter, sort) ->
                                               productService.fetch(q.getOffset(), q.getLimit(), text, filter, sort)))
                        .selectionKey(Product::getId))
                .lazyDetail(d -> d
                        .header(h -> h
                                .breadcrumb(detailBreadcrumb())
                                .prefix(detailAvatar())
                                .heading(detailHeading())
                                .details(detailStatus())
                                .actions(
                                        detailActions(viewMode)
                                )
                                .tabs(
                                        Components.lazyTabs()
                                                .withLazyTab("Overview", () -> overviewTab())
                                                .withContainer(tabContentPanelBuilder.build())
                                                .build()
                                )
                        )
                        .content(tabContentPanelBuilder.build())
                        .withDetailSync(this::syncDetail))
                .build();

        return layout;
    }

    private Component[] detailActions(ViewMode viewMode) {
        if (viewMode == ViewMode.DESKTOP) {
            return new Component[]{
                    Components.button().preset(ButtonPreset.EDIT)
                            .small()
                            .onClick(event -> editProduct())
                            .build()
                    ,
                    Components.menuBar()
                            .withThemeVariants(MenuBarVariant.SMALL)
                            .withMenuItem("More")
                            .withSubMenu(s ->
                                                 s.withMenuItem("Clone Product", event -> {})
                                                         .withMenuItem("Mark Inactive", event -> {})


                            )
                            .build()
                    ,
                    Components.button().preset(ButtonPreset.CLOSE)
                            .small()
                            .error()
                            .build()
            };
        } else {
            return new Component[]{
                    Components.menuBar()
                            .withThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE)
                            .withMenuItem(VaadinIcon.ELLIPSIS_DOTS_V.create())
                            .withSubMenu(s ->
                                                 s.withMenuItem(LumoIcon.EDIT.create(),"Edit", event -> {})
                                                         .withMenuItem("Clone Product", event -> {})
                                                         .withMenuItem("Mark Inactive", event -> {})

                            )

                            .build()
            };
        }
    }

    private Component overviewTab() {
        overviewKeyValueList = Components.keyValueList();

//        return new Button("dskfjsldkfj");

        return overviewKeyValueList.getContent();
    }

    private void editProduct() {

    }

    // ── Navigation lifecycle ──────────────────────────────────────────────────

    // ── Master panel helpers ──────────────────────────────────────────────────

    private Component masterActions() {
        return Components.button().preset(ButtonPreset.NEW)
                .small()
                .build();
    }

    private Component masterBadges() {
        return Components.hl().spacing()
                .addToStart(
                        Components.iconBadge().size(Size.SM).variant(Variant.SUCCESS).text("All").build(),
                        Components.iconBadge().size(Size.SM).variant(Variant.DESTRUCTIVE).text("Unavailable").build())
                .build();
    }

    private Component mobileHeader() {
        return Components.hl()
                .addToStart(new Span("Product Name"))
                .addToEnd(new Span("Price"))
                .build();
    }

    private LitRenderer<Product> mobileColumn() {
        return Components.<Product>mobileGridColumnLit()
                .flexDirection(FlexDirection.ROW)
                .withAvatarAsPrimary(Product::getName)
                .withSecondaryText(Product::getName)
                .withTertiaryText(p -> String.valueOf(p.getPrice()))
                .build();
    }

    // ── Detail panel helpers ──────────────────────────────────────────────────
    //
    // Desktop : called at buildLayout() time (eager degradation of lazyDetail).
    // Mobile  : called on the FIRST grid tap (genuinely lazy).
    //
    // In both cases they execute BEFORE syncDetail() fires on the same dispatch
    // cycle, so avatar/headingSpan/currentPage are never null inside syncDetail().

    private Breadcrumb detailBreadcrumb() {
        currentPage = new BreadcrumbPage("—");
        return Components.breadcrumb()
                .addWithSeparators(
                        new BreadcrumbItem("Home", IndexView.class),
                        new BreadcrumbItem("Products", MasterDetailDemoV2.class),
                        currentPage)
                .build();
    }

    private Avatar detailAvatar() {
        return avatar = Components.avatar("?")
                .withThemeVariants(AvatarVariant.XLARGE)
                .build();
    }

    private Span detailHeading() {
        return headingSpan = new Span("Select a product");
    }

    private Component[] detailStatus() {
        return new Component[]{
                detailCategorySpan = Components.iconBadge().size(Size.XS).variant(Variant.SUCCESS).build()
                ,
                detailActiveSpan = Components.iconBadge().size(Size.XS).variant(Variant.SUCCESS).build()
        };
    }

    // ── Sync handler ─────────────────────────────────────────────────────────

    /**
     * Called by the framework on every row selection (click or selectFirst/restoreFromUrl).
     *
     * <p>Updates all live detail component refs (avatar, heading, breadcrumb). The
     * {@code ?id=} URL state is pushed by the layout itself as part of the selection, so
     * nothing needs to be done here for it.</p>
     */
    private void syncDetail(Product product) {
        this.currentProduct = product;

        avatar.setName(product.getName());
        headingSpan.setText(product.getName());
        currentPage.setText(product.getName());
        detailCategorySpan.setText(product.getCategory());

        if (product.isActive()) {
            detailActiveSpan.setVariant(Variant.INFO);
            detailActiveSpan.setText("Active");
        } else {
            detailActiveSpan.setVariant(Variant.DESTRUCTIVE);
            detailActiveSpan.setText("Inactive");
        }

        overviewKeyValueList.asFields()
                .addFromBean(product, "category", "name", "active");

    }
}
