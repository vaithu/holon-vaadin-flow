package com.iyensoft.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.ComparisonMatrix;
import com.iyensoft.vaadin.flow.components.Components;
import com.iyensoft.vaadin.flow.components.builders.ComparisonMatrixConfigurator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.TableRow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestComparisonMatrixBuilder {

    @Test
    void rendersSemanticTableWithArbitraryHeadersValuesAndActions() {
        Div customHeader = new Div(new Span("PT"), new Span("PrahaTech"));
        Button award = new Button("Award");
        Button closed = new Button("Closed");
        closed.setEnabled(false);

        ComparisonMatrix matrix = Components.comparisonMatrix()
                .heading("Criteria")
                .column(customHeader)
                .column("Cumbre Mining")
                .row("Quote ref", "PT-QUO-2026-0488", "—")
                .row("Award", award, closed)
                .build();

        var table = matrix.getTable();
        assertEquals("table", table.getElement().getTag());
        assertEquals("Criteria", table.getHeaderRows().getFirst().getHeaderCells().getFirst().getText());
        assertEquals("col", table.getHeaderRows().getFirst().getHeaderCells().get(1).getElement().getAttribute("scope"));
        assertSame(customHeader, table.getHeaderRows().getFirst().getHeaderCells().get(1).getChildren().findFirst().orElseThrow());
        assertEquals(2, table.getBodyRows().size());
        assertEquals("row", table.getBodyRows().getFirst().getHeaderCells().getFirst().getElement().getAttribute("scope"));
        assertEquals("PT-QUO-2026-0488", table.getBodyRows().getFirst().getDataCells().getFirst()
                .getChildren().findFirst().map(component -> ((Span) component).getText()).orElseThrow());
        assertSame(award, table.getBodyRows().get(1).getDataCells().getFirst().getChildren().findFirst().orElseThrow());
        assertFalse(closed.isEnabled());
    }

    @Test
    void acceptsCustomRowsWithCellSpecificStyling() {
        ComparisonMatrix matrix = ComparisonMatrix.builder().column("First").column("Second").build();
        TableRow highlighted = new TableRow();
        highlighted.addRowHeaderCell("Total").addClassName("label");
        highlighted.addDataCell("100").addClassName("best-cell");
        highlighted.addDataCell("120");
        highlighted.addClassName("row-best");
        matrix.addRow(highlighted);

        assertSame(highlighted, matrix.getTable().getBodyRows().getFirst());
        assertTrue(matrix.getTable().getBodyRows().getFirst().getClassNames().contains("row-best"));
        assertTrue(matrix.getTable().getBodyRows().getFirst().getDataCells().getFirst()
                .getClassNames().contains("best-cell"));
    }

    @Test
    void rejectsWrongDimensionsAndLateColumns() {
        ComparisonMatrix matrix = Components.comparisonMatrix().column("First").build();
        assertThrows(IllegalArgumentException.class, () -> matrix.addRow("Total", "1", "2"));
        assertThrows(IllegalArgumentException.class, () -> matrix.addRow(new TableRow()));
        matrix.addRow("Total", "1");
        assertThrows(IllegalStateException.class, () -> matrix.addColumn("Second"));
    }

    @Test
    void configuresExistingMatrix() {
        ComparisonMatrix matrix = new ComparisonMatrix();
        ComparisonMatrixConfigurator.configure(matrix).styleName("custom").column("Column");
        assertSame(matrix, Components.comparisonMatrix(matrix).build());
        assertTrue(matrix.getClassNames().contains("custom"));
    }
}
