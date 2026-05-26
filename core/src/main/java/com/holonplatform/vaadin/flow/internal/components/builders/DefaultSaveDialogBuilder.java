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
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.DialogBuilder;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ModalityMode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

import java.util.function.Consumer;

public class DefaultSaveDialogBuilder extends AbstractDialogConfigurator<DialogBuilder.SaveDialogBuilder>
        implements DialogBuilder.SaveDialogBuilder {

    private final Button saveButton;
    private final Button denyButton;

    public DefaultSaveDialogBuilder(SaveDialogCallback saveDialogCallback) {

        super();
        ObjectUtils.argumentNotNull(saveDialogCallback, "Save dialog callback must be not null");

        this.saveButton = ButtonBuilder.create()
                .text(Localizable.of("Save", DialogBuilder.DEFAULT_SAVE_BUTTON_MESSAGE_CODE))
                .styleName("h-dialog__action-btn")
                .withClickListener(e -> saveDialogCallback.onUserAnswer(true, isOkToClose -> {
                    if (isOkToClose) getComponent().attemptClose();
                }))
                .withClickShortcutKey(Key.ENTER)
                .build();

        this.denyButton = ButtonBuilder.create()
                .text(Localizable.of("Cancel", DialogBuilder.DEFAULT_DENY_BUTTON_MESSAGE_CODE))
                .styleName("h-dialog__cancel-btn")
                .withClickListener(e -> getComponent().attemptClose())
                .build();

        getComponent().addFooterComponent(this.denyButton);
        getComponent().addFooterComponent(this.saveButton);

        getComponent().setCloseOnEsc(true);
        getComponent().setResizable(true);
        getComponent().setDraggable(true);
        getComponent().setCloseOnOutsideClick(false);
        getComponent().setModality(ModalityMode.STRICT);
    }

    @Override
    public SaveDialogBuilder saveButtonConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(ButtonConfigurator.configure(saveButton));
        return getConfigurator();
    }

    @Override
    public SaveDialogBuilder denialButtonConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(ButtonConfigurator.configure(denyButton));
        return getConfigurator();
    }

    @Override
    protected SaveDialogBuilder getConfigurator() {
        return this;
    }

    @Override
    public Dialog build() {
        return getComponent();
    }
}
