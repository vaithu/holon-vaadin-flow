package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.SplitLayoutBuilder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SplitLayoutBuilder}.
 */
class TestSplitLayoutBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_returnsNonNull() {
        assertNotNull(SplitLayoutBuilder.create());
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_default_returnsSplitLayout() {
        SplitLayout layout = SplitLayoutBuilder.create().build();
        assertNotNull(layout);
    }

    // =========================================================================
    // Primary / Secondary
    // =========================================================================

    @Nested
    class ComponentTests {

        @Test
        void primaryComponent_sets() {
            Div primary = new Div("Left");
            SplitLayout layout = SplitLayoutBuilder.create()
                    .primaryComponent(primary)
                    .build();
            assertNotNull(layout);
        }

        @Test
        void secondaryComponent_sets() {
            Div secondary = new Div("Right");
            SplitLayout layout = SplitLayoutBuilder.create()
                    .secondaryComponent(secondary)
                    .build();
            assertNotNull(layout);
        }

        @Test
        void both_components() {
            Div left = new Div("Left");
            Div right = new Div("Right");
            SplitLayout layout = SplitLayoutBuilder.create()
                    .primaryComponent(left)
                    .secondaryComponent(right)
                    .build();
            assertNotNull(layout);
        }
    }

    // =========================================================================
    // Orientation
    // =========================================================================

    @Test
    void orientation_horizontal() {
        SplitLayout layout = SplitLayoutBuilder.create()
                .orientation(SplitLayout.Orientation.HORIZONTAL)
                .build();
        assertEquals(SplitLayout.Orientation.HORIZONTAL, layout.getOrientation());
    }

    @Test
    void orientation_vertical() {
        SplitLayout layout = SplitLayoutBuilder.create()
                .orientation(SplitLayout.Orientation.VERTICAL)
                .build();
        assertEquals(SplitLayout.Orientation.VERTICAL, layout.getOrientation());
    }

    // =========================================================================
    // Splitter Position
    // =========================================================================

    @Test
    void splitterPosition_sets() {
        SplitLayout layout = SplitLayoutBuilder.create()
                .splitterPosition(30)
                .build();
        assertNotNull(layout);
    }

    // =========================================================================
    // Size
    // =========================================================================

    @Test
    void width_setsWidth() {
        SplitLayout layout = SplitLayoutBuilder.create()
                .width("100%")
                .build();
        assertEquals("100%", layout.getWidth());
    }

    @Test
    void height_setsHeight() {
        SplitLayout layout = SplitLayoutBuilder.create()
                .height("500px")
                .build();
        assertEquals("500px", layout.getHeight());
    }

    // =========================================================================
    // Component configurator
    // =========================================================================

    @Test
    void id_setsId() {
        SplitLayout layout = SplitLayoutBuilder.create()
                .id("split-1")
                .build();
        assertEquals("split-1", layout.getId().orElse(null));
    }

    @Test
    void styleName_addsClass() {
        SplitLayout layout = SplitLayoutBuilder.create()
                .styleName("main-split")
                .build();
        assertTrue(layout.getClassNames().contains("main-split"));
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        SplitLayout layout = SplitLayoutBuilder.create()
                .primaryComponent(new Div("Master"))
                .secondaryComponent(new Div("Detail"))
                .orientation(SplitLayout.Orientation.HORIZONTAL)
                .splitterPosition(40)
                .width("100%")
                .height("600px")
                .id("master-detail")
                .styleName("md-split")
                .build();
        assertNotNull(layout);
        assertEquals(SplitLayout.Orientation.HORIZONTAL, layout.getOrientation());
        assertEquals("master-detail", layout.getId().orElse(null));
    }
}
