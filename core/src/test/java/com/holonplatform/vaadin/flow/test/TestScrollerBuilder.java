package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.ScrollerBuilder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.Scroller.ScrollDirection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ScrollerBuilder}.
 */
class TestScrollerBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_returnsNonNull() {
        assertNotNull(ScrollerBuilder.create());
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_default_returnsScroller() {
        Scroller scroller = ScrollerBuilder.create().build();
        assertNotNull(scroller);
    }

    // =========================================================================
    // ScrollDirection
    // =========================================================================

    @Test
    void scrollDirection_both() {
        Scroller scroller = ScrollerBuilder.create()
                .scrollDirection(ScrollDirection.BOTH)
                .build();
        assertEquals(ScrollDirection.BOTH, scroller.getScrollDirection());
    }

    @Test
    void scrollDirection_vertical() {
        Scroller scroller = ScrollerBuilder.create()
                .scrollDirection(ScrollDirection.VERTICAL)
                .build();
        assertEquals(ScrollDirection.VERTICAL, scroller.getScrollDirection());
    }

    @Test
    void scrollDirection_horizontal() {
        Scroller scroller = ScrollerBuilder.create()
                .scrollDirection(ScrollDirection.HORIZONTAL)
                .build();
        assertEquals(ScrollDirection.HORIZONTAL, scroller.getScrollDirection());
    }

    @Test
    void scrollDirection_none() {
        Scroller scroller = ScrollerBuilder.create()
                .scrollDirection(ScrollDirection.NONE)
                .build();
        assertEquals(ScrollDirection.NONE, scroller.getScrollDirection());
    }

    // =========================================================================
    // Content
    // =========================================================================

    @Test
    void content_setsChild() {
        Div child = new Div("content");
        Scroller scroller = ScrollerBuilder.create()
                .content(child)
                .build();
        assertSame(child, scroller.getContent());
    }

    @Test
    void content_replacesExisting() {
        Div first = new Div("first");
        Div second = new Div("second");
        Scroller scroller = ScrollerBuilder.create()
                .content(first)
                .content(second)
                .build();
        assertSame(second, scroller.getContent());
    }

    // =========================================================================
    // Size
    // =========================================================================

    @Test
    void width_setsWidth() {
        Scroller scroller = ScrollerBuilder.create()
                .width("400px")
                .build();
        assertEquals("400px", scroller.getWidth());
    }

    @Test
    void height_setsHeight() {
        Scroller scroller = ScrollerBuilder.create()
                .height("300px")
                .build();
        assertEquals("300px", scroller.getHeight());
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        Div child = new Div("content");
        Scroller scroller = ScrollerBuilder.create()
                .scrollDirection(ScrollDirection.VERTICAL)
                .content(child)
                .width("100%")
                .height("500px")
                .build();
        assertNotNull(scroller);
        assertEquals(ScrollDirection.VERTICAL, scroller.getScrollDirection());
        assertSame(child, scroller.getContent());
        assertEquals("100%", scroller.getWidth());
        assertEquals("500px", scroller.getHeight());
    }
}
