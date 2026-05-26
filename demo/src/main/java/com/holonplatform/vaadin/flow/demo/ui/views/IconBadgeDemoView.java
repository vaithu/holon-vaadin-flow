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
 *   <li>All semantic variants (DEFAULT, DESTRUCTIVE, WARNING, SUCCESS, INFO)</li>
 *   <li>All size presets (SM, DEFAULT, LG)</li>
 *   <li>Builder API usage</li>
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
                "Five color variants × three size presets are available out of the box.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(variantsExample());
        examples.add(sizesExample());
        examples.add(builderExample());
        examples.add(mutationExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample variantsExample() {
        var row = new Div();
        row.addClassNames("demo-stack","demo-stack--row");

        row.add(new IconBadge(VaadinIcon.CIRCLE,       null));
        row.add(new IconBadge(VaadinIcon.CHECK_CIRCLE, Alert.Variant.SUCCESS));
        row.add(new IconBadge(VaadinIcon.INFO_CIRCLE,  Alert.Variant.INFO));
        row.add(new IconBadge(VaadinIcon.WARNING,      Alert.Variant.WARNING));
        row.add(new IconBadge(VaadinIcon.CLOSE_CIRCLE, Alert.Variant.DESTRUCTIVE));

        return new DemoExample("Semantic Variants", row, """
                // Neutral (no variant)
                new IconBadge(VaadinIcon.CIRCLE, null);

                // Success
                new IconBadge(VaadinIcon.CHECK_CIRCLE, Alert.Variant.SUCCESS);

                // Info
                new IconBadge(VaadinIcon.INFO_CIRCLE, Alert.Variant.INFO);

                // Warning
                new IconBadge(VaadinIcon.WARNING, Alert.Variant.WARNING);

                // Destructive
                new IconBadge(VaadinIcon.CLOSE_CIRCLE, Alert.Variant.DESTRUCTIVE);
                """);
    }

    private DemoExample sizesExample() {
        var row = new Div();
        row.addClassNames("demo-stack","demo-stack--row","demo-stack--align-center");

        row.add(new IconBadge(VaadinIcon.CHECK_CIRCLE, Alert.Variant.SUCCESS, IconBadge.Size.SM));
        row.add(new IconBadge(VaadinIcon.CHECK_CIRCLE, Alert.Variant.SUCCESS, IconBadge.Size.DEFAULT));
        row.add(new IconBadge(VaadinIcon.CHECK_CIRCLE, Alert.Variant.SUCCESS, IconBadge.Size.LG));

        return new DemoExample("Size Presets", row, """
                // SM  — 2 rem / 32 px — compact, for inline / list use
                new IconBadge(VaadinIcon.CHECK_CIRCLE, Alert.Variant.SUCCESS, IconBadge.Size.SM);

                // DEFAULT — 2.75 rem / 44 px — matches AlertDialog header icon
                new IconBadge(VaadinIcon.CHECK_CIRCLE, Alert.Variant.SUCCESS, IconBadge.Size.DEFAULT);

                // LG  — 3.5 rem / 56 px — hero / empty-state use
                new IconBadge(VaadinIcon.CHECK_CIRCLE, Alert.Variant.SUCCESS, IconBadge.Size.LG);
                """);
    }

    private DemoExample builderExample() {
        var row = new Div();
        row.addClassNames("demo-stack","demo-stack--row");

        // builder() — bare neutral
        row.add(IconBadge.builder()
                .icon(VaadinIcon.CIRCLE)
                .build());

        // builder(icon, variant)
        row.add(IconBadge.builder(VaadinIcon.CHECK_CIRCLE, Alert.Variant.SUCCESS)
                .build());

        // builder(icon, variant, size) — LG
        row.add(IconBadge.builder(VaadinIcon.TRUCK, Alert.Variant.WARNING, IconBadge.Size.LG)
                .build());

        // full chain
        row.add(IconBadge.builder()
                .icon(VaadinIcon.INFO_CIRCLE)
                .variant(Alert.Variant.INFO)
                .size(IconBadge.Size.SM)
                .build());

        return new DemoExample("Builder API", row, """
                // Neutral badge via full chain
                IconBadge badge = IconBadge.builder()
                    .icon(VaadinIcon.CIRCLE)
                    .build();

                // Shortest — icon + variant
                IconBadge success = IconBadge.builder(VaadinIcon.CHECK_CIRCLE, Alert.Variant.SUCCESS)
                    .build();

                // Icon + variant + explicit size
                IconBadge lg = IconBadge.builder(VaadinIcon.TRUCK, Alert.Variant.WARNING, IconBadge.Size.LG)
                    .build();

                // Full fluent chain
                IconBadge info = IconBadge.builder()
                    .icon(VaadinIcon.INFO_CIRCLE)
                    .variant(Alert.Variant.INFO)
                    .size(IconBadge.Size.SM)
                    .build();
                """);
    }

    private DemoExample mutationExample() {
        var badge = new IconBadge(VaadinIcon.CIRCLE, null, IconBadge.Size.DEFAULT);

        // Cycle through variants on each click (demo: show all states without buttons)
        // We demonstrate the API by building five mutated states side-by-side.
        var row = new Div();
        row.addClassNames("demo-stack","demo-stack--row");

        var b1 = new IconBadge(VaadinIcon.STAR, Alert.Variant.INFO);
        b1.setBadgeSize(IconBadge.Size.LG);
        row.add(b1);

        var b2 = new IconBadge(VaadinIcon.STAR, Alert.Variant.INFO, IconBadge.Size.LG);
        b2.setVariant(Alert.Variant.WARNING);
        row.add(b2);

        var b3 = new IconBadge(VaadinIcon.STAR, Alert.Variant.INFO, IconBadge.Size.LG);
        b3.setVariant(Alert.Variant.SUCCESS);
        b3.setIcon(VaadinIcon.CHECK);
        row.add(b3);

        var b4 = new IconBadge(VaadinIcon.STAR, Alert.Variant.INFO, IconBadge.Size.LG);
        b4.setVariant(Alert.Variant.DESTRUCTIVE);
        b4.setIcon(VaadinIcon.CLOSE);
        b4.setBadgeSize(IconBadge.Size.SM);
        row.add(b4);

        row.add(badge); // neutral, unchanged

        return new DemoExample("Runtime Mutation", row, """
                // Start with INFO/LG, then resize to LG explicitly
                IconBadge b = new IconBadge(VaadinIcon.STAR, Alert.Variant.INFO);
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

