package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.builders.IyenMasterBuilder;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Default implementation of {@link MasterDetailBuilder}.
 *
 * <p>Assembles the master and detail panels using plain {@link Layout} instances
 * styled with BEM class names from {@code master-details.css} and the new
 * {@code master-detail-layout.css}. No Lumo tokens or inline styles are used.</p>
 *
 * @param <T> the item type
 */
public class DefaultMasterDetailBuilder<T> implements MasterDetailBuilder<T> {

    // -------------------------------------------------------------------------
    // Master configuration
    // -------------------------------------------------------------------------
    private Header masterHeaderComponent;
    private Component masterToolbarComponent;
    private Component masterFooterComponent;
    private TextField masterSearchField;
    private Button[] masterSearchActions = new Button[0];
    private Grid<T> masterGrid;

    // -------------------------------------------------------------------------
    // Detail configuration
    // -------------------------------------------------------------------------
    private Header detailHeaderComponent;
    private Breadcrumb detailBreadcrumbsComponent;
    private Tabs detailMenuTabs;
    private MenuBar detailMenuMenuBar;
    private Function<T, Component[]> detailContentProvider;
    private Component[] detailFooterComponents = new Component[0];

    // -------------------------------------------------------------------------
    // URL sync
    // -------------------------------------------------------------------------
    private Function<T, String> idExtractor;
    private Function<String, Optional<T>> itemLoader;

    // -------------------------------------------------------------------------
    // Mobile sheet
    // -------------------------------------------------------------------------
    private String mobileSheetTitle;

    // -------------------------------------------------------------------------
    // Data change listeners
    // -------------------------------------------------------------------------
    private final List<Runnable> dataChangedListeners = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Auto-select
    // -------------------------------------------------------------------------
    private boolean autoSelectFirst = false;

    // -------------------------------------------------------------------------
    // Detail sync handlers
    // -------------------------------------------------------------------------
    // Stored as Map.Entry<owner, handler> pairs; wired post-build via withDetailSync().
    private final List<Map.Entry<Component, Consumer<T>>> syncHandlers = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Builder methods
    // -------------------------------------------------------------------------

    @Override
    public MasterDetailBuilder<T> masterHeader(Header header) {
        if (header != null) this.masterHeaderComponent = header;
        return this;
    }

    @Override
    public MasterDetailBuilder<T> masterToolbar(Component toolbar) {
        if (toolbar != null) this.masterToolbarComponent = toolbar;
        return this;
    }

    @Override
    public MasterDetailBuilder<T> masterFooter(Component footer) {
        if (footer != null) this.masterFooterComponent = footer;
        return this;
    }

    @Override
    public MasterDetailBuilder<T> masterGrid(Grid<T> grid) {
        this.masterGrid = grid;
        return this;
    }

