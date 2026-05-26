package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.TransferListBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.TransferList;

/**
 * Default {@link TransferListBuilder} implementation.
 *
 * <p>Instantiates an empty {@link TransferList} and delegates all configuration
 * to {@link AbstractTransferListConfigurator}.
 * Returned by {@link TransferListBuilder#create()} and {@link TransferList#builder()}.</p>
 */
public class DefaultTransferListBuilder
        extends AbstractTransferListConfigurator<TransferListBuilder>
        implements TransferListBuilder {

    /**
     * Constructor — creates a default-configured transfer list.
     */
    public DefaultTransferListBuilder() {
        super(new TransferList());
    }

    @Override
    protected TransferListBuilder getConfigurator() {
        return this;
    }

    @Override
    public TransferList build() {
        return getComponent();
    }
}

