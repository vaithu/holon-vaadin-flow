package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.FlexBoxLayout;
import com.holonplatform.vaadin.flow.components.builders.FlexBoxLayoutBuilder;
import com.holonplatform.vaadin.flow.components.builders.FlexBoxLayoutConfigurator;
import com.holonplatform.vaadin.flow.components.css.BorderRadius;
import com.holonplatform.vaadin.flow.components.css.BoxSizing;
import com.holonplatform.vaadin.flow.components.css.Size;
import com.holonplatform.vaadin.flow.internal.lumo.Display;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FlexBoxLayoutBuilder} and the {@link FlexBoxLayout} component.
 */
class TestFlexBoxLayoutBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_returnsNonNull() {
        assertNotNull(FlexBoxLayoutBuilder.create());
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_empty_returnsFlexBoxLayout() {
        FlexBoxLayout layout = FlexBoxLayoutBuilder.create().build();
        assertNotNull(layout);
        assertTrue(layout.getClassNames().contains("flex-box-layout"));
    }

    // =========================================================================
    // Add components
    // =========================================================================

    @Nested
    class AddTests {

        @Test
        void add_component_addsChild() {
            FlexBoxLayout layout = FlexBoxLayoutBuilder.create()
                    .add(new Div("Child"))
                    .build();
            assertEquals(1, layout.getComponentCount());
        }

        @Test
        void add_multiple_components() {
            FlexBoxLayout layout = FlexBoxLayoutBuilder.create()
                    .add(new Div("A"), new Div("B"), new Div("C"))
                    .build();
            assertEquals(3, layout.getComponentCount());
        }

        @Test
        void add_text_addsText() {
            FlexBoxLayout layout = FlexBoxLayoutBuilder.create()
                    .add("Some text")
                    .build();
            assertNotNull(layout);
        }

        @Test
        void addComponentAsFirst_insertsAtIndex0() {
            Div first = new Div("First");
            Div second = new Div("Second");
            FlexBoxLayout layout = FlexBoxLayoutBuilder.create()
                    .add(second)
                    .addComponentAsFirst(first)
                    .build();
            assertEquals(2, layout.getComponentCount());
            assertSame(first, layout.getComponentAt(0));
        }

        @Test
        void addComponentAtIndex_insertsAtCorrectPosition() {
            Div a = new Div("A");
            Div b = new Div("B");
            Div c = new Div("C");
            FlexBoxLayout layout = FlexBoxLayoutBuilder.create()
                    .add(a, c)
                    .addComponentAtIndex(1, b)
                    .build();
            assertEquals(3, layout.getComponentCount());
            assertSame(b, layout.getComponentAt(1));
        }
    }

    // =========================================================================
    // Styling
    // =========================================================================

    @Nested
    class StylingTests {

        @Test
        void borderRadius_setsStyle() {
            FlexBoxLayout layout = FlexBoxLayoutBuilder.create()
                    .borderRadius(BorderRadius.M)
                    .build();
            assertEquals(BorderRadius.M.getValue(), layout.getStyle().get("border-radius"));
        }

        @Test
        void boxSizing_setsStyle() {
            FlexBoxLayout layout = FlexBoxLayoutBuilder.create()
                    .boxSizing(BoxSizing.BORDER_BOX)
                    .build();
            assertEquals(BoxSizing.BORDER_BOX.getValue(), layout.getStyle().get("box-sizing"));
        }

        @Test
        void display_setsStyle() {
            FlexBoxLayout layout = FlexBoxLayoutBuilder.create()
                    .display(Display.FLEX)
                    .build();
            assertEquals(Display.FLEX.getClassName(), layout.getStyle().get("display"));
        }
    }

    // =========================================================================
    // Flex properties
    // =========================================================================

    @Nested
    class FlexTests {

        @Test
        void flex_setsOnChild() {
            Div child = new Div("Child");
            FlexBoxLayout layout = FlexBoxLayoutBuilder.create()
                    .add(child)
                    .flex("1 1 auto", child)
                    .build();
            assertEquals("1 1 auto", child.getElement().getStyle().get("flex"));
        }

        @Test
        void flexBasis_setsOnChild() {
            Div child = new Div("Child");
            FlexBoxLayout layout = FlexBoxLayoutBuilder.create()
                    .add(child)
                    .flexBasis("200px", child)
                    .build();
            assertEquals("200px", child.getElement().getStyle().get("flex-basis"));
        }

        @Test
        void flexShrink_setsOnChild() {
            Div child = new Div("Child");
            FlexBoxLayout layout = FlexBoxLayoutBuilder.create()
                    .add(child)
                    .flexShrink("0", child)
                    .build();
            assertEquals("0", child.getElement().getStyle().get("flex-shrink"));
        }
    }

    // =========================================================================
    // Configure static
    // =========================================================================

    @Test
    void configure_static_returnsConfigurator() {
        FlexBoxLayout layout = new FlexBoxLayout();
        FlexBoxLayoutConfigurator.BaseFlexBoxLayoutConfigurator configurator =
                FlexBoxLayoutConfigurator.configure(layout);
        assertNotNull(configurator);
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        Div child1 = new Div("A");
        Div child2 = new Div("B");
        FlexBoxLayout layout = FlexBoxLayoutBuilder.create()
                .add(child1, child2)
                .borderRadius(BorderRadius.L)
                .boxSizing(BoxSizing.BORDER_BOX)
                .display(Display.FLEX)
                .flex("1", child1)
                .flexShrink("0", child2)
                .build();
        assertNotNull(layout);
        assertEquals(2, layout.getComponentCount());
    }
}