    @Override
    public MasterDetailBuilder<T> masterGrid(BeanListing<T> listing) {
        return masterGrid((ItemListing<T, ?>) listing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MasterDetailBuilder<T> masterGrid(ItemListing<T, ?> listing) {
        // AbstractItemListing.getComponent() returns getGrid() — safe cast
        this.masterGrid = (Grid<T>) listing.getComponent();
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MasterDetailBuilder<T> masterGrid(PropertyListing listing) {
        // PropertyListing<PropertyBox> — caller must ensure T == PropertyBox
        this.masterGrid = (Grid<T>) listing.getComponent();
        return this;
    }

    @Override
    public MasterDetailBuilder<T> masterGrid(ListingBundle<T> bundle) {
        Objects.requireNonNull(bundle, "bundle must not be null");
        // ── 1. Grid-header / toolbar slot (above the grid, inside content) ───
        // The bundle exposes EITHER a GridHeader (when built with gridHeader(title))
        // OR a legacy toolbar Div — never both: if a gridHeader is configured the
        // bundle returns a hidden empty toolbar. We do NOT route the bundle's
        // GridHeader into the panel-level masterHeader slot, so any previously
        // configured masterHeader(...) is preserved and BOTH render together.
        Header gridHeader = bundle.header();
        if (gridHeader != null) {
            masterToolbar(gridHeader);
        } else {
            com.vaadin.flow.component.html.Div toolbar = bundle.toolbar();
            if (toolbar.isVisible()) {
                masterToolbar(toolbar);
            }
        }
        // ── 2. Footer (pagination bar) ───────────────────────────────────
        // Cached inside the bundle. Hidden by default; auto-shows when the
        // user switches to paginated mode via the bundle's options menu.
        masterFooter(bundle.footer());
        // ── 3. Listing → grid (mandatory) ────────────────────────────────
        return masterGrid(bundle.listing());
    }

    @Override
    public MasterDetailBuilder<T> detailHeader(Header header) {
        this.detailHeaderComponent = header;
        return this;
    }

    @Override
    public MasterDetailBuilder<T> detailBreadcrumbs(Breadcrumb breadcrumbs) {
        this.detailBreadcrumbsComponent = breadcrumbs;
        return this;
    }

    @Override
    public MasterDetailBuilder<T> detailMenu(Tabs tabs) {
        this.detailMenuTabs = tabs;
        return this;
    }

    @Override
    public MasterDetailBuilder<T> detailMenu(MenuBar menuBar) {
        this.detailMenuMenuBar = menuBar;
        return this;
    }

    @Override
    public MasterDetailBuilder<T> detailContent(Function<T, Component[]> contentProvider) {
        this.detailContentProvider = contentProvider;
        return this;
    }

    @Override
    public MasterDetailBuilder<T> detailFooter(Component... footer) {
        this.detailFooterComponents = footer != null ? footer : new Component[0];
        return this;
    }

    @Override
    public MasterDetailBuilder<T> itemId(Function<T, String> idExtractor,
                                          Function<String, Optional<T>> itemLoader) {
        this.idExtractor = idExtractor;
        this.itemLoader = itemLoader;
        return this;
    }

    @Override
    public MasterDetailBuilder<T> mobileSheetTitle(String title) {
        this.mobileSheetTitle = title;
        return this;
    }

    @Override
    public MasterDetailBuilder<T> onDataChanged(Runnable listener) {
        if (listener != null) dataChangedListeners.add(listener);
        return this;
    }

    @Override
    public MasterDetailBuilder<T> autoSelectFirst(boolean autoSelect) {
        this.autoSelectFirst = autoSelect;
        return this;
    }

    @Override
    public MasterDetailBuilder<T> withDetailSync(Component owner, Consumer<T> handler) {
        Objects.requireNonNull(owner,   "withDetailSync: owner must not be null");
        Objects.requireNonNull(handler, "withDetailSync: handler must not be null");
        syncHandlers.add(Map.entry(owner, handler));
        return this;
    }

    // -------------------------------------------------------------------------
    // Build
    // -------------------------------------------------------------------------

    @Override
    public MasterDetailLayout<T> build() {
        Objects.requireNonNull(masterGrid, "masterGrid is mandatory");
        Objects.requireNonNull(detailContentProvider, "detailContent is mandatory");

        IyenMasterBuilder masterBuilder = buildMaster();
        Layout detailContainer = buildDetailContainer();
        Layout dynamicContentSlot = buildDynamicSlot(detailContainer);

        // Static footer — appended after the scrollable slot so it stays pinned at bottom
        if (detailFooterComponents.length > 0) {
            Layout footer = new Layout(detailFooterComponents);
            footer.addClassName("iyen-detail-footer");
            detailContainer.add(footer);
        }

        MasterDetailLayout<T> layout = MasterDetailLayout.create(
                masterBuilder,
                detailContainer,
                dynamicContentSlot,
                masterGrid,
                detailContentProvider,
                idExtractor,
                itemLoader,
                mobileSheetTitle,
                dataChangedListeners,
                autoSelectFirst);

        // Wire pre-registered sync effects against the built layout.
        syncHandlers.forEach(e -> layout.withDetailSync(e.getKey(), e.getValue()));

        return layout;
    }

    // -------------------------------------------------------------------------
    // Internal assembly
    // -------------------------------------------------------------------------

    /**
     * Builds the master panel as an {@link IyenMasterBuilder} containing
     * the optional header, optional toolbar (page-size selector + search +
     * filter options when wired from a {@link ListingBundle}), the mandatory
     * grid, and an optional footer (pagination bar).
     *
     * <p>Uses CSS class names from {@code master-details.css}:
     * {@code iyen-master}, {@code iyen-master-header}, {@code iyen-master-toolbar},
     * {@code iyen-master-content}, {@code iyen-master-footer}, {@code mdl-master-grid}.</p>
     *
     * <p>Layout structure produced:</p>
     * <pre>
     * .iyen-master
     * ├── .iyen-master-header   (optional)
     * ├── .iyen-master-content
     * │   ├── .iyen-master-toolbar  (optional — bundle.toolbar() OR search field wrap)
     * │   └── .mdl-master-grid      (mandatory)
     * └── .iyen-master-footer   (optional — bundle.footer())
     * </pre>
     *
     * <p>The toolbar (if any) and the grid are wrapped in a single
     * {@code iyen-master-content} container. On desktop the layout switches to
     * CSS Grid with two rows (header / content); both panels share the same
     * header-row height via {@code grid-template-rows: subgrid}, keeping the
     * header bottom-borders horizontally aligned.</p>
     */
    private IyenMasterBuilder buildMaster() {
        Layout masterLayout = new Layout();

        if (masterHeaderComponent != null) {
            // masterHeaderComponent.addClassName("iyen-master-header");
            masterLayout.add(masterHeaderComponent);
        }

        // Wrap toolbar + grid in a single content area so each panel has exactly
        // two children (header + content) — required for CSS subgrid row alignment.
        Layout contentArea = Components.layout().styleName("iyen-master-content").build();

        // Toolbar precedence:
        //   1. masterToolbarComponent (typically wired from a ListingBundle)
        //   2. masterSearchField wrap (legacy programmatic search)
        if (masterToolbarComponent != null) {
            masterToolbarComponent.addClassName("iyen-master-toolbar");
            contentArea.add(masterToolbarComponent);
        } else if (masterSearchField != null) {
            Layout toolbar = new Layout(masterSearchField);
            toolbar.addClassName("toolbar");
            toolbar.addClassName("iyen-master-toolbar");
            if (masterSearchActions.length > 0) {
                for (Button btn : masterSearchActions) {
                    btn.addClassName("toolbar__btn--constrained");
                }
                Layout btnRow = new Layout(masterSearchActions);
                btnRow.addClassName("toolbar");
                toolbar.add(btnRow);
            }
            contentArea.add(toolbar);
        }

        masterGrid.addClassName("mdl-master-grid");
        contentArea.add(masterGrid);
        contentArea.setFlexGrow(masterGrid);

        // Footer (pagination bar) — placed INSIDE the master content area, below
        // the grid. It must live here (not as a third sibling of .iyen-master)
        // because the master panel uses CSS `grid-template-rows: subgrid` at
        // ≥768px, which inherits exactly two rows from .iyen-md-row (auto + 1fr).
        // A third sibling would have no track to land in. As an in-content child
        // it pins naturally at the bottom of the column-flex content area; the
        // grid above it (flex:1 1 auto, min-height:0) absorbs all remaining
        // height, so the footer is always visible at the bottom of the panel.
        if (masterFooterComponent != null) {
            masterFooterComponent.addClassName("iyen-master-footer");
            contentArea.add(masterFooterComponent);
        }

        masterLayout.add(contentArea);
        masterLayout.setFlexGrow(contentArea);

        return IyenMasterBuilder.create(masterLayout);
    }

    /**
     * Builds the outer detail panel container with any static components
     * (header, menu/tabs) and an empty dynamic content slot.
     * CSS class names: {@code iyen-detail}, {@code iyen-detail-header},
     * {@code iyen-detail-tabs}, {@code iyen-detail-content}.
     *
     * @return the fully assembled detail container (dynamic slot added by caller)
     */
    private Layout buildDetailContainer() {
        Layout detailContainer = Components.layout().styleName("iyen-detail").build();

        if (detailHeaderComponent != null) {
            detailHeaderComponent.addClassName("iyen-detail-header");
            detailContainer.add(detailHeaderComponent);
        }

        if (detailBreadcrumbsComponent != null && detailHeaderComponent != null) {
            detailHeaderComponent.setBreadcrumb(detailBreadcrumbsComponent);
        }

        if (detailMenuTabs != null && detailHeaderComponent != null) {
            detailMenuTabs.addClassName("iyen-detail-tabs");
            detailHeaderComponent.setTabs(detailMenuTabs);
        }

        if (detailMenuMenuBar != null) {
            detailMenuMenuBar.addClassName("iyen-detail-menu-bar");
            detailContainer.add(detailMenuMenuBar);
        }

        return detailContainer;
    }

    /**
     * Creates the dynamic content slot, adds it to {@code detailContainer}, and returns it.
     * The slot uses {@code iyen-detail-scroll} so it gets the scrollable flex-column treatment
     * from {@code master-details.css}.
     */
    private Layout buildDynamicSlot(Layout detailContainer) {
        Layout dynamicSlot = Components.layout().styleName("iyen-detail-scroll").build();
        detailContainer.add(dynamicSlot);
        detailContainer.setFlexGrow(dynamicSlot);
        return dynamicSlot;
    }
}





