package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ZohoViewConfigurator;

public class DefaultZohoMasterBuilder<B extends ZohoViewConfigurator<B>>
        extends AbstractZohoCommonBuilder<B>
        implements ZohoViewConfigurator.ZohoMasterBuilder<B> {


    public DefaultZohoMasterBuilder(B configurator) {
        super(configurator, Components.layout().styleName("detail-layout").build());
    }




}

