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
import com.vaadin.flow.component.ModalityMode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

import java.util.function.Consumer;

/**
 * Default {@link DialogBuilder.DeleteDialogBuilder} implementation.
 *
 * @since 5.5.4
 */
public class DefaultDeleteDialogBuilder extends AbstractDialogConfigurator<DialogBuilder.DeleteDialogBuilder>
		implements DialogBuilder.DeleteDialogBuilder {

	private final Button confirmButton;
	private final Button denyButton;

	public DefaultDeleteDialogBuilder(DeleteDialogCallback deleteDialogCallback) {

		super();
		ObjectUtils.argumentNotNull(deleteDialogCallback, "Delete dialog callback must be not null");

		this.confirmButton = ButtonBuilder.create()
				.text(Localizable.of("Delete", DEFAULT_DELETE_BUTTON_MESSAGE_CODE))
				.styleName("h-dialog__action-btn h-dialog__action-btn--destructive")
				.withClickListener(e -> {
					getComponent().attemptClose();
					deleteDialogCallback.onUserAnswer(true);
				})
				.build();
		this.denyButton = ButtonBuilder.create()
				.text(Localizable.of("Cancel", DEFAULT_DENY_BUTTON_MESSAGE_CODE))
				.styleName("h-dialog__cancel-btn")
				.autofocus()
				.withClickListener(e -> {
					getComponent().attemptClose();
					deleteDialogCallback.onUserAnswer(false);
				})
				.build();

		getComponent().addFooterComponent(this.denyButton);
		getComponent().addFooterComponent(this.confirmButton);

		// Force explicit choice — ESC must not silently dismiss without firing the deny callback
		getComponent().setCloseOnEsc(false);
		getComponent().setCloseOnOutsideClick(false);

		// since 5.5.0: set modal by default
		getComponent().setModality(ModalityMode.STRICT);

		// shadcn/ui AlertDialog pattern — hide close button to force explicit choice
		getComponent().setCloseButtonVisible(false);

	}

	@Override
	public DeleteDialogBuilder confirmButtonConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
		ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
		configurator.accept(ButtonConfigurator.configure(confirmButton));
		return getConfigurator();
	}

	@Override
	public DeleteDialogBuilder denialButtonConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
		ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
		configurator.accept(ButtonConfigurator.configure(denyButton));
		return getConfigurator();
	}

	@Override
	protected DeleteDialogBuilder getConfigurator() {
		return this;
	}

	@Override
	public Dialog build() {
		return getComponent();
	}

}
