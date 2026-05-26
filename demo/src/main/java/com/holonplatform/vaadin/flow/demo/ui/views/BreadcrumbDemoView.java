package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link Breadcrumb} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Manual composition (explicit separators)</li>
 *   <li>Automatic separators via {@code addWithSeparators()}</li>
 *   <li>Custom separator icon</li>
 *   <li>Ellipsis for long trails</li>
 * </ol>
 */
@PageTitle("Breadcrumb – Holon Demo")
@Route(value = "breadcrumb", layout = DemoMainLayout.class)
public class BreadcrumbDemoView extends Div {

    public BreadcrumbDemoView() {
        addClassName("app-view");

        var title = new H1("Breadcrumb");

        var desc = new Paragraph(
                "Accessible navigation trail rendered as a <nav> wrapping an <ol>. " +
                "BreadcrumbItem wraps a RouterLink; BreadcrumbPage marks the current (non-linked) crumb. " +
                "Separators can be inserted manually or automatically. " +
                "Custom separator icons and an ellipsis item are also supported.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(manualExample());
        examples.add(autoSeparatorsExample());
        examples.add(customSeparatorExample());
        examples.add(withEllipsisExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample manualExample() {
        var bc = new Breadcrumb(
                new BreadcrumbItem(new com.vaadin.flow.component.html.Anchor("/", "Home")),
                new BreadcrumbSeparator(),
                new BreadcrumbItem(new com.vaadin.flow.component.html.Anchor("/components", "Components")),
                new BreadcrumbSeparator(),
                new BreadcrumbPage("Breadcrumb")
        );

        return new DemoExample("Manual Composition", bc, """
                // Full manual control: you supply every separator explicitly.
                Breadcrumb bc = new Breadcrumb(
                    new BreadcrumbItem("Home",       HomeView.class),
                    new BreadcrumbSeparator(),
                    new BreadcrumbItem("Components", ComponentsView.class),
                    new BreadcrumbSeparator(),
                    new BreadcrumbPage("Breadcrumb")    // current page — not a link
                );
                """);
    }

    private DemoExample autoSeparatorsExample() {
        var bc = new Breadcrumb();
        bc.addWithSeparators(
                new BreadcrumbItem(new com.vaadin.flow.component.html.Anchor("/", "Home")),
                new BreadcrumbItem(new com.vaadin.flow.component.html.Anchor("/docs", "Docs")),
                new BreadcrumbItem(new com.vaadin.flow.component.html.Anchor("/docs/ui", "UI")),
                new BreadcrumbPage("Breadcrumb")
        );

        return new DemoExample("Automatic Separators", bc, """
                // addWithSeparators() inserts a BreadcrumbSeparator between every item.
                Breadcrumb bc = new Breadcrumb();
                bc.addWithSeparators(
                    new BreadcrumbItem("Home",      HomeView.class),
                    new BreadcrumbItem("Docs",      DocsView.class),
                    new BreadcrumbItem("UI",        UiView.class),
                    new BreadcrumbPage("Breadcrumb")
                );
                """);
    }

    private DemoExample customSeparatorExample() {
        var bc = new Breadcrumb();
        // Use a chevron-right character as custom separator
        bc.setSeparatorSupplier(() -> {
            var sep = new BreadcrumbSeparator();
            sep.getElement().setText("›");
            return sep;
        });
        bc.addWithSeparators(
                new BreadcrumbItem(new com.vaadin.flow.component.html.Anchor("/", "Home")),
                new BreadcrumbItem(new com.vaadin.flow.component.html.Anchor("/settings", "Settings")),
                new BreadcrumbPage("Profile")
        );

        return new DemoExample("Custom Separator", bc, """
                // Override the separator factory with any icon or character.
                Breadcrumb bc = new Breadcrumb();
                bc.setSeparatorSupplier(
                    () -> new BreadcrumbSeparator(MaterialSymbol.CHEVRON_RIGHT)
                );
                bc.addWithSeparators(
                    new BreadcrumbItem("Home",     HomeView.class),
                    new BreadcrumbItem("Settings", SettingsView.class),
                    new BreadcrumbPage("Profile")
                );
                """);
    }

    private DemoExample withEllipsisExample() {
        var bc = new Breadcrumb();
        bc.addWithSeparators(
                new BreadcrumbItem(new com.vaadin.flow.component.html.Anchor("/", "Home")),
                new BreadcrumbEllipsis(),
                new BreadcrumbItem(new com.vaadin.flow.component.html.Anchor("/components", "Components")),
                new BreadcrumbPage("Breadcrumb")
        );

        return new DemoExample("With Ellipsis", bc, """
                // BreadcrumbEllipsis collapses intermediate crumbs in long trails.
                Breadcrumb bc = new Breadcrumb();
                bc.addWithSeparators(
                    new BreadcrumbItem("Home",       HomeView.class),
                    new BreadcrumbEllipsis(),                // "…" placeholder
                    new BreadcrumbItem("Components", ComponentsView.class),
                    new BreadcrumbPage("Breadcrumb")
                );
                """);
    }
}

