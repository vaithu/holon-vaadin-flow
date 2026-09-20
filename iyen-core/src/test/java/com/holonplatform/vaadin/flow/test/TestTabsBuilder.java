package com.holonplatform.vaadin.flow.test;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.TabBuilder;
import com.iyensoft.vaadin.flow.components.builders.TabsBuilder;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestTabsBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(TabsBuilder.create());
    }

    @Test
    void build_default_returnsTabs() {
        Tabs tabs = TabsBuilder.create().build();
        assertNotNull(tabs);
    }

    @Test
    void add_tabs() {
        Tab t1 = new Tab("One");
        Tab t2 = new Tab("Two");
        Tabs tabs = TabsBuilder.create()
                .withTab(t1, t2)
                .build();
        assertEquals(2, tabs.getTabCount());
    }

    @Test
    void add_strings() {
        Tabs tabs = TabsBuilder.create()
                .withTab("Alpha", "Beta")
                .build();
        assertEquals(2, tabs.getTabCount());
    }

    @Test
    void add_singleStringLabel() {
        // TabsBuilder is a bar-only builder — use content() to register labeled tabs
        Tabs tabs = TabsBuilder.create()
                .withTab("Info")
                .build();
        assertEquals(1, tabs.getTabCount());
    }

    @Test
    void withTab_localizableLabel() {
        Localizable label = Localizable.builder().message("Overview").build();
        Tabs tabs = TabsBuilder.create()
                .withTab(label)
                .build();
        assertEquals(1, tabs.getTabCount());
    }

    @Test
    void orientation_vertical() {
        Tabs tabs = TabsBuilder.create()
                .orientation(Tabs.Orientation.VERTICAL)
                .build();
        assertEquals(Tabs.Orientation.VERTICAL, tabs.getOrientation());
    }

    @Test
    void autoselect_false() {
        Tabs tabs = TabsBuilder.create()
                .autoselect(false)
                .build();
        assertNotNull(tabs);
    }

    @Test
    void selectedIndex_sets() {
        Tabs tabs = TabsBuilder.create()
                .withTab("A", "B", "C")
                .selectedIndex(1)
                .build();
        assertEquals(1, tabs.getSelectedIndex());
    }

    @Test
    void flexGrowForEnclosedTabs() {
        Tabs tabs = TabsBuilder.create()
                .withTab("A", "B")
                .flexGrowForEnclosedTabs(1.0)
                .build();
        assertNotNull(tabs);
    }

    @Test
    void styleName_addsClassName() {
        Tabs tabs = TabsBuilder.create()
                .styleName("my-tabs")
                .build();
        assertTrue(tabs.getClassNames().contains("my-tabs"));
    }

    @Test
    void fluent_chain() {
        Tabs tabs = TabsBuilder.create()
                .withTab("Tab1", "Tab2")
                .orientation(Tabs.Orientation.HORIZONTAL)
                .autoselect(true)
                .flexGrowForEnclosedTabs(1.0)
                .styleName("nav-tabs")
                .build();
        assertNotNull(tabs);
        assertEquals(2, tabs.getTabCount());
    }

    @Test
    void tabBuilder_create_returnsNonNull() {
        assertNotNull(TabBuilder.create());
    }
}
