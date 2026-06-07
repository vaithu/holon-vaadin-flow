package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTransferListBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.TransferList;

/**
 * Holon Fluent Builder for {@link TransferList}.
 *
 * <p>Extends {@link TransferListConfigurator} (all configuration methods)
 * and {@link ComponentBuilder} (terminal {@link #build()} method).</p>
 *
 * <p>Obtain via {@link TransferList#builder()} or {@link #create()}:</p>
 * <pre>{@code
 * TransferList list = TransferList.builder()
 *     .availableItems(catalogue)
 *     .availableTitle("Products")
 *     .selectedTitle("Ordered Items")
 *     .onTransfer(event -> save(event.getSelectedItems()))
 *     .build();
 *
 * content(list);
 * }</pre>
 *
 * @see TransferListConfigurator
 * @see TransferList
 */
public interface TransferListBuilder
        extends TransferListConfigurator<TransferListBuilder>,
                ComponentBuilder<TransferList, TransferListBuilder> {

    /**
     * Creates a new {@link TransferListBuilder}.
     *
     * @return a new builder
     */
    static TransferListBuilder create() {
        return new DefaultTransferListBuilder();
    }
}

