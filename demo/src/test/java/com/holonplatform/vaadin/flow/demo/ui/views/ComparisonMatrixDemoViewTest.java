package com.holonplatform.vaadin.flow.demo.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.TableRow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComparisonMatrixDemoViewTest {

    @Test
    void matchesMockupHeadersRowsAndAwardStates() {
        var matrix = ComparisonMatrixDemoView.createMatrix();
        var table = matrix.getTable();
        var header = table.getHeaderRows().getFirst();
        assertEquals(5, header.getHeaderCells().size());
        assertEquals("Criteria", header.getHeaderCells().getFirst().getText());
        assertTrue(header.getHeaderCells().get(1).getClassNames().contains("win"));
        assertTrue(header.getHeaderCells().get(4).getClassNames().contains("closed"));
        assertEquals(19, table.getBodyRows().size());

        TableRow quote = table.getBodyRows().getFirst();
        assertEquals("Quote ref", quote.getHeaderCells().getFirst().getText());
        assertEquals("PT-QUO-2026-0488", ((Span) quote.getDataCells().getFirst()
                .getChildren().findFirst().orElseThrow()).getText());
        assertTrue(quote.getDataCells().get(3).getClassNames().contains("dash"));

        TableRow price = table.getBodyRows().get(4);
        assertEquals("PT-SEN-T2 unit price", price.getHeaderCells().getFirst().getText());
        assertTrue(price.getClassNames().contains("row-best"));
        assertTrue(price.getDataCells().getFirst().getClassNames().contains("best-cell"));
        TableRow total = table.getBodyRows().get(15);
        assertEquals("TOTAL · all lines", total.getHeaderCells().getFirst().getText());
        assertTrue(total.getClassNames().contains("total-row"));

        TableRow actions = table.getBodyRows().getLast();
        assertEquals("Award", actions.getHeaderCells().getFirst().getText());
        assertTrue(actions.getClassNames().contains("award-row"));
        assertFalse(((Button) actions.getDataCells().get(3).getChildren().findFirst().orElseThrow()).isEnabled());
    }
}
