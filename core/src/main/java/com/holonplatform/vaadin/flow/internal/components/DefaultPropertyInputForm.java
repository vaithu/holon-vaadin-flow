/*
 * Copyright 2000-2017 Holon TDCN.
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

import com.holonplatform.core.Validator;
import com.holonplatform.core.Validator.ValidationException;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertyRenderer;
import com.holonplatform.core.property.PropertyRendererRegistry;
import com.holonplatform.vaadin.flow.components.*;
import com.holonplatform.vaadin.flow.components.builders.PropertyInputFormBuilder;
import com.holonplatform.vaadin.flow.components.events.GroupValueChangeEvent;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.shared.HasTooltip;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Default {@link PropertyInputForm} implementation.
 * 
 * @param <C> Content component type.
 *
 * @since 5.2.0
 */
public class DefaultPropertyInputForm<C extends Component>
		extends AbstractComposablePropertyForm<C, Input<?>, PropertyInputGroup> implements PropertyInputForm {

	private static final long serialVersionUID = -4202049108110710744L;

	private boolean enterMovesFocusToNext;

	private boolean validateOnEnterFocusMove;

	private boolean autoRequiredIndicators;

	private Set<String> explicitRequiredPropertyNames = Set.of();

	private static final String[] AUTO_REQUIRED_ANNOTATIONS = {
			"jakarta.validation.constraints.NotNull",
			"jakarta.validation.constraints.NotBlank",
			"jakarta.validation.constraints.NotEmpty" };

	private transient List<BoundComponentGroup.Binding<Property<?>, Input<?>>> inputBindings = List.of();

	/**
	 * Constructor.
	 * @param content Form content (not null)
	 */
	public DefaultPropertyInputForm(C content) {
		super(content);
	}

	protected void setEnterMovesFocusToNext(boolean enterMovesFocusToNext) {
		this.enterMovesFocusToNext = enterMovesFocusToNext;
	}

	protected boolean isEnterMovesFocusToNext() {
		return enterMovesFocusToNext;
	}

	protected void setValidateOnEnterFocusMove(boolean validateOnEnterFocusMove) {
		this.validateOnEnterFocusMove = validateOnEnterFocusMove;
	}

	protected boolean isValidateOnEnterFocusMove() {
		return validateOnEnterFocusMove;
	}

	private void setupEnterFocusNavigation() {
		this.inputBindings = getBindings().toList();
		if (!enterMovesFocusToNext) {
			return;
		}
		inputBindings.forEach(binding -> configureEnterFocusNavigation(binding.getElement()));
	}

	private void configureEnterFocusNavigation(Input<?> input) {
		if (input == null || input.getComponent() instanceof TextArea) {
			return;
		}
		input.getComponent().getElement().addEventListener("keydown", event -> focusNextInput(input))
				.setFilter("event.key === 'Enter' && !event.shiftKey && !event.isComposing");
	}

	private void focusNextInput(Input<?> sourceInput) {
		if (sourceInput == null || inputBindings.isEmpty()) {
			return;
		}
		if (isValidateOnEnterFocusMove()) {
			try {
				getComponentGroup().getValue(true);
			} catch (ValidationException e) {
				return;
			}
		}
		int index = -1;
		for (int i = 0; i < inputBindings.size(); i++) {
			if (inputBindings.get(i).getElement() == sourceInput) {
				index = i;
				break;
			}
		}
		if (index < 0) {
			return;
		}
		boolean moved = false;
		for (int i = index + 1; i < inputBindings.size(); i++) {
			final Input<?> candidate = inputBindings.get(i).getElement();
			if (isFocusable(candidate)) {
				candidate.focus();
				moved = true;
				break;
			}
		}
		if (!moved) {
			focusNextDocumentElement(sourceInput);
		}
	}

	private void focusNextDocumentElement(Input<?> sourceInput) {
		// Align Enter with browser Tab behavior when the form has no next focusable input,
		// including focusable custom-element hosts such as Vaadin components.
		sourceInput.getComponent().getElement().executeJs("""
				const current = this;
				const isFocusable = (el) => {
				  if (!(el instanceof HTMLElement || el instanceof SVGElement)) {
				    return false;
				  }
				  if (el === document.body || el === document.documentElement) {
				    return false;
				  }
				  if (el.hasAttribute('disabled') || el.getAttribute('aria-disabled') === 'true') {
				    return false;
				  }
				  if (el.getAttribute('tabindex') === '-1' || el.tabIndex < 0) {
				    return false;
				  }
				  if (el.hidden || el.getAttribute('aria-hidden') === 'true' || ('inert' in el && el.inert)) {
				    return false;
				  }
				  const style = window.getComputedStyle(el);
				  if (style.display === 'none' || style.visibility === 'hidden') {
				    return false;
				  }
				  return el.offsetParent !== null || style.position === 'fixed' || el === current;
				};
				const focusables = Array.from(document.querySelectorAll('*')).filter(isFocusable);
				const currentIndex = focusables.indexOf(current);
				if (currentIndex >= 0 && currentIndex + 1 < focusables.length) {
				  focusables[currentIndex + 1].focus();
				}
				""");
	}

	private static boolean isFocusable(Input<?> input) {
		if (input == null || input.isReadOnly()) {
			return false;
		}
		final Component component = input.getComponent();
		if (!component.isVisible()) {
			return false;
		}
		if (component instanceof HasEnabled hasEnabled && !hasEnabled.isEnabled()) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.PropertyInputBinder#refresh()
	 */
	@Override
	public void refresh() {
		getComponentGroup().refresh();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.PropertyInputBinder#refresh(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> boolean refresh(Property<T> property) {
		return getComponentGroup().refresh(property);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup#getFormValue(boolean)
	 */
	@Override
	public PropertyBox getValue(boolean validate) {
		return getComponentGroup().getValue(validate);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup#getValueIfValid()
	 */
	@Override
	public Optional<PropertyBox> getValueIfValid() {
		return getComponentGroup().getValueIfValid();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.flow.components.PropertyInputGroup#setFormValue(com.holonplatform.core.property.PropertyBox,
	 * boolean)
	 */
	@Override
	public void setValue(PropertyBox propertyBox, boolean validate) {
		getComponentGroup().setValue(propertyBox, validate);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		getComponentGroup().setReadOnly(readOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		getComponentGroup().setEnabled(enabled);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.flow.components.Validatable#validate()
	 */
	@Override
	public void validate() throws ValidationException {
		getComponentGroup().validate();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.PropertyViewSource#getViewComponent(com.holonplatform.core.property.Property)
	 */
	@Override
	public <T> Optional<Input<T>> getInput(Property<T> property) {
		return getComponentGroup().getInput(property);
	}

	@Override
	public void setAutoRequiredIndicators(boolean autoRequiredIndicators) {
		this.autoRequiredIndicators = autoRequiredIndicators;
		refreshRequiredIndicators();
	}

	@Override
	public boolean isAutoRequiredIndicators() {
		return autoRequiredIndicators;
	}

	private void setExplicitRequiredPropertyNames(Set<String> explicitRequiredPropertyNames) {
		this.explicitRequiredPropertyNames = (explicitRequiredPropertyNames != null) ? explicitRequiredPropertyNames
				: Set.of();
	}

	private void refreshRequiredIndicators() {
		getBindings().forEach(binding -> {
			Property<?> property = binding.getProperty();
			Input<?> input = binding.getElement();
			boolean required = explicitRequiredPropertyNames.contains(property.getName())
					|| (autoRequiredIndicators && isAutoRequiredProperty(property));
			input.setRequired(required);
		});
	}

	private boolean isAutoRequiredProperty(Property<?> property) {
		for (String annotationClassName : AUTO_REQUIRED_ANNOTATIONS) {
			if (hasAnnotation(property, annotationClassName)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasAnnotation(Property<?> property, String annotationClassName) {
		try {
			Class<?> annotationType = Class.forName(annotationClassName);
			return Boolean.TRUE.equals(property.getClass().getMethod("hasAnnotation", Class.class)
					.invoke(property, annotationType));
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			return false;
		}
	}

	// Builder

	/**
	 * Default {@link PropertyInputFormBuilder}.
	 * @param <C> Content type
	 */
	public static class DefaultBuilder<C extends Component> extends
			AbstractComponentConfigurator<C, PropertyInputFormBuilder<C>> implements PropertyInputFormBuilder<C> {

		private final DefaultPropertyInputForm<C> instance;

		private final DefaultPropertyInputGroup.InternalBuilder inputGroupBuilder;

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public <P extends Property<?>> DefaultBuilder(C content, Iterable<P> properties) {
			super(content);
			this.instance = new DefaultPropertyInputForm<>(content);
			this.inputGroupBuilder = new DefaultPropertyInputGroup.InternalBuilder(properties);
			// setup default composer
			if (content instanceof HasComponents) {
				instance.setComposer((Composer) Composable.componentContainerComposer());
			}
		}

		@Override
		protected Optional<HasSize> hasSize() {
			return Optional.empty();
		}

		@Override
		protected Optional<HasStyle> hasStyle() {
			return Optional.empty();
		}

		@Override
		protected Optional<HasTooltip> hasTooltip() {
			return Optional.empty();
		}

		@Override
		protected Optional<HasEnabled> hasEnabled() {
			return Optional.empty();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator#getConfigurator()
		 */
		@Override
		protected PropertyInputFormBuilder<C> getConfigurator() {
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#bind(com.holonplatform.core.property.
		 * Property, com.holonplatform.core.property.PropertyRenderer)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> bind(Property<T> property, PropertyRenderer<Input<T>, T> renderer) {
			inputGroupBuilder.bind(property, renderer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#withPostProcessor(java.util.function.
		 * BiConsumer)
		 */
		@Override
		public PropertyInputFormBuilder<C> withPostProcessor(BiConsumer<Property<?>, Input<?>> postProcessor) {
			inputGroupBuilder.withPostProcessor(postProcessor);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyInputGroupConfigurator#readOnly(com.holonplatform.
		 * core.property.Property, boolean)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> readOnly(Property<T> property, boolean readOnly) {
			inputGroupBuilder.readOnly(property, readOnly);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#required(com.holonplatform.core.property.
		 * Property)
		 */
		@Override
		public PropertyInputFormBuilder<C> required(Property<?> property) {
			inputGroupBuilder.required(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#required(com.holonplatform.core.property.
		 * Property, com.holonplatform.core.i18n.Localizable)
		 */
		@Override
		public PropertyInputFormBuilder<C> required(Property<?> property, Localizable message) {
			inputGroupBuilder.required(property, message);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#defaultValue(com.holonplatform.core.
		 * property.Property, com.holonplatform.vaadin.flow.components.PropertyInputGroup.DefaultValueProvider)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> defaultValue(Property<T> property, Supplier<T> defaultValueProvider) {
			inputGroupBuilder.defaultValue(property, defaultValueProvider);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#withValidator(com.holonplatform.core.
		 * property.Property, com.holonplatform.core.Validator)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> withValidator(Property<T> property, Validator<? super T> validator) {
			inputGroupBuilder.withValidator(property, validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#withValidator(com.holonplatform.core.
		 * Validator)
		 */
		@Override
		public PropertyInputFormBuilder<C> withValidator(Validator<PropertyBox> validator) {
			inputGroupBuilder.withValidator(validator);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.InputGroupConfigurator#groupValidationStatusHandler(com.
		 * holonplatform.vaadin.flow.components.GroupValidationStatusHandler)
		 */
		@Override
		public PropertyInputFormBuilder<C> groupValidationStatusHandler(
				GroupValidationStatusHandler<PropertyInputGroup, Property<?>, Input<?>> groupValidationStatusHandler) {
			inputGroupBuilder.groupValidationStatusHandler(groupValidationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyInputGroupConfigurator#validationStatusHandler(com.
		 * holonplatform.core.property.Property, com.holonplatform.vaadin.flow.components.ValidationStatusHandler)
		 */
		@Override
		public PropertyInputFormBuilder<C> validationStatusHandler(Property<?> property,
				ValidationStatusHandler<Input<?>> validationStatusHandler) {
			inputGroupBuilder.validationStatusHandler(property, validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyInputGroupConfigurator#validationStatusHandler(com.
		 * holonplatform.vaadin.flow.components.ValidationStatusHandler)
		 */
		@Override
		public PropertyInputFormBuilder<C> validationStatusHandler(
				ValidationStatusHandler<PropertyInputGroup> validationStatusHandler) {
			inputGroupBuilder.validationStatusHandler(validationStatusHandler);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#validateOnValueChange(boolean)
		 */
		@Override
		public PropertyInputFormBuilder<C> validateOnValueChange(boolean validateOnValueChange) {
			inputGroupBuilder.validateOnValueChange(validateOnValueChange);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#stopValidationAtFirstFailure(boolean)
		 */
		@Override
		public PropertyInputFormBuilder<C> stopInputsValidationAtFirstFailure(boolean stopValidationAtFirstFailure) {
			inputGroupBuilder.stopInputsValidationAtFirstFailure(stopValidationAtFirstFailure);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#stopOverallValidationAtFirstFailure(
		 * boolean)
		 */
		@Override
		public PropertyInputFormBuilder<C> stopGroupValidationAtFirstFailure(
				boolean stopOverallValidationAtFirstFailure) {
			inputGroupBuilder.stopGroupValidationAtFirstFailure(stopOverallValidationAtFirstFailure);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyInputGroupConfigurator#withValueChangeListener(com.
		 * holonplatform.core.property.Property,
		 * com.holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public <T> PropertyInputFormBuilder<C> withValueChangeListener(Property<T> property,
				ValueChangeListener<T, GroupValueChangeEvent<T, Property<?>, Input<?>, PropertyInputGroup>> listener) {
			inputGroupBuilder.withValueChangeListener(property, listener);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.builders.PropertyGroupConfigurator#withValueChangeListener(com.
		 * holonplatform.vaadin.flow.components.ValueHolder.ValueChangeListener)
		 */
		@Override
		public PropertyInputFormBuilder<C> withValueChangeListener(
				ValueChangeListener<PropertyBox, GroupValueChangeEvent<PropertyBox, Property<?>, Input<?>, PropertyInputGroup>> listener) {
			inputGroupBuilder.withValueChangeListener(listener);
			return this;
		}

		@Override
		public <T> PropertyInputFormBuilder<C> hidden(Property<T> property) {
			inputGroupBuilder.hidden(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.PropertyInputGroup.Builder#usePropertyRendererRegistry(com.
		 * holonplatform.core.property.PropertyRendererRegistry)
		 */
		@Override
		public PropertyInputFormBuilder<C> usePropertyRendererRegistry(
				PropertyRendererRegistry propertyRendererRegistry) {
			inputGroupBuilder.usePropertyRendererRegistry(propertyRendererRegistry);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.PropertyInputGroupConfigurator#enableRefreshOnValueChange(
		 * boolean)
		 */
		@Override
		public PropertyInputFormBuilder<C> enableRefreshOnValueChange(boolean enableRefreshOnValueChange) {
			inputGroupBuilder.enableRefreshOnValueChange(enableRefreshOnValueChange);
			return this;
		}

		@Override
		public PropertyInputFormBuilder<C> initializer(Consumer<C> initializer) {
			ObjectUtils.argumentNotNull(initializer, "Form content initializer must be not null");
			instance.setInitializer(initializer);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * com.holonplatform.vaadin.flow.components.builders.ComposableConfigurator#composer(com.holonplatform.vaadin.
		 * flow.components.Composable.Composer)
		 */
		@Override
		public PropertyInputFormBuilder<C> composer(Composer<? super C, Input<?>, PropertyInputGroup> composer) {
			ObjectUtils.argumentNotNull(composer, "Composer must be not null");
			instance.setComposer(composer);
			return this;
		}

		@Override
		public PropertyInputFormBuilder<C> composeOnAttach(boolean composeOnAttach) {
			instance.setComposeOnAttach(composeOnAttach);
			return this;
		}

		@Override
		public PropertyInputFormBuilder<C> enterMovesFocusToNext(boolean enterMovesFocusToNext) {
			instance.setEnterMovesFocusToNext(enterMovesFocusToNext);
			return this;
		}

		@Override
		public PropertyInputFormBuilder<C> validateOnEnterFocusMove(boolean validateOnEnterFocusMove) {
			instance.setValidateOnEnterFocusMove(validateOnEnterFocusMove);
			return this;
		}

		@Override
		public PropertyInputFormBuilder<C> propertyCaption(Property<?> property, Localizable caption) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			ObjectUtils.argumentNotNull(caption, "Caption must be not null");
			instance.setPropertyCaption(property, caption);
			return this;
		}

		@Override
		public PropertyInputFormBuilder<C> propertyCaption(Property<?> property, String caption) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.setPropertyCaption(property, Localizable.builder().message(caption).build());
			return this;
		}

		@Override
		public PropertyInputFormBuilder<C> propertyCaption(Property<?> property, String defaultCaption,
				String messageCode, Object... arguments) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.setPropertyCaption(property, Localizable.builder().message(defaultCaption).messageCode(messageCode)
					.messageArguments(arguments).build());
			return this;
		}

		@Override
		public PropertyInputFormBuilder<C> hidePropertyCaption(Property<?> property) {
			ObjectUtils.argumentNotNull(property, "Property must be not null");
			instance.hidePropertyCaption(property);
			return this;
		}

		/*
		 * (non-Javadoc)
		 * @see com.holonplatform.vaadin.flow.components.PropertyViewGroup.Builder#build()
		 */
		@Override
		public PropertyInputForm build() {
			instance.setComponentGroup(inputGroupBuilder.withPostProcessor(instance::configurePropertyComponent).build());
			instance.setExplicitRequiredPropertyNames(instance.getBindings()
					.filter(binding -> binding.getElement().isRequired())
					.map(binding -> binding.getProperty().getName())
					.collect(Collectors.toCollection(LinkedHashSet::new)));
			instance.refreshRequiredIndicators();
			instance.setupEnterFocusNavigation();
			return instance;
		}

	}

}
