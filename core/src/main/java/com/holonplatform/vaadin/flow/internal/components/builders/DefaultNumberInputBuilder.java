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

import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ValidatableInputBuilder;
import com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter;
import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.CompositionEndEvent;
import com.vaadin.flow.component.CompositionStartEvent;
import com.vaadin.flow.component.CompositionUpdateEvent;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.InputEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyDownEvent;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.KeyUpEvent;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

/**
 * Default {@link NumberInputBuilder} implementation using a {@link TextField} as concrete component.
 *
 * @param <T> Number type
 *
 * @since 5.2.0
 */
public class DefaultNumberInputBuilder<T extends Number> extends
		AbstractLocalizableComponentConfigurator<TextField, NumberInputBuilder<T>> implements NumberInputBuilder<T> {

	private final List<ValueChangeListener<T>> valueChangeListeners = new LinkedList<>();

	private final Class<T> numberType;

	private T initialValue;

	private StringToNumberConverter<T> converter;

	protected final DefaultHasSizeConfigurator sizeConfigurator;
	protected final DefaultHasStyleConfigurator styleConfigurator;
	protected final DefaultHasEnabledConfigurator enabledConfigurator;
	protected final DefaultHasAutocompleteConfigurator autocompleteConfigurator;
	protected final DefaultHasPrefixAndSuffixConfigurator prefixAndSuffixConfigurator;
	protected final DefaultCompositionNotifierConfigurator compositionNotifierConfigurator;
	protected final DefaultInputNotifierConfigurator inputNotifierConfigurator;
	protected final DefaultKeyNotifierConfigurator keyNotifierConfigurator;
	protected final DefaultHasValueChangeModeConfigurator valueChangeModeConfigurator;
	protected final DefaultHasLabelConfigurator<TextField> labelConfigurator;
	protected final DefaultHasTitleConfigurator<TextField> titleConfigurator;
	protected final DefaultHasPlaceholderConfigurator<TextField> placeholderConfigurator;

	public DefaultNumberInputBuilder(Class<T> numberType) {
		super(new TextField());
		ObjectUtils.argumentNotNull(numberType, "Number type must be not null");
		this.numberType = numberType;
		this.converter = StringToNumberConverter.create(numberType);

		getComponent().setAutocapitalize(Autocapitalize.NONE);
		getComponent().setAutocorrect(false);
		getComponent().setAutocomplete(Autocomplete.OFF);

		sizeConfigurator = new DefaultHasSizeConfigurator(getComponent());
		styleConfigurator = new DefaultHasStyleConfigurator(getComponent());
		enabledConfigurator = new DefaultHasEnabledConfigurator(getComponent());
		autocompleteConfigurator = new DefaultHasAutocompleteConfigurator(getComponent());
		prefixAndSuffixConfigurator = new DefaultHasPrefixAndSuffixConfigurator(getComponent());
		compositionNotifierConfigurator = new DefaultCompositionNotifierConfigurator(getComponent());
		inputNotifierConfigurator = new DefaultInputNotifierConfigurator(getComponent());
		keyNotifierConfigurator = new DefaultKeyNotifierConfigurator(getComponent());
		valueChangeModeConfigurator = new DefaultHasValueChangeModeConfigurator(getComponent());

		labelConfigurator = new DefaultHasLabelConfigurator<>(getComponent(), label -> {
			getComponent().setLabel(label);
		}, this);
		titleConfigurator = new DefaultHasTitleConfigurator<>(getComponent(), title -> {
			getComponent().setTitle(title);
		}, this);
		placeholderConfigurator = new DefaultHasPlaceholderConfigurator<>(getComponent(), placeholder -> {
			getComponent().setPlaceholder(placeholder);
		}, this);
	}

	/**
	 * Get the {@link StringToNumberConverter} instance.
	 * @return the converter
	 */
	protected StringToNumberConverter<T> getConverter() {
		if (converter == null) {
			converter = StringToNumberConverter.create(numberType);
		}
		return converter;
	}

	protected void replaceConverter(StringToNumberConverter<T> converter) {
		final StringToNumberConverter<T> oldConverter = this.converter;
		this.converter = converter;
		if (oldConverter != null) {
			this.converter.setUseGrouping(oldConverter.isUseGrouping());
			this.converter.setAllowNegatives(oldConverter.isAllowNegatives());
			this.converter.setMinDecimals(oldConverter.getMinDecimals());
			this.converter.setMaxDecimals(oldConverter.getMaxDecimals());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
	 */
	@Override
	protected NumberInputBuilder<T> getConfigurator() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#build()
	 */
	@Override
	public Input<T> build() {
		final TextField component = getComponent();

		// pattern
		component.setPattern(getConverter().getValidationPattern());
		component.setPreventInvalidInput(true);

		final Input<String> input = Input.builder(component).emptyValueSupplier(field -> null)
				.requiredPropertyHandler((f, c) -> f.isRequired(), (f, c, v) -> f.setRequired(v))
				.labelPropertyHandler((f, c) -> c.getLabel(), (f, c, v) -> c.setLabel(v))
				.titlePropertyHandler((f, c) -> c.getTitle(), (f, c, v) -> c.setTitle(v))
				.placeholderPropertyHandler((f, c) -> c.getPlaceholder(), (f, c, v) -> c.setPlaceholder(v))
				.focusOperation(f -> f.focus()).hasEnabledSupplier(f -> f).build();

		// conversion
		final Input<T> numberInput = Input.from(input, getConverter());
		if (initialValue != null) {
			numberInput.setValue(initialValue);
		}
		valueChangeListeners.forEach(listener -> numberInput.addValueChangeListener(listener));
		return numberInput;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder#locale(java.util.Locale)
	 */
	@Override
	public NumberInputBuilder<T> locale(Locale locale) {
		replaceConverter(StringToNumberConverter.create(numberType, locale));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder#numberFormat(java.text.NumberFormat)
	 */
	@Override
	public NumberInputBuilder<T> numberFormat(NumberFormat numberFormat) {
		replaceConverter(StringToNumberConverter.create(numberType, numberFormat));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder#numberFormatPattern(java.lang.String)
	 */
	@Override
	public NumberInputBuilder<T> numberFormatPattern(String numberFormatPattern) {
		replaceConverter(StringToNumberConverter.create(numberType, numberFormatPattern));
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder#allowNegative(boolean)
	 */
	@Override
	public NumberInputBuilder<T> allowNegative(boolean allowNegative) {
		getConverter().setAllowNegatives(allowNegative);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder#useGrouping(boolean)
	 */
	@Override
	public NumberInputBuilder<T> useGrouping(boolean useGrouping) {
		getConverter().setUseGrouping(useGrouping);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder#minDecimals(int)
	 */
	@Override
	public NumberInputBuilder<T> minDecimals(int minDecimals) {
		getConverter().setMinDecimals(minDecimals);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder#maxDecimals(int)
	 */
	@Override
	public NumberInputBuilder<T> maxDecimals(int maxDecimals) {
		getConverter().setMaxDecimals(maxDecimals);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputBuilder#validatable()
	 */
	@Override
	public ValidatableInputBuilder<T, ValidatableInput<T>> validatable() {
		return ValidatableInputBuilder.create(build());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#readOnly(boolean)
	 */
	@Override
	public NumberInputBuilder<T> readOnly(boolean readOnly) {
		getComponent().setReadOnly(readOnly);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValue(java.lang.Object)
	 */
	@Override
	public NumberInputBuilder<T> withValue(T value) {
		this.initialValue = value;
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputConfigurator#withValueChangeListener(com.holonplatform.
	 * vaadin.flow.components.ValueHolder.ValueChangeListener)
	 */
	@Override
	public NumberInputBuilder<T> withValueChangeListener(ValueChangeListener<T> listener) {
		ObjectUtils.argumentNotNull(listener, "ValueChangeListener must be not null");
		this.valueChangeListeners.add(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.InputConfigurator#required(boolean)
	 */
	@Override
	public NumberInputBuilder<T> required(boolean required) {
		getComponent().setRequired(required);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.TextInputConfigurator#pattern(java.lang.String)
	 */
	@Override
	public NumberInputBuilder<T> pattern(String pattern) {
		getComponent().setPattern(pattern);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.TextInputConfigurator#preventInvalidInput(boolean)
	 */
	@Override
	public NumberInputBuilder<T> preventInvalidInput(boolean preventInvalidInput) {
		getComponent().setPreventInvalidInput(preventInvalidInput);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasAutocompleteConfigurator#autocomplete(com.vaadin.flow.
	 * component.textfield.Autocomplete)
	 */
	@Override
	public NumberInputBuilder<T> autocomplete(Autocomplete autocomplete) {
		autocompleteConfigurator.autocomplete(autocomplete);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasEnabledConfigurator#enabled(boolean)
	 */
	@Override
	public NumberInputBuilder<T> enabled(boolean enabled) {
		enabledConfigurator.enabled(enabled);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.InputNotifierConfigurator#withInputListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public NumberInputBuilder<T> withInputListener(ComponentEventListener<InputEvent> listener) {
		inputNotifierConfigurator.withInputListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.KeyNotifierConfigurator#withKeyDownListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public NumberInputBuilder<T> withKeyDownListener(ComponentEventListener<KeyDownEvent> listener) {
		keyNotifierConfigurator.withKeyDownListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.KeyNotifierConfigurator#withKeyPressListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public NumberInputBuilder<T> withKeyPressListener(ComponentEventListener<KeyPressEvent> listener) {
		keyNotifierConfigurator.withKeyPressListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.KeyNotifierConfigurator#withKeyUpListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@Override
	public NumberInputBuilder<T> withKeyUpListener(ComponentEventListener<KeyUpEvent> listener) {
		keyNotifierConfigurator.withKeyUpListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.KeyNotifierConfigurator#withKeyDownListener(com.vaadin.flow.
	 * component.Key, com.vaadin.flow.component.ComponentEventListener, com.vaadin.flow.component.KeyModifier[])
	 */
	@Override
	public NumberInputBuilder<T> withKeyDownListener(Key key, ComponentEventListener<KeyDownEvent> listener,
			KeyModifier... modifiers) {
		keyNotifierConfigurator.withKeyDownListener(key, listener, modifiers);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.KeyNotifierConfigurator#withKeyPressListener(com.vaadin.flow.
	 * component.Key, com.vaadin.flow.component.ComponentEventListener, com.vaadin.flow.component.KeyModifier[])
	 */
	@Override
	public NumberInputBuilder<T> withKeyPressListener(Key key, ComponentEventListener<KeyPressEvent> listener,
			KeyModifier... modifiers) {
		keyNotifierConfigurator.withKeyPressListener(key, listener, modifiers);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.KeyNotifierConfigurator#withKeyUpListener(com.vaadin.flow.
	 * component.Key, com.vaadin.flow.component.ComponentEventListener, com.vaadin.flow.component.KeyModifier[])
	 */
	@Override
	public NumberInputBuilder<T> withKeyUpListener(Key key, ComponentEventListener<KeyUpEvent> listener,
			KeyModifier... modifiers) {
		keyNotifierConfigurator.withKeyUpListener(key, listener, modifiers);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasValueChangeModeConfigurator#valueChangeMode(com.vaadin.flow.
	 * data.value.ValueChangeMode)
	 */
	@Override
	public NumberInputBuilder<T> valueChangeMode(ValueChangeMode valueChangeMode) {
		valueChangeModeConfigurator.valueChangeMode(valueChangeMode);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#width(java.lang.String)
	 */
	@Override
	public NumberInputBuilder<T> width(String width) {
		sizeConfigurator.width(width);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasSizeConfigurator#height(java.lang.String)
	 */
	@Override
	public NumberInputBuilder<T> height(String height) {
		sizeConfigurator.height(height);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleNames(java.lang.String[])
	 */
	@Override
	public NumberInputBuilder<T> styleNames(String... styleNames) {
		styleConfigurator.styleNames(styleNames);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#styleName(java.lang.String)
	 */
	@Override
	public NumberInputBuilder<T> styleName(String styleName) {
		styleConfigurator.styleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#removeStyleName(java.lang.String)
	 */
	@Override
	public NumberInputBuilder<T> removeStyleName(String styleName) {
		styleConfigurator.removeStyleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasStyleConfigurator#replaceStyleName(java.lang.String)
	 */
	@Override
	public NumberInputBuilder<T> replaceStyleName(String styleName) {
		styleConfigurator.replaceStyleName(styleName);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasAutofocusConfigurator#autofocus(boolean)
	 */
	@Override
	public NumberInputBuilder<T> autofocus(boolean autofocus) {
		getComponent().setAutofocus(autofocus);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#tabIndex(int)
	 */
	@Override
	public NumberInputBuilder<T> tabIndex(int tabIndex) {
		getComponent().setTabIndex(tabIndex);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withFocusListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@SuppressWarnings("serial")
	@Override
	public NumberInputBuilder<T> withFocusListener(ComponentEventListener<FocusEvent<Component>> listener) {
		getComponent().addFocusListener(new ComponentEventListener<FocusNotifier.FocusEvent<TextField>>() {

			@Override
			public void onComponentEvent(FocusEvent<TextField> event) {
				listener.onComponentEvent(new FocusEvent<Component>(event.getSource(), event.isFromClient()));
			}

		});
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.FocusableConfigurator#withBlurListener(com.vaadin.flow.
	 * component.ComponentEventListener)
	 */
	@SuppressWarnings("serial")
	@Override
	public NumberInputBuilder<T> withBlurListener(ComponentEventListener<BlurEvent<Component>> listener) {
		getComponent().addBlurListener(new ComponentEventListener<BlurNotifier.BlurEvent<TextField>>() {

			@Override
			public void onComponentEvent(BlurEvent<TextField> event) {
				listener.onComponentEvent(new BlurEvent<Component>(event.getSource(), event.isFromClient()));
			}

		});
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasPrefixAndSuffixConfigurator#prefixComponent(com.vaadin.flow.
	 * component.Component)
	 */
	@Override
	public NumberInputBuilder<T> prefixComponent(Component component) {
		prefixAndSuffixConfigurator.prefixComponent(component);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasPrefixAndSuffixConfigurator#suffixComponent(com.vaadin.flow.
	 * component.Component)
	 */
	@Override
	public NumberInputBuilder<T> suffixComponent(Component component) {
		prefixAndSuffixConfigurator.suffixComponent(component);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.CompositionNotifierConfigurator#withCompositionStartListener(
	 * com.vaadin.flow.component.ComponentEventListener)
	 */
	@Override
	public NumberInputBuilder<T> withCompositionStartListener(ComponentEventListener<CompositionStartEvent> listener) {
		compositionNotifierConfigurator.withCompositionStartListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.CompositionNotifierConfigurator#withCompositionUpdateListener(
	 * com.vaadin.flow.component.ComponentEventListener)
	 */
	@Override
	public NumberInputBuilder<T> withCompositionUpdateListener(
			ComponentEventListener<CompositionUpdateEvent> listener) {
		compositionNotifierConfigurator.withCompositionUpdateListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.CompositionNotifierConfigurator#withCompositionEndListener(com.
	 * vaadin.flow.component.ComponentEventListener)
	 */
	@Override
	public NumberInputBuilder<T> withCompositionEndListener(ComponentEventListener<CompositionEndEvent> listener) {
		compositionNotifierConfigurator.withCompositionEndListener(listener);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.builders.HasPlaceholderConfigurator#placeholder(com.holonplatform.core.
	 * i18n.Localizable)
	 */
	@Override
	public NumberInputBuilder<T> placeholder(Localizable placeholder) {
		placeholderConfigurator.placeholder(placeholder);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasLabelConfigurator#label(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public NumberInputBuilder<T> label(Localizable label) {
		labelConfigurator.label(label);
		return getConfigurator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.builders.HasTitleConfigurator#title(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public NumberInputBuilder<T> title(Localizable title) {
		titleConfigurator.title(title);
		return getConfigurator();
	}

}