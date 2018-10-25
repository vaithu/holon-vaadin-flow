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
package com.holonplatform.vaadin.flow.components;

import java.time.LocalDate;
import java.util.Date;
import java.util.function.Function;

import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.flow.components.Composable.Composer;
import com.holonplatform.vaadin.flow.components.PropertyViewForm.PropertyViewFormBuilder;
import com.holonplatform.vaadin.flow.components.PropertyViewGroup.PropertyViewGroupBuilder;
import com.holonplatform.vaadin.flow.components.builders.BooleanInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator.BaseButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.DateInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.FormLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.FormLayoutConfigurator;
import com.holonplatform.vaadin.flow.components.builders.FormLayoutConfigurator.BaseFormLayoutConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HorizontalLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelConfigurator;
import com.holonplatform.vaadin.flow.components.builders.LabelConfigurator.BaseLabelConfigurator;
import com.holonplatform.vaadin.flow.components.builders.LocalDateInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.NativeButtonBuilder;
import com.holonplatform.vaadin.flow.components.builders.NumberInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.PasswordInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.StringAreaInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.StringInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ThemableFlexComponentConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ThemableFlexComponentConfigurator.HorizontalLayoutConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ThemableFlexComponentConfigurator.VerticalLayoutConfigurator;
import com.holonplatform.vaadin.flow.components.builders.VerticalLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.ViewComponentBuilder;
import com.holonplatform.vaadin.flow.internal.components.DefaultPropertyViewGroup;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Main provider of UI components builders and configurators.
 * <p>
 * Provides static methods to obtain builder for common UI components type, allowing fluent and implementation-agnostic
 * components creation and configuration.
 * </p>
 * 
 * @since 5.2.0
 */
public interface Components {

	// Configurators
	// TODO APICHG: removed GridLayout, CssLayout and AbsoluteLayout configurators

	/**
	 * Get a {@link LabelConfigurator} to configure given <em>label</em> type component.
	 * <p>
	 * The component must be a {@link HtmlContainer} and a {@link ClickNotifier}, such as {@link Span} or {@link Div}.
	 * </p>
	 * @param <L> Label element type
	 * @param label The component to configure (not null)
	 * @return A {@link LabelConfigurator}
	 */
	@SuppressWarnings("rawtypes")
	static <L extends HtmlContainer & ClickNotifier> BaseLabelConfigurator<L> configure(L label) {
		return LabelConfigurator.configure(label);
	}

	/**
	 * Get a {@link ButtonConfigurator} to configure given {@link Button} instance.
	 * @param button Button to configure (not null)
	 * @return A {@link ButtonConfigurator}
	 */
	static BaseButtonConfigurator configure(Button button) {
		return ButtonConfigurator.configure(button);
	}

	/**
	 * Get a {@link VerticalLayoutConfigurator} to configure given {@link VerticalLayout}.
	 * @param layout Layout to configure
	 * @return A new {@link VerticalLayoutConfigurator}
	 */
	static VerticalLayoutConfigurator configure(VerticalLayout layout) {
		return ThemableFlexComponentConfigurator.configure(layout);
	}

	/**
	 * Get a {@link HorizontalLayoutConfigurator} to configure given {@link HorizontalLayout}.
	 * @param layout Layout to configure
	 * @return A new {@link HorizontalLayoutConfigurator}
	 */
	static HorizontalLayoutConfigurator configure(HorizontalLayout layout) {
		return ThemableFlexComponentConfigurator.configure(layout);
	}

	/**
	 * Get a {@link BaseFormLayoutConfigurator} to configure given {@link FormLayout}.
	 * @param layout Layout to configure
	 * @return A new {@link BaseFormLayoutConfigurator}
	 */
	static BaseFormLayoutConfigurator configure(FormLayout layout) {
		return FormLayoutConfigurator.configure(layout);
	}

