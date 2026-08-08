/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.iyensoft.vaadin.flow.enums.ButtonPreset;
import com.iyensoft.vaadin.flow.enums.ButtonSize;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultButtonConfigurator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;

/**
 * {@link Button} component configurator.
 *
 * @param <C> Concrete configurator type
 *
 * @since 5.2.0
 */
public interface ButtonConfigurator<C extends ButtonConfigurator<C>> extends ComponentConfigurator<C>,
		HasSizeConfigurator<C>, HasStyleConfigurator<C>, HasIconConfigurator<C>, HasTextConfigurator<C>,HasTooltipConfigurator<C>,
		HasEnabledConfigurator<C>, HasTitleConfigurator<C>, HasAriaLabelConfigurator<C>,
		ClickNotifierConfigurator<Button, ClickEvent<Button>, C>,
		FocusableConfigurator<Button, C>, HasAutofocusConfigurator<C>, HasThemeVariantConfigurator<ButtonVariant, C>,
		DeferrableLocalizationConfigurator<C> {

	/**
	 * Sets whether this button's icon should be positioned after it's text content or the other way around.
	 * @param iconAfterText Whether the icon should be positioned after the button text
	 * @return this
	 */
	C iconAfterText(boolean iconAfterText);


	/**
	 * Set the button to be input focused when the page loads.
	 * @return this
	 */
	default C autofocus() {
		return autofocus(true);
	}

	/**
	 * Automatically disables button when clicked, typically to prevent (accidental) extra clicks on a button.
	 * @return this
	 */
	C disableOnClick();

	C primary();

	C secondary();

	C tertiary();

	C error();

	C large();

	C small();

	C normal();

	/**
	 * Sets the button size via a {@link ButtonSize}, replacing the whole-button size
	 * (font-size, padding, min-height) defined in {@code buttons.css}.
	 * <ul>
	 *   <li>{@link ButtonSize#SMALL}  → compact, 2rem height</li>
	 *   <li>{@link ButtonSize#NORMAL} → default shell-theme size (~36px)</li>
	 *   <li>{@link ButtonSize#LARGE}  → spacious, 3.25rem height</li>
	 * </ul>
	 * @param size button size (not null)
	 * @return this
	 */
	C size(ButtonSize size);

	C tertiaryInline();

	C success();

	C contrast();

	C icon();

	C image(Image image);

	C marginInlineEndAuto();
	C marginInlineStartAuto();

	C borderContrast();
	C borderPrimary();
	C borderError();
	C borderWarning();
	C borderSuccess();
	C borderRadius();

	Button getSource();

	C withClickListener(ClickEventListener<Button, ClickEvent<Button>> clickEventListener, boolean avoidDoubleClick);

	C preset(ButtonPreset preset);

	/**
	 * Styles the button as a pill-shaped filter chip (inactive state).
	 * Applies {@code theme="tertiary"} and adds class {@code btn--chip}.
	 * Requires {@code chip.css} to be loaded (auto-loaded when using {@link ChipGroup}).
	 * @return this
	 */
	C chip();

	/**
	 * Toggles the chip's active (selected) visual state.
	 * Adds or removes class {@code btn--chip-active}.
	 * @param active {@code true} to show the filled brand-tinted active state
	 * @return this
	 */
	C chipActive(boolean active);


	/**
	 * Base button configurator.
	 */
	interface BaseButtonConfigurator extends ButtonConfigurator<BaseButtonConfigurator> {

	}

	/**
	 * Obtain a {@link ButtonConfigurator} to create given {@link Button} component.
	 * @param button The component to create (not null)
	 * @return A {@link ButtonConfigurator} to create given component
	 */
	static BaseButtonConfigurator configure(Button button) {
		return new DefaultButtonConfigurator(button);
	}

}
