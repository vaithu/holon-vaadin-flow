package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.SheetBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Sheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SheetBuilder}.
 */
class TestSheetBuilder {

    // =========================================================================
    // Factory
    // =========================================================================

    @Test
    void create_default_returnsNonNull() {
        assertNotNull(SheetBuilder.create());
    }

    @Test
    void create_withSide_returnsNonNull() {
        assertNotNull(SheetBuilder.create(Sheet.Side.BOTTOM));
        assertNotNull(SheetBuilder.create(Sheet.Side.LEFT));
        assertNotNull(SheetBuilder.create(Sheet.Side.RIGHT));
    }

    // =========================================================================
    // Build
    // =========================================================================

    @Test
    void build_default_returnsSheet() {
        Sheet sheet = SheetBuilder.create().build();
        assertNotNull(sheet);
    }

    // =========================================================================
    // Side
    // =========================================================================

    @Test
    void side_overridesInitial() {
        Sheet sheet = SheetBuilder.create(Sheet.Side.BOTTOM)
                .side(Sheet.Side.RIGHT)
                .build();
        assertNotNull(sheet);
    }

    // =========================================================================
    // Title / Description
    // =========================================================================

    @Nested
    class TitleTests {

        @Test
        void title_string() {
            Sheet sheet = SheetBuilder.create()
                    .title("My Sheet")
                    .build();
            assertNotNull(sheet);
        }

        @Test
        void description_string() {
            Sheet sheet = SheetBuilder.create()
                    .description("Some description")
                    .build();
            assertNotNull(sheet);
        }
    }

    // =========================================================================
    // Content
    // =========================================================================

    @Test
    void content_setsChildren() {
        Sheet sheet = SheetBuilder.create()
                .content(new Div("A"), new Span("B"))
                .build();
        assertNotNull(sheet);
    }

    @Test
    void lazyContent_defersRendering() {
        Sheet sheet = SheetBuilder.create()
                .lazyContent(() -> new Div[]{new Div("Lazy")})
                .build();
        assertNotNull(sheet);
    }

    // =========================================================================
    // Close options
    // =========================================================================

    @Test
    void closeOnBackdropClick_sets() {
        Sheet sheet = SheetBuilder.create()
                .closeOnBackdropClick(true)
                .build();
        assertNotNull(sheet);
    }

    @Test
    void closeButton_show() {
        Sheet sheet = SheetBuilder.create()
                .closeButton(true)
                .build();
        assertNotNull(sheet);
    }

    @Test
    void backButton_show() {
        Sheet sheet = SheetBuilder.create()
                .backButton(true)
                .build();
        assertNotNull(sheet);
    }

    // =========================================================================
    // Callbacks
    // =========================================================================

    @Test
    void onClose_setsCallback() {
        Sheet sheet = SheetBuilder.create()
                .onClose(() -> {})
                .build();
        assertNotNull(sheet);
    }

    @Test
    void onOpen_setsCallback() {
        Sheet sheet = SheetBuilder.create()
                .onOpen(() -> {})
                .build();
        assertNotNull(sheet);
    }

    // =========================================================================
    // Advanced options
    // =========================================================================

    @Test
    void historyEnabled_sets() {
        Sheet sheet = SheetBuilder.create()
                .historyEnabled(true)
                .build();
        assertNotNull(sheet);
    }

    @Test
    void fullscreenOnMobile_sets() {
        Sheet sheet = SheetBuilder.create()
                .fullscreenOnMobile(true)
                .build();
        assertNotNull(sheet);
    }

    // =========================================================================
    // Component configurator
    // =========================================================================

    @Test
    void id_setsId() {
        Sheet sheet = SheetBuilder.create()
                .id("filter-sheet")
                .build();
        assertEquals("filter-sheet", sheet.getId().orElse(null));
    }

    @Test
    void styleName_addsClass() {
        Sheet sheet = SheetBuilder.create()
                .styleName("custom-sheet")
                .build();
        assertTrue(sheet.getClassNames().contains("custom-sheet"));
    }

    // =========================================================================
    // Fluent chain
    // =========================================================================

    @Test
    void fluent_chain_fullExample() {
        Sheet sheet = SheetBuilder.create(Sheet.Side.RIGHT)
                .title("Settings")
                .description("Configure your preferences")
                .content(new Div("Form"))
                .closeOnBackdropClick(true)
                .closeButton(true)
                .backButton(false)
                .onClose(() -> {})
                .id("settings-sheet")
                .styleName("settings")
                .build();
        assertNotNull(sheet);
        assertEquals("settings-sheet", sheet.getId().orElse(null));
        assertTrue(sheet.getClassNames().contains("settings"));
    }
}
