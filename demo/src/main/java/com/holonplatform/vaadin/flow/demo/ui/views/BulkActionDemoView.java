package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.BulkActionBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("BulkAction – Holon Demo")
@Route(value = "bulk-action", layout = DemoMainLayout.class)
public class BulkActionDemoView extends Div {

    public BulkActionDemoView() {
        addClassName("app-view");

        var title = new H1("BulkAction");

        var desc = new Paragraph(
                "BulkActionBuilder creates a toolbar bar for bulk operations "
                + "on selected grid items — typically showing selection count, "
                + "a select-all checkbox, action menus, and a close button.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(withMenuExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var bar = BulkActionBuilder.create()
                .selected("3 items selected")
                .closeButton(e -> Notification.show("Closed"))
                .build();

        return new DemoExample("Basic BulkAction Bar", bar, """
                BulkActionBuilder.create()
                    .selected("3 items selected")
                    .closeButton(e -> Notification.show("Closed"))
                    .build();""");
    }

    private DemoExample withMenuExample() {
        var bar = BulkActionBuilder.create()
                .selected(5)
                .closeButton(e -> Notification.show("Dismissed"))
                .build();

        return new DemoExample("With Count", bar, """
                BulkActionBuilder.create()
                    .selected(5)
                    .closeButton(e -> Notification.show("Dismissed"))
                    .build();""");
    }
}
