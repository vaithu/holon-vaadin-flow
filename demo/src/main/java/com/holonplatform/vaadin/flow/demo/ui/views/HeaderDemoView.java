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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.avatar.AvatarVariant;
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
 *   <li>Mobile-friendly product header with breadcrumb, avatar, details, actions, and tabs</li>
 *   <li>Header with actions (edit, refresh, new, close)</li>
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
                "Responsive page header with top breadcrumbs, a middle content row, "
                + "and tabs below — all via the Holon builder pattern.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(productHeaderExample());
        examples.add(actionsExample());
        examples.add(tabsExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample productHeaderExample() {
        var preview = new Div();

        Header header = HeaderBuilder.create("Temperature Sensor IP67").build();
        header.setAvatar("4A1", AvatarVariant.LUMO_LARGE);
        header.setBreadcrumb(
                new BreadcrumbItem(new Span("Catalog")),
                new BreadcrumbItem(new Span("Products")),
                new BreadcrumbItem(new Span("Sensors")),
                new BreadcrumbItem(new Span("Temperature Sensor IP67"))
        );
        header.setDetails(
                new Span("PT-SEN-T2"),
                new Span("PrahaTech s.r.o."),
                new Span("Sensors > Industrial")
        );
        header.setActions(
                new Button("Duplicate"),
                new Button("Export"),
                new Button("Edit")
        );
        header.setTabs(new Tab("Overview"), new Tab("Specs"), new Tab("Stock"));
        header.withoutBorder();

        preview.add(header);

        return new DemoExample("Mobile-friendly Product Header",
                preview,
                """
                Header header = HeaderBuilder.create("Temperature Sensor IP67").build();
                header.setAvatar("4A1", AvatarVariant.LUMO_LARGE);
                header.setBreadcrumb(
                    new BreadcrumbItem(new Span("Catalog")),
                    new BreadcrumbItem(new Span("Products")),
                    new BreadcrumbItem(new Span("Sensors")),
                    new BreadcrumbItem(new Span("Temperature Sensor IP67"))
                );
                header.setDetails(
                    new Span("PT-SEN-T2"),
                    new Span("PrahaTech s.r.o."),
                    new Span("Sensors > Industrial")
                );
                header.setActions(
                    new Button("Duplicate"),
                    new Button("Export"),
                    new Button("Edit")
                );
                header.setTabs(new Tab("Overview"), new Tab("Specs"), new Tab("Stock"));
                header.withoutBorder();
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
