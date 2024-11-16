package com.holonplatform.vaadin.flow.components.utils;

import com.github.javaparser.quality.NotNull;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.vaadin.flow.HasOptionsBar;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.PropertyInputForm;
import com.holonplatform.vaadin.flow.components.Selectable;
import com.holonplatform.vaadin.flow.components.builders.BooleanInputBuilder;
import com.holonplatform.vaadin.flow.components.builders.BulkActionBuilder;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.components.builders.SearchBarBuilder;
import com.holonplatform.vaadin.flow.components.css.WhiteSpace;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridLazyDataView;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.DataProviderListener;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.selection.MultiSelectionEvent;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import elemental.json.JsonValue;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.holonplatform.vaadin.flow.internal.components.support.BreakPoint.*;
import static com.vaadin.flow.component.button.ButtonVariant.LUMO_TERTIARY_INLINE;

public class UIUtils {

    public static final String IMG_PATH = "images/";
    public static final String MIN_DESKTOP_WIDTH = "700px";
    public static final String MARGIN_RIGHT = "margin-right";
    public static final String AUTO = "auto";
    public static final int MIN_DESKTOP_COLUMNS = 4;
    public static final FormLayout.ResponsiveStep.LabelsPosition DEFAULT_POS = FormLayout.ResponsiveStep.LabelsPosition.TOP;
    public static final int DEFAULT_COL_SPAN = 2;
    public static final String LEFT_SMALL_PADDING = LumoUtility.Padding.Horizontal.SMALL;
    public static final String RIGHT_SMALL_PADDING = LumoUtility.Padding.Right.SMALL;
    public static final String CARD = "card";
    public static final String CONTAINER = "container";

    public static final String DISCARD_MESSAGE = "There are unsaved modifications to the %s. Discard changes?";
    public static final String DELETE_MESSAGE = "Are you sure you want to delete the selected %s? This action cannot be undone.";

    public static final String CUSTOM_COMBOBOX_ITEMS = """
            <div style="display: flex;">
            <vaadin-avatar style="height: var(--lumo-size-m); margin-right: var(--lumo-space-s);"
            name= ${item.primaryName}>
            </vaadin-avatar>
            <div>
            ${item.primaryName}
            <div style="font-size: var(--lumo-font-size-s); color: var(--lumo-secondary-text-color);">
            ${item.secondaryName}
            </div>
            </div>
            """;

    private static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    /****************************************/
//    https://github.com/fredpena/vaadin-i18n/blob/main/src/main/java/dev/fredpena/app/views/MainLayout.java
    public static MenuItemComponent createIconItem(MenuBar menu, VaadinIcon iconName, String label, String ariaLabel) {
        return createIconItem(menu, new Icon(iconName), label, ariaLabel, false);
    }

    public static MenuItemComponent createIconItem(MenuBar menu, LumoIcon iconName, String label, String ariaLabel) {
        return createIconItem(menu, iconName.create(), label, ariaLabel, false);
    }


