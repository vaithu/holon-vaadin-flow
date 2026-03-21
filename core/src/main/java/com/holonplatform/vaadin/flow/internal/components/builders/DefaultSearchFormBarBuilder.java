package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ZohoConfigurator;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DefaultSearchFormBarBuilder<B extends ZohoConfigurator<B>>
        extends AbstractSearchBarConfigurator<ZohoConfigurator.SearchFormBarBuilder<B>>
        implements ZohoConfigurator.SearchFormBarBuilder<B> {

    private final B parentBuilder;
    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultSearchFormBarBuilder(B parentBuilder,HorizontalLayout component) {
        super(component);
        this.parentBuilder = parentBuilder;
    }

    @Override
    public B add() {
        parentBuilder.searchBar(getComponent());
        return parentBuilder;
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected ZohoConfigurator.SearchFormBarBuilder<B> getConfigurator() {
        return this;
    }
}
