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
import com.holonplatform.vaadin.flow.internal.components.builders.*;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueItem;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueList;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.holonplatform.vaadin.flow.vaadinplus.components.*;
import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.iyensoft.vaadin.flow.components.Panel;
import com.iyensoft.vaadin.flow.components.builders.*;
import com.iyensoft.vaadin.flow.internal.components.builders.MobileGridColumnBuilder;
import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
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
     * {@link Property} to create the <em>steps</em> to show for the
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
    ConfigProperty<Duration> TIME_INPUT_STEP = ConfigProperty
            .create(Components.class.getName() + ".time-input-step", Duration.class);

    // Configurators

    /**
     * Get a {@link LabelConfigurator} to create given <em>label</em> type
     * component.
     * <p>
     * The component must be a {@link HtmlContainer} and a {@link ClickNotifier},
     * such as {@link Span} or {@link Div}.
     * </p>
     *
     * @param <L>   Label element type
     * @param label The component to create (not null)
     * @return A {@link LabelConfigurator}
     */
    @SuppressWarnings("rawtypes")
    static <L extends HtmlContainer & ClickNotifier> BaseLabelConfigurator<L> configure(L label) {
        return LabelConfigurator.configure(label);
    }

    /**
     * Get a {@link ButtonConfigurator} to create given {@link Button} instance.
     *
     * @param button Button to create (not null)
     * @return A {@link ButtonConfigurator}
     */
    static BaseButtonConfigurator configure(Button button) {
        return ButtonConfigurator.configure(button);
    }

    /**
     * Get a {@link VerticalLayoutConfigurator} to create given
     * {@link VerticalLayout}.
     *
     * @param layout Layout to create
     * @return A new {@link VerticalLayoutConfigurator}
     */
    static VerticalLayoutConfigurator configure(VerticalLayout layout) {
        return ThemableFlexComponentConfigurator.configure(layout);
    }

    /**
     * Get a {@link HorizontalLayoutConfigurator} to create given
     * {@link HorizontalLayout}.
     *
     * @param layout Layout to create
     * @return A new {@link HorizontalLayoutConfigurator}
     */
    static HorizontalLayoutConfigurator configure(HorizontalLayout layout) {
        return ThemableFlexComponentConfigurator.configure(layout);
    }

    /**
     * Get a {@link BaseFormLayoutConfigurator} to create given
     * {@link FormLayout}.
     *
     * @param layout Layout to create
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

    static LayoutConfigurator.BaseLayoutConfigurator configure(Layout layout) {
        return LayoutConfigurator.configure(layout);
    }


    static NotificationConfigurator.BaseNotificationConfigurator configure(Notification notification) {
        return NotificationConfigurator.configure(notification);
    }

    static NotificationBuilder notification() {
        return NotificationBuilder.create();
    }

    static GridHeaderBuilder gridHeader(String title) {
        return GridHeaderBuilder.create(title);
    }

    static GridHeaderBuilder gridHeader(LabelBuilder<?> labelBuilder) {
        return GridHeaderBuilder.create(labelBuilder);
    }

    static TabsBuilder tabs() {
        return TabsBuilder.create();
    }

    static LazyTabsBuilder lazyTabs() {
        return LazyTabsBuilder.create();
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

    /**
     * Obtain a {@link SheetBuilder} for a {@link Sheet.Side#BOTTOM} sheet
     * (slides up from the bottom â€” primary mobile pattern).
     *
     * @return a new {@link SheetBuilder}
     */
    static SheetBuilder sheet() {
        return SheetBuilder.create();
    }

    /**
     * Obtain a {@link SheetBuilder} for the given slide direction.
     *
     * @param side the edge from which the panel slides in (not null)
     * @return a new {@link SheetBuilder}
     */
    static SheetBuilder sheet(Sheet.Side side) {
        return SheetBuilder.create(side);
    }

    /**
     * Obtain a configurator for an already-created {@link Sheet} instance.
     *
     * @param sheet the sheet to configure (not null)
     * @return a {@link SheetConfigurator.BaseSheetConfigurator}
     */
    static SheetConfigurator.BaseSheetConfigurator configure(Sheet sheet) {
        return SheetConfigurator.configure(sheet);
    }

    /**
     * Create a {@link SheetStack} that slides sheets in from the given edge.
     *
     * <p>A {@link SheetStack} is a bounded navigation stack backed by a
     * {@link com.holonplatform.core.utils.SizedStack}. Each {@code push(title, content)}
     * call creates, configures, and opens a new sheet, applying stacking rules
     * automatically:</p>
     * <ul>
     *   <li>Root sheet â€” {@code backdropVisible(true)}: dims the app content behind it.</li>
     *   <li>Child sheets â€” {@code backdropVisible(false)}: parent not visible through overlay.</li>
     *   <li>All sheets â€” {@code fullscreenOnMobile(true)}: full viewport on mobile.</li>
     * </ul>
     *
     * <pre>{@code
     * var stack = Components.sheetStack(Sheet.Side.RIGHT);
     * openBtn.addClickListener(e -> stack.push("Categories", categoryList));
     * catRow.addClickListener(e -> stack.push(cat.name(), productList));
     * viewBtn.addClickListener(e -> stack.push("Detail", detailView));
     * }</pre>
     *
     * @param side the edge from which all sheets in this stack slide in (not null)
     * @return a new {@link SheetStack}
     */
    static SheetStack sheetStack(Sheet.Side side) {
        return new SheetStack(side);
    }


    // Builders

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a {@link Div}
     * tag.
     * <p>
     * This is an alias for the {@link #divLabel()} ()} method.
     * </p>
     *
     * @return The {@link LabelBuilder} to create and obtain the component
     * instance
     */
    static LabelBuilder<Div> label() {
        return divLabel();
    }

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a
     * {@link Span} tag.
     *
     * @return The {@link LabelBuilder} to create and obtain the component
     * instance
     */
    static LabelBuilder<Span> span() {
        return LabelBuilder.span();
    }


    /**
     * Obtain a {@link LabelBuilder} to create a label component using a {@link Div}
     * tag.
     *
     * @return The {@link LabelBuilder} to create and obtain the component
     * instance
     */
    static LabelBuilder<Div> divLabel() {
        return LabelBuilder.div();
    }

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a
     * {@link Paragraph} tag.
     *
     * @return The {@link LabelBuilder} to create and obtain the component
     * instance
     */
    static LabelBuilder<Paragraph> paragraph() {
        return LabelBuilder.paragraph();
    }

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a {@link H1}
     * tag.
     *
     * @return The {@link LabelBuilder} to create and obtain the component
     * instance
     */
    static LabelBuilder<H1> h1() {
        return LabelBuilder.h1();
    }

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a {@link H2}
     * tag.
     *
     * @return The {@link LabelBuilder} to create and obtain the component
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
     * @return The {@link LabelBuilder} to create and obtain the component
     * instance
     */
    static LabelBuilder<H3> h3() {
        return LabelBuilder.h3();
    }

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a {@link H4}
     * tag.
     *
     * @return The {@link LabelBuilder} to create and obtain the component
     * instance
     */
    static LabelBuilder<H4> h4() {
        return LabelBuilder.h4();
    }

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a {@link H5}
     * tag.
     *
     * @return The {@link LabelBuilder} to create and obtain the component
     * instance
     */
    static LabelBuilder<H5> h5() {
        return LabelBuilder.h5();
    }

    /**
     * Obtain a {@link LabelBuilder} to create a label component using a {@link H6}
     * tag.
     *
     * @return The {@link LabelBuilder} to create and obtain the component
     * instance
     */
    static LabelBuilder<H6> h6() {
        return LabelBuilder.h6();
    }

    /**
     * Gets a builder to create an anonymous {@link Avatar}.
     *
     * @return A new {@link AvatarBuilder}
     */
    static AvatarBuilder avatar() {
        return AvatarBuilder.create();
    }

    /**
     * Gets a builder to create a named {@link Avatar}.
     *
     * @param name display name shown in the tooltip; auto-generates initials
     * @return A new {@link AvatarBuilder}
     */
    static AvatarBuilder avatar(String name) {
        return AvatarBuilder.create(name);
    }

    /**
     * Gets a builder to create a named {@link Avatar} with a profile image.
     *
     * @param name     display name
     * @param imageUrl profile-image URL
     * @return A new {@link AvatarBuilder}
     */
    static AvatarBuilder avatar(String name, String imageUrl) {
        return AvatarBuilder.create(name, imageUrl);
    }

    /**
     * Gets a builder to create an empty {@link com.vaadin.flow.component.avatar.AvatarGroup}.
     *
     * @return A new {@link AvatarGroupBuilder}
     */
    static AvatarGroupBuilder avatarGroup() {
        return AvatarGroupBuilder.create();
    }

    /**
     * Gets a builder to create {@link Button}s.
     *
     * @return A new {@link ButtonBuilder}
     */
    static ButtonBuilder button() {
        return ButtonBuilder.create();
    }

    static LayoutBuilder layout(Component... components) {
        return LayoutBuilder.create(components);
    }

    static LayoutBuilder layout() {
        return LayoutBuilder.create();
    }

    static HeaderBuilder header(String title) {
        return HeaderBuilder.create(title);
    }

    static HeaderBuilder header(LabelBuilder<?> labelBuilder) {
        return HeaderBuilder.create(labelBuilder);
    }

    static HeaderBuilder configure(Header header) {
        return new DefaultHeaderBuilder(header);
    }

    static FooterBuilder footer() {
        return FooterBuilder.create();
    }

    static FooterBuilder configure(Footer footer) {
        return new DefaultFooterBuilder(footer);
    }

    static FooterConfigurator.BaseFooterConfigurator footer(Footer footer) {
        return FooterConfigurator.configure(footer);
    }

    static BreadcrumbBuilder breadcrumb() {
        return BreadcrumbBuilder.create();
    }

    static BreadcrumbBuilder breadcrumb(Breadcrumb breadcrumb) {
        return BreadcrumbBuilder.create(breadcrumb);
    }

    static MobileGridColumnBuilder mobileGridColumn() {
        return MobileGridColumnBuilder.create();
    }

    static MobileGridColumnBuilder mobileGridColumn(Layout layout) {
        return MobileGridColumnBuilder.create(layout);
    }

    /**
     * Get a {@link LitRendererBuilder.MobileGridColumnBuilder} to create a client-side Lit renderer
     * with the same visual structure as {@link MobileGridColumnBuilder} but zero server-side
     * component overhead per row.
     *
     * @param <T> the grid item type
     * @return a new builder instance
     */
    static <T> LitRendererBuilder.MobileGridColumnBuilder<T> mobileGridColumnLit() {
        return LitRendererBuilder.mobileGridColumn();
    }

    /**
     * Get a {@link LitRendererBuilder} for composing client-side Lit template renderers.
     *
     * @param <T> the grid item type
     * @return a new {@link LitRendererBuilder}
     */
    static <T> LitRendererBuilder<T> litRenderer() {
        return LitRendererBuilder.create();
    }

    // -----------------------------------------------------------------------
    // Alert (shadcn/ui-inspired inline notification banner)
    // -----------------------------------------------------------------------

    /**
     * Get an {@link AlertBuilder} to create an {@link Alert} with the default variant.
     *
     * @return a new {@link AlertBuilder}
     */
    static AlertBuilder alert() {
        return AlertBuilder.create();
    }

    /**
     * Get an {@link AlertBuilder} to create an {@link Alert} with the given variant.
     *
     * @param variant the alert variant (not null)
     * @return a new {@link AlertBuilder}
     */
    static AlertBuilder alert(Alert.Variant variant) {
        return AlertBuilder.create(variant);
    }

    /**
     * Get an {@link AlertConfigurator} to configure an existing {@link Alert}.
     *
     * @param alert the alert to configure (not null)
     * @return a {@link AlertConfigurator.BaseAlertConfigurator}
     */
    static AlertConfigurator.BaseAlertConfigurator configure(Alert alert) {
        return AlertConfigurator.configure(alert);
    }

    // -----------------------------------------------------------------------
    // AlertModal (shadcn/ui-inspired dismissible modal notification)
    // -----------------------------------------------------------------------

    /**
     * Get an {@link AlertModalBuilder} to create an {@link AlertModal} with the default variant.
     *
     * @return a new {@link AlertModalBuilder}
     */
    static AlertModalBuilder alertModal() {
        return AlertModalBuilder.create();
    }

    /**
     * Get an {@link AlertModalBuilder} to create an {@link AlertModal} with the given variant.
     *
     * @param variant the alert variant (not null)
     * @return a new {@link AlertModalBuilder}
     */
    static AlertModalBuilder alertModal(Alert.Variant variant) {
        return AlertModalBuilder.create(variant);
    }

    /**
     * Get an {@link AlertModalConfigurator} to configure an existing {@link AlertModal}.
     *
     * @param modal the modal to configure (not null)
     * @return a {@link AlertModalConfigurator.BaseAlertModalConfigurator}
     */
    static AlertModalConfigurator.BaseAlertModalConfigurator configure(AlertModal modal) {
        return AlertModalConfigurator.configure(modal);
    }

    // -----------------------------------------------------------------------
    // AlertDialog (shadcn/ui-inspired non-dismissible confirmation dialog)
    // -----------------------------------------------------------------------

    /**
     * Get an {@link AlertDialogBuilder} to create an {@link AlertDialog} â€” the
     * shadcn/ui-inspired confirmation dialog that forces an explicit user choice.
     *
     * <p>By default the dialog is non-dismissible (no ESC, no click-outside).
     * Use for dangerous or irreversible operations such as deletion or sign-out.</p>
     *
     * <pre>{@code
     * Components.alertDialog()
     *     .title("Are you absolutely sure?")
     *     .description("This action cannot be undone.")
     *     .variant(Alert.Variant.DESTRUCTIVE)
     *     .confirmText("Yes, delete account")
     *     .onConfirm(() -> accountService.delete(currentUser))
     *     .open();
     * }</pre>
     *
     * @return a new {@link AlertDialogBuilder}
     */
    static AlertDialogBuilder alertDialog() {
        return AlertDialogBuilder.create();
    }

    /**
     * Get an {@link AlertDialogConfigurator} to configure an existing {@link AlertDialog}.
     *
     * @param dialog the dialog to configure (not null)
     * @return a {@link AlertDialogConfigurator.BaseAlertDialogConfigurator}
     */
    static AlertDialogConfigurator.BaseAlertDialogConfigurator configure(AlertDialog dialog) {
        return AlertDialogConfigurator.configure(dialog);
    }

    // -----------------------------------------------------------------------
    // IconBadge â€” circular tinted icon container
    // -----------------------------------------------------------------------

    /**
     * Get an {@link IconBadgeBuilder} for a neutral badge (no icon pre-set).
     *
     * <pre>{@code
     * IconBadge badge = Components.iconBadge()
     *     .icon(VaadinIcon.CHECK_CIRCLE)
     *     .variant(Alert.Variant.SUCCESS)
     *     .size(IconBadge.Size.LG)
     *     .build();
     * }</pre>
     *
     * @return a new {@link IconBadgeBuilder}
     */
    static IconBadgeBuilder iconBadge() {
        return IconBadgeBuilder.create();
    }

    /**
     * Get an {@link IconBadgeBuilder} pre-configured with the given icon.
     *
     * @param icon the VaadinIcon to display (not null)
     * @return a new {@link IconBadgeBuilder}
     */
    static IconBadgeBuilder iconBadge(Icon icon) {
        return IconBadgeBuilder.create(icon);
    }

    /**
     * Get an {@link IconBadgeBuilder} pre-configured with icon and variant.
     *
     * <pre>{@code
     * // Green circle + checkmark, large
     * Components.iconBadge(VaadinIcon.CHECK_CIRCLE, Alert.Variant.SUCCESS)
     *     .size(IconBadge.Size.LG).build();
     * }</pre>
     *
     * @param icon    the VaadinIcon to display (not null)
     * @param variant the semantic color variant (null = neutral)
     * @return a new {@link IconBadgeBuilder}
     */
    static IconBadgeBuilder iconBadge(Icon icon, Alert.Variant variant) {
        return IconBadgeBuilder.create(icon, variant);
    }

    /**
     * Get a fully-specified {@link IconBadgeBuilder}.
     *
     * @param icon    the VaadinIcon to display (not null)
     * @param variant the semantic color variant (null = neutral)
     * @param size    the size preset (not null)
     * @return a new {@link IconBadgeBuilder}
     */
    static IconBadgeBuilder iconBadge(Icon icon, Alert.Variant variant, IconBadge.Size size) {
        return IconBadgeBuilder.create(icon, variant, size);
    }

    /**
     * Get an {@link IconBadgeConfigurator} to configure an existing {@link IconBadge}.
     *
     * @param badge the badge to configure (not null)
     * @return a {@link IconBadgeConfigurator.BaseIconBadgeConfigurator}
     */
    static IconBadgeConfigurator.BaseIconBadgeConfigurator configure(IconBadge badge) {
        return IconBadgeConfigurator.configure(badge);
    }

    // -----------------------------------------------------------------------
    // Empty (shadcn/ui-inspired empty-state component)
    // -----------------------------------------------------------------------

    /**
     * Get an {@link EmptyBuilder} to create an {@link Empty} empty-state component.
     *
     * <p>Use this component to communicate that a collection, list, or data set
     * contains no items â€” and give the user a clear path forward.</p>
     *
     * @return a new {@link EmptyBuilder}
     */
    static EmptyBuilder empty() {
        return EmptyBuilder.create();
    }

    /**
     * Get an {@link EmptyConfigurator} to configure an existing {@link Empty} instance.
     *
     * @param empty the empty component to configure (not null)
     * @return a {@link EmptyConfigurator.BaseEmptyConfigurator}
     */
    static EmptyConfigurator.BaseEmptyConfigurator configure(Empty empty) {
        return EmptyConfigurator.configure(empty);
    }

    // -----------------------------------------------------------------------
    // InputGroup (shadcn/ui-inspired unified input-field grouping container)
    // -----------------------------------------------------------------------

    /**
     * Get an {@link InputGroupBuilder} to create an {@link InputGroup} â€” a horizontal
     * flex container that merges input fields, buttons, and text addons into a single
     * unified control.
     *
     * <pre>{@code
     * InputGroup group = Components.inputGroup()
     *     .content(new InputGroupText("@"))
     *     .content(new TextField())
     *     .content(new Button("Go"))
     *     .build();
     * }</pre>
     *
     * @return a new {@link InputGroupBuilder}
     */
    static InputGroupBuilder inputGroup() {
        return InputGroupBuilder.create();
    }

    /**
     * Get an {@link InputGroupLayoutConfigurator} to configure an existing {@link InputGroup} instance.
     *
     * @param inputGroup the group to configure (not null)
     * @return a {@link InputGroupLayoutConfigurator.BaseInputGroupLayoutConfigurator}
     */
    static InputGroupLayoutConfigurator.BaseInputGroupLayoutConfigurator configure(InputGroup inputGroup) {
        return InputGroupLayoutConfigurator.configure(inputGroup);
    }

    // -----------------------------------------------------------------------
    // ButtonGroup (shadcn/ui-inspired button group)
    // -----------------------------------------------------------------------

    /**
     * Get a {@link ButtonGroupBuilder} to create a {@link com.holonplatform.vaadin.flow.vaadinplus.components.ButtonGroup}.
     *
     * <pre>{@code
     * ButtonGroup group = Components.buttonGroup()
     *     .content(new Button("Day"), new Button("Week"), new Button("Month"))
     *     .build();
     * }</pre>
     *
     * @return a new {@link ButtonGroupBuilder}
     */
    static ButtonGroupBuilder buttonGroup() {
        return ButtonGroupBuilder.create();
    }

    /**
     * Get a {@link ButtonGroupConfigurator} to configure an existing
     * {@link com.holonplatform.vaadin.flow.vaadinplus.components.ButtonGroup} instance.
     *
     * @param buttonGroup the group to configure (not null)
     * @return a {@link ButtonGroupConfigurator.BaseButtonGroupConfigurator}
     */
    static ButtonGroupConfigurator.BaseButtonGroupConfigurator configure(
            ButtonGroup buttonGroup) {
        return ButtonGroupConfigurator.configure(buttonGroup);
    }

    // -----------------------------------------------------------------------
    // InputOTP (shadcn/ui-inspired one-time password input)
    // -----------------------------------------------------------------------

    /**
     * Get an {@link InputOTPBuilder} to create an {@link InputOTP} one-time password input.
     *
     * <pre>{@code
     * InputOTP otp = Components.inputOTP()
     *     .group(3).separator().group(3)
     *     .pattern("[0-9]")
     *     .onComplete(value -> verifyCode(value))
     *     .build();
     * }</pre>
     *
     * @return a new {@link InputOTPBuilder}
     */
    static InputOTPBuilder inputOTP() {
        return InputOTPBuilder.create();
    }

    /**
     * Get an {@link InputOTPConfigurator} to configure an existing {@link InputOTP} instance.
     *
     * @param otp the OTP component to configure (not null)
     * @return a {@link InputOTPConfigurator.BaseInputOTPConfigurator}
     */
    static InputOTPConfigurator.BaseInputOTPConfigurator configure(InputOTP otp) {
        return InputOTPConfigurator.configure(otp);
    }

    // -----------------------------------------------------------------------
    // Separator (shadcn/ui-inspired separator)
    // -----------------------------------------------------------------------

    /**
     * Get a {@link SeparatorBuilder} to create a {@link Separator} component.
     *
     * <pre>{@code
     * // Horizontal rule (default)
     * Separator sep = Components.separator().build();
     *
     * // Vertical rule
     * Separator sep = Components.separator()
     *     .orientation(Separator.Orientation.VERTICAL)
     *     .build();
     *
     * // Decorative (hidden from assistive technologies)
     * Separator sep = Components.separator()
     *     .decorative(true)
     *     .build();
     * }</pre>
     *
     * @return a new {@link SeparatorBuilder}
     */
    static SeparatorBuilder separator() {
        return SeparatorBuilder.create();
    }

    /**
     * Get a {@link SeparatorConfigurator} to configure an existing {@link Separator} instance.
     *
     * @param separator the separator to configure (not null)
     * @return a {@link SeparatorConfigurator.BaseSeparatorConfigurator}
     */
    static SeparatorConfigurator.BaseSeparatorConfigurator configure(Separator separator) {
        return SeparatorConfigurator.configure(separator);
    }

    // -----------------------------------------------------------------------
    // Stepper
    // -----------------------------------------------------------------------

    /**
     * Get a {@link StepperBuilder} to create a {@link FlowStepper} component.
     *
     * <pre>{@code
     * // Horizontal stepper (default)
     * FlowStepper stepper = Components.stepper()
     *     .steps("Account", "Details", "Review", "Confirm")
     *     .build();
     *
     * // Vertical stepper starting at step 1
     * FlowStepper stepper = Components.stepper()
     *     .steps("Choose Plan", "Payment", "Go Live")
     *     .currentStep(1)
     *     .orientation(FlowStepper.Orientation.VERTICAL)
     *     .build();
     * }</pre>
     *
     * @return a new {@link StepperBuilder}
     */
    static StepperBuilder stepper() {
        return StepperBuilder.create();
    }

    /**
     * Get a {@link StepperConfigurator} to configure an existing {@link FlowStepper} instance.
     *
     * @param stepper the stepper to configure (not null)
     * @return a {@link StepperConfigurator.BaseStepperConfigurator}
     */
    static StepperConfigurator.BaseStepperConfigurator configure(FlowStepper stepper) {
        return StepperConfigurator.configure(stepper);
    }

    // -----------------------------------------------------------------------
    // Timeline Stepper (audit log)
    // -----------------------------------------------------------------------

    /**
     * Get a {@link TimelineStepperBuilder} to create a {@link TimelineStepper} component.
     *
     * <pre>{@code
     * TimelineStepper tl = Components.timelineStepper()
     *     .pageSize(20)
     *     .hasMore(true)
     *     .width("100%")
     *     .onLoadMore(e -> tl.appendEntries(service.getPage(e.getPage())))
     *     .build();
     * }</pre>
     *
     * @return a new {@link TimelineStepperBuilder}
     */
    static TimelineStepperBuilder timelineStepper() {
        return TimelineStepperBuilder.create();
    }

    /**
     * Get a {@link TimelineStepperConfigurator} to configure an existing
     * {@link TimelineStepper} instance.
     *
     * @param timeline the timeline to configure (not null)
     * @return a {@link TimelineStepperConfigurator.BaseTimelineStepperConfigurator}
     */
    static TimelineStepperConfigurator.BaseTimelineStepperConfigurator configure(TimelineStepper timeline) {
        return TimelineStepperConfigurator.configure(timeline);
    }

    interface utils {
        static FormResponsiveStepBuilder responsiveSteps() {
            return FormResponsiveStepBuilder.create();
        }


    }

    static CardBuilder card() {
        return CardBuilder.create();
    }

    static RowBuilder row() {
        return RowBuilder.create();
    }

    static ColumnBuilder column() {
        return ColumnBuilder.create();
    }

    static PanelBuilder panel() {
        return PanelBuilder.create();
    }

    static PanelBuilder panel(Panel panel) {
        return PanelBuilder.create(panel);
    }

    /**
     * Creates a typed {@link MasterDetailBuilder} for the given bean type.
     * Enables no-arg {@code listing()}, type-witness-free {@code withSelectionKey},
     * and uncast {@code withDetailSync} lambdas.
     *
     * <pre>{@code
     * MasterDetailLayout<Order> mdl = Components.masterDetail(Order.class)
     *     .master()
     *         .listing().fetch((q, t) -> service.fetch(q)).add()
     *         .withSelectionKey(Order::getId).add()
     *     .detail()
     *         .withDetailSync(o -> populate(o)).add()
     *     .build();
     * mdl.selectFirst(viewMode);
     * mdl.notifyDataChanged();
     * }</pre>
     *
     * @param <T>      item type
     * @param beanType bean class (not null)
     * @return a new typed {@link MasterDetailBuilder}
     */
    static <T> MasterDetailBuilder<T> masterDetail(Class<T> beanType) {
        return MasterDetailBuilder.create(beanType);
    }

    /**
     * Creates a {@link MasterDetailBuilder} backed by a Holon {@link PropertySet}.
     * The item type is fixed to {@link PropertyBox}; enables no-arg {@code .master().listing()}
     * which constructs a {@link PropertyListing} from the supplied property set.
     *
     * <pre>{@code
     * MasterDetailLayout<PropertyBox> mdl = Components.masterDetail(PRODUCT_SET)
     *     .master()
     *         .listing()
     *             .search("Search…")
     *             .fetch((q, text, sort) -> datastore.query(TARGET)
     *                 .restrict(q.getLimit(), q.getOffset())
     *                 .stream(PRODUCT_SET))
     *             .add()
     *         .withSelectionKey(pb -> pb.getValue(ID))
     *         .add()
     *     .detail()
     *         .withDetailSync(pb -> populate(pb))
     *         .add()
     *     .build();
     * }</pre>
     *
     * @param propertySet the Holon property set that drives the listing (not null)
     * @return a new {@link MasterDetailBuilder}{@code <PropertyBox>}
     */
    static MasterDetailBuilder<PropertyBox> masterDetail(PropertySet<?> propertySet) {
        return MasterDetailBuilder.create(propertySet);
    }

    /**
     * Creates a {@link PanelConfigurator.BasePanelConfigurator} for the given
     * {@link Panel} instance.
     *
     * <pre>{@code
     * Panel panel = new Panel();
     * Components.configure(panel)
     *     .header()
     *         .heading("Deployment status")
     *         .details(new Span("Last updated just now"))
     *         .actions(new Button("Open details"), new Button("Archive"))
     *         .add()
     *     .content(new Div(new Span("Body content")))
     *     .footer()
     *         .meta(new Span("Status: Ready"))
     *         .add();
     * }</pre>
     *
     * @param panel the panel to configure (not null)
     * @return a configurator for the provided panel
     * @since 10.0.0
     */
    static PanelConfigurator.BasePanelConfigurator configure(Panel panel) {
        return PanelConfigurator.configure(panel);
    }


    static LabelBuilder<H4> title() {
        LabelBuilder<H4> h4LabelBuilder = LabelBuilder.h4();
        h4LabelBuilder.styleNames(UIUtils.getTitleStyles());
        return h4LabelBuilder;
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

    static <T> Grid<T> grid(Class<T> tClass) {
        return new Grid<T>(tClass);
    }

    static <T> Grid<T> grid(Class<T> tClass, boolean autoCreateColumns) {
        return new Grid<T>(tClass, autoCreateColumns);
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

    static DefaultFormFooter formFooter() {
        return new DefaultFormFooter();
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
    static MenuBarBuilder menuBar() {
        return MenuBarBuilder.create();
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
    interface dialog {

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
            message().withContent(message).open();
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

        /*static ConfirmDialogBuilder save(boolean okToCancelDialog) {
            return DialogBuilder.save(okToCancelDialog);
        }

        static void showSave(boolean okToCancelDialog, String text) {
            save(okToCancelDialog)
                    .okButtonConfigurator(baseButtonConfigurator -> {
                        baseButtonConfigurator.withThemeVariants(ButtonVariant.LUMO_PRIMARY);
                        baseButtonConfigurator.text("Save", "save.code");
                    }).text(text)
                    .open();
        }*/

        /**
         * Show a confirm dialog with given localizable message text.
         *
         * @param message The dialog message text
         */
        static void showConfirm(Localizable message) {
            confirm().withContent(message).open();
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

        // -----------------------------------------------------------------------
        // AlertDialog (shadcn/ui-inspired non-dismissible confirmation dialog)
        // -----------------------------------------------------------------------

        /**
         * Get an {@link AlertDialogBuilder} to create an {@link AlertDialog} â€” the
         * shadcn/ui-inspired confirmation dialog that forces an explicit user choice.
         * <p>By default the dialog is non-dismissible (no ESC, no click-outside).</p>
         *
         * @return a new {@link AlertDialogBuilder}
         */
        static AlertDialogBuilder alertDialog() {
            return AlertDialogBuilder.create();
        }

        /**
         * Get an {@link AlertDialogConfigurator} to configure an existing {@link AlertDialog}.
         *
         * @param dialog the dialog to configure (not null)
         * @return a {@link AlertDialogConfigurator.BaseAlertDialogConfigurator}
         */
        static AlertDialogConfigurator.BaseAlertDialogConfigurator configure(AlertDialog dialog) {
            return AlertDialogConfigurator.configure(dialog);
        }

        /**
         * Show a question dialog with given localizable message text.
         *
         * @param questionDialogCallback The callback function use to react to the user
         *                               selection (not null)
         * @param message                The dialog message text
         */
        static void showQuestion(QuestionDialogCallback questionDialogCallback, Localizable message) {
            question(questionDialogCallback).withContent(message).open();
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
            delete(deleteDialogCallback).withContent(message).open();
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

    interface Badge {
        /**
         * Obtain a {@link LabelBuilder} to create a badge component using a
         * {@link Span} tag.
         *
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
         * @return The {@link LabelBuilder} to create and obtain the component
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
    interface view {

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
    interface input {

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
         * Gets a builder to create a NumberField-backed numeric type {@link Input}.
         *
         * @param <T>         Number type
         * @param numberClass Number class (not null)
         * @return A new {@link NumberFieldInputBuilder}
         */
        static <T extends Number> NumberFieldInputBuilder<T> numberField(Class<T> numberClass) {
            return Input.numberField(numberClass);
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
    interface listing {

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
         * @param <T>               Bean type
         * @param beanType          The bean class, i.e. the item type (not null)
         * @param autoCreateColumns an initial set of columns for each of the bean's properties.
         * @return A new {@link BeanListingBuilder}
         */
        static <T> BeanListingBuilder<T> items(Class<T> beanType, boolean autoCreateColumns) {
            return BeanListing.builder(beanType, autoCreateColumns);
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

    // -----------------------------------------------------------------------
    // Carousel
    // -----------------------------------------------------------------------

    /**
     * Get a {@link CarouselBuilder} to create a horizontal {@link Carousel}.
     *
     * <p>Usage:
     * <pre>{@code
     * Carousel carousel = Components.carousel()
     *     .loop(true)
     *     .addItem(card1, card2, card3)
     *     .build();
     * }</pre>
     *
     * @return a new {@link CarouselBuilder}
     */
    static CarouselBuilder carousel() {
        return CarouselBuilder.create();
    }

    /**
     * Get a {@link CarouselBuilder} to create a {@link Carousel} with the given orientation.
     *
     * @param orientation the scroll axis (not null)
     * @return a new {@link CarouselBuilder}
     */
    static CarouselBuilder carousel(Carousel.Orientation orientation) {
        return CarouselBuilder.create(orientation);
    }

    /**
     * Get a {@link CarouselConfigurator.BaseCarouselConfigurator} to configure an
     * existing {@link Carousel} instance.
     *
     * @param carousel the carousel to configure (not null)
     * @return a new {@link CarouselConfigurator.BaseCarouselConfigurator}
     */
    static CarouselConfigurator.BaseCarouselConfigurator configure(Carousel carousel) {
        return CarouselConfigurator.configure(carousel);
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

    // -----------------------------------------------------------------------
    // Page-size selector
    // -----------------------------------------------------------------------

    /**
     * Starts building a <em>"Show N entries"</em> page-size selector bound to
     * the given {@link ItemListing}.
     *
     * <pre>{@code
     * BeanListing<Person> listing = ...;
     *
     * ItemListingPageSizeSelector<Person, String> selector =
     *         Components.pageSizeSelector(listing)
     *             .withOptions(10, 25, 50, 100)
     *             .withDefaultSize(25)
     *             .build();
     * }</pre>
     *
     * @param listing the listing to control (not null)
     * @param <T>     item type
     * @param <P>     property type
     * @return a new {@link ItemListingPageSizeSelector.Builder}
     * @since 10.0.1
     */
    static <T, P> ItemListingPageSizeSelector.Builder<T, P> pageSizeSelector(ItemListing<T, P> listing) {
        return ItemListingPageSizeSelector.of(listing);
    }

    /**
     * Starts building a <em>"Show N entries"</em> page-size selector bound to
     * the given {@link PropertyListing}.
     *
     * <p>Convenience overload that avoids the verbose wildcard generics at call
     * sites when working with {@link PropertyListing}.</p>
     *
     * <pre>{@code
     * PropertyListing listing = ...;
     *
     * ItemListingPageSizeSelector<PropertyBox, Property<?>> selector =
     *         Components.pageSizeSelector(listing)
     *             .withOptions(10, 25, 50, 100)
     *             .withDefaultSize(25)
     *             .build();
     * }</pre>
     *
     * @param listing the property listing to control (not null)
     * @return a new {@link ItemListingPageSizeSelector.Builder}
     * @since 10.0.1
     */
    static ItemListingPageSizeSelector.Builder<PropertyBox, Property<?>> pageSizeSelector(PropertyListing listing) {
        return ItemListingPageSizeSelector.of(listing);
    }

    // -----------------------------------------------------------------------
    // Pagination bar
    // -----------------------------------------------------------------------

    /**
     * Creates a pagination bar bound to the given {@link ItemListing}.
     *
     * <p>The bar renders <em>Previous / numbered pages / Next</em> navigation and
     * uses a look-ahead fetch to detect the last page without issuing a
     * {@code COUNT(*)} query.</p>
     *
     * <pre>{@code
     * BeanListing<Person> listing = ...;
     * ItemListingPaginationBar<Person, String> bar = Components.paginationBar(listing);
     * layout.content(listing.getComponent(), bar);
     * }</pre>
     *
     * @param listing the listing to paginate (not null)
     * @param <T>     item type
     * @param <P>     property type
     * @return a new {@link ItemListingPaginationBar}
     * @since 10.0.1
     */
    static <T, P> ItemListingPaginationBar<T, P> paginationBar(ItemListing<T, P> listing) {
        return new ItemListingPaginationBar<>(listing);
    }

    /**
     * Creates a pagination bar bound to the given {@link PropertyListing}.
     *
     * <p>Convenience overload that avoids verbose wildcard generics at call sites
     * when working with {@link PropertyListing}.</p>
     *
     * <pre>{@code
     * PropertyListing listing = ...;
     * ItemListingPaginationBar<PropertyBox, Property<?>> bar = Components.paginationBar(listing);
     * layout.content(listing.getComponent(), bar);
     * }</pre>
     *
     * @param listing the property listing to paginate (not null)
     * @return a new {@link ItemListingPaginationBar}
     * @since 10.0.1
     */
    static ItemListingPaginationBar<PropertyBox, Property<?>> paginationBar(PropertyListing listing) {
        return new ItemListingPaginationBar<>(listing);
    }

    // -----------------------------------------------------------------------
    // Listing bundle builder
    // -----------------------------------------------------------------------

    /**
     * Creates a fluent {@link ListingBundleBuilder} that assembles a fully pre-wired
     * {@link ListingBundle} &mdash; listing, pagination bar, page-size selector, optional
     * search field, and optional filter panel &mdash; in a single chained call.
     *
     * <p>{@link ListingBundle} extends {@link com.vaadin.flow.component.Composite Composite&lt;Div&gt;} and
     * self-assembles toolbar, grid, and footer in its constructor &mdash; just add the bundle
     * directly to your view layout:</p>
     *
     * <pre>{@code
     * var bundle = Components.listing(Product.class)
     *     .columns("id", "name", "category", "price")
     *     .pageSizes(10, 25, 50)
     *     .search("Search products…")
     *     .fetch((q, text) -> service.fetch(q, text))
     *     .build();
     *
     * add(bundle);  // toolbar + grid + footer are already assembled inside the bundle
     *
     * // Only call toolbar()/grid()/footer() individually when you need to place the
     * // three pieces in different layout slots (e.g. toolbar in an AppLayout header):
     * // add(bundle.toolbar());
     * // setContent(bundle.grid());
     * }</pre>
     *
     * @param beanType the bean class to introspect for columns (not null)
     * @param <T>      item type
     * @return a new {@link ListingBundleBuilder}
     * @since 10.0.1
     */
    static <T> ListingBundleBuilder<T> listing(Class<T> beanType) {
        return new DefaultListingBundleBuilder<>(beanType);
    }

    /**
     * Creates a fluent {@link PropertyListingBundleBuilder} from an explicit set of
     * Holon {@link Property} objects.
     *
     * <pre>{@code
     * var bundle = Components.listing(NAME, CATEGORY, PRICE, STATUS)
     *     .header(NAME,  "Product Name")
     *     .header(PRICE, "Price (€)")
     *     .pageSizes(10, 25, 50)
     *     .search("Search products…")
     *     .fetch((q, text) -> service.fetch(q, text))
     *     .build();
     *
     * add(bundle);  // toolbar + grid + footer are already assembled inside the bundle
     * }</pre>
     *
     * @param properties the properties to display as listing columns (not null)
     * @return a new {@link PropertyListingBundleBuilder}
     * @since 10.0.1
     */
    static PropertyListingBundleBuilder listing(Property<?>... properties) {
        return new PropertyListingBundleBuilder(properties);
    }

    /**
     * Creates a fluent {@link PropertyListingBundleBuilder} from a Holon {@link PropertySet}.
     *
     * <pre>{@code
     * var bundle = Components.listing(PRODUCT_SET)
     *     .pageSizes(10, 25, 50)
     *     .withFilterPanel()
     *     .fetch((q, text, filter) -> {
     *         var q2 = datastore.query(TARGET).restrict(q.getLimit(), q.getOffset());
     *         if (filter != null) q2.filter(filter);
     *         return q2.stream(PRODUCT_SET);
     *     })
     *     .build();
     * }</pre>
     *
     * @param propertySet the property set to display (not null)
     * @return a new {@link PropertyListingBundleBuilder}
     * @since 10.0.1
     */
    static PropertyListingBundleBuilder listing(PropertySet<?> propertySet) {
        return new PropertyListingBundleBuilder(propertySet);
    }

    // -----------------------------------------------------------------------
    // Highlight KPI card
    // -----------------------------------------------------------------------

    /**
     * Creates a fluent {@link HighlightBuilder} for a KPI / metric card with the
     * given heading label and value text.
     *
     * <pre>{@code
     * Highlight card = Components.highlight("Total Revenue", "$128,430")
     *     .valueFirst()
     *     .accentColor(Highlight.AccentColor.PURPLE)
     *     .valueFontSize(Font.Size.XXLARGE)
     *     .details(trendSpan)
     *     .suffix(IconBadge.of(VaadinIcon.DOLLAR, Alert.Variant.INFO))
     *     .ariaLabel("Total Revenue KPI card")
     *     .build();
     * }</pre>
     *
     * @param heading the heading label text (not null)
     * @param value   the value text (not null)
     * @return a new {@link HighlightBuilder}
     * @since 10.0.0
     */
    static HighlightBuilder highlight(
            String heading, String value) {
        return HighlightBuilder.create(heading, value);
    }

    /**
     * Creates a fluent {@link HighlightBuilder} for a prefix + heading + value card.
     *
     * <pre>{@code
     * Highlight card = Components.highlight(
     *         IconBadge.of(VaadinIcon.USER, Alert.Variant.INFO),
     *         "Active Users", "4,291")
     *     .details(trendSpan)
     *     .build();
     * }</pre>
     *
     * @param prefix  the prefix component (left icon / avatar slot)
     * @param heading the heading label text (not null)
     * @param value   the value text (not null)
     * @return a new {@link HighlightBuilder}
     * @since 10.0.0
     */
    static HighlightBuilder highlight(
            Component prefix, String heading, String value) {
        return HighlightBuilder.create(prefix, heading, value);
    }

    // -----------------------------------------------------------------------
    // EntityFormPanel
    // -----------------------------------------------------------------------

    /**
     * Creates an {@link EntityFormPanel.BeanBuilder} for a form panel driven by the
     * given bean class.
     *
     * <p>The form panel wraps a {@link BeanPropertyInputForm} and provides a standard
     * button footer with mandatory <em>Save</em> and <em>Clear</em> buttons plus
     * optional <em>Save &amp; New</em> and <em>Cancel</em> buttons.
     * ENTER-key navigation between inputs is enabled by default.
     *
     * <pre>{@code
     * EntityFormPanel<Customer> panel = Components.<Customer>entityFormPanel(Customer.class)
     *     .configure(fb -> fb.excludeFields("id"))
     *     .saveButton(btn -> btn.primary().withText("Save"),
     *                 customer -> service.save(customer))
     *     .clearButton(btn -> btn.tertiary().withText("Reset"))
     *     .cancelButton(btn -> btn.tertiary().withText("Cancel"),
     *                   () -> dialog.close())
     *     .build();
     * }</pre>
     *
     * @param <T>       bean type
     * @param beanClass the bean class to introspect (not null)
     * @return a new {@link EntityFormPanel.BeanBuilder}
     * @since 10.0.0
     */
    static <T> EntityFormPanel.BeanBuilder<T> entityFormPanel(Class<T> beanClass) {
        return EntityFormPanel.bean(beanClass);
    }

    /**
     * Creates an {@link EntityFormPanel.BeanBuilder} for a bean-driven form panel
     * using the given layout mode.
     *
     * @param <T>        bean type
     * @param beanClass  the bean class to introspect (not null)
     * @param layoutMode the layout mode to use (not null)
     * @return a new {@link EntityFormPanel.BeanBuilder}
     * @since 10.0.0
     */
    static <T> EntityFormPanel.BeanBuilder<T> entityFormPanel(Class<T> beanClass, EntityFormPanel.LayoutMode layoutMode) {
        return EntityFormPanel.bean(beanClass).layout(layoutMode);
    }

    /**
     * Creates an {@link EntityFormPanel.DivBeanBuilder} for a bean-driven form
     * panel using a {@link com.vaadin.flow.component.html.Div} grid container.
     *
     * @param <T>       bean type
     * @param beanClass the bean class to introspect (not null)
     * @return a new {@link EntityFormPanel.DivBeanBuilder}
     * @since 10.0.0
     */
    static <T> EntityFormPanel.DivBeanBuilder<T> entityFormPanelDiv(Class<T> beanClass) {
        return EntityFormPanel.beanDiv(beanClass);
    }

    /**
     * Creates an {@link EntityFormPanel.PropertyBuilder} for a form panel driven by
     * the given {@link PropertySet}.
     *
     * <pre>{@code
     * EntityFormPanel<PropertyBox> panel = Components.entityFormPanel(CUSTOMER_SET)
     *     .saveButton(btn -> btn.primary().withText("Save"),
     *                 pb -> service.save(pb))
     *     .clearButton(btn -> btn.tertiary().withText("Reset"))
     *     .build();
     * }</pre>
     *
     * @param propertySet the property set that defines the form fields (not null)
     * @return a new {@link EntityFormPanel.PropertyBuilder}
     * @since 10.0.0
     */
    static EntityFormPanel.PropertyBuilder entityFormPanel(PropertySet<?> propertySet) {
        return EntityFormPanel.properties(propertySet);
    }

    /**
     * Creates an {@link EntityFormPanel.PropertyBuilder} for a property-set driven
     * form panel using the given layout mode.
     *
     * @param propertySet the property set that defines the form fields (not null)
     * @param layoutMode  the layout mode to use (not null)
     * @return a new {@link EntityFormPanel.PropertyBuilder}
     * @since 10.0.0
     */
    static EntityFormPanel.PropertyBuilder entityFormPanel(PropertySet<?> propertySet, EntityFormPanel.LayoutMode layoutMode) {
        return EntityFormPanel.properties(propertySet).layout(layoutMode);
    }

    /**
     * Creates an {@link EntityFormPanel.DivPropertyBuilder} for a property-set
     * driven form panel using a {@link com.vaadin.flow.component.html.Div} grid container.
     *
     * @param propertySet the property set that defines the form fields (not null)
     * @return a new {@link EntityFormPanel.DivPropertyBuilder}
     * @since 10.0.0
     */
    static EntityFormPanel.DivPropertyBuilder entityFormPanelDiv(PropertySet<?> propertySet) {
        return EntityFormPanel.propertiesDiv(propertySet);
    }

    /**
     * Creates an {@link EntityFormPanel.PropertyBuilder} for a form panel driven by
     * the given properties (varargs).
     *
     * <pre>{@code
     * EntityFormPanel<PropertyBox> panel = Components.entityFormPanel(NAME, EMAIL, PHONE)
     *     .saveButton(btn -> btn.primary().withText("Save"),
     *                 pb -> service.save(pb))
     *     .clearButton(btn -> btn.tertiary().withText("Reset"))
     *     .build();
     * }</pre>
     *
     * @param properties the properties that define the form fields (not null)
     * @return a new {@link EntityFormPanel.PropertyBuilder}
     * @since 10.0.0
     */
    static EntityFormPanel.PropertyBuilder entityFormPanel(Property<?>... properties) {
        return EntityFormPanel.properties(properties);
    }

    /**
     * Creates an {@link EntityFormPanel.PropertyBuilder} for a property-list driven
     * form panel using the given layout mode.
     *
     * @param layoutMode the layout mode to use (not null)
     * @param properties the properties that define the form fields (not null)
     * @return a new {@link EntityFormPanel.PropertyBuilder}
     * @since 10.0.0
     */
    static EntityFormPanel.PropertyBuilder entityFormPanel(EntityFormPanel.LayoutMode layoutMode, Property<?>... properties) {
        return EntityFormPanel.properties(properties).layout(layoutMode);
    }

    /**
     * Creates an {@link EntityFormPanel.DivPropertyBuilder} for a property-list
     * driven form panel using a {@link com.vaadin.flow.component.html.Div} grid container.
     *
     * @param properties the properties that define the form fields (not null)
     * @return a new {@link EntityFormPanel.DivPropertyBuilder}
     * @since 10.0.0
     */
    static EntityFormPanel.DivPropertyBuilder entityFormPanelDiv(Property<?>... properties) {
        return EntityFormPanel.propertiesDiv(properties);
    }

    // -----------------------------------------------------------------------
    // KeyValueList
    // -----------------------------------------------------------------------

    /**
     * Creates an empty {@link KeyValueList}.
     *
     * <p>Items can be added fluently:
     * <pre>{@code
     * Components.keyValueList()
     *     .addItem(KeyValueItem.of("Name",  "Jane Smith"))
     *     .addItem(KeyValueItem.of("Email", "jane@example.com"));
     * }</pre>
     *
     * @return a new empty {@link KeyValueList}
     * @since 10.0.0
     */
    static KeyValueList keyValueList() {
        return new KeyValueList();
    }

    /**
     * Creates a {@link KeyValueList} pre-populated with the given items.
     *
     * <pre>{@code
     * Components.keyValueList(
     *     KeyValueItem.of("Name",  "Jane Smith"),
     *     KeyValueItem.of("Email", "jane@example.com")
     * );
     * }</pre>
     *
     * @param items the {@link KeyValueItem} rows to content (not null)
     * @return a new {@link KeyValueList} containing the provided items
     * @since 10.0.0
     */
    static KeyValueList keyValueList(KeyValueItem... items) {
        var list = new KeyValueList();
        for (KeyValueItem item : items) {
            list.addItem(item);
        }
        return list;
    }

    // -----------------------------------------------------------------------
    // LineItemGrid
    // -----------------------------------------------------------------------

    /**
     * Creates a new {@link com.holonplatform.vaadin.flow.vaadinplus.components.LineItemGrid.Builder}
     * for a keyboard-centric inline document line-item spreadsheet.
     *
     * <p>All rows are rendered without virtual scrolling so the browser's native Tab order
     * covers every cell. {@code Enter} moves to the same column in the next row (Excel
     * behaviour). On mobile viewports the table switches automatically to a card-list view
     * with a {@link com.holonplatform.vaadin.flow.vaadinplus.components.Sheet} edit panel.</p>
     *
     * <pre>{@code
     * LineItemGrid grid = Components.lineItemGrid()
     *     .title("Invoice Lines")
     *     .withItemSuggestion("Laptop", "SKU-001", 1299.00)
     *     .withTaxOption("GST 10%", 0.10)
     *     .withInitialRows(2)
     *     .build();
     * }</pre>
     *
     * @return a new {@link com.holonplatform.vaadin.flow.vaadinplus.components.LineItemGrid.Builder}
     * @since 10.0.0
     */
    static LineItemGrid.Builder lineItemGrid() {
        return LineItemGrid.builder();
    }

    static DivBuilder div() {
        return DivBuilder.create();
    }

    static MasterBuilder master() {
        return MasterBuilder.create();
    }

    static DetailBuilder detail() {
        return DetailBuilder.create();
    }

    // -----------------------------------------------------------------------
    // StatusBadge
    // -----------------------------------------------------------------------

    /**
     * Creates a neutral (gray) {@link StatusBadge} — a non-interactive dot-prefix status pill.
     *
     * <pre>{@code
     * Components.statusBadge("ASN: NW-2281")
     * }</pre>
     *
     * @param label badge label text (not null)
     * @return a new {@link StatusBadge} with {@link StatusBadge.Variant#DEFAULT}
     * @since 10.0.0
     */
    static StatusBadge statusBadge(String label) {
        return StatusBadge.of(label);
    }

    /**
     * Creates a {@link StatusBadge} with a semantic colour variant.
     *
     * <pre>{@code
     * Components.statusBadge("Posted",          StatusBadge.Variant.SUCCESS)
     * Components.statusBadge("Partial receipt", StatusBadge.Variant.WARNING)
     * Components.statusBadge("In progress",     StatusBadge.Variant.INFO)
     * Components.statusBadge("Quality check",   StatusBadge.Variant.VIOLET)
     * }</pre>
     *
     * @param label   badge label text (not null)
     * @param variant semantic colour variant
     * @return a new {@link StatusBadge}
     * @since 10.0.0
     */
    static StatusBadge statusBadge(String label, StatusBadge.Variant variant) {
        return StatusBadge.of(label, variant);
    }

    // -----------------------------------------------------------------------
    // ArAgingBar
    // -----------------------------------------------------------------------

    /**
     * Returns a fluent builder for {@link ArAgingBar} — a proportional segmented bar card
     * for visualising distributions such as AR aging buckets, pipeline stages, budget
     * breakdowns, or recruitment funnels.
     *
     * <p>The builder uses nested sub-builders with {@code Consumer<>} overloads:</p>
     *
     * <pre>{@code
     * Components.arAgingBar()
     *     .header(h -> h
     *         .icon("€")
     *         .title("AR aging · Helix Robotics")
     *         .variant(ArAgingBar.Variant.INFO))
     *     .content(c -> c
     *         .segment(s -> s.key("Current").value("Current · €35K").percent(56).variant(Variant.SUCCESS))
     *         .segment(s -> s.key("1–30d")  .value("1-30d · €18.7K").percent(30).variant(Variant.INFO))
     *         .segment(s -> s.key("31–60d") .value("31-60d · €8.7K").percent(14).variant(Variant.WARNING)))
     *     .footer(f -> f
     *         .left("€0 owed").center("€0 overdue").right("€62.4K total open"))
     *     .build();
     * }</pre>
     *
     * @return a new {@link ArAgingBarBuilder}
     */
    static com.holonplatform.vaadin.flow.components.builders.ArAgingBarBuilder arAgingBar() {
        return com.holonplatform.vaadin.flow.components.builders.ArAgingBarBuilder.create();
    }

    // -----------------------------------------------------------------------
    // HeroStrip
    // -----------------------------------------------------------------------

    /**
     * Returns a fluent builder for {@link HeroStrip} — a gradient card showing an optional
     * thumbnail/name/meta header row, an optional row of status tag pills, and N key
     * performance indicators in equally-wide columns.
     *
     * <pre>{@code
     * Components.heroStrip()
     *     .variant(HeroStrip.Variant.INFO)
     *     .header(h -> h.thumbIcon(VaadinIcon.BUILDING.create())
     *         .ribbon("T1")
     *         .name("Helix Robotics SE")
     *         .starred(true)
     *         .meta("C-2026-0023 · Munich · since 1.9 yr"))
     *     .tag("● Active", HeroStrip.TagVariant.OK)
     *     .tag("★ T1", HeroStrip.TagVariant.PRI)
     *     .tag("EMEA · DACH", HeroStrip.TagVariant.PRIM)
     *     .cell(c -> c.header("Open pipeline").content("€182K").footer("4 active deals").pulse(true))
     *     .cell(c -> c.header("Booked YTD").content("€624K").footer("14 orders").valueVariant(HeroStrip.ValueVariant.OK))
     *     .cell(c -> c.header("AR balance").content("€62,400").footer("3 open · all on-time"))
     *     .build();
     * }</pre>
     *
     * @return a new {@link HeroStripBuilder}
     */
    static com.holonplatform.vaadin.flow.components.builders.HeroStripBuilder heroStrip() {
        return com.holonplatform.vaadin.flow.components.builders.HeroStripBuilder.create();
    }

    // -----------------------------------------------------------------------
    // Chip / ChipGroup
    // -----------------------------------------------------------------------

    /**
     * Creates a label-only {@link Chip} — an interactive pill-shaped filter chip.
     *
     * <pre>{@code
     * Components.chip("All").active(true)
     * }</pre>
     *
     * @param label chip label text (not null)
     * @return a new {@link Chip}
     * @since 10.0.0
     */
    static Chip chip(String label) {
        return Chip.of(label);
    }

    /**
     * Creates a {@link Chip} with a trailing numeric count badge.
     *
     * <pre>{@code
     * Components.chip("All", 2418).active(true)
     * Components.chip("Open", 14)
     * }</pre>
     *
     * @param label chip label text (not null)
     * @param count count badge value
     * @return a new {@link Chip}
     * @since 10.0.0
     */
    static Chip chip(String label, long count) {
        return Chip.of(label, count);
    }

    /**
     * Creates a new empty {@link ChipGroup} — a mutually exclusive toggle group of {@link Chip}s.
     *
     * <pre>{@code
     * Components.chipGroup()
     *     .addChip(Components.chip("All",       2418), true)
     *     .addChip(Components.chip("Open",        14))
     *     .addChip(Components.chip("Posted",    2376))
     *     .addChip(Components.chip("QC Issues",    3))
     *     .onSelect(e -> applyFilter(e.getLabel()))
     *     .build();
     * }</pre>
     *
     * @return a new {@link ChipGroup}
     * @since 10.0.0
     */
    static ChipGroup chipGroup() {
        return ChipGroup.create();
    }

    // -----------------------------------------------------------------------
    // AppShellLayout
    // -----------------------------------------------------------------------

    /**
     * Creates a new {@link com.iyensoft.vaadin.flow.components.builders.AppShellLayoutBuilder}
     * for the standard enterprise application shell.
     *
     * <p>The builder assembles an {@link com.holonplatform.vaadin.flow.vaadinplus.components.AppBar}
     * in the navbar with an optional
     * {@link com.vaadin.flow.component.applayout.DrawerToggle}, brand, search,
     * notification bell, language selector, dark/light theme toggle, and user avatar —
     * plus an optional drawer header and navigation wrapper.
     *
     * <h4>Option A — standalone (demo / prototype)</h4>
     * <pre>{@code
     * AppShellLayout shell = Components.appShell()
     *     .navbarBrand("My App", "v1.0", HomeView.class)
     *     .search("Search…")
     *     .notifications(3, "Deployment done", "New message")
     *     .languages("English", "Deutsch", "Francais")
     *     .themeToggle()
     *     .user(u -> u
     *         .name("Jane Smith")
     *         .avatar("/avatars/jane.png")
     *         .menu(m -> m
     *             .item("Profile")
     *             .item("Sign out")))
     *     .drawerBrand(logoComponent)
     *     .nav(Components.sideNav()...buildWrapper())
     *     .build();
     * }</pre>
     *
     * <h4>Option B — per-user with Spring injection (production)</h4>
     * <pre>{@code
     * @SpringComponent @UIScope
     * public class MyAppLayout extends AppLayout {
     *     @Autowired
     *     public MyAppLayout(SecurityService sec, NotificationService notifs) {
     *         var me = sec.currentUser();
     *         Components.appShell()
     *             .navbarBrand(me.getCompanyName(), HomeView.class)
     *             .notifications(notifs.countUnread(me),
     *                            notifs.topItems(me, 5).toArray(String[]::new))
     *             .themeToggle()
     *             .user(u -> u
     *                 .name(me.getFullName())
     *                 .avatar(me.getAvatarUrl())
     *             .menu(m -> m
     *                 .item("Profile")
     *                 .item("Sign out")))
     *             .nav(buildNav(me.getRoles()))
     *             .configure(this);   // applies to THIS AppLayout instance
     *     }
     * }
     * }</pre>
     *
     * @return a new {@link com.iyensoft.vaadin.flow.components.builders.AppShellLayoutBuilder}
     * @since 10.0.0
     */
    static AppShellLayoutBuilder appShell() {
        return AppShellLayoutBuilder.create();
    }

    // ── VaadinPlus custom components ──────────────────────────────────────

    /**
     * Creates a new {@link LivePreviewCard} with the given eyebrow label.
     *
     * <p>A dark-gradient summary card for displaying a live preview of structured form data.
     * Typically used as a reactive sidebar alongside a multi-step form.</p>
     *
     * @param eyebrow small uppercase caption shown above the title (e.g. {@code "Live preview"})
     * @return a new {@link LivePreviewCard}
     */
    static LivePreviewCard livePreviewCard(String eyebrow) {
        return new LivePreviewCard(eyebrow);
    }

    /**
     * Creates an empty {@link ChecklistPanel}.
     *
     * <p>A vertical checklist showing ordered steps with DONE / CURRENT / PENDING visual states.
     * Add items via {@link ChecklistPanel#addItem} and advance via {@link ChecklistPanel#advance()}.</p>
     *
     * @return a new empty {@link ChecklistPanel}
     */
    static ChecklistPanel checklistPanel() {
        return new ChecklistPanel();
    }

    /**
     * Creates a new {@link AssignmentPickerRow} with the given name and description.
     *
     * <p>A clickable row displaying an avatar (with auto-derived initials), a name, a role/description,
     * and an optional "Change" action link.</p>
     *
     * @param name        display name of the assigned person (initials derived automatically)
     * @param description secondary description, e.g. role or department (may be {@code null})
     * @return a new {@link AssignmentPickerRow}
     */
    static AssignmentPickerRow assignmentPickerRow(String name, String description) {
        return new AssignmentPickerRow(name, description);
    }

    /**
     * Creates an empty {@link StickyActionBar}.
     *
     * <p>A generic fixed bottom action bar with an optional status indicator, an optional progress
     * display, and action buttons. Automatically respects the Vaadin AppLayout drawer width so it
     * never overlaps a {@code SideNav} sidebar.</p>
     *
     * <p>Configure with {@link StickyActionBar#setStatus}, {@link StickyActionBar#setProgress},
     * and {@link StickyActionBar#addAction}.</p>
     *
     * @return a new empty {@link StickyActionBar}
     */
    static StickyActionBar stickyActionBar() {
        return new StickyActionBar();
    }

    /**
     * Gets a builder to create a {@link FormStepCard}.
     *
     * <p>A Panel-based card representing one step in a multi-step form.
     * The builder composes a step badge, heading, subtitle, "STEP N OF M" counter,
     * form content, and optional help-text footer into a fully structured panel.</p>
     *
     * <pre>{@code
     * FormStepCard card = Components.formStepCard()
     *     .stepNumber(2)
     *     .totalSteps(6)
     *     .title("Primary contact")
     *     .subtitle("Name, email, phone")
     *     .state(FormStepCard.StepState.CURRENT)
     *     .helpText("At least one contact is required before saving.")
     *     .content(nameField, emailField)
     *     .build();
     * }</pre>
     *
     * @return a new {@link FormStepCardBuilder}
     */
    static com.holonplatform.vaadin.flow.components.builders.FormStepCardBuilder formStepCard() {
        return com.holonplatform.vaadin.flow.components.builders.FormStepCardBuilder.create();
    }

    /**
     * Creates a new {@link EntityCreationFormBuilder} for building a generic multi-step
     * entity creation form.
     *
     * <p>A full-page layout that composes a page header (breadcrumb + title + draft badge +
     * actions), a vertically stacked list of {@link FormStepCard} steps, and a
     * {@link StickyActionBar} at the bottom.  The side column (LivePreviewCard, ChecklistPanel)
     * is intentionally absent — it is a single-column, focused creation flow.</p>
     *
     * <pre>{@code
     * EntityCreationForm form = Components.entityCreationForm()
     *     .breadcrumb(
     *         new BreadcrumbItem(new Span("Products")),
     *         new BreadcrumbPage("New product")
     *     )
     *     .title("New product")
     *     .subtitle("All required fields are marked with *")
     *     .draftBadge("Unsaved draft")
     *     .headerAction(Components.button().text("Discard").build())
     *     .headerAction(Components.button().text("Save product").primary().build())
     *     .step(detailsStep)
     *     .step(pricingStep)
     *     .status("Auto-saved · 2 sec ago", StickyActionBar.Variant.SUCCESS)
     *     .progress("Setup", 50)
     *     .barAction(Components.button().text("Discard").build())
     *     .barAction(Components.button().text("Save product").primary().build())
     *     .build();
     * }</pre>
     *
     * @return a new {@link EntityCreationFormBuilder}
     */
    static com.holonplatform.vaadin.flow.components.builders.EntityCreationFormBuilder entityCreationForm() {
        return com.holonplatform.vaadin.flow.components.builders.EntityCreationFormBuilder.create();
    }

}
