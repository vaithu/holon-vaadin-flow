package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.holonplatform.vaadin.flow.vaadinplus.components.MaterialHeader;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/** Demo page for the Material 3 {@link MaterialHeader} component. */
@PageTitle("Material Header - Holon Demo")
@Route(value = "material-header", layout = DemoMainLayout.class)
public class MaterialHeaderDemoView extends Div {

    private static final String EXPORT = "Export";

    public MaterialHeaderDemoView() {
        addClassName("app-view");

        var title = new H1("Material Header");
        var description = new Paragraph(
                "Material 3 content header with explicit breadcrumb, media, headline, subtitle, "
                        + "details, tags, and primary/secondary action slots.");
        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(smallExample());
        examples.add(mediumExample());
        examples.add(largeExample());
        examples.add(modeAwareExample());

        add(title, description, examples);
    }

    private DemoExample smallExample() {
        var header = Components.materialHeader()
                .variant(MaterialHeader.Variant.SMALL)
                .headline("Products")
                .primaryAction(new Button("New product"))
                .build();

        return new DemoExample("Small header", wrap(header), """
                MaterialHeader header = Components.materialHeader()
                        .variant(MaterialHeader.Variant.SMALL)
                        .headline("Products")
                        .primaryAction(new Button("New product"))
                        .build();
                """);
    }

    private DemoExample mediumExample() {
        var header = Components.materialHeader()
                .variant(MaterialHeader.Variant.MEDIUM)
                .breadcrumb(new Span("Workspace / Products"))
                .headline("Sensor catalogue")
                .subtitle(new Span("128 active items"))
                .secondaryAction(new Button("Share"), new Button("Archive"))
                .primaryAction(new Button("Edit"))
                .build();

        return new DemoExample("Medium header with breadcrumb", wrap(header), """
                MaterialHeader header = Components.materialHeader()
                        .variant(MaterialHeader.Variant.MEDIUM)
                        .breadcrumb(new Span("Workspace / Products"))
                        .headline("Sensor catalogue")
                        .subtitle(new Span("128 active items"))
                        .secondaryAction(new Button("Share"), new Button("Archive"))
                        .primaryAction(new Button("Edit"))
                        .build();
                """);
    }

    private DemoExample largeExample() {
        var header = Components.materialHeader()
                .variant(MaterialHeader.Variant.LARGE)
                .breadcrumb(new Span("Workspace / Products / Phoenix"))
                .media(new Avatar("Project Phoenix"))
                .headline("Project Phoenix")
                .subtitle(new Span("Q3 launch dossier"))
                .details(
                        new Span("Owner: Sara Parker"),
                        new Span("Due in 3 days"),
                        new Span("Status: In review"))
                .tags(new Span("Priority"), new Span("High"), new Span("Design"))
                .secondaryAction(new Button("Share"), new Button("Archive"))
                .primaryAction(new Button("Approve"))
                .overflowAction("Delete", () -> Notification.show("Delete requested"))
                .build();

        return new DemoExample("Large header with media, details, and tags", wrap(header), """
                MaterialHeader header = Components.materialHeader()
                        .variant(MaterialHeader.Variant.LARGE)
                        .breadcrumb(new Span("Workspace / Products / Phoenix"))
                        .media(new Avatar("Project Phoenix"))
                        .headline("Project Phoenix")
                        .subtitle(new Span("Q3 launch dossier"))
                        .details(
                                new Span("Owner: Sara Parker"),
                                new Span("Due in 3 days"),
                                new Span("Status: In review"))
                        .tags(new Span("Priority"), new Span("High"), new Span("Design"))
                        .secondaryAction(new Button("Share"), new Button("Archive"))
                        .primaryAction(new Button("Approve"))
                        .overflowAction("Delete", this::deleteProject)
                        .build();
                """);
    }

    private DemoExample modeAwareExample() {
        var header = Components.materialHeader()
                .variant(MaterialHeader.Variant.LARGE)
                .viewMode(ViewMode.MOBILE)
                .breadcrumb(new Span("Workspace / Products / Phoenix"))
                .headline("Project Phoenix")
                .subtitle(new Span("Q3 launch dossier"))
                .primaryAction(new Button("Approve"))
                .responsiveAction(new Button(EXPORT), EXPORT,
                        () -> Notification.show("Export requested"))
                .responsiveAction(new Button("Review"), "Review",
                        () -> Notification.show("Review requested"))
                .build();

        var toggle = new Button("Switch to desktop mode", VaadinIcon.ARROWS_LONG_RIGHT.create());
        toggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        toggle.addClickListener(event -> {
            var next = header.getViewMode() == ViewMode.MOBILE ? ViewMode.DESKTOP : ViewMode.MOBILE;
            header.setViewMode(next);
            toggle.setText(next == ViewMode.MOBILE ? "Switch to desktop mode" : "Switch to mobile mode");
        });

        var preview = new Div(header, toggle);
        preview.getStyle().set("display", "grid").set("gap", "var(--lumo-space-m)");

        return new DemoExample("ViewMode-driven responsiveness", preview, """
                MaterialHeader header = Components.materialHeader()
                        .variant(MaterialHeader.Variant.LARGE)
                        .viewMode(ViewMode.MOBILE)
                        .breadcrumb(new Span("Workspace / Products / Phoenix"))
                        .headline("Project Phoenix")
                        .subtitle(new Span("Q3 launch dossier"))
                        .primaryAction(new Button("Approve"))
                        .responsiveAction(new Button("Export"), "Export", this::export)
                        .responsiveAction(new Button("Review"), "Review", this::review)
                        .build();

                header.setViewMode(header.getViewMode() == ViewMode.MOBILE
                        ? ViewMode.DESKTOP
                        : ViewMode.MOBILE);
                """);
    }

    private static Div wrap(MaterialHeader header) {
        return new Div(header);
    }
}
