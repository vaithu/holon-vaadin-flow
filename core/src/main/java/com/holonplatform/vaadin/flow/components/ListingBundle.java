/*
 * Copyright 2016-2017 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.textfield.TextField;

import java.util.List;

/**
 * Assembled result of {@code ListingBundleBuilder#build()} or
 * {@code PropertyListingBundleBuilder#build()}.
 *
 * <p>Works identically for both {@link BeanListing} and {@link PropertyListing} — the
 * {@link #listing()} accessor returns the underlying {@link ItemListing} in both cases.</p>
 *
 * <pre>{@code
 * // Bean listing
 * var bundle = Components.listing(Product.class)
 *     .columns("id", "name", "category", "price")
 *     .search("Search products…")
 *     .fetch((q, text) -> service.fetch(q.getOffset(), q.getLimit(), text))
 *     .build();
 *
 * // Property listing
 * var bundle = Components.listing(NAME, CATEGORY, PRICE)
 *     .header(NAME, "Product Name")
 *     .search("Search…")
 *     .fetch((q, text) -> service.fetch(q.getOffset(), q.getLimit(), text))
 *     .build();
 *
 * add(bundle.toolbar(), bundle.grid(), bundle.footer());
 * }</pre>
 *
 * @param <T> item type ({@code Product} for BeanListing; {@code PropertyBox} for PropertyListing)
 * @since 10.0.1
 */
public final class ListingBundle<T> {

    /**
     * An extra item contributed to the options menu in the toolbar via
     * {@link ListingBundleBuilder#withFilterOption(String, Runnable)}.
     *
     * @param label  display text of the menu item
     * @param action action to execute when the item is clicked
     */
    public record FilterOption(String label, Runnable action) {}

    private final ItemListing<T, ?>                listing;
    private final ItemListingPaginationBar<T, ?>    bar;
    private final ItemListingPageSizeSelector<T, ?> selector;
    /** {@code null} when {@code search(String)} was not called on the builder. */
    private final TextField                         search;
    /** {@code null} when {@code withFilterPanel()} was not called on the builder. */
    private final DynamicFilterPanel<T>             filterPanel;
    private final List<FilterOption>                filterOptions;
    private final String                            advancedSearchLabel;
    /**
     * When {@code true} (default) the filter dialog retains its values between
     * open/close cycles.  When {@code false} the panel is reset every time the
     * dialog is opened so the user always starts with a blank slate.
     */
    private final boolean                           retainFilterValues;
    /** Lazily-created dialog that hosts the {@link DynamicFilterPanel}. Created once on first call to {@link #toolbar()}. */
    private Dialog                                  filterDialog;

    ListingBundle(ItemListing<T, ?>                listing,
                  ItemListingPaginationBar<T, ?>    bar,
                  ItemListingPageSizeSelector<T, ?> selector,
                  TextField                         search,
                  DynamicFilterPanel<T>             filterPanel,
                  List<FilterOption>                filterOptions,
                  String                            advancedSearchLabel,
                  boolean                           retainFilterValues) {
        this.listing             = listing;
        this.bar                 = bar;
        this.selector            = selector;
        this.search              = search;
        this.filterPanel         = filterPanel;
        this.filterOptions       = filterOptions != null ? filterOptions : List.of();
        this.advancedSearchLabel = advancedSearchLabel != null ? advancedSearchLabel : "Advanced Search";
        this.retainFilterValues  = retainFilterValues;

        // Make the grid fill its container by default — avoids every view having
        // to set width/flex manually. The rule lives in pagination.css.
        listing.getComponent().addClassName("listing-bundle-grid");
    }

    // ── Accessors ──────────────────────────────────────────────────────────

    /**
     * The underlying {@link ItemListing} ({@link BeanListing} or {@link PropertyListing}).
     * Cast to the specific subtype when you need listing-specific methods.
     */
    public ItemListing<T, ?> listing() { return listing; }

    /** The pagination bar. */
    public ItemListingPaginationBar<T, ?> bar() { return bar; }

    /** The page-size selector (also owns the data binding in managed-fetch mode). */
    public ItemListingPageSizeSelector<T, ?> selector() { return selector; }

    /**
     * The search {@link TextField}, or {@code null} if
     * {@code ListingBundleBuilder#search(String)} was not called.
     */
    public TextField search() { return search; }

    /**
     * The {@link DynamicFilterPanel}, or {@code null} if
     * {@code ListingBundleBuilder#withFilterPanel()} was not called.
     *
     * <p><strong>Note:</strong> when both a search field and a filter panel are configured,
     * the panel is rendered inside a {@link Dialog} managed by {@link #toolbar()}.
     * Do <em>not</em> add this component manually to the layout — the dialog is opened
     * automatically via the filter options button in the toolbar.
     * You may still use this reference for programmatic access (e.g. listening to filter
     * changes or calling {@code resetAll()}).</p>
     */
    public DynamicFilterPanel<T> filterPanel() { return filterPanel; }

    /** Convenience: returns the underlying Vaadin Grid component. */
    public Component grid() { return listing.getComponent(); }

    // ── Layout helpers ─────────────────────────────────────────────────────

