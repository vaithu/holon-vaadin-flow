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
package com.holonplatform.vaadin.flow.internal.components;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Default {@link Dialog} to be used with dialog builders.
 *
 * @since 5.2.0
 */
public class DefaultDialog extends Dialog implements HasStyle {

	private static final long serialVersionUID = 5017183187214693820L;

	private final VerticalLayout content;
//	private final Div message;
	private final DialogFooter footer;

	private static final int MAX_WIDTH = 900;
	private static final int MAX_HEIGHT = 450;

	private static final String PIXELS = "px";


	/**
	 * Constructor.
	 */
	public DefaultDialog() {
		super();


		// default CSS style class name
		getElement().getClassList().add("h-dialog");

		Button closeButton = new Button(new Icon("lumo", "cross"),
				(e) -> close());
		closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		getHeader().add(closeButton);

//		this.message = new Div();
//		this.message.addClassName("message");
		this.footer = getFooter();
//		this.footer.addClassName("footer");

//		this.footer.setAlignItems(Alignment.END);

		this.content = new VerticalLayout();
		this.content.setSizeUndefined();
		this.content.addClassNames(LumoUtility.Padding.Top.NONE,
				LumoUtility.Padding.Bottom.NONE);
		this.content.setSpacing(false);
//		this.content.add(message);
//		this.content.add(this.footer);
//		this.content.setAlignSelf(Alignment.CENTER, this.message);
//		this.content.setAlignSelf(Alignment.END, this.footer);

//		this.message.setVisible(false);
//		this.footer.setVisible(false);
//		this.footer.setWidthFull();

		add(content);
	}

	/**
	 * Get the dialog message text.
	 * @return Optional message text
	 */
	public Optional<String> getMessage() {
		String text = getHeaderTitle();
		if (text != null && !text.trim().equals("")) {
			return Optional.of(text);
		}
		return Optional.empty();
	}

	/**
	 * Set the dialog message text.
	 * @param text the dialog message text, if <code>null</code> or blank the dialog message is removed
	 */
	public void setMessage(String text) {
		setHeaderTitle(text);
	}

	/**
	 * Add a component to dialog content.
	 * @param component The component to add (not null)
	 */
	public void addContentComponent(Component component) {
		ObjectUtils.argumentNotNull(component, "Component must be not null");
//		this.content.addComponentAtIndex(this.content.indexOf(this.message) + 1, component);
		this.content.setAlignSelf(Alignment.CENTER, component);
	}

	public void addContentComponent(Component... components) {
		this.content.add(components);
	}

	/**
	 * Get the dialog content components, excluding the message and footer components.
	 * @return the dialog content components
	 */
	public Stream<Component> getContentComponents() {
		return this.content.getChildren();
	}

	/**
	 * Add a component to the dialog footer.
	 * @param component The component to add (not null)
	 */
	public void addFooterComponent(Component component) {
		ObjectUtils.argumentNotNull(component, "Component must be not null");
		this.footer.add(component);
//		this.footer.setVisible(true);
	}

	public void makeDialogResponsive() {
		UI.getCurrent().getPage().retrieveExtendedClientDetails(details -> {
			setWidth(Math.max(MAX_WIDTH, details.getScreenWidth() / 2) + PIXELS);
			setHeight(Math.max(MAX_HEIGHT, details.getScreenHeight() / 2) + PIXELS);
		});

		UI.getCurrent().getPage().addBrowserWindowResizeListener(details -> {
			setWidth(Math.max(MAX_WIDTH, details.getWidth() / 2) + PIXELS);
			setHeight(Math.max(MAX_HEIGHT, details.getHeight() / 2) + PIXELS);
		});
	}
}
