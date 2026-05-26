package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.AlertModalBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertModal;

public class DefaultAlertModalBuilder
        extends AbstractAlertModalConfigurator<AlertModalBuilder>
        implements AlertModalBuilder {

    public DefaultAlertModalBuilder(Alert.Variant variant) {
        super(new AlertModal(variant));
    }

    @Override
    protected AlertModalBuilder getConfigurator() { return this; }

    @Override
    public AlertModal build() { return getComponent(); }
}

