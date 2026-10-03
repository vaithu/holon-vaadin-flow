package com.holonplatform.vaadin.flow.demo.ui.views;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.demo.ui.DemoMainLayout;
import com.iyensoft.vaadin.flow.components.ComparisonMatrix;
import com.iyensoft.vaadin.flow.components.Components;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.TableRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Procurement RFQ mockup rendered with the domain-agnostic ComparisonMatrix.
 */
@PageTitle("Comparison Matrix - Holon Demo")
@Route(value = "comparison-matrix", layout = DemoMainLayout.class)
@StyleSheet("context://comparison-matrix-demo.css")
public class ComparisonMatrixDemoView extends Div {

    public ComparisonMatrixDemoView() {
        addClassNames("app-view", "comparison-matrix-demo");

        Div card = new Div();
        card.addClassName("comparison-demo-card");
        Div heading = new Div();
        heading.addClassName("comparison-demo-heading");
        heading.add(new Span("Vendor comparison matrix"),
                new Span("Customize scoring weights"));

        card.add(heading, createMatrix());
        add(new H1("ComparisonMatrix"), card);
    }

    static ComparisonMatrix createMatrix() {
        ComparisonMatrix matrix = Components.comparisonMatrix()
                .heading("Criteria")
                .column(vendor("PT", "PrahaTech s.r.o.", "CZ · preferred", "★ 4.6/5 · 8 wins", "teal"))
                .column(vendor("HR", "Helix Robotics", "DE · approved", "★ 4.2/5 · 5 wins", "violet"))
                .column(vendor("BG", "BioGenetics Lab", "DE · new vendor", "★ 3.8/5 · 1 win", "pink"))
                .column(vendor("?", "Cumbre Mining", "CL · not quoted", "★ 3.4/5 · 2 wins", "muted"))
                .build();
        matrix.addClassName("rfq-matrix");
        var headers = matrix.getTable().getHeaderRows().getFirst().getHeaderCells();
        headers.get(1).addClassName("win");
        headers.get(4).addClassName("closed");

        row(matrix, "Quote ref", "PT-QUO-2026-0488", "HR-QUO-2026-1212", "BG-QUO-2026-0092", "—");
        row(matrix, "Submitted", "Jun 18 09:42", "Jun 19 16:08", "Jun 20 11:30", "overdue 2d")
                .addClassName("submitted");
        mark(row(matrix, "Response time", "6d", "7d", "8d", "— (didn't respond)"), 1, "b star", 4, "w");
        row(matrix, "Valid until", "Jul 18", "Jul 12", "Jun 30", "—");

        TableRow price = row(matrix, "PT-SEN-T2 unit price", "€84.20", "€80.50", "€86.00", "not quoted");
        price.addClassName("row-best");
        mark(price, 1, "b best-cell", 2, "b", 4, "w");
        mark(row(matrix, "Volume discount", "−5%", "—", "−3%", "—"), 1, "positive", 3, "positive");
        mark(row(matrix, "Lead time", "10–14 days", "12–16 days", "21–28 days", "—"),
                1, "b star", 3, "w");
        mark(row(matrix, "Min order qty", "50 units", "100 units", "25 units", "—"),
                1, "b", 3, "star");
        mark(row(matrix, "Payment terms", "Net 14 (2/10)", "Net 30", "Net 45", "—"),
                1, "b star");
        mark(row(matrix, "Incoterm", "DDP", "DAP", "EXW", "—"), 1, "b star");
        mark(row(matrix, "Quality cert.", "ISO 9001 · CE", "ISO 9001 · CE · ATEX", "ISO 13485", "—"),
                2, "b star");
        mark(row(matrix, "Historical defect rate", "0.4%", "0.8%", "1.6%", "—"),
                1, "b star", 3, "w");
        mark(row(matrix, "CO₂ footprint", "12 kg/u", "18 kg/u", "28 kg/u", "—"),
                1, "b star", 3, "w");
        mark(row(matrix, "Customs / origin", "EU (CZ)", "EU (DE)", "EU (DE)", "CL (LATAM)"),
                2, "b star");
        mark(row(matrix, "References provided", "3 (Helix, Lumen, BioGen)", "2", "1", "0"),
                1, "b star", 4, "w");

        TableRow total = row(matrix, "TOTAL · all lines", "€42,612.00", "€40,168.00", "€43,820.00", "—");
        total.addClassNames("row-best", "total-row");
        mark(total, 1, "best-cell", 2, "b", 4, "w");
        mark(row(matrix, "vs. budget (€42k)", "+€612 (+1.5%)", "−€1,832 (−4.4%)",
                "+€1,820 (+4.3%)", "—"), 1, "w", 2, "b", 3, "w");
        mark(row(matrix, "Cash discount (2/10)", "−€852", "—", "—", "—"), 1, "b");

        Button awardPo = Components.button().text(Localizable.of("Award → PO", "rfq.demo.awardPo"))
                .icon(VaadinIcon.CHECK).onClick(e -> demoAction()).build();
        awardPo.addClassName("award-po");
        Button award = Components.button().text(Localizable.of("Award", "rfq.demo.award"))
                .onClick(e -> demoAction()).build();
        award.addClassName("award-alt");
        Button reject = Components.button().text(Localizable.of("Reject", "rfq.demo.reject"))
                .onClick(e -> demoAction()).build();
        reject.addClassName("reject");
        Button closed = Components.button().text(Localizable.of("Closed", "rfq.demo.closed")).build();
        closed.setEnabled(false);
        closed.addClassName("reject");
        matrix.addRow("Award", awardPo, award, reject, closed).addClassName("award-row");
        return matrix;
    }

    private static Div vendor(String initials, String name, String meta, String score, String color) {
        Div header = new Div();
        Div logo = new Div();
        logo.addClassNames("v-logo", "logo-" + color);
        logo.setText(initials);
        Div title = new Div();
        title.addClassName("v-name");
        title.setText(name);
        Div subtitle = new Div();
        subtitle.addClassName("v-meta");
        subtitle.setText(meta);
        Div rating = new Div();
        rating.addClassName("v-score");
        rating.setText(score);
        header.add(logo, title, subtitle, rating);
        return header;
    }

    private static TableRow row(ComparisonMatrix matrix, String label, String... values) {
        TableRow row = matrix.addRow(label, values);
        for (int i = 0; i < values.length; i++) {
            if ("—".equals(values[i])) {
                row.getDataCells().get(i).addClassName("dash");
            }
        }
        return row;
    }

    private static void mark(TableRow row, int firstColumn, String firstClasses) {
        styleCell(row, firstColumn, firstClasses);
    }

    private static void mark(TableRow row, int firstColumn, String firstClasses,
                             int secondColumn, String secondClasses) {
        styleCell(row, firstColumn, firstClasses);
        styleCell(row, secondColumn, secondClasses);
    }

    private static void mark(TableRow row, int firstColumn, String firstClasses,
                             int secondColumn, String secondClasses, int thirdColumn, String thirdClasses) {
        styleCell(row, firstColumn, firstClasses);
        styleCell(row, secondColumn, secondClasses);
        styleCell(row, thirdColumn, thirdClasses);
    }

    private static void styleCell(TableRow row, int column, String classes) {
        var cell = row.getDataCells().get(column - 1);
        for (String className : classes.split(" ")) {
            cell.addClassName(className);
        }
        if (cell.getClassNames().contains("star")) {
            Span star = new Span("★");
            star.addClassName("star-icon");
            star.getElement().setAttribute("aria-label", "Best in category");
            cell.add(star);
        }
    }

    private static void demoAction() {
        Notification.show("Demo only — no award decision was saved.");
    }
}
