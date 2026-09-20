package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.iyensoft.vaadin.flow.components.MaterialAppBar;
import com.iyensoft.vaadin.flow.components.ResponsiveDiv;
import com.holonplatform.vaadin.flow.components.support.ViewMode;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/** Demo page for the Material 3 {@link MaterialAppBar} component. */
@PageTitle("Material AppBar - Holon Demo")
@Route(value = "material-app-bar", layout = DemoMainLayout.class)
public class MaterialAppBarDemoView extends Div {

    public MaterialAppBarDemoView() {
        addClassName("app-view");

        var title = new H1("Material AppBar");
        var description = new Paragraph(
                "Material 3 app bar variants with explicit leading, headline, subtitle, "
                        + "trailing action, search, alignment, and scroll-state configuration.");
        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(smallExample());
        examples.add(searchExample());
        examples.add(modeAwareExample());
        examples.add(mediumFlexibleExample());
        examples.add(largeFlexibleExample());

        add(title, description, examples);
    }

    private DemoExample smallExample() {
        var menu = action(VaadinIcon.MENU, "Open navigation");
        var settings = action(VaadinIcon.COG, "Open settings");
        var appBar = Components.materialAppBar()
                .headline("Dashboard")
                .leading(menu)
                .actions(settings)
                .build();

        return new DemoExample("Small app bar", wrap(appBar), """
                MaterialAppBar appBar = Components.materialAppBar()
                        .headline("Dashboard")
                        .leading(new Button(VaadinIcon.MENU.create()))
                        .actions(new Button(VaadinIcon.COG.create()))
                        .build();
                """);
    }

    private DemoExample searchExample() {
        var search = new TextField();
        search.setPlaceholder("Search");
        search.setPrefixComponent(VaadinIcon.SEARCH.create());

        var appBar = Components.materialAppBar()
                .variant(MaterialAppBar.Variant.SEARCH)
                .search()
                .leading(action(VaadinIcon.ARROW_LEFT, "Close search"))
                .headline(search)
                .actions(action(VaadinIcon.MICROPHONE, "Use voice search"))
                .responsiveAction(action(VaadinIcon.FILTER, "Filter"), "Filter", () -> { })
                .responsiveAction(action(VaadinIcon.SORT, "Sort"), "Sort", () -> { })
                .build();

        return new DemoExample("Search app bar", wrap(appBar), """
                TextField search = new TextField();
                search.setPlaceholder("Search");
                search.setPrefixComponent(VaadinIcon.SEARCH.create());

                MaterialAppBar appBar = Components.materialAppBar()
                        .variant(MaterialAppBar.Variant.SEARCH)
                        .search()
                        .leading(new Button(VaadinIcon.ARROW_LEFT.create()))
                        .headline(search)
                        .actions(new Button(VaadinIcon.MICROPHONE.create()))
                        .responsiveAction(new Button(VaadinIcon.FILTER.create()), "Filter", this::openFilters)
                        .responsiveAction(new Button(VaadinIcon.SORT.create()), "Sort", this::openSort)
                        .build();
                """);
    }

    private DemoExample modeAwareExample() {
        var appBar = Components.materialAppBar()
                .variant(MaterialAppBar.Variant.SMALL)
                .viewMode(ViewMode.MOBILE)
                .headline("Inbox")
                .leading(action(VaadinIcon.MENU, "Open navigation"))
                .actions(action(VaadinIcon.PLUS, "Create item"))
                .responsiveAction(new Button("Filter"), "Filter", () -> { })
                .responsiveAction(new Button("Sort"), "Sort", () -> { })
                .build();

        var toggle = new Button("Switch to desktop mode", VaadinIcon.ARROWS_LONG_RIGHT.create());
        toggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        toggle.addClickListener(event -> appBar.setViewMode(
                appBar.getViewMode() == ViewMode.MOBILE ? ViewMode.DESKTOP : ViewMode.MOBILE));

        var preview = new Div(appBar, toggle);
        preview.getStyle().set("display", "grid").set("gap", "var(--lumo-space-m)");

        return new DemoExample("ViewMode-driven responsiveness", preview, """
                MaterialAppBar appBar = Components.materialAppBar()
                        .variant(MaterialAppBar.Variant.SMALL)
                        .viewMode(ViewMode.MOBILE)
                        .headline("Inbox")
                        .leading(new Button(VaadinIcon.MENU.create()))
                        .actions(new Button(VaadinIcon.PLUS.create()))
                        .responsiveAction(new Button("Filter"), "Filter", () -> {})
                        .responsiveAction(new Button("Sort"), "Sort", () -> {})
                        .build();

                appBar.setViewMode(appBar.getViewMode() == ViewMode.MOBILE
                        ? ViewMode.DESKTOP
                        : ViewMode.MOBILE);
                """);
    }

    private DemoExample mediumFlexibleExample() {
        var appBar = Components.materialAppBar()
                .variant(MaterialAppBar.Variant.MEDIUM_FLEXIBLE)
                .headline("Orders")
                .subtitle(new Span("12 open orders"))
                .leading(action(VaadinIcon.ARROW_LEFT, "Go back"))
                .actions(action(VaadinIcon.ELLIPSIS_DOTS_H, "More actions"))
                .build();

        return new DemoExample("Medium flexible app bar", wrap(appBar), """
                MaterialAppBar appBar = Components.materialAppBar()
                        .variant(MaterialAppBar.Variant.MEDIUM_FLEXIBLE)
                        .headline("Orders")
                        .subtitle(new Span("12 open orders"))
                        .leading(new Button(VaadinIcon.ARROW_LEFT.create()))
                        .actions(new Button(VaadinIcon.ELLIPSIS_DOTS_H.create()))
                        .build();
                """);
    }

    private DemoExample largeFlexibleExample() {
        var appBar = Components.materialAppBar()
                .variant(MaterialAppBar.Variant.LARGE_FLEXIBLE)
                .headline("Account")
                .subtitle(new Span("Personal preferences"))
                .leading(new Avatar("Jane Doe"))
                .actions(action(VaadinIcon.EDIT, "Edit account"))
                .centered()
                .scrolled()
                .build();

        var toggle = new Button("Toggle scroll state", VaadinIcon.ARROW_DOWN.create());
        toggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        toggle.addClickListener(event -> {
            boolean scrolled = appBar.getClassNames().contains("material-app-bar--scrolled");
            appBar.setScrolled(!scrolled);
        });

        var preview = new Div(appBar, toggle);
        preview.getStyle().set("display", "grid").set("gap", "var(--lumo-space-m)");

        return new DemoExample("Large flexible, centered, and scrolled", preview, """
                MaterialAppBar appBar = Components.materialAppBar()
                        .variant(MaterialAppBar.Variant.LARGE_FLEXIBLE)
                        .headline("Account")
                        .subtitle(new Span("Personal preferences"))
                        .leading(new Avatar("Jane Doe"))
                        .actions(new Button(VaadinIcon.EDIT.create()))
                        .centered()
                        .scrolled()
                        .build();
                """);
    }

    private static Button action(VaadinIcon icon, String label) {
        var button = new Button(icon.create());
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        button.setAriaLabel(label);
        return button;
    }

    private static Div wrap(MaterialAppBar appBar) {
        return new Div(appBar);
    }
}
