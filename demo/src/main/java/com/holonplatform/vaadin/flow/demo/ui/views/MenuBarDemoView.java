package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.MenuBarBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("MenuBar – Holon Demo")
@Route(value = "menu-bar", layout = DemoMainLayout.class)
public class MenuBarDemoView extends Div {

    public MenuBarDemoView() {
        addClassName("app-view");

        var title = new H1("MenuBar");

        var desc = new Paragraph(
                "MenuBarBuilder provides a fluent API for Vaadin MenuBar with "
                + "top-level items, sub-menus, and click handlers.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(withSubMenuExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var menuBar = MenuBarBuilder.create()
                .withMenuItem("File", e -> Notification.show("File clicked"))
                .withMenuItem("Edit", e -> Notification.show("Edit clicked"))
                .withMenuItem("View", e -> Notification.show("View clicked"))
                .build();

        return new DemoExample("Basic MenuBar", menuBar, """
                MenuBarBuilder.create()
                    .withMenuItem("File", e -> Notification.show("File clicked"))
                    .withMenuItem("Edit", e -> Notification.show("Edit clicked"))
                    .withMenuItem("View", e -> Notification.show("View clicked"))
                    .build();""");
    }

    private DemoExample withSubMenuExample() {
        var menuBar = MenuBarBuilder.create()
                .withMenuItem("File").add()
                .withMenuItem("Edit").add()
                .build();

        // Add sub-menu items after build
        var fileItem = menuBar.getItems().get(0);
        fileItem.getSubMenu().addItem("New", e -> Notification.show("New"));
        fileItem.getSubMenu().addItem("Open", e -> Notification.show("Open"));
        fileItem.getSubMenu().addItem("Save", e -> Notification.show("Save"));

        var editItem = menuBar.getItems().get(1);
        editItem.getSubMenu().addItem("Undo", e -> Notification.show("Undo"));
        editItem.getSubMenu().addItem("Redo", e -> Notification.show("Redo"));

        return new DemoExample("With Sub-Menus", menuBar, """
                var menuBar = MenuBarBuilder.create()
                    .withMenuItem("File").add()
                    .withMenuItem("Edit").add()
                    .build();
                var fileItem = menuBar.getItems().get(0);
                fileItem.getSubMenu().addItem("New", e -> ...);
                fileItem.getSubMenu().addItem("Open", e -> ...);""");
    }
}
