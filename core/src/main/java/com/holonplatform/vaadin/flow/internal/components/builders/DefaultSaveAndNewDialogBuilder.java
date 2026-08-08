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

public class DefaultSaveAndNewDialogBuilder extends AbstractDialogConfigurator<DialogBuilder.SaveAndNewDialogBuilder>
        implements DialogBuilder.SaveAndNewDialogBuilder {

    private final Button saveAndNewButton;
    private final Button denyButton;

    public DefaultSaveAndNewDialogBuilder(QuestionDialogCallback questionDialogCallback) {
        super();
        ObjectUtils.argumentNotNull(questionDialogCallback, "SaveAndNew dialog callback must be not null");

        this.saveAndNewButton = ButtonBuilder.create()
                .text(Localizable.of("Save&New", DEFAULT_SAVE_NEW_BUTTON_MESSAGE_CODE))
                .styleName("h-dialog__action-btn")
                .withClickListener(e -> {
                    // Close first; the caller's callback handles form reset / re-open
                    getComponent().attemptClose();
                    questionDialogCallback.onUserAnswer(true);
                })
                .withClickShortcutKey(Key.ENTER)
                .build();

        this.denyButton = ButtonBuilder.create()
                .text(Localizable.of("Cancel", DEFAULT_DENY_BUTTON_MESSAGE_CODE))
                .styleName("h-dialog__cancel-btn")
                .withClickListener(e -> {
                    getComponent().attemptClose();
                    questionDialogCallback.onUserAnswer(false);
                })
                .build();

        getComponent().addFooterComponent(this.denyButton);
        getComponent().addFooterComponent(this.saveAndNewButton);

        getComponent().setCloseOnEsc(true);
        getComponent().setResizable(true);
        getComponent().setDraggable(true);
        getComponent().setCloseOnOutsideClick(false);
        getComponent().setModality(ModalityMode.STRICT);
    }

    @Override
    public SaveAndNewDialogBuilder saveAndNewButtonConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(ButtonConfigurator.configure(saveAndNewButton));
        return getConfigurator();
    }

    @Override
    public SaveAndNewDialogBuilder denialButtonConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
        ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
        configurator.accept(ButtonConfigurator.configure(denyButton));
        return getConfigurator();
    }

    @Override
    protected SaveAndNewDialogBuilder getConfigurator() {
        return this;
    }

    @Override
    public Dialog build() {
        return getComponent();
    }
}
