package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueItem;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueItem.Category;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueItem.DeltaDirection;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueItem.PillVariant;
import com.holonplatform.vaadin.flow.vaadinplus.KeyValueList;
import com.holonplatform.vaadin.flow.vaadinplus.ResponsiveDiv;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Demo page for {@link KeyValueList} / {@link KeyValueItem}.
 *
 * <p>Five numbered pattern sections that pixel-perfectly replicate the Nexus
 * key-value design reference:
 * <ol>
 *   <li>Account Summary  — core row variants (text, mono, numeric, pill, text-wrap, muted)</li>
 *   <li>Editable &amp; Assigned Values — identity values, hover-reveal edit buttons</li>
 *   <li>Dashboard Summary Cards — delta trends, pills, 4-card responsive grid</li>
 *   <li>Confirmation Step — dense rows, review banner</li>
 *   <li>Widescreen Detail Layout — 2-column: detail card + activity side panel</li>
 * </ol>
 */
@PageTitle("KeyValueList Holon Demo")
@Route(value = "key-value-list", layout = DemoMainLayout.class)
public class KeyValueListDemoView extends Div {

    public KeyValueListDemoView() {
        addClassName("app-view");
        getStyle()
            .set("background", "#f8fafc")
            .set("min-height", "100vh")
            .set("padding", "2.5rem 1.5rem");

        var sections = new Div(
            pageHeader(),
            section("01", "Account Summary",
                "Core row variants: text, monospace, numeric, pill, text-wrap, muted, and 2 px category accent bars.",
                pattern01(), CODE_01),
            section("02", "Editable & Assigned Values",
                "Identity values with circular avatar initials, hover-to-reveal edit buttons, and read-only rows.",
                pattern02(), CODE_02),
            section("03", "Dashboard Summary Cards",
                "Four summary cards with delta trend annotations and status pills in a responsive 2-column grid.",
                pattern03(), CODE_03),
            section("04", "Form Review \u2014 Confirmation Step",
                "Dense compact rows for scanning key transfer details before final submission.",
                pattern04(), CODE_04),
            section("05", "Widescreen Detail Layout",
                "Two-column pattern: primary detail card on the left, activity side panel on the right.",
                pattern05(), CODE_05)
        );
        sections.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column")
            .set("gap", "4rem")
            .set("max-width", "900px")
            .set("margin", "0 auto");

        add(sections);
    }

    // -------------------------------------------------------------------------
    // Page header
    // -------------------------------------------------------------------------

    private Div pageHeader() {
        var h1 = new H1("Key-Value Patterns");
        h1.getStyle()
            .set("font-size", "1.875rem")
            .set("font-weight", "700")
            .set("color", "#0f172a")
            .set("margin", "0 0 0.5rem 0");

        var sub = new Paragraph(
            "Pixel-perfect implementation of the Nexus key-value design pattern. " +
            "Flex-per-row architecture: 168 px fixed label, right-aligned value, 2 px absolute accent bar.");
        sub.getStyle()
            .set("font-size", "1rem")
            .set("color", "#64748b")
            .set("margin", "0")
            .set("line-height", "1.6");

        var div = new Div(h1, sub);
        div.getStyle().set("margin-bottom", "0.5rem");
        return div;
    }

    // -------------------------------------------------------------------------
    // Layout helpers
    // -------------------------------------------------------------------------

    /** Numbered section wrapper with label, h2, description, and a Preview / Code toggle. */
    private Div section(String num, String title, String note, Component preview, String code) {
        var numSpan = new Span(num);
        numSpan.getStyle()
            .set("font-size", "0.6875rem")
            .set("font-weight", "700")
            .set("color", "#7c3aed")
            .set("letter-spacing", "0.12em")
            .set("text-transform", "uppercase");

        var h2 = new H2(title);
        h2.getStyle()
            .set("font-size", "1.125rem")
            .set("font-weight", "700")
            .set("color", "#0f172a")
            .set("margin", "0");

        var labelRow = new Div(numSpan, h2);
        labelRow.getStyle()
            .set("display", "flex")
            .set("align-items", "baseline")
            .set("gap", "0.875rem")
            .set("margin-bottom", "0.375rem");

        var noteP = new Paragraph(note);
        noteP.getStyle()
            .set("font-size", "0.875rem")
            .set("color", "#64748b")
            .set("line-height", "1.6")
            .set("margin", "0 0 1.25rem 0");

        var sec = new Div();
        sec.add(labelRow, noteP, previewWithCode(preview, code));
        return sec;
    }

