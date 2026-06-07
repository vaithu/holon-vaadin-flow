package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ListingBundle;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.builders.ComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Common configurator for master/detail layouts.
 *
 * @param <C> Concrete configurator type
 */
public interface IyenCommonConfigurator<C extends IyenCommonConfigurator<C>>
        extends ComponentConfigurator<C>,
        HasStyleConfigurator<C>,
        HasSizeConfigurator<C> {

    C header(Header header);

    C toolbar(TextField textField, Button... buttons);

    C toolbar(MenuBar menuBar);

    C toolbar(Input<String> input, Button... buttons);

    C content(GridHeader gridHeader, Grid<?> grid);

    C content(GridHeader gridHeader, BeanListing<?> listing);

    C content(GridHeader gridHeader, PropertyListing listing);

    C content(Component component);

    C content(ListingBundle<?> bundle);

    /**
     * Get the content {@link Layout}.
     * @return The content layout
     */
    Layout getContent();

}