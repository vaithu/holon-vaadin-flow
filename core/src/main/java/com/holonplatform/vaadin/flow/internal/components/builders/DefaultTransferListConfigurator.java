package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.TransferListConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.TransferList;

/**
 * Default {@link TransferListConfigurator.BaseTransferListConfigurator} implementation —
 * returned by {@link TransferListConfigurator#configure(TransferList)}.
 */
public class DefaultTransferListConfigurator
        extends AbstractTransferListConfigurator<TransferListConfigurator.BaseTransferListConfigurator>
        implements TransferListConfigurator.BaseTransferListConfigurator {

    /**
     * Constructor.
     *
     * @param component the existing list to configure (not null)
     */
    public DefaultTransferListConfigurator(TransferList component) {
        super(component);
    }

    @Override
    protected TransferListConfigurator.BaseTransferListConfigurator getConfigurator() {
        return this;
    }
}

