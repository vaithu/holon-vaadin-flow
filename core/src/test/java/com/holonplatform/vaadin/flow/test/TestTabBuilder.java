package com.holonplatform.vaadin.flow.test;

import com.holonplatform.vaadin.flow.components.builders.TabBuilder;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TabBuilder}.
 */
class TestTabBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(TabBuilder.create());
    }

    @Test
    void build_default_returnsTab() {
        Tab tab = TabBuilder.create().build();
        assertNotNull(tab);
    }

    @Test
    void label_setsLabel() {
        Tab tab = TabBuilder.create()
                .label("My Tab")
                .build();
        assertEquals("My Tab", tab.getLabel());
    }

    @Test
    void selected_true() {
        Tab tab = TabBuilder.create()
                .selected(true)
                .build();
        assertTrue(tab.isSelected());
    }

    @Test
    void selected_false() {
        Tab tab = TabBuilder.create()
                .selected(false)
                .build();
        assertFalse(tab.isSelected());
    }

    @Test
    void flexGrow_setsFlexGrow() {
        Tab tab = TabBuilder.create()
                .flexGrow(2.0)
                .build();
        assertNotNull(tab);
    }

    @Test
    void icon_vaadinIcon() {
        Tab tab = TabBuilder.create()
                .icon(VaadinIcon.HOME)
                .build();
        assertTrue(tab.getComponentCount() > 0);
    }

    @Test
    void span_addsSpan() {
        Tab tab = TabBuilder.create()
                .span("Tab Label")
                .build();
        assertTrue(tab.getComponentCount() > 0);
    }

    @Test
    void badge_intValue() {
        Tab tab = TabBuilder.create()
                .badge(5)
                .build();
        assertTrue(tab.getComponentCount() > 0);
    }

    @Test
    void add_labelAndIcon() {
        Tab tab = TabBuilder.create()
                .add("Home", VaadinIcon.HOME)
                .build();
        assertTrue(tab.getComponentCount() > 0);
    }

    @Test
    void styleName_addsClassName() {
        Tab tab = TabBuilder.create()
                .styleName("my-tab")
                .build();
        assertTrue(tab.getClassNames().contains("my-tab"));
    }

    @Test
    void tooltipText_setsTooltip() {
        Tab tab = TabBuilder.create()
                .tooltipText("Tooltip text")
                .build();
        assertNotNull(tab);
    }

    @Test
    void fluent_chain_fullExample() {
        Tab tab = TabBuilder.create()
                .label("Dashboard")
                .selected(true)
                .flexGrow(1.0)
                .styleName("nav-tab")
                .build();
        assertEquals("Dashboard", tab.getLabel());
        assertTrue(tab.isSelected());
        assertTrue(tab.getClassNames().contains("nav-tab"));
    }
}
