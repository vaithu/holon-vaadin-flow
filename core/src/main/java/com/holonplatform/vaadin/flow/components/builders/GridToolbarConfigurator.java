package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridToolbar;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;

/** Fluent configuration for a {@link GridToolbar}. */
public interface GridToolbarConfigurator<C extends GridToolbarConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C>, HasAriaLabelConfigurator<C>, HasPlaceholderConfigurator<C> {

    C searchPlaceholder(Localizable placeholder);

    default C searchPlaceholder(String placeholder) {
        return searchPlaceholder((placeholder == null) ? null : Localizable.builder().message(placeholder).build());
    }

    default C searchPlaceholder(String defaultPlaceholder, String messageCode, Object... arguments) {
        return searchPlaceholder(Localizable.builder().message((defaultPlaceholder == null) ? "" : defaultPlaceholder)
                .messageCode(messageCode).messageArguments(arguments).build());
    }

    @Override
    default C placeholder(String placeholder) {
        return searchPlaceholder(placeholder);
    }

    @Override
    default C placeholder(Localizable placeholder) {
        return searchPlaceholder(placeholder);
    }

    C filterContent(Component content);

    C filterPanel(DynamicFilterPanel<?> panel);

    C filterActiveCount(int count);

    C primaryAction(Component action);

    C selectionGrid(Grid<?> grid);
    C selectionListing(BeanListing<?> listing);
    C selectionListing(PropertyListing listing);

    C selectedCount(int count);

    C clearSelection(Runnable action);

    C bulkAction(Localizable label, Runnable action);

    default C bulkAction(String label, Runnable action) {
        return bulkAction((label == null) ? null : Localizable.builder().message(label).build(), action);
    }

    default C bulkAction(String defaultLabel, String messageCode, Runnable action) {
        return bulkAction(Localizable.builder().message((defaultLabel == null) ? "" : defaultLabel)
                .messageCode(messageCode).build(), action);
    }

    C bulkAction(VaadinIcon icon, Localizable label, Runnable action);

    default C bulkAction(VaadinIcon icon, String label, Runnable action) {
        return bulkAction(icon, (label == null) ? null : Localizable.builder().message(label).build(), action);
    }

    default C bulkAction(VaadinIcon icon, String defaultLabel, String messageCode, Runnable action) {
        return bulkAction(icon, Localizable.builder().message((defaultLabel == null) ? "" : defaultLabel)
                .messageCode(messageCode).build(), action);
    }

    C destructiveBulkAction(Localizable label, Runnable action);

    default C destructiveBulkAction(String label, Runnable action) {
        return destructiveBulkAction((label == null) ? null : Localizable.builder().message(label).build(), action);
    }

    default C destructiveBulkAction(String defaultLabel, String messageCode, Runnable action) {
        return destructiveBulkAction(Localizable.builder().message((defaultLabel == null) ? "" : defaultLabel)
                .messageCode(messageCode).build(), action);
    }

    C destructiveBulkAction(VaadinIcon icon, Localizable label, Runnable action);

    default C destructiveBulkAction(VaadinIcon icon, String label, Runnable action) {
        return destructiveBulkAction(icon, (label == null) ? null : Localizable.builder().message(label).build(), action);
    }

    default C destructiveBulkAction(VaadinIcon icon, String defaultLabel, String messageCode, Runnable action) {
        return destructiveBulkAction(icon, Localizable.builder().message((defaultLabel == null) ? "" : defaultLabel)
                .messageCode(messageCode).build(), action);
    }

    // Options menu methods
    C optionsMenu(boolean enable);

    default C optionsMenu() {
        return optionsMenu(true);
    }

    C optionsMenuAction(Localizable label, Runnable action);

    default C optionsMenuAction(String label, Runnable action) {
        return optionsMenuAction((label == null) ? null : Localizable.builder().message(label).build(), action);
    }

    default C optionsMenuAction(String defaultLabel, String messageCode, Runnable action) {
        return optionsMenuAction(Localizable.builder().message((defaultLabel == null) ? "" : defaultLabel)
                .messageCode(messageCode).build(), action);
    }

    C optionsMenuActionWithContent(Component content, Runnable action);

    // Configuration methods for standard grid operations
    C sort(boolean enabled);
    C refresh(boolean enabled);
    C resetColumnWidths(boolean enabled);
    C showHideColumns(boolean enabled);
    C viewModeToggle(boolean enabled);
}