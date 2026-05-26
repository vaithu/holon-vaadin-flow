package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Empty;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link Empty} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Title only (minimal)</li>
 *   <li>Icon + title + description</li>
 *   <li>With action button</li>
 *   <li>Icon + title + description + action (full composition)</li>
 *   <li>Custom illustration component</li>
 *   <li>Common use-case scenarios (no results, no notifications, empty inbox)</li>
 * </ol>
 */
@PageTitle("Empty – Holon Demo")
@Route(value = "empty", layout = DemoMainLayout.class)
public class EmptyDemoView extends Div {

    public EmptyDemoView() {
        addClassName("app-view");

        var title = new H1("Empty");

        var desc = new Paragraph(
                "Empty-state placeholder inspired by shadcn/ui EmptyState. " +
                "Use it wherever a list, grid, or data set contains no items — giving the user " +
                "an icon, a human-readable message, and a clear call-to-action. " +
                "Supports icon/illustration, title, description, and action slots via the fluent builder.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(minimalExample());
        examples.add(iconWithDescExample());
        examples.add(withActionExample());
        examples.add(fullCompositionExample());
        examples.add(scenariosExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample minimalExample() {
        var empty = Empty.builder()
                .title("Nothing here yet")
                .build();

        return new DemoExample("Title Only (minimal)", empty, """
                Empty empty = Empty.builder()
                    .title("Nothing here yet")
                    .build();
                """);
    }

    private DemoExample iconWithDescExample() {
        var empty = Empty.builder()
                .icon(VaadinIcon.SEARCH.create())
                .title("No results found")
                .description("Try adjusting your search or filter to find what you're looking for.")
                .build();

        return new DemoExample("Icon + Title + Description", empty, """
                Empty empty = Empty.builder()
                    .icon(VaadinIcon.SEARCH.create())
                    .title("No results found")
                    .description("Try adjusting your search or filter to find what you're looking for.")
                    .build();
                """);
    }

    private DemoExample withActionExample() {
        var empty = Empty.builder()
                .icon(VaadinIcon.PLUS_CIRCLE.create())
                .title("No products yet")
                .description("Get started by adding your first product to the catalog.")
                .action(new Button("Add Product", VaadinIcon.PLUS.create()))
                .build();

        return new DemoExample("With Action Button", empty, """
                Empty empty = Empty.builder()
                    .icon(VaadinIcon.PLUS_CIRCLE.create())
                    .title("No products yet")
                    .description("Get started by adding your first product.")
                    .action(new Button("Add Product", VaadinIcon.PLUS.create()))
                    .build();
                """);
    }

    private DemoExample fullCompositionExample() {
        var empty = Empty.builder()
                .icon(VaadinIcon.INBOX.create())
                .title("Your inbox is empty")
                .description("When you receive messages, they will appear here. " +
                              "Start a conversation to see your messages.")
                .action(new Button("Compose Message"), new Button("Invite teammates"))
                .build();

        return new DemoExample("Full Composition (icon + title + description + actions)", empty, """
                // Multiple action buttons are all wrapped in the EmptyAction container.
                Empty empty = Empty.builder()
                    .icon(VaadinIcon.INBOX.create())
                    .title("Your inbox is empty")
                    .description("When you receive messages, they will appear here.")
                    .action(new Button("Compose Message"), new Button("Invite teammates"))
                    .build();
                """);
    }

    private DemoExample scenariosExample() {
        var container = new Div();

        // No notifications
        container.add(Empty.builder()
                .icon(VaadinIcon.BELL_O.create())
                .title("No notifications")
                .description("You're all caught up. Notifications will appear here when they arrive.")
                .build());

        // Empty cart
        container.add(Empty.builder()
                .icon(VaadinIcon.CART.create())
                .title("Your cart is empty")
                .description("Add items from the catalog to start a new order.")
                .action(new Button("Browse Catalog"))
                .build());

        // Offline / error state
        container.add(Empty.builder()
                .icon(VaadinIcon.CLOUD_O.create())
                .title("Unable to load data")
                .description("Check your connection and try again. Contact support if the problem persists.")
                .action(new Button("Retry", VaadinIcon.REFRESH.create()))
                .build());

        return new DemoExample("Common Scenarios", container, """
                // No notifications
                Empty.builder()
                    .icon(VaadinIcon.BELL_O.create())
                    .title("No notifications")
                    .description("You're all caught up.")
                    .build();

                // Empty cart
                Empty.builder()
                    .icon(VaadinIcon.CART.create())
                    .title("Your cart is empty")
                    .action(new Button("Browse Catalog"))
                    .build();

                // Error / offline state
                Empty.builder()
                    .icon(VaadinIcon.CLOUD_O.create())
                    .title("Unable to load data")
                    .action(new Button("Retry", VaadinIcon.REFRESH.create()))
                    .build();
                """);
    }
}

