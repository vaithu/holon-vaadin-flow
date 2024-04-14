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

	public DefaultConfirmDialogBuilder() {
		super();
		this.okButton = ButtonBuilder.create().text(Localizable.of("OK", DialogBuilder.DEFAULT_OK_BUTTON_MESSAGE_CODE))
				.withClickListener(e -> getComponent().close()).build();

		getComponent().addFooterComponent(this.okButton);


	}

	private void addCancelButton() {
		this.denyButton = ButtonBuilder.create()
				.text(Localizable.of("Cancel", DialogBuilder.DEFAULT_DENY_BUTTON_MESSAGE_CODE))
				.withClickListener(e -> {
					getComponent().close();
				}).build();

		this.denyButton.getStyle().set("margin-right", "auto");

		getComponent().addFooterComponent(this.denyButton);
	}

	public DefaultConfirmDialogBuilder(PropertyInputForm inputForm) {
		super();
		this.okButton = ButtonBuilder.create().text(Localizable.of("OK", DialogBuilder.DEFAULT_OK_BUTTON_MESSAGE_CODE))
				.withClickListener(e -> {
					if (inputForm.isValid()) getComponent().close();
				}).build();
		getComponent().addFooterComponent(this.okButton);
		addCancelButton();
	}

	public DefaultConfirmDialogBuilder(boolean okToCancelDialog) {
		super();
		this.okButton = ButtonBuilder.create().text(Localizable.of("OK", DialogBuilder.DEFAULT_OK_BUTTON_MESSAGE_CODE))
				.withClickListener(e -> {
					if (okToCancelDialog) getComponent().close();
				}).build();
		getComponent().addFooterComponent(this.okButton);

		addCancelButton();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected ConfirmDialogBuilder getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.DialogBuilder.ConfirmDialogBuilder#okButtonConfigurator(java.
	 * util.function.Consumer)
	 */
	@Override
	public ConfirmDialogBuilder okButtonConfigurator(Consumer<BaseButtonConfigurator> configurator) {
		ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
		configurator.accept(ButtonConfigurator.configure(okButton));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.holonplatform.vaadin.flow.components.builders.DialogBuilder.
	 * QuestionDialogBuilder#denialButtonConfigurator( java.util.function.Consumer)
	 */
	@Override
	public ConfirmDialogBuilder denialButtonConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator) {
		ObjectUtils.argumentNotNull(configurator, "Configurator must be not null");
		configurator.accept(ButtonConfigurator.configure(denyButton));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.DialogBuilder#build()
	 */
	@Override
	public Dialog build() {
		return getComponent();
	}

}