    /**
     * Wraps the live {@code preview} and its Java {@code code} snippet in a
     * Preview / Code tab toggle, mirroring the shared {@code DemoExample} pattern
     * used across the other demo views.
     */
    private Component previewWithCode(Component preview, String code) {
        var pre = new Pre();
        pre.getElement().setText(code);
        pre.getStyle()
            .set("margin", "0")
            .set("padding", "1rem 1.25rem")
            .set("font-size", "0.8125rem")
            .set("line-height", "1.55")
            .set("overflow", "auto")
            .set("background", "#0f172a")
            .set("color", "#e2e8f0")
            .set("border-radius", "0.875rem")
            .set("white-space", "pre");
        var codePane = new Div(pre);

        Div container = ResponsiveDiv.flex().column().gapM().marginS().build();

        var lazyTabs = Components.lazyTabs()
            .withContainer(container)
            .withLazyTab("Preview", () -> preview)
            .withLazyTab("Code", () -> codePane);

        return new Div(lazyTabs.getTabs(), lazyTabs.getContentContainer());
    }

    /** Standard card wrapper with header row (title + meta). */
    private Div card(String title, String meta, Component body) {
        var h3 = new H3(title);
        h3.getStyle()
            .set("font-size", "0.875rem")
            .set("font-weight", "700")
            .set("color", "#0f172a")
            .set("margin", "0");

        var metaSpan = new Span(meta);
        metaSpan.getStyle()
            .set("font-size", "0.75rem")
            .set("color", "#94a3b8");

        var head = new Div(h3, metaSpan);
        head.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "space-between")
            .set("padding", "1rem 1.25rem")
            .set("border-bottom", "1px solid #f1f5f9");

