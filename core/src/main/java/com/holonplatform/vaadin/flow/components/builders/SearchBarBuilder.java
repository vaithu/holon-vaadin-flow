package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultSearchBarBuilder;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public interface SearchBarBuilder extends SearchBarConfigurator<SearchBarBuilder>, ComponentBuilder<HorizontalLayout, SearchBarBuilder> {

    static SearchBarBuilder create() {
        return new DefaultSearchBarBuilder(new HorizontalLayout());
    }
}
