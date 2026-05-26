package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.NotificationBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("Notification – Holon Demo")
@Route(value = "notification", layout = DemoMainLayout.class)
public class NotificationDemoView extends Div {

    public NotificationDemoView() {
        addClassName("app-view");

        var title = new H1("Notification");

        var desc = new Paragraph(
                "NotificationBuilder provides a fluent API for Vaadin Notifications. "
                + "Supports theme variants (success, error, warning, contrast, primary), "
                + "positioning, duration, close button, and icon.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(defaultExample());
        examples.add(variantsExample());
        examples.add(positionsExample());
        examples.add(closeButtonExample());

        add(title, desc, examples);
    }

    private DemoExample defaultExample() {
        var btn = new Button("Show Default Notification", e ->
                NotificationBuilder.create()
                        .text("This is a default notification")
                        .duration(3000)
                        .build()
                        .open());

        return new DemoExample("Default", btn, """
                NotificationBuilder.create()
                    .text("This is a default notification")
                    .duration(3000)
                    .build()
                    .open();""");
    }

    private DemoExample variantsExample() {
        var successBtn = new Button("Success", e ->
                NotificationBuilder.create()
                        .text("Operation completed successfully")
                        .success()
                        .build()
                        .open());

        var errorBtn = new Button("Error", e ->
                NotificationBuilder.create()
                        .text("Something went wrong")
                        .error()
                        .build()
                        .open());

        var warningBtn = new Button("Warning", e ->
                NotificationBuilder.create()
                        .text("Please review your input")
                        .warning()
                        .build()
                        .open());

        var contrastBtn = new Button("Contrast", e ->
                NotificationBuilder.create()
                        .text("Contrast notification")
                        .contrast()
                        .build()
                        .open());

        var primaryBtn = new Button("Primary", e ->
                NotificationBuilder.create()
                        .text("Primary notification")
                        .primary()
                        .build()
                        .open());

        var layout = new HorizontalLayout(successBtn, errorBtn, warningBtn, contrastBtn, primaryBtn);
        return new DemoExample("Theme Variants", layout, """
                NotificationBuilder.create().text("Success").success().build().open();
                NotificationBuilder.create().text("Error").error().build().open();
                NotificationBuilder.create().text("Warning").warning().build().open();
                NotificationBuilder.create().text("Contrast").contrast().build().open();
                NotificationBuilder.create().text("Primary").primary().build().open();""");
    }

    private DemoExample positionsExample() {
        var topCenter = new Button("Top Center", e ->
                NotificationBuilder.create()
                        .text("Top center")
                        .topCenter()
                        .duration(2000)
                        .build()
                        .open());

        var bottomEnd = new Button("Bottom End", e ->
                NotificationBuilder.create()
                        .text("Bottom end")
                        .bottomEnd()
                        .duration(2000)
                        .build()
                        .open());

        var middle = new Button("Middle", e ->
                NotificationBuilder.create()
                        .text("Middle of the screen")
                        .middle()
                        .duration(2000)
                        .build()
                        .open());

        var layout = new HorizontalLayout(topCenter, bottomEnd, middle);
        return new DemoExample("Positions", layout, """
                NotificationBuilder.create().text("Top center").topCenter().duration(2000).build().open();
                NotificationBuilder.create().text("Bottom end").bottomEnd().duration(2000).build().open();
                NotificationBuilder.create().text("Middle").middle().duration(2000).build().open();""");
    }

    private DemoExample closeButtonExample() {
        var btn = new Button("Notification with Close Button", e ->
                NotificationBuilder.create()
                        .text("Close me manually")
                        .closeButton(true)
                        .duration(0)
                        .build()
                        .open());

        return new DemoExample("Close Button", btn, """
                NotificationBuilder.create()
                    .text("Close me manually")
                    .closeButton(true)
                    .duration(0)
                    .build()
                    .open();""");
    }
}
