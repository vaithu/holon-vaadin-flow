package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestColor {

    @Test
    void testBackgroundClassNames() {
        assertEquals("color-bg-base", Color.Background.BASE.getClassName());
        assertEquals("color-bg-primary", Color.Background.PRIMARY.getClassName());
        assertEquals("color-bg-error", Color.Background.ERROR.getClassName());
        assertEquals("color-bg-success", Color.Background.SUCCESS.getClassName());
        assertEquals("color-bg-contrast", Color.Background.CONTRAST.getClassName());
    }

    @Test
    void testAllBackgroundsHaveClassNames() {
        for (Color.Background bg : Color.Background.values()) {
            assertNotNull(bg.getClassName());
            assertTrue(bg.getClassName().startsWith("color-bg-"));
        }
    }

    @Test
    void testTextClassNames() {
        assertEquals("color-text-header", Color.Text.HEADER.getClassName());
        assertEquals("color-text-body", Color.Text.BODY.getClassName());
        assertEquals("color-text-secondary", Color.Text.SECONDARY.getClassName());
        assertEquals("color-text-primary", Color.Text.PRIMARY.getClassName());
        assertEquals("color-text-error", Color.Text.ERROR.getClassName());
        assertEquals("color-text-success", Color.Text.SUCCESS.getClassName());
    }

    @Test
    void testAllTextsHaveClassNames() {
        for (Color.Text text : Color.Text.values()) {
            assertNotNull(text.getClassName());
            assertTrue(text.getClassName().startsWith("color-text-"));
        }
    }
}
