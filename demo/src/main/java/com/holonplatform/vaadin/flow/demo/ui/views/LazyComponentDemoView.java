package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.LazyComponent;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for {@link LazyComponent}.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic usage — supplier runs on first attach</li>
 *   <li>Inside a Dialog — content deferred until dialog opens</li>
 *   <li>Inside Tabs — content deferred until tab pane is added to DOM</li>
 *   <li>Multiple independent lazy components in one container</li>
 * </ol>
 */
@PageTitle("LazyComponent – Holon Demo")
@Route(value = "lazy-component", layout = DemoMainLayout.class)
public class LazyComponentDemoView extends Div {

    public LazyComponentDemoView() {
        addClassName("app-view");

        var title = new H1("LazyComponent");

        var desc = new Paragraph(
                "A Div wrapper that defers child construction until the component is first " +
                "attached to the DOM. The supplier is called at most once — ideal for " +
                "expensive components inside Dialogs, Tabs, or any container that is " +
                "conditionally added to the component tree.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(inDialogExample());
        examples.add(inTabsExample());
        examples.add(multipleExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample basicExample() {
        // The LazyComponent is in a visible container, so it attaches immediately
        // on page load. The creation timestamp proves when the supplier ran.
        var lazy = new LazyComponent(() -> {
            var stamp = new Span("Supplier ran at: " +
                    LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
            return stamp;
        });

        return new DemoExample("Basic Usage", lazy, """
                // Supply a Component factory — called exactly once on first attach.
                var lazy = new LazyComponent(() -> {
                    // This block runs when the component is first attached to the DOM.
                    var grid = new Grid<>(MyBean.class);
                    grid.setItems(dataService.load());
                    return grid;
                });
                layout.add(lazy);
                """);
    }

    private DemoExample inDialogExample() {
        var dialog = new Dialog();
        dialog.setHeaderTitle("Lazy dialog content");

        // Content is constructed only when the dialog is first opened.
        // A Dialog's overlay is NOT attached until it is opened.
        dialog.add(new LazyComponent(() -> {
            var wrapper = new Div();
            var msg = new Span("Content built at: " +
                    LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
            wrapper.add(new Paragraph(
                    "This component was constructed the first time the dialog opened. " +
                    "Subsequent opens reuse the already-built child."), msg);
            return wrapper;
        }));

        var closeBtn = new Button("Close", e -> dialog.close());
        dialog.getFooter().add(closeBtn);

        var openBtn = new Button("Open dialog", VaadinIcon.EXTERNAL_LINK.create(),
                e -> dialog.open());

        return new DemoExample("Inside a Dialog", openBtn, """
                // A Dialog's overlay is NOT attached to the DOM until opened —
                // so content is deferred automatically until the first open().
                var dialog = new Dialog();
                dialog.add(new LazyComponent(() -> new ReportsGrid(reportService)));

                openBtn.addClickListener(e -> dialog.open());
                // On first open(): supplier runs, Grid is built.
                // On subsequent opens(): existing Grid is reused — no rebuild.
                """);
    }

    private DemoExample inTabsExample() {
        var tab1 = new Tab("Overview");
        var tab2 = new Tab("Analytics");
        var tabs = new Tabs(tab1, tab2);

        // Overview is always present
        var overviewPane = new Div(new Span("Overview — always rendered immediately."));

        // Analytics pane is only added to the DOM when its tab is first selected
        var analyticsPane = new Div();
        analyticsPane.setVisible(false);

        tabs.addSelectedChangeListener(e -> {
            boolean showOverview = e.getSelectedTab() == tab1;
            overviewPane.setVisible(showOverview);
            if (!showOverview && analyticsPane.getChildren().findFirst().isEmpty()) {
                // Add LazyComponent on first selection — it attaches (and renders) now
                analyticsPane.add(new LazyComponent(() -> {
                    var content = new Div();
                    var stamp = new Span("Analytics built at: " +
                            LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
                    content.add(new Paragraph(
                            "Supplier ran when the Analytics tab was first selected."), stamp);
                    return content;
                }));
            }
            analyticsPane.setVisible(!showOverview);
        });

        var container = new Div(tabs, overviewPane, analyticsPane);

        return new DemoExample("Inside Tabs (deferred)", container, """
                // Add the LazyComponent to the pane only on first tab selection.
                // This guarantees the supplier runs lazily — not on page load.
                tabs.addSelectedChangeListener(e -> {
                    if (e.getSelectedTab() == analyticsTab && !panePopulated) {
                        analyticsPane.add(new LazyComponent(() -> new AnalyticsGrid(svc)));
                        panePopulated = true;
                    }
                    analyticsPane.setVisible(e.getSelectedTab() == analyticsTab);
                });
                """);
    }

    private DemoExample multipleExample() {
        var container = new Div();

        // Three independent lazy components — each supplier is independent
        for (int i = 1; i <= 3; i++) {
            final int idx = i;
            var card = new LazyComponent(() -> {
                var div = new Div();
                var label = new Span("Card " + idx + " — created at " +
                        LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
                div.add(label);
                return div;
            });
            container.add(card);
        }

        return new DemoExample("Multiple Independent Suppliers", container, """
                // Each LazyComponent has its own supplier — called once on attach.
                // Useful for a dashboard where each card may call a different service.
                for (String metric : metrics) {
                    layout.add(new LazyComponent(
                        () -> new MetricCard(analyticsService.load(metric))
                    ));
                }
                """);
    }
}

