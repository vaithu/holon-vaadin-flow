package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ZohoConfigurator;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DeafultBulkActionBarBuilder<B extends ZohoConfigurator<B>>
        extends AbstractBulkActionConfigurator<ZohoConfigurator.BulkActionBarBuilder<B>>
        implements ZohoConfigurator.BulkActionBarBuilder<B> {

    private final B parentBuilder;

    /**
     * Constructor.
     *
     * @param content The content instance (not null)
     */
    public DeafultBulkActionBarBuilder( B parentBuilder,HorizontalLayout content) {
        super(content);
        this.parentBuilder = parentBuilder;
    }

    @Override
    public B add() {
        parentBuilder.bulkAction(getComponent());
        return parentBuilder;
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected ZohoConfigurator.BulkActionBarBuilder<B> getConfigurator() {
        return this;
    }
}
