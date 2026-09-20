package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.AlertModalConfigurator;
import com.iyensoft.vaadin.flow.components.AlertModal;

public class DefaultAlertModalConfigurator
        extends AbstractAlertModalConfigurator<AlertModalConfigurator.BaseAlertModalConfigurator>
        implements AlertModalConfigurator.BaseAlertModalConfigurator {

    public DefaultAlertModalConfigurator(AlertModal modal) {
        super(modal);
    }

    @Override
    protected AlertModalConfigurator.BaseAlertModalConfigurator getConfigurator() { return this; }
}

