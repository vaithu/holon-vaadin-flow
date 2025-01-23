package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.HasSearchBar;

import java.util.function.Consumer;

public interface SearchBarConfigurator<C extends SearchBarConfigurator<C>> extends
        HasSearchBar<C>, ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>, HasComponentsConfigurator<C> {

    C withPostProcessor(Consumer<SearchBarConfigurator<C>> postProcessor);

    interface BaseSearchBarConfigurator extends SearchBarConfigurator<BaseSearchBarConfigurator> {

    }
}
