package com.iyensoft.vaadin.flow.test;

import com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder;
import com.iyensoft.vaadin.flow.components.DetailSyncAware;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        // withContainer() stores the passed Div directly as the content container so that
        // tab content is rendered into the caller's own DOM element (not a hidden off-DOM wrapper).
        assertNotNull(builder.getContentContainer(),
                "getContentContainer() must return a non-null container");
        assertSame(container, builder.getContentContainer(),
                "getContentContainer() must return the same Div that was passed to withContainer()");
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
        // Second child is the SyncableContentContainer returned by getContentContainer()
        assertSame(builder.getContentContainer(), children.get(1),
                "Second child of buildHorizontal() must be the SyncableContentContainer");
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

    @Test
    void eagerTabContents_areDirectChildrenAndRemainSyncableWhenRevisited() {
        SyncAwareDiv tabContents = new SyncAwareDiv();
        Span child = new Span("Overview content");
        tabContents.add(child);

        LazyTabsBuilder builder = LazyTabsBuilder.create()
                .withEagerTabContents("Overview", tabContents)
                .withEagerTab("Other", new Span("Other content"));
        Div contentContainer = builder.getContentContainer();
        sync(contentContainer, "customer-1");
        builder.selectedIndex(0);

        assertTrue(contentContainer.getClassNames().contains("d-body"));
        assertEquals(1, contentContainer.getChildren().count());
        assertSame(child, contentContainer.getChildren().findFirst().orElseThrow());
        assertSame(contentContainer, child.getParent().orElseThrow());
        assertEquals("customer-1", tabContents.lastItem);

        builder.getTabs().setSelectedIndex(1);
        assertSame(tabContents, child.getParent().orElseThrow());

        builder.getTabs().setSelectedIndex(0);
        assertEquals(1, contentContainer.getChildren().count());
        assertSame(child, contentContainer.getChildren().findFirst().orElseThrow());
        assertEquals("customer-1", tabContents.lastItem);
    }

    @Test
    void lazyTabContents_addsChildrenDirectlyWhenSelected() {
        AtomicInteger factoryCalls = new AtomicInteger();
        Span content = new Span("Lazy content");
        LazyTabsBuilder builder = LazyTabsBuilder.create()
                .withEagerTab("Overview", new Span("Overview"))
                .withLazyTabContents("Orders", 3, () -> {
                    factoryCalls.incrementAndGet();
                    Div contents = new Div(content);
                    return contents;
                });

        assertEquals(0, factoryCalls.get());
        builder.getTabs().setSelectedIndex(1);

        assertEquals(1, factoryCalls.get());
        assertSame(content, builder.getContentContainer().getChildren().findFirst().orElseThrow());
        assertSame(builder.getContentContainer(), content.getParent().orElseThrow());
    }

    @Test
    void selectionCounter_isHiddenUntilSelectionThenTracksEachItem() {
        AtomicInteger counterCalls = new AtomicInteger();
        LazyTabsBuilder builder = LazyTabsBuilder.create()
                .withEagerTab("Overview", new Span("Overview"))
                .withLazyTab("Orders", (String item) -> {
                    counterCalls.incrementAndGet();
                    return item.length();
                }, () -> new Span("Orders"));
        Span badge = badgeOf(builder, 1);

        assertFalse(badge.isVisible(), "No selection yet: the badge must not show a stale value");
        assertEquals(0, counterCalls.get());

        sync(builder.getContentContainer(), "abc");
        assertTrue(badge.isVisible());
        assertEquals("3", badge.getText());

        sync(builder.getContentContainer(), "abcdef");
        assertEquals("6", badge.getText());
        assertEquals(2, counterCalls.get(),
                "Counters refresh on selection even when their tab has never been opened");
    }

    @Test
    void selectionCounter_hidesBadgeWhenCounterReturnsNull() {
        LazyTabsBuilder builder = LazyTabsBuilder.create()
                .withLazyTabContents("Files", (String item) -> null, Div::new);
        Span badge = badgeOf(builder, 0);

        sync(builder.getContentContainer(), "customer-1");

        assertFalse(badge.isVisible());
    }

    private static Span badgeOf(LazyTabsBuilder builder, int tabIndex) {
        return builder.getTabs().getTabAt(tabIndex).getChildren()
                .filter(Span.class::isInstance).map(Span.class::cast)
                .reduce((first, second) -> second).orElseThrow();
    }

    @SuppressWarnings("unchecked")
    private static void sync(Div container, Object item) {
        ((DetailSyncAware<Object>) container).onItemSelected(item);
    }

    private static final class SyncAwareDiv extends Div implements DetailSyncAware<String> {
        private String lastItem;

        @Override
        public void onItemSelected(String item) {
            lastItem = item;
        }
    }
}
