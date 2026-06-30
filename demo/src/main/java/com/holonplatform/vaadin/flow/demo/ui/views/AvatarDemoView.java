package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.AvatarBuilder;
import com.holonplatform.vaadin.flow.components.builders.AvatarColor;
import com.holonplatform.vaadin.flow.components.builders.AvatarGroupBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.components.Alert;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link Avatar} and {@link AvatarGroup} builders.
 *
 * <p>Covers:
 * <ol>
 *   <li>Anonymous avatar</li>
 *   <li>Named avatar — auto-generated initials</li>
 *   <li>Custom abbreviation override</li>
 *   <li>Image avatar</li>
 *   <li>AvatarColor enum — all 7 semantic colour tokens</li>
 *   <li>AvatarColor.forId — deterministic colour assignment from an entity id</li>
 *   <li>Semantic variant background — IconBadge-style tinted look</li>
 *   <li>Holon i18n — Localizable name and abbreviation with deferred resolution</li>
 *   <li>Accessibility — aria-label (string &amp; Localizable)</li>
 *   <li>Size variants (xsmall → xlarge)</li>
 *   <li>AvatarGroup — basic group</li>
 *   <li>AvatarGroup — overflow counter (maxItemsVisible)</li>
 *   <li>AvatarGroup — coloured items via AvatarColor</li>
 * </ol>
 */
@PageTitle("Avatar – Holon Demo")
@Route(value = "avatar", layout = DemoMainLayout.class)
@StyleSheet("context://utilities.css")
public class AvatarDemoView extends Div {

    public AvatarDemoView() {
        addClassName("app-view");

        var title = new H1("Avatar");

        var desc = new Paragraph(
                "Avatar is a graphical representation of a person or entity. " +
                "The Holon fluent builder wraps the Vaadin Avatar and AvatarGroup components with " +
                "Holon Platform i18n (Localizable), the AvatarColor semantic enum, " +
                "IconBadge-style tinted backgrounds via the shared semantic variant API, " +
                "full accessibility support (aria-label), and deferred localization.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(anonymousExample());
        examples.add(namedExample());
        examples.add(abbreviationExample());
        examples.add(imageExample());
        examples.add(avatarColorEnumExample());
        examples.add(avatarColorForIdExample());
        examples.add(variantBackgroundExample());
        examples.add(i18nExample());
        examples.add(accessibilityExample());
        examples.add(sizeVariantsExample());
        examples.add(avatarGroupExample());
        examples.add(avatarGroupOverflowExample());
        examples.add(avatarGroupColoredExample());

        add(title, desc, examples);
    }

    // ── Example 1 ─────────────────────────────────────────────────────────────

    private DemoExample anonymousExample() {
        Avatar avatar = AvatarBuilder.create().build();
        return new DemoExample("Anonymous avatar", avatar, """
                // Shows the generic silhouette icon.
                Avatar avatar = AvatarBuilder.create().build();
                // Via Components factory:
                Avatar avatar = Components.avatar().build();
                """);
    }

    // ── Example 2 ─────────────────────────────────────────────────────────────

    private DemoExample namedExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row");
        row.add(AvatarBuilder.create("Jane Smith").build());
        row.add(AvatarBuilder.create("John Doe").build());
        row.add(AvatarBuilder.create("Alice Wonderland").build());
        row.add(AvatarBuilder.create("Bob").build());

        return new DemoExample("Named avatars — auto-generated initials", row, """
                AvatarBuilder.create("Jane Smith").build();       // → "JS"
                AvatarBuilder.create("John Doe").build();         // → "JD"
                AvatarBuilder.create("Alice Wonderland").build(); // → "AW"
                """);
    }

    // ── Example 3 ─────────────────────────────────────────────────────────────

    private DemoExample abbreviationExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row");
        row.add(AvatarBuilder.create("Augusta Ada King").abbreviation("AK").build());
        row.add(AvatarBuilder.create("William Shakespeare").abbreviation("WS").build());
        row.add(AvatarBuilder.create("Leonardo da Vinci").abbreviation("LV").build());

