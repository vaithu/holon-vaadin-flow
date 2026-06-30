package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Footer – Holon Demo")
@Route(value = "footer", layout = DemoMainLayout.class)
public class FooterDemoView extends Div {

    public FooterDemoView() {
        addClassName("app-view");

        var title = new H1("Footer");

        var desc = new Paragraph(
                "FooterBuilder creates a responsive page footer with prefix, details, action, "
                + "meta and legal areas, plus semantic background and border controls.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(backgroundExample());

        add(title, desc, examples);
    }

    private DemoExample basicExample() {
        Footer footer = Components.footer()
                .prefix(new Span("Holon Platform"), new Span("Enterprise UI building blocks"))
                .details(new Span("Docs"), new Span("Support"), new Span("Changelog"))
                .actions(new Button("Contact"), new Button("GitHub"))
                .meta(new Span("© 2026 Holon Platform"))
                .legal(new Span("Privacy"), new Span("Terms"))
                .build();

        return new DemoExample("Responsive Footer", footer, """
                Footer footer = Components.footer()
                    .prefix(new Span("Holon Platform"), new Span("Enterprise UI building blocks"))
                    .details(new Span("Docs"), new Span("Support"), new Span("Changelog"))
                    .actions(new Button("Contact"), new Button("GitHub"))
                    .meta(new Span("© 2026 Holon Platform"))
                    .legal(new Span("Privacy"), new Span("Terms"))
                    .build();
                """);
    }

    private DemoExample backgroundExample() {
        Footer footer = Components.footer()
                .prefix(new Span("Brand"))
                .details(new Span("Status"), new Span("About"))
                .actions(new Button("Subscribe"))
                .meta(new Span("Built with the Holon builder stack"))
                .background(Color.Background.PRIMARY_10)
                .withoutBorder()
                .build();

        return new DemoExample("Background and Border Controls", footer, """
                Footer footer = Components.footer()
                    .prefix(new Span("Brand"))
                    .details(new Span("Status"), new Span("About"))
                    .actions(new Button("Subscribe"))
                    .meta(new Span("Built with the Holon builder stack"))
                    .background(Color.Background.PRIMARY_10)
                    .withoutBorder()
                    .build();
                """);
    }
}