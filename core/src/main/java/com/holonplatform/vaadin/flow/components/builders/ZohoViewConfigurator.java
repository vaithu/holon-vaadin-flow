package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.internal.lumo.SeparatorColor;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridHeader;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Main interface for configuring a Zoho View.
 *
 * @param <C> the type of the configurator
 */
public interface ZohoViewConfigurator<C extends ZohoViewConfigurator< C>> {


    ZohoMasterBuilder<  C> master();

    ZohoDetailBuilder<  C> detail();
    C separator();

    C separator(SeparatorColor  separatorColor);

    Layout build();

    /**
     * Common builder contract; accumulates UI parts and finally adds them to the
     * parent's layout when {@code content()} is called.
     */

    interface ZohoCommonBuilder<B extends ZohoViewConfigurator<B>>
            extends HasStyleConfigurator<ZohoCommonBuilder<B>>,
            HasSizeConfigurator<ZohoCommonBuilder<B>>{

        ZohoCommonBuilder<B> id(String id);
        ZohoCommonBuilder<B> header(Header header);
        ZohoCommonBuilder<B> toolbar(TextField textField, Button... buttons);
        ZohoCommonBuilder<B> toolbar(Input<String> input, Button... buttons);

        ZohoCommonBuilder<B> content(GridHeader gridHeader, Grid<?> grid);
        ZohoCommonBuilder<B> content(GridHeader gridHeader, BeanListing<?> listing);
        ZohoCommonBuilder<B> content(GridHeader gridHeader, PropertyListing listing);

        ZohoCommonBuilder<B> content(Grid<?> grid);
        ZohoCommonBuilder<B> content(BeanListing<?> listing);
        ZohoCommonBuilder<B> content(PropertyListing listing);
        ZohoCommonBuilder<B> content(Component component);

        BuiltView<B> build();
    }




    interface ZohoMasterBuilder<B extends ZohoViewConfigurator<B>>
            extends ZohoCommonBuilder<B> {
        // Master-specific methods if needed


    }

    interface ZohoDetailBuilder<B extends ZohoViewConfigurator<B>>
            extends ZohoCommonBuilder<B> {
        // Detail-specific methods if needed

    }

    // ✅ Nested class inside the interface
    class BuiltView<C extends ZohoViewConfigurator<C>> {
        private final Layout layout;
        private final C configurator;

        public BuiltView(Layout layout, C configurator) {
            this.layout = layout;
            this.configurator = configurator;
        }

        public Layout getLayout() {
            return layout;
        }

        public C add() {
            configurator.build().add(layout);
            return configurator;
        }
    }




}
