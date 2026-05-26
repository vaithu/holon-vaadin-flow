/*
 * Copyright 2016-2018 Axioma srl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.PropertyInputForm;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator.BaseButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.DialogBuilder;
import com.holonplatform.vaadin.flow.components.builders.DialogBuilder.ConfirmDialogBuilder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

import java.util.function.Consumer;

/**
 * Default {@link ConfirmDialogBuilder} implementation.
 *
 * @since 5.2.0
 */
public class DefaultConfirmDialogBuilder extends AbstractClosableDialogConfigurator<ConfirmDialogBuilder>
        implements ConfirmDialogBuilder {

    private final Button okButton;
    private Button denyButton;

    /**
     * Simple confirm dialog — single "OK" action button, no cancel.
     */
    public DefaultConfirmDialogBuilder() {
        super();
        this.okButton = ButtonBuilder.create()
                .text(Localizable.of("OK", DialogBuilder.DEFAULT_OK_BUTTON_MESSAGE_CODE))
                .styleName("h-dialog__action-btn")
                .withClickListener(e -> getComponent().attemptClose())
                .build();
        // Footer: action only (no cancel in a simple message confirm)
        getComponent().addFooterComponent(this.okButton);
    }

    /**
     * Confirm dialog that validates a {@link PropertyInputForm} before closing.
     * Includes a cancel button so the user can dismiss without validating.
     */
    public DefaultConfirmDialogBuilder(PropertyInputForm inputForm) {
        super();
        this.okButton = ButtonBuilder.create()
                .text(Localizable.of("OK", DialogBuilder.DEFAULT_OK_BUTTON_MESSAGE_CODE))
                .styleName("h-dialog__action-btn")
                .withClickListener(e -> {
                    if (inputForm.isValid()) getComponent().attemptClose();
                })
                .build();
        this.denyButton = buildCancelButton();
        // Footer: cancel on the left, action on the right
        getComponent().addFooterComponent(this.denyButton);
        getComponent().addFooterComponent(this.okButton);
    }

    private Button buildCancelButton() {
        return ButtonBuilder.create()
                .text(Localizable.of("Cancel", DialogBuilder.DEFAULT_DENY_BUTTON_MESSAGE_CODE))
                .styleName("h-dialog__cancel-btn")
                .withClickListener(e -> getComponent().attemptClose())
                .build();
    }

    @Override
    protected ConfirmDialogBuilder getConfigurator() {
        return this;
    }

    @Override
    public ConfirmDialogBuilder okButtonConfigurator(Consumer<BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(ButtonConfigurator.configure(okButton));
        return getConfigurator();
    }

    @Override
    public ConfirmDialogBuilder denialButtonConfigurator(Consumer<BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        if (denyButton == null) {
            // lazily create and insert the cancel button if not already present
            this.denyButton = buildCancelButton();
            getComponent().getFooter().addComponentAsFirst(this.denyButton);
        }
        configurator.accept(ButtonConfigurator.configure(denyButton));
        return getConfigurator();
    }

    @Override
    public Dialog build() {
        return getComponent();
    }
}
