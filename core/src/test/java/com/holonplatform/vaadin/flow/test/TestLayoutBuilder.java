package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.LayoutBuilder;
import com.holonplatform.vaadin.flow.internal.lumo.*;
import com.holonplatform.vaadin.flow.vaadinplus.Layout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LayoutBuilder} and the vaadinplus {@link Layout} component.
 */
class TestLayoutBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_empty_returnsNonNull() {
        assertNotNull(LayoutBuilder.create());
    }

    @Test
    void create_withComponents_returnsNonNull() {
        assertNotNull(LayoutBuilder.create(new Div("A"), new Span("B")));
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_empty_returnsLayout() {
        Layout layout = LayoutBuilder.create().build();
        assertNotNull(layout);
    }

    @Test
    void build_withInitialComponents() {
        Layout layout = LayoutBuilder.create(new Div("A"), new Div("B")).build();
        assertEquals(2, layout.getComponentCount());
    }

    // =========================================================================
    // Add components
    // =========================================================================

    @Nested
    class AddTests {

        @Test
        void add_component() {
            Layout layout = LayoutBuilder.create()
                    .add(new Div("Child"))
                    .build();
            assertEquals(1, layout.getComponentCount());
        }

        @Test
        void add_text() {
            Layout layout = LayoutBuilder.create()
                    .add("Text content")
                    .build();
            assertNotNull(layout);
        }

        @Test
        void addComponentAsFirst() {
            Div first = new Div("First");
            Div second = new Div("Second");
            Layout layout = LayoutBuilder.create()
                    .add(second)
                    .addComponentAsFirst(first)
                    .build();
            assertSame(first, layout.getComponentAt(0));
        }

        @Test
        void addComponentAtIndex() {
            Div a = new Div("A");
            Div b = new Div("B");
            Div c = new Div("C");
            Layout layout = LayoutBuilder.create()
                    .add(a, c)
                    .addComponentAtIndex(1, b)
                    .build();
            assertSame(b, layout.getComponentAt(1));
        }
    }

    // =========================================================================
    // Display
    // =========================================================================

    @Test
    void display_flex() {
        Layout layout = LayoutBuilder.create()
                .display(Display.FLEX)
                .build();
        assertNotNull(layout);
    }

    @Test
    void flex_shortcut() {
        Layout layout = LayoutBuilder.create()
                .flex()
                .build();
        assertNotNull(layout);
    }

    // =========================================================================
    // FlexDirection
    // =========================================================================

    @Test
    void flexDirection_column() {
        Layout layout = LayoutBuilder.create()
                .flexDirection(FlexDirection.COLUMN)
                .build();
        assertNotNull(layout);
    }

    @Test
    void flexDirection_row() {
        Layout layout = LayoutBuilder.create()
                .flexDirection(FlexDirection.ROW)
                .build();
        assertNotNull(layout);
    }

    // =========================================================================
    // Gap
    // =========================================================================

    @Test
    void gap_sets() {
        Layout layout = LayoutBuilder.create()
                .gap(Gap.MEDIUM)
                .build();
        assertNotNull(layout);
    }

    @Test
    void columnGap_sets() {
        Layout layout = LayoutBuilder.create()
                .columnGap(Gap.LARGE)
                .build();
        assertNotNull(layout);
    }

    @Test
    void rowGap_sets() {
        Layout layout = LayoutBuilder.create()
                .rowGap(Gap.SMALL)
                .build();
        assertNotNull(layout);
    }

    // =========================================================================
    // AlignItems / JustifyContent
    // =========================================================================

    @Test
    void alignItems_center() {
        Layout layout = LayoutBuilder.create()
                .alignItems(AlignItems.CENTER)
                .build();
        assertNotNull(layout);
    }

    @Test
    void justifyContent_between() {
        Layout layout = LayoutBuilder.create()
                .justifyContent(JustifyContent.BETWEEN)
                .build();
        assertNotNull(layout);
    }

    // =========================================================================
    // Grid layout
    // =========================================================================

    @Test
    void columns_sets() {
        Layout layout = LayoutBuilder.create()
                .columns(GridColumns.COLUMNS_3)
                .build();
        assertNotNull(layout);
    }

    // =========================================================================
    // FlexWrap
    // =========================================================================

    @Test
    void flexWrap_wrap() {
        Layout layout = LayoutBuilder.create()
                .flexWrap(FlexWrap.WRAP)
                .build();
        assertNotNull(layout);
    }

    // =========================================================================
    // FlexGrow
    // =========================================================================

    @Test
    void flexGrow_noArgs() {
        Layout layout = LayoutBuilder.create()
                .flexGrow()
                .build();
        assertNotNull(layout);
    }

    // =========================================================================
    // Overflow / Position
    // =========================================================================

    @Test
    void overflow_hidden() {
        Layout layout = LayoutBuilder.create()
                .overflow(Overflow.HIDDEN)
                .build();
        assertNotNull(layout);
    }

    @Test
    void position_relative() {
        Layout layout = LayoutBuilder.create()
                .position(Position.RELATIVE)
                .build();
        assertNotNull(layout);
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        Layout layout = LayoutBuilder.create()
                .display(Display.FLEX)
                .flexDirection(FlexDirection.ROW)
                .flexWrap(FlexWrap.WRAP)
                .gap(Gap.MEDIUM)
                .alignItems(AlignItems.CENTER)
                .justifyContent(JustifyContent.BETWEEN)
                .add(new Div("A"), new Div("B"))
                .styleName("my-layout")
                .build();
        assertNotNull(layout);
        assertEquals(2, layout.getComponentCount());
        assertTrue(layout.getClassNames().contains("my-layout"));
    }
}
