package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.AlertModalConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertModal;

public class DefaultAlertModalConfigurator
        extends AbstractAlertModalConfigurator<AlertModalConfigurator.BaseAlertModalConfigurator>
        implements AlertModalConfigurator.BaseAlertModalConfigurator {

    public DefaultAlertModalConfigurator(AlertModal modal) {
        super(modal);
    }

    @Override
    protected AlertModalConfigurator.BaseAlertModalConfigurator getConfigurator() { return this; }
}

