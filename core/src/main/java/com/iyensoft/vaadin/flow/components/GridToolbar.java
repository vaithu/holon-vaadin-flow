package com.iyensoft.vaadin.flow.components;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.Input;
import com.holonplatform.vaadin.flow.components.ItemListing;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.components.Selectable;
import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.components.support.ButtonPreset;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * Search, filter, selection, and bulk-action toolbar intended for use above a
 * grid. Selection and filtering remain owned by the grid view; this component
 * exposes the visual state and action callbacks.
 */
@StyleSheet("context://grid-toolbar.css")
public class GridToolbar extends Div implements HasTheme {

    private record BulkAction(String label, Runnable action, boolean destructive) {
    }

    private final Div defaultRow;
    private final Div selectedRow;
    private Div filterSlot;
    private final Div bulkActionsSlot;
    private final Span selectedCount;
    private final Span selectedLabel;
    private final Div selectionInfo;
    private Span filterBadge;
    private final Input<String> searchInput;
    private Button filterButton;
    private Div filterTrigger;
    private final Button clearButton;
    private final Button moreButton;
    private Dialog filterDialog;
    private final ContextMenu moreMenu = new ContextMenu();
    private Button filterCloseButton;
    private final List<BulkAction> bulkActions = new ArrayList<>();
    private boolean filterEnabled;

    private Div optionsMenuSlot;
    private ContextMenu optionsMenu;
    private Button optionsMenuButton;

    private boolean sortEnabled;
    private boolean refreshEnabled;
    private boolean resetColumnWidthsEnabled;
    private boolean showHideColumnsEnabled;
    private boolean viewModeToggleEnabled;

    private Registration selectionRegistration;
    private com.holonplatform.core.Registration listingSelectionRegistration;
    private Component primaryAction;
    private transient Runnable clearSelectionAction = () -> {};

    public GridToolbar(Component... components) {
        this(false, components);
    }

    public GridToolbar(boolean filterEnabled, Component... components) {
        addClassName("grid-toolbar");
        getElement().setAttribute("role", "toolbar");
        getElement().setAttribute("aria-label", LocalizationProvider.localize("Grid toolbar", "grid_toolbar.aria_label"));

        // Configure Search Field
        searchInput = Components.input.string()
                .placeholder("Search", "grid_toolbar.search_placeholder")
                .ariaLabel("Search", "grid_toolbar.search_aria")
                .prefixComponent(VaadinIcon.SEARCH.create())
                .valueChangeMode(ValueChangeMode.TIMEOUT)
                .styleName("grid-toolbar__search")
                .build();
        getSearchField().setValueChangeTimeout(300);

        defaultRow = Components.div()
                .styleNames("grid-toolbar__row", "grid-toolbar__default")
                .elementConfiguration(el -> {
                    el.setAttribute("role", "region");
                    el.setAttribute("aria-label", LocalizationProvider.localize("Default toolbar actions", "grid_toolbar.default_row_aria"));
                })
                .add(searchInput.getComponent())
                .build();

        // Selected Row components
        selectedCount = Components.Badge.badgePillPrimary().build();

        selectedLabel = Components.span()
                .text("selected", "grid_toolbar.selected_label")
                .build();

        selectionInfo = Components.div()
                .styleName("grid-toolbar__selection-info")
                .add(selectedCount, selectedLabel)
                .build();

        clearButton = UIUtils.Buttons.createCloseButton();
        clearButton.addClickListener(event -> {
            if (clearSelectionAction != null) {
                clearSelectionAction.run();
            }
        });

        bulkActionsSlot = Components.div().styleName("grid-toolbar__bulk-actions")
                .visible(false)
                .build();

        moreButton = Components.button()
                .icon(LineAwesomeIcon.ELLIPSIS_H_SOLID.create())
                .ariaLabel("More bulk actions", "grid_toolbar.more_actions_aria")
                .tooltip("More bulk actions", "grid_toolbar.more_actions_tooltip")
                .tertiary()
                .styleName("grid-toolbar__more")
                .visible(false)
                .build();

        moreMenu.setTarget(moreButton);
        moreMenu.setOpenOnClick(true);

        selectedRow = Components.div()
                .styleNames("grid-toolbar__row", "grid-toolbar__selected")
                .elementConfiguration(el -> {
                    el.setAttribute("role", "region");
                    el.setAttribute("aria-live", "polite");
                    el.setAttribute("aria-label", LocalizationProvider.localize("Bulk actions", "grid_toolbar.selected_row_aria"));
                })
                .add(selectionInfo, clearButton, bulkActionsSlot, moreButton, moreMenu)
                .build();

        if (components != null && components.length > 0) {
            bulkActionsSlot.add(components);
        }
        updateBulkActionsSlotVisibility();
        add(defaultRow, selectedRow);
        setSelectedCount(0);

        this.filterEnabled = false;
        if (filterEnabled) {
            enableFilterUi();
        }
    }

