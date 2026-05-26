package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.BeanListing;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.ShowAndHideColumns;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.popover.PopoverPosition;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class DefaultShowAndHideColumns<T> implements ShowAndHideColumns<T> {

    private Button showHideBtn;
    private Popover popover;
    private final Function<List<Grid.Column<T>>,List<String>> allColumns = columns -> columns.stream().map(Grid.Column::getKey).toList();

    private void createPopOver(CheckboxGroup<String> group, HorizontalLayout footer) {

        this.showHideBtn = Components.button().icon(VaadinIcon.GRID_H).ariaLabel("Show / hide columns").build();
        this.showHideBtn.addThemeVariants(ButtonVariant.LUMO_ICON);

        this.popover = new Popover();
        popover.setModal(true);
        popover.setBackdropVisible(true);
        popover.setPosition(PopoverPosition.BOTTOM_END);
        popover.setTarget(this.showHideBtn );

        Div heading = Components.div().add(Components.span().text("Configure columns").build()).build();
        heading.getStyle().set("font-weight", "600");
        heading.getStyle().set("padding", "var(--lumo-space-xs)");

        popover.add(heading, group, footer);

    }

    private static  HorizontalLayout getHorizontalLayout(List<String> allColumns, Set<String> defaultColumns, CheckboxGroup<String> group) {

        group.setValue(defaultColumns);

        Button showAll = Components.button().text("Show all").withClickListener(e -> group.setValue(new HashSet<>(allColumns))).build();
        showAll.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button reset = Components.button().text("Reset").withClickListener(e -> group.setValue(defaultColumns)).build();
        reset.addThemeVariants(ButtonVariant.LUMO_SMALL);

        HorizontalLayout footer = Components.hl().add(showAll, reset).build();
        footer.setSpacing(false);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        return footer;
    }

    private static  CheckboxGroup<String> createCheckBoxGroup(List<String> allColumns) {
        CheckboxGroup<String> group = new CheckboxGroup<>();
        group.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        group.setItems(allColumns);
        group.setItemLabelGenerator((item) -> {
            String label = StringUtils
                    .join(StringUtils.splitByCharacterTypeCamelCase(item), " ");
            return StringUtils.capitalize(label.toLowerCase());
        });
        return group;
    }


    @Override
    public void showAndHideColumns(BeanListing<T> beanListing) {

        List<String> allColumns = this.allColumns.apply(beanListing.getAllColumns());
        Set<String> defaultColumns = new HashSet<>(beanListing.getVisibleColumns());

        final CheckboxGroup<String> group = createCheckBoxGroup(allColumns);
        group.addValueChangeListener((e) -> {
            allColumns.forEach((key) -> {
                beanListing.setColumnVisible(key, e.getValue().contains(key));
            });
        });

        final HorizontalLayout footer = getHorizontalLayout(allColumns, defaultColumns, group);

        createPopOver(group,footer);
    }

    @Override
    public void showAndHideColumns(Grid<T> grid) {
        final List<Grid.Column<T>> columns = grid.getColumns();
        List<String> allColumns = this.allColumns.apply(columns);

        List<String> visibleColumns = grid.getColumns().stream()
                .filter(Component::isVisible)
                .map(Grid.Column::getKey) // Assuming getKey() returns the property name for the column
                .toList();

        Set<String> defaultColumns = new HashSet<>(visibleColumns);

        final CheckboxGroup<String> group = createCheckBoxGroup(allColumns);
        group.addValueChangeListener((e) -> {
            allColumns.forEach((key) -> {
                grid.getColumnByKey(key).setVisible(e.getValue().contains(key));
            });
        });

        final HorizontalLayout footer = getHorizontalLayout(allColumns, defaultColumns, group);

        createPopOver(group,footer);
    }

    @Override
    public Popover getPopover() {
        return popover;
    }

    @Override
    public Button getShowHideBtn() {
        return showHideBtn;
    }
}
