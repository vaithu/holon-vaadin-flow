package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.HeaderBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.BreadcrumbItem;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link Header} component via Holon's fluent
 * {@link HeaderBuilder}.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic header with title</li>
 *   <li>Header with actions (edit, refresh, new, close)</li>
 *   <li>Header with breadcrumb and details</li>
 *   <li>Header with tabs</li>
 * </ol>
 */
@PageTitle("Header – Holon Demo")
@Route(value = "header", layout = DemoMainLayout.class)
public class HeaderDemoView extends Div {

    public HeaderDemoView() {
        addClassName("app-view");

        var title = new H1("Header");

        var desc = new Paragraph(
                "Semantic page header with prefix, breadcrumb, details, actions, "
                + "and tab slots — all via the Holon builder pattern.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(actionsExample());
        examples.add(breadcrumbExample());
        examples.add(tabsExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample basicExample() {
        var preview = new Div();

        Header header = HeaderBuilder.create("Dashboard")
                .build();

        preview.add(header);

        return new DemoExample("Basic Header",
                preview,
                """
                HeaderBuilder.create("Dashboard")
                    .build();
                """);
    }

    private DemoExample actionsExample() {
        var preview = new Div();

        Header header = HeaderBuilder.create("Products")
                .newBtn(btn -> btn.text("New Product")
                        .withClickListener(e -> Notification.show("New clicked")))
                .refresh(btn -> btn.withClickListener(e -> Notification.show("Refreshed")))
                .edit(btn -> btn.text("Edit")
                        .withClickListener(e -> Notification.show("Edit clicked")))
                .build();

        preview.add(header);

        return new DemoExample("Header with Actions",
                preview,
                """
                HeaderBuilder.create("Products")
                    .newBtn(btn -> btn.text("New Product")
                        .withClickListener(e -> Notification.show("New clicked")))
                    .refresh(btn -> btn.withClickListener(e -> Notification.show("Refreshed")))
                    .edit(btn -> btn.text("Edit")
                        .withClickListener(e -> Notification.show("Edit clicked")))
                    .build();
                """);
    }

    private DemoExample breadcrumbExample() {
        var preview = new Div();

        Header header = HeaderBuilder.create("Order #12345")
                .breadcrumb(
                        new BreadcrumbItem(new Span("Home")),
                        new BreadcrumbItem(new Span("Orders")),
                        new BreadcrumbItem(new Span("#12345")))
                .details(new Span("Status: Processing"), new Span("Date: 2026-04-16"))
                .withoutBorder()
                .build();

        preview.add(header);

        return new DemoExample("Breadcrumb & Details",
                preview,
                """
                HeaderBuilder.create("Order #12345")
                    .breadcrumb(
                        new BreadcrumbItem(new Span("Home")),
                        new BreadcrumbItem(new Span("Orders")),
                        new BreadcrumbItem(new Span("#12345")))
                    .details(new Span("Status: Processing"), new Span("Date: 2026-04-16"))
                    .withoutBorder()
                    .build();
                """);
    }

    private DemoExample tabsExample() {
        var preview = new Div();

        Header header = HeaderBuilder.create("Analytics")
                .tabs(new Tab("Overview"), new Tab("Revenue"), new Tab("Traffic"))
                .actions(new Span("Last 30 days"))
                .build();

        preview.add(header);

        return new DemoExample("Header with Tabs",
                preview,
                """
                HeaderBuilder.create("Analytics")
                    .tabs(new Tab("Overview"), new Tab("Revenue"), new Tab("Traffic"))
                    .actions(new Span("Last 30 days"))
                    .build();
                """);
    }
}
