package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.vaadinplus.components.Breadcrumb;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.tabs.Tabs;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Simplified fluent builder for {@link MasterDetailLayout} that accepts a
 * {@link ListingBundle} as the entire left (master) panel, removing the need
 * to wire the grid, search field, and header separately.
 *
 * <h3>Layout structure</h3>
 * <pre>
 * ┌──────────────────────┬──────────────────────────────────────────────┐
 * │  MASTER (left)       │  DETAIL (right)                              │
 * │  ─────────────────── │  ────────────────────────────────────────── │
 * │  GridHeader          │  detailTitle + detailActions (Header)        │
 * │  Search TextField    │  detailBreadcrumbs  (optional)               │
 * │  ──────────────────  │  detailTabs / MenuBar  (optional)            │
 * │  ItemListing (grid)  │  ─────────── scrollable ─────────────────── │
 * │                      │  detailContent  (dynamic per-item)           │
 * │                      │  ─────────────────────────────────────────  │
 * │                      │  detailFooter  (optional static footer)      │
 * └──────────────────────┴──────────────────────────────────────────────┘
 * </pre>
 *
 * <p>On <b>mobile</b> the detail opens as a full-screen
 * {@link com.holonplatform.vaadin.flow.vaadinplus.components.Sheet Sheet} slide-in.
 * On <b>tablet/desktop</b> both panels sit side by side.</p>
 *
 * <h3>Minimal usage</h3>
 * <pre>{@code
 * ListingBundle<Product> bundle = Components.listing(Product.class)
 *     .columns("name", "price")
 *     .hidden("id")
 *     .gridHeader("Products")
 *     .search("Search…")
 *     .fetch(...)
 *     .build();
 *
 * MasterDetailLayout<Product> layout = Components.masterDetail(bundle)
 *     .detailTitle("Product Details")
 *     .detailContent(p -> new Component[]{ Components.keyValueList().addFromBean(p) })
 *     .mobileSheetTitle("Product Details")
 *     .itemId(p -> String.valueOf(p.getId()), id -> service.findById(Long.parseLong(id)))
 *     .build();
 *
 * content(layout);
 * }</pre>
 *
 * <h3>Full usage with header actions, tabs, footer and reactive sync</h3>
 * <pre>{@code
 * Button saveBtn    = new Button("Save");
 * Button deleteBtn  = new Button("Delete");
 * TextField nameField = new TextField("Name");
 *
 * MasterDetailLayout<Product> layout = Components.masterDetail(bundle)
 *     .detailTitle("Product Details")
 *     .detailActions(saveBtn, deleteBtn)          // appear in the Header's action slot
 *     .detailBreadcrumbs(breadcrumb)              // optional nav trail below header
 *     .detailTabs(tabSheet)                       // optional tab bar below breadcrumbs
 *     .detailContent(p -> new Component[]{ formBody })
 *     .detailFooter(archiveBtn, metaLabel)        // static footer below content
 *     .withDetailSync(formBody, p -> {
 *         nameField.setValue(p.getName());
 *     })
 *     .mobileSheetTitle("Product Details")
 *     .itemId(p -> String.valueOf(p.getId()), id -> service.findById(Long.parseLong(id)))
 *     .autoSelectFirst(false)
 *     .onDataChanged(() -> bundle.listing().getDataProvider().refreshAll())
 *     .build();
 * }</pre>
 *
 * @param <T> the item type displayed in the master grid
 * @see MasterDetailBuilder
 * @see MasterDetailLayout
 * @since 10.0.2
 */
public interface BundleMasterDetailBuilder<T> {

    // -------------------------------------------------------------------------
    // Detail panel — header
    // -------------------------------------------------------------------------

    /**
     * Sets the title text of the detail panel header.
     * A {@link com.holonplatform.vaadin.flow.vaadinplus.components.Header Header}
     * component is created automatically from this string.
     *
     * <p>Mutually exclusive with {@link #detailHeader(Header)}: the last call wins.</p>
     *
     * @param title the detail panel title (not null)
     * @return this builder
     */
    BundleMasterDetailBuilder<T> detailTitle(String title);

    /**
     * Adds action buttons to the detail header (e.g. Save, Delete, Edit).
     * These are placed in the trailing action slot of the auto-built
     * {@link com.holonplatform.vaadin.flow.vaadinplus.components.Header Header}.
     * No-op when a fully custom header is provided via {@link #detailHeader(Header)}.
     *
     * @param actions one or more action components (not null)
     * @return this builder
     */
    BundleMasterDetailBuilder<T> detailActions(Component... actions);

    /**
     * Provides a fully custom component for the detail panel header, bypassing
     * the auto-built title/actions header.
     *
     * <p>Use this when you need complete control over the header layout.
     * Mutually exclusive with {@link #detailTitle(String)} + {@link #detailActions(Component...)}:
     * the last setter wins.</p>
     *
     * @param header a fully composed header component (not null)
     * @return this builder
     */
    BundleMasterDetailBuilder<T> detailHeader(Header header);

    /**
     * Adds an optional breadcrumbs component below the detail header.
     *
     * @param breadcrumbs the breadcrumbs component (not null)
     * @return this builder
     */
    BundleMasterDetailBuilder<T> detailBreadcrumbs(Breadcrumb breadcrumbs);