    public void setSearchPlaceholder(Localizable placeholder) {
        Objects.requireNonNull(placeholder, "placeholder must not be null");
        getSearchField().setPlaceholder(LocalizationProvider.localize(placeholder).orElse(""));
    }

    public void setSearchPlaceholder(String placeholder) {
        setSearchPlaceholder(Localizable.builder().message(placeholder).build());
    }

    public void setSearchPlaceholder(String defaultPlaceholder, String messageCode, Object... arguments) {
        setSearchPlaceholder(Localizable.builder().message(defaultPlaceholder).messageCode(messageCode).messageArguments(arguments).build());
    }

    public TextField getSearchField() {return (TextField) searchInput.getComponent();}

    public Input<String> getSearchInput() {return searchInput;}

    public boolean isFilterEnabled() {
        return filterEnabled;
    }

    public void setFilterEnabled(boolean enabled) {
        if (this.filterEnabled == enabled) {
            return;
        }
        if (enabled) {
            enableFilterUi();
        } else {
            disableFilterUi();
        }
    }

    private void ensureFilterUi() {
        if (!filterEnabled) {
            enableFilterUi();
        }
    }

    private void enableFilterUi() {
        if (filterEnabled) {
            return;
        }

        filterButton = Components.button()
                .text("Filter", "grid_toolbar.filter_button")
                .icon(LineAwesomeIcon.FILTER_SOLID.create())
                .ariaLabel("Open filters", "grid_toolbar.filter_aria")
                .tooltip("Open filters", "grid_toolbar.filter_tooltip")
                .tertiaryInline()
                .styleName("grid-toolbar__filter")
                .onClick(event -> openFilterDialog())
                .build();

        filterBadge = Components.span()
                .text("0")
                .styleName("grid-toolbar__badge")
                .elementConfiguration(el -> {
                    el.setAttribute("data-testid", "grid-toolbar-filter-badge");
                    el.setAttribute("aria-label", LocalizationProvider.localize("0 active filters", "grid_toolbar.active_filters_aria", 0));
                })
                .build();

        filterTrigger = Components.div()
                .styleName("grid-toolbar__filter-trigger")
                .add(filterButton, filterBadge)
                .build();

        filterSlot = Components.div().styleName("grid-toolbar__filter-content").build();

        filterCloseButton = UIUtils.Buttons.createCloseButton();
        filterCloseButton.addClickListener(event -> closeFilterDialog());

        filterDialog = UIUtils.createFilterDialog(
                LocalizationProvider.localize("Filter", "grid_toolbar.filter_dialog_title"),
                filterSlot);
        filterDialog.getElement().setAttribute("aria-label", LocalizationProvider.localize("Filter options", "grid_toolbar.filter_dialog_aria"));
        filterDialog.getHeader().add(filterCloseButton);

        defaultRow.add(filterTrigger, filterDialog);
        filterEnabled = true;
    }

    private void disableFilterUi() {
        if (!filterEnabled) {
            return;
        }
        if (defaultRow != null && filterTrigger != null) {
            defaultRow.remove(filterTrigger, filterDialog);
        }
        filterTrigger = null;
        filterButton = null;
        filterBadge = null;
        filterSlot = null;
        filterCloseButton = null;
        filterDialog = null;
        filterEnabled = false;
    }

