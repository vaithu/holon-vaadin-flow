package com.holonplatform.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LazyTabsBuilder}.
 */
class TestLazyTabsBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(LazyTabsBuilder.create());
    }

    @Test
    void build_default_returnsVerticalLayout() {
        VerticalLayout layout = LazyTabsBuilder.create().build();
        assertNotNull(layout);
    }

    @Test
    void withEagerTab_addsTab() {
        VerticalLayout layout = LazyTabsBuilder.create()
                .withEagerTab("Tab1", new Span("Content1"))
                .build();
        assertNotNull(layout);
        assertTrue(layout.getComponentCount() > 0);
    }

    @Test
    void withLazyTab_addsTab() {
        VerticalLayout layout = LazyTabsBuilder.create()
                .withLazyTab("Lazy", () -> new Span("Lazy content"))
                .build();
        assertNotNull(layout);
        assertTrue(layout.getComponentCount() > 0);
    }

    @Test
    void multipleTabs_eagerAndLazy() {
        VerticalLayout layout = LazyTabsBuilder.create()
                .withEagerTab("Eager1", new Span("E1"))
                .withLazyTab("Lazy1", () -> new Span("L1"))
                .withEagerTab("Eager2", new Span("E2"))
                .build();
        assertNotNull(layout);
    }

    @Test
    void orientation_vertical() {
        VerticalLayout layout = LazyTabsBuilder.create()
                .withEagerTab("A", new Span("A"))
                .orientation(Tabs.Orientation.VERTICAL)
                .build();
        assertNotNull(layout);
    }

    @Test
    void cacheEnabled_enablesCache() {
        VerticalLayout layout = LazyTabsBuilder.create()
                .cacheEnabled()
                .withLazyTab("Cached", () -> new Span("cached"))
                .build();
        assertNotNull(layout);
    }

    @Test
    void getTabs_returnsTabsComponent() {
        LazyTabsBuilder builder = LazyTabsBuilder.create()
                .withEagerTab("X", new Span("X"));
        assertNotNull(builder.getTabs());
    }

    @Test
    void getContentContainer_returnsContainer() {
        LazyTabsBuilder builder = LazyTabsBuilder.create()
                .withEagerTab("Y", new Span("Y"));
        assertNotNull(builder.getContentContainer());
    }

    @Test
    void fluent_chain() {
        VerticalLayout layout = LazyTabsBuilder.create()
                .withEagerTab("Overview", new Span("overview"))
                .withLazyTab("Details", () -> new Span("details"))
                .cacheEnabled()
                .flexGrowForEnclosedTabs(1.0)
                .build();
        assertNotNull(layout);
        assertTrue(layout.getComponentCount() >= 2);
    }
}
