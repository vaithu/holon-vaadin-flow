package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.ContextMenuBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


@PageTitle("ContextMenu – Holon Demo")
@Route(value = "context-menu", layout = DemoMainLayout.class)
public class ContextMenuDemoView extends Div {

    public ContextMenuDemoView() {
        addClassName("app-view");

        var title = new H1("ContextMenu");

        var desc = new Paragraph(
                "ContextMenuBuilder provides a fluent API for Vaadin ContextMenu. "
                + "Right-click or long-press the target component to open the menu.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(openOnClickExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        var target = new Span("Right-click me");
        target.getElement().getStyle().set("padding", "var(--space-m)");
        target.getElement().getStyle().set("border", "1px dashed var(--color-border)");
        target.getElement().getStyle().set("cursor", "context-menu");

        ContextMenuBuilder.create()
                .withItem("Cut", e -> Notification.show("Cut"))
                .withItem("Copy", e -> Notification.show("Copy"))
                .withItem("Paste", e -> Notification.show("Paste"))
                .build(target);

        return new DemoExample("Basic ContextMenu", target, """
                var target = new Span("Right-click me");
                ContextMenuBuilder.create()
                    .withItem("Cut", e -> Notification.show("Cut"))
                    .withItem("Copy", e -> Notification.show("Copy"))
                    .withItem("Paste", e -> Notification.show("Paste"))
                    .build(target);""");
    }

    private DemoExample openOnClickExample() {
        var target = new Span("Click me (left-click)");
        target.getElement().getStyle().set("padding", "var(--space-m)");
        target.getElement().getStyle().set("border", "1px dashed var(--color-border)");
        target.getElement().getStyle().set("cursor", "pointer");

        ContextMenuBuilder.create()
                .openOnClick(true)
                .withItem("Option A", e -> Notification.show("A"))
                .withItem("Option B", e -> Notification.show("B"))
                .build(target);

        return new DemoExample("Open on Left Click", target, """
                ContextMenuBuilder.create()
                    .openOnClick(true)
                    .withItem("Option A", e -> Notification.show("A"))
                    .withItem("Option B", e -> Notification.show("B"))
                    .build(target);""");
    }
}
