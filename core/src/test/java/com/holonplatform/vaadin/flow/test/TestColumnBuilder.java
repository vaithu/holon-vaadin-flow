package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.ColumnBuilder;
import com.iyensoft.vaadin.flow.enums.ColSpan;
import com.iyensoft.vaadin.flow.enums.ViewMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ColumnBuilder}.
 */
class TestColumnBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(ColumnBuilder.create());
    }

    @Test
    void build_default_returnsDiv() {
        assertNotNull(ColumnBuilder.create().build());
    }

    @Test
    void column_addsArbitraryClassNames() {
        Div div = ColumnBuilder.create().column("my-class", "another").build();
        assertTrue(div.getClassNames().contains("my-class"));
        assertTrue(div.getClassNames().contains("another"));
    }

    @Test
    void add_components() {
        Div div = ColumnBuilder.create().add(new Span("test")).build();
        assertEquals(1, div.getComponentCount());
    }

    // --- span(ColSpan) — enum overload ---

    @Test
    void span_COL_12_addsColSpan12() {
        assertTrue(ColumnBuilder.create().span(ColSpan.COL_12).build().getClassNames().contains("col-span-12"));
    }

    @Test
    void span_COL_6_addsColSpan6() {
        assertTrue(ColumnBuilder.create().span(ColSpan.COL_6).build().getClassNames().contains("col-span-6"));
    }

    @Test
    void span_COL_4_addsColSpan4() {
        assertTrue(ColumnBuilder.create().span(ColSpan.COL_4).build().getClassNames().contains("col-span-4"));
    }

    @Test
    void span_COL_3_addsColSpan3() {
        assertTrue(ColumnBuilder.create().span(ColSpan.COL_3).build().getClassNames().contains("col-span-3"));
    }

    @Test
    void span_COL_8_addsColSpan8() {
        assertTrue(ColumnBuilder.create().span(ColSpan.COL_8).build().getClassNames().contains("col-span-8"));
    }

    @Test
    void span_COL_9_addsColSpan9() {
        assertTrue(ColumnBuilder.create().span(ColSpan.COL_9).build().getClassNames().contains("col-span-9"));
    }

    @Test
    void span_COL_2_addsColSpan2() {
        assertTrue(ColumnBuilder.create().span(ColSpan.COL_2).build().getClassNames().contains("col-span-2"));
    }

    @Test
    void span_COL_1_addsColSpan1() {
        assertTrue(ColumnBuilder.create().span(ColSpan.COL_1).build().getClassNames().contains("col-span-1"));
    }

    @Test
    void span_COL_8_plus_COL_4_equals_12() {
        // COL_8 + COL_4 = 12 — classic two-column layout
        assertEquals(12, ColSpan.COL_8.getGridSpan() + ColSpan.COL_4.getGridSpan());
    }

    @Test
    void span_colSpan_combinedWithAt() {
        Div div = ColumnBuilder.create()
                .span(ColSpan.COL_12)            // col-span-12 on mobile
                .at(ViewMode.TABLET,  2)         // md:col-span-6
                .at(ViewMode.DESKTOP, 4)         // lg:col-span-3
                .build();
        assertTrue(div.getClassNames().contains("col-span-12"));
        assertTrue(div.getClassNames().contains("md:col-span-6"));
        assertTrue(div.getClassNames().contains("lg:col-span-3"));
    }

    // --- span(int) — raw span (1–12) ---

    @Test
    void span_addsBaseColSpanClass() {
        assertTrue(ColumnBuilder.create().span(6).build().getClassNames().contains("col-span-6"));
    }

    @Test
    void span_fullWidth() {
        assertTrue(ColumnBuilder.create().span(12).build().getClassNames().contains("col-span-12"));
    }

    // --- at(ViewMode, int) — "items per row" semantics ---

    @Test
    void at_mobile_1perRow_fullWidth() {
        // 1 per row → 12/1 = span 12
        assertTrue(ColumnBuilder.create().at(ViewMode.MOBILE, 1).build().getClassNames().contains("sm:col-span-12"));
    }

    @Test
    void at_tablet_2perRow_halves() {
        // 2 per row → 12/2 = span 6
        assertTrue(ColumnBuilder.create().at(ViewMode.TABLET, 2).build().getClassNames().contains("md:col-span-6"));
    }

    @Test
    void at_desktop_3perRow_thirds() {
        // 3 per row → 12/3 = span 4
        assertTrue(ColumnBuilder.create().at(ViewMode.DESKTOP, 3).build().getClassNames().contains("lg:col-span-4"));
    }

    @Test
    void at_desktop_4perRow_quarters() {
        // 4 per row → 12/4 = span 3
        assertTrue(ColumnBuilder.create().at(ViewMode.DESKTOP, 4).build().getClassNames().contains("lg:col-span-3"));
    }

    @Test
    void at_largeDesktop_6perRow() {
        // 6 per row → 12/6 = span 2
        assertTrue(ColumnBuilder.create().at(ViewMode.LARGE_DESKTOP, 6).build().getClassNames().contains("xl:col-span-2"));
    }

    @Test
    void at_ultraWide_12perRow() {
        // 12 per row → 12/12 = span 1
        assertTrue(ColumnBuilder.create().at(ViewMode.ULTRA_WIDE, 12).build().getClassNames().contains("2xl:col-span-1"));
    }

    @Test
    void at_fullResponsiveChain_itemsPerRow() {
        // 1 → 2 → 4 items per row as viewport grows
        Div div = ColumnBuilder.create()
                .at(ViewMode.MOBILE,  1)   // sm:col-span-12
                .at(ViewMode.TABLET,  2)   // md:col-span-6
                .at(ViewMode.DESKTOP, 4)   // lg:col-span-3
                .build();
        assertTrue(div.getClassNames().contains("sm:col-span-12"));
        assertTrue(div.getClassNames().contains("md:col-span-6"));
        assertTrue(div.getClassNames().contains("lg:col-span-3"));
    }

    @Test
    void span_and_at_combine() {
        // span() = raw grid span; at() = items-per-row (auto-calculates span)
        Div div = ColumnBuilder.create()
                .span(12)                         // col-span-12 (base)
                .at(ViewMode.TABLET,  2)          // md:col-span-6  (2 per row)
                .at(ViewMode.DESKTOP, 4)          // lg:col-span-3  (4 per row)
                .build();
        assertTrue(div.getClassNames().contains("col-span-12"));
        assertTrue(div.getClassNames().contains("md:col-span-6"));
        assertTrue(div.getClassNames().contains("lg:col-span-3"));
    }

    @Test
    void fluent_fullChain() {
        Div div = ColumnBuilder.create()
                .at(ViewMode.MOBILE,  1)   // sm:col-span-12
                .at(ViewMode.TABLET,  2)   // md:col-span-6
                .at(ViewMode.DESKTOP, 4)   // lg:col-span-3
                .styleName("my-col")
                .add(new Span("content"))
                .build();
        assertNotNull(div);
        assertTrue(div.getClassNames().contains("sm:col-span-12"));
        assertTrue(div.getClassNames().contains("md:col-span-6"));
        assertTrue(div.getClassNames().contains("lg:col-span-3"));
        assertTrue(div.getClassNames().contains("my-col"));
        assertEquals(1, div.getComponentCount());
    }

    @Test
    void styleName_addsClassName() {
        assertTrue(ColumnBuilder.create().styleName("custom").build().getClassNames().contains("custom"));
    }

    @Test
    void styleNames_addsMultipleClassNames() {
        Div div = ColumnBuilder.create().styleNames("a", "b").build();
        assertTrue(div.getClassNames().contains("a"));
        assertTrue(div.getClassNames().contains("b"));
    }

    @Test
    void getComponent_returnsDiv() {
        assertNotNull(ColumnBuilder.create().getComponent());
    }

    @Test
    void getElement_returnsElement() {
        assertNotNull(ColumnBuilder.create().getElement());
    }
}