    /**
     * Adds an optional tabs or menu bar below the breadcrumbs / header.
     *
     * @param tabs a {{@code Tabs}(not null)
     * @return this builder
     */
    BundleMasterDetailBuilder<T> detailTabs(Tabs tabs);
/**
     * Adds an optional tabs or menu bar below the breadcrumbs / header.
     *
     * @param menuBar a  {@code MenuBar} (not null)
     * @return this builder
     */
    BundleMasterDetailBuilder<T> detailMenuBar(MenuBar menuBar);

    // -------------------------------------------------------------------------
    // Detail panel — dynamic content
    // -------------------------------------------------------------------------

    /**
     * Sets the factory that produces the per-item detail components.
     * Called once on the first selection; subsequent data changes are propagated
     * through {@link #withDetailSync(Component, Consumer)} effects without rebuilding
     * the component tree.
     *
     * @param provider a function that receives the selected item and returns the
     *                 component(s) to display (not null)
     * @return this builder
     */
    BundleMasterDetailBuilder<T> detailContent(Function<T, Component[]> provider);

    // -------------------------------------------------------------------------
    // Detail panel — footer
    // -------------------------------------------------------------------------

    /**
     * Adds a static footer at the bottom of the detail panel, below the
     * scrollable content area. Footer components are built once and never replaced;
     * use {@link #withDetailSync(Component, Consumer)} to update them reactively.
     *
     * @param footer one or more footer components (not null)
     * @return this builder
     */
    BundleMasterDetailBuilder<T> detailFooter(Component... footer);

    // -------------------------------------------------------------------------
    // Reactive sync
    // -------------------------------------------------------------------------

    /**
     * Registers a lifecycle-bound reactive handler that is called automatically
     * whenever the selected item changes <em>or</em> after
     * {@link MasterDetailLayout#notifyDataChanged()} is invoked.
     *
     * <p>The handler is bound to {@code owner}'s attach/detach lifecycle —
     * no manual cleanup required.</p>
     *
     * <pre>{@code
     * .withDetailSync(formBody, product -> {
     *     nameField.setValue(product.getName());
     *     priceField.setValue(product.getPrice());
     * })
     * .withDetailSync(headerComponent, p -> header.setHeading(p.getName()))
     * }</pre>
     *
     * @param owner   the component whose lifecycle bounds this effect (not null)
     * @param handler called with the selected item on every reactive update (not null)
     * @return this builder
     */
    BundleMasterDetailBuilder<T> withDetailSync(Component owner, Consumer<T> handler);

    // -------------------------------------------------------------------------
    // URL synchronisation
    // -------------------------------------------------------------------------

    /**
     * Enables URL {@code ?id=} query-parameter synchronisation.
     * Selecting a row appends {@code ?id=<value>} to the browser URL via
     * {@code history.replaceState}. To restore selection on a hard refresh,
     * use {@code @QueryParameter} + {@link MasterDetailLayout#restoreSelection(String)}
     * in the host view.
     *
     * @param idExtractor converts an item to its URL-safe string ID (not null)
     * @param itemLoader  loads an item by its string ID (not null)
     * @return this builder
     */
    BundleMasterDetailBuilder<T> itemId(Function<T, String> idExtractor,
                                         Function<String, Optional<T>> itemLoader);

    // -------------------------------------------------------------------------
    // Mobile overlay
    // -------------------------------------------------------------------------

    /**
     * Sets the title shown in the mobile {@link com.holonplatform.vaadin.flow.vaadinplus.components.Sheet Sheet}
     * overlay. Defaults to {@code "Details"}.
     *
     * @param title the sheet title text (not null)
     * @return this builder
     */
    BundleMasterDetailBuilder<T> mobileSheetTitle(String title);

    // -------------------------------------------------------------------------
    // Behaviour
    // -------------------------------------------------------------------------

    /**
     * When {@code true}, the first row is selected automatically the first time
     * the layout is shown in a tablet/desktop viewport and no selection exists.
     * Defaults to {@code false}.
     *
     * @param auto {@code true} to auto-select the first row
     * @return this builder
     */
    BundleMasterDetailBuilder<T> autoSelectFirst(boolean auto);

    /**
     * Registers a listener that is called when
     * {@link MasterDetailLayout#notifyDataChanged()} is invoked from the detail view.
     * Typically used to call {@code grid.getDataProvider().refreshAll()}.
     *
     * @param listener the listener (not null)
     * @return this builder
     */
    BundleMasterDetailBuilder<T> onDataChanged(Runnable listener);

    // -------------------------------------------------------------------------
    // Build
    // -------------------------------------------------------------------------

    /**
     * Builds and returns the configured {@link MasterDetailLayout}.
     *
     * @return the layout instance (not null)
     * @throws IllegalStateException if a mandatory property ({@code detailContent}) is missing
     */
    MasterDetailLayout<T> build();

    // -------------------------------------------------------------------------
    // Factory
    // -------------------------------------------------------------------------

    /**
     * Creates a new {@link BundleMasterDetailBuilder} pre-wired with the given bundle
     * as the master (left) panel.
     *
     * @param <T>    the item type
     * @param bundle the listing bundle driving the master panel (not null)
     * @return a new builder instance
     */
    static <T> BundleMasterDetailBuilder<T> create(ListingBundle<T> bundle) {
        return new com.iyensoft.vaadin.flow.internal.components.builders.DefaultBundleMasterDetailBuilder<>(bundle);
    }
}



