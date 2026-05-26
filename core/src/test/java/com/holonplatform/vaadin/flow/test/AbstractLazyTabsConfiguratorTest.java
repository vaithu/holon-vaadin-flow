package com.holonplatform.vaadin.flow.test;

import com.iyensoft.vaadin.flow.internal.components.builders.AbstractLazyTabsConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Supplier-based AbstractTabsConfigurator (Vaadin 24.9 / Supplier API).
 */
public class AbstractLazyTabsConfiguratorTest {

    private TestLazyTabsConfigurator testee;

    /**
     * Minimal concrete configurator so we can instantiate and access the underlying Tabs.
     */
    private static class TestLazyTabsConfigurator extends AbstractLazyTabsConfigurator<TestLazyTabsConfigurator> {
        public TestLazyTabsConfigurator() {
            super(new VerticalLayout());
        }
        @Override
        protected TestLazyTabsConfigurator getConfigurator() {
            return this;
        }

    }

    // Tiny component to distinguish instances in identity assertions
    @Tag("x-sample")
    private static class SampleComp extends HtmlComponent {
        private final String id = UUID.randomUUID().toString();
        public String getIdStr() { return id; }
    }

    @BeforeEach
    void setUp() {
        testee = new TestLazyTabsConfigurator();
    }

    /** Helper to fetch the single content component currently shown. */
    private Component soleContent() {
        List<Component> comps = testee.getContentContainer().getChildren().toList();
        if (comps.isEmpty()) return null;
        assertEquals(1, comps.size(), "Content container should host exactly one component");
        return comps.get(0);
    }

    // ---------------------------------------------------
    // 1) Programmatic selection via selectedIndex renders
    // ---------------------------------------------------

    @Test
    void rendersContentOnProgrammaticSelection() {
        // Eager tabs (withTab adds the Tab to the Tabs component)
        Div a = new Div("A");
        Div b = new Div("B");
        testee.withEagerTab("A", a)
                .withEagerTab("B", b);

        // select 1st (index 0)
        testee.selectedIndex(0);
        assertSame(a, soleContent(), "Selecting index 0 should render component A");

        // select 2nd (index 1)
        testee.selectedIndex(1);
        assertSame(b, soleContent(), "Selecting index 1 should render component B");
    }

    // ----------------------------------------------------
    // 2) Eager registration (same component instance reuse)
    // ----------------------------------------------------

    @Test
    void eagerRegistrationReusesSameInstanceRegardlessOfCache() {
        Tab t1 = new Tab("Eager");
        Tab t2 = new Tab("Other");

        SampleComp eagerComp = new SampleComp();
        testee.withEagerTab(t1, eagerComp); // adds t1
        testee.withEagerTab(t2, new SampleComp()); // adds t2

        testee.enableCache(false);

        // select t1 (index 0)
        testee.selectedIndex(0);
        Component first = soleContent();

        // select t2 (index 1), then back to t1
        testee.selectedIndex(1);
        testee.selectedIndex(0);
        Component second = soleContent();

        assertSame(first, second, "Eagerly registered component must be the same instance");
    }

    // -------------------------------------------------------------
    // 3) Lazy registration WITHOUT caching (new instance each time)
    // -------------------------------------------------------------

    @Test
    void lazyWithoutCacheCreatesNewInstanceOnEachSelection() {
        testee.enableCache(false);

        Tab t1 = new Tab("LazyNoCache");
        Tab t2 = new Tab("Other");

        // IMPORTANT: withTab(Tab, Supplier) DOES NOT add the tab to the Tabs component.
        // Use withLazyTab(Tab, Supplier) which DOES add it.
        testee.withLazyTab(t1, SampleComp::new);      // adds t1 + supplier
        testee.withEagerTab(t2, new SampleComp());         // adds t2 (eager)

        // select t1 (index 0)
        testee.selectedIndex(0);
        Component first = soleContent();
        assertTrue(first instanceof SampleComp);

        // select t2 (index 1), then back to t1
        testee.selectedIndex(1);
        testee.selectedIndex(0);
        Component second = soleContent();
        assertTrue(second instanceof SampleComp);

        assertNotSame(first, second, "Lazy without cache should create a NEW instance each selection");
    }

    // --------------------------------------------------------
    // 4) Lazy registration WITH caching (create once, reuse)
    // --------------------------------------------------------

