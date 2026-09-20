package com.iyensoft.vaadin.flow.components;

/**
 * Implemented by any component that wants to automatically receive the selected
 * item when the user clicks a row in an associated master grid or listing.
 *
 * <p>Components implementing this interface are auto-discovered at build time by
 * the master-detail infrastructure — no explicit wiring required. They are also
 * supported by {@link com.iyensoft.vaadin.flow.components.builders.LazyTabsConfigurator}:
 * lazy-built tab panels implementing {@code DetailSyncAware} are notified immediately
 * after construction so they are never shown in a stale or empty state.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * public class InvoiceSummaryTab extends Div implements DetailSyncAware<Invoice> {
 *     private final Span customerName = new Span();
 *     private final Span total        = new Span();
 *
 *     public InvoiceSummaryTab() { add(customerName, total); }
 *
 *     @Override
 *     public void onItemSelected(Invoice invoice) {
 *         customerName.setText(invoice.getCustomerName());
 *         total.setText(formatCurrency(invoice.getTotal()));
 *     }
 * }
 * }</pre>
 *
 * <p>Components implementing this interface are fully portable — they work
 * identically whether placed in the desktop detail panel, a mobile Sheet,
 * or as tab content inside a {@code LazyTabsConfigurator}.</p>
 *
 * @param <T> the item type
 */
@FunctionalInterface
public interface DetailSyncAware<T> {

    /**
     * Called when an item is selected. Implementations should update their
     * UI state to reflect the given item.
     *
     * @param item the selected item, never null
     */
    void onItemSelected(T item);
}