    public void setFilterContent(Component content) {
        ensureFilterUi();
        filterSlot.removeAll();
        if (content != null) {
            filterSlot.add(content);
        }
    }

    public Div getFilterSlot() {
        ensureFilterUi();
        return filterSlot;
    }

    /**
     * Returns the filter trigger {@link Button} that opens the filter dialog
     * (e.g. for per-user visibility/authorization control).
     */
    public Button getFilterButton() {
        ensureFilterUi();
        return filterButton;
    }

    /**
     * Returns the filter dialog's header close {@link Button}.
     */
    public Button getFilterCloseButton() {
        ensureFilterUi();
        return filterCloseButton;
    }

    private void openFilterDialog() {
        ensureFilterUi();
        filterDialog.open();
    }

    private void closeFilterDialog() {
        if (filterDialog != null) {
            filterDialog.close();
        }
    }

    /**
     * Uses DynamicFilterPanel's built-in Apply filter and Reset controls.
     */
    public void setFilterPanel(DynamicFilterPanel<?> panel) {
        Objects.requireNonNull(panel, "panel must not be null");
        ensureFilterUi();
        setFilterContent(panel);
        panel.addApplyListener(this::closeFilterDialog);
        panel.addFilterChangeListener(event -> setFilterActiveCount(panel.isAnyActive() ? 1 : 0));
        setFilterActiveCount(panel.isAnyActive() ? 1 : 0);
    }

    public Dialog getFilterDialog() {
        ensureFilterUi();
        return filterDialog;
    }

