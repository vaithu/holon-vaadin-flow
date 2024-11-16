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

import com.holonplatform.core.Context;
import com.holonplatform.core.config.ConfigProperty;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.components.Composable.Composer;
import com.holonplatform.vaadin.flow.components.builders.*;
import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator.BaseButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.DialogBuilder.ConfirmDialogBuilder;
import com.holonplatform.vaadin.flow.components.builders.DialogBuilder.MessageDialogBuilder;
import com.holonplatform.vaadin.flow.components.builders.DialogBuilder.QuestionDialogBuilder;
import com.holonplatform.vaadin.flow.components.builders.DialogBuilder.QuestionDialogCallback;
import com.holonplatform.vaadin.flow.components.builders.FilterableSingleSelectConfigurator.FilterableSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.FilterableSingleSelectConfigurator.PropertyFilterableSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.FormLayoutConfigurator.BaseFormLayoutConfigurator;
import com.holonplatform.vaadin.flow.components.builders.LabelConfigurator.BaseLabelConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ListMultiSelectConfigurator.ListMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ListMultiSelectConfigurator.PropertyListMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ListSingleSelectConfigurator.ListSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ListSingleSelectConfigurator.PropertyListSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.OptionsMultiSelectConfigurator.OptionsMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.OptionsMultiSelectConfigurator.PropertyOptionsMultiSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.OptionsSingleSelectConfigurator.OptionsSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.OptionsSingleSelectConfigurator.PropertyOptionsSingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.SingleSelectConfigurator.PropertySingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.SingleSelectConfigurator.SingleSelectInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.ThemableFlexComponentConfigurator.HorizontalLayoutConfigurator;
import com.holonplatform.vaadin.flow.components.builders.ThemableFlexComponentConfigurator.VerticalLayoutConfigurator;
import com.holonplatform.vaadin.flow.components.events.ClickEvent;
import com.holonplatform.vaadin.flow.components.events.ClickEventListener;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.data.ItemConverter;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.components.DefaultFormFooter;
import com.holonplatform.vaadin.flow.internal.components.DefaultTimeline;
import com.holonplatform.vaadin.flow.internal.components.DefaultXPanel;
import com.holonplatform.vaadin.flow.internal.components.builders.*;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.Scroller.ScrollDirection;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

/**
 * Main provider of UI components builders and configurators.
 * <p>
 * Provides static methods to obtain builder for common UI components type,
 * allowing fluent and implementation-agnostic components creation and
 * configuration.
 * </p>
 *
 * @since 5.2.0
 */
public interface Components {

    // Property configuration

    /**
     * Configuration property which can be used for a {@link LocalTime} type
     * {@link Property} to configure the <em>steps</em> to show for the
     * {@link Input} component bound to the property, i.e. the intervals for the
     * displayed items in the time input dropdown.
     * <p>
     * If the step is less than 60 seconds, the format will be changed to
     * <code>hh:mm:ss</code> and it can be in <code>hh:mm:ss.fff</code> format, when
     * the step is less than 1 second.
     * </p>
     * <p>
     * If the step is less than 900 seconds, the dropdown is hidden.
     * </p>
     */
    public static final ConfigProperty<Duration> TIME_INPUT_STEP = ConfigProperty
            .create(Components.class.getName() + ".time-input-step", Duration.class);

    // Configurators

    /**
     * Get a {@link LabelConfigurator} to configure given <em>label</em> type
     * component.
     * <p>
     * The component must be a {@link HtmlContainer} and a {@link ClickNotifier},
     * such as {@link Span} or {@link Div}.
     * </p>
     *
     * @param <L>   Label element type
     * @param label The component to configure (not null)
     * @return A {@link LabelConfigurator}
     */
    @SuppressWarnings("rawtypes")
    static <L extends HtmlContainer & ClickNotifier> BaseLabelConfigurator<L> configure(L label) {
        return LabelConfigurator.configure(label);
    }

    /**
     * Get a {@link ButtonConfigurator} to configure given {@link Button} instance.
     *
     * @param button Button to configure (not null)
     * @return A {@link ButtonConfigurator}
     */
    static BaseButtonConfigurator configure(Button button) {
        return ButtonConfigurator.configure(button);
    }

    /**
     * Get a {@link VerticalLayoutConfigurator} to configure given
     * {@link VerticalLayout}.
     *
     * @param layout Layout to configure
     * @return A new {@link VerticalLayoutConfigurator}
     */
    static VerticalLayoutConfigurator configure(VerticalLayout layout) {
        return ThemableFlexComponentConfigurator.configure(layout);
    }

    /**
     * Get a {@link HorizontalLayoutConfigurator} to configure given
     * {@link HorizontalLayout}.
     *
     * @param layout Layout to configure
     * @return A new {@link HorizontalLayoutConfigurator}
     */
    static HorizontalLayoutConfigurator configure(HorizontalLayout layout) {
        return ThemableFlexComponentConfigurator.configure(layout);
    }

    /**
     * Get a {@link BaseFormLayoutConfigurator} to configure given
     * {@link FormLayout}.
     *
     * @param layout Layout to configure
     * @return A new {@link BaseFormLayoutConfigurator}
     */
    static BaseFormLayoutConfigurator configure(FormLayout layout) {
        return FormLayoutConfigurator.configure(layout);
    }

    static FlexLayoutConfigurator.BaseFlexLayoutConfigurator configure(FlexLayout layout) {
        return FlexLayoutConfigurator.configure(layout);
    }

    static DivConfigurator.BaseDivConfigurator configure(Div div) {
        return DivConfigurator.configure(div);
    }

    static CardGridConfigurator.BaseCardGridConfigurator cardGrid(Div div) {
        return CardGridConfigurator.configure(div);
    }

    static CardGridConfigurator.BaseCardGridConfigurator cardGrid() {
        return CardGridConfigurator.configure(new Div());
    }

    static NotificationConfigurator.BaseNotificationConfigurator configure(Notification notification) {
        return NotificationConfigurator.configure(notification);
    }

    static NotificationBuilder notification() {
        return NotificationBuilder.create();
    }

	/*static CardGridConfigurator.CardBuilder card(Div div) {
		return CardGridBuilder.create().withCard(div);
	}

	static CardGridConfigurator.CardBuilder card() {
		return CardGridBuilder.create().withCard(new Div());
	}*/

    static TabsBuilder tabs(Div div) {
        return TabsBuilder.create().container(div);
    }

    static TabsBuilder tabs(VerticalLayout verticalLayout) {
        return TabsBuilder.create().container(verticalLayout);
    }

    static TabsConfigurator.BaseTabsConfigurator configure(Tabs tabs) {
        return TabsConfigurator.configure(tabs);
    }

    static TabBuilder tab() {
        return TabBuilder.create();
    }

    static TabBuilder tab(String title) {
        return TabBuilder.create().label(title);
    }

    static TabConfigurator.BaseTabConfigurator configure(Tab tab) {
        return TabConfigurator.configure(tab);
    }

    static HasInputEditorConfigurator.InputEditorBuilder suffixInputEditor() {
        return HasInputEditorConfigurator.create();
    }

    static AccordionBuilder accordion() {
        return AccordionBuilder.create();
    }

    static SideNavBuilder sideNav() {
        return SideNavBuilder.create();
    }

    static SideNavBuilder configure(SideNav sideNav) {
        return SideNavBuilder.configure(sideNav);
    }

    static DefaultAccordionHeaderBuilder accordionHeader() {
        return new DefaultAccordionHeaderBuilder();
    }

    static AccordionConfigurator.BaseAccordionConfigurator configure(Accordion accordion) {
        return AccordionConfigurator.configure(accordion);
    }

    static TabSheetConfigurator.BaseTabSheetConfigurator configure(TabSheet tabSheet) {
        return TabSheetConfigurator.configure(tabSheet);
    }

    static TabSheetBuilder tabSheet() {
        return TabSheetBuilder.create();
    }

    static DefaultMobileGridTemplateBuilder mobileGrid() {
        return new DefaultMobileGridTemplateBuilder();
    }

    static TitleBuilder titlePanel() {
        return TitleBuilder.create();
    }

    static TitleConfigurator.BaseTitleConfigurator titlePanel(HorizontalLayout layout) {
        return TitleConfigurator.configure(layout);
    }

    static SplitLayoutConfigurator.BaseSplitLayoutConfigurator configure(SplitLayout splitLayout) {
        return SplitLayoutConfigurator.configure(splitLayout);
    }

    static SplitLayoutBuilder splitLayout() {
        return SplitLayoutBuilder.create();
    }

    static FlexBoxLayoutConfigurator.BaseFlexBoxLayoutConfigurator configure(FlexBoxLayout layout) {
        return FlexBoxLayoutConfigurator.configure(layout);
    }

    static FlexBoxLayoutBuilder flexBoxLayout() {
        return FlexBoxLayoutBuilder.create();
    }


