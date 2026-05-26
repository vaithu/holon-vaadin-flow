package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.iyensoft.vaadin.flow.components.MasterDetailLayout;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultMasterDetailBuilder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Fluent builder for {@link MasterDetailLayout}.
 *
 * <h3>Minimal usage</h3>
 * <pre>{@code
 * MasterDetailLayout<Order> layout = MasterDetailLayout.<Order>builder()
 *     .masterGrid(orderGrid)
 *     .detailContent(order -> new Component[]{ new OrderDetailForm(order) })
 *     .build();
 * }</pre>
 *
 * <h3>Full usage with URL sync and auto-refresh</h3>
 * <pre>{@code
 * MasterDetailLayout<Order> layout = MasterDetailLayout.<Order>builder()
 *     .masterHeader(new Header("Orders"))
 *     .masterSearch(searchField)
 *     .masterGrid(orderGrid)
 *     .detailHeader(new Header("Order details"))
 *     .detailMenu(menuBar)
 *     .detailContent(order -> new Component[]{ new OrderDetailForm(order) })
 *     .itemId(o -> String.valueOf(o.getId()),
 *             id -> orderService.findById(Long.parseLong(id)))
 *     .mobileSheetTitle("Order details")
 *     .onDataChanged(() -> orderGrid.getDataProvider().refreshAll())
 *     .build();
 * }</pre>
 *
 * @param <T> the item type managed by the master grid
 * @see MasterDetailLayout
 */
public interface MasterDetailBuilder<T> {

    // -------------------------------------------------------------------------
    // Master panel
    // -------------------------------------------------------------------------

    /**
     * Adds an optional header component to the top of the master panel.
     *
     * @param header the header component (not null)
     * @return this builder
     */
    MasterDetailBuilder<T> masterHeader(Component header);

    /**
     * Adds an optional search / filter toolbar to the master panel.
     * The field is placed in a responsive toolbar row; additional action buttons
     * are aligned to the trailing edge.
     *
     * @param searchField the search input (not null)
     * @param actions     optional trailing action buttons
     * @return this builder
     */
    MasterDetailBuilder<T> masterSearch(TextField searchField, com.vaadin.flow.component.button.Button... actions);

    /**
     * Sets the mandatory master {@link Grid}.
     * The grid's single-selection listener is wired internally.
     *
     * @param grid the grid (not null)
     * @return this builder
     */
    MasterDetailBuilder<T> masterGrid(Grid<T> grid);

    /**
     * Sets a Holon {@link BeanListing} as the master listing.
     * The underlying {@link Grid} is extracted from the listing automatically.
     *
     * @param listing the bean listing (not null)
     * @return this builder
     */
    MasterDetailBuilder<T> masterGrid(BeanListing<T> listing);

    /**
     * Sets any Holon {@link ItemListing} as the master listing.
     * The underlying {@link Grid} is extracted from the listing automatically.
     *
     * @param listing the item listing (not null)
     * @return this builder
     */
    MasterDetailBuilder<T> masterGrid(ItemListing<T, ?> listing);

    /**
     * Sets a Holon {@link PropertyListing} as the master listing.
     * Use this overload when {@code T} is {@code PropertyBox}.
     *
     * <p><em>Note</em>: an unchecked cast from {@code PropertyBox} to {@code T} is performed at
     * runtime. Ensure {@code T} is {@code PropertyBox} to avoid a {@link ClassCastException}.</p>
     *
     * @param listing the property listing (not null)
     * @return this builder
     */
    MasterDetailBuilder<T> masterGrid(PropertyListing listing);

    // -------------------------------------------------------------------------
    // Detail panel — static parts
    // -------------------------------------------------------------------------

    /**
     * Adds an optional static header to the detail panel.
     * The header is rendered above the menu bar / tabs and does not change
     * when a different item is selected.
     *
     * @param header the header component (not null)
     * @return this builder
     */
    MasterDetailBuilder<T> detailHeader(Component header);

    /**
     * Adds an optional static menu / navigation component (e.g. {@code MenuBar} or
     * {@code Tabs}) below the detail header.
     *
     * @param menuOrTabs the menu / tabs component (not null)
     * @return this builder
     */
    MasterDetailBuilder<T> detailMenu(Component menuOrTabs);

    // -------------------------------------------------------------------------
    // Detail panel — dynamic content
    // -------------------------------------------------------------------------

    /**
     * Sets the factory that produces the per-item detail component(s).
     * The factory is called every time a new item is selected.
     * Components are physically added to the DOM on selection and removed on
     * deselection — they are never merely hidden.
     *
     * <p>For expensive components consider caching the result externally.</p>
     *
     * @param contentProvider a function that receives the selected item and returns
     *                        the component(s) to display (not null)
     * @return this builder
     */
    MasterDetailBuilder<T> detailContent(Function<T, Component[]> contentProvider);

