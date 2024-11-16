package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.HasSearchBar;

public interface SearchBarConfigurator<C extends SearchBarConfigurator<C>> extends
        HasSearchBar<C>, ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    interface BaseSearchBarConfigurator extends SearchBarConfigurator<BaseSearchBarConfigurator> {

    }
}
