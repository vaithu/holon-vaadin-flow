package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.holonplatform.vaadin.flow.vaadinplus.components.IconBadge;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link IconBadge} component.
 *
 * <p>Covers:
 * <ol>
 *   <li>All semantic variants (DEFAULT, DESTRUCTIVE, WARNING.create(), SUCCESS, INFO)</li>
 *   <li>All size presets (SM, DEFAULT, LG)</li>
 *   <li>Fluent builder API usage</li>
 *   <li>Optional text label that can be updated later</li>
 *   <li>Runtime mutation (setVariant / setBadgeSize / setIcon)</li>
 * </ol>
 */
@PageTitle("IconBadge – Holon Demo")
@Route(value = "icon-badge", layout = DemoMainLayout.class)
public class IconBadgeDemoView extends Div {

    public IconBadgeDemoView() {
        addClassName("app-view");

        var title = new H1("IconBadge");

        var desc = new Paragraph(
                "Circular tinted icon badge — a round, semantically-coloured circle that wraps a " +
                "vaadin-icon. Matches the AlertDialog header icon pattern. " +
                "Five color variants × five size presets are available out of the box.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(variantsExample());
        examples.add(sizesExample());
        examples.add(builderExample());
        examples.add(textExample());
        examples.add(mutationExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample variantsExample() {
        var row = new Div();
        row.addClassNames("demo-stack","demo-stack--row");

        row.add(IconBadge.builder(VaadinIcon.CIRCLE.create()).build());
        row.add(IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS).build());
        row.add(IconBadge.builder(VaadinIcon.INFO_CIRCLE.create(), Alert.Variant.INFO).build());
        row.add(IconBadge.builder(VaadinIcon.WARNING.create(), Alert.Variant.WARNING).build());
        row.add(IconBadge.builder(VaadinIcon.CLOSE_CIRCLE.create(), Alert.Variant.DESTRUCTIVE).build());

        return new DemoExample("Semantic Variants", row, """
                // Neutral (no variant)
                IconBadge.builder(VaadinIcon.CIRCLE.create()).build();

                // Success
                IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS).build();

                // Info
                IconBadge.builder(VaadinIcon.INFO_CIRCLE.create(), Alert.Variant.INFO).build();

                // Warning
                IconBadge.builder(VaadinIcon.WARNING.create(), Alert.Variant.WARNING).build();

                // Destructive
                IconBadge.builder(VaadinIcon.CLOSE_CIRCLE.create(), Alert.Variant.DESTRUCTIVE).build();
                """);
    }

    private DemoExample sizesExample() {
        var row = new Div();
        row.addClassNames("demo-stack","demo-stack--row","demo-stack--align-center");

        row.add(IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS, IconBadge.Size.XS).build());
        row.add(IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS, IconBadge.Size.SM).build());
        row.add(IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS, IconBadge.Size.DEFAULT).build());
        row.add(IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS, IconBadge.Size.LG).build());
        row.add(IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS, IconBadge.Size.XL).build());

        return new DemoExample("Size Presets", row, """
                // XS  — 1.75 rem / 28 px — dense inline chips
                IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS, IconBadge.Size.XS).build();

                // SM  — 2 rem / 32 px — compact, for inline / list use
                IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS, IconBadge.Size.SM).build();

                // DEFAULT — 2.75 rem / 44 px — matches AlertDialog header icon
                IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS, IconBadge.Size.DEFAULT).build();

                // LG  — 3.5 rem / 56 px — hero / empty-state use
                IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS, IconBadge.Size.LG).build();

                // XL  — 4.5 rem / 72 px — large status / dashboard use
                IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS, IconBadge.Size.XL).build();
                """);
    }

    private DemoExample builderExample() {
        var row = new Div();
        row.addClassNames("demo-stack","demo-stack--row");

        // builder() — bare neutral
        row.add(IconBadge.builder(VaadinIcon.CIRCLE.create()).build());

        // builder(icon, variant)
        row.add(IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS).build());

        // builder(icon, variant, size) — LG
        row.add(IconBadge.builder(VaadinIcon.TRUCK.create(), Alert.Variant.WARNING, IconBadge.Size.LG).build());

        // full chain
        row.add(IconBadge.builder(VaadinIcon.INFO_CIRCLE.create(), Alert.Variant.INFO, IconBadge.Size.SM).build());

        return new DemoExample("Builder API", row, """
                // Neutral badge via full chain
                IconBadge badge = IconBadge.builder(VaadinIcon.CIRCLE.create()).build();

                // Shortest — icon + variant
                IconBadge success = IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS)
                    .build();

                // Icon + variant + explicit size
                IconBadge lg = IconBadge.builder(VaadinIcon.TRUCK.create(), Alert.Variant.WARNING, IconBadge.Size.LG)
                    .build();

                // Full fluent chain
                IconBadge info = IconBadge.builder(VaadinIcon.INFO_CIRCLE.create(), Alert.Variant.INFO, IconBadge.Size.SM).build();
                """);
    }

    private DemoExample textExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row", "demo-stack--align-center");

        var compactPill = IconBadge.builder(VaadinIcon.INFO_CIRCLE.create(), Alert.Variant.INFO, IconBadge.Size.XS, "Draft").build();
        compactPill.text("Ready");

        var spaciousPill = IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS, IconBadge.Size.XL, "Published").build();
        spaciousPill.text("Published");

        row.add(compactPill);
        row.add(new Paragraph("Compact pill: " + compactPill.getBadgeSize()));
        row.add(spaciousPill);
        row.add(new Paragraph("Spacious pill: " + spaciousPill.getBadgeSize()));

        return new DemoExample("Text label — setText / update later", row, """
                IconBadge compact = IconBadge.builder(VaadinIcon.INFO_CIRCLE.create(), Alert.Variant.INFO, IconBadge.Size.XS, "Draft").build();
                compact.text("Ready");

                IconBadge spacious = IconBadge.builder(VaadinIcon.CHECK_CIRCLE.create(), Alert.Variant.SUCCESS, IconBadge.Size.XL, "Published").build();
                spacious.text("Published");

                // Clear and re-apply when needed
                compact.clearText();
                compact.text("Ready again");

                // Inspect the chosen pill sizes later
                compact.getBadgeSize();
                spacious.getBadgeSize();
                """);
    }

    private DemoExample mutationExample() {
        var badge = IconBadge.builder(VaadinIcon.CIRCLE.create(), null, IconBadge.Size.DEFAULT).build();

        // Cycle through variants on each click (demo: show all states without buttons)
        // We demonstrate the API by building five mutated states side-by-side.
        var row = new Div();
        row.addClassNames("demo-stack","demo-stack--row");

        var b1 = IconBadge.builder(VaadinIcon.STAR.create(), Alert.Variant.INFO).build();
        b1.setBadgeSize(IconBadge.Size.LG);
        row.add(b1);

        var b2 = IconBadge.builder(VaadinIcon.STAR.create(), Alert.Variant.INFO, IconBadge.Size.LG).build();
        b2.setVariant(Alert.Variant.WARNING);
        row.add(b2);

        var b3 = IconBadge.builder(VaadinIcon.STAR.create(), Alert.Variant.INFO, IconBadge.Size.LG).build();
        b3.setVariant(Alert.Variant.SUCCESS);
        b3.setIcon(VaadinIcon.CHECK);
        row.add(b3);

        var b4 = IconBadge.builder(VaadinIcon.STAR.create(), Alert.Variant.INFO, IconBadge.Size.LG).build();
        b4.setVariant(Alert.Variant.DESTRUCTIVE);
        b4.setIcon(VaadinIcon.CLOSE);
        b4.setBadgeSize(IconBadge.Size.SM);
        row.add(b4);

        row.add(badge); // neutral, unchanged

        return new DemoExample("Runtime Mutation", row, """
                // Start with INFO/LG, then resize to LG explicitly
                IconBadge b = IconBadge.builder(VaadinIcon.STAR.create(), Alert.Variant.INFO).build();
                b.setBadgeSize(IconBadge.Size.LG);

                // Swap variant at runtime
                b.setVariant(Alert.Variant.WARNING);

                // Change icon and variant together
                b.setIcon(VaadinIcon.CHECK);
                b.setVariant(Alert.Variant.SUCCESS);

                // Shrink back
                b.setBadgeSize(IconBadge.Size.SM);
                """);
    }
}

