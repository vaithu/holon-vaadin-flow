package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.GridToolbarConfigurator;
import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.PropertyListing;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.vaadinplus.components.GridToolbar;
import com.holonplatform.vaadin.flow.vaadinplus.components.DynamicFilterPanel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

/** Base fluent configurator for {@link GridToolbar}. */
public abstract class AbstractGridToolbarConfigurator<C extends GridToolbarConfigurator<C>>
        extends AbstractComponentConfigurator<GridToolbar, C>
        implements GridToolbarConfigurator<C> {

    protected AbstractGridToolbarConfigurator(GridToolbar component) {
        super(component);
    }

    @Override protected Optional<HasSize> hasSize() { return Optional.of(getComponent()); }
    @Override protected Optional<HasStyle> hasStyle() { return Optional.of(getComponent()); }
    @Override protected Optional<HasEnabled> hasEnabled() { return Optional.of(getComponent()); }
    @Override protected Optional<HasTooltip> hasTooltip() { return Optional.empty(); }

    @Override public C searchPlaceholder(Localizable placeholder) { getComponent().setSearchPlaceholder(placeholder); return getConfigurator(); }
    @Override public C filterContent(Component content) { getComponent().setFilterEnabled(true); getComponent().setFilterContent(content); return getConfigurator(); }
    @Override public C filterPanel(DynamicFilterPanel<?> panel) { getComponent().setFilterEnabled(true); getComponent().setFilterPanel(panel); return getConfigurator(); }
    @Override public C filterActiveCount(int count) { getComponent().setFilterEnabled(true); getComponent().setFilterActiveCount(count); return getConfigurator(); }
    @Override public C primaryAction(Component action) { getComponent().setPrimaryAction(action); return getConfigurator(); }
    @Override public C selectionGrid(Grid<?> grid) { getComponent().setSelectionGrid(grid); return getConfigurator(); }
    @Override public C selectionListing(BeanListing<?> listing) { getComponent().setSelectionListing(listing); return getConfigurator(); }
    @Override public C selectionListing(PropertyListing listing) { getComponent().setSelectionListing(listing); return getConfigurator(); }
    @Override public C selectedCount(int count) { getComponent().setSelectedCount(count); return getConfigurator(); }
    @Override public C clearSelection(Runnable action) { getComponent().setClearSelectionAction(action); return getConfigurator(); }
    @Override public C bulkAction(Localizable label, Runnable action) { getComponent().addBulkAction(label, action); return getConfigurator(); }
    @Override public C bulkAction(VaadinIcon icon, Localizable label, Runnable action) { getComponent().addBulkAction(icon, label, action); return getConfigurator(); }
    @Override public C destructiveBulkAction(Localizable label, Runnable action) { getComponent().addDestructiveBulkAction(label, action); return getConfigurator(); }
    @Override public C destructiveBulkAction(VaadinIcon icon, Localizable label, Runnable action) { getComponent().addDestructiveBulkAction(icon, label, action); return getConfigurator(); }

    @Override public C optionsMenu(boolean enable) {
        if (enable) {
            getComponent().enableOptionsMenu();
        } else {
            getComponent().disableOptionsMenu();
        }
        return getConfigurator();
    }

    @Override public C optionsMenuAction(Localizable label, Runnable action) { getComponent().addOptionsMenuAction(label, action); return getConfigurator(); }

    @Override public C optionsMenuActionWithContent(Component content, Runnable action) { getComponent().addOptionsMenuActionWithContent(content, action); return getConfigurator(); }

    @Override public C sort(boolean enabled) { getComponent().sort(enabled); return getConfigurator(); }
    @Override public C refresh(boolean enabled) { getComponent().refresh(enabled); return getConfigurator(); }
    @Override public C resetColumnWidths(boolean enabled) { getComponent().resetColumnWidths(enabled); return getConfigurator(); }
    @Override public C showHideColumns(boolean enabled) { getComponent().showHideColumns(enabled); return getConfigurator(); }
    @Override public C viewModeToggle(boolean enabled) { getComponent().viewModeToggle(enabled); return getConfigurator(); }

    @Override public C ariaLabel(String ariaLabel) { getComponent().getElement().setAttribute("aria-label", ariaLabel); return getConfigurator(); }
    @Override public C ariaLabelledBy(String ariaLabelledBy) { getComponent().getElement().setAttribute("aria-labelledby", ariaLabelledBy); return getConfigurator(); }
    @Override public C ariaLabel(Localizable ariaLabel) { getComponent().getElement().setAttribute("aria-label", LocalizationProvider.localize(ariaLabel).orElse("")); return getConfigurator(); }
}