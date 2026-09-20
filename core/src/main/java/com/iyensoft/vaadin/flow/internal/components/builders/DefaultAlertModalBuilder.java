package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.AlertModalBuilder;
import com.iyensoft.vaadin.flow.components.Alert;
import com.iyensoft.vaadin.flow.components.AlertModal;

public class DefaultAlertModalBuilder
        extends AbstractAlertModalConfigurator<AlertModalBuilder>
        implements AlertModalBuilder {

    public DefaultAlertModalBuilder(Alert.Variant variant) {
        super(new AlertModal(variant));
    }

    @Override
    protected AlertModalBuilder getConfigurator() { return this; }

    @Override
    public AlertModal build() { applyPostProcessors(); return getComponent(); }
}

