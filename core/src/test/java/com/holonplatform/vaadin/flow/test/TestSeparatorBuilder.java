package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.SeparatorBuilder;
import com.holonplatform.vaadin.flow.components.builders.SeparatorConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Separator;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SeparatorBuilder} and {@link SeparatorConfigurator}.
 */
class TestSeparatorBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_returnsNonNull() {
        assertNotNull(SeparatorBuilder.create());
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_default_returnsNonNull() {
        Separator sep = SeparatorBuilder.create().build();
        assertNotNull(sep);
    }

    @Test
    void build_default_hasBaseClassName() {
        Separator sep = SeparatorBuilder.create().build();
        assertTrue(sep.getClassNames().contains("separator"));
    }

    // =========================================================================
    // Orientation
    // =========================================================================

    @Nested
    class OrientationTests {

        @Test
        void orientation_horizontal() {
            Separator sep = SeparatorBuilder.create()
                    .orientation(Separator.Orientation.HORIZONTAL)
                    .build();
            assertNotNull(sep);
        }

        @Test
        void orientation_vertical() {
            Separator sep = SeparatorBuilder.create()
                    .orientation(Separator.Orientation.VERTICAL)
                    .build();
            assertTrue(sep.getClassNames().contains("separator--vertical"));
        }
    }

    // =========================================================================
    // Decorative
    // =========================================================================

    @Nested
    class DecorativeTests {

        @Test
        void decorative_true() {
            Separator sep = SeparatorBuilder.create()
                    .decorative(true)
                    .build();
            assertEquals("none", sep.getElement().getAttribute("role"));
        }

        @Test
        void decorative_false() {
            Separator sep = SeparatorBuilder.create()
                    .decorative(false)
                    .build();
            assertEquals("separator", sep.getElement().getAttribute("role"));
        }
    }

    // =========================================================================
    // Color
    // =========================================================================

    @Test
    void color_sets() {
        Separator sep = SeparatorBuilder.create()
                .color(Color.Background.PRIMARY)
                .build();
        assertNotNull(sep);
    }

    @Test
    void color_null_clears() {
        Separator sep = SeparatorBuilder.create()
                .color(Color.Background.PRIMARY)
                .color(null)
                .build();
        assertNotNull(sep);
    }

    // =========================================================================
    // Thickness
    // =========================================================================

    @Nested
    class ThicknessTests {

        @Test
        void thickness_thin() {
            Separator sep = SeparatorBuilder.create()
                    .thickness(Separator.Thickness.THIN)
                    .build();
            assertNotNull(sep);
        }

        @Test
        void thickness_medium() {
            Separator sep = SeparatorBuilder.create()
                    .thickness(Separator.Thickness.MEDIUM)
                    .build();
            assertNotNull(sep);
        }

        @Test
        void thickness_thick() {
            Separator sep = SeparatorBuilder.create()
                    .thickness(Separator.Thickness.THICK)
                    .build();
            assertNotNull(sep);
        }

        @Test
        void thickness_null_clears() {
            Separator sep = SeparatorBuilder.create()
                    .thickness(Separator.Thickness.THICK)
                    .thickness(null)
                    .build();
            assertNotNull(sep);
        }
    }

    // =========================================================================
    // Component Configurator basics
    // =========================================================================

    @Test
    void id_setsId() {
        Separator sep = SeparatorBuilder.create()
                .id("my-separator")
                .build();
        assertTrue(sep.getId().isPresent());
        assertEquals("my-separator", sep.getId().get());
    }

    @Test
    void styleName_addsClass() {
        Separator sep = SeparatorBuilder.create()
                .styleName("custom-sep")
                .build();
        assertTrue(sep.getClassNames().contains("custom-sep"));
    }

    // =========================================================================
    // Configure (existing instance)
    // =========================================================================

    @Test
    void configure_existingInstance() {
        Separator sep = new Separator();
        SeparatorConfigurator.configure(sep)
                .orientation(Separator.Orientation.VERTICAL)
                .decorative(true);
        assertEquals("none", sep.getElement().getAttribute("role"));
        assertTrue(sep.getClassNames().contains("separator--vertical"));
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        Separator sep = SeparatorBuilder.create()
                .orientation(Separator.Orientation.HORIZONTAL)
                .decorative(false)
                .thickness(Separator.Thickness.MEDIUM)
                .color(Color.Background.PRIMARY)
                .id("divider-1")
                .styleName("my-divider")
                .build();
        assertNotNull(sep);
        assertTrue(sep.getClassNames().contains("separator"));
        assertTrue(sep.getClassNames().contains("my-divider"));
        assertEquals("divider-1", sep.getId().orElse(null));
    }
}
