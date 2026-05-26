package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("Button – Holon Demo")
@Route(value = "button", layout = DemoMainLayout.class)
public class ButtonDemoView extends Div {

    public ButtonDemoView() {
        addClassName("app-view");

        var title = new H1("Button");

        var desc = new Paragraph(
                "ButtonBuilder provides a fluent API for creating Vaadin Buttons with "
                + "theme variants (primary, secondary, tertiary, error, success, contrast), "
                + "sizes, icons, and click handling.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(variantsExample());
        examples.add(sizesExample());
        examples.add(iconButtonExample());
        examples.add(deleteButtonExample());

        add(title, desc, examples);
    }

    private DemoExample variantsExample() {
        var primary = ButtonBuilder.create().text("Primary").primary()
                .onClick(e -> Notification.show("Primary clicked")).build();
        var secondary = ButtonBuilder.create().text("Secondary").secondary()
                .onClick(e -> Notification.show("Secondary clicked")).build();
        var tertiary = ButtonBuilder.create().text("Tertiary").tertiary()
                .onClick(e -> Notification.show("Tertiary clicked")).build();
        var error = ButtonBuilder.create().text("Error").error().build();
        var success = ButtonBuilder.create().text("Success").success().build();
        var contrast = ButtonBuilder.create().text("Contrast").contrast().build();

        var layout = new HorizontalLayout(primary, secondary, tertiary, error, success, contrast);
        return new DemoExample("Variants", layout, """
                ButtonBuilder.create().text("Primary").primary()
                    .onClick(e -> Notification.show("Clicked")).build();
                ButtonBuilder.create().text("Secondary").secondary().build();
                ButtonBuilder.create().text("Tertiary").tertiary().build();
                ButtonBuilder.create().text("Error").error().build();
                ButtonBuilder.create().text("Success").success().build();
                ButtonBuilder.create().text("Contrast").contrast().build();""");
    }

    private DemoExample sizesExample() {
        var small = ButtonBuilder.create().text("Small").small().build();
        var normal = ButtonBuilder.create().text("Normal").normal().build();
        var large = ButtonBuilder.create().text("Large").large().build();

        var layout = new HorizontalLayout(small, normal, large);
        return new DemoExample("Sizes", layout, """
                ButtonBuilder.create().text("Small").small().build();
                ButtonBuilder.create().text("Normal").normal().build();
                ButtonBuilder.create().text("Large").large().build();""");
    }

    private DemoExample iconButtonExample() {
        var iconBtn = ButtonBuilder.create().text("Add")
                .icon(VaadinIcon.PLUS).primary().build();
        var iconOnly = ButtonBuilder.create()
                .icon(VaadinIcon.SEARCH).icon().build();

        var layout = new HorizontalLayout(iconBtn, iconOnly);
        return new DemoExample("With Icons", layout, """
                ButtonBuilder.create().text("Add")
                    .icon(VaadinIcon.PLUS).primary().build();
                ButtonBuilder.create()
                    .icon(VaadinIcon.SEARCH).icon().build();""");
    }

    private DemoExample deleteButtonExample() {
        var deleteBtn = ButtonBuilder.createDelBtn()
                .text("Delete")
                .onClick(e -> Notification.show("Deleted"))
                .build();

        return new DemoExample("Delete Button", deleteBtn, """
                ButtonBuilder.createDelBtn()
                    .text("Delete")
                    .onClick(e -> Notification.show("Deleted"))
                    .build();""");
    }
}
