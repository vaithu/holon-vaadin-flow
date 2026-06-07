package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.FlexLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.FlexLayoutConfigurator;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FlexLayoutBuilder} and the {@link FlexLayoutConfigurator} infrastructure.
 */
class TestFlexLayoutBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_returnsNonNull() {
        assertNotNull(FlexLayoutBuilder.create());
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_empty_returnsFlexLayout() {
        FlexLayout layout = FlexLayoutBuilder.create().build();
        assertNotNull(layout);
    }

    // =========================================================================
    // Add components
    // =========================================================================

    @Nested
    class AddTests {

        @Test
        void add_component_addsChild() {
            FlexLayout layout = FlexLayoutBuilder.create()
                    .add(new Div("Child"))
                    .build();
            assertEquals(1, layout.getComponentCount());
        }

        @Test
        void add_multiple_components() {
            FlexLayout layout = FlexLayoutBuilder.create()
                    .add(new Div("A"), new Div("B"))
                    .build();
            assertEquals(2, layout.getComponentCount());
        }

        @Test
        void add_text() {
            FlexLayout layout = FlexLayoutBuilder.create()
                    .add("Some text")
                    .build();
            assertNotNull(layout);
        }

        @Test
        void addComponentAsFirst_insertsAtIndex0() {
            Div first = new Div("First");
            Div second = new Div("Second");
            FlexLayout layout = FlexLayoutBuilder.create()
                    .add(second)
                    .addComponentAsFirst(first)
                    .build();
            assertSame(first, layout.getComponentAt(0));
        }

        @Test
        void addComponentAtIndex_insertsCorrectly() {
            Div a = new Div("A");
            Div b = new Div("B");
            Div c = new Div("C");
            FlexLayout layout = FlexLayoutBuilder.create()
                    .add(a, c)
                    .addComponentAtIndex(1, b)
                    .build();
            assertSame(b, layout.getComponentAt(1));
        }
    }

    // =========================================================================
    // Flex properties
    // =========================================================================

    @Nested
    class FlexTests {

        @Test
        void alignContent_setsAlignment() {
            FlexLayout layout = FlexLayoutBuilder.create()
                    .alignContent(FlexLayout.ContentAlignment.CENTER)
                    .build();
            assertNotNull(layout);
        }

        @Test
        void flexDirection_setsDirection() {
            FlexLayout layout = FlexLayoutBuilder.create()
                    .flexDirection(FlexLayout.FlexDirection.COLUMN)
                    .build();
            assertEquals(FlexLayout.FlexDirection.COLUMN, layout.getFlexDirection());
        }

        @Test
        void flexWrap_setsWrap() {
            FlexLayout layout = FlexLayoutBuilder.create()
                    .flexWrap(FlexLayout.FlexWrap.WRAP)
                    .build();
            assertEquals(FlexLayout.FlexWrap.WRAP, layout.getFlexWrap());
        }

        @Test
        void flexBasis_setsOnComponents() {
            Div child = new Div("Child");
            FlexLayout layout = FlexLayoutBuilder.create()
                    .add(child)
                    .flexBasis("200px", child)
                    .build();
            assertNotNull(layout);
        }

        @Test
        void order_setsOnComponent() {
            Div child = new Div("Child");
            FlexLayout layout = FlexLayoutBuilder.create()
                    .add(child)
                    .order(2, child)
                    .build();
            assertNotNull(layout);
        }
    }

    // =========================================================================
    // Title
    // =========================================================================

    @Test
    void title_string_addsH4() {
        FlexLayout layout = FlexLayoutBuilder.create()
                .title("Section Title")
                .build();
        assertTrue(layout.getComponentCount() > 0);
    }

    @Test
    void add_titleAndComponents() {
        FlexLayout layout = FlexLayoutBuilder.create()
                .add("Section", new Div("Content"))
                .build();
        assertTrue(layout.getComponentCount() > 0);
    }

    // =========================================================================
    // Card convenience
    // =========================================================================

    @Test
    void card_addsClassName() {
        FlexLayout layout = FlexLayoutBuilder.create()
                .card()
                .build();
        assertTrue(layout.getClassNames().contains("card"));
    }

    // =========================================================================
    // Configure static
    // =========================================================================

    @Test
    void configure_static_returnsConfigurator() {
        FlexLayout layout = new FlexLayout();
        FlexLayoutConfigurator.BaseFlexLayoutConfigurator cfg =
                FlexLayoutConfigurator.configure(layout);
        assertNotNull(cfg);
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        Div child = new Div("Child");
        FlexLayout layout = FlexLayoutBuilder.create()
                .add(child)
                .flexDirection(FlexLayout.FlexDirection.ROW)
                .flexWrap(FlexLayout.FlexWrap.WRAP)
                .alignContent(FlexLayout.ContentAlignment.SPACE_BETWEEN)
                .card()
                .build();
        assertNotNull(layout);
        assertTrue(layout.getClassNames().contains("card"));
    }
}