    // -------------------------------------------------------------------------
    // URL synchronisation
    // -------------------------------------------------------------------------

    /**
     * Enables URL {@code ?id=} query-parameter synchronisation.
     *
     * <p>When an item is selected its serialised ID is appended to the current URL
     * via {@code history.replaceState} (no full navigation). When the selection is
     * cleared the parameter is removed.</p>
     *
     * <p>To restore the selection on a page refresh, inject the parameter via
     * {@code @QueryParameter} in the host view and call
     * {@link MasterDetailLayout#restoreSelection(String)} from {@code @OnShow}.</p>
     *
     * @param idExtractor converts an item to its URL-safe string ID
     * @param itemLoader  loads an item by its string ID; returns {@code Optional.empty()}
     *                    if not found
     * @return this builder
     */
    MasterDetailBuilder<T> itemId(Function<T, String> idExtractor,
                                   Function<String, Optional<T>> itemLoader);

    // -------------------------------------------------------------------------
    // Mobile overlay
    // -------------------------------------------------------------------------

    /**
     * Sets the title shown in the mobile {@link com.holonplatform.vaadin.flow.vaadinplus.components.Sheet}
     * overlay header. Defaults to {@code "Details"}.
     *
     * @param title the sheet title text
     * @return this builder
     */
    MasterDetailBuilder<T> mobileSheetTitle(String title);

    // -------------------------------------------------------------------------
    // Data-change notification
    // -------------------------------------------------------------------------

    /**
     * Registers a listener that is called when
     * {@link MasterDetailLayout#notifyDataChanged()} is invoked from the detail view.
     * Typically used to trigger {@code grid.getDataProvider().refreshAll()}.
     *
     * <p>Multiple listeners can be registered by calling this method repeatedly.</p>
     *
     * @param listener the listener (not null)
     * @return this builder
     */
    MasterDetailBuilder<T> onDataChanged(Runnable listener);

    /**
     * When set to {@code true}, the first row in the master grid is selected automatically
     * the first time the layout is rendered in a tablet/desktop viewport and no other
     * selection is active (e.g. no URL {@code ?id=} was present on load).
     *
     * <p>Defaults to {@code false}.</p>
     *
     * @param autoSelect {@code true} to enable auto-selection of the first row
     * @return this builder
     */
    MasterDetailBuilder<T> autoSelectFirst(boolean autoSelect);

    // -------------------------------------------------------------------------
    // Reactive detail sync
    // -------------------------------------------------------------------------

    /**
     * Registers a lifecycle-bound reactive handler that is called automatically
     * whenever the selected item changes <em>or</em> after
     * {@link MasterDetailLayout#notifyDataChanged()} is invoked.
     *
     * <p>The handler is bound to {@code owner}'s attach/detach lifecycle via
     * {@link com.vaadin.flow.signals.Signal#effect Signal.effect}: it activates on
     * attach and is automatically removed on detach — no manual cleanup needed.</p>
     *
     * <p>Multiple handlers can be registered against different owner components:</p>
     * <pre>{@code
     * MasterDetailLayout.<Contact>builder()
     *     .masterGrid(grid)
     *     .detailContent(c -> new Component[]{ formBody, footer })
     *     .withDetailSync(formBody, c -> {
     *         nameField.setValue(c.getName());
     *         emailField.setValue(c.getEmail());
     *     })
     *     .withDetailSync(headerComponent, c -> headerComponent.setHeading(c.getName()))
     *     .build();
     * }</pre>
     *
     * @param owner   the component whose lifecycle bounds this effect (not null)
     * @param handler called with the selected item on every reactive update (not null)
     * @return this builder
     */
    MasterDetailBuilder<T> withDetailSync(Component owner, Consumer<T> handler);

    // -------------------------------------------------------------------------
    // Build
    // -------------------------------------------------------------------------

    /**
     * Builds and returns the configured {@link MasterDetailLayout}.
     *
     * @return the layout instance (not null)
     * @throws IllegalStateException if a mandatory property is missing
     */
    MasterDetailLayout<T> build();

    // -------------------------------------------------------------------------
    // Factory
    // -------------------------------------------------------------------------

    /**
     * Creates a new {@link MasterDetailBuilder}.
     *
     * @param <T> the item type
     * @return a new builder instance
     */
    static <T> MasterDetailBuilder<T> create() {
        return new DefaultMasterDetailBuilder<>();
    }
}