    /**
     * Builds a toolbar {@link Div} with:
     * <ul>
     *   <li>The page-size selector on the <strong>left</strong> (if configured)</li>
     *   <li>A {@code listing-toolbar__right} wrapper pushed to the <strong>right</strong>
     *       containing the search field (if configured) and, when a filter panel is also
     *       configured, a {@link MenuBar} options button that opens the
     *       {@link DynamicFilterPanel} in a {@link Dialog}.</li>
     * </ul>
     *
     * <p>The filter panel dialog retains its values between open/close cycles by default.
     * Pass {@code retainFilterValues(false)} on the builder to reset on every open.</p>
     *
     * <p>Clicking the built-in <em>Apply filter</em> button inside the dialog commits the
     * filter, closes the dialog, and resets the listing to page 1.  The dialog footer
     * also exposes a <em>Reset All</em> button (clears all rows) and a <em>Close</em> button
     * for dismissal without applying.</p>
     *
     * <p>Returns a new {@link Div} on each call.</p>
     */
    public Div toolbar() {
        var toolbarRow = new Div();
        toolbarRow.addClassName("listing-toolbar");
        if (selector != null) toolbarRow.add(selector);

        // Right group: search field + optional filter options button
        if (search != null || filterPanel != null) {
            var rightGroup = new Div();
            rightGroup.addClassName("listing-toolbar__right");

            if (search != null) rightGroup.add(search);

            if (filterPanel != null) {
                var menuBar = new MenuBar();
                menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY, MenuBarVariant.LUMO_ICON);
                menuBar.addClassName("listing-toolbar__options");

                var triggerItem = menuBar.addItem(new Icon(VaadinIcon.FILTER));
                triggerItem.getElement().setAttribute("title", "Search options");
                var subMenu = triggerItem.getSubMenu();

                // "Advanced Search" — opens the filter dialog
                subMenu.addItem(advancedSearchLabel)
                        .addClickListener(e -> getOrCreateFilterDialog().open());

                // Extra items contributed by the builder
                for (FilterOption opt : filterOptions) {
                    subMenu.addItem(opt.label()).addClickListener(e2 -> opt.action().run());
                }

                rightGroup.add(menuBar);
            }

            toolbarRow.add(rightGroup);
        }

        return toolbarRow;
    }

    /**
     * Lazily creates and caches the {@link Dialog} that hosts the {@link DynamicFilterPanel}.
     * Only created once; subsequent calls return the cached instance so filter state is
     * retained between open/close cycles (unless {@code retainFilterValues} is {@code false}).
     *
     * <p><strong>Data refresh contract:</strong> the {@link ItemListingPageSizeSelector} is
     * wired via {@code withFilterResetSignal(filterPanel)} during build, which registers a
     * {@link com.vaadin.flow.signals.Signal#effect Signal.effect} on the selector component.
     * Whenever the filter panel fires a filter-change event (on Apply <em>or</em> row removal),
     * the Signal updates and the lifecycle-bound effect calls {@code resetToPage1()}, which
     * resets the page offset and triggers {@code GridLazyDataView.refreshAll()} — re-fetching
     * with the current filter without any additional wiring here.</p>
     */
    private Dialog getOrCreateFilterDialog() {
        if (filterDialog != null) return filterDialog;

        filterDialog = new Dialog();
        filterDialog.setHeaderTitle(advancedSearchLabel);
        filterDialog.setWidth("min(600px, 95vw)");
        filterDialog.addClassName("listing-filter-dialog");

        // Wrap the panel so CSS can target it without shadow-DOM tricks
        var body = new Div(filterPanel);
        body.addClassName("listing-filter-dialog__body");
        filterDialog.add(body);

        // If retain-values is disabled, reset on every open so the user starts blank.
        // resetAll() fires FilterChangeEvent → queryFilterSignal updates → Signal.effect
        // fires resetToPage1() in the selector → grid refreshes with empty filter.
        if (!retainFilterValues) {
            filterDialog.addOpenedChangeListener(e -> {
                if (e.isOpened()) {
                    filterPanel.resetAll();
                }
            });
        }

        // The panel's "Apply filter" button: close the dialog and directly trigger a
        // page reset + data refresh. Although the selector's Signal.effect (wired via
        // withFilterResetSignal during build) handles the reactive case, we also call
        // selector.resetToPage1() directly here as a reliable, lifecycle-independent path.
        // resetToPage1() is a no-op if managedDataView is null (non-managed mode).
        filterPanel.addApplyListener(() -> {
            filterDialog.close();
            if (selector != null) {
                selector.resetToPage1();
            }
        });

        // Footer: only [Close] — "Clear all" in the panel actions bar already handles row reset.
        // Having both "Reset All" (footer) and "Clear all" (panel) was redundant.
        var closeBtn = new Button("Close", e -> filterDialog.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        filterDialog.getFooter().add(closeBtn);

        return filterDialog;
    }

    /**
     * Builds a footer {@link Div} that centers the pagination bar.
     * Applies the {@code listing-footer} CSS class from core {@code pagination.css}.
     * Returns a new {@link Div} on each call.
     */
    public Div footer() {
        var div = Components.div().add(bar).styleName("listing-footer").build();
        return div;
    }
}

