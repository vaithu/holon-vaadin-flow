package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.LabelBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.iyensoft.vaadin.flow.components.builders.CardBuilder;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.card.CardVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the Vaadin {@link com.vaadin.flow.component.card.Card} component
 * accessed via the {@link CardBuilder} fluent API.
 *
 * <p>Covers every slot and all built-in theme variants — no custom CSS is added to the card itself.
 *
 * <ol>
 *   <li>Minimal – title + body</li>
 *   <li>Title + Subtitle</li>
 *   <li>Header-prefix, suffix, title &amp; subtitle together</li>
 *   <li>Custom header slot (overrides title/subtitle)</li>
 *   <li>Media slot (Avatar as media)</li>
 *   <li>Footer with action buttons</li>
 *   <li>OUTLINED, ELEVATED, HORIZONTAL, STRETCH_MEDIA, COVER_MEDIA variants + combinations</li>
 *   <li>Full-featured card (every slot populated)</li>
 * </ol>
 */
@PageTitle("Card – Holon Demo")
@Route(value = "card", layout = DemoMainLayout.class)
public class CardDemoView extends Div {

    public CardDemoView() {
        addClassName("app-view");

        var title = new H1("Card");

        var desc = new Paragraph(
                "The Vaadin Card component is a versatile container for grouping related content and actions. " +
                "It exposes distinct slots — media, title, subtitle, header, headerPrefix, headerSuffix, " +
                "body content, and footer — all wired via the fluent CardBuilder API. " +
                "Five built-in theme variants (OUTLINED, ELEVATED, HORIZONTAL, STRETCH_MEDIA, COVER_MEDIA) " +
                "can be combined freely.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(minimalExample());
        examples.add(titleSubtitleExample());
        examples.add(headerSlotsExample());
        examples.add(customHeaderExample());
        examples.add(mediaSlotExample());
        examples.add(footerActionsExample());
        examples.add(themeVariantsExample());
        examples.add(fullFeaturedExample());

        add(title, desc, examples);
    }

    // ── Examples ──────────────────────────────────────────────────────────────

    /**
     * Example 1 – simplest possible card: title + body content.
     */
    private DemoExample minimalExample() {
        var card = CardBuilder.create()
                .title(LabelBuilder.span().text("Hello, Card"))
                .add(new Paragraph(
                        "This is the simplest Card: just a title and body content. " +
                        "No media, no subtitle, no footer."))
                .build();

        return new DemoExample("Minimal – title + body", card, """
                CardBuilder.create()
                    .title(LabelBuilder.span().text("Hello, Card"))
                    .add(new Paragraph("Body content goes here."))
                    .build();
                """);
    }

    /**
     * Example 2 – title and subtitle slots.
     * The subtitle is rendered below the title with secondary-text styling.
     */
    private DemoExample titleSubtitleExample() {
        var preview = new Div();

        preview.add(
                CardBuilder.create()
                        .title(LabelBuilder.span().text("Getting Started"))
                        .subtitle(new Span("Step 1 of 3"))
                        .add(new Paragraph(
                                "Learn the basics of building applications with Vaadin Flow and Holon Platform."))
                        .build(),
                CardBuilder.create()
                        .title(LabelBuilder.span().text("Project Overview"))
                        .subtitle(new Span("Last updated: today"))
                        .add(new Paragraph(
                                "The subtitle slot is always rendered below the title and is styled " +
                                "with secondary text colour."))
                        .build()
        );

        return new DemoExample("Title + Subtitle", preview, """
                // subtitle() accepts any Component — use Span for simple text
                CardBuilder.create()
                    .title(LabelBuilder.span().text("Getting Started"))
                    .subtitle(new Span("Step 1 of 3"))
                    .add(new Paragraph("Body text."))
                    .build();
                """);
    }

    /**
     * Example 3 – headerPrefix, title, subtitle and headerSuffix together.
     * headerPrefix appears before the title area; headerSuffix after it.
     */
    private DemoExample headerSlotsExample() {
        var preview = new Div();

        preview.add(
                CardBuilder.create()
                        .headerPrefix(new Avatar("Anna Brown"))
                        .title(LabelBuilder.span().text("Anna Brown"))
                        .subtitle(new Span("anna.brown@example.com"))
                        .headerSuffix(new Span("Admin"))
                        .add(new Paragraph("Last login: today at 14:32"))
                        .build(),
                CardBuilder.create()
                        .headerPrefix(new Avatar("TK"))
                        .title(LabelBuilder.span().text("Team Kanban"))
                        .subtitle(new Span("Sprint 12 · 8 open tasks"))
                        .headerSuffix(new Span("In Progress"))
                        .add(new Paragraph("Track sprint tasks on the Kanban board view."))
                        .build()
        );

        return new DemoExample("headerPrefix · title · subtitle · headerSuffix", preview, """
                // headerPrefix – any Component before the title area (Avatar, Icon, …)
                // headerSuffix – any Component after the title area (Badge, Button, Span, …)
                CardBuilder.create()
                    .headerPrefix(new Avatar("Anna Brown"))
                    .title(LabelBuilder.span().text("Anna Brown"))
                    .subtitle(new Span("anna.brown@example.com"))
                    .headerSuffix(new Span("Admin"))
                    .add(new Paragraph("Last login: today at 14:32"))
                    .build();
                """);
    }

    /**
     * Example 4 – custom header slot.
     * Using header() gives complete layout control over the header area and
     * automatically overrides the title and subtitle slots.
     */
    private DemoExample customHeaderExample() {
        var customHeader = new Div();
        var heading = new H3("Vaadin Platform");
        var sub = new Span("Enterprise · v25.1");
        customHeader.add(heading, sub);

        var card = CardBuilder.create()
                .header(customHeader)
                .add(new Paragraph(
                        "The header() slot accepts any Component and overrides the title and subtitle slots. " +
                        "Use it when you need full control over heading structure or typography."))
                .build();

        return new DemoExample("Custom header() slot (overrides title + subtitle)", card, """
                // header() overrides title + subtitle — gives full layout control
                var customHeader = new Div();
                customHeader.add(new H3("Vaadin Platform"), new Span("Enterprise · v25.1"));

                CardBuilder.create()
                    .header(customHeader)
                    .add(new Paragraph("Body text."))
                    .build();
                """);
    }

    /**
     * Example 5 – media slot.
     * The media slot renders before all other card content regardless of DOM order.
     */
    private DemoExample mediaSlotExample() {
        var preview = new Div();

        preview.add(
                CardBuilder.create()
                        .media(new Avatar("John Smith"))
                        .title(LabelBuilder.span().text("John Smith"))
                        .subtitle(new Span("Software Engineer"))
                        .add(new Paragraph("Building enterprise applications with Java and Vaadin Flow."))
                        .build(),
                CardBuilder.create()
                        .media(new Avatar("DB"))
                        .title(LabelBuilder.span().text("Design Board"))
                        .subtitle(new Span("3 members · 12 cards"))
                        .add(new Paragraph(
                                "The media slot is rendered before all other card content, " +
                                "regardless of the order it is set in the builder chain."))
                        .build()
        );

        return new DemoExample("Media slot", preview, """
                // media() accepts any Component — Avatar, Icon, Image, etc.
                // Rendered before all other content regardless of DOM order.
                CardBuilder.create()
                    .media(new Avatar("John Smith"))
                    .title(LabelBuilder.span().text("John Smith"))
                    .subtitle(new Span("Software Engineer"))
                    .add(new Paragraph("Body text."))
                    .build();
                """);
    }

    /**
     * Example 6 – footer with action buttons.
     * The footer is anchored to the bottom of the card even when the card grows taller.
     */
    private DemoExample footerActionsExample() {
        var preview = new Div();

        preview.add(
                CardBuilder.create()
                        .title(LabelBuilder.span().text("Project Alpha"))
                        .add(new Paragraph(
                                "A long-running project in its final delivery phase. " +
                                "All critical milestones have been completed."))
                        .withFooter(new Button("Open"), new Button("Archive"))
                        .build(),
                CardBuilder.create()
                        .title(LabelBuilder.span().text("Unsaved Draft"))
                        .subtitle(new Span("Last edited 3 min ago"))
                        .add(new Paragraph(
                                "This document has unsaved changes. " +
                                "The footer stays anchored to the card bottom even as content grows."))
                        .withFooter(new Button("Save"), new Button("Discard"))
                        .build()
        );

        return new DemoExample("Footer with action buttons", preview, """
                // withFooter() anchors content to the card bottom
                CardBuilder.create()
                    .title(LabelBuilder.span().text("Project Alpha"))
                    .add(new Paragraph("Description."))
                    .withFooter(new Button("Open"), new Button("Archive"))
                    .build();
                """);
    }

    /**
     * Example 7 – all five built-in theme variants.
     * OUTLINED, ELEVATED, HORIZONTAL, STRETCH_MEDIA, COVER_MEDIA can be combined freely.
     */
    private DemoExample themeVariantsExample() {
        var preview = new Div();

        // Default (no variant)
        preview.add(
                CardBuilder.create()
                        .title(LabelBuilder.span().text("Default (no variant)"))
                        .add(new Paragraph("The base card style with no theme variant applied."))
                        .build()
        );

        // OUTLINED
        preview.add(
                CardBuilder.create()
                        .withThemeVariants(CardVariant.OUTLINED)
                        .title(LabelBuilder.span().text("OUTLINED"))
                        .add(new Paragraph("Adds a solid border outline to the card."))
                        .build()
        );

        // ELEVATED
        preview.add(
                CardBuilder.create()
                        .withThemeVariants(CardVariant.ELEVATED)
                        .title(LabelBuilder.span().text("ELEVATED"))
                        .add(new Paragraph("Adds an elevated appearance with a drop shadow. Best on shaded backgrounds."))
                        .build()
        );

        // HORIZONTAL (requires media to be meaningful)
        preview.add(
                CardBuilder.create()
                        .withThemeVariants(CardVariant.HORIZONTAL)
                        .media(new Avatar("HL"))
                        .title(LabelBuilder.span().text("HORIZONTAL"))
                        .subtitle(new Span("Media beside content"))
                        .add(new Paragraph("Places all card content alongside the media slot instead of below it."))
                        .build()
        );

        // STRETCH_MEDIA
        preview.add(
                CardBuilder.create()
                        .withThemeVariants(CardVariant.STRETCH_MEDIA)
                        .media(new Avatar("SM"))
                        .title(LabelBuilder.span().text("STRETCH_MEDIA"))
                        .add(new Paragraph("Stretches an image or icon media element to the full card width (or height when horizontal)."))
                        .build()
        );

        // COVER_MEDIA
        preview.add(
                CardBuilder.create()
                        .withThemeVariants(CardVariant.COVER_MEDIA)
                        .media(new Avatar("CM"))
                        .title(LabelBuilder.span().text("COVER_MEDIA"))
                        .add(new Paragraph("Like STRETCH_MEDIA but also covers the card's padding area. Overrides STRETCH_MEDIA."))
                        .build()
        );

        // OUTLINED + ELEVATED combined
        preview.add(
                CardBuilder.create()
                        .withThemeVariants(CardVariant.OUTLINED, CardVariant.ELEVATED)
                        .title(LabelBuilder.span().text("OUTLINED + ELEVATED"))
                        .add(new Paragraph("Variants can be combined freely — border and shadow at the same time."))
                        .withFooter(new Button("Action"))
                        .build()
        );

        // HORIZONTAL + OUTLINED
        preview.add(
                CardBuilder.create()
                        .withThemeVariants(CardVariant.HORIZONTAL, CardVariant.OUTLINED)
                        .media(new Avatar("HO"))
                        .title(LabelBuilder.span().text("HORIZONTAL + OUTLINED"))
                        .subtitle(new Span("Two variants combined"))
                        .add(new Paragraph("Horizontal layout with an outlined border."))
                        .build()
        );

        return new DemoExample(
                "Theme variants: OUTLINED · ELEVATED · HORIZONTAL · STRETCH_MEDIA · COVER_MEDIA", preview, """
                // Five built-in variants — all combinable:
                // OUTLINED  – solid border
                // ELEVATED  – drop shadow
                // HORIZONTAL – media beside content
                // STRETCH_MEDIA – media spans full card width / height
                // COVER_MEDIA   – media covers the card padding area

                CardBuilder.create()
                    .withThemeVariants(CardVariant.OUTLINED)
                    .title(LabelBuilder.span().text("Outlined"))
                    .add(new Paragraph("Solid border."))
                    .build();

                CardBuilder.create()
                    .withThemeVariants(CardVariant.HORIZONTAL)
                    .media(new Avatar("HL"))
                    .title(LabelBuilder.span().text("Horizontal"))
                    .add(new Paragraph("Media beside content."))
                    .build();

                // Combine freely:
                CardBuilder.create()
                    .withThemeVariants(CardVariant.OUTLINED, CardVariant.ELEVATED)
                    .title(LabelBuilder.span().text("Outlined + Elevated"))
                    .withFooter(new Button("Action"))
                    .build();
                """);
    }

    /**
     * Example 8 – fully populated card using every available slot.
     */
    private DemoExample fullFeaturedExample() {
        var card = CardBuilder.create()
                .withThemeVariants(CardVariant.OUTLINED)
                // Media slot
                .media(new Avatar("EJ"))
                // Header slots
                .headerPrefix(new Avatar("EJ"))
                .title(LabelBuilder.span().text("Emma Johnson"))
                .subtitle(new Span("Lead Architect · Platform Team"))
                .headerSuffix(new Span("Active"))
                // Body content
                .add(new Paragraph(
                        "Emma leads the platform architecture team. Her focus areas are distributed systems, " +
                        "API design, and developer experience."))
                // Footer
                .withFooter(new Button("View Profile"), new Button("Message"))
                .build();

        return new DemoExample("Full-featured card (every slot populated)", card, """
                // All slots in a single builder chain:
                CardBuilder.create()
                    .withThemeVariants(CardVariant.OUTLINED)

                    // Media — rendered at the very top
                    .media(new Avatar("EJ"))

                    // Header area
                    .headerPrefix(new Avatar("EJ"))
                    .title(LabelBuilder.span().text("Emma Johnson"))
                    .subtitle(new Span("Lead Architect · Platform Team"))
                    .headerSuffix(new Span("Active"))

                    // Body
                    .add(new Paragraph("Emma leads the platform architecture team…"))

                    // Footer — anchored to card bottom
                    .withFooter(new Button("View Profile"), new Button("Message"))

                    .build();
                """);
    }
}