        return new DemoExample("Custom abbreviation override", row, """
                AvatarBuilder.create("Augusta Ada King")
                    .abbreviation("AK")   // overrides auto-generated "AA"
                    .build();
                """);
    }

    // ── Example 4 ─────────────────────────────────────────────────────────────

    private DemoExample imageExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row");
        row.add(AvatarBuilder.create("Allison Torres")
                .image("https://randomuser.me/api/portraits/women/44.jpg").build());
        row.add(AvatarBuilder.create("Michael Jordan")
                .image("https://randomuser.me/api/portraits/men/41.jpg").build());
        row.add(AvatarBuilder.create("Sara Connor")
                .image("https://randomuser.me/api/portraits/women/68.jpg").build());

        return new DemoExample("Image avatar", row, """
                // When an image URL is set the abbreviation is hidden.
                AvatarBuilder.create("Allison Torres")
                    .image("https://example.com/alice.jpg")
                    .build();

                // Backend-served image:
                AvatarBuilder.create("Bob")
                    .imageHandler(DownloadHandler.forClassResource(
                        getClass(), "/images/bob.png", "bob.png"))
                    .build();
                """);
    }

    // ── Example 5 ─────────────────────────────────────────────────────────────

    private DemoExample avatarColorEnumExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row");

        for (AvatarColor color : AvatarColor.values()) {
            row.add(AvatarBuilder.create(color.name()).colorIndex(color).build());
        }

        return new DemoExample("AvatarColor enum — all 7 semantic colour tokens", row, """
                // Use the AvatarColor enum instead of magic numbers 0–6.
                AvatarBuilder.create("Alice").colorIndex(AvatarColor.BLUE).build();
                AvatarBuilder.create("Bob")  .colorIndex(AvatarColor.GREEN).build();
                AvatarBuilder.create("Carol").colorIndex(AvatarColor.PINK).build();
                AvatarBuilder.create("Dave") .colorIndex(AvatarColor.ORANGE).build();
                AvatarBuilder.create("Eve")  .colorIndex(AvatarColor.VIOLET).build();
                AvatarBuilder.create("Frank").colorIndex(AvatarColor.INDIGO).build();
                AvatarBuilder.create("Grace").colorIndex(AvatarColor.RED).build();
                """);
    }

    // ── Example 6 ─────────────────────────────────────────────────────────────

    private DemoExample avatarColorForIdExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row");

        String[] names = {"Alice", "Bob", "Carol", "Dave", "Eve",
                          "Frank", "Grace", "Hank", "Iris", "Jack"};
        for (int i = 0; i < names.length; i++) {
            row.add(AvatarBuilder.create(names[i])
                    .colorIndex(AvatarColor.forId(i + 1L))
                    .build());
        }

        return new DemoExample("AvatarColor.forId — deterministic colour from entity id", row, """
                // AvatarColor.forId(id) returns a stable colour using id % 7.
                for (User user : activeUsers) {
                    Avatar avatar = AvatarBuilder.create(user.getFullName())
                        .colorIndex(AvatarColor.forId(user.getId()))
                        .build();
                }
                """);
    }

    // ── Example 7 ─────────────────────────────────────────────────────────────

    private DemoExample variantBackgroundExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row", "demo-stack--align-center");

        row.add(AvatarBuilder.create("Def") .variant(Alert.Variant.DEFAULT)     .build());
        row.add(AvatarBuilder.create("OK")  .variant(Alert.Variant.SUCCESS)     .build());
        row.add(AvatarBuilder.create("Warn").variant(Alert.Variant.WARNING)     .build());
        row.add(AvatarBuilder.create("Info").variant(Alert.Variant.INFO)        .build());
        row.add(AvatarBuilder.create("Err") .variant(Alert.Variant.DESTRUCTIVE) .build());

        // Combined with size
        row.add(AvatarBuilder.create("LG")
                .variant(Alert.Variant.SUCCESS)
                .withThemeVariants(AvatarVariant.LUMO_LARGE)
                .build());

        return new DemoExample("IconBadge-style tinted background — shared variant API", row, """
                // Switch the avatar background to the same semantic palette as IconBadge.
                // Overrides --vaadin-avatar-background and --vaadin-avatar-abbreviation-color
                // using CSS custom properties — no inline styles.

                AvatarBuilder.create("Alice")
                    .variant(Alert.Variant.SUCCESS)     // green tint  ✓
                    .build();

                AvatarBuilder.create("Bob")
                    .variant(Alert.Variant.WARNING)     // amber tint
                    .build();

                AvatarBuilder.create("Carol")
                    .variant(Alert.Variant.INFO)        // violet tint
                    .build();

                AvatarBuilder.create("Dave")
                    .variant(Alert.Variant.DESTRUCTIVE) // red tint
                    .build();

                // Combine with size:
                AvatarBuilder.create("Eve")
                    .variant(Alert.Variant.SUCCESS)
                    .withThemeVariants(AvatarVariant.LUMO_LARGE)
                    .build();
                """);
    }

    // ── Example 8 ─────────────────────────────────────────────────────────────

    private DemoExample i18nExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row");

        Avatar direct = AvatarBuilder.create()
                .name(Localizable.builder()
                        .message("Jane Smith")
                        .messageCode("user.jane.fullname")
                        .build())
                .colorIndex(AvatarColor.BLUE)
                .build();
        row.add(direct);

        Avatar deferred = AvatarBuilder.create()
                .name("Bob Evans", "user.bob.fullname")
                .abbreviation("BE", "user.bob.abbrev")
                .colorIndex(AvatarColor.GREEN)
                .deferLocalization()
                .build();
        row.add(deferred);

        Avatar coded = AvatarBuilder.create()
                .name("Carol White", "user.carol.fullname")
                .colorIndex(AvatarColor.PINK)
                .build();
        row.add(coded);

        return new DemoExample("Holon i18n — Localizable name and abbreviation", row, """
                // Immediate resolution (at build time):
                AvatarBuilder.create()
                    .name(Localizable.builder()
                        .message("Jane Smith")
                        .messageCode("user.jane.fullname")
                        .build())
                    .colorIndex(AvatarColor.BLUE)
                    .build();

                // Convenience shorthand: name(defaultText, messageCode, args…)
                AvatarBuilder.create()
                    .name("Bob Evans", "user.bob.fullname")
                    .abbreviation("BE", "user.bob.abbrev")
                    .build();

                // Deferred: resolved on first UI attach
                AvatarBuilder.create()
                    .name("Carol White", "user.carol.fullname")
                    .deferLocalization()
                    .build();
                """);
    }

    // ── Example 9 ─────────────────────────────────────────────────────────────

    private DemoExample accessibilityExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row");

        Avatar withLabel = AvatarBuilder.create("Dave")
                .colorIndex(AvatarColor.ORANGE)
                .ariaLabel("Dave's profile picture")
                .build();

        Avatar withI18nLabel = AvatarBuilder.create("Eve")
                .colorIndex(AvatarColor.VIOLET)
                .ariaLabel(Localizable.builder()
                        .message("Eve's profile picture")
                        .messageCode("avatar.eve.ariaLabel")
                        .build())
                .build();

        row.add(withLabel, withI18nLabel);

        return new DemoExample("Accessibility — aria-label (string & Localizable)", row, """
                AvatarBuilder.create("Dave")
                    .ariaLabel("Dave's profile picture")
                    .build();

                // Localizable aria-label:
                AvatarBuilder.create("Eve")
                    .ariaLabel(Localizable.builder()
                        .message("Eve's profile picture")
                        .messageCode("avatar.eve.ariaLabel")
                        .build())
                    .build();

                // Reference another element's id:
                AvatarBuilder.create("Frank").ariaLabelledBy("name-span-id").build();
                """);
    }

    // ── Example 10 ────────────────────────────────────────────────────────────

    private DemoExample sizeVariantsExample() {
        var row = new Div();
        row.addClassNames("demo-stack", "demo-stack--row", "demo-stack--align-center");

        row.add(AvatarBuilder.create("XS").withThemeVariants(AvatarVariant.LUMO_XSMALL).build());
        row.add(AvatarBuilder.create("SM").withThemeVariants(AvatarVariant.LUMO_SMALL).build());
        row.add(AvatarBuilder.create("MD").build());
        row.add(AvatarBuilder.create("LG").withThemeVariants(AvatarVariant.LUMO_LARGE).build());
        row.add(AvatarBuilder.create("XL").withThemeVariants(AvatarVariant.LUMO_XLARGE).build());

        return new DemoExample("Size variants", row, """
                AvatarBuilder.create("XS").withThemeVariants(AvatarVariant.LUMO_XSMALL).build();
                AvatarBuilder.create("SM").withThemeVariants(AvatarVariant.LUMO_SMALL).build();
                AvatarBuilder.create("MD").build();   // default
                AvatarBuilder.create("LG").withThemeVariants(AvatarVariant.LUMO_LARGE).build();
                AvatarBuilder.create("XL").withThemeVariants(AvatarVariant.LUMO_XLARGE).build();
                """);
    }

    // ── Example 11 ────────────────────────────────────────────────────────────

    private DemoExample avatarGroupExample() {
        AvatarGroup group = AvatarGroupBuilder.create()
                .add(new AvatarGroup.AvatarGroupItem("Alice"))
                .add(new AvatarGroup.AvatarGroupItem("Bob"))
                .add(new AvatarGroup.AvatarGroupItem("Carol"))
                .add(new AvatarGroup.AvatarGroupItem("Dave"))
                .build();

        return new DemoExample("AvatarGroup — basic group", group, """
                AvatarGroupBuilder.create()
                    .add(new AvatarGroup.AvatarGroupItem("Alice"))
                    .add(new AvatarGroup.AvatarGroupItem("Bob"))
                    .build();
                """);
    }

    // ── Example 12 ────────────────────────────────────────────────────────────

    private DemoExample avatarGroupOverflowExample() {
        AvatarGroup group = AvatarGroupBuilder.create()
                .maxItemsVisible(3)
                .add(
                    new AvatarGroup.AvatarGroupItem("Alice"),
                    new AvatarGroup.AvatarGroupItem("Bob"),
                    new AvatarGroup.AvatarGroupItem("Carol"),
                    new AvatarGroup.AvatarGroupItem("Dave"),
                    new AvatarGroup.AvatarGroupItem("Eve"),
                    new AvatarGroup.AvatarGroupItem("Frank")
                )
                .build();

        return new DemoExample("AvatarGroup — overflow counter (maxItemsVisible=3)", group, """
                AvatarGroupBuilder.create()
                    .maxItemsVisible(3)
                    .add(/* 6 items */)
                    .build();
                """);
    }

    // ── Example 13 ────────────────────────────────────────────────────────────

    private DemoExample avatarGroupColoredExample() {
        String[] names = {"Alice B.", "Bob C.", "Carol D.", "Dave E.", "Eve F.", "Frank G.", "Grace H."};
        AvatarGroup.AvatarGroupItem[] items = new AvatarGroup.AvatarGroupItem[names.length];
        for (int i = 0; i < names.length; i++) {
            var item = new AvatarGroup.AvatarGroupItem(names[i]);
            item.setColorIndex(AvatarColor.forId(i + 1L).getIndex());
            items[i] = item;
        }

        AvatarGroup group = AvatarGroupBuilder.create().add(items).build();

        return new DemoExample("AvatarGroup — AvatarColor.forId per member", group, """
                for (User user : activeUsers) {
                    var item = new AvatarGroup.AvatarGroupItem(user.getName());
                    item.setColorIndex(AvatarColor.forId(user.getId()).getIndex());
                    group.add(item);
                }
                """);
    }
}