        var card = new Div(head, body);
        card.getStyle()
            .set("background", "#fff")
            .set("border", "1px solid #e2e8f0")
            .set("border-radius", "0.875rem")
            .set("overflow", "hidden")
            .set("box-shadow", "0 1px 3px rgb(0 0 0 / 0.06), 0 1px 2px rgb(0 0 0 / 0.04)");
        return card;
    }

    // -------------------------------------------------------------------------
    // Pattern 01 — Account Summary
    // -------------------------------------------------------------------------

    private Component pattern01() {
        var list = new KeyValueList();
        list.addItem(KeyValueItem.of("Full name", "Marcus Whitfield"));
        list.addItem(
            KeyValueItem.of("Account number", "AC-4471-9902")
                .setValueMono(true)
                .setShowCopyButton(true));
        list.addItem(
            KeyValueItem.of("Current balance", "$18,240.55")
                .setCategory(Category.FINANCIAL)
                .setValueNumeric(true));
        list.addItem(
            KeyValueItem.pill("Account status", "Active", PillVariant.SUCCESS)
                .setCategory(Category.STATUS));
        list.addItem(
            KeyValueItem.pill("Compliance flag", "Review pending", PillVariant.WARNING)
                .setCategory(Category.ALERT));
        list.addItem(
            KeyValueItem.builder()
                .key("Notes")
                .value("Client requested paper statements only \u2014 do not email. "
                    + "Three failed login attempts on 15 Jun.")
                .textWrap(true)
                .build());
        list.addItem(
            KeyValueItem.of("Referred by", "Not provided")
                .setValueMuted(true));
        return card("Account Summary", "Last synced 2 min ago", list);
    }

    // -------------------------------------------------------------------------
    // Pattern 02 — Editable & Assigned Values
    // -------------------------------------------------------------------------

    private Component pattern02() {
        var list = new KeyValueList();
        list.addItem(KeyValueItem.of("Account holder", "Marcus Whitfield").setEditable(true));
        list.addItem(KeyValueItem.of("Contact email", "m.whitfield@example.com").setEditable(true));
        list.addItem(KeyValueItem.of("Primary phone", "+1 (415) 555-0182").setEditable(true));
        list.addItem(KeyValueItem.identity("Assigned advisor", "JC", "Jordan Cole").setEditable(true));
        list.addItem(KeyValueItem.identity("Last modified by", "SP", "Sarah Patterson"));
        list.addItem(KeyValueItem.of("Support tier", "Premium").setEditable(true));
        return card("Client Record", "ID: ACC-0042", list);
    }

    // -------------------------------------------------------------------------
    // Pattern 03 — Dashboard Summary Cards (2 x 2 grid)
    // -------------------------------------------------------------------------

    private Component pattern03() {
        var grid = new Div();
        grid.getStyle()
            .set("display", "grid")
            .set("grid-template-columns", "repeat(2, 1fr)")
            .set("gap", "1rem");

        grid.add(revenueCard());
        grid.add(customerCard());
        grid.add(operationsCard());
        grid.add(riskCard());
        return grid;
    }

    private Div revenueCard() {
        var list = new KeyValueList().asSummary();
        list.addItem(
            KeyValueItem.withDelta("Monthly revenue", "$84,120", "+4.6%", DeltaDirection.UP)
                .setCategory(Category.FINANCIAL));
        list.addItem(
            KeyValueItem.withDelta("Net profit", "$31,800", "+2.1%", DeltaDirection.UP)
                .setCategory(Category.FINANCIAL));
        list.addItem(
            KeyValueItem.withDelta("Burn rate", "$12,400", "\u22126.2%", DeltaDirection.DOWN)
                .setCategory(Category.ALERT));
        return summaryCard("Revenue", "Jun 2026", list);
    }

    private Div customerCard() {
        var list = new KeyValueList().asSummary();
        list.addItem(
            KeyValueItem.withDelta("Active customers", "4,821", "+312", DeltaDirection.UP));
        list.addItem(
            KeyValueItem.withDelta("Churn rate", "1.8%", "\u22120.3pt", DeltaDirection.DOWN));
        list.addItem(
            KeyValueItem.withDelta("NPS score", "72", "+5", DeltaDirection.UP)
                .setCategory(Category.STATUS));
        return summaryCard("Customers", "Trailing 30d", list);
    }

    private Div operationsCard() {
        var list = new KeyValueList().asSummary();
        list.addItem(
            KeyValueItem.pill("System health", "Operational", PillVariant.SUCCESS)
                .setCategory(Category.STATUS));
        list.addItem(
            KeyValueItem.of("Avg response time", "142 ms")
                .setValueNumeric(true));
        list.addItem(
            KeyValueItem.pill("Incidents open", "2 critical", PillVariant.DANGER)
                .setCategory(Category.ALERT));
        return summaryCard("Operations", "Live", list);
    }

    private Div riskCard() {
        var list = new KeyValueList().asSummary();
        list.addItem(
            KeyValueItem.pill("Fraud alerts", "6 flagged", PillVariant.WARNING)
                .setCategory(Category.ALERT));
        list.addItem(
            KeyValueItem.pill("Compliance", "Passed", PillVariant.SUCCESS)
                .setCategory(Category.STATUS));
        list.addItem(
            KeyValueItem.of("Next audit", "30 Sep 2026")
                .setCategory(Category.FINANCIAL));
        return summaryCard("Risk & Compliance", "Q3 snapshot", list);
    }

    /** Compact card wrapper for dashboard summary cards (no box-shadow, smaller head). */
    private Div summaryCard(String title, String meta, Component body) {
        var h3 = new H3(title);
        h3.getStyle()
            .set("font-size", "0.8125rem")
            .set("font-weight", "700")
            .set("color", "#0f172a")
            .set("margin", "0");

        var metaSpan = new Span(meta);
        metaSpan.getStyle()
            .set("font-size", "0.6875rem")
            .set("color", "#94a3b8");

        var head = new Div(h3, metaSpan);
        head.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "space-between")
            .set("padding", "0.875rem 1.25rem")
            .set("border-bottom", "1px solid #f1f5f9");

        var card = new Div(head, body);
        card.getStyle()
            .set("background", "#fff")
            .set("border", "1px solid #e2e8f0")
            .set("border-radius", "0.875rem")
            .set("overflow", "hidden");
        return card;
    }

    // -------------------------------------------------------------------------
    // Pattern 04 — Confirmation Step
    // -------------------------------------------------------------------------

    private Component pattern04() {
        // Review banner
        var icon = new Span("\u2713");
        icon.getStyle()
            .set("display", "inline-flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("width", "28px").set("height", "28px")
            .set("border-radius", "50%")
            .set("background", "#f0fdf4")
            .set("color", "#16a34a")
            .set("font-weight", "700")
            .set("flex-shrink", "0");

        var bannerTitle = new Span("Review your details");
        bannerTitle.getStyle()
            .set("font-size", "0.875rem")
            .set("font-weight", "700")
            .set("color", "#0f172a")
            .set("display", "block");

        var bannerSub = new Span("Please confirm everything looks correct before submitting.");
        bannerSub.getStyle()
            .set("font-size", "0.8125rem")
            .set("color", "#64748b")
            .set("display", "block");

        var bannerText = new Div(bannerTitle, bannerSub);

        var banner = new Div(icon, bannerText);
        banner.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("gap", "0.875rem")
            .set("background", "#f0fdf4")
            .set("border", "1px solid #bbf7d0")
            .set("border-radius", "0.875rem")
            .set("padding", "1rem 1.25rem")
            .set("margin-bottom", "0.875rem");

        // Dense list inside a card
        var list = new KeyValueList().asDense();
        list.addItem(KeyValueItem.of("Transfer from", "Checking \u00b74471"));
        list.addItem(
            KeyValueItem.of("Transfer to", "Savings \u00b79902")
                .setCategory(Category.FINANCIAL));
        list.addItem(
            KeyValueItem.of("Amount", "$5,000.00")
                .setCategory(Category.FINANCIAL)
                .setValueNumeric(true));
        list.addItem(KeyValueItem.of("Transfer type", "Immediate"));
        list.addItem(
            KeyValueItem.of("Reference", "REF-20260615-001")
                .setValueMono(true));
        list.addItem(
            KeyValueItem.pill("Fee", "No fee \u2014 included in Premium", PillVariant.SUCCESS));

        var denseCard = card("Transfer Confirmation", "Step 3 of 3", list);

        var wrap = new Div(banner, denseCard);
        wrap.getStyle()
            .set("display", "flex")
            .set("flex-direction", "column");
        return wrap;
    }

    // -------------------------------------------------------------------------
    // Pattern 05 — Widescreen Detail Layout
    // -------------------------------------------------------------------------

    private Component pattern05() {
        // Left: primary detail card
        var detail = new KeyValueList();
        detail.addItem(KeyValueItem.of("Account type", "Premium Savings"));
        detail.addItem(
            KeyValueItem.of("Account number", "AC-4471-9902")
                .setValueMono(true)
                .setShowCopyButton(true));
        detail.addItem(
            KeyValueItem.of("Routing number", "021000021")
                .setValueMono(true));
        detail.addItem(KeyValueItem.of("Opened on", "14 Mar 2019"));
        detail.addItem(
            KeyValueItem.of("Interest rate", "4.75% APY")
                .setCategory(Category.FINANCIAL)
                .setValueNumeric(true));
        detail.addItem(
            KeyValueItem.of("Available balance", "$18,240.55")
                .setCategory(Category.FINANCIAL)
                .setValueNumeric(true));
        detail.addItem(
            KeyValueItem.pill("Status", "Active", PillVariant.SUCCESS)
                .setCategory(Category.STATUS));
        var leftCard = card("Account Details", "ACC-0042", detail);

        // Right: activity side panel (asSummary — transparent, no card chrome)
        var activity = new KeyValueList().asSummary();
        activity.addItem(
            KeyValueItem.builder()
                .key("Jun 15").value("+$2,400.00").superText("DIRECT DEPOSIT")
                .numeric(true).category(Category.STATUS).build());
        activity.addItem(
            KeyValueItem.builder()
                .key("Jun 14").value("\u2212$340.00").superText("BILL PAYMENT")
                .numeric(true).category(Category.ALERT).build());
        activity.addItem(
            KeyValueItem.builder()
                .key("Jun 13").value("\u2212$84.50").superText("PURCHASE")
                .numeric(true).build());
        activity.addItem(
            KeyValueItem.builder()
                .key("Jun 12").value("+$5,000.00").superText("TRANSFER IN")
                .numeric(true).category(Category.STATUS).build());
        activity.addItem(
            KeyValueItem.builder()
                .key("Jun 10").value("\u2212$1,200.00").superText("WIRE OUT")
                .numeric(true).category(Category.ALERT).build());
        var rightCard = summaryCard("Recent Activity", "Last 5 transactions", activity);

        // Two-column layout
        var layout = new Div(leftCard, rightCard);
        layout.getStyle()
            .set("display", "grid")
            .set("grid-template-columns", "1fr 320px")
            .set("gap", "1.25rem")
            .set("align-items", "start");
        return layout;
    }

    // -------------------------------------------------------------------------
    // Code snippets shown in the "Code" tab of each section
    // -------------------------------------------------------------------------

    private static final String CODE_01 = """
        var list = new KeyValueList();
        list.addItem(KeyValueItem.of("Full name", "Marcus Whitfield"));
        list.addItem(KeyValueItem.of("Account number", "AC-4471-9902")
                .setValueMono(true)
                .setShowCopyButton(true));
        list.addItem(KeyValueItem.of("Current balance", "$18,240.55")
                .setCategory(Category.FINANCIAL)
                .setValueNumeric(true));
        list.addItem(KeyValueItem.pill("Account status", "Active", PillVariant.SUCCESS)
                .setCategory(Category.STATUS));
        list.addItem(KeyValueItem.pill("Compliance flag", "Review pending", PillVariant.WARNING)
                .setCategory(Category.ALERT));
        list.addItem(KeyValueItem.builder()
                .key("Notes")
                .value("Client requested paper statements only \u2014 do not email.")
                .textWrap(true)
                .build());
        list.addItem(KeyValueItem.of("Referred by", "Not provided")
                .setValueMuted(true));""";

    private static final String CODE_02 = """
        var list = new KeyValueList();
        list.addItem(KeyValueItem.of("Account holder", "Marcus Whitfield").setEditable(true));
        list.addItem(KeyValueItem.of("Contact email", "m.whitfield@example.com").setEditable(true));
        list.addItem(KeyValueItem.of("Primary phone", "+1 (415) 555-0182").setEditable(true));
        list.addItem(KeyValueItem.identity("Assigned advisor", "JC", "Jordan Cole").setEditable(true));
        list.addItem(KeyValueItem.identity("Last modified by", "SP", "Sarah Patterson"));
        list.addItem(KeyValueItem.of("Support tier", "Premium").setEditable(true));""";

    private static final String CODE_03 = """
        // Each card uses a compact summary list; four are placed in a 2 x 2 grid.
        var list = new KeyValueList().asSummary();
        list.addItem(KeyValueItem.withDelta("Monthly revenue", "$84,120", "+4.6%", DeltaDirection.UP)
                .setCategory(Category.FINANCIAL));
        list.addItem(KeyValueItem.withDelta("Net profit", "$31,800", "+2.1%", DeltaDirection.UP)
                .setCategory(Category.FINANCIAL));
        list.addItem(KeyValueItem.withDelta("Burn rate", "$12,400", "\u22126.2%", DeltaDirection.DOWN)
                .setCategory(Category.ALERT));""";

    private static final String CODE_04 = """
        var list = new KeyValueList().asDense();
        list.addItem(KeyValueItem.of("Transfer from", "Checking \u00b74471"));
        list.addItem(KeyValueItem.of("Transfer to", "Savings \u00b79902")
                .setCategory(Category.FINANCIAL));
        list.addItem(KeyValueItem.of("Amount", "$5,000.00")
                .setCategory(Category.FINANCIAL)
                .setValueNumeric(true));
        list.addItem(KeyValueItem.of("Transfer type", "Immediate"));
        list.addItem(KeyValueItem.of("Reference", "REF-20260615-001")
                .setValueMono(true));
        list.addItem(KeyValueItem.pill("Fee", "No fee \u2014 included in Premium", PillVariant.SUCCESS));""";

    private static final String CODE_05 = """
        // Left: primary detail card
        var detail = new KeyValueList();
        detail.addItem(KeyValueItem.of("Account type", "Premium Savings"));
        detail.addItem(KeyValueItem.of("Account number", "AC-4471-9902")
                .setValueMono(true)
                .setShowCopyButton(true));
        detail.addItem(KeyValueItem.of("Available balance", "$18,240.55")
                .setCategory(Category.FINANCIAL)
                .setValueNumeric(true));
        detail.addItem(KeyValueItem.pill("Status", "Active", PillVariant.SUCCESS)
                .setCategory(Category.STATUS));

        // Right: activity side panel (asSummary — transparent, no card chrome)
        var activity = new KeyValueList().asSummary();
        activity.addItem(KeyValueItem.builder()
                .key("Jun 15").value("+$2,400.00").superText("DIRECT DEPOSIT")
                .numeric(true).category(Category.STATUS).build());""";
}
