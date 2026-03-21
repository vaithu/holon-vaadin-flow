package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ZohoViewConfigurator;

public class DefaultZohoDetailsBuilder<B extends ZohoViewConfigurator<B>>
        extends AbstractZohoCommonBuilder<B>
        implements ZohoViewConfigurator.ZohoDetailBuilder<B> {


    public DefaultZohoDetailsBuilder(B configurator ){
        super(configurator, Components.layout().styleName("master-layout").build());
    }
}

