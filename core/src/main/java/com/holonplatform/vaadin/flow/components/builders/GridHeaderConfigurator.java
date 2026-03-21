package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Font;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.HeadingLevel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;

/**
 * Configurator interface for GridHeader.
 *
 * @param <C> the concrete configurator type
 */
public interface GridHeaderConfigurator<C extends GridHeaderConfigurator<C>> extends ComponentConfigurator<C>,
        HasSizeConfigurator<C>, HasStyleConfigurator<C>, HasEnabledConfigurator<C> {


    C grid(Grid<?> grid);

    C listing(BeanListing<?> beanListing);

    C listing(PropertyListing propertyListing);

    C defaultActions(Component... components);

    C contextActions(Component... components);

}
