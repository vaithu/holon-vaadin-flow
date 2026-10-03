package com.holonplatform.vaadin.flow.components.utils;

import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.components.css.WhiteSpace;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class CoreUIUtils {

    private CoreUIUtils() {
    }

    public static Div div(String className, Component... children) {
        StyleSheetSupport.require("utilities.css", "layout.css");
        Div div = new Div(children);
        if (className != null && !className.isEmpty()) {
            div.addClassName(className);
        }
        return div;
    }

    public static Component[] toComponents(HasComponent[] components) {
        StyleSheetSupport.require("utilities.css");
        return Arrays.stream(components).map(HasComponent::getComponent).toArray(Component[]::new);
    }

    public static LabelBuilder<H4> createH4(String title) {
        StyleSheetSupport.require("utilities.css");
        return LabelBuilder.h4()
                .text(title)
                .title(title)
                .styleNames(titleStyles());
    }

    public static String[] titleStyles() {
        return new String[]{"color-text-primary", "padding-small"};
    }

    public static String[] borderStyles() {
        return new String[]{"border-bottom", "border-color-contrast-10"};
    }

    public static void setWhiteSpace(WhiteSpace whiteSpace, Component... components) {
        for (Component component : components) {
            component.getElement().getStyle().set("white-space", whiteSpace.getValue());
        }
    }

    public static MenuBar createMenuToggle(Map<Grid.Column<?>, String> toggleableColumns) {
        MenuBar menuBar = new MenuBar();
        menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        MenuItem menuItem = menuBar.addItem(VaadinIcon.GRID_H.create(),
                (ComponentEventListener<ClickEvent<MenuItem>>) null);
        SubMenu subMenu = menuItem.getSubMenu();

        toggleableColumns.forEach((column, header) -> {
            Checkbox checkbox = new Checkbox(header);
            checkbox.setValue(column.isVisible());
            checkbox.addValueChangeListener(event -> column.setVisible(event.getValue()));
            subMenu.addItem(checkbox, (ComponentEventListener<ClickEvent<MenuItem>>) null);
        });
        return menuBar;
    }

    public static List<FormLayout.ResponsiveStep> updateColumnValues(
            List<FormLayout.ResponsiveStep> steps, int sumOfAllColumnsSize) {
        List<FormLayout.ResponsiveStep> updatedSteps = new ArrayList<>();
        for (FormLayout.ResponsiveStep step : steps) {
            var jsonObject = step.toJson();
            if (jsonObject.has("columns")) {
                double columns = jsonObject.get("columns").asDouble();
                if (columns < sumOfAllColumnsSize) {
                    jsonObject.put("columns", sumOfAllColumnsSize);
                }
            }
            updatedSteps.add(step.readJson(jsonObject));
        }
        return updatedSteps;
    }

    public static int parseMinWidth(FormLayout.ResponsiveStep step) {
        String minWidth = org.apache.commons.lang3.StringUtils.substringAfter(step.toJson().toString(), ":");
        minWidth = org.apache.commons.lang3.StringUtils.substringBetween(minWidth, "\"", "\"");
        if (minWidth.endsWith("px")) {
            return Integer.parseInt(minWidth.replace("px", ""));
        }
        return 0;
    }

    public static final class Icon {

        private Icon() {
        }

        public static com.vaadin.flow.component.icon.Icon createStatusIcon(String status) {
            boolean isAvailable = "Available".equals(status);
            com.vaadin.flow.component.icon.Icon icon = isAvailable
                    ? VaadinIcon.CHECK.create()
                    : VaadinIcon.CLOSE.create();
            icon.getElement().getThemeList().add(isAvailable ? "badge success" : "badge error");
            icon.addClassName("padding-xsmall");
            return icon;
        }
    }
}
