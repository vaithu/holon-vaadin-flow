package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultAlertModalBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.AlertModal;

/**
 * Builder to create and configure {@link AlertModal} components.
 *
 * @see AlertModalConfigurator
 * @see AlertModal
 */
public interface AlertModalBuilder
        extends AlertModalConfigurator<AlertModalBuilder>, ComponentBuilder<AlertModal, AlertModalBuilder> {

    /**
     * Build the {@link AlertModal} and immediately open it.
     *
     * @return the opened {@link AlertModal} instance
     */
    default AlertModal open() {
        AlertModal modal = build();
        modal.open();
        return modal;
    }

    static AlertModalBuilder create() {
        return new DefaultAlertModalBuilder(Alert.Variant.DEFAULT);
    }

    static AlertModalBuilder create(Alert.Variant variant) {
        return new DefaultAlertModalBuilder(variant);
    }
}