    public void setFilterActiveCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count must not be negative");
        }
        if (!filterEnabled || filterBadge == null) {
            return;
        }
        filterBadge.setText(String.valueOf(count));
        filterBadge.setVisible(count > 0);
        filterBadge.getElement().setAttribute("aria-label",
                                              LocalizationProvider.localize(count + " active filters", "grid_toolbar.active_filters_aria", count));
    }

    public void setPrimaryAction(Component action) {
        if (primaryAction != null) {
            primaryAction.removeClassName("grid-toolbar__primary-action");
            defaultRow.remove(primaryAction);
        }
        primaryAction = action;
        if (action != null) {
            action.addClassName("grid-toolbar__primary-action");
            defaultRow.add(action);
        }
    }

    /**
     * Binds selection mode to a grid. The toolbar then owns the selected count
     * and Clear behavior; changing the selection does not replace grid items.
     */
    public void setSelectionGrid(Grid<?> grid) {
        removeSelectionBindings();
        Objects.requireNonNull(grid, "grid must not be null");
        selectionRegistration = grid.addSelectionListener(event -> setSelectedCount(event.getAllSelectedItems().size()));
        clearSelectionAction = grid::deselectAll;
        setSelectedCount(grid.getSelectedItems().size());
    }

    public void setSelectionListing(BeanListing<?> listing) {
        setSelectionListingInternal(listing);
    }

    public void setSelectionListing(PropertyListing listing) {
        setSelectionListingInternal(listing);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void setSelectionListingInternal(ItemListing<?, ?> listing) {
        removeSelectionBindings();
        Objects.requireNonNull(listing, "listing must not be null");
        Selectable selectable = listing;
        listingSelectionRegistration = selectable.addSelectionListener(event ->
                                                                               setSelectedCount(event.getAllSelectedItems().size()));
        clearSelectionAction = listing::deselectAll;
        setSelectedCount(listing.getSelectedItems().size());
    }

    private void removeSelectionBindings() {
        if (selectionRegistration != null) {
            selectionRegistration.remove();
            selectionRegistration = null;
        }
        if (listingSelectionRegistration != null) {
            listingSelectionRegistration.remove();
            listingSelectionRegistration = null;
        }
    }

    public void setSelectedCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count must not be negative");
        }
        selectedCount.setText(String.valueOf(count));
        selectedLabel.setText(LocalizationProvider.localize("selected", "grid_toolbar.selected_label"));
        selectedRow.getElement().setAttribute("aria-label",
                                              LocalizationProvider.localize(count + " items selected", "grid_toolbar.selected_row_count_aria", count));

        boolean selected = count > 0;
        defaultRow.setVisible(!selected);
        selectedRow.setVisible(selected);
        if (selected) {
            addClassName("grid-toolbar--selected");
        } else {
            removeClassName("grid-toolbar--selected");
        }
    }

    public int getSelectedCount() {return Integer.parseInt(selectedCount.getText());}

    public void setClearSelectionAction(Runnable action) {clearSelectionAction = Objects.requireNonNull(action);}

    public void addBulkAction(Localizable label, Runnable action) {
        addBulkAction(null, label, action, false);
    }

    public void addBulkAction(String label, Runnable action) {
        addBulkAction(null, (label == null) ? null : Localizable.builder().message(label).build(), action, false);
    }

    public void addBulkAction(String defaultLabel, String messageCode, Runnable action) {
        addBulkAction(null, Localizable.builder().message(defaultLabel).messageCode(messageCode).build(), action, false);
    }

    /**
     * Adds a bulk action with an icon, shown both as an inline button (in {@link #getBulkActionsSlot()})
     * and as an item in the overflow "more" menu.
     * @param icon icon shown next to the label (not null)
     */
    public void addBulkAction(VaadinIcon icon, Localizable label, Runnable action) {
        addBulkAction(Objects.requireNonNull(icon, "icon must not be null"), label, action, false);
    }

    public void addBulkAction(VaadinIcon icon, String label, Runnable action) {
        addBulkAction(icon, (label == null) ? null : Localizable.builder().message(label).build(), action);
    }

    public void addBulkAction(VaadinIcon icon, String defaultLabel, String messageCode, Runnable action) {
        addBulkAction(icon, Localizable.builder().message(defaultLabel).messageCode(messageCode).build(), action);
    }

    public void addDestructiveBulkAction(Localizable label, Runnable action) {
        addBulkAction(null, label, action, true);
    }

    public void addDestructiveBulkAction(String label, Runnable action) {
        addBulkAction(null, (label == null) ? null : Localizable.builder().message(label).build(), action, true);
    }

    public void addDestructiveBulkAction(String defaultLabel, String messageCode, Runnable action) {
        addBulkAction(null, Localizable.builder().message(defaultLabel).messageCode(messageCode).build(), action, true);
    }

    /**
     * Adds a destructive bulk action with an icon, shown both as an inline button (in {@link #getBulkActionsSlot()})
     * and as an item in the overflow "more" menu.
     * @param icon icon shown next to the label (not null)
     */
    public void addDestructiveBulkAction(VaadinIcon icon, Localizable label, Runnable action) {
        addBulkAction(Objects.requireNonNull(icon, "icon must not be null"), label, action, true);
    }

    public void addDestructiveBulkAction(VaadinIcon icon, String label, Runnable action) {
        addDestructiveBulkAction(icon, (label == null) ? null : Localizable.builder().message(label).build(), action);
    }

    public void addDestructiveBulkAction(VaadinIcon icon, String defaultLabel, String messageCode, Runnable action) {
        addDestructiveBulkAction(icon, Localizable.builder().message(defaultLabel).messageCode(messageCode).build(), action);
    }

    private void addBulkAction(VaadinIcon icon, Localizable localizableLabel, Runnable action, boolean destructive) {
        Objects.requireNonNull(localizableLabel, "label must not be null");
        Objects.requireNonNull(action, "action must not be null");
        String resolvedLabel = LocalizationProvider.localize(localizableLabel).orElseGet(localizableLabel::getMessage);
        BulkAction bulkAction = new BulkAction(resolvedLabel, action, destructive);
        bulkActions.add(bulkAction);

        ButtonBuilder buttonBuilder = Components.button()
                .text(localizableLabel)
                .styleName("grid-toolbar__bulk-action")
                .onClick(event -> action.run());
        if (icon != null) {
            buttonBuilder.icon(icon);
        }
        if (destructive) {
            buttonBuilder.styleName("grid-toolbar__bulk-action--destructive");
        }
        Button button = buttonBuilder.build();

        bulkActionsSlot.add(button);
        updateBulkActionsSlotVisibility();

        if (icon != null) {
            Div content = Components.div()
                    .styleName("grid-toolbar__options-menu-item")
                    .add(icon.create(), new Span(resolvedLabel))
                    .build();
            moreMenu.addItem(content).addClickListener(event -> action.run());
        } else {
            moreMenu.addItem(resolvedLabel, event -> action.run());
        }
        updateMoreButtonVisibility();
    }

    private void updateBulkActionsSlotVisibility() {
        bulkActionsSlot.setVisible(bulkActionsSlot.getComponentCount() > 0);
    }

    private void updateMoreButtonVisibility() {
        moreButton.setVisible(!moreMenu.getItems().isEmpty());
    }

    public Div getBulkActionsSlot() {return bulkActionsSlot;}

    /**
     * Returns the "Clear selection" {@link Button} shown in the selection row.
     */
    public Button getClearButton() {
        return clearButton;
    }

    /**
     * Returns the bulk-actions overflow ("⋯") {@link Button}, visible once at least one bulk
     * action has been registered via {@code addBulkAction(...)} (e.g. for per-user
     * visibility/authorization control).
     */
    public Button getMoreButton() {
        return moreButton;
    }

    /**
     * Returns the {@link ContextMenu} attached to the bulk-actions overflow button.
     */
    public ContextMenu getMoreMenu() {
        return moreMenu;
    }

    public List<String> getBulkActionLabels() {
        return bulkActions.stream().map(action -> action.label()).toList();
    }

    /**
     * Enables the options menu (cog button) and returns the trigger component.
     * Add menu items via {@link #addOptionsMenuAction(String, Runnable)}.
     */
    public Component enableOptionsMenu() {
        if (optionsMenuButton != null) {
            return optionsMenuButton;
        }

        optionsMenuButton = Components.button()
                .icon(VaadinIcon.COG.create())
                .icon()
                .tertiary()
                .ariaLabel("Grid options", "grid_toolbar.options_menu_aria")
                .tooltip("Grid options", "grid_toolbar.options_menu_tooltip")
                .styleName("grid-toolbar__options")
                .build();

        optionsMenu = Components.contextMenu().build(optionsMenuButton);
        optionsMenu.setOpenOnClick(true);

        optionsMenuSlot = Components.div()
                .styleName("grid-toolbar__options-menu-slot")
                .add(optionsMenuButton, optionsMenu)
                .build();

        defaultRow.add(optionsMenuSlot);
        return optionsMenuButton;
    }

    /**
     * Disables and removes the options menu.
     */
    public void disableOptionsMenu() {
        if (optionsMenuSlot != null && defaultRow != null) {
            defaultRow.remove(optionsMenuSlot);
        }
        optionsMenuButton = null;
        optionsMenu = null;
        optionsMenuSlot = null;
    }

    /**
     * Adds an action item to the options menu.
     * Call {@link #enableOptionsMenu()} first if not already enabled.
     */
    public void addOptionsMenuAction(Localizable label, Runnable action) {
        Objects.requireNonNull(label, "label must not be null");
        Objects.requireNonNull(action, "action must not be null");

        if (optionsMenu == null) {
            enableOptionsMenu();
        }

        optionsMenu.addItem(LocalizationProvider.localize(label).orElseGet(label::getMessage),
                            event -> action.run());
    }

    /**
     * Adds an action item to the options menu.
     * Call {@link #enableOptionsMenu()} first if not already enabled.
     */
    public void addOptionsMenuAction(String label, Runnable action) {
        addOptionsMenuAction((label == null) ? Localizable.builder().message("").build() :
                             Localizable.builder().message(label).build(), action);
    }

    /**
     * Adds an action item to the options menu with i18n support.
     * Call {@link #enableOptionsMenu()} first if not already enabled.
     */
    public void addOptionsMenuAction(String defaultLabel, String messageCode, Runnable action) {
        addOptionsMenuAction(Localizable.builder().message(defaultLabel).messageCode(messageCode).build(), action);
    }

    /**
     * Adds an action item with an icon to the options menu.
     * Call {@link #enableOptionsMenu()} first if not already enabled.
     * The icon may be any icon component, e.g. a {@link com.vaadin.flow.component.icon.Icon} (from
     * {@link VaadinIcon}) or a {@link com.vaadin.flow.component.icon.SvgIcon}
     * (e.g. from {@link org.vaadin.lineawesome.LineAwesomeIcon}).
     */
    public void addOptionsMenuAction(Component icon, Localizable label, Runnable action) {
        Objects.requireNonNull(icon, "icon must not be null");
        Objects.requireNonNull(label, "label must not be null");
        Objects.requireNonNull(action, "action must not be null");

        String resolvedLabel = LocalizationProvider.localize(label).orElseGet(label::getMessage);
        Div content = Components.div()
                .styleName("grid-toolbar__options-menu-item")
                .add(icon, new Span(resolvedLabel))
                .build();

        addOptionsMenuActionWithContent(content, action);
    }

    /**
     * Adds an action item with an icon to the options menu.
     * Call {@link #enableOptionsMenu()} first if not already enabled.
     */
    public void addOptionsMenuAction(Component icon, String label, Runnable action) {
        addOptionsMenuAction(icon, (label == null) ? Localizable.builder().message("").build() :
                             Localizable.builder().message(label).build(), action);
    }

    /**
     * Adds an action item with an icon to the options menu, with i18n support.
     * Call {@link #enableOptionsMenu()} first if not already enabled.
     */
    public void addOptionsMenuAction(Component icon, String defaultLabel, String messageCode, Runnable action) {
        addOptionsMenuAction(icon, Localizable.builder().message(defaultLabel).messageCode(messageCode).build(),
                              action);
    }

    /**
     * Adds a menu item with custom content (icon + label).
     * Call {@link #enableOptionsMenu()} first if not already enabled.
     */
    public void addOptionsMenuActionWithContent(Component content, Runnable action) {
        Objects.requireNonNull(content, "content must not be null");
        Objects.requireNonNull(action, "action must not be null");

        if (optionsMenu == null) {
            enableOptionsMenu();
        }

        optionsMenu.addItem(content).addClickListener(event -> action.run());
    }

    /**
     * Returns the options menu if enabled, otherwise null.
     */
    public ContextMenu getOptionsMenu() {
        return optionsMenu;
    }

    /**
     * Returns the options menu trigger {@link Button}, if the options menu has been enabled
     * (e.g. for per-user visibility/authorization control).
     *
     * @return the options menu button, or {@code null} if not enabled
     */
    public Button getOptionsMenuButton() {
        return optionsMenuButton;
    }

    /**
     * Enable/disable Sort in options menu.
     */
    public void sort(boolean enabled) {
        this.sortEnabled = enabled;
    }

    /**
     * Enable/disable Refresh in options menu.
     */
    public void refresh(boolean enabled) {
        this.refreshEnabled = enabled;
    }

    /**
     * Enable/disable Reset Column Widths in options menu.
     */
    public void resetColumnWidths(boolean enabled) {
        this.resetColumnWidthsEnabled = enabled;
    }

    /**
     * Enable/disable Show/Hide Columns in options menu.
     */
    public void showHideColumns(boolean enabled) {
        this.showHideColumnsEnabled = enabled;
    }

    /**
     * Enable/disable View Mode Toggle in options menu.
     */
    public void viewModeToggle(boolean enabled) {
        this.viewModeToggleEnabled = enabled;
    }

    /**
     * Check if sort is enabled in options menu.
     */
    public boolean isSortEnabled() {
        return sortEnabled;
    }

    /**
     * Check if refresh is enabled in options menu.
     */
    public boolean isRefreshEnabled() {
        return refreshEnabled;
    }

    /**
     * Check if reset column widths is enabled in options menu.
     */
    public boolean isResetColumnWidthsEnabled() {
        return resetColumnWidthsEnabled;
    }

    /**
     * Check if show/hide columns is enabled in options menu.
     */
    public boolean isShowHideColumnsEnabled() {
        return showHideColumnsEnabled;
    }

    /**
     * Check if view mode toggle is enabled in options menu.
     */
    public boolean isViewModeToggleEnabled() {
        return viewModeToggleEnabled;
    }
}
