package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.components.builders.IyenMasterBuilder;
import com.iyensoft.vaadin.flow.components.builders.MasterDetailBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
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
    private Component masterHeaderComponent;
    private TextField masterSearchField;
    private Button[] masterSearchActions = new Button[0];
    private Grid<T> masterGrid;

    // -------------------------------------------------------------------------
    // Detail configuration
    // -------------------------------------------------------------------------
    private Component detailHeaderComponent;
    private Component detailMenuComponent;
    private Function<T, Component[]> detailContentProvider;

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
    public MasterDetailBuilder<T> masterHeader(Component header) {
        this.masterHeaderComponent = header;
        return this;
    }

    @Override
    public MasterDetailBuilder<T> masterSearch(TextField searchField, Button... actions) {
        this.masterSearchField = searchField;
        this.masterSearchActions = actions != null ? actions : new Button[0];
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
    public MasterDetailBuilder<T> detailHeader(Component header) {
        this.detailHeaderComponent = header;
        return this;
    }

    @Override
    public MasterDetailBuilder<T> detailMenu(Component menuOrTabs) {
        this.detailMenuComponent = menuOrTabs;
        return this;
    }

    @Override
    public MasterDetailBuilder<T> detailContent(Function<T, Component[]> contentProvider) {
        this.detailContentProvider = contentProvider;
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
     * the optional header, optional search toolbar, and the mandatory grid.
     * Uses CSS class names from {@code master-details.css}:
     * {@code iyen-master}, {@code iyen-master-header}, {@code iyen-master-content},
     * {@code toolbar}, {@code mdl-master-grid}.
     *
     * <p>The toolbar (if any) and the grid are wrapped in a single
     * {@code iyen-master-content} container. On desktop the layout switches to
     * CSS Grid with two rows (header / content); both panels share the same
     * header-row height via {@code grid-template-rows: subgrid}, keeping the
     * header bottom-borders horizontally aligned.</p>
     */
    private IyenMasterBuilder buildMaster() {
        Layout masterLayout = Components.layout().styleName("iyen-master").build();

        if (masterHeaderComponent != null) {
            masterHeaderComponent.addClassName("iyen-master-header");
            masterLayout.add(masterHeaderComponent);
        }

        // Wrap toolbar + grid in a single content area so each panel has exactly
        // two children (header + content) — required for CSS subgrid row alignment.
        Layout contentArea = Components.layout().styleName("iyen-master-content").build();

        if (masterSearchField != null) {
            Layout toolbar = new Layout(masterSearchField);
            toolbar.addClassName("toolbar");
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

        if (detailMenuComponent != null) {
            detailMenuComponent.addClassName("iyen-detail-tabs");
            detailContainer.add(detailMenuComponent);
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