    public static H2 bigTitle(String text) {
        final var day = new H2(text);
        day.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.Border.BOTTOM,
                LumoUtility.BorderColor.CONTRAST_10,
                LumoUtility.FontSize.XXLARGE,
//                Layout.TOP_0,
                LumoUtility.Margin.Bottom.NONE,
                LumoUtility.Margin.Horizontal.AUTO,
                LumoUtility.Margin.Top.MEDIUM,
                LumoUtility.MaxWidth.SCREEN_SMALL,
                LumoUtility.Padding.SMALL,
                LumoUtility.Position.STICKY
//                Layout.Z_10
        );
        return day;
    }

    private static MenuItemComponent createIconItem(HasMenuItems menu, String iconName, String label) {

        return createIconItem(menu, createIcon(iconName), label, null, true);
    }

    public static MenuItemComponent createIconItem(HasMenuItems menu, Component component, String label, String ariaLabel, boolean isChild) {
        if (isChild) {
            component.getStyle().set("margin-right", "var(--lumo-space-m)");
        }

        MenuItem item = menu.addItem(component, e -> {
        });

        if (ariaLabel != null) {
            item.setAriaLabel(ariaLabel);
        }

        Text text = new Text(label);
        if (label != null) {
            item.add(text);
        }

        return new MenuItemComponent(item, text);
    }

    public static Button addNewItemButton() {
        return Components.button()
                .icon(VaadinIcon.PLUS)
                .iconAfterText(false)
                .text("New", "new.code")
                .withThemeVariants(ButtonVariant.LUMO_PRIMARY)
                .withFocusShortcutKey(Key.KEY_N, KeyModifier.CONTROL)
                .tooltip("Add New", "add.new.code")
                .build();
    }

    public static com.holonplatform.vaadin.flow.components.Input<String> createSearchField() {
        return Components.input.string()
                .clearButtonVisible(true)
                .blankValuesAsNull(true)
                .emptyValuesAsNull(true)
                .placeholder("Search", "search.code")
                .prefixComponent(VaadinIcon.SEARCH.create())
                .withFocusShortcutKey(Key.KEY_F, KeyModifier.CONTROL)
                .build();
    }

    private static Component createIcon(String name) {
        Image image = new Image("images/%s.png".formatted(name), "");
        image.setMaxWidth("25px");
        return image;
    }

    public static String[] getTitleStyles() {
        return new String[]{LumoUtility.TextColor.PRIMARY, LumoUtility.Padding.SMALL,
                LumoUtility.Border.BOTTOM,
                LumoUtility.BorderColor.CONTRAST_30};
    }

    public static Component[] toComponents(HasComponent[] components) {
        return Arrays.asList(components).stream().map(c -> c.getComponent()).collect(Collectors.toList())
                .toArray(new Component[0]);
    }

    public static Component createTextFieldFilterHeader(String labelText,
                                                        Consumer<String> filterChangeConsumer) {
        NativeLabel label = new NativeLabel(labelText);
        label.getStyle().set("padding-top", "var(--lumo-space-m)")
                .set("font-size", "var(--lumo-font-size-xs)");

        TextField textField = textFieldFilter(filterChangeConsumer);
        VerticalLayout layout = new VerticalLayout(label, textField);
        layout.getThemeList().clear();
        layout.getThemeList().add("spacing-xs");

        return layout;
    }

    private static TextField textFieldFilter(Consumer<String> filterChangeConsumer) {
        TextField textField = new TextField();
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.setClearButtonVisible(true);
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        textField.setWidthFull();
        textField.getStyle().set("max-width", "100%");
        textField.addValueChangeListener(
                e -> filterChangeConsumer.accept(e.getValue()));

        return textField;
    }

    public static LabelBuilder<H4> createH4(String title) {
        return LabelBuilder.h4()
                .text(title)
//                .fullWidth()
                .title(title)
                .styleNames(titleStyles());
    }

    public static String[] titleStyles() {
        return new String[]{LumoUtility.TextColor.PRIMARY, LumoUtility.Padding.SMALL};
    }

    public static String[] borderStyles() {
        return new String[]{LumoUtility.Border.BOTTOM, LumoUtility.BorderColor.CONTRAST_30};
    }

    public static Span createDaySpan() {
        final var daySpan = new Span();
        daySpan.addClassNames(LumoUtility.Display.FLEX, LumoUtility.JustifyContent.CENTER, LumoUtility.Padding.MEDIUM, LumoUtility.Background.BASE);
        daySpan.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.SMALL, LumoUtility.Border.BOTTOM, LumoUtility.BorderColor.CONTRAST_10); //, "sticky-date"
        return daySpan;
    }

    public static Button createChangeLanguageButton(AttachEvent attachEvent) {
        var changeLanguage = new Button();
        changeLanguage.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        updateChangeLanguageButtonIcon(attachEvent.getUI(), changeLanguage);
        Locale locale = attachEvent.getUI().getLocale();
        changeLanguage.addClickListener(e -> {
            VaadinService.getCurrentResponse().addCookie(new Cookie("locale", locale.toLanguageTag()));
            updateChangeLanguageButtonIcon(attachEvent.getUI(), changeLanguage);
            attachEvent.getUI().getPage().reload();
        });
        return changeLanguage;
    }

    public static void updateChangeLanguageButtonIcon(UI ui, Button changeLanguage) {
        Image image;
        if (ui.getLocale() != null) { //fiLocale.equals(ui.getLocale())
            // TODO: Translation
            image = new Image("icons/finnish.png", "Finnish");
            changeLanguage.setAriaLabel("Switch to English");
        } else {
            // TODO: Translation
            image = new Image("icons/english.png", "English");
            changeLanguage.setAriaLabel("Switch to Finnish");
        }
        image.setHeightFull();

        // Wrapper; circle
        Span icon = new Span(image);
        icon.addClassNames(
                LumoUtility.AlignItems.CENTER,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Display.FLEX,
                LumoUtility.IconSize.MEDIUM,
                LumoUtility.JustifyContent.CENTER,
                LumoUtility.Overflow.HIDDEN
        );
        changeLanguage.setIcon(icon);
    }

    public static void addChildIfNotExists(VerticalLayout parent, Component child) {
        if (parent.getChildren().noneMatch(Predicate.isEqual(child))) {
            parent.add(child);
        }
    }

    public static void addChildIfNotExists(HorizontalLayout parent, Component child) {
        if (parent.getChildren().noneMatch(Predicate.isEqual(child))) {
            parent.add(child);
        }
    }

    public static void addChildIfNotExists(Div parent, Component child) {
        // Check if the child already exists in the parent
        if (parent.getChildren().noneMatch(Predicate.isEqual(child))) {
            parent.add(child);// Add the child if it doesn't exist
        }
    }

    public static void addChildIfNotExists(FlexLayout parent, Component child) {
        if (parent.getChildren().noneMatch(Predicate.isEqual(child))) {
            parent.add(child);
        }
    }

    public static String formatSize(int size) {
        return String.format("%dpx", size);
    }

    private record MenuItemComponent(MenuItem menuItem, Text text) {

    }



    /****************************************/

    public static final Map<Integer, FormLayout.ResponsiveStep> FIXED_COLUMNS_EXTRA_SMALL = Map.of(
            1, new FormLayout.ResponsiveStep(BREAKPOINT_XS.getSize(), 1),
            2, new FormLayout.ResponsiveStep(BREAKPOINT_XS.getSize(), 2),
            3, new FormLayout.ResponsiveStep(BREAKPOINT_XS.getSize(), 3),
            4, new FormLayout.ResponsiveStep(BREAKPOINT_XS.getSize(), 4),
            5, new FormLayout.ResponsiveStep(BREAKPOINT_XS.getSize(), 5)
    );

    public static final Map<Integer, FormLayout.ResponsiveStep> FIXED_COLUMNS_SMALL = Map.of(
            1, new FormLayout.ResponsiveStep(BREAKPOINT_SM.getSize(), 1),
            2, new FormLayout.ResponsiveStep(BREAKPOINT_SM.getSize(), 2),
            3, new FormLayout.ResponsiveStep(BREAKPOINT_SM.getSize(), 3),
            4, new FormLayout.ResponsiveStep(BREAKPOINT_SM.getSize(), 4),
            5, new FormLayout.ResponsiveStep(BREAKPOINT_SM.getSize(), 5)
    );

    public static final Map<Integer, FormLayout.ResponsiveStep> FIXED_COLUMNS_MEDIUM = Map.of(
            1, new FormLayout.ResponsiveStep(BREAKPOINT_MD.getSize(), 1),
            2, new FormLayout.ResponsiveStep(BREAKPOINT_MD.getSize(), 2),
            3, new FormLayout.ResponsiveStep(BREAKPOINT_MD.getSize(), 3),
            4, new FormLayout.ResponsiveStep(BREAKPOINT_MD.getSize(), 4),
            5, new FormLayout.ResponsiveStep(BREAKPOINT_MD.getSize(), 5)
    );

    public static final Map<Integer, FormLayout.ResponsiveStep> FIXED_COLUMNS_LARGE = Map.of(
            1, new FormLayout.ResponsiveStep(BREAKPOINT_LG.getSize(), 1),
            2, new FormLayout.ResponsiveStep(BREAKPOINT_LG.getSize(), 2),
            3, new FormLayout.ResponsiveStep(BREAKPOINT_LG.getSize(), 3),
            4, new FormLayout.ResponsiveStep(BREAKPOINT_LG.getSize(), 4),
            5, new FormLayout.ResponsiveStep(BREAKPOINT_LG.getSize(), 5)
    );

    public static final Map<Integer, FormLayout.ResponsiveStep> FIXED_COLUMNS_EXTRA_LARGE = Map.of(
            1, new FormLayout.ResponsiveStep(BREAKPOINT_XL.getSize(), 1),
            2, new FormLayout.ResponsiveStep(BREAKPOINT_XL.getSize(), 2),
            3, new FormLayout.ResponsiveStep(BREAKPOINT_XL.getSize(), 3),
            4, new FormLayout.ResponsiveStep(BREAKPOINT_XL.getSize(), 4),
            5, new FormLayout.ResponsiveStep(BREAKPOINT_XL.getSize(), 5)
    );

    public static final List<FormLayout.ResponsiveStep> FLEXIBLE_COLUMNS = List.of(
            new FormLayout.ResponsiveStep(BREAKPOINT_SM.getSize(), 1),
            new FormLayout.ResponsiveStep(BREAKPOINT_MD.getSize(), 2),
            new FormLayout.ResponsiveStep(BREAKPOINT_LG.getSize(), 3),
            new FormLayout.ResponsiveStep(BREAKPOINT_XL.getSize(), 4)
    );

    public static Anchor createAnchor(String href, String text) {
        Anchor anchor = new Anchor(href, text);

        anchor.getStyle()
                .setTextDecoration("underline")
                .set("margin-block-start", "1em")
                .set("margin-block-end", "1em");

        anchor.addClassNames(LumoUtility.LineHeight.SMALL, LumoUtility.Margin.Bottom.NONE, LumoUtility.Display.BLOCK);
        anchor.addClassNames(LumoUtility.FontWeight.SEMIBOLD);

        return anchor;
    }


    /**
     * Thread-unsafe formatters.
     */
    private static final ThreadLocal<DecimalFormat> decimalFormat = ThreadLocal
            .withInitial(() -> new DecimalFormat("###,###.00", DecimalFormatSymbols.getInstance(Locale.US)));
    private static final ThreadLocal<DateTimeFormatter> dateFormat = ThreadLocal
            .withInitial(() -> DateTimeFormatter.ofPattern("MMM dd, yyyy"));

    /* ==== BUTTONS ==== */

    // Styles

    public static Button createPrimaryButton(String text) {
        return createButton(text, ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createPrimaryButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createPrimaryButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createTertiaryButton(String text) {
        return createButton(text, ButtonVariant.LUMO_TERTIARY);
    }

    public static Button createTertiaryButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_TERTIARY);
    }

    public static Button createTertiaryButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_TERTIARY);
    }

    public static Button createTertiaryInlineButton(String text) {
        return createButton(text, LUMO_TERTIARY_INLINE);
    }

    public static Button createTertiaryInlineButton(VaadinIcon icon) {
        return createButton(icon, LUMO_TERTIARY_INLINE);
    }

    public static Button createTertiaryInlineButton(String text,
                                                    VaadinIcon icon) {
        return createButton(text, icon, LUMO_TERTIARY_INLINE);
    }


    public static Button createSuccessButton(String text) {
        return createButton(text, ButtonVariant.LUMO_SUCCESS);
    }

    public static Button createSuccessButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_SUCCESS);
    }

    public static Button createSuccessButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_SUCCESS);
    }

    public static Button createSuccessPrimaryButton(String text) {
        return createButton(text, ButtonVariant.LUMO_SUCCESS,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createSuccessPrimaryButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_SUCCESS,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createSuccessPrimaryButton(String text,
                                                    VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_SUCCESS,
                ButtonVariant.LUMO_PRIMARY);
    }


    public static void clear(List<PropertyInputForm> inputForms) {
        inputForms.forEach(propertyInputForm -> propertyInputForm.clear());
    }

    public static boolean isValid(List<PropertyInputForm> inputForms) {
        return inputForms.stream()
                .allMatch(propertyInputForm -> propertyInputForm.isValid());
    }

    public static PropertyBox copyValues(PropertySet<?> properties, List<PropertyInputForm> inputForms) {
        PropertyBox propertyBox = PropertyBox.builder(properties)
                .build();
        for (PropertyInputForm inputForm : inputForms) {
            propertyBox = propertyBox.cloneBox(inputForm.getValue());
        }

        return propertyBox;
    }

    public static void enable(List<PropertyInputForm> inputForms, boolean enabled) {
        inputForms.forEach(propertyInputForm -> propertyInputForm.setEnabled(enabled));
    }

    public static Button createErrorButton(String text) {
        return createButton(text, ButtonVariant.LUMO_ERROR);
    }

    public static Button createErrorButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_ERROR);
    }

    public static Button createErrorButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_ERROR);
    }

    public static Button createErrorPrimaryButton(String text) {
        return createButton(text, ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createErrorPrimaryButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createErrorPrimaryButton(String text,
                                                  VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createContrastButton(String text) {
        return createButton(text, ButtonVariant.LUMO_CONTRAST);
    }

    public static Button createContrastButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_CONTRAST);
    }

    public static Button createContrastButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_CONTRAST);
    }

    public static Button createContrastPrimaryButton(String text) {
        return createButton(text, ButtonVariant.LUMO_CONTRAST,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createContrastPrimaryButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_CONTRAST,
                ButtonVariant.LUMO_PRIMARY);
    }

    public static Button createContrastPrimaryButton(String text,
                                                     VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_CONTRAST,
                ButtonVariant.LUMO_PRIMARY);
    }

    // Size

    public static Button createSmallButton(String text) {
        return createButton(text, ButtonVariant.LUMO_SMALL);
    }

    public static Button createSmallButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_SMALL);
    }

    public static Button createSmallButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_SMALL);
    }

    public static Button createLargeButton(String text) {
        return createButton(text, ButtonVariant.LUMO_LARGE);
    }

    public static Button createLargeButton(VaadinIcon icon) {
        return createButton(icon, ButtonVariant.LUMO_LARGE);
    }

    public static Button createLargeButton(String text, VaadinIcon icon) {
        return createButton(text, icon, ButtonVariant.LUMO_LARGE);
    }

    // Text

    public static Button createButton(String text, ButtonVariant... variants) {
        Button button = new Button(text);
        button.addThemeVariants(variants);
        button.getElement().setAttribute("aria-label", text);
        return button;
    }

    // Icon

    public static Button createButton(VaadinIcon icon,
                                      ButtonVariant... variants) {
        Button button = new Button(new Icon(icon));
        button.addThemeVariants(variants);
        return button;
    }

    // Text and icon

    public static Button createButton(String text, VaadinIcon icon,
                                      ButtonVariant... variants) {
        Icon i = new Icon(icon);
        i.addClassName(LumoUtility.IconSize.SMALL);
//        i.getElement().setAttribute("slot", "prefix");
        Button button = new Button(text);
        button.setPrefixComponent(i);
        button.addThemeVariants(variants);
        return button;
    }

    /* ==== TEXTFIELDS ==== */

    public static TextField createSmallTextField() {
        TextField textField = new TextField();
        textField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        return textField;
    }


    /* === MISC === */


    public static Button createFloatingActionButton(VaadinIcon icon) {
        Button button = createPrimaryButton(icon);
        button.addThemeName("fab");
        return button;
    }


    /* === NUMBERS === */

    public static String formatAmount(Double amount) {
        return decimalFormat.get().format(amount);
    }

    public static String formatAmount(int amount) {
        return decimalFormat.get().format(amount);
    }


    public static String formatUnits(int units) {
        return NumberFormat.getIntegerInstance().format(units);
    }

    public static H3 createUnitsLabel(int units) {
        H3 label = new H3(formatUnits(units));
        label.addClassName(LumoUtility.FontWeight.THIN);
//        CSSUtils.FontFamily.MONOSPACE
        return label;
    }

    public static String getCurrencySymbol() {
        return NumberFormat.getCurrencyInstance().getCurrency().getSymbol();
        //new Locale("en", "IN")
    }

    public static Double formatNumber(String number) throws ParseException {
        return NumberFormat.getInstance().parse(number).doubleValue();
//        LocalizationContext context = LocalizationContext.builder().build();
//        return Double.parseDouble(context.format(NumberUtils.createNumber(number), NumberFormatFeature.DISABLE_GROUPING));
    }

    public static void setTextColor(String textColor, Component... components) {
        for (Component component : components) {
            component.addClassName(textColor);
        }
    }

    /* === ICONS === */

    public static Icon createPrimaryIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        setTextColor(LumoUtility.TextColor.PRIMARY, i);
        return i;
    }

    public static Icon createSecondaryIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        setTextColor(LumoUtility.TextColor.SECONDARY, i);
        return i;
    }

    public static Icon createTertiaryIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        setTextColor(LumoUtility.TextColor.TERTIARY, i);
        return i;
    }

    public static Icon createDisabledIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        setTextColor(LumoUtility.TextColor.DISABLED, i);
        return i;
    }

    public static Icon createSuccessIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        setTextColor(LumoUtility.TextColor.SUCCESS, i);
        return i;
    }

    public static Icon createErrorIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        setTextColor(LumoUtility.TextColor.ERROR, i);
        return i;
    }

    public static void formatPhoneField(TextField field) {
        field.setAllowedCharPattern("[0-9()]");
        field.setMinLength(10);
        field.setMaxLength(10);
    }

    public static Icon createSmallIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        i.addClassName(LumoUtility.IconSize.SMALL);
        return i;
    }

    public static Icon createLargeIcon(VaadinIcon icon) {
        Icon i = new Icon(icon);
        i.addClassName(LumoUtility.IconSize.LARGE);
        return i;
    }

    // Combinations

    public static Icon createIcon(String iconSize, String color,
                                  VaadinIcon icon) {
        Icon i = new Icon(icon);
        i.addClassNames(iconSize, color);
        setTextColor(color, i);
        return i;
    }

    /* === DATES === */

    public static String formatDate(LocalDate date) {
        return dateFormat.get().format(date);
    }

    /* === NOTIFICATIONS === */

    public static void showNotification(String text) {
        Notification.show(text, 3000, Notification.Position.BOTTOM_CENTER);
    }


    public static void setColSpan(int span, Component... components) {
        for (Component component : components) {
            component.getElement().setAttribute("colspan",
                    Integer.toString(span));
        }
    }


    public static void setLineHeight(String value,
                                     Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("line-height",
                    value);
        }
    }

    public static void setMaxWidth(String value, Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("max-width", value);
        }
    }


    public static void setTheme(String theme, Component... components) {
        for (Component component : components) {
            component.getElement().setAttribute("theme", theme);
        }
    }

    public static void setTooltip(String tooltip, Component... components) {
        for (Component component : components) {
            component.getElement().setProperty("title", tooltip);
        }
    }

    public static Anchor createLogoutLink(String contextPath) {
        final Anchor a = populateLink(new Anchor(), VaadinIcon.ARROW_RIGHT, "Logout");
        return a;
    }

    public static <T extends HasComponents> T populateLink(T a, VaadinIcon icon, String title) {
        a.add(icon.create());
        a.add(title);
        return a;
    }

    public static Div createWrapper() {
        final var wrapper = new Div();
        wrapper.addClassNames(LumoUtility.Margin.Horizontal.AUTO);
        wrapper.setWidthFull();
        wrapper.setMaxWidth(1024, Unit.PIXELS);
        return wrapper;
    }

    public static Div createCard(Component... components) {
        final var div = new Div(components);
        div.addClassNames(LumoUtility.FlexDirection.COLUMN, LumoUtility.Display.FLEX, LumoUtility.Padding.MEDIUM, LumoUtility.Margin.MEDIUM, LumoUtility.BoxShadow.MEDIUM);
        div.addClassNames("bg-yellow-200", LumoUtility.BorderRadius.LARGE);
        div.addClassNames(LumoUtility.AlignItems.CENTER);
        return div;
    }

    /*private static FlexLayout createCard(Component... components) {
        FlexLayout layout = new FlexLayout();
        layout.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        layout.addClassName("card");
        layout.addClassName(LumoUtility.Gap.MEDIUM);
        layout.add(components);
        return layout;
    }*/
    public static FlexLayout createCard(String heading, Component... components) {
        FlexLayout layout = new FlexLayout();
        layout.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        layout.addClassName("card");
        layout.addClassName(LumoUtility.Gap.MEDIUM);
        if (!heading.isEmpty()) {
            layout.add(new H3(heading));
        }
        layout.add(components);
        return layout;
    }

    public static FlexLayout createCard(String heading, String classnames, Component... components) {
        FlexLayout layout = new FlexLayout();
        layout.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        layout.addClassName("card");
        layout.addClassName(classnames);
        if (!heading.isEmpty()) {
            layout.add(new H3(heading));
        }
        layout.add(components);
        return layout;
    }

    public static MenuItem createCheckableItem(HasMenuItems menu, String item, boolean checked,
                                               ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        MenuItem menuItem = menu.addItem(item, clickListener);
        menuItem.setCheckable(true);
        menuItem.setChecked(checked);

        return menuItem;
    }

    public static <T> Renderer<T> createCheckboxRenderer(ValueProvider<T, Boolean> valueProvider) {
        return LitRenderer.<T>of("<vaadin-checkbox ?checked=${item.active} onclick='return false;' onkeydown='return false;'></vaadin-checkbox>")
                .withProperty("active", valueProvider);
    }


    public static MenuItem createIconItem(HasMenuItems menu, LumoIcon iconName, String label, String ariaLabel,
                                          ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        Icon icon = new Icon("lumo", iconName.toString().toLowerCase());

        MenuItem item = menu.addItem(icon, clickListener);
        item.setAriaLabel(ariaLabel);

        if (label != null) {
            item.add(new Text(label));
        }

        return item;
    }

    public static MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName, String label, String ariaLabel,
                                          ComponentEventListener<ClickEvent<MenuItem>> clickListener) {
        Icon icon = new Icon(iconName);

        icon.getStyle().set("width", "var(--lumo-icon-size-s)");
        icon.getStyle().set("height", "var(--lumo-icon-size-s)");
        icon.getStyle().set("marginRight", "var(--lumo-space-s)");

        MenuItem item = menu.addItem(icon, clickListener);
        item.setAriaLabel(ariaLabel);

        if (label != null) {
            item.add(new Text(label));
        }

        return item;
    }

    public static void setWhiteSpace(WhiteSpace whiteSpace,
                                     Component... components) {
        for (Component component : components) {
            component.getElement().setProperty("white-space",
                    whiteSpace.getValue());
        }
    }

    public Span createSpan(String title) {
        final var span = new Span(title);
        span.addClassNames(LumoUtility.FontSize.XXXLARGE, LumoUtility.FontWeight.BOLD);
        return span;
    }

    public static void setEnabled(boolean isEnabled, HasEnabled... hasEnableds) {
        Stream.of(hasEnableds).forEach(field -> field.setEnabled(isEnabled));
    }

    public static void addErrorHandling(Upload upload) {
        upload.addFileRejectedListener(e -> {
            Notification.show(("File was rejected") + ": " + e.getErrorMessage());
        });
        upload.addFailedListener(e -> {
            Notification.show(("Upload failed") + ": " + e.getReason());
        });
    }

    public static Div createWrapDiv(Component... components) {
        final var wrapDiv = new Div(components);
        wrapDiv.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexWrap.WRAP, LumoUtility.Margin.Top.MEDIUM, LumoUtility.Padding.Left.SMALL);
        return wrapDiv;
    }

    public static Component createSectionHeader(String title) {
        final var header = new H2(title);
        header.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.NONE);
        return header;
    }

    public static Div addSection(Div div, String title) {
        final var overviewDiv = createWrapDiv();
        final var overviewHeader = createSectionHeader(title);
        final var details = new Details(overviewHeader, overviewDiv);
        details.setOpened(true);
        div.add(details);
        return overviewDiv;
    }

    public static boolean isLogout(BeforeEnterEvent ev) {
        return ev.getLocation().getQueryParameters().getParameters().containsKey("logout");
    }

    public static void removeLoginRoute() {
        RouteConfiguration.forSessionScope().removeRoute("login");
    }


    public static void ensureLoginPresent(Class<? extends Component> loginComponent) {
        // no double registration for route !
        if (!RouteConfiguration.forSessionScope().getRoute("login").isPresent()) {
            RouteConfiguration.forSessionScope().setRoute("login", loginComponent);
        }
    }

    public static String getClientIp() {
        HttpServletRequest request;
        VaadinServletRequest current = VaadinServletRequest.getCurrent();
        request = current.getHttpServletRequest();

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }

    public static void storeInSessionStorage(String key, String value) {
        UI.getCurrent().getElement().executeJs("window.sessionStorage.setItem($0, $1);", key, value);
    }

    public static Button createCloseBtn(Notification notification) {
        Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(),
                clickEvent -> notification.close());
        closeBtn.addThemeVariants(LUMO_TERTIARY_INLINE);

        return closeBtn;
    }


    public static void setWidth(String value, Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("width", value);
        }
    }


    /* === ACCESSIBILITY === */

    public static void setAriaLabel(String value, Component... components) {
        for (Component component : components) {
            component.getElement().setAttribute("aria-label", value);
        }
    }


    public static Icon createTrashIcon() {
        return createIcon(LumoUtility.IconSize.SMALL, LumoUtility.TextColor.ERROR, VaadinIcon.TRASH);
    }

    public static Div addDummyDiv(String size) {
        Div dummyDiv = new Div();
        dummyDiv.setHeight(size);

        return dummyDiv;
    }

    public static Div addDummyDiv() {
        return addDummyDiv("200px");
    }

    public static final int MOBILE_BREAKPOINT = 480;

    public static boolean isMobile(AttachEvent attachEvent) {
        final List<Boolean> mobile = new ArrayList<>();
        Page page = attachEvent.getUI().getPage();
        page.retrieveExtendedClientDetails(details -> {
            boolean b = details.getWindowInnerWidth() < 740;
            if (b) {
                mobile.add(b);
            }
        });
        page.addBrowserWindowResizeListener(e -> {
            boolean b = e.getWidth() < 740;
            if (b) {
                mobile.add(b);
            }
        });

        return !mobile.isEmpty();
    }

    public static boolean isMobile(int width) {
        return width < MOBILE_BREAKPOINT;
    }

    public static Button createRefreshButton() {
        return createTertiaryButton(VaadinIcon.REFRESH);
    }

    public static void successNotification(String text) {
        notification(text, NotificationVariant.LUMO_SUCCESS);
    }

    public static void errorNotification(String text) {
        notification(text, NotificationVariant.LUMO_ERROR);
    }

    public static void errorNotification(Exception e) {
        notification(ExceptionUtils.getRootCauseMessage(e), NotificationVariant.LUMO_ERROR);
    }

    public static Component viewPropertyBox(PropertyBox propertyBox) {
        FlexLayout layout = new FlexLayout();
        layout.setFlexDirection(FlexLayout.FlexDirection.COLUMN);
        layout.addClassNames(LumoUtility.Gap.SMALL);
        propertyBox.forEach(property -> {
            layout.add(
                    Components.hl()
                            .add(new Text(property.getName()))
                            .add(new Emphasis(String.valueOf(propertyBox.getValue(property))))
                            .build()

            );
        });

        return layout;
    }

    public static void primaryNotification(String text) {
        notification(text, NotificationVariant.LUMO_PRIMARY);
    }

    private static void notification(String text, NotificationVariant variant) {
        Notification notification = new Notification(text);
        notification.setDuration(3000);
        notification.addThemeVariants(variant);
        notification.open();
    }


    public static boolean hasLink(Component tab, String currentRoute) {
        return tab.getChildren().filter(RouterLink.class::isInstance)
                .map(RouterLink.class::cast).map(RouterLink::getHref)
                .anyMatch(currentRoute::equals);
    }

    public static void singleColumn(FormLayout form) {
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
    }


    public static Icon createEditIcon() {
        return createIcon(LumoUtility.IconSize.SMALL, LumoUtility.TextColor.TERTIARY, VaadinIcon.TRASH);
    }

    public static Button createButton(String text, LumoIcon lumoIcon, ButtonVariant... variants) {
        Button button = new Button(text);
        button.setPrefixComponent(lumoIcon.create());
        button.addThemeVariants(variants);
        return button;
    }

    public static void createReadOnlyTextArea(TextArea textArea) {
        textArea.addClassNames(LumoUtility.TextColor.DISABLED, LumoUtility.BorderColor.CONTRAST_10,
                LumoUtility.Background.CONTRAST_5);
        textArea.setSizeFull();
    }

    public static TextArea createReadOnlyTextArea() {
        TextArea textArea = new TextArea();
        textArea.setWidthFull();
        textArea.addClassNames(LumoUtility.TextColor.DISABLED, LumoUtility.BorderColor.CONTRAST_10,
                LumoUtility.Background.CONTRAST_5);
        return textArea;
    }

    public static void createTooltip(String tip, Component component) {
        Tooltip.forComponent(component)
                .withText(tip)
                .withPosition(Tooltip.TooltipPosition.BOTTOM_END);
    }

    private void write(Object o) {
        System.out.println(o);
    }

    public static final class DeleteDialog extends ConfirmDialog {

        private String headerTitle;
        private String message;

        public DeleteDialog() {
            super();
            initializeUI();
        }

        private void initializeUI() {
            setCancelable(true);
            setConfirmText("Delete");
        }

        public DeleteDialog(String headerTitle, String message) {
            this();
            this.headerTitle = headerTitle;
            this.message = message;
        }

        public String getHeaderTitle() {
            return headerTitle;
        }

        public DeleteDialog setHeaderTitle(String headerTitle) {
            this.headerTitle = headerTitle;
            setHeader(this.headerTitle);
//            add(new Hr());
            return this;
        }

        public String getMessage() {
            return message;
        }

        public DeleteDialog setMessage(String message) {
            this.message = message;
            add(new Paragraph(this.message));
            return this;
        }

        public void delete(DeleteButton deleteButton) {
            super.setConfirmButton(deleteButton);
            open();
        }
    }


    public static void preventBrowserKeys(Component component, Key key) {
        // Prevent click shortcut of the OK button from also triggering when
        // another button is focused
        ShortcutRegistration shortcutRegistration = Shortcuts
                .addShortcutListener(component, () -> {
                }, key)
                .listenOn(component);
        shortcutRegistration.setEventPropagationAllowed(false);
        shortcutRegistration.setBrowserDefaultAllowed(true);
    }

    public static void beforeLeave(BeforeLeaveEvent event, Binder<?> binder) {
        if (binder.hasChanges()) {
            final BeforeLeaveEvent.ContinueNavigationAction action = event.postpone();
            final ConfirmDialog dialog = new ConfirmDialog();
            dialog.setText("Are you sure you want to leave? You have unsaved data.");
            dialog.setConfirmButton("Stay", e -> dialog.close());
            dialog.setCancelButton("Leave", e -> action.proceed());
            dialog.setCancelable(true);
            dialog.open();
        }
    }

    public static void trimPadding(Grid<?> grid) {
        // Tip: Grid uses these values for padding, lets set them smaller value
        // so that editor will look nicer.
        grid.getStyle().set("--lumo-space-xs", "1px");
        grid.getStyle().set("--lumo-space-m", "1px");
    }

    public static MenuBar createMenuToggle(Map<Grid.Column<?>, String> toggleableColumns) {
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        MenuItem menuItem = menuBar.addItem(VaadinIcon.ELLIPSIS_DOTS_V.create());
        SubMenu subMenu = menuItem.getSubMenu();

        toggleableColumns.forEach(
                (column, header) -> {
                    Checkbox checkbox = new Checkbox(header);
                    checkbox.setValue(column.isVisible());
                    checkbox.addValueChangeListener(e -> column.setVisible(e.getValue()));
                    subMenu.addItem(checkbox);
                }
        );

        return menuBar;
    }

    public static final class ColumnToggleContextMenu extends ContextMenu {
        public ColumnToggleContextMenu(Component target) {
            super(target);
            setOpenOnClick(true);
        }

        public void addColumnToggleItem(String label, Grid.Column<?> column) {
            MenuItem menuItem = this.addItem(label, e -> {
                column.setVisible(e.getSource().isChecked());
            });
            menuItem.setCheckable(true);
            menuItem.setChecked(column.isVisible());
        }
    }

    public static DataProviderListener<?> gridNoDataListener(Grid<?> grid, Div noDataWarningMsg) {
        DataProvider<?, ?> dataProvider = grid.getDataProvider();
        DataProviderListener<?> listener = dataChangeEvent -> {
            if (dataProvider.size(new Query<>()) == 0) {
                noDataWarningMsg.addClassName(LumoUtility.Display.HIDDEN);
            } else {
                noDataWarningMsg.removeClassName(LumoUtility.Display.HIDDEN);
            }
        };

        // Initial run of the listener, as there is no event fired for the initial state
        // of the data set that might be empty or not.
        listener.onDataChange(null);

        return listener;

    }

    public static class PriceConverter extends StringToBigDecimalConverter {

        public PriceConverter() {
            super(BigDecimal.ZERO, "Cannot convert value to a number.");
        }

        @Override
        protected NumberFormat getFormat(Locale locale) {
            // Always display currency with two decimals
            final NumberFormat format = super.getFormat(locale);
            if (format instanceof DecimalFormat) {
                format.setMaximumFractionDigits(2);
                format.setMinimumFractionDigits(2);
            }
            return format;
        }
    }

    public static class StockCountConverter extends StringToIntegerConverter {

        public StockCountConverter() {
            super(0, "Could not convert value to " + Integer.class.getName()
                    + ".");
        }

        @Override
        protected NumberFormat getFormat(Locale locale) {
            // Do not use a thousands separator, as HTML5 input type
            // number expects a fixed wire/DOM number format regardless
            // of how the browser presents it to the user (which could
            // depend on the browser locale).
            final DecimalFormat format = new DecimalFormat();
            format.setMaximumFractionDigits(0);
            format.setDecimalSeparatorAlwaysShown(false);
            format.setParseIntegerOnly(true);
            format.setGroupingUsed(false);
            return format;
        }
    }

    public static void updateSaveBtnStatus(Binder<?> binder, Button save, Button discard) {
        // enable/disable save button while editing
        binder.addStatusChangeListener(event -> {
            final boolean isValid = !event.hasValidationErrors();
            final boolean hasChanges = binder.hasChanges();
            save.setEnabled(hasChanges && isValid);
            discard.setEnabled(hasChanges);
        });
    }

    public static TextField addFocusShortcut(TextField filter) {
        // A shortcut to focus on the textField by pressing ctrl + F
        filter.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);
        return filter;
    }

    public static Button shortcutForNew(Button newProduct) {
        // A shortcut to click the new product button by pressing ALT + N
        newProduct.addClickShortcut(Key.KEY_N, KeyModifier.ALT);
        return newProduct;
    }

    public static Button saveBtnShortcut(Button save) {
        save.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);
        return save;
    }

    public static Icon createCloseIcon() {
        final Icon closeIcon = VaadinIcon.CLOSE_SMALL.create();
        closeIcon.addClassName(LumoUtility.IconSize.SMALL);
        return closeIcon;
    }

    public static Locale[] getAvailableLocales() {
        Locale[] availableLocales = Locale.getAvailableLocales();
        Arrays.sort(availableLocales, Comparator.comparing(Locale::getDisplayName));
        return availableLocales;
    }

    public static void useLocale(Grid<?> grid, Locale locale) {
        grid.removeAllColumns();
        grid.addColumn(value -> value).setHeader("Unformatted number");
        grid
                .addColumn(new NumberRenderer<>(value -> (Number) value, NumberFormat.getInstance(locale)))
                .setHeader("Formatted number");
    }

    public static Button createCloseButton() {
        var closeBtn = new Button(createCloseIcon());
        closeBtn.addThemeVariants(LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ICON);
//        closeBtn.setThemeName("icon");
        return closeBtn;
    }

    public static Component createWidget(String title, Double accountBalance) {
        VaadinIcon icon = VaadinIcon.MONEY;
        String prefix = " ";
        String theme = "badge";


        if (accountBalance > 0) {
            theme += " success";
        } else if (accountBalance < 0) {
            theme += " error";
        }

        H2 welcomeText = new H2(title);
        welcomeText.addClassNames(LumoUtility.FontWeight.NORMAL, LumoUtility.Margin.NONE,
                LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.MEDIUM);

        Span accountBalanceSpan = new Span("Account Balance");
        accountBalanceSpan.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);

        Icon i = icon.create();
        i.addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Padding.XSMALL);

        String balance = currencyFormat.format(accountBalance);
        Span badge = new Span(i, new Span(prefix + balance));
        badge.getElement().getThemeList().add(theme);
        badge.addClassNames(LumoUtility.FontSize.LARGE);

        VerticalLayout layout = new VerticalLayout(welcomeText, accountBalanceSpan, badge);
        layout.addClassName(LumoUtility.Padding.LARGE);
        layout.setPadding(false);
        layout.setSpacing(false);
        return layout;
    }

    public static Component createWidget(String title, String value, Integer year) {

        H2 titleText = new H2(title);
        titleText.addClassNames(LumoUtility.FontWeight.NORMAL, LumoUtility.Margin.NONE,
                LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.MEDIUM);

        Span valueSpan = new Span(value);
        valueSpan.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.XXXLARGE);

        String theme = "badge";
        Span badge = new Span(year.toString());
        badge.getElement().getThemeList().add(theme);

        VerticalLayout layout = new VerticalLayout(titleText, valueSpan, badge);
        layout.addClassName(LumoUtility.Padding.LARGE);
        layout.setPadding(false);
        layout.setSpacing(false);
        return layout;
    }

    public static HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.NONE);

        Span span = new Span(subtitle);
        span.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.XSMALL);
        span.getElement().getThemeList().add("badge");

        VerticalLayout column = new VerticalLayout(h2, span);
        column.setPadding(false);
        column.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(column);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setSpacing(false);
        header.setWidthFull();
        return header;
    }


    public static final class UpdateButton extends Button {

        public UpdateButton() {
            super();
            setButtonProperties();

        }

        private void setButtonProperties() {
            setText("Update");
            addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        }

        public UpdateButton(ComponentEventListener<ClickEvent<Button>> buttonClickEvent) {
            this();
            setButtonProperties();
            action(buttonClickEvent);

        }

        public void action(ComponentEventListener<ClickEvent<Button>> buttonClickEvent) {
            addClickListener(buttonClickEvent);
        }
    }

    public static final class DeleteButton extends Button {

        public DeleteButton() {
            super();
            setButtonProperties();

        }

        private void setButtonProperties() {
            setText("Delete");
            addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        }

        public DeleteButton(ComponentEventListener<ClickEvent<Button>> buttonClickEvent) {
            super();
            setButtonProperties();
            action(buttonClickEvent);


        }

        public void action(ComponentEventListener<ClickEvent<Button>> buttonClickEvent) {
            addClickListener(buttonClickEvent);
        }
    }

    public static Icon badgeIcon(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge");
    }

    public static Icon badgeIconSuceess(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge success");
    }

    public static Icon badgeIconError(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge error");
    }

    public static Icon badgeIconContrast(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge contrast");
    }

    public static Icon badgeIconPrimary(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge primary");
    }

    public static Icon badgeIconPrimarySuccess(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge success primary");
    }

    public static Icon badgeIconPrimaryError(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge error primary");
    }

    public static Icon badgeIconPrimaryContrast(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge contrast primary");
    }

    public static Icon badgeIconSmall(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge small");
    }

    public static Icon badgeIconSmallSuccess(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge success small");
    }

    public static Icon badgeIconSmallError(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge error smalle");
    }

    public static Icon badgeIconSmallContrast(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge contrast small");
    }

    public static Icon badgeIconPrimarySmall(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge small primary");
    }

    public static Icon badgeIconPrimarySmallSuccess(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge success small primary");
    }

    public static Icon badgeIconPrimarySmallError(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge error small primary");
    }

    public static Icon badgeIconPrimarySmallContrast(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge contrast small primary");
    }

    public static Icon badgeIconPill(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge pill");
    }

    public static Icon badgeIconPillSuccess(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge success pill");
    }

    public static Icon badgeIconPillError(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge error pill");
    }

    public static Icon badgeIconPillContrast(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge contrast pill");
    }

    public static Icon badgeIconPillPrimary(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge primary pill");
    }

    public static Icon badgeIconPillPrimarySuccess(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge success primary pill");
    }

    public static Icon badgeIconPillPrimaryError(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge error primary pill");
    }

    public static Icon badgeIconPillPrimaryContrast(VaadinIcon vaadinIcon, String ariaLabel) {
        return createIconBadge(vaadinIcon, ariaLabel, "badge contrast primary pill");
    }


    private static Icon createIconBadge(VaadinIcon vaadinIcon, String ariaLabel, String theme) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        // Accessible label
        icon.getElement().setAttribute("aria-label", ariaLabel);
        // Tooltip
        icon.getElement().setAttribute("title", ariaLabel);
        icon.getElement().getThemeList().add(theme);
        return icon;
    }

    public static Component createTextFieldFilterHeader(Consumer<String> filterChangeConsumer) {
        return textFieldFilter(filterChangeConsumer);
    }

    public static LitRenderer<String> customerNameRenederer(Supplier<String> name) {
        return LitRenderer.<String>of(
                        "<vaadin-horizontal-layout style=\"align-items: center;\" theme=\"spacing\">"
                                + "  <vaadin-avatar name=\"${item.fullName}\"></vaadin-avatar>"
                                + "  <span> ${item.fullName} </span>"
                                + "</vaadin-horizontal-layout>")
                .withProperty("fullName", s -> name.get());
    }

    public static void addComponentAsLastAndAlignToRight(VerticalLayout layout, Component component) {
        layout.addComponentAtIndex(layout.getComponentCount(), component);
        component.getElement().getStyle().set("margin-left", "auto");
    }

    public static void addComponentAsLastAndAlignToRight(HorizontalLayout layout, Component component) {
        layout.addComponentAtIndex(layout.getComponentCount(), component);
        component.getElement().getStyle().set("margin-left", "auto");
    }

    public static void setTabErrorIcon(Tab tab, boolean partHasErrors) {
        Component currentIcon = tab.getChildren().filter(child -> child instanceof Icon).findAny().orElse(null);

        if (partHasErrors && currentIcon == null) {
            tab.add(VaadinIcon.EXCLAMATION.create());
        } else if (!partHasErrors && currentIcon != null) {
            tab.remove(currentIcon);
        }
    }

    public static void selectGridRowsUsingUpDownKeys(Grid<?> grid) {
        // A 'keyup' event is used (1) to do the selection on the client-side
        // when arrow keys are used, and (2) to inform the server of the
        // selection.
        grid.addAttachListener(
                a -> {
                    grid
                            .getElement()
                            .executeJs(
                                    "this.addEventListener('keyup', function(e) {" +
                                            // ignore Space as it can still be used to
                                            // (de)select items
                                            "if (e.keyCode == 32) return;" +
                                            "let grid = $0;" +
                                            "if (grid.selectedItems){" +
                                            "grid.activeItem=this.getEventContext(e).item;" +
                                            "grid.selectedItems=[this.getEventContext(e).item];" +
                                            "grid.$server.select(this.getEventContext(e).item.key);}} )",
                                    grid.getElement()
                            );
                }
        );
    }

    public static LitRenderer<MyImage> imageLitRenderer() {
        return LitRenderer.<MyImage>of("<img src=${item.image} />")
                .withProperty("image", pojo -> {
                    if (pojo.getImage() == null) {
                        return "";
                    }
                    return "data:image;base64," + Base64.getEncoder().encodeToString(pojo.getImage());
                });
    }

    public static class MyImage {
        private byte[] image;

        public MyImage(byte[] image) {
            this.image = image;
        }

        public byte[] getImage() {
            return image;
        }

        public void setImage(byte[] image) {
            this.image = image;
        }
    }

    public static void disableBtnDoubleClick(Button button, Supplier<Void> action) {
        button
                .getElement()
                .addEventListener(
                        "click",
                        e -> {
                            JsonValue detail = e.getEventData().get("event.detail");
                            if (detail.asNumber() > 1) {
                                // double click, ignore
                            } else {
                                action.get();
                            }
                        }
                )
                .addEventData("event.detail");
    }

    public static final class MyGrid {
        public static Grid<?> createIndexColumn(@NotNull Grid<?> grid) {
            final Grid.Column<?> rowIndex = grid.addColumn(item -> "").setKey("rowIndex").setHeader("#ID");
            rowIndex.setFlexGrow(0);

            grid.addAttachListener(event -> {
                grid.getColumnByKey("rowIndex").getElement().executeJs(
                        "this.renderer = function(root, column, rowData) {root.textContent = rowData.index + 1}"
                );
            });

            return grid;
        }

        public static Grid<?> makeCompact(@NotNull Grid<?> grid) {
            grid.addThemeVariants(GridVariant.LUMO_COMPACT);
            return grid;
        }

        public static void setDefaultLoadSize(Grid<?> grid) {
            grid.setPageSize(100);
        }

        public static void setDefaultItemCount(GridLazyDataView<?> dataView) {
            dataView.setItemCountEstimate(1000);
            dataView.setItemCountEstimateIncrease(500);
        }
    }

    public static void showFailureDialog(String title, String errMessage) {
        ConfirmDialog dialog = new ConfirmDialog();

        Icon errorIcon = new Icon(VaadinIcon.EXCLAMATION_CIRCLE_O);
        errorIcon.setColor("#ff7745");
        errorIcon.setSize("2.5em");

        H2 headerMessage = new H2("Creation failed");
        headerMessage.getStyle().set("font-family", "system-ui").set("font-weight", "900");

        HorizontalLayout headerLayout = new HorizontalLayout(errorIcon, headerMessage);
        headerLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        dialog.setHeader(headerLayout);
        dialog.setText(new Html(
                String.format(

                        "<h5>%s. <br/><br/>" +
                                "If the problem persists, please contact: " +
                                "<b><a href=\"mailto:prospero.support@pm.me\">prospero.support@pm.me</a></b></h5>", errMessage)));

        dialog.setConfirmText("OK");
        dialog.open();
    }

    public static Component mapToTextArea(final String value) {
        final var resultTextArea = new TextArea();
        resultTextArea.setReadOnly(true);
        resultTextArea.setWidth("100%");
        resultTextArea.setValue(value);
        resultTextArea.setMaxHeight("120px");
        return resultTextArea;
    }

    public static void showNotification(Notification.Position position, String whatToShow, NotificationVariant notificationVariant) {
        Notification notification = Notification.show(whatToShow, 3000, position);
        notification.addThemeVariants(notificationVariant);
    }

    /**
     * Provides exclusive access to this UI from outside a request handling thread.
     *
     * @param ui   The UI (not null)
     * @param task The task to execute
     * @return A future that can be used to check for task completion and to cancel
     * the task
     */
    public static Future<Void> access(UI ui, final Consumer<UI> task) {
        Objects.requireNonNull(ui, "UI must be not null");
        return ui.access(() -> {
            task.accept(ui);
            // check push
            final PushMode pushMode = ui.getPushConfiguration().getPushMode();
            if (PushMode.MANUAL == pushMode) {
                ui.push();
            }
        });
    }

    public static UnorderedList unorderedPriceList() {
        UnorderedList unorderedList = new UnorderedList();
        unorderedList.addClassNames(
//                FontFamily.MONO,
                LumoUtility.ListStyleType.NONE,
                LumoUtility.Margin.Horizontal.AUTO,
                LumoUtility.Margin.Vertical.NONE,
                LumoUtility.MaxWidth.SCREEN_SMALL,
                LumoUtility.Padding.NONE
        );

        return unorderedList;
    }


    public static ListItem listItem(Span timeSpan, Span priceSpan) {
        ListItem listItem = new ListItem(timeSpan, priceSpan);
        listItem.addClassNames(
                LumoUtility.Border.BOTTOM,
                LumoUtility.Display.FLEX,
                LumoUtility.JustifyContent.BETWEEN,
                LumoUtility.Padding.SMALL
        );

        return listItem;
    }

    public static void serviceInit(ServiceInitEvent serviceInitEvent) {
//        Check the below line and see how it works
//        setProperty("vaadin.i18n.provider", TranslationProvider.class.getName());
        serviceInitEvent.getSource().addUIInitListener(uiInitEvent -> {
            // Whenever a new user arrives, determine locale
            initLanguage(uiInitEvent.getUI());
            //uiInitEvent.getUI().setLocale(fiLocale);
        });
    }

    public static void initLanguage(UI ui) {
        Optional<Cookie> localeCookie = Optional.empty();
        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
        if (cookies != null) {
            localeCookie = Arrays.stream(cookies).filter(cookie -> "locale".equals(cookie.getName())).findFirst();
        }
        if (localeCookie.isPresent() && !"".equals(localeCookie.get().getValue())) {
            // Cookie found, use that
            ui.setLocale(Locale.forLanguageTag(localeCookie.get().getValue()));
        } else {
            // Try to use Vaadin's browser locale detection
            ui.setLocale(VaadinService.getCurrentRequest().getLocale());
        }
    }

    public static Icon createStatusIcon(String status) {
        boolean isAvailable = "Available".equals(status);
        Icon icon;
        if (isAvailable) {
            icon = VaadinIcon.CHECK.create();
            icon.getElement().getThemeList().add("badge success");
        } else {
            icon = VaadinIcon.CLOSE_SMALL.create();
            icon.getElement().getThemeList().add("badge error");
        }
        icon.getStyle().set("padding", "var(--lumo-space-xs");
        return icon;
    }

    /**
     * Check whether the current browser is running on a touch device.
     *
     * @return Whether the current browser is running on a touch device
     */
    public static boolean isTouchDevice() {
        final UI ui = UI.getCurrent();
        if (ui != null && ui.getInternals() != null) {
            if (ui.getInternals().getExtendedClientDetails() != null) {
                return ui.getInternals().getExtendedClientDetails().isTouchDevice();
            }
        }
        final VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            return session.getBrowser().isAndroid() || session.getBrowser().isIPhone()
                    || session.getBrowser().isWindowsPhone();

        }
        return false;
    }

    public static void updateMenuItemsAsCheckable(MenuItem parentItem, String menuItemText) {
        parentItem.getSubMenu().getChildren().forEach(component -> {
            if (component instanceof MenuItem) {
                MenuItem menuItem = (MenuItem) component;

                menuItem.setCheckable(true);
                if (menuItem.getText().equals(menuItemText)) {

                    menuItem.setChecked(true);
                } else {
                    menuItem.setChecked(false);
                }
            }
        });
    }

    public static void setSortable(Grid<?> grid, boolean sortable) {
        grid.getColumns()
                .forEach(column -> column.setSortable(sortable));
    }

    public static void setToggleColumns(Grid<?> grid, MenuItem toggleColumnMenuItem) {
        SubMenu menuItemToggleColumn = toggleColumnMenuItem.getSubMenu();
        grid.getColumns().forEach(column -> {
                       /*Checkbox checkbox = new Checkbox(column.getHeaderText());
                       checkbox.setFormValue(column.isVisible());
                       checkbox.addValueChangeListener(e -> column.setVisible(e.getFormValue()));
            menuItemToggleColumn.addItem(checkbox);*/

            MenuItem menuItem = menuItemToggleColumn.addItem(column.getHeaderText(), e -> {
                column.setVisible(e.getSource().isChecked());
            });
            menuItem.setCheckable(true);
            menuItem.setChecked(column.isVisible());
            menuItem.setKeepOpen(true);
        });
    }

    public static Icon createIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
       /* icon.getStyle().set("color", "var(--lumo-primary-text-color)")
                .set("margin-inline-end", "var(--lumo-space-s")
                .set("padding", "var(--lumo-space-xs");*/
        icon.addClassName(LumoUtility.TextColor.PRIMARY);
        icon.setSize("20px");
        return icon;
    }

    public static HorizontalLayout createIconMenuItem(VaadinIcon vaadinIcon, String text) {

        Icon icon = new Icon(vaadinIcon);
        icon.addClassName(LumoUtility.TextColor.PRIMARY);
        icon.setSize("15px");

        return Components.hl()
                .fullWidth()
                .spacing()
                .padding(false)
                .alignItems(FlexComponent.Alignment.CENTER)
                .add(icon)
                .add(new Text(text))
                .build();
    }

    public static void notifyOptimisticLockingFailureException() {
        Notification n = Notification.show(
                "Error updating the data. Somebody else has updated the record while you were making changes.");
        n.setPosition(Notification.Position.MIDDLE);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    public static void notifyValidationException() {
        Notification.show("Failed to update the data. Check again that all values are valid");
    }

    public static MenuItem createIconItem(MenuBar menu, VaadinIcon iconName,
                                    String ariaLabel) {
        Icon icon = new Icon(iconName);
        MenuItem item = menu.addItem(icon);
        item.setAriaLabel(ariaLabel);

        return item;
    }

    public static BulkActionBuilder createBulkActionBuilder(GridMultiSelectionModel<?> gridMultiSelectionModel) {
        return BulkActionBuilder.create()
                .selectAll(BooleanInputBuilder.create()
                        .withValueChangeListener(event -> {
                            if (event.getValue()) {
                                gridMultiSelectionModel.selectAll();
                            } else {
                                gridMultiSelectionModel.deselectAll();
                            }
                        }))
                .hidden();
    }

    private static void showHideSearchBarAndBulkActionBar(int selectedItems,LabelBuilder<Span> labelBuilder, BulkActionBuilder bulkActionBuilder, SearchBarBuilder searchBarBuilder) {
        if (selectedItems > 0) {
            bulkActionBuilder.selected(labelBuilder.text(String.format("%d selected", selectedItems)));
            searchBarBuilder.visible(false);
            bulkActionBuilder.visible(true);

        } else {
            searchBarBuilder.visible(true);
            bulkActionBuilder.visible(false);
        }
    }

    public static void showHideSearchBarAndBulkActionBar(Selectable.SelectionEvent<?> selectionEvent, LabelBuilder<Span> labelBuilder, BulkActionBuilder bulkActionBuilder, SearchBarBuilder searchBarBuilder) {
        showHideSearchBarAndBulkActionBar(selectionEvent.getAllSelectedItems().size(),labelBuilder,bulkActionBuilder,searchBarBuilder);
    }

    public static void showHideSearchBarAndBulkActionBar(MultiSelectionEvent<Grid<?>, ?> multiSelectionEvent, LabelBuilder<Span> labelBuilder, BulkActionBuilder bulkActionBuilder, SearchBarBuilder searchBarBuilder) {
        int selectedItems = multiSelectionEvent.getAllSelectedItems().size();

        showHideSearchBarAndBulkActionBar(selectedItems,labelBuilder,bulkActionBuilder,searchBarBuilder);

    }

   public static class OptionsBar implements HasOptionsBar {


        @Override
        public MenuItem createSrtBy(MenuBar menuBar) {
            MenuItem menuItem = menuBar.addItem("Sort By");
            return menuItem;
        }

        @Override
        public MenuItem importEntity(MenuBar menuBar) {
            MenuItem menuItem = createIconItem(menuBar, LumoIcon.DOWNLOAD, "Import", "Import").menuItem();
            return menuItem;
        }

        @Override
        public MenuItem exportEntity(MenuBar menuBar) {
            MenuItem menuItem = createIconItem(menuBar, LumoIcon.UPLOAD, "Export", "Export").menuItem();
            return menuItem;
        }

        @Override
        public MenuItem refreshList(MenuBar menuBar) {
            MenuItem menuItem = createIconItem(menuBar, VaadinIcon.REFRESH, "Refresh", "Refresh").menuItem();
            return menuItem;
        }

        @Override
        public MenuItem advancedSearch(MenuBar menuBar) {
            MenuItem menuItem = createIconItem(menuBar, LumoIcon.SEARCH, "Search", "Advanced Search").menuItem();
            return menuItem;
        }
    }


}