    // Builders

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a {@link Div}
     * tag.
     * <p>
     * This is an alias for the {@link #divLabel()} ()} method.
     * </p>
     *
     * @return The {@link LabelBuilder} to configure and obtain the component
     * instance
     */
    static LabelBuilder<Div> label() {
        return divLabel();
    }

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a
     * {@link Span} tag.
     *
     * @return The {@link LabelBuilder} to configure and obtain the component
     * instance
     */
    static LabelBuilder<Span> span() {
        return LabelBuilder.span();
    }


    /**
     * Obtain a {@link LabelBuilder} to create a label component using a {@link Div}
     * tag.
     *
     * @return The {@link LabelBuilder} to configure and obtain the component
     * instance
     */
    static LabelBuilder<Div> divLabel() {
        return LabelBuilder.div();
    }

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a
     * {@link Paragraph} tag.
     *
     * @return The {@link LabelBuilder} to configure and obtain the component
     * instance
     */
    static LabelBuilder<Paragraph> paragraph() {
        return LabelBuilder.paragraph();
    }

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a {@link H1}
     * tag.
     *
     * @return The {@link LabelBuilder} to configure and obtain the component
     * instance
     */
    static LabelBuilder<H1> h1() {
        return LabelBuilder.h1();
    }

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a {@link H2}
     * tag.
     *
     * @return The {@link LabelBuilder} to configure and obtain the component
     * instance
     */
    static LabelBuilder<H2> h2() {
        return LabelBuilder.h2();
    }

    static LabelBuilder<Emphasis> emphasis() {
        return LabelBuilder.emphasis();
    }

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a {@link H3}
     * tag.
     *
     * @return The {@link LabelBuilder} to configure and obtain the component
     * instance
     */
    static LabelBuilder<H3> h3() {
        return LabelBuilder.h3();
    }

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a {@link H4}
     * tag.
     *
     * @return The {@link LabelBuilder} to configure and obtain the component
     * instance
     */
    static LabelBuilder<H4> h4() {
        return LabelBuilder.h4();
    }

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a {@link H5}
     * tag.
     *
     * @return The {@link LabelBuilder} to configure and obtain the component
     * instance
     */
    static LabelBuilder<H5> h5() {
        return LabelBuilder.h5();
    }

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a {@link H6}
     * tag.
     *
     * @return The {@link LabelBuilder} to configure and obtain the component
     * instance
     */
    static LabelBuilder<H6> h6() {
        return LabelBuilder.h6();
    }

    /**
     * Gets a builder to create {@link Button}s.
     *
     * @return A new {@link ButtonBuilder}
     */
    static ButtonBuilder button() {
        return ButtonBuilder.create();
    }


    interface utils {
        static FormResponsiveStepBuilder responsiveSteps() {
            return FormResponsiveStepBuilder.create();
        }

        static DividerBuilder divider() {
            return DividerBuilder.create();
        }

        static DividerBuilder divider(Span span) {
            return DividerBuilder.create(span);
        }

    }

    static ZohoBuilder zoho() {
        return ZohoBuilder.create();
    }

    static RowBuilder row() {
        return RowBuilder.create();
    }

    static ColumnBuilder column() {
        return ColumnBuilder.create();
    }

    static DefaultXPanel panel() {
        return new DefaultXPanel();
    }


    static LabelBuilder<H4> title() {
        LabelBuilder<H4> h4LabelBuilder = LabelBuilder.h4();
        h4LabelBuilder.styleNames(UIUtils.getTitleStyles());
        return h4LabelBuilder;
    }


    static ButtonBuilder deleteButton() {
        return ButtonBuilder.createDelBtn();
    }

    static Button deleteButton(String text, ClickEventListener<Button, ClickEvent<Button>> clickListener) {
        return ButtonBuilder.createDelBtn().text(text).onClick(clickListener).build();
    }

    /**
     * Create a {@link Button} with given text and given <code>click</code> event
     * listener.
     *
     * @param text          The button text
     * @param clickListener The click listener (not null)
     * @return A new {@link Button}
     * @see #button()
     */
    static Button button(String text, ClickEventListener<Button, ClickEvent<Button>> clickListener) {
        return ButtonBuilder.create().text(text).onClick(clickListener).build();
    }

    static <T> Grid<T> grid(Class<T> tClass) {
        return new Grid<T>(tClass);
    }

    static <T> Grid<T> grid(Class<T> tClass, boolean autoCreateColumns) {
        return new Grid<T>(tClass, autoCreateColumns);
    }

    /**
     * Create a {@link Button} with given localizable text and given
     * <code>click</code> event listener.
     *
     * @param defaultText   The default button text
     * @param messageCode   The button text message localization code
     * @param clickListener The click listener (not null)
     * @return A new {@link Button}
     * @see #button()
     */
    static Button button(String defaultText, String messageCode,
                         ClickEventListener<Button, ClickEvent<Button>> clickListener) {
        return ButtonBuilder.create().text(defaultText, messageCode).onClick(clickListener).build();
    }

    /**
     * Create a {@link Button} with given localizable text and given
     * <code>click</code> event listener.
     *
     * @param text          The {@link Localizable} button text
     * @param clickListener The click listener (not null)
     * @return A new {@link Button}
     * @see #button()
     */
    static Button button(Localizable text, ClickEventListener<Button, ClickEvent<Button>> clickListener) {
        return ButtonBuilder.create().text(text).onClick(clickListener).build();
    }

    /**
     * Gets a builder to create {@link NativeButton}s.
     *
     * @return A new {@link NativeButtonBuilder}
     */
    static NativeButtonBuilder nativeButton() {
        return NativeButtonBuilder.create();
    }

    /**
     * Gets a builder to create {@link VerticalLayout}s.
     *
     * @return A new {@link VerticalLayoutBuilder}
     */
    static VerticalLayoutBuilder vl() {
        return VerticalLayoutBuilder.create();
    }

    static VerticalLayoutBuilder verticalLayout() {
        return vl();
    }

    /**
     * Gets a builder to create {@link HorizontalLayout}s.
     *
     * @return A new {@link HorizontalLayoutBuilder}
     */
    static HorizontalLayoutBuilder hl() {
        return HorizontalLayoutBuilder.create();
    }

    static HorizontalLayoutBuilder horizontalLayout() {
        return hl();
    }

    static DefaultCloseButtonBuilder closeButton() {
        return new DefaultCloseButtonBuilder();
    }

    static DefaultOptionsButtonBuilder optionsButton() {
        return new DefaultOptionsButtonBuilder();
    }

    static BulkActionBuilder bulkActionBar() {
        return BulkActionBuilder.create();
    }

    static DivBuilder div() {
        return DivBuilder.create();
    }

    static FormHeaderBuilder formHeader() {
        return FormHeaderBuilder.create();
    }

    static DefaultFormFooter formFooter() {
        return new DefaultFormFooter();
    }


    /**
     * Gets a builder to create {@link HorizontalLayout} footer.
     *
     * @return A new {@link HorizontalLayoutBuilder}
     */
    static HorizontalLayoutBuilder footer(String... styles) {
        HorizontalLayoutBuilder builder = HorizontalLayoutBuilder.create();
        builder.styleNames(styles);
        builder.spacing();
        builder.fullWidth();

        return builder;
    }

    static FlexLayoutBuilder flexLayout() {
        return FlexLayoutBuilder.create();
    }

    /**
     * Gets a builder to create {@link FormLayout}s.
     *
     * @return A new {@link FormLayoutBuilder}
     */
    static FormLayoutBuilder formLayout() {
        return FormLayoutBuilder.create();
    }

    /**
     * Gets a builder to create {@link ContextMenu}s.
     *
     * @return A new {@link ContextMenuBuilder}
     */
    static ContextMenuBuilder contextMenu() {
        return ContextMenuBuilder.create();
    }

    /**
     * Gets a builder to create {@link com.vaadin.flow.component.menubar.MenuBar}s.
     *
     * @return A new {@link MenuBarBuilder}
     */
    /*static MenuBarBuilder menuBar() {
        return MenuBarBuilder.create();
    }*/

    /**
     * Gets a builder to create {@link com.vaadin.flow.component.menubar.MenuBar}s.
     *
     * @return A new {@link MenuItemBuilder}
     */
    static MenuItemBuilder menuBar(MenuBar menuBar) {
        return MenuItemBuilder.create(menuBar);
    }

    /**
     * Gets a builder to create {@link com.vaadin.flow.component.menubar.MenuBar}s.
     *
     * @return A new {@link MenuItemBuilder}
     */
    static MenuItemBuilder menuBar(MenuBar menuBar, MenuItem menuItem) {
        return MenuItemBuilder.create(menuBar, menuItem);
    }

    /**
     * Gets a builder to create {@link com.vaadin.flow.component.menubar.MenuBar}s.
     *
     * @return A new {@link MenuItemBuilder}
     */
    static MenuItemBuilder menuBar() {
        return MenuItemBuilder.create(new MenuBar());
    }

    /**
     * Gets a builder to create a {@link Scroller}.
     *
     * @return A new {@link ScrollerBuilder}
     * @since 5.5.0
     */
    static ScrollerBuilder scroller() {
        return ScrollerBuilder.create();
    }

    /**
     * Gets a builder to create a {@link Scroller}.
     *
     * @param content The content of the scroller
     * @return A new {@link ScrollerBuilder}
     * @since 5.5.0
     */
    static ScrollerBuilder scroller(Component content) {
        return ScrollerBuilder.create().content(content);
    }

    /**
     * Gets a builder to create a {@link Scroller}.
     *
     * @param content         The content of the scroller
     * @param scrollDirection The scroll direction (not null)
     * @return A new {@link ScrollerBuilder}
     * @since 5.5.0
     */
    static ScrollerBuilder scroller(Component content, ScrollDirection scrollDirection) {
        return ScrollerBuilder.create().content(content).scrollDirection(scrollDirection);
    }

    // Dialogs

    /**
     * Dialog builders provider.
     */
    static interface dialog {

        /**
         * Get a builder to create a generic message dialog.
         *
         * @return A new {@link MessageDialogBuilder}
         */
        static MessageDialogBuilder message() {
            return DialogBuilder.message();
        }

        /**
         * Show a message dialog with given localizable message text.
         *
         * @param message The dialog message text
         */
        static void showMessage(Localizable message) {
            message().text(message).open();
        }

        /**
         * Show a message dialog with given message text.
         *
         * @param message The dialog message text
         */
        static void showMessage(String message) {
            showMessage(Localizable.of(message));
        }

        /**
         * Show a message dialog with given localizable message text.
         *
         * @param defaultMessage Default dialog message if no translation is available
         *                       for given <code>messageCode</code> for current
         *                       {@link Locale}
         * @param messageCode    Dialog message translation message key
         * @param arguments      Optional dialog message translation arguments
         * @see LocalizationProvider
         */
        static void showMessage(String defaultMessage, String messageCode, Object... arguments) {
            showMessage(Localizable.builder().message(defaultMessage).messageCode(messageCode)
                    .messageArguments(arguments).build());
        }

        /**
         * Get a builder to create a message dialog with a <em>OK</em> button in the
         * dialog toolbar which can be used to close the dialog.
         * <p>
         * The default <em>OK</em> button message localization code is
         * {@link DialogBuilder#DEFAULT_OK_BUTTON_MESSAGE_CODE}.
         * </p>
         *
         * @return A new {@link ConfirmDialogBuilder}
         */
        static ConfirmDialogBuilder confirm() {
            return DialogBuilder.confirm();
        }

        static ConfirmDialogBuilder confirm(PropertyInputForm inputForm) {
            return DialogBuilder.confirm(inputForm);
        }

        static ConfirmDialogBuilder save(boolean okToCancelDialog) {
            return DialogBuilder.save(okToCancelDialog);
        }

        static void showSave(boolean okToCancelDialog, String text) {
            save(okToCancelDialog)
                    .okButtonConfigurator(baseButtonConfigurator -> {
                        baseButtonConfigurator.withThemeVariants(ButtonVariant.LUMO_PRIMARY);
                        baseButtonConfigurator.text("Save", "save.code");
                    }).text(text)
                    .open();
        }

        /**
         * Show a confirm dialog with given localizable message text.
         *
         * @param message The dialog message text
         */
        static void showConfirm(Localizable message) {
            confirm().text(message).open();
        }

        /**
         * Show a confirm dialog with given message text.
         *
         * @param message The dialog message text
         */
        static void showConfirm(String message) {
            showConfirm(Localizable.of(message));
        }

        /**
         * Show a confirm dialog with given localizable message text.
         *
         * @param defaultMessage Default dialog message if no translation is available
         *                       for given <code>messageCode</code> for current
         *                       {@link Locale}.
         * @param messageCode    Dialog message translation message key
         * @param arguments      Optional dialog message translation arguments
         * @see LocalizationProvider
         */
        static void showConfirm(String defaultMessage, String messageCode, Object... arguments) {
            showConfirm(Localizable.builder().message(defaultMessage).messageCode(messageCode)
                    .messageArguments(arguments).build());
        }

        /**
         * Get a builder to create a question dialog, with a <em>confirm</em> button and
         * a <em>deny</em> button in the dialog toolbar which will trigger the given
         * <code>questionDialogCallback</code> to react to the user choice.
         * <p>
         * The default <em>confirm</em> button message localization code is
         * {@link DialogBuilder#DEFAULT_CONFIRM_BUTTON_MESSAGE_CODE}. The default
         * <em>deny</em> button message localization code is
         * {@link DialogBuilder#DEFAULT_DENY_BUTTON_MESSAGE_CODE}.
         * </p>
         *
         * @param questionDialogCallback The callback function use to react to the user
         *                               selection (not null)
         * @return A new {@link QuestionDialogBuilder}
         */
        static QuestionDialogBuilder question(QuestionDialogCallback questionDialogCallback) {
            return DialogBuilder.question(questionDialogCallback);
        }

        /**
         * Get a builder to create a save dialog, with a <em>confirm</em> button and
         * a <em>deny</em> button in the dialog toolbar which will trigger the given
         * <code>questionDialogCallback</code> to react to the user choice.
         * <p>
         * The default <em>confirm</em> button message localization code is
         * {@link DialogBuilder#DEFAULT_CONFIRM_BUTTON_MESSAGE_CODE}. The default
         * <em>deny</em> button message localization code is
         * {@link DialogBuilder#DEFAULT_DENY_BUTTON_MESSAGE_CODE}.
         * </p>
         * @param okToCloseDialog This is to decide when to close the dialog window
         * @param questionDialogCallback The callback function use to react to the user
         *                               selection (not null)
         * @return A new {@link QuestionDialogBuilder}
         */
		/*static DialogBuilder.SaveDialogBuilder save(boolean okToCloseDialog,QuestionDialogCallback questionDialogCallback) {
			return DialogBuilder.save(questionDialogCallback);
		}*/

        /**
         * Get a builder to create a delete dialog, with a <em>confirm</em> button and
         * a <em>deny</em> button in the dialog toolbar which will trigger the given
         * <code>deleteDialogCallback</code> to react to the user choice.
         * <p>
         * The default <em>confirm</em> button message localization code is
         * {@link DialogBuilder#DEFAULT_CONFIRM_BUTTON_MESSAGE_CODE}. The default
         * <em>deny</em> button message localization code is
         * {@link DialogBuilder#DEFAULT_DENY_BUTTON_MESSAGE_CODE}.
         * </p>
         *
         * @param deleteDialogCallback The callback function use to react to the user
         *                             selection (not null)
         * @return A new {@link DialogBuilder.DeleteDialogBuilder}
         */
        static DialogBuilder.DeleteDialogBuilder delete(DialogBuilder.DeleteDialogCallback deleteDialogCallback) {
            return DialogBuilder.delete(deleteDialogCallback);
        }

        static DialogBuilder.SaveAndNewDialogBuilder saveAndNew(QuestionDialogCallback questionDialogCallback) {
            return DialogBuilder.saveAndNew(questionDialogCallback);
        }

        static DialogBuilder.SaveDialogBuilder save(DialogBuilder.SaveDialogCallback saveDialogCallback) {
            return DialogBuilder.save(saveDialogCallback);
        }

        /**
         * Show a question dialog with given localizable message text.
         *
         * @param questionDialogCallback The callback function use to react to the user
         *                               selection (not null)
         * @param message                The dialog message text
         */
        static void showQuestion(QuestionDialogCallback questionDialogCallback, Localizable message) {
            question(questionDialogCallback).text(message).open();
        }

        /**
         * Show a question dialog with given message text.
         *
         * @param questionDialogCallback The callback function use to react to the user
         *                               selection (not null)
         * @param message                The dialog message text
         */
        static void showQuestion(QuestionDialogCallback questionDialogCallback, String message) {
            showQuestion(questionDialogCallback, Localizable.of(message));
        }

        /**
         * Show a question dialog with given localizable message text.
         *
         * @param questionDialogCallback The callback function use to react to the user
         *                               selection (not null)
         * @param defaultMessage         Default dialog message if no translation is
         *                               available for given <code>messageCode</code>
         *                               for current {@link Locale}
         * @param messageCode            Dialog message translation message key
         * @param arguments              Optional dialog message translation arguments
         * @see LocalizationProvider
         */
        static void showQuestion(QuestionDialogCallback questionDialogCallback, String defaultMessage,
                                 String messageCode, Object... arguments) {
            showQuestion(questionDialogCallback, Localizable.builder().message(defaultMessage).messageCode(messageCode)
                    .messageArguments(arguments).build());
        }

        /**
         * Show a delete dialog with given localizable message text.
         *
         * @param deleteDialogCallback The callback function use to react to the user
         *                             selection (not null)
         * @param message              The dialog message text
         */
        static void showDelete(DialogBuilder.DeleteDialogCallback deleteDialogCallback, Localizable message) {
            delete(deleteDialogCallback).text(message).open();
        }

        /**
         * Show a delete dialog with given message text.
         *
         * @param deleteDialogCallback The callback function use to react to the user
         *                             selection (not null)
         * @param message              The dialog message text
         */
        static void showDelete(DialogBuilder.DeleteDialogCallback deleteDialogCallback, String message) {
            showDelete(deleteDialogCallback, Localizable.of(message));
        }

        /**
         * Show a delete dialog with given localizable message text.
         *
         * @param deleteDialogCallback The callback function use to react to the user
         *                             selection (not null)
         * @param defaultMessage       Default dialog message if no translation is
         *                             available for given <code>messageCode</code>
         *                             for current {@link Locale}
         * @param messageCode          Dialog message translation message key
         * @param arguments            Optional dialog message translation arguments
         * @see LocalizationProvider
         */
        static void showDelete(DialogBuilder.DeleteDialogCallback deleteDialogCallback, String defaultMessage,
                               String messageCode, Object... arguments) {
            showDelete(deleteDialogCallback, Localizable.builder().message(defaultMessage).messageCode(messageCode)
                    .messageArguments(arguments).build());
        }

    }

    interface badge {
        /**
         * Obtain a {@link LabelBuilder} to create a badge component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badge() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge success component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgeSuccess() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge success"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge error component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgeError() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge error"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge primary component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgePrimary() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge primary"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge success primary component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgePrimarySuccess() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge success primary"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge contrast component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgePrimaryError() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge error primary"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge contrast component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgePrimaryContrast() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge contrast primary"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge small component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgeSmall() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge small"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge contrast component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgeSmallSuccess() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge success small"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge error small component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgeSmallError() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge error small"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge contrast small component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgeSmallContrast() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge contrast small"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge small primary component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgeSmallPrimary() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge small primary"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge success small primary component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgeSmallPrimarySuccess() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge success small primary"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge error small primary component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgeSmallPrimaryError() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge error small primary"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge contrast small primary component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgeSmallPrimaryContrast() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge contrast small primary"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge pill component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgePill() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge pill"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge success pill component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgePillSuccess() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge success pill"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge error pill component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgePillError() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge error pill"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge contrast pill component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgePillContrast() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge contrast pill"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge primary pill component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgePillPrimary() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge primary pill"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge success primary pill component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgePillPrimarySuccess() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge success primary pill"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge error primary pill component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgePillPrimaryError() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge error primary pill"));
            return span;
        }

        /**
         * Obtain a {@link LabelBuilder} to create a badge contrast primary pill component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to configure and obtain the component
         * instance
         */
        static LabelBuilder<Span> badgePillPrimaryContrast() {
            LabelBuilder<Span> span = LabelBuilder.span();
            span.elementConfiguration(element -> element.getThemeList().add("badge contrast primary pill"));
            return span;
        }
    }
    // View components

    /**
     * {@link ViewComponent} and {@link PropertyViewGroup} builders provider.
     */
    static interface view {

        /**
         * Get a {@link ViewComponentBuilder} to create a {@link ViewComponent} using
         * given value type.
         *
         * @param <T>       Value type
         * @param valueType Value type (not null)
         * @return A {@link ViewComponentBuilder}
         */
        static <T> ViewComponentBuilder<T> component(Class<? extends T> valueType) {
            return ViewComponent.builder(valueType);
        }

        /**
         * Get a {@link ViewComponentBuilder} to create a {@link ViewComponent} using
         * given {@link Property} for label and value presentation through the
         * {@link Property#present(Object)} method.
         *
         * @param <T>      Value type
         * @param property The property to use (not null)
         * @return A {@link ViewComponentBuilder}
         */
        static <T> ViewComponentBuilder<T> component(Property<T> property) {
            return ViewComponent.builder(property);
        }

        /**
         * Get a {@link ViewComponentBuilder} to create a {@link ViewComponent} using
         * given function to convert the value to a {@link String} type representation.
         *
         * @param <T>                  Value type
         * @param stringValueConverter Value converter function (not null)
         * @return A {@link ViewComponentBuilder}
         */
        static <T> ViewComponentBuilder<T> component(Function<T, String> stringValueConverter) {
            return ViewComponent.builder(stringValueConverter);
        }

        /**
         * Create a {@link ViewComponent} using given value type.
         *
         * @param <T>       Value type
         * @param valueType Value type (not null)
         * @return A {@link ViewComponent} instance
         */
        static <T> ViewComponent<T> type(Class<T> valueType) {
            return ViewComponent.builder(valueType).build();
        }

        /**
         * Create a {@link ViewComponent} using given {@link Property} for label and
         * value presentation through the {@link Property#present(Object)} method.
         *
         * @param <T>      Value type
         * @param property The property to use (not null)
         * @return A {@link ViewComponent} instance
         */
        static <T> ViewComponent<T> property(Property<T> property) {
            return ViewComponent.create(property);
        }

        /**
         * Get a {@link PropertyViewGroupBuilder} to create and setup a
         * {@link PropertyViewGroup}.
         *
         * @param <P>        Property type
         * @param properties The property set (not null)
         * @return A new {@link PropertyViewGroupBuilder}
         */
        @SuppressWarnings("rawtypes")
        static <P extends Property> PropertyViewGroupBuilder propertyGroup(Iterable<P> properties) {
            return PropertyViewGroup.builder(properties);
        }

        /**
         * Get a {@link PropertyViewGroupBuilder} to create and setup a
         * {@link PropertyViewGroup}.
         *
         * @param properties The property set (not null)
         * @return A new {@link PropertyViewGroupBuilder}
         */
        static PropertyViewGroupBuilder propertyGroup(Property<?>... properties) {
            return PropertyViewGroup.builder(properties);
        }

        /**
         * Get a builder to create a {@link PropertyViewForm} using given property set.
         *
         * @param <C>        Form content element type
         * @param <P>        Property type
         * @param content    The form content, where the {@link ViewComponent}s will be
         *                   composed using the configured {@link Composer} (not null)
         * @param properties The property set (not null)
         * @return A new {@link PropertyViewFormBuilder}
         */
        @SuppressWarnings("rawtypes")
        static <C extends Component, P extends Property> PropertyViewFormBuilder<C> form(C content,
                                                                                         Iterable<P> properties) {
            return PropertyViewForm.builder(content, properties);
        }

        /**
         * Get a builder to create a {@link PropertyViewForm} using given property set.
         *
         * @param <C>        Form content element type
         * @param content    The form content, where the {@link ViewComponent}s will be
         *                   composed using the configured {@link Composer} (not null)
         * @param properties The property set (not null)
         * @return A new {@link PropertyViewFormBuilder}
         */
        static <C extends Component> PropertyViewFormBuilder<C> form(C content, Property<?>... properties) {
            return PropertyViewForm.builder(content, properties);
        }

        /**
         * Get a builder to create a {@link PropertyViewForm} using given property set
         * and a {@link FormLayout} as content layout.
         * <p>
         * A default composer is configured using
         * {@link Composable#componentContainerComposer()}. Use
         * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)}
         * to provide a custom components composer.
         * </p>
         *
         * @param <P>        Property type
         * @param properties The property set (not null)
         * @return A {@link PropertyViewForm} builder
         */
        @SuppressWarnings("rawtypes")
        static <P extends Property> PropertyViewFormBuilder<FormLayout> form(Iterable<P> properties) {
            return PropertyViewForm.formLayout(properties);
        }

        /**
         * Get a builder to create a {@link PropertyViewForm} using given property set
         * and a {@link FormLayout} as content layout.
         * <p>
         * A default composer is configured using
         * {@link Composable#componentContainerComposer()}. Use
         * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)}
         * to provide a custom components composer.
         * </p>
         *
         * @param properties The property set (not null)
         * @return A {@link PropertyViewForm} builder
         */
        static PropertyViewFormBuilder<FormLayout> form(Property<?>... properties) {
            return PropertyViewForm.formLayout(properties);
        }

        /**
         * Get a builder to create a {@link PropertyViewForm} using given property set
         * and a {@link VerticalLayout} as content layout.
         * <p>
         * A default composer is configured using
         * {@link Composable#componentContainerComposer()}. Use
         * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)}
         * to provide a custom components composer.
         * </p>
         *
         * @param <P>        Property type
         * @param properties The property set (not null)
         * @return A {@link PropertyViewForm} builder
         */
        @SuppressWarnings("rawtypes")
        static <P extends Property> PropertyViewFormBuilder<VerticalLayout> formVertical(Iterable<P> properties) {
            return PropertyViewForm.verticalLayout(properties);
        }

        /**
         * Get a builder to create a {@link PropertyViewForm} using given property set
         * and a {@link VerticalLayout} as content layout.
         * <p>
         * A default composer is configured using
         * {@link Composable#componentContainerComposer()}. Use
         * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)}
         * to provide a custom components composer.
         * </p>
         *
         * @param properties The property set (not null)
         * @return A {@link PropertyViewForm} builder
         */
        static PropertyViewFormBuilder<VerticalLayout> formVertical(Property<?>... properties) {
            return PropertyViewForm.verticalLayout(properties);
        }

        /**
         * Get a builder to create a {@link PropertyViewForm} using given property set
         * and a {@link HorizontalLayout} as content layout.
         * <p>
         * A default composer is configured using
         * {@link Composable#componentContainerComposer()}. Use
         * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)}
         * to provide a custom components composer.
         * </p>
         *
         * @param <P>        Property type
         * @param properties The property set (not null)
         * @return A {@link PropertyViewForm} builder
         */
        @SuppressWarnings("rawtypes")
        static <P extends Property> PropertyViewFormBuilder<HorizontalLayout> formHorizontal(Iterable<P> properties) {
            return PropertyViewForm.horizontalLayout(properties);
        }

        /**
         * Get a builder to create a {@link PropertyViewForm} using given property set
         * and a {@link HorizontalLayout} as content layout.
         * <p>
         * A default composer is configured using
         * {@link Composable#componentContainerComposer()}. Use
         * {@link PropertyViewFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)}
         * to provide a custom components composer.
         * </p>
         *
         * @param properties The property set (not null)
         * @return A {@link PropertyViewForm} builder
         */
        static PropertyViewFormBuilder<HorizontalLayout> formHorizontal(Property<?>... properties) {
            return PropertyViewForm.horizontalLayout(properties);
        }

    }

    // Inputs

    /**
     * {@link Input}, {@link PropertyInputGroup} and {@link PropertyInputForm}
     * builders provider.
     */
    static interface input {

        /**
         * Gets a builder to create {@link String} type {@link Input}s.
         *
         * @return A {@link StringInputBuilder}
         */
        static StringInputBuilder string() {
            return Input.string();
        }

        /**
         * Gets a builder to create {@link String} type {@link Input}s rendered as a
         * <em>text area</em>.
         *
         * @return A {@link StringAreaInputBuilder}
         */
        static StringAreaInputBuilder stringArea() {
            return Input.stringArea();
        }

        static SearchBarBuilder searchBar() {
            return SearchBarBuilder.create();
        }

        static DefaultTimeline timeline() {
            return new DefaultTimeline();
        }

        /**
         * Gets a builder to create {@link String} type {@link Input}s which not display
         * user input on screen, used to enter secret text information like passwords.
         * <p>
         * Alias for {@link #password()}.
         * </p>
         *
         * @return A {@link PasswordInputBuilder}
         */
        static PasswordInputBuilder secretString() {
            return password();
        }

        /**
         * Gets a builder to create {@link String} type {@link Input}s which not display
         * user input on screen, used to enter secret text information like passwords.
         *
         * @return A {@link PasswordInputBuilder}
         */
        static PasswordInputBuilder password() {
            return Input.password();
        }

        /**
         * Gets a builder to create {@link LocalDate} type {@link Input}s.
         *
         * @return A {@link LocalDateInputBuilder}
         */
        static LocalDateInputBuilder localDate() {
            return Input.localDate();
        }

        /**
         * Gets a builder to create {@link LocalDateTime} type {@link Input}s.
         *
         * @return A {@link LocalDateTimeInputBuilder}
         */
        static LocalDateTimeInputBuilder localDateTime() {
            return Input.localDateTime();
        }

        /**
         * Gets a builder to create {@link LocalTime} type {@link Input}s.
         *
         * @return A {@link LocalTimeInputBuilder}
         */
        static LocalTimeInputBuilder localTime() {
            return LocalTimeInputBuilder.create();
        }

        /**
         * Gets a builder to create {@link Date} type {@link Input}s.
         * <p>
         * This Input use the {@link Date} type only for simple date representations
         * (day, month, year), i.e. without the time part.
         * </p>
         *
         * @return A {@link DateInputBuilder}
         */
        static DateInputBuilder date() {
            return Input.date();
        }

        /**
         * Gets a builder to create {@link Date} type {@link Input}s with time (hours
         * and minutes) support.
         * <p>
         * Only the hours and minutes time parts are supported.
         * </p>
         *
         * @return A {@link DateTimeInputBuilder}
         */
        static DateTimeInputBuilder dateTime() {
            return Input.dateTime();
        }

        /**
         * Gets a builder to create {@link Boolean} type {@link Input}s.
         *
         * @return A {@link BooleanInputBuilder}
         */
        static BooleanInputBuilder boolean_() {
            return Input.boolean_();
        }

        /**
         * Gets a builder to create a numeric type {@link Input}.
         *
         * @param <T>         Number type
         * @param numberClass Number class (not null)
         * @return A new {@link NumberInputBuilder}
         */
        static <T extends Number> NumberInputBuilder<T> number(Class<T> numberClass) {
            return Input.number(numberClass);
        }

        /**
         * Gets a builder to create a <em>filterable</em> {@link SingleSelect} type
         * {@link Input}, which uses a {@link ComboBox} as input component.
         * <p>
         * This builder can be used when the selection items type and the selection
         * value type are consistent. Use
         * {@link #singleSelect(Class, Class, ItemConverter)} if not.
         * <p>
         *
         * @param <T>  Value type
         * @param type Selection value type (not null)
         * @return A new {@link FilterableSingleSelectInputBuilder}
         */
        static <T> FilterableSingleSelectInputBuilder<T, T> singleSelect(Class<T> type) {
            return Input.singleSelect(type);
        }

        /**
         * Gets a builder to create a <em>filterable</em> {@link SingleSelect} type
         * {@link Input}, which uses a {@link ComboBox} as input component.
         * <p>
         * This builder can be used when the selection items type and the selection
         * value type are not consistent (i.e. of different type). When the the
         * selection item and the selection value types are consistent, the
         * {@link #singleSelect(Class)} method can be used.
         * <p>
         *
         * @param <T>           Value type
         * @param <ITEM>        Item type
         * @param type          Selection value type (not null)
         * @param itemType      Selection items type (not null)
         * @param itemConverter The item converter to use to convert a selection item
         *                      into a selection (Input) value and back (not null)
         * @return A new {@link FilterableSingleSelectInputBuilder}
         */
        static <T, ITEM> FilterableSingleSelectInputBuilder<T, ITEM> singleSelect(Class<T> type, Class<ITEM> itemType,
                                                                                  ItemConverter<T, ITEM> itemConverter) {
            return Input.singleSelect(type, itemType, itemConverter);
        }

        /**
         * Gets a builder to create a {@link Property} model based <em>filterable</em>
         * {@link SingleSelect} type {@link Input}, which uses a {@link ComboBox} as
         * input component.
         *
         * @param <T>               Value type
         * @param selectionProperty The property to use to represent the selection value
         *                          (not null)
         * @return A new {@link PropertyFilterableSingleSelectInputBuilder}
         */
        static <T> PropertyFilterableSingleSelectInputBuilder<T> singleSelect(final Property<T> selectionProperty) {
            return Input.singleSelect(selectionProperty);
        }

        /**
         * Gets a builder to create a {@link Property} model based <em>filterable</em>
         * {@link SingleSelect} type {@link Input}, which uses a {@link ComboBox} as
         * input component.
         *
         * @param <T>               Value type
         * @param selectionProperty The property to use to represent the selection value
         *                          (not null)
         * @param itemConverter     The function to use to convert a selection value
         *                          into the corresponding {@link PropertyBox} item
         * @return A new {@link PropertyFilterableSingleSelectInputBuilder}
         */
        static <T> PropertyFilterableSingleSelectInputBuilder<T> singleSelect(final Property<T> selectionProperty,
                                                                              Function<T, Optional<PropertyBox>> itemConverter) {
            return Input.singleSelect(selectionProperty, itemConverter);
        }

        /**
         * Gets a builder to create a <em>simple</em> {@link SingleSelect} type
         * {@link Input}, which uses a {@link Select} as input component.
         * <p>
         * This builder can be used when the selection items type and the selection
         * value type are consistent. Use
         * {@link #singleSelect(Class, Class, ItemConverter)} if not.
         * <p>
         *
         * @param <T>  Value type
         * @param type Selection value type (not null)
         * @return A new {@link SingleSelectInputBuilder}
         */
        static <T> SingleSelectInputBuilder<T, T> singleSimpleSelect(Class<T> type) {
            return Input.singleSimpleSelect(type);
        }

        /**
         * Gets a builder to create a <em>simple</em> {@link SingleSelect} type
         * {@link Input}, which uses a {@link Select} as input component.
         * <p>
         * This builder can be used when the selection items type and the selection
         * value type are not consistent (i.e. of different type). When the the
         * selection item and the selection value types are consistent, the
         * {@link #singleSelect(Class)} method can be used.
         * <p>
         *
         * @param <T>           Value type
         * @param <ITEM>        Item type
         * @param type          Selection value type (not null)
         * @param itemType      Selection items type (not null)
         * @param itemConverter The item converter to use to convert a selection item
         *                      into a selection (Input) value and back (not null)
         * @return A new {@link SingleSelectInputBuilder}
         */
        static <T, ITEM> SingleSelectInputBuilder<T, ITEM> singleSimpleSelect(Class<T> type, Class<ITEM> itemType,
                                                                              ItemConverter<T, ITEM> itemConverter) {
            return Input.singleSimpleSelect(type, itemType, itemConverter);
        }

        /**
         * Gets a builder to create a {@link Property} model based <em>simple</em>
         * {@link SingleSelect} type {@link Input}, which uses a {@link Select} as input
         * component.
         *
         * @param <T>               Value type
         * @param selectionProperty The property to use to represent the selection value
         *                          (not null)
         * @return A new {@link PropertySingleSelectInputBuilder}
         */
        static <T> PropertySingleSelectInputBuilder<T> singleSimpleSelect(final Property<T> selectionProperty) {
            return Input.singleSimpleSelect(selectionProperty);
        }

        /**
         * Gets a builder to create a {@link Property} model based <em>simple</em>
         * {@link SingleSelect} type {@link Input}, which uses a {@link Select} as input
         * component.
         *
         * @param <T>               Value type
         * @param selectionProperty The property to use to represent the selection value
         *                          (not null)
         * @param itemConverter     The function to use to convert a selection value
         *                          into the corresponding {@link PropertyBox} item
         * @return A new {@link PropertySingleSelectInputBuilder}
         */
        static <T> PropertySingleSelectInputBuilder<T> singleSimpleSelect(final Property<T> selectionProperty,
                                                                          Function<T, Optional<PropertyBox>> itemConverter) {
            return Input.singleSimpleSelect(selectionProperty, itemConverter);
        }

        /**
         * Gets a builder to create a <em>options</em> {@link SingleSelect} type
         * {@link Input}, which uses a {@link RadioButtonGroup} as input component.
         * <p>
         * This builder can be used when the selection items type and the selection
         * value type are consistent. Use
         * {@link #singleOptionSelect(Class, Class, ItemConverter)} if not.
         * <p>
         *
         * @param <T>  Value type
         * @param type Selection value type (not null)
         * @return A new {@link OptionsSingleSelectInputBuilder}
         */
        static <T> OptionsSingleSelectInputBuilder<T, T> singleOptionSelect(Class<T> type) {
            return Input.singleOptionSelect(type);
        }

        /**
         * Gets a builder to create a <em>options</em> {@link SingleSelect} type
         * {@link Input}, which uses a {@link RadioButtonGroup} as input component.
         * <p>
         * This builder can be used when the selection items type and the selection
         * value type are not consistent (i.e. of different type). When the the
         * selection item and the selection value types are consistent, the
         * {@link #singleOptionSelect(Class)} method can be used.
         * <p>
         *
         * @param <T>           Value type
         * @param <ITEM>        Item type
         * @param type          Selection value type (not null)
         * @param itemType      Selection items type (not null)
         * @param itemConverter The item converter to use to convert a selection item
         *                      into a selection (Input) value and back (not null)
         * @return A new {@link OptionsSingleSelectInputBuilder}
         */
        static <T, ITEM> OptionsSingleSelectInputBuilder<T, ITEM> singleOptionSelect(Class<T> type,
                                                                                     Class<ITEM> itemType, ItemConverter<T, ITEM> itemConverter) {
            return Input.singleOptionSelect(type, itemType, itemConverter);
        }

        /**
         * Gets a builder to create a {@link Property} model based <em>options</em>
         * {@link SingleSelect} type {@link Input}, which uses a
         * {@link RadioButtonGroup} as input component.
         *
         * @param <T>               Value type
         * @param selectionProperty The property to use to represent the selection value
         *                          (not null)
         * @return A new {@link PropertyOptionsSingleSelectInputBuilder}
         */
        static <T> PropertyOptionsSingleSelectInputBuilder<T> singleOptionSelect(final Property<T> selectionProperty) {
            return Input.singleOptionSelect(selectionProperty);
        }

        /**
         * Gets a builder to create a {@link Property} model based <em>options</em>
         * {@link SingleSelect} type {@link Input}, which uses a
         * {@link RadioButtonGroup} as input component.
         *
         * @param <T>               Value type
         * @param selectionProperty The property to use to represent the selection value
         *                          (not null)
         * @param itemConverter     The function to use to convert a selection value
         *                          into the corresponding {@link PropertyBox} item
         * @return A new {@link PropertyOptionsSingleSelectInputBuilder}
         */
        static <T> PropertyOptionsSingleSelectInputBuilder<T> singleOptionSelect(final Property<T> selectionProperty,
                                                                                 Function<T, Optional<PropertyBox>> itemConverter) {
            return Input.singleOptionSelect(selectionProperty, itemConverter);
        }

        /**
         * Gets a builder to create a <em>list</em> {@link SingleSelect} type
         * {@link Input}, which uses a {@link ListBox} as input component.
         * <p>
         * This builder can be used when the selection items type and the selection
         * value type are consistent. Use
         * {@link #singleListSelect(Class, Class, ItemConverter)} if not.
         * <p>
         *
         * @param <T>  Value type
         * @param type Selection value type (not null)
         * @return A new {@link ListSingleSelectInputBuilder}
         */
        static <T> ListSingleSelectInputBuilder<T, T> singleListSelect(Class<T> type) {
            return Input.singleListSelect(type);
        }

        /**
         * Gets a builder to create a <em>list</em> {@link SingleSelect} type
         * {@link Input}, which uses a {@link ListBox} as input component.
         * <p>
         * This builder can be used when the selection items type and the selection
         * value type are not consistent (i.e. of different type). When the the
         * selection item and the selection value types are consistent, the
         * {@link #singleListSelect(Class)} method can be used.
         * <p>
         *
         * @param <T>           Value type
         * @param <ITEM>        Item type
         * @param type          Selection value type (not null)
         * @param itemType      Selection items type (not null)
         * @param itemConverter The item converter to use to convert a selection item
         *                      into a selection (Input) value and back (not null)
         * @return A new {@link ListSingleSelectInputBuilder}
         */
        static <T, ITEM> ListSingleSelectInputBuilder<T, ITEM> singleListSelect(Class<T> type, Class<ITEM> itemType,
                                                                                ItemConverter<T, ITEM> itemConverter) {
            return Input.singleListSelect(type, itemType, itemConverter);
        }

        /**
         * Gets a builder to create a {@link Property} model based <em>list</em>
         * {@link SingleSelect} type {@link Input}, which uses a {@link ListBox} as
         * input component.
         *
         * @param <T>               Value type
         * @param selectionProperty The property to use to represent the selection value
         *                          (not null)
         * @return A new {@link PropertyListSingleSelectInputBuilder}
         */
        static <T> PropertyListSingleSelectInputBuilder<T> singleListSelect(final Property<T> selectionProperty) {
            return Input.singleListSelect(selectionProperty);
        }

        /**
         * Gets a builder to create a {@link Property} model based <em>list</em>
         * {@link SingleSelect} type {@link Input}, which uses a {@link ListBox} as
         * input component.
         *
         * @param <T>               Value type
         * @param selectionProperty The property to use to represent the selection value
         *                          (not null)
         * @param itemConverter     The function to use to convert a selection value
         *                          into the corresponding {@link PropertyBox} item
         * @return A new {@link PropertyListSingleSelectInputBuilder}
         */
        static <T> PropertyListSingleSelectInputBuilder<T> singleListSelect(final Property<T> selectionProperty,
                                                                            Function<T, Optional<PropertyBox>> itemConverter) {
            return Input.singleListSelect(selectionProperty, itemConverter);
        }

        /**
         * Gets a builder to create a <em>options</em> {@link MultiSelect} type
         * {@link Input}, which uses a {@link CheckboxGroup} as input component.
         * <p>
         * This builder can be used when the selection items type and the selection
         * value type are consistent. Use
         * {@link #multiOptionSelect(Class, Class, ItemConverter)} if not.
         * <p>
         *
         * @param <T>  Value type
         * @param type Selection value type (not null)
         * @return A new {@link OptionsMultiSelectInputBuilder}
         */
        static <T> OptionsMultiSelectInputBuilder<T, T> multiOptionSelect(Class<T> type) {
            return Input.multiOptionSelect(type);
        }

        /**
         * Gets a builder to create a <em>options</em> {@link MultiSelect} type
         * {@link Input}, which uses a {@link CheckboxGroup} as input component.
         * <p>
         * This builder can be used when the selection items type and the selection
         * value type are not consistent (i.e. of different type). When the the
         * selection item and the selection value types are consistent, the
         * {@link #multiOptionSelect(Class)} method can be used.
         * <p>
         *
         * @param <T>           Value type
         * @param <ITEM>        Item type
         * @param type          Selection value type (not null)
         * @param itemType      Selection items type (not null)
         * @param itemConverter The item converter to use to convert a selection item
         *                      into a selection (Input) value and back (not null)
         * @return A new {@link OptionsMultiSelectInputBuilder}
         */
        static <T, ITEM> OptionsMultiSelectInputBuilder<T, ITEM> multiOptionSelect(Class<T> type, Class<ITEM> itemType,
                                                                                   ItemConverter<T, ITEM> itemConverter) {
            return Input.multiOptionSelect(type, itemType, itemConverter);
        }

        /**
         * Gets a builder to create a {@link Property} model based <em>options</em>
         * {@link MultiSelect} type {@link Input}, which uses a {@link CheckboxGroup} as
         * input component.
         *
         * @param <T>               Value type
         * @param selectionProperty The property to use to represent the selection value
         *                          (not null)
         * @return A new {@link PropertyOptionsMultiSelectInputBuilder}
         */
        static <T> PropertyOptionsMultiSelectInputBuilder<T> multiOptionSelect(final Property<T> selectionProperty) {
            return Input.multiOptionSelect(selectionProperty);
        }

        /**
         * Gets a builder to create a {@link Property} model based <em>options</em>
         * {@link MultiSelect} type {@link Input}, which uses a {@link CheckboxGroup} as
         * input component.
         *
         * @param <T>               Value type
         * @param selectionProperty The property to use to represent the selection value
         *                          (not null)
         * @param itemConverter     The function to use to convert a selection value
         *                          into the corresponding {@link PropertyBox} item
         * @return A new {@link PropertyOptionsMultiSelectInputBuilder}
         */
        static <T> PropertyOptionsMultiSelectInputBuilder<T> multiOptionSelect(final Property<T> selectionProperty,
                                                                               Function<T, Optional<PropertyBox>> itemConverter) {
            return Input.multiOptionSelect(selectionProperty, itemConverter);
        }

        /**
         * Gets a builder to create a <em>list</em> {@link MultiSelect} type
         * {@link Input}, which uses a {@link MultiSelectListBox} as input component.
         * <p>
         * This builder can be used when the selection items type and the selection
         * value type are consistent. Use
         * {@link #multiListSelect(Class, Class, ItemConverter)} if not.
         * <p>
         *
         * @param <T>  Value type
         * @param type Selection value type (not null)
         * @return A new {@link ListMultiSelectInputBuilder}
         */
        static <T> ListMultiSelectInputBuilder<T, T> multiListSelect(Class<T> type) {
            return Input.multiListSelect(type);
        }

        /**
         * Gets a builder to create a <em>list</em> {@link MultiSelect} type
         * {@link Input}, which uses a {@link MultiSelectListBox} as input component.
         * <p>
         * This builder can be used when the selection items type and the selection
         * value type are not consistent (i.e. of different type). When the the
         * selection item and the selection value types are consistent, the
         * {@link #multiListSelect(Class)} method can be used.
         * <p>
         *
         * @param <T>           Value type
         * @param <ITEM>        Item type
         * @param type          Selection value type (not null)
         * @param itemType      Selection items type (not null)
         * @param itemConverter The item converter to use to convert a selection item
         *                      into a selection (Input) value and back (not null)
         * @return A new {@link ListMultiSelectInputBuilder}
         */
        static <T, ITEM> ListMultiSelectInputBuilder<T, ITEM> multiListSelect(Class<T> type, Class<ITEM> itemType,
                                                                              ItemConverter<T, ITEM> itemConverter) {
            return Input.multiListSelect(type, itemType, itemConverter);
        }

        /**
         * Gets a builder to create a {@link Property} model based <em>list</em>
         * {@link MultiSelect} type {@link Input}, which uses a
         * {@link MultiSelectListBox} as input component.
         *
         * @param <T>               Value type
         * @param selectionProperty The property to use to represent the selection value
         *                          (not null)
         * @return A new {@link PropertyListMultiSelectInputBuilder}
         */
        static <T> PropertyListMultiSelectInputBuilder<T> multiListSelect(final Property<T> selectionProperty) {
            return Input.multiListSelect(selectionProperty);
        }

        /**
         * Gets a builder to create a {@link Property} model based <em>list</em>
         * {@link MultiSelect} type {@link Input}, which uses a
         * {@link MultiSelectListBox} as input component.
         *
         * @param <T>               Value type
         * @param selectionProperty The property to use to represent the selection value
         *                          (not null)
         * @param itemConverter     The function to use to convert a selection value
         *                          into the corresponding {@link PropertyBox} item
         * @return A new {@link PropertyListMultiSelectInputBuilder}
         */
        static <T> PropertyListMultiSelectInputBuilder<T> multiListSelect(final Property<T> selectionProperty,
                                                                          Function<T, Optional<PropertyBox>> itemConverter) {
            return Input.multiListSelect(selectionProperty, itemConverter);
        }

        /**
         * Gets a builder to create a {@link SingleSelect} type {@link Input} for given
         * <code>enum</code> type.
         * <p>
         * All the enum constants declared for the given enum type will be available as
         * selection items.
         * </p>
         *
         * @param <E>      Enum type
         * @param enumType Enum type (not null)
         * @return A new {@link FilterableSingleSelectInputBuilder}
         */
        static <E extends Enum<E>> FilterableSingleSelectInputBuilder<E, E> enumSelect(Class<E> enumType) {
            return Input.enumSelect(enumType);
        }

        /**
         * Gets a builder to create a <em>options</em> {@link SingleSelect} type
         * {@link Input} for given <code>enum</code> type.
         * <p>
         * All the enum constants declared for the given enum type will be available as
         * selection items.
         * </p>
         *
         * @param <E>      Enum type
         * @param enumType Enum type (not null)
         * @return A new {@link OptionsSingleSelectInputBuilder}
         */
        static <E extends Enum<E>> OptionsSingleSelectInputBuilder<E, E> enumOptionSelect(Class<E> enumType) {
            return Input.enumOptionSelect(enumType);
        }

        /**
         * Gets a builder to create a <em>options</em> {@link MultiSelect} type Input
         * for given <code>enum</code> type.
         * <p>
         * All the enum constants declared for the given enum type will be available as
         * selection items.
         * </p>
         *
         * @param <E>      Enum type
         * @param enumType Enum type (not null)
         * @return A new {@link OptionsMultiSelectInputBuilder}
         */
        static <E extends Enum<E>> OptionsMultiSelectInputBuilder<E, E> enumMultiSelect(Class<E> enumType) {
            return Input.enumMultiSelect(enumType);
        }

        /**
         * Get a {@link PropertyInputGroupBuilder} to create and setup a
         * {@link PropertyInputGroup}.
         *
         * @param <P>        Property type
         * @param properties The property set (not null)
         * @return A new {@link PropertyInputGroupBuilder}
         */
        @SuppressWarnings("rawtypes")
        static <P extends Property> PropertyInputGroupBuilder propertyGroup(Iterable<P> properties) {
            return PropertyInputGroup.builder(properties);
        }

        /**
         * Get a {@link PropertyInputGroupBuilder} to create and setup a
         * {@link PropertyInputGroup}.
         *
         * @param properties The property set (not null)
         * @return A new {@link PropertyInputGroupBuilder}
         */
        static PropertyInputGroupBuilder propertyGroup(Property<?>... properties) {
            return PropertyInputGroup.builder(properties);
        }

        /**
         * Get a builder to create a {@link PropertyInputForm} using given property set.
         *
         * @param <C>        Form content element type
         * @param <P>        Property type
         * @param content    The form content, where the {@link Input}s will be composed
         *                   using the configured {@link Composer} (not null)
         * @param properties The property set (not null)
         * @return A new {@link PropertyInputFormBuilder}
         */
        @SuppressWarnings("rawtypes")
        static <C extends Component, P extends Property> PropertyInputFormBuilder<C> form(C content,
                                                                                          Iterable<P> properties) {
            return PropertyInputForm.builder(content, properties);
        }

        /**
         * Get a builder to create a {@link PropertyInputForm} using given property set.
         *
         * @param <C>        Form content element type
         * @param content    The form content, where the {@link Input}s will be composed
         *                   using the configured {@link Composer} (not null)
         * @param properties The property set (not null)
         * @return A new {@link PropertyInputFormBuilder}
         */
        static <C extends Component> PropertyInputFormBuilder<C> form(C content, Property<?>... properties) {
            return PropertyInputForm.builder(content, PropertySet.of(properties));
        }

        /**
         * Get a builder to create a {@link PropertyInputForm} using given property set
         * and a {@link FormLayout} as content layout.
         * <p>
         * A default composer is configured using
         * {@link Composable#componentContainerComposer()}. Use
         * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)}
         * to provide a custom components composer.
         * </p>
         *
         * @param <P>        Property type
         * @param properties The property set (not null)
         * @return A {@link PropertyInputForm} builder
         */
        @SuppressWarnings("rawtypes")
        static <P extends Property> PropertyInputFormBuilder<FormLayout> form(Iterable<P> properties) {
            return PropertyInputForm.formLayout(properties);
        }

        /**
         * Get a builder to create a {@link PropertyInputForm} using given property set
         * and a {@link FormLayout} as content layout.
         * <p>
         * A default composer is configured using
         * {@link Composable#componentContainerComposer()}. Use
         * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)}
         * to provide a custom components composer.
         * </p>
         *
         * @param properties The property set (not null)
         * @return A {@link PropertyInputForm} builder
         */
        static PropertyInputFormBuilder<FormLayout> form(Property<?>... properties) {
            return PropertyInputForm.formLayout(properties);
        }

        /**
         * Get a builder to create a {@link PropertyInputForm} using given property set
         * and a {@link VerticalLayout} as content layout.
         * <p>
         * A default composer is configured using
         * {@link Composable#componentContainerComposer()}. Use
         * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)}
         * to provide a custom components composer.
         * </p>
         *
         * @param <P>        Property type
         * @param properties The property set (not null)
         * @return A {@link PropertyInputForm} builder
         */
        @SuppressWarnings("rawtypes")
        static <P extends Property> PropertyInputFormBuilder<VerticalLayout> formVertical(Iterable<P> properties) {
            return PropertyInputForm.verticalLayout(properties);
        }

        /**
         * Get a builder to create a {@link PropertyInputForm} using given property set
         * and a {@link VerticalLayout} as content layout.
         * <p>
         * A default composer is configured using
         * {@link Composable#componentContainerComposer()}. Use
         * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)}
         * to provide a custom components composer.
         * </p>
         *
         * @param properties The property set (not null)
         * @return A {@link PropertyInputForm} builder
         */
        static PropertyInputFormBuilder<VerticalLayout> formVertical(Property<?>... properties) {
            return PropertyInputForm.verticalLayout(properties);
        }

        /**
         * Get a builder to create a {@link PropertyInputForm} using given property set
         * and a {@link HorizontalLayout} as content layout.
         * <p>
         * A default composer is configured using
         * {@link Composable#componentContainerComposer()}. Use
         * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)}
         * to provide a custom components composer.
         * </p>
         *
         * @param <P>        Property type
         * @param properties The property set (not null)
         * @return A {@link PropertyInputForm} builder
         */
        @SuppressWarnings("rawtypes")
        static <P extends Property> PropertyInputFormBuilder<HorizontalLayout> formHorizontal(Iterable<P> properties) {
            return PropertyInputForm.horizontalLayout(properties);
        }

        /**
         * Get a builder to create a {@link PropertyInputForm} using given property set
         * and a {@link HorizontalLayout} as content layout.
         * <p>
         * A default composer is configured using
         * {@link Composable#componentContainerComposer()}. Use
         * {@link PropertyInputFormBuilder#composer(com.holonplatform.vaadin.flow.components.Composable.Composer)}
         * to provide a custom components composer.
         * </p>
         *
         * @param properties The property set (not null)
         * @return A {@link PropertyInputForm} builder
         */
        static PropertyInputFormBuilder<HorizontalLayout> formHorizontal(Property<?>... properties) {
            return PropertyInputForm.horizontalLayout(properties);
        }

    }

    // Item listings

    /**
     * {@link ItemListing} builders provider.
     */
    static interface listing {

        /**
         * Get a {@link BeanListingBuilder} to create and setup a {@link BeanListing}
         * using given <code>beanType</code>.
         *
         * @param <T>      Bean type
         * @param beanType The bean class, i.e. the item type (not null)
         * @return A new {@link BeanListingBuilder}
         */
        static <T> BeanListingBuilder<T> items(Class<T> beanType) {
            return BeanListing.builder(beanType);
        }

        /**
         * Get a {@link BeanListingBuilder} to create and setup a {@link BeanListing}
         * using given <code>beanType</code>.
         *
         * @param <T>      Bean type
         * @param beanType The bean class, i.e. the item type (not null)
         * @param autoCreateColumns an initial set of columns for each of the bean's properties.
         * @return A new {@link BeanListingBuilder}
         */
        static <T> BeanListingBuilder<T> items(Class<T> beanType,boolean autoCreateColumns) {
            return BeanListing.builder(beanType,autoCreateColumns);
        }

        /**
         * Get a {@link PropertyListingBuilder} to create and setup a
         * {@link PropertyListing}.
         *
         * @param <P>        Property type
         * @param properties The listing property set (not null)
         * @return A new {@link PropertyListingBuilder}
         */
        @SuppressWarnings("rawtypes")
        static <P extends Property> PropertyListingBuilder properties(Iterable<P> properties) {
            return PropertyListing.builder(properties);
        }

        /**
         * Get a {@link PropertyListingBuilder} to create and setup a
         * {@link PropertyListing}.
         *
         * @param properties The listing property set (not null)
         * @return A new {@link PropertyListingBuilder}
         */
        static PropertyListingBuilder properties(Property<?>... properties) {
            return PropertyListing.builder(properties);
        }

    }

    // ------- localization

    /**
     * Get the current {@link Locale}, if available.
     * <p>
     * The current {@link Locale} retrieving strategy is:
     * <ul>
     * <li>If a current {@link UI} is available and a UI {@link Locale} is
     * configured, the UI locale is returned.</li>
     * <li>If a {@link LocalizationContext} is available as a {@link Context}
     * resource and it is localized, the {@link LocalizationContext} {@link Locale}
     * is returned.</li>
     * <li>If a {@link I18NProvider} is available from the {@link VaadinService},
     * the first {@link Locale} from {@link I18NProvider#getProvidedLocales()} is
     * returned, if available.</li>
     * </ul>
     *
     * @return Optional current {@link Locale}
     * @see LocalizationContext#getCurrent()
     */
    static Optional<Locale> getCurrentLocale() {
        return LocalizationProvider.getCurrentLocale();
    }

    /**
     * Get the message localization for given <code>locale</code>, using the
     * provided {@link Localizable} to obtain the message localization key
     * ({@link Localizable#getMessageCode()}) and the optional localization
     * arguments.
     * <p>
     * If a {@link I18NProvider} is available from the current
     * {@link VaadinService}, it is used for message localization. Otherwise, the
     * current {@link LocalizationContext} is used, if it is available as a
     * {@link Context} resource and it is localized.
     * </p>
     *
     * @param locale      The {@link Locale} for which to obtain the message
     *                    localization (not null)
     * @param localizable The {@link Localizable} which represents the message to
     *                    localize (not null)
     * @return The localized message, if available. If the given
     * <code>localizable</code> provides a default message
     * ({@link Localizable#getMessage()}) and a message localization is not
     * available, the default message is returned
     * @see LocalizationContext#getCurrent()
     */
    static Optional<String> getLocalization(Locale locale, Localizable localizable) {
        return LocalizationProvider.getLocalization(locale, localizable);
    }

    /**
     * Get the message localization for given <code>locale</code>, using the
     * provided <code>messageCode</code> as message localization key and the
     * optional localization arguments.
     * <p>
     * If a {@link I18NProvider} is available from the current
     * {@link VaadinService}, it is used for message localization. Otherwise, the
     * current {@link LocalizationContext} is used, if it is available as a
     * {@link Context} resource and it is localized.
     * </p>
     *
     * @param locale      The {@link Locale} for which to obtain the message
     *                    localization (not null)
     * @param messageCode The message localization key (not null)
     * @param arguments   Optional message localization arguments
     * @return The localized message, if available
     * @see LocalizationContext#getCurrent()
     */
    static Optional<String> getLocalization(Locale locale, String messageCode, Object... arguments) {
        return LocalizationProvider.getLocalization(locale, messageCode, arguments);
    }

    /**
     * Get the message localization for given <code>locale</code>, using the
     * provided <code>messageCode</code> as message localization key and the
     * optional localization arguments.
     * <p>
     * If a {@link I18NProvider} is available from the current
     * {@link VaadinService}, it is used for message localization. Otherwise, the
     * current {@link LocalizationContext} is used, if it is available as a
     * {@link Context} resource and it is localized.
     * </p>
     *
     * @param locale         The {@link Locale} for which to obtain the message
     *                       localization (not null)
     * @param defaultMessage The default message to use when a message localization
     *                       is not available for the provided {@link Locale} and
     *                       message code
     * @param messageCode    The message localization key (not null)
     * @param arguments      Optional message localization arguments
     * @return The localized message, or the <code>defaultMessage</code> if not
     * available
     * @see LocalizationContext#getCurrent()
     */
    static String getLocalization(Locale locale, String defaultMessage, String messageCode, Object... arguments) {
        return LocalizationProvider.getLocalization(locale, defaultMessage, messageCode, arguments);
    }

    /**
     * Get the message localization for the current {@link Locale}, using the
     * provided {@link Localizable} to obtain the message localization key
     * ({@link Localizable#getMessageCode()}) and the optional localization
     * arguments.
     * <p>
     * If a {@link I18NProvider} is available from the current
     * {@link VaadinService}, it is used for message localization. Otherwise, the
     * current {@link LocalizationContext} is used, if it is available as a
     * {@link Context} resource and it is localized.
     * </p>
     * <p>
     * The message localization will be performed only if a current {@link Locale}
     * is available.
     * </p>
     *
     * @param localizable The {@link Localizable} which represents the message to
     *                    localize (not null)
     * @return The localized message, if available. If the given
     * <code>localizable</code> provides a default message
     * ({@link Localizable#getMessage()}) and a message localization is not
     * available, the default message is returned
     * @see #getCurrentLocale()
     */
    static Optional<String> localize(Localizable localizable) {
        return LocalizationProvider.localize(localizable);
    }

    /**
     * Get the message localization for the current {@link Locale}, using the
     * provided <code>messageCode</code> as message localization key and the
     * optional localization arguments.
     * <p>
     * If a {@link I18NProvider} is available from the current
     * {@link VaadinService}, it is used for message localization. Otherwise, the
     * current {@link LocalizationContext} is used, if it is available as a
     * {@link Context} resource and it is localized.
     * </p>
     * <p>
     * The message localization will be performed only if a current {@link Locale}
     * is available.
     * </p>
     *
     * @param messageCode The message localization key (not null)
     * @param arguments   Optional message localization arguments
     * @return The localized message, if available
     * @see #getCurrentLocale()
     */
    static Optional<String> localize(String messageCode, Object... arguments) {
        return LocalizationProvider.localize(messageCode, arguments);
    }

    /**
     * Get the message localization for the current {@link Locale}, using the
     * provided <code>messageCode</code> as message localization key and the
     * optional localization arguments.
     * <p>
     * If a {@link I18NProvider} is available from the current
     * {@link VaadinService}, it is used for message localization. Otherwise, the
     * current {@link LocalizationContext} is used, if it is available as a
     * {@link Context} resource and it is localized.
     * </p>
     * <p>
     * The message localization will be performed only if a current {@link Locale}
     * is available.
     * </p>
     *
     * @param defaultMessage The default message to use when a message localization
     *                       is not available for the provided {@link Locale} and
     *                       message code
     * @param messageCode    The message localization key (not null)
     * @param arguments      Optional message localization arguments
     * @return The localized message, or the <code>defaultMessage</code> if not
     * available
     * @see #getCurrentLocale()
     */
    static String localize(String defaultMessage, String messageCode, Object... arguments) {

        return LocalizationProvider.localize(defaultMessage, messageCode, arguments);
    }


}