    @Test
    void lazyWithCacheCreatesOnceThenReuses() {
        testee.cacheEnabled();

        Tab t1 = new Tab("LazyCache");
        Tab t2 = new Tab("Other");

        testee.withLazyTab(t1, SampleComp::new);  // adds t1 + supplier
        testee.withEagerTab(t2, new SampleComp());     // adds t2 (eager)

        testee.selectedIndex(0);
        Component first = soleContent();

        // switch away and back
        testee.selectedIndex(1);
        testee.selectedIndex(0);
        Component second = soleContent();

        assertSame(first, second, "With cache enabled, lazy content should be reused");
    }

    // --------------------------------------------------------
    // 5) SelectedChange listener chaining still works
    // --------------------------------------------------------

    @Test
    void userSelectedChangeListenerStillFires() {
        // add two eager tabs
        testee.withEagerTab("L1", new Div("L1"))
                .withEagerTab("L2", new Div("L2"));

        AtomicInteger called = new AtomicInteger(0);
        ComponentEventListener<Tabs.SelectedChangeEvent> listener = ev -> called.incrementAndGet();
        testee.withSelectedChangeListener(listener);

        testee.selectedIndex(0); // initial
        testee.selectedIndex(1); // should fire at least once

        assertTrue(called.get() >= 1, "Custom selected change listener should be invoked");
    }

    // --------------------------------------------------------
    // 6) Orientation, autoselect, theme variants smoke checks
    // --------------------------------------------------------

    @Test
    void setsOrientationAutoSelectAndVariants() {
        testee.orientation(Tabs.Orientation.VERTICAL)
                .autoselect(true)
                .withThemeVariants(TabsVariant.LUMO_MINIMAL);

        assertEquals(Tabs.Orientation.VERTICAL, testee.getTabs().getOrientation());
        assertTrue(testee.getTabs().isAutoselect(), "Autoselect should be true");
        // Theme variants presence is not straightforward to assert without checking internal theme names;
        // this test ensures no exception is thrown during setup.
    }

    // --------------------------------------------------------
    // 7) buildTabsWithContent() wrapper contains tabs + content
    // --------------------------------------------------------

    @Test
    void buildTabsWithContentProducesWrapperWithEagerTabsAndContainer() {
        // Prepare one tab to ensure content can render
        testee.withEagerTab("Wrapper", new Div("X"));
        testee.selectedIndex(0);

        Component wrapper = testee.buildTabsWithContent();
        assertTrue(wrapper instanceof VerticalLayout);

        List<Component> children = ((VerticalLayout) wrapper).getChildren().toList();
        assertEquals(2, children.size(), "Wrapper should contain Tabs + content container");

        // Expect the first child to be Tabs, second the content container
        assertSame(testee.getTabs(), children.get(0), "First child should be the Tabs component");
        assertSame(testee.getContentContainer(), children.get(1), "Second child should be the content container");
    }

    // --------------------------------------------------------
    // 8) (Optional) Document current bug: withLazyTab(String, Supplier)
    //    DOES NOT add the tab to the Tabs component
    // --------------------------------------------------------

    @Test
    void withLazyTabStringShouldAddTabToTabs() {
        int before = testee.getTabs().getTabCount();
        testee.withLazyTab("LazyByLabel", SampleComp::new);
        int after = testee.getTabs().getTabCount();
        assertEquals(before + 1, after, "withLazyTab(String, Supplier) should add the Tab to the Tabs component");
    }

    // --------------------------------------------------------
    // 9) i18n (Localizable) overloads — fallback to message()
    // --------------------------------------------------------

    @Test
    void withEagerTab_localizableLabel_usesMessageAsFallback() {
        com.holonplatform.core.i18n.Localizable label =
                com.holonplatform.core.i18n.Localizable.builder().message("i18n-tab").build();
        testee.withEagerTab(label, new Div("i18n content"));

        int tabCount = testee.getTabs().getTabCount();
        assertEquals(1, tabCount, "Localizable eager tab should add one Tab to the bar");

        // Verify label fallback resolves to the message string
        com.vaadin.flow.component.tabs.Tab added = testee.getTabs()
                .getChildren()
                .filter(c -> c instanceof com.vaadin.flow.component.tabs.Tab)
                .map(c -> (com.vaadin.flow.component.tabs.Tab) c)
                .findFirst().orElseThrow();
        assertEquals("i18n-tab", added.getLabel(), "Tab label should fall back to Localizable#getMessage()");
    }

    @Test
    void withLazyTab_localizableLabel_usesMessageAsFallback() {
        com.holonplatform.core.i18n.Localizable label =
                com.holonplatform.core.i18n.Localizable.builder().message("lazy-i18n").build();
        testee.withLazyTab(label, SampleComp::new);

        assertEquals(1, testee.getTabs().getTabCount());
        testee.selectedIndex(0);
        assertTrue(soleContent() instanceof SampleComp);
    }
}