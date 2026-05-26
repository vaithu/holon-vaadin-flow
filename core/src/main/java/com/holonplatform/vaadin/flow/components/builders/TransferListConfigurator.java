package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTransferListConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.TransferItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.TransferList;

import java.util.Collection;

/**
 * Fluent configurator for {@link TransferList}.
 *
 * @param <C> the concrete configurator type for fluent chaining
 *
 * @see TransferListBuilder
 * @see TransferList
 */
public interface TransferListConfigurator<C extends TransferListConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    // ── Items ─────────────────────────────────────────────────────────────────

    /**
     * Sets the initial items in the "Available" (left) panel.
     *
     * @param items the available catalogue (not null)
     * @return this configurator (for chaining)
     */
    C availableItems(Collection<TransferItem> items);

    /**
     * Pre-populates the "Selected" (right) panel with the given items.
     * These items are automatically removed from the available list if present.
     *
     * @param items the items to pre-select (not null)
     * @return this configurator (for chaining)
     */
    C selectedItems(Collection<TransferItem> items);

    // ── Labels ────────────────────────────────────────────────────────────────

    /**
     * Sets the title of the "Available" (left) panel.
     * Default: {@code "Available"}.
     *
     * @param title the panel title (not null)
     * @return this configurator (for chaining)
     */
    C availableTitle(String title);

    /**
     * Sets the title of the "Selected" (right) panel.
     * Default: {@code "Selected"}.
     *
     * @param title the panel title (not null)
     * @return this configurator (for chaining)
     */
    C selectedTitle(String title);

    // ── Events ────────────────────────────────────────────────────────────────

    /**
     * Registers a listener called whenever items are transferred between panels.
     *
     * @param listener the transfer event listener (not null)
     * @return this configurator (for chaining)
     */
    C onTransfer(com.vaadin.flow.component.ComponentEventListener<TransferList.TransferEvent> listener);

    // ── Configure factory ─────────────────────────────────────────────────────

    /**
     * Returns a fluent configurator for an existing {@link TransferList}.
     *
     * @param transferList the list to configure (not null)
     * @return a new {@link BaseTransferListConfigurator}
     */
    static BaseTransferListConfigurator configure(TransferList transferList) {
        return new DefaultTransferListConfigurator(transferList);
    }

    // ── Base configurator ─────────────────────────────────────────────────────

    /**
     * Non-generic base configurator.
     */
    interface BaseTransferListConfigurator extends TransferListConfigurator<BaseTransferListConfigurator> {
    }
}