	// Builders
	// TODO APICHG: removed GridLayout, CssLayout and AbsoluteLayout configurators

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link Div} tag.
	 * <p>
	 * This is an alias for the {@link #div()} method.
	 * </p>
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<Div> label() {
		return div();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link Span} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<Span> span() {
		return LabelBuilder.span();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link Div} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<Div> div() {
		return LabelBuilder.div();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link Paragraph} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<Paragraph> paragraph() {
		return LabelBuilder.paragraph();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H1} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H1> h1() {
		return LabelBuilder.h1();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H2} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H2> h2() {
		return LabelBuilder.h2();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H3} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H3> h3() {
		return LabelBuilder.h3();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H4} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H4> h4() {
		return LabelBuilder.h4();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H5} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H5> h5() {
		return LabelBuilder.h5();
	}

	/**
	 * Obtain a {@link LabelBuilder} to create a label component using a {@link H6} tag.
	 * @return The {@link LabelBuilder} to configure and obtain the component instance
	 */
	static LabelBuilder<H6> h6() {
		return LabelBuilder.h6();
	}

	/**
	 * Gets a builder to create {@link Button}s.
	 * @return A new {@link ButtonBuilder}
	 */
	static ButtonBuilder button() {
		return ButtonBuilder.create();
	}

	/**
	 * Gets a builder to create {@link NativeButton}s.
	 * @return A new {@link NativeButtonBuilder}
	 */
	// TODO APICHG: removed button(boolean nativeMode)
	static NativeButtonBuilder nativeButton() {
		return NativeButtonBuilder.create();
	}

	/**
	 * Gets a builder to create {@link VerticalLayout}s.
	 * @return A new {@link VerticalLayoutBuilder}
	 */
	static VerticalLayoutBuilder vl() {
		return VerticalLayoutBuilder.create();
	}

	/**
	 * Gets a builder to create {@link HorizontalLayout}s.
	 * @return A new {@link HorizontalLayoutBuilder}
	 */
	static HorizontalLayoutBuilder hl() {
		return HorizontalLayoutBuilder.create();
	}

	/**
	 * Gets a builder to create {@link FormLayout}s.
	 * @return A new {@link FormLayoutBuilder}
	 */
	static FormLayoutBuilder formLayout() {
		return FormLayoutBuilder.create();
	}

	// View components

	/**
	 * {@link ViewComponent} and {@link PropertyViewGroup} builders provider.
	 */
	// TODO APICHG: removed formGrid - GridLayout non available in flow
	static interface view {

		/**
		 * Get a {@link ViewComponentBuilder} to create a {@link ViewComponent} using given value type.
		 * @param <T> Value type
		 * @param valueType Value type (not null)
		 * @return A {@link ViewComponentBuilder}
		 */
		static <T> ViewComponentBuilder<T> component(Class<? extends T> valueType) {
			return ViewComponent.builder(valueType);
		}

		/**
		 * Get a {@link ViewComponentBuilder} to create a {@link ViewComponent} using given {@link Property} for label
		 * and value presentation through the {@link Property#present(Object)} method.
		 * @param <T> Value type
		 * @param property The property to use (not null)
		 * @return A {@link ViewComponentBuilder}
		 */
		static <T> ViewComponentBuilder<T> component(Property<T> property) {
			return ViewComponent.builder(property);
		}

		/**
		 * Get a {@link ViewComponentBuilder} to create a {@link ViewComponent} using given function to convert the
		 * value to a {@link String} type representation.
		 * @param <T> Value type
		 * @param stringValueConverter Value converter function (not null)
		 * @return A {@link ViewComponentBuilder}
		 */
		static <T> ViewComponentBuilder<T> component(Function<T, String> stringValueConverter) {
			return ViewComponent.builder(stringValueConverter);
		}

		/**
		 * Create a {@link ViewComponent} using given value type.
		 * @param <T> Value type
		 * @param valueType Value type (not null)
		 * @return A {@link ViewComponent} instance
		 */
		static <T> ViewComponent<T> type(Class<T> valueType) {
			return ViewComponent.builder(valueType).build();
		}

		/**
		 * Create a {@link ViewComponent} using given {@link Property} for label and value presentation through the
		 * {@link Property#present(Object)} method.
		 * @param <T> Value type
		 * @param property The property to use (not null)
		 * @return A {@link ViewComponent} instance
		 */
		static <T> ViewComponent<T> property(Property<T> property) {
			return ViewComponent.create(property);
		}

		/**
		 * Get a builder to create and setup a {@link PropertyViewGroup}, which provides functionalities to build and
		 * manage a group of {@link ViewComponent}s bound to a {@link Property} set.
		 * @return {@link PropertyViewGroup} builder
		 */
		static PropertyViewGroupBuilder propertyGroup() {
			return new DefaultPropertyViewGroup.DefaultBuilder();
		}

		/**
		 * Gets a builder to create a {@link PropertyViewForm}.
		 * @param <C> Content type
		 * @param content Form content, where view components will be composed by the form {@link Composer} (not null)
		 * @return {@link PropertyViewForm} builder
		 */
		static <C extends Component> PropertyViewFormBuilder<C> form(C content) {
			return PropertyViewForm.builder(content);
		}

		/**
		 * Gets a builder to create a {@link PropertyViewForm} using a {@link FormLayout} as layout component and a
		 * default {@link PropertyViewForm#componentContainerComposer()} to compose the view components on layout.
		 * @return {@link PropertyViewForm} builder
		 */
		static PropertyViewFormBuilder<FormLayout> form() {
			return PropertyViewForm.formLayout();
		}

		/**
		 * Gets a builder to create a {@link PropertyViewForm} using a {@link VerticalLayout} as layout component and a
		 * default {@link PropertyViewForm#componentContainerComposer()} to compose the view components on layout.
		 * @return {@link PropertyViewForm} builder
		 */
		static PropertyViewFormBuilder<VerticalLayout> formVertical() {
			return PropertyViewForm.verticalLayout();
		}

		/**
		 * Gets a builder to create a {@link PropertyViewForm} using a {@link HorizontalLayout} as layout component and
		 * a default {@link PropertyViewForm#componentContainerComposer()} to compose the view components on layout.
		 * @return {@link PropertyViewForm} builder
		 */
		static PropertyViewFormBuilder<HorizontalLayout> formHorizontal() {
			return PropertyViewForm.horizontalLayout();
		}

	}

	//
	// /**
	// * Get a {@link InputConfigurator} to configure given field.
	// * @param <T> Field type
	// * @param field Field to configure (not null)
	// * @return BaseFieldConfigurator
	// */
	// static <T> BaseFieldConfigurator<T> configure(AbstractField<T> field) {
	// return new DefaultFieldConfigurator<>(field);
	// }
	//

	//
	// // Builders
	//
	// /**
	// * Build a filler component, i.e. a {@link Label} with undefined size and the HTML entity <code>&nbsp;</code> as
	// * content, which can be used with full expand ratio as a space filler in layouts.
	// * @return Filler
	// */
	// static Component filler() {
	// return new Filler();
	// }
	//

	//
	// /**
	// * Gets a builder to create {@link Panel}s.
	// * @return Panel builder
	// */
	// static PanelBuilder panel() {
	// return new DefaultPanelBuilder();
	// }
	//
	// /**
	// * Gets a builder to create a {@link TabSheet}.
	// * @return TabSheet builder
	// */
	// static TabsBuilder<TabSheet> tabSheet() {
	// return new TabSheetBuilder();
	// }
	//
	// /**
	// * Gets a builder to create an {@link Accordion}.
	// * @return Accordion builder
	// */
	// static TabsBuilder<Accordion> accordion() {
	// return new AccordionBuilder();
	// }
	//
	// /**
	// * Gets a builder to create and open a {@link Dialog} window. The dialog will present by default a single
	// * <em>ok</em> button.
	// * @return DialogBuilder
	// */
	// static DialogBuilder dialog() {
	// return Dialog.builder();
	// }
	//
	// /**
	// * Gets a builder to create and open a question {@link Dialog} window. The dialog will present by default a
	// * <em>yes</em> and a <em>no</em> button. Use
	// * {@link QuestionDialogBuilder#callback(com.holonplatform.vaadin.components.Dialog.QuestionCallback)} to handle
	// the
	// * user selected answer.
	// * @return QuestionDialogBuilder
	// */
	// static QuestionDialogBuilder questionDialog() {
	// return Dialog.question();
	// }
	//
	// Inputs

	/**
	 * {@link Input}, {@link PropertyInputGroup} and {@link PropertyInputForm} builders provider.
	 */
	// TODO APICHG: removed string(boolean area) for stringArea()
	static interface input {

		/**
		 * Gets a builder to create {@link String} type {@link Input}s.
		 * @return A {@link StringInputBuilder}
		 */
		static StringInputBuilder string() {
			return Input.string();
		}

		/**
		 * Gets a builder to create {@link String} type {@link Input}s rendered as a <em>text area</em>.
		 * @return A {@link StringAreaInputBuilder}
		 */
		static StringAreaInputBuilder stringArea() {
			return Input.stringArea();
		}

		/**
		 * Gets a builder to create {@link String} type {@link Input}s which not display user input on screen, used to
		 * enter secret text information like passwords.
		 * <p>
		 * Alias for {@link #password()}.
		 * </p>
		 * @return A {@link PasswordInputBuilder}
		 */
		static PasswordInputBuilder secretString() {
			return password();
		}

		/**
		 * Gets a builder to create {@link String} type {@link Input}s which not display user input on screen, used to
		 * enter secret text information like passwords.
		 * @return A {@link PasswordInputBuilder}
		 */
		static PasswordInputBuilder password() {
			return Input.password();
		}

		/**
		 * Gets a builder to create {@link LocalDate} type {@link Input}s.
		 * @return A {@link LocalDateInputBuilder}
		 */
		static LocalDateInputBuilder localDate() {
			return Input.localDate();
		}

		/**
		 * Gets a builder to create {@link Date} type {@link Input}s.
		 * @return A {@link LocalDateInputBuilder}
		 */
		static DateInputBuilder date() {
			return Input.date();
		}

		/**
		 * Gets a builder to create {@link Boolean} type {@link Input}s.
		 * @return A {@link BooleanInputBuilder}
		 */
		static BooleanInputBuilder boolean_() {
			return Input.boolean_();
		}

		/**
		 * Gets a builder to create a numeric type {@link Input}.
		 * @param <T> Number type
		 * @param numberType Number class (not null)
		 * @return A new {@link NumberInputBuilder}
		 */
		static <T extends Number> NumberInputBuilder<T> number(Class<T> numberClass) {
			return Input.number(numberClass);
		}

		//
		// /**
		// * Gets a builder to create {@link Date} type {@link Input}s.
		// * @param resolution field Resolution
		// * @param inline <code>true</code> to render the input component using an inline calendar
		// * @return Input builder
		// */
		// static DateInputBuilder date(Resolution resolution, boolean inline) {
		// ObjectUtils.argumentNotNull(resolution, "Resolution must be not null");
		// return resolution.isTime() ? new DateTimeField.Builder(resolution, inline)
		// : new DateField.Builder(resolution, inline);
		// }
		//
		// /**
		// * Gets a builder to create {@link Date} type {@link Input}s.
		// * @param resolution field Resolution
		// * @return Input builder
		// */
		// static DateInputBuilder date(Resolution resolution) {
		// return date(resolution, false);
		// }
		//
		// /**
		// * Gets a builder to create {@link Date} type {@link Input}s.
		// * @param inline <code>true</code> to render the input component using an inline calendar
		// * @return Input builder
		// */
		// static DateInputBuilder date(boolean inline) {
		// return date(Resolution.DAY, inline);
		// }
		//
		// /**
		// * Gets a builder to create {@link LocalDate} type {@link Input}s.
		// * @param inline <code>true</code> to render the input component using an inline calendar
		// * @return Input builder
		// */
		// static TemporalWithoutTimeFieldBuilder<LocalDate> localDate(boolean inline) {
		// return new LocalDateField.Builder(inline);
		// }
		//
		// /**
		// * Gets a builder to create {@link LocalDateTime} type {@link Input}s.
		// * @param inline <code>true</code> to render the input component using an inline calendar
		// * @return Input builder
		// */
		// static TemporalWithTimeFieldBuilder<LocalDateTime> localDateTime(boolean inline) {
		// return new LocalDateTimeField.Builder(inline);
		// }
		//
		// /**
		// * Gets a builder to create {@link LocalDateTime} type {@link Input}s.
		// * @return Input builder
		// */
		// static TemporalWithTimeFieldBuilder<LocalDateTime> localDateTime() {
		// return localDateTime(false);
		// }
		//
		// /**
		// * Gets a builder to create a single selection {@link Input}.
		// * @param <T> Selection value type
		// * @param type Selection value type
		// * @param renderingMode Rendering mode
		// * @return Input builder
		// */
		// static <T> GenericSingleSelectInputBuilder<T> singleSelect(Class<? extends T> type,
		// RenderingMode renderingMode) {
		// return SingleSelect.builder(type, renderingMode);
		// }
		//
		// /**
		// * Gets a builder to create a single selection {@link Input} using {@link RenderingMode#SELECT}.
		// * @param <T> Selection value type
		// * @param type Selection value type
		// * @return Input builder
		// */
		// static <T> SelectModeSingleSelectInputBuilder<T> singleSelect(Class<? extends T> type) {
		// return SingleSelect.select(type);
		// }
		//
		// /**
		// * Gets a builder to create a single selection {@link Input} using {@link RenderingMode#NATIVE_SELECT}.
		// * @param <T> Selection value type
		// * @param type Selection value type
		// * @return Input builder
		// */
		// static <T> NativeModeSingleSelectInputBuilder<T> singleNativeSelect(Class<? extends T> type) {
		// return SingleSelect.nativeSelect(type);
		// }
		//
		// /**
		// * Gets a builder to create a single selection {@link Input} using {@link RenderingMode#OPTIONS}.
		// * @param <T> Selection value type
		// * @param type Selection value type
		// * @return Input builder
		// */
		// static <T> OptionsModeSingleSelectInputBuilder<T> singleOptionSelect(Class<? extends T> type) {
		// return SingleSelect.options(type);
		// }
		//
		// /**
		// * Gets a builder to create a {@link SingleSelect} with a {@link PropertyBox} items data source with
		// * {@link Property} as item properties.
		// * @param <T> Selection value type
		// * @param selectProperty Property to select (not null)
		// * @param renderingMode Rendering mode
		// * @return {@link SingleSelect} builder
		// */
		// static <T> GenericSinglePropertySelectInputBuilder<T> singleSelect(Property<T> selectProperty,
		// RenderingMode renderingMode) {
		// return SingleSelect.property(selectProperty, renderingMode);
		// }
		//
		// /**
		// * Gets a builder to create a {@link SingleSelect} with a {@link PropertyBox} items data source with
		// * {@link Property} as item properties using {@link RenderingMode#SELECT}.
		// * @param <T> Selection value type
		// * @param selectProperty Property to select (not null)
		// * @return {@link SingleSelect} builder
		// */
		// static <T> SelectModeSinglePropertySelectInputBuilder<T> singleSelect(Property<T> selectProperty) {
		// return SingleSelect.select(selectProperty);
		// }
		//
		// /**
		// * Gets a builder to create a {@link SingleSelect} with a {@link PropertyBox} items data source with
		// * {@link Property} as item properties using {@link RenderingMode#NATIVE_SELECT}.
		// * @param <T> Selection value type
		// * @param selectProperty Property to select (not null)
		// * @return {@link SingleSelect} builder
		// */
		// static <T> NativeModeSinglePropertySelectInputBuilder<T> singleNativeSelect(Property<T> selectProperty) {
		// return SingleSelect.nativeSelect(selectProperty);
		// }
		//
		// /**
		// * Gets a builder to create a {@link SingleSelect} with a {@link PropertyBox} items data source with
		// * {@link Property} as item properties using {@link RenderingMode#OPTIONS}.
		// * @param <T> Selection value type
		// * @param selectProperty Property to select (not null)
		// * @return {@link SingleSelect} builder
		// */
		// static <T> OptionsModeSinglePropertySelectInputBuilder<T> singleOptionSelect(Property<T> selectProperty) {
		// return SingleSelect.options(selectProperty);
		// }
		//
		// /**
		// * Gets a builder to create a multiple selection {@link Input}.
		// * @param <T> Selection value type
		// * @param type Selection value type
		// * @param renderingMode Rendering mode
		// * @return Input builder
		// */
		// static <T> GenericMultiSelectInputBuilder<T> multiSelect(Class<? extends T> type, RenderingMode
		// renderingMode) {
		// return MultiSelect.builder(type, renderingMode);
		// }
		//
		// /**
		// * Gets a builder to create a multiple selection {@link Input} using default {@link RenderingMode#OPTIONS}.
		// * @param <T> Selection value type
		// * @param type Selection value type
		// * @return Input builder
		// */
		// static <T> OptionsModeMultiSelectInputBuilder<T> multiSelect(Class<? extends T> type) {
		// return MultiSelect.options(type);
		// }
		//
		// /**
		// * Gets a builder to create a multiple selection {@link Input} using {@link RenderingMode#SELECT}.
		// * @param <T> Selection value type
		// * @param type Selection value type
		// * @return Input builder
		// */
		// static <T> SelectModeMultiSelectInputBuilder<T> multiSelectList(Class<? extends T> type) {
		// return MultiSelect.list(type);
		// }
		//
		// /**
		// * Gets a builder to create a {@link MultiSelect} with a {@link PropertyBox} items data source with
		// * {@link Property} as item properties.
		// * @param <T> Selection value type
		// * @param selectProperty Property to select (not null)
		// * @param renderingMode Rendering mode
		// * @return {@link MultiSelect} builder
		// */
		// static <T> GenericMultiPropertySelectInputBuilder<T> multiSelect(Property<T> selectProperty,
		// RenderingMode renderingMode) {
		// return MultiSelect.property(selectProperty, renderingMode);
		// }
		//
		// /**
		// * Gets a builder to create a {@link MultiSelect} with a {@link PropertyBox} items data source with
		// * {@link Property} as item properties using default {@link RenderingMode#OPTIONS}.
		// * @param <T> Selection value type
		// * @param selectProperty Property to select (not null)
		// * @return {@link MultiSelect} builder
		// */
		// static <T> OptionsModeMultiPropertySelectInputBuilder<T> multiSelect(Property<T> selectProperty) {
		// return MultiSelect.options(selectProperty);
		// }
		//
		// /**
		// * Gets a builder to create a {@link MultiSelect} with a {@link PropertyBox} items data source with
		// * {@link Property} as item properties using default {@link RenderingMode#SELECT}.
		// * @param <T> Selection value type
		// * @param selectProperty Property to select (not null)
		// * @return {@link MultiSelect} builder
		// */
		// static <T> SelectModeMultiPropertySelectInputBuilder<T> multiSelectList(Property<T> selectProperty) {
		// return MultiSelect.list(selectProperty);
		// }
		//
		// /**
		// * Gets a builder to create a single selection {@link Input} with given {@link Enum} class as data source,
		// using
		// * all enum constants as selection items.
		// * @param <E> Enum value type
		// * @param type Enum value type
		// * @param renderingMode Rendering mode
		// * @return Input builder
		// */
		// static <E extends Enum<E>> GenericSingleSelectInputBuilder<E> enumSingle(Class<E> type,
		// RenderingMode renderingMode) {
		// return singleSelect(type, renderingMode).items(type.getEnumConstants());
		// }
		//
		// /**
		// * Gets a builder to create a single selection {@link Input} with given {@link Enum} class as data source,
		// using
		// * all enum constants as selection items and default {@link RenderingMode#SELECT} rendering mode.
		// * @param <E> Enum value type
		// * @param type Enum value type
		// * @return Input builder
		// */
		// static <E extends Enum<E>> SelectModeSingleSelectInputBuilder<E> enumSingle(Class<E> type) {
		// return singleSelect(type).items(type.getEnumConstants());
		// }
		//
		// /**
		// * Gets a builder to create a multiple selection {@link Input} with given {@link Enum} class as data source,
		// * using all enum constants as selection items.
		// * @param <E> Enum value type
		// * @param type Enum value type
		// * @param renderingMode Rendering mode
		// * @return Input builder
		// */
		// static <E extends Enum<E>> GenericMultiSelectInputBuilder<E> enumMulti(Class<E> type,
		// RenderingMode renderingMode) {
		// return multiSelect(type, renderingMode).items(type.getEnumConstants());
		// }
		//
		// /**
		// * Gets a builder to create a multiple selection {@link Input} with given {@link Enum} class as data source,
		// * using all enum constants as selection items and default {@link RenderingMode#OPTIONS} rendering mode.
		// * @param <E> Enum value type
		// * @param type Enum value type
		// * @return Input builder
		// */
		// static <E extends Enum<E>> OptionsModeMultiSelectInputBuilder<E> enumMulti(Class<E> type) {
		// return multiSelect(type).items(type.getEnumConstants());
		// }
		//
		// /**
		// * Gets a builder to create a {@link PropertyInputGroup}.
		// * @return {@link PropertyInputGroup} builder
		// */
		// static PropertyInputGroupBuilder propertyGroup() {
		// return PropertyInputGroup.builder();
		// }
		//
		// /**
		// * Gets a builder to create a {@link PropertyInputForm}.
		// * @param <C> Content type
		// * @param content Form content, where fields will be composed by the form {@link Composer} (not null)
		// * @return {@link PropertyInputForm} builder
		// */
		// static <C extends Component> PropertyInputFormBuilder<C> form(C content) {
		// return PropertyInputForm.builder(content);
		// }
		//
		// /**
		// * Gets a builder to create a {@link PropertyInputForm} using a {@link FormLayout} as layout component and a
		// * default {@link PropertyInputForm#componentContainerComposer()} to compose the fields on layout.
		// * @return {@link PropertyInputForm} builder
		// */
		// static PropertyInputFormBuilder<FormLayout> form() {
		// return PropertyInputForm.builder(formLayout().fullWidth().spacing().build())
		// .composer(ComposableComponent.componentContainerComposer());
		// }
		//
		// /**
		// * Gets a builder to create a {@link PropertyInputForm} using a {@link VerticalLayout} as layout component and
		// a
		// * default {@link PropertyInputForm#componentContainerComposer()} to compose the fields on layout.
		// * @return {@link PropertyInputForm} builder
		// */
		// static PropertyInputFormBuilder<VerticalLayout> formVertical() {
		// return PropertyInputForm.builder(vl().fullWidth().build())
		// .composer(ComposableComponent.componentContainerComposer());
		// }
		//
		// /**
		// * Gets a builder to create a {@link PropertyInputForm} using a {@link HorizontalLayout} as layout component
		// and
		// * a default {@link PropertyInputForm#componentContainerComposer()} to compose the fields on layout.
		// * @return {@link PropertyInputForm} builder
		// */
		// static PropertyInputFormBuilder<HorizontalLayout> formHorizontal() {
		// return PropertyInputForm.builder(hl().build()).composer(ComposableComponent.componentContainerComposer());
		// }
		//
		// /**
		// * Gets a builder to create a {@link PropertyInputForm} using a {@link VerticalLayout} as layout component and
		// a
		// * default {@link PropertyInputForm#componentContainerComposer()} to compose the fields on layout.
		// * @return {@link PropertyInputForm} builder
		// */
		// static PropertyInputFormBuilder<GridLayout> formGrid() {
		// return PropertyInputForm.builder(gridLayout().fullWidth().build())
		// .composer(ComposableComponent.componentContainerComposer());
		// }

	}

	//
	// // Item listings
	//
	// /**
	// * {@link ItemListing} builders provider.
	// */
	// static interface listing {
	//
	// /**
	// * Builder to create an {@link ItemListing} instance using a {@link Grid} as backing component.
	// * @param <T> Item data type
	// * @param itemType Item bean type
	// * @return Grid {@link ItemListing} builder
	// */
	// static <T> BeanListingBuilder<T> items(Class<T> itemType) {
	// return BeanListing.builder(itemType);
	// }
	//
	// /**
	// * Builder to create an {@link PropertyListing} instance using a {@link Grid} as backing component.
	// * @param <P> Actual property type
	// * @param properties The property set to use for the listing
	// * @return Grid {@link PropertyListing} builder
	// */
	// @SafeVarargs
	// static <P extends Property<?>> GridPropertyListingBuilder properties(P... properties) {
	// return properties(PropertySet.of(properties));
	// }
	//
	// /**
	// * Builder to create an {@link PropertyListing} instance using a {@link Grid} as backing component.
	// * @param <P> Actual property type
	// * @param properties The property set to use for the listing
	// * @return Grid {@link PropertyListing} builder
	// */
	// static <P extends Property<?>> GridPropertyListingBuilder properties(Iterable<P> properties) {
	// return PropertyListing.builder(properties);
	// }
	//
	// /**
	// * Builder to create an {@link PropertyListing} instance using a {@link Grid} as backing component.
	// * @param <P> Actual property type
	// * @param properties The property set to use for the listing (not null)
	// * @param additionalProperties Additional properties to declare
	// * @return Grid {@link PropertyListing} builder
	// */
	// @SafeVarargs
	// @SuppressWarnings("rawtypes")
	// static <P extends Property> GridPropertyListingBuilder properties(PropertySet<P> properties,
	// P... additionalProperties) {
	// ObjectUtils.argumentNotNull(properties, "Properties must be not null");
	// if (additionalProperties != null && additionalProperties.length > 0) {
	// PropertySet.Builder<Property<?>> builder = PropertySet.builder().add(properties);
	// for (P property : additionalProperties) {
	// builder.add(property);
	// }
	// return PropertyListing.builder(builder.build());
	// }
	// return PropertyListing.builder(properties);
	// }
	//
	// }

}