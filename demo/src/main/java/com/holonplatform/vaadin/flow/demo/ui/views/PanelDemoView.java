package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;
import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.iyensoft.vaadin.flow.components.Panel;
import com.iyensoft.vaadin.flow.components.builders.PanelBuilder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo page for the widget-style {@link Panel} component.
 */
@PageTitle("Panel – Holon Demo")
@Route(value = "panel", layout = DemoMainLayout.class)
public class PanelDemoView extends Div {

    public PanelDemoView() {
        addClassName("app-view");

        H1 title = new H1("Panel / Widget");
        Paragraph description = new Paragraph(
                "Panel is a lightweight shell for grouping a header, content, and footer.");

        Div examples = new Div();
        examples.addClassName("demo-examples");
        examples.add(
                builderWidgetExample(),
                configuredWidgetExample(),
                footerWidgetExample()
        );

        add(title, description, examples);
    }

    private DemoExample builderWidgetExample() {
        Button primary = new Button("Open details");
        primary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button secondary = new Button("Archive");

        Footer footer = new Footer();
        footer.setMeta(new Span("Status: Ready"), new Span("Owner: Platform Team"));
        footer.setLegal(new Span("Internal use only"));
        footer.setActions(new Button("Footer Action"));

        Panel panel = PanelBuilder.create()
                .header()
                    .heading("Deployment status")
                    .details(new Span("Last updated just now"))
                    .actions(primary, secondary)
                    .add()
                .content(cell("A widget should present a clear hierarchy: title, body, actions, and supporting metadata."))
                .footer(footer)
                .build();

        return new DemoExample("Widget panel — builder", panel, """
                Button primary = new Button("Open details");
                primary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
                Button secondary = new Button("Archive");

                Footer footer = new Footer();
                footer.setMeta(new Span("Status: Ready"), new Span("Owner: Platform Team"));
                footer.setLegal(new Span("Internal use only"));
                footer.setActions(new Button("Footer Action"));

                Panel panel = PanelBuilder.create()
                        .styleName("panel-demo")
                        .header()
                            .heading("Deployment status")
                            .details(new Span("Last updated just now"))
                            .actions(primary, secondary)
                            .add()
                        .content(bodyContent)
                        .footer(footer)
                        .build();
                """);
    }

    private DemoExample configuredWidgetExample() {
        Button acknowledge = new Button("Acknowledge");
        acknowledge.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button snooze = new Button("Snooze");

        Panel panel = new Panel();
        Components.panel(panel)
                .styleName("panel-demo--accent")
                .header()
                    .heading("Incident summary")
                    .details(new Span("Live update"))
                    .actions(acknowledge, snooze)
                    .add()
                .content(cell("Use the configurator when the panel is already created and you want to set its widget slots."))
                .footer()
                    .meta(new Span("Severity: Medium"), new Span("Channel: Ops"))
                    .add();

        return new DemoExample("Widget panel — configurator", panel, """
                Panel panel = new Panel();
                Components.panel(panel)
                        .styleName("panel-demo--accent")
                        .header()
                            .heading("Incident summary")
                            .details(new Span("Live update"))
                            .actions(acknowledge, snooze)
                            .add()
                        .content(bodyContent)
                        .footer()
                            .meta(new Span("Severity: Medium"), new Span("Channel: Ops"))
                            .add();
                """);
    }

    private DemoExample footerWidgetExample() {
        Header header = new Header("Release note");
        header.setDetails(new Span("v10.0.2-SNAPSHOT"));

        Footer footer = new Footer();
        footer.setMeta(new Span("Published today"), new Span("Platform team"));
        footer.setLegal(new Span("All rights reserved"));

        Panel panel = PanelBuilder.create()
                .styleName("panel-demo--footer")
                .header(header)
                .content(cell("The footer slot is useful for summary metadata or supporting legal text."))
                .footer(footer)
                .build();

        return new DemoExample("Widget panel — footer slot", panel, """
                Footer footer = new Footer();
                footer.setMeta(new Span("Published today"), new Span("Platform team"));
                footer.setLegal(new Span("All rights reserved"));

                Panel panel = PanelBuilder.create()
                        .styleName("panel-demo--footer")
                        .header(new Header("Release note"))
                        .content(bodyContent)
                        .footer(footer)
                        .build();
                """);
    }

    private static Div cell(String text) {
        return new Div(new Span(text));
    }
}












