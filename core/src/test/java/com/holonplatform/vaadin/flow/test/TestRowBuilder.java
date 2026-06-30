package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.ColumnBuilder;
import com.holonplatform.vaadin.flow.components.builders.RowBuilder;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RowBuilder}.
 */
class TestRowBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(RowBuilder.create());
    }

    @Test
    void build_default_hasDivWithRowClass() {
        Div div = RowBuilder.create().build();
        assertNotNull(div);
        assertTrue(div.getClassNames().contains("row"));
    }

    @Test
    void add_components() {
        Span span = new Span("content");
        Div div = RowBuilder.create().add(span).build();
        assertEquals(1, div.getComponentCount());
    }

    @Test
    void add_columnBuilders() {
        ColumnBuilder col1 = ColumnBuilder.create().span(6);   // col-span-6
        ColumnBuilder col2 = ColumnBuilder.create().span(6);   // col-span-6
        Div div = RowBuilder.create().add(col1, col2).build();
        assertEquals(2, div.getComponentCount());
        assertTrue(div.getComponentAt(0).getElement().getClassList().contains("col-span-6"));
        assertTrue(div.getComponentAt(1).getElement().getClassList().contains("col-span-6"));
    }

    @Test
    void remove_component() {
        Span span = new Span("content");
        RowBuilder builder = RowBuilder.create();
        builder.add(span);
        builder.remove(span);
        assertEquals(0, builder.build().getComponentCount());
    }

    @Test
    void styleName_addsClassName() {
        assertTrue(RowBuilder.create().styleName("my-row").build().getClassNames().contains("my-row"));
    }

    @Test
    void styleNames_addsMultipleClassNames() {
        Div div = RowBuilder.create().styleNames("row-a", "row-b").build();
        assertTrue(div.getClassNames().contains("row-a"));
        assertTrue(div.getClassNames().contains("row-b"));
    }

    @Test
    void getComponent_returnsDiv() {
        assertNotNull(RowBuilder.create().getComponent());
    }

    @Test
    void getElement_returnsElement() {
        assertNotNull(RowBuilder.create().getElement());
    }

    // --- gridColumns API ---

    @Test
    void gridColumns_base_addsGridColsClass() {
        Div div = RowBuilder.create().gridColumns(3).build();
        assertTrue(div.getClassNames().contains("row"));
        assertTrue(div.getClassNames().contains("grid-cols-3"));
    }

    @Test
    void gridColumns_viewMode_addsResponsiveClass() {
        Div div = RowBuilder.create().gridColumns(ViewMode.DESKTOP, 3).build();
        assertTrue(div.getClassNames().contains("lg:grid-cols-3"));
    }

    @Test
    void gridColumns_responsiveChain() {
        Div div = RowBuilder.create()
                .gridColumns(1)
                .gridColumns(ViewMode.TABLET, 2)
                .gridColumns(ViewMode.DESKTOP, 3)
                .build();
        assertTrue(div.getClassNames().contains("grid-cols-1"));
        assertTrue(div.getClassNames().contains("md:grid-cols-2"));
        assertTrue(div.getClassNames().contains("lg:grid-cols-3"));
    }

    @Test
    void fluent_chain_fullExample() {
        Div div = RowBuilder.create()
                .styleName("my-row")
                .add(new Span("cell1"))
                .add(ColumnBuilder.create().at(ViewMode.TABLET, 6))
                .build();
        assertNotNull(div);
        assertTrue(div.getClassNames().contains("row"));
        assertTrue(div.getClassNames().contains("my-row"));
        assertEquals(2, div.getComponentCount());
    }
}
