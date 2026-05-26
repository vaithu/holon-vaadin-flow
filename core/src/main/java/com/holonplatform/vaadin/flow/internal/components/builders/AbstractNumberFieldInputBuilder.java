/*
 * Copyright 2016-2019 Axioma srl.
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
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ValidatableInput;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeEvent;
import com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener;
import com.holonplatform.vaadin.flow.components.builders.NumberInputConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ShortcutConfigurator;
import com.holonplatform.vaadin.flow.components.converters.StringToNumberConverter;
import com.holonplatform.vaadin.flow.components.events.ReadonlyChangeListener;
import com.holonplatform.vaadin.flow.components.support.InputAdaptersContainer;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.BlurNotifier.BlurEvent;
import com.vaadin.flow.component.FocusNotifier.FocusEvent;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Base NumberField-backed NumberInputConfigurator implementation.
 *
 * @param <T> Number type
 * @param <C> Concrete configurator type
 */
public abstract class AbstractNumberFieldInputBuilder<T extends Number, C extends NumberInputConfigurator<T, C>>
		extends AbstractInputConfigurator<T, ValueChangeEvent<T>, NumberField, C>
		implements NumberInputConfigurator<T, C> {

	private final Class<T> numberType;
	private T initialValue;
	private StringToNumberConverter<T> converter;
	private String customPattern;

	protected final DefaultHasAutocompleteConfigurator autocompleteConfigurator;
	protected final DefaultHasPrefixAndSuffixConfigurator prefixAndSuffixConfigurator;
	protected final DefaultCompositionNotifierConfigurator compositionNotifierConfigurator;
	protected final DefaultInputNotifierConfigurator inputNotifierConfigurator;
	protected final DefaultKeyNotifierConfigurator keyNotifierConfigurator;
	protected final DefaultHasValueChangeModeConfigurator valueChangeModeConfigurator;
	protected final DefaultHasLabelConfigurator<NumberField> labelConfigurator;
	protected final DefaultHasTitleConfigurator<NumberField> titleConfigurator;
	protected final DefaultHasTooltipConfigurator<NumberField> tooltipConfigurator;
	protected final DefaultHasHelperTextConfigurator<NumberField> helperTextConfigurator;
	protected final DefaultHasPlaceholderConfigurator<NumberField> placeholderConfigurator;

	public AbstractNumberFieldInputBuilder(Class<T> numberType) {
		this(numberType, new NumberField(), null, StringToNumberConverter.create(numberType), Collections.emptyList(),
				Collections.emptyList(), InputAdaptersContainer.create());
	}

	public AbstractNumberFieldInputBuilder(Class<T> numberType, NumberField component, T initialValue,
			StringToNumberConverter<T> converter,
			List<ValueChangeListener<T, ValueChangeEvent<T>>> valueChangeListeners,
			List<ReadonlyChangeListener> readonlyChangeListeners, InputAdaptersContainer<T> adapters) {
		super(component, adapters);
		ObjectUtils.argumentNotNull(numberType, "Number type must be not null");
		this.numberType = numberType;
		this.initialValue = initialValue;
		this.converter = converter;
		initValueChangeListeners(valueChangeListeners);
		initReadonlyChangeListeners(readonlyChangeListeners);

		getComponent().setAutocapitalize(Autocapitalize.NONE);
		getComponent().setAutocorrect(false);
		getComponent().setAutocomplete(Autocomplete.OFF);
		getComponent().setClearButtonVisible(true);

		autocompleteConfigurator = new DefaultHasAutocompleteConfigurator(getComponent());
		prefixAndSuffixConfigurator = new DefaultHasPrefixAndSuffixConfigurator(getComponent(), getComponent());
		compositionNotifierConfigurator = new DefaultCompositionNotifierConfigurator(getComponent());
		inputNotifierConfigurator = new DefaultInputNotifierConfigurator(getComponent());
		keyNotifierConfigurator = new DefaultKeyNotifierConfigurator(getComponent());
		valueChangeModeConfigurator = new DefaultHasValueChangeModeConfigurator(getComponent());

		labelConfigurator = new DefaultHasLabelConfigurator<>(getComponent(), getComponent()::setLabel, this);
		titleConfigurator = new DefaultHasTitleConfigurator<>(getComponent(), getComponent()::setTitle, this);
		tooltipConfigurator = new DefaultHasTooltipConfigurator<>(getComponent(), getComponent()::setTooltipText, this);
		helperTextConfigurator = new DefaultHasHelperTextConfigurator<>(getComponent(), getComponent()::setHelperText,
				this);
		placeholderConfigurator = new DefaultHasPlaceholderConfigurator<>(getComponent(), getComponent()::setPlaceholder,
				this);
	}

	protected T getInitialValue() {
		return initialValue;
	}

	protected Class<T> getNumberType() {
		return numberType;
	}

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
			this.converter.setAllowNegatives(oldConverter.isAllowNegatives());
			this.converter.setMinDecimals(oldConverter.getMinDecimals());
			this.converter.setMaxDecimals(oldConverter.getMaxDecimals());
		}
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
	protected Optional<HasTooltip> hasTooltip() {
		return Optional.of(getComponent());
	}

	protected Input<T> buildAsInput() {
		final NumberField component = getComponent();
		if (customPattern != null) {
			component.getElement().setProperty("pattern", customPattern);
		} else {
			component.setAllowedCharPattern(getConverter().getValidationPattern());
		}

		final Input<Double> input = Input.builder(component).emptyValueSupplier(field -> null)
				.requiredPropertyHandler((f, c) -> f.isRequired(), (f, c, v) -> f.setRequired(v))
				.labelPropertyHandler((f, c) -> c.getLabel(), (f, c, v) -> c.setLabel(v))
				.titlePropertyHandler((f, c) -> c.getTitle(), (f, c, v) -> c.setTitle(v))
				.placeholderPropertyHandler((f, c) -> c.getPlaceholder(), (f, c, v) -> c.setPlaceholder(v))
				.focusOperation(Focusable::focus).hasEnabledSupplier(f -> f).build();

		final Converter<Double, T> numberConverter = Converter.from(
				modelValue -> toType(modelValue),
				presentationValue -> fromType(presentationValue),
				Exception::getMessage);

		final Input<T> numberInput = Input.builder(input, numberConverter)
				.withValueChangeListeners(getValueChangeListeners())
				.withReadonlyChangeListeners(getReadonlyChangeListeners())
				.withAdapters(getAdapters()).build();
		if (initialValue != null) {
			numberInput.setValue(initialValue);
		}
		return numberInput;
	}

	protected ValidatableInput<T> buildAsValidatableInput() {
		return ValidatableInput.from(buildAsInput());
	}

	private T toType(Double modelValue) {
		if (modelValue == null) {
			return null;
		}
		if (!getConverter().isAllowNegatives() && modelValue < 0d) {
			throw new IllegalArgumentException("Negative values are not allowed");
		}
		try {
			BigDecimal value = BigDecimal.valueOf(modelValue);
			if (getConverter().getMaxDecimals() >= 0) {
				value = value.setScale(getConverter().getMaxDecimals(), RoundingMode.HALF_UP);
			}
			if (numberType == BigDecimal.class) {
				return numberType.cast(value);
			}
			if (numberType == Double.class) {
				return numberType.cast(value.doubleValue());
			}
			if (numberType == Float.class) {
				return numberType.cast(value.floatValue());
			}

			if (value.stripTrailingZeros().scale() > 0) {
				throw new IllegalArgumentException("Decimal value not allowed for " + numberType.getSimpleName());
			}
			if (numberType == Integer.class) {
				return numberType.cast(value.intValueExact());
			}
			if (numberType == Long.class) {
				return numberType.cast(value.longValueExact());
			}
			if (numberType == Short.class) {
				return numberType.cast(value.shortValueExact());
			}
			if (numberType == Byte.class) {
				return numberType.cast(value.byteValueExact());
			}
			if (numberType == BigInteger.class) {
				return numberType.cast(value.toBigIntegerExact());
			}
			throw new IllegalArgumentException("Unsupported number type: " + numberType.getName());
		} catch (Exception e) {
			throw (e instanceof IllegalArgumentException)
					? (IllegalArgumentException) e
					: new IllegalArgumentException(e.getMessage(), e);
		}
	}

	private Double fromType(T presentationValue) {
		if (presentationValue == null) {
			return null;
		}
		return presentationValue.doubleValue();
	}

	@Override
	public C locale(Locale locale) {
		replaceConverter(StringToNumberConverter.create(numberType, locale));
		return getConfigurator();
	}

	@Override
	public C numberFormat(NumberFormat numberFormat) {
		replaceConverter(StringToNumberConverter.create(numberType, numberFormat));
		return getConfigurator();
	}

	@Override
	public C numberFormatPattern(String numberFormatPattern) {
		replaceConverter(StringToNumberConverter.create(numberType, numberFormatPattern));
		return getConfigurator();
	}

	@Override
	public C allowNegative(boolean allowNegative) {
		getConverter().setAllowNegatives(allowNegative);
		return getConfigurator();
	}

	@Override
	public C useGrouping(boolean useGrouping) {
		getConverter().setUseGrouping(useGrouping);
		return getConfigurator();
	}

	@Override
	public C minDecimals(int minDecimals) {
		getConverter().setMinDecimals(minDecimals);
		return getConfigurator();
	}

	@Override
	public C maxDecimals(int maxDecimals) {
		getConverter().setMaxDecimals(maxDecimals);
		return getConfigurator();
	}

	@Override
	public C readOnly(boolean readOnly) {
		getComponent().setReadOnly(readOnly);
		return getConfigurator();
	}

	@Override
	public C withValue(T value) {
		this.initialValue = value;
		return getConfigurator();
	}

	@Override
	public C pattern(String pattern) {
		this.customPattern = pattern;
		getComponent().getElement().setProperty("pattern", pattern);
		return getConfigurator();
	}

	@Override
	public C allowedCharPattern(String pattern) {
		getComponent().setAllowedCharPattern(pattern);
		return getConfigurator();
	}

	@Override
	public C autocomplete(Autocomplete autocomplete) {
		autocompleteConfigurator.autocomplete(autocomplete);
		return getConfigurator();
	}

	@Override
	public C withInputListener(ComponentEventListener<InputEvent> listener) {
		inputNotifierConfigurator.withInputListener(listener);
		return getConfigurator();
	}

	@Override
	public C withKeyDownListener(ComponentEventListener<KeyDownEvent> listener) {
		keyNotifierConfigurator.withKeyDownListener(listener);
		return getConfigurator();
	}

	@Override
	public C withKeyPressListener(ComponentEventListener<KeyPressEvent> listener) {
		keyNotifierConfigurator.withKeyPressListener(listener);
		return getConfigurator();
	}

	@Override
	public C withKeyUpListener(ComponentEventListener<KeyUpEvent> listener) {
		keyNotifierConfigurator.withKeyUpListener(listener);
		return getConfigurator();
	}

	@Override
	public C withKeyDownListener(Key key, ComponentEventListener<KeyDownEvent> listener, KeyModifier... modifiers) {
		keyNotifierConfigurator.withKeyDownListener(key, listener, modifiers);
		return getConfigurator();
	}

	@Override
	public C withKeyPressListener(Key key, ComponentEventListener<KeyPressEvent> listener, KeyModifier... modifiers) {
		keyNotifierConfigurator.withKeyPressListener(key, listener, modifiers);
		return getConfigurator();
	}

	@Override
	public C withKeyUpListener(Key key, ComponentEventListener<KeyUpEvent> listener, KeyModifier... modifiers) {
		keyNotifierConfigurator.withKeyUpListener(key, listener, modifiers);
		return getConfigurator();
	}

	@Override
	public C valueChangeMode(ValueChangeMode valueChangeMode) {
		valueChangeModeConfigurator.valueChangeMode(valueChangeMode);
		return getConfigurator();
	}

	@Override
	public C autofocus(boolean autofocus) {
		getComponent().setAutofocus(autofocus);
		return getConfigurator();
	}

	@Override
	public C tabIndex(int tabIndex) {
		getComponent().setTabIndex(tabIndex);
		return getConfigurator();
	}

	@Override
	public C withFocusListener(ComponentEventListener<FocusEvent<Component>> listener) {
		getComponent().addFocusListener(event -> listener.onComponentEvent(new FocusEvent<>(event.getSource(), event.isFromClient())));
		return getConfigurator();
	}

	@Override
	public C withBlurListener(ComponentEventListener<BlurEvent<Component>> listener) {
		getComponent().addBlurListener(event -> listener.onComponentEvent(new BlurEvent<>(event.getSource(), event.isFromClient())));
		return getConfigurator();
	}

	@Override
	public ShortcutConfigurator<C> withFocusShortcut(Key key) {
		return new DefaultShortcutConfigurator<>(getComponent().addFocusShortcut(key), getConfigurator());
	}

	@Override
	public C prefixComponent(Component component) {
		prefixAndSuffixConfigurator.prefixComponent(component);
		return getConfigurator();
	}

	@Override
	public C suffixComponent(Component component) {
		prefixAndSuffixConfigurator.suffixComponent(component);
		return getConfigurator();
	}

	@Override
	public C withCompositionStartListener(ComponentEventListener<CompositionStartEvent> listener) {
		compositionNotifierConfigurator.withCompositionStartListener(listener);
		return getConfigurator();
	}

	@Override
	public C withCompositionUpdateListener(ComponentEventListener<CompositionUpdateEvent> listener) {
		compositionNotifierConfigurator.withCompositionUpdateListener(listener);
		return getConfigurator();
	}

	@Override
	public C withCompositionEndListener(ComponentEventListener<CompositionEndEvent> listener) {
		compositionNotifierConfigurator.withCompositionEndListener(listener);
		return getConfigurator();
	}

	@Override
	public C placeholder(Localizable placeholder) {
		placeholderConfigurator.placeholder(placeholder);
		return getConfigurator();
	}

	@Override
	public C label(Localizable label) {
		labelConfigurator.label(label);
		return getConfigurator();
	}

	@Override
	public C title(Localizable title) {
		titleConfigurator.title(title);
		return getConfigurator();
	}

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

	@Override
	public C helperText(Localizable helperText) {
		helperTextConfigurator.helperText(helperText);
		return getConfigurator();
	}

	@Override
	public C helperText(String helperText) {
		helperTextConfigurator.helperText(helperText);
		return getConfigurator();
	}

	@Override
	public C helperComponent(Component component) {
		helperTextConfigurator.helperComponent(component);
		return getConfigurator();
	}

	@Override
	public C withThemeVariants(TextFieldVariant... variants) {
		getComponent().addThemeVariants(variants);
		return getConfigurator();
	}

	@Override
	public C required() {
		return required(true);
	}

	@Override
	public C autoselect(boolean autoselect) {
		getComponent().setAutoselect(autoselect);
		return getConfigurator();
	}

	@Override
	public C clearButtonVisible(boolean clearButtonVisible) {
		getComponent().setClearButtonVisible(clearButtonVisible);
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

	@Override
	public C ariaLabel(String defaultAriaLabel, String messageCode, Object... arguments) {
		return NumberInputConfigurator.super.ariaLabel(defaultAriaLabel, messageCode, arguments);
	}
}

