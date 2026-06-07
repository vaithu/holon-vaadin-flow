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
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.components.support.ComponentClickListenerAdapter;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import tools.jackson.databind.JsonNode;

import java.util.Optional;

/**
 * Base {@link ButtonConfigurator} implementation.
 * 
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public abstract class AbstractButtonConfigurator<C extends ButtonConfigurator<C>>
		extends AbstractLocalizableComponentConfigurator<Button, C> implements ButtonConfigurator<C> {

	protected final DefaultHasTextConfigurator textConfigurator;
	protected final DefaultHasTitleConfigurator<Button> titleConfigurator;
	protected final DefaultHasTooltipConfigurator<Button> tooltipConfigurator;

	public AbstractButtonConfigurator(Button component) {
		super(component);
		this.textConfigurator = new DefaultHasTextConfigurator(component, this);
		this.titleConfigurator = new DefaultHasTitleConfigurator<>(component, title -> {
			component.getElement().setAttribute("title", (title != null) ? title : "");
		}, this);
		this.tooltipConfigurator = new DefaultHasTooltipConfigurator<>(component, component::setTooltipText, this);
	}

	@Override
	protected Optional<HasSize> hasSize() {
		return Optional.of(getComponent());
	}

	@Override
	protected Optional<HasStyle> hasStyle() {
		return Optional.of(getComponent());
	}

	@Override
	protected Optional<HasEnabled> hasEnabled() {
		return Optional.of(getComponent());
	}

	@Override
	public C icon(Component icon) {
		getComponent().setIcon(icon);
		return getConfigurator();
	}

	@Override
	public C icon() {
		getComponent().addThemeVariants(ButtonVariant.LUMO_ICON);
		return getConfigurator();
	}

	@Override
	public C image(Image image) {
		getComponent().setIcon(image);
		return getConfigurator();
	}

	@Override
	public C marginInlineEndAuto() {
		getComponent().addClassName("btn--push-end");
		return getConfigurator();
	}

	@Override
	public C marginInlineStartAuto() {
		getComponent().addClassName("btn--push-start");
		return getConfigurator();
	}

	private C border(String border) {
		// btn--outlined-border  → buttons.css: vaadin-button::part(base) { border: 1px solid; }
		// btn--tertiary         → buttons.css: background: transparent; box-shadow: none
		// <border>              → utilities.css: the specific border-color class
		getComponent().addClassNames("btn--outlined-border", "btn--tertiary", border);
		return getConfigurator();
	}

	@Override
	public C borderContrast() {
		return border("border-color-contrast");
	}

	@Override
	public C borderPrimary() {
		return border("border-color-primary-50");
	}

	@Override
	public C borderError() {
		return border("border-color-error-50");
	}

	@Override
	public C borderWarning() {
		return border("border-color-warning");
	}

	@Override
	public C borderSuccess() {
		return border("border-color-success-50");
	}

	@Override
	public C borderRadius() {
		getComponent().addClassName("btn--rounded");
		return getConfigurator();
	}

	@Override
	public Button getSource() {
		return getComponent();
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public IconConfigurator<C> iconConfigurator(Icon icon) {
		return new DefaultIconConfigurator(this, icon);
	}

	@Override
	public C withThemeVariants(ButtonVariant... variants) {
		getComponent().setThemeVariants(variants);
		return getConfigurator();
	}

	@Override
	public C text(Localizable text) {
		textConfigurator.text(text);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasTitleConfigurator#title(
	 * com.holonplatform.core.i18n. Localizable)
	 */
	@Override
	public C title(Localizable title) {
		titleConfigurator.title(title);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTooltipConfigurator#title(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public C tooltip(Localizable tooltip) {
		tooltipConfigurator.tooltip(tooltip);
		return getConfigurator();
	}

	@Override
	public C tooltipText(String text) {
		tooltipConfigurator.tooltipText(text);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator#
	 * disableOnClick()
	 */
	@Override
	public C disableOnClick() {
		getComponent().setDisableOnClick(true);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ClickNotifierConfigurator#
	 * withClickListener(com.holonplatform.
	 * vaadin.flow.components.events.ClickEventListener)
	 */
	@Override
	public C withClickListener(ClickEventListener<Button, ClickEvent<Button>> clickEventListener) {
		getComponent().addClickListener(new ComponentClickListenerAdapter<>(clickEventListener));
		return getConfigurator();
	}

	@Override
	public C withClickListener(ClickEventListener<Button, ClickEvent<Button>> clickEventListener, boolean avoidDoubleClick) {
		getComponent().getElement()
				.addEventListener(
						"click",
						e -> {
							JsonNode detail = e.getEventData().get("event.detail");
							if (avoidDoubleClick && detail.asInt() > 1) {
								// double click, ignore
							} else {
								getComponent().addClickListener(new ComponentClickListenerAdapter<>(clickEventListener));
							}
						}
				)
				.addEventData("event.detail");
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ClickNotifierConfigurator#
	 * withClickShortcut(com.vaadin.flow. component.Key,
	 * com.vaadin.flow.component.KeyModifier[])
	 */
	@Override
	public C withClickShortcutKey(Key key, KeyModifier... keyModifiers) {
		getComponent().addClickShortcut(key, keyModifiers);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.ClickNotifierConfigurator#
	 * withClickShortcut(com.vaadin.flow. component.Key)
	 */
	@Override
	public ShortcutConfigurator<C> withClickShortcut(Key key) {
		return new DefaultShortcutConfigurator<>(getComponent().addClickShortcut(key), getConfigurator());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
	 * tabIndex(int)
	 */
	@Override
	public C tabIndex(int tabIndex) {
		getComponent().setTabIndex(tabIndex);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
	 * withFocusListener(com.vaadin.flow. component.ComponentEventListener)
	 */
	@Override
	public C withFocusListener(ComponentEventListener<FocusEvent<Button>> listener) {
		getComponent().addFocusListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
	 * withBlurListener(com.vaadin.flow. component.ComponentEventListener)
	 */
	@Override
	public C withBlurListener(ComponentEventListener<BlurEvent<Button>> listener) {
		getComponent().addBlurListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#
	 * withFocusShortcut(com.vaadin.flow. component.Key)
	 */
	@Override
	public ShortcutConfigurator<C> withFocusShortcut(Key key) {
		return new DefaultShortcutConfigurator<>(getComponent().addFocusShortcut(key), getConfigurator());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator#
	 * iconAfterText(boolean)
	 */
	@Override
	public C iconAfterText(boolean iconAfterText) {
		getComponent().setIconAfterText(iconAfterText);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator#
	 * autofocus(boolean)
	 */
	@Override
	public C autofocus(boolean autofocus) {
		getComponent().setAutofocus(autofocus);
		return getConfigurator();
	}

	@Override
	public C primary() {
		getComponent().addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		return getConfigurator();
	}

	@Override
	public C secondary() {
		//this is the default button so no variant is required
		return getConfigurator();
	}

	@Override
	public C tertiary() {
		getComponent().addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		return getConfigurator();
	}

	@Override
	public C error() {
		getComponent().addThemeVariants(ButtonVariant.LUMO_ERROR);
		return getConfigurator();
	}

	@Override
	public C large() {
		getComponent().addThemeVariants(ButtonVariant.LUMO_LARGE);
		return getConfigurator();
	}

	@Override
	public C small() {
		getComponent().addThemeVariants(ButtonVariant.LUMO_SMALL);
		return getConfigurator();
	}

	@Override
	public C normal() {
		//this is the default button so no variant is required
		return getConfigurator();
	}

	@Override
	public C tertiaryInline() {
		getComponent().addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		return getConfigurator();
	}

	@Override
	public C success() {
		getComponent().addThemeVariants(ButtonVariant.SUCCESS);
		return getConfigurator();
	}

	@Override
	public C contrast() {
		getComponent().addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		return getConfigurator();
	}

	@Override
	public C ariaLabel(String ariaLabel) {
		getComponent().setAriaLabel(ariaLabel);
		return getConfigurator();
	}

	@Override
	public C ariaLabelledBy(String ariaLabelledBy) {
		getComponent().setAriaLabelledBy(ariaLabelledBy);
		return getConfigurator();
	}

	@Override
	public C ariaLabel(Localizable ariaLabel) {
		final String defaultAriaLabel = (ariaLabel != null && ariaLabel.getMessage() != null) ? ariaLabel.getMessage()
				: "";
		if (ariaLabel == null) {
			return ariaLabel(defaultAriaLabel);
		}
		if (isDeferredLocalizationEnabled()) {
			ariaLabel(defaultAriaLabel);
			return withAttachListener(event -> {
				if (event.isInitialAttach()) {
					LocalizationProvider.localize(ariaLabel).ifPresent(this::ariaLabel);
				}
			});
		}
		return ariaLabel(LocalizationProvider.localize(ariaLabel).orElse(defaultAriaLabel));
	}
}
