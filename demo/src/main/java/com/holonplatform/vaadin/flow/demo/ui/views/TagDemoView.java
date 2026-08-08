package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Tag;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link Tag} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>Simple text tags (all Color.Text variants)</li>
 *   <li>With VaadinIcon icon prefix</li>
 *   <li>With VaadinIcon prefix</li>
 *   <li>With Avatar prefix</li>
 *   <li>Semantic status chips (Active, Warning, Error, Neutral)</li>
 *   <li>Tag cloud / multi-tag layout</li>
 * </ol>
 */
@PageTitle("Tag – Holon Demo")
@Route(value = "tag", layout = DemoMainLayout.class)
public class TagDemoView extends Div {

    public TagDemoView() {
        addClassName("app-view");

        var title = new H1("Tag");

        var desc = new Paragraph(
                "A compact label/badge chip rendered as a <span>. " +
                "Supports an optional prefix slot (icon, avatar, or any component) " +
                "and a Color.Text colour class. " +
                "Ideal for status labels, category badges, and multi-value filters.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(textColorsExample());
        examples.add(withIconExample());
        examples.add(withAvatarExample());
        examples.add(statusChipsExample());
        examples.add(tagCloudExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample textColorsExample() {
        var row = new Div();
        row.addClassName("tag-row");

        for (Color.Text color : Color.Text.values()) {
            String label = color.name().charAt(0)
                    + color.name().substring(1).toLowerCase().replace("_", " ");
            var tag = new Tag((com.vaadin.flow.component.Component) null, label, color);
            row.add(tag);
        }

        return new DemoExample("All Color.Text Variants", row, """
                // Text colour is driven by a CSS class from Color.Text enum.
                new Tag("Header",  Color.Text.HEADER);
                new Tag("Body",    Color.Text.BODY);
                new Tag("Primary", Color.Text.PRIMARY);
                new Tag("Error",   Color.Text.ERROR);
                new Tag("Success", Color.Text.SUCCESS);
                // … and all other Color.Text values
                """);
    }

    private DemoExample withIconExample() {
        var row = new Div();
        row.addClassName("tag-row");

        row.add(new Tag(VaadinIcon.CHECK_CIRCLE, "Verified",   Color.Text.SUCCESS));
        row.add(new Tag(VaadinIcon.WARNING,      "Deprecated", Color.Text.ERROR));
        row.add(new Tag(VaadinIcon.EXCLAMATION_CIRCLE, "Preview", Color.Text.PRIMARY));
        row.add(new Tag(VaadinIcon.STAR,         "Featured",   Color.Text.HEADER));
        row.add(new Tag(VaadinIcon.LOCK,         "Private",    Color.Text.SECONDARY));
        row.add(new Tag(VaadinIcon.TAG,          "Beta",       Color.Text.TERTIARY));

        return new DemoExample("With VaadinIcon Prefix", row, """
                // Pass a VaadinIcon enum value as the icon prefix.
                new Tag(VaadinIcon.CHECK_CIRCLE, "Verified",   Color.Text.SUCCESS);
                new Tag(VaadinIcon.WARNING,      "Deprecated", Color.Text.ERROR);
                new Tag(VaadinIcon.EXCLAMATION_CIRCLE, "Preview", Color.Text.PRIMARY);
                new Tag(VaadinIcon.LOCK,         "Private",    Color.Text.SECONDARY);
                """);
    }

    private DemoExample withAvatarExample() {
        var row = new Div();
        row.addClassName("tag-row");

        var alice = new Avatar("Alice Smith");
        row.add(new Tag(alice, "Alice Smith", Color.Text.HEADER));

        var bob = new Avatar("Bob Brown");
        row.add(new Tag(bob, "Bob Brown", Color.Text.SECONDARY));

        var carol = new Avatar("Carol White");
        row.add(new Tag(carol, "Carol White", Color.Text.BODY));

        return new DemoExample("With Avatar Prefix", row, """
                // An Avatar component is recognised and gets the tag__avatar CSS class.
                Avatar avatar = new Avatar("Alice Smith");
                new Tag(avatar, "Alice Smith", Color.Text.HEADER);
                """);
    }

    private DemoExample statusChipsExample() {
        var row = new Div();
        row.addClassName("tag-row");

        // Status chips using semantic modifier classes for background colour
        record Status(String label, VaadinIcon icon, Color.Text color, String modifier) {}
        var statuses = java.util.List.of(
                new Status("Active",      VaadinIcon.CHECK_CIRCLE, Color.Text.SUCCESS,   "tag--success"),
                new Status("Pending",     VaadinIcon.CLOCK,        Color.Text.PRIMARY,   "tag--info"),
                new Status("Warning",     VaadinIcon.WARNING,      Color.Text.ERROR,     "tag--warning"),
                new Status("Inactive",    VaadinIcon.CLOSE,        Color.Text.SECONDARY, "tag--neutral"),
                new Status("Processing",  VaadinIcon.REFRESH,      Color.Text.PRIMARY,   "tag--info"),
                new Status("Archived",    VaadinIcon.INBOX,        Color.Text.TERTIARY,  "tag--neutral")
        );

        for (var s : statuses) {
            var tag = new Tag(s.icon(), s.label(), s.color());
            tag.addClassName(s.modifier());
            row.add(tag);
        }

        return new DemoExample("Semantic Status Chips", row, """
                // Combine Color.Text with a semantic background modifier.
                Tag active = new Tag(VaadinIcon.CHECK_CIRCLE, "Active", Color.Text.SUCCESS);
                active.addClassName("tag--success");

                Tag pending = new Tag(VaadinIcon.CLOCK, "Pending", Color.Text.PRIMARY);
                pending.addClassName("tag--info");

                Tag warning = new Tag(VaadinIcon.WARNING, "Warning", Color.Text.ERROR);
                warning.addClassName("tag--warning");
                """);
    }

    private DemoExample tagCloudExample() {
        var cloud = new Div();
        cloud.addClassName("tag-row");
        var topics = new String[][]{
                {"Java",        "HEADER"},
                {"Spring Boot", "PRIMARY"},
                {"Vaadin",      "SUCCESS"},
                {"JPA",         "SECONDARY"},
                {"REST API",    "BODY"},
                {"Docker",      "TERTIARY"},
                {"Kubernetes",  "PRIMARY"},
                {"CI/CD",       "SECONDARY"},
                {"PostgreSQL",  "HEADER"},
                {"Redis",       "ERROR"},
                {"GraphQL",     "SUCCESS"},
                {"TypeScript",  "PRIMARY"}
        };

        for (var t : topics) {
            Color.Text color = Color.Text.valueOf(t[1]);
            var tag = new Tag((com.vaadin.flow.component.Component) null, t[0], color);
            cloud.add(tag);
        }

        return new DemoExample("Tag Cloud", cloud, """
                // Plain text tags in a wrapping flex container make a tag cloud.
                for (String topic : topics) {
                    cloud.add(new Tag(topic, Color.Text.SECONDARY));
                }
                // cloud CSS: display: flex; flex-wrap: wrap; gap: 0.5rem;
                """);
    }
}



