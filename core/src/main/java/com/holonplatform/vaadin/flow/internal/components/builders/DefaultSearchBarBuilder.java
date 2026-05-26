package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.SearchBarBuilder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class DefaultSearchBarBuilder extends AbstractSearchBarConfigurator<SearchBarBuilder> implements SearchBarBuilder {
    public DefaultSearchBarBuilder(HorizontalLayout component) {
        super(component);
    }

    @Override
    protected SearchBarBuilder getConfigurator() {
        return this;
    }

    @Override
    public HorizontalLayout build() {
        return getComponent();
    }
}
