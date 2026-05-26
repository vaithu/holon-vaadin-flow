package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.NativeButtonBuilder;
import com.vaadin.flow.component.html.NativeButton;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link NativeButtonBuilder}.
 */
class TestNativeButtonBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(NativeButtonBuilder.create());
    }

    @Test
    void build_default_returnsNativeButton() {
        NativeButton btn = NativeButtonBuilder.create().build();
        assertNotNull(btn);
    }

    @Test
    void text_setsText() {
        NativeButton btn = NativeButtonBuilder.create()
                .text("Click me")
                .build();
        assertEquals("Click me", btn.getText());
    }

    @Test
    void id_setsId() {
        NativeButton btn = NativeButtonBuilder.create()
                .id("native-btn")
                .build();
        assertEquals("native-btn", btn.getId().orElse(null));
    }

    @Test
    void styleName_addsClass() {
        NativeButton btn = NativeButtonBuilder.create()
                .styleName("custom")
                .build();
        assertTrue(btn.getClassNames().contains("custom"));
    }

    @Test
    void enabled_false() {
        NativeButton btn = NativeButtonBuilder.create()
                .enabled(false)
                .build();
        assertFalse(btn.isEnabled());
    }

    @Test
    void tabIndex_setsTabIndex() {
        NativeButton btn = NativeButtonBuilder.create()
                .tabIndex(3)
                .build();
        assertEquals(3, btn.getTabIndex());
    }

    @Test
    void withClickListener_adds() {
        NativeButton btn = NativeButtonBuilder.create()
                .withClickListener(e -> {})
                .build();
        assertNotNull(btn);
    }

    @Test
    void fluent_chain() {
        NativeButton btn = NativeButtonBuilder.create()
                .text("Submit")
                .id("nb-submit")
                .enabled(true)
                .tabIndex(1)
                .styleName("action")
                .withClickListener(e -> {})
                .build();
        assertNotNull(btn);
        assertEquals("Submit", btn.getText());
        assertEquals("nb-submit", btn.getId().orElse(null));
        assertTrue(btn.getClassNames().contains("action"));
    }
}
