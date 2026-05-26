package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.builders.DetailsBuilder;
import com.holonplatform.vaadin.flow.demo.ui.DemoExample;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;


/**
 * Demo page for the {@link Details} component via Holon's fluent
 * {@link DetailsBuilder}.
 *
 * <p>Covers:
 * <ol>
 *   <li>Basic Details with summary text</li>
 *   <li>Initially opened Details</li>
 *   <li>Theme variants (filled, reverse)</li>
 *   <li>Component summary and rich content</li>
 * </ol>
 */
@PageTitle("Details – Holon Demo")
@Route(value = "details", layout = DemoMainLayout.class)
public class DetailsDemoView extends Div {

    public DetailsDemoView() {
        addClassName("app-view");

        var title = new H1("Details");

        var desc = new Paragraph(
                "Fluent builder for Vaadin's Details (collapsible section) component. "
                + "Supports summary text or component, opened state, theme variants, "
                + "and arbitrary content — all via the Holon builder pattern.");

        var examples = ResponsiveDiv.flex().column().gapL().build();

        examples.add(basicExample());
        examples.add(openedExample());
        examples.add(themeVariantsExample());
        examples.add(richContentExample());

        add(title, desc, examples);
    }

    // ── Example builders ────────────────────────────────────────────────────

    private DemoExample basicExample() {
        var preview = new Div();

        Details details1 = DetailsBuilder.create()
                .summaryText("What is your refund policy?")
                .add(new Paragraph("We offer a full refund within 30 days of purchase, no questions asked."))
                .build();

        Details details2 = DetailsBuilder.create()
                .summaryText("How do I cancel my subscription?")
                .add(new Paragraph("Go to Settings → Billing → Cancel Plan. Your access continues until the end of the billing period."))
                .build();

        Details details3 = DetailsBuilder.create()
                .summaryText("Can I change my plan later?")
                .add(new Paragraph("Yes, you can upgrade or downgrade at any time from your account dashboard."))
                .build();

        preview.add(details1, details2, details3);

        return new DemoExample("Basic FAQ", preview, """
                Details details = DetailsBuilder.create()
                    .summaryText("What is your refund policy?")
                    .add(new Paragraph("Full refund within 30 days."))
                    .build();
                """);
    }

    private DemoExample openedExample() {
        var preview = new Div();

        Details details = DetailsBuilder.create()
                .summaryText("System Requirements")
                .opened(true)
                .add(new Paragraph("Java 21 or later"))
                .add(new Paragraph("Maven 3.9+"))
                .add(new Paragraph("Node.js 18+ (for frontend builds)"))
                .build();

        preview.add(details);

        return new DemoExample("Initially Opened", preview, """
                Details details = DetailsBuilder.create()
                    .summaryText("System Requirements")
                    .opened(true)
                    .add(new Paragraph("Java 21 or later"))
                    .add(new Paragraph("Maven 3.9+"))
                    .add(new Paragraph("Node.js 18+"))
                    .build();
                """);
    }

    private DemoExample themeVariantsExample() {
        var preview = new Div();

        Details filled = DetailsBuilder.create()
                .summaryText("Filled variant")
                .withThemeVariants(DetailsVariant.FILLED)
                .add(new Paragraph("This details panel uses the FILLED theme variant for a subtle background."))
                .build();

        Details reverse = DetailsBuilder.create()
                .summaryText("Reverse variant")
                .withThemeVariants(DetailsVariant.REVERSE)
                .add(new Paragraph("This details panel uses the REVERSE theme variant — content appears above the summary."))
                .build();

        Details filledReverse = DetailsBuilder.create()
                .summaryText("Filled + Reverse")
                .withThemeVariants(DetailsVariant.FILLED, DetailsVariant.REVERSE)
                .add(new Paragraph("Both FILLED and REVERSE variants combined."))
                .build();

        preview.add(filled, reverse, filledReverse);

        return new DemoExample("Theme Variants", preview, """
                // Filled
                DetailsBuilder.create()
                    .summaryText("Filled variant")
                    .withThemeVariants(DetailsVariant.FILLED)
                    .add(new Paragraph("Subtle background."))
                    .build();

                // Reverse
                DetailsBuilder.create()
                    .summaryText("Reverse variant")
                    .withThemeVariants(DetailsVariant.REVERSE)
                    .add(new Paragraph("Content above summary."))
                    .build();
                """);
    }

    private DemoExample richContentExample() {
        var preview = new Div();

        var summaryComponent = new Span("Contact Information");

        Details details = DetailsBuilder.create()
                .summary(summaryComponent)
                .add(new Paragraph("Email: support@example.com"))
                .add(new Paragraph("Phone: +1 (555) 123-4567"))
                .add(new Paragraph("Hours: Mon–Fri, 9 AM – 5 PM EST"))
                .styleName("rich-details")
                .id("contact-details")
                .build();

        preview.add(details);

        return new DemoExample("Component Summary + Rich Content", preview, """
                var summaryComponent = new Span("Contact Information");

                Details details = DetailsBuilder.create()
                    .summary(summaryComponent)
                    .add(new Paragraph("Email: support@example.com"))
                    .add(new Paragraph("Phone: +1 (555) 123-4567"))
                    .add(new Paragraph("Hours: Mon-Fri, 9 AM - 5 PM"))
                    .styleName("rich-details")
                    .id("contact-details")
                    .build();
                """);
    }
}
