package com.holonplatform.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link LazyTabsBuilder}.
 *
 * <p>Updated for the current API where:
 * <ul>
 *   <li>{@code build()} returns the {@link Tabs} component (not a {@code VerticalLayout}).</li>
 *   <li>The content container is no longer auto-created — callers must wire one via
 *       {@link LazyTabsBuilder#withContainer(Div)} for content to render.</li>
 *   <li>{@link LazyTabsBuilder#buildHorizontal()} produces a side-by-side
 *       {@link HorizontalLayout} of {@code Tabs} + content container.</li>
 * </ul>
 */
class TestLazyTabsBuilder {

    @Test
    void create_returnsNonNull() {
        assertNotNull(LazyTabsBuilder.create());
    }

    @Test
    void build_default_returnsTabsComponent() {
        Tabs tabs = LazyTabsBuilder.create().build();
        assertNotNull(tabs);
        assertEquals(0, tabs.getTabCount());
    }

    @Test
    void withEagerTab_addsTabToTabs() {
        LazyTabsBuilder builder = LazyTabsBuilder.create()
                .withContainer(new Div())
                .withEagerTab("Tab1", new Span("Content1"));
        Tabs tabs = builder.build();
        assertNotNull(tabs);
        assertEquals(1, tabs.getTabCount());
    }

    @Test
    void withLazyTab_addsTabToTabs() {
        LazyTabsBuilder builder = LazyTabsBuilder.create()
                .withContainer(new Div())
                .withLazyTab("Lazy", () -> new Span("Lazy content"));
        Tabs tabs = builder.build();
        assertNotNull(tabs);
        assertEquals(1, tabs.getTabCount());
    }

    @Test
    void multipleTabs_eagerAndLazy() {
        Tabs tabs = LazyTabsBuilder.create()
                .withContainer(new Div())
                .withEagerTab("Eager1", new Span("E1"))
                .withLazyTab("Lazy1", () -> new Span("L1"))
                .withEagerTab("Eager2", new Span("E2"))
                .build();
        assertEquals(3, tabs.getTabCount());
    }

    @Test
    void orientation_vertical() {
        Tabs tabs = LazyTabsBuilder.create()
                .withContainer(new Div())
                .withEagerTab("A", new Span("A"))
                .orientation(Tabs.Orientation.VERTICAL)
                .build();
        assertEquals(Tabs.Orientation.VERTICAL, tabs.getOrientation());
    }

    @Test
    void cacheEnabled_doesNotThrow_andTabIsAdded() {
        Tabs tabs = LazyTabsBuilder.create()
                .withContainer(new Div())
                .cacheEnabled()
                .withLazyTab("Cached", () -> new Span("cached"))
                .build();
        assertEquals(1, tabs.getTabCount());
    }

    @Test
    void getTabs_returnsBackingTabsComponent() {
        LazyTabsBuilder builder = LazyTabsBuilder.create()
                .withContainer(new Div())
                .withEagerTab("X", new Span("X"));
        Tabs tabs = builder.getTabs();
        assertNotNull(tabs);
        assertSame(tabs, builder.build(), "build() should return the same Tabs instance as getTabs()");
    }

    @Test
    void getContentContainer_returnsTheWiredContainer() {
        Div container = new Div();
        LazyTabsBuilder builder = LazyTabsBuilder.create()
                .withContainer(container)
                .withEagerTab("Y", new Span("Y"));
        assertSame(container, builder.getContentContainer(),
                "getContentContainer() should return the Div passed to withContainer(...)");
    }

    @Test
    void buildHorizontal_wrapsTabsAndContainer() {
        Div container = new Div();
        LazyTabsBuilder builder = LazyTabsBuilder.create()
                .withContainer(container)
                .withEagerTab("Wrapped", new Span("w"));

        HorizontalLayout layout = builder.buildHorizontal();
        assertNotNull(layout);

        var children = layout.getChildren().toList();
        assertEquals(2, children.size(), "Wrapper should contain Tabs + content container");
        assertSame(builder.getTabs(), children.get(0));
        assertSame(container, children.get(1));
    }

    @Test
    void fluent_chain_buildsTabsWithExpectedTabCount() {
        Tabs tabs = LazyTabsBuilder.create()
                .withContainer(new Div())
                .withEagerTab("Overview", new Span("overview"))
                .withLazyTab("Details", () -> new Span("details"))
                .cacheEnabled()
                .flexGrowForEnclosedTabs(1.0)
                .build();
        assertNotNull(tabs);
        assertTrue(tabs.getTabCount() >= 2);
    }
}
