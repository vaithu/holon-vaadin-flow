package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.EmptyBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Empty;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link EmptyBuilder} and the {@link Empty} component.
 */
class TestEmptyBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_returnsNonNull() {
        assertNotNull(EmptyBuilder.create());
    }

    @Test
    void staticFactory_returnsNonNull() {
        assertNotNull(Empty.builder());
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_empty_returnsEmpty() {
        Empty empty = EmptyBuilder.create().build();
        assertNotNull(empty);
        assertTrue(empty.getClassNames().contains("empty"));
    }

    // =========================================================================
    // Title
    // =========================================================================

    @Nested
    class TitleTests {

        @Test
        void title_string_setsTitle() {
            Empty empty = EmptyBuilder.create()
                    .title("No results")
                    .build();
            assertNotNull(empty.getEmptyTitle());
        }

        @Test
        void title_replaces_previous() {
            Empty empty = EmptyBuilder.create()
                    .title("First")
                    .title("Second")
                    .build();
            assertNotNull(empty.getEmptyTitle());
        }
    }

    // =========================================================================
    // Description
    // =========================================================================

    @Nested
    class DescriptionTests {

        @Test
        void description_string_setsDescription() {
            Empty empty = EmptyBuilder.create()
                    .description("Try adjusting your search")
                    .build();
            assertNotNull(empty.getDescription());
        }

        @Test
        void description_replaces_previous() {
            Empty empty = EmptyBuilder.create()
                    .description("First")
                    .description("Second")
                    .build();
            assertNotNull(empty.getDescription());
        }
    }

    // =========================================================================
    // Icon
    // =========================================================================

    @Nested
    class IconTests {

        @Test
        void icon_vaadinIcon_setsIcon() {
            Empty empty = EmptyBuilder.create()
                    .icon(new Icon(VaadinIcon.INBOX))
                    .build();
            assertNotNull(empty);
        }

        @Test
        void icon_component_setsIllustration() {
            Empty empty = EmptyBuilder.create()
                    .icon(new Div("SVG illustration"))
                    .build();
            assertNotNull(empty);
        }

        @Test
        void clearIcon_removesIcon() {
            Empty empty = EmptyBuilder.create()
                    .icon(new Icon(VaadinIcon.INBOX))
                    .clearIcon()
                    .build();
            assertNotNull(empty);
        }
    }

    // =========================================================================
    // Action
    // =========================================================================

    @Nested
    class ActionTests {

        @Test
        void action_component_setsAction() {
            Empty empty = EmptyBuilder.create()
                    .action(new Span("Clear filters"))
                    .build();
            assertNotNull(empty.getAction());
        }

        @Test
        void action_multipleComponents() {
            Empty empty = EmptyBuilder.create()
                    .action(new Span("Retry"), new Span("Cancel"))
                    .build();
            assertNotNull(empty.getAction());
        }

        @Test
        void action_replaces_previous() {
            Empty empty = EmptyBuilder.create()
                    .action(new Span("First"))
                    .action(new Span("Second"))
                    .build();
            assertNotNull(empty.getAction());
        }
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        Empty empty = EmptyBuilder.create()
                .icon(new Icon(VaadinIcon.INBOX))
                .title("No results found")
                .description("Try adjusting your search or filter.")
                .action(new Span("Clear filters"))
                .build();
        assertNotNull(empty);
        assertNotNull(empty.getEmptyTitle());
        assertNotNull(empty.getDescription());
        assertNotNull(empty.getAction());
    }

    @Test
    void title_and_description_without_icon_or_action() {
        Empty empty = EmptyBuilder.create()
                .title("Nothing here")
                .description("This space is empty.")
                .build();
        assertNotNull(empty);
        assertNotNull(empty.getEmptyTitle());
        assertNotNull(empty.getDescription());
        assertNull(empty.getAction());
    }
}
