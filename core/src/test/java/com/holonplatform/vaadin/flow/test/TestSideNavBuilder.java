package com.holonplatform.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.builders.SideNavBuilder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SideNavBuilder}.
 */
class TestSideNavBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(SideNavBuilder.create());
    }

    @Test
    void build_default_returnsSideNav() {
        SideNav nav = SideNavBuilder.create().build();
        assertNotNull(nav);
    }

    @Test
    void configure_existingSideNav() {
        SideNav existing = new SideNav();
        SideNavBuilder builder = SideNavBuilder.configure(existing);
        assertNotNull(builder);
        assertSame(existing, builder.build());
    }

    @Test
    void withItem_addsSideNavItem() {
        SideNavItem item = new SideNavItem("Home");
        SideNav nav = SideNavBuilder.create()
                .withItem(item)
                .build();
        assertFalse(nav.getItems().isEmpty());
    }

    @Test
    void label_setsLabel() {
        SideNav nav = SideNavBuilder.create()
                .label("Navigation")
                .build();
        assertEquals("Navigation", nav.getLabel());
    }

    @Test
    void collapsible_true() {
        SideNav nav = SideNavBuilder.create()
                .label("Nav")
                .collapsible(true)
                .build();
        assertNotNull(nav);
    }

    @Test
    void expanded_false() {
        SideNav nav = SideNavBuilder.create()
                .label("Nav")
                .expanded(false)
                .build();
        assertNotNull(nav);
    }

    @Test
    void withNavItem_string_returnsItemBuilder() {
        SideNavBuilder builder = SideNavBuilder.create();
        assertNotNull(builder.withNavItem("Dashboard"));
    }

    @Test
    void withNavItem_stringAndPath_returnsItemBuilder() {
        SideNavBuilder builder = SideNavBuilder.create();
        assertNotNull(builder.withNavItem("Reports", "/reports"));
    }

    @Test
    void withSearch_enablesSearch() {
        SideNavBuilder builder = SideNavBuilder.create()
                .withItem(new SideNavItem("Home"))
                .withSearch();
        assertNotNull(builder);
    }

    @Test
    void withCollapse_enablesCollapse() {
        SideNavBuilder builder = SideNavBuilder.create()
                .withItem(new SideNavItem("Home"))
                .withCollapse();
        assertNotNull(builder);
    }

    @Test
    void buildWrapper_returnsDiv() {
        Div wrapper = SideNavBuilder.create()
                .withItem(new SideNavItem("Home"))
                .withSearch()
                .withCollapse()
                .buildWrapper();
        assertNotNull(wrapper);
        assertTrue(wrapper.getClassNames().contains("sidenav-host"));
    }

    @Test
    void getItems_returnsAddedItems() {
        SideNavItem item1 = new SideNavItem("A");
        SideNavItem item2 = new SideNavItem("B");
        SideNavBuilder builder = SideNavBuilder.create()
                .withItem(item1, item2);
        assertEquals(2, builder.getItems().size());
    }

    @Test
    void fluent_chain() {
        Div wrapper = SideNavBuilder.create()
                .label("Main Menu")
                .collapsible(true)
                .expanded(true)
                .withItem(new SideNavItem("Home"))
                .withItem(new SideNavItem("Settings"))
                .withSearch("Filter...")
                .withCollapse()
                .buildWrapper();
        assertNotNull(wrapper);
    }
}
