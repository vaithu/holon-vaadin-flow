package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.iyensoft.vaadin.flow.components.builders.LazyTabsConfigurator;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

/**
 * Tabs configurator with optional lazy content and caching.
 */
public abstract class AbstractLazyTabsConfigurator<C extends LazyTabsConfigurator<C>>
        extends AbstractComponentConfigurator<VerticalLayout, C>
        implements LazyTabsConfigurator<C> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLazyTabsConfigurator.class);

    /**
     * Factories for tab content.
     * Semantics: supplier.get() returns a component instance (new or reused according to supplier policy).
     */
    private final Map<Tab, Supplier<Component>> tabSupplierMap = new HashMap<>();

    /**
     * Cache of realized components when caching is enabled.
     */
    private final Map<Tab, Component> cachedComponents = new HashMap<>();

    private Tab currentTab;

    private boolean enableCaching = false;

    @Getter
    private final Tabs tabs = new Tabs();

    // Display area
    @Getter
    private final VerticalLayout contentContainer = new VerticalLayout();

    public AbstractLazyTabsConfigurator(VerticalLayout component) {
        super(component);
        contentContainer.setPadding(false);
        contentContainer.setSpacing(false);
        contentContainer.setSizeFull();
        tabs.setWidthFull();

        // Keep the content area in sync with the selected tab
        tabs.addSelectedChangeListener(e -> switchToTab(e.getSelectedTab()));
    }

    /**
     * Convenience wrapper: Tabs on top + content below.
     */
    public Component buildTabsWithContent() {
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setPadding(false);
        wrapper.setSpacing(false);
        wrapper.setSizeFull();
        wrapper.add(tabs, contentContainer);
        return wrapper;
    }

    @Override
    public Tab getSelectedTab() {
        return getTabs().getSelectedTab();
    }

    protected void switchToTab(Tab tab) {
        if (tab == null) {
            contentContainer.removeAll();
            currentTab = null;
            return;
        }

        // If caching is OFF, evict the content we are leaving
        if (!enableCaching && currentTab != null) {
            cachedComponents.remove(currentTab);
        }

        Component content = null;

        // Only consult the cache when caching is enabled
        if (enableCaching) {
            content = cachedComponents.get(tab);
        }

        if (content == null) {
            Supplier<Component> supplier = tabSupplierMap.get(tab);
            if (supplier == null) {
                // Fallback UI + warn
                content = new Div("No content registered for this tab.");
                LOGGER.warn("No content supplier registered for tab: {}", safeLabel(tab));
            } else {
                content = supplier.get(); // create or return supplier-provided instance
                if (enableCaching && content != null) {
                    cachedComponents.put(tab, content);
                }
            }
        }

        if (content != null) {
            contentContainer.removeAll();
            contentContainer.add(content);
        }

        currentTab = tab;
    }

    private static String safeLabel(Tab tab) {
        try {
            return tab.getLabel();
        } catch (Exception e) {
            return "(unlabeled)";
        }
    }

    // ----------------------------
    // TabsConfigurator API
    // ----------------------------

    @Override
    public C enableCache(boolean enableCache) {
        this.enableCaching = enableCache;
        return getConfigurator();
    }

    @Override
    public C cacheEnabled() {
        this.enableCaching = true;
        return getConfigurator();
    }

    @Override
    public C scrollIntoView() {
        tabs.scrollIntoView();
        return getConfigurator();
    }

    @Override
    public C scrollIntoView(ScrollOptions scrollOptions) {
        tabs.scrollIntoView(scrollOptions);
        return getConfigurator();
    }

    @Override
    public C autoSelect(boolean autoSelect) {
        // Vaadin 24.9 API
        tabs.setAutoselect(autoSelect);
        return getConfigurator();
    }

    @Override
    public C flexGrowForEnclosedTabs(double flexGrow) {
        tabs.setFlexGrowForEnclosedTabs(flexGrow);
        return getConfigurator();
    }

    @Override
    public C orientation(Tabs.Orientation orientation) {
        tabs.setOrientation(orientation);
        return getConfigurator();
    }

    @Override
    public C selectedIndex(int selectedIndex) {
        tabs.setSelectedIndex(selectedIndex);
        Tab selected = tabs.getSelectedTab();
        if (selected != null) {
            switchToTab(selected);
        }
        return getConfigurator();
    }

    @Override
    public C selectedTab(Tab tab) {
        getTabs().setSelectedTab(tab);
        Tab selected = tabs.getSelectedTab();
        if (selected != null) {
            switchToTab(selected);
        }
        return getConfigurator();
    }

    @Override
    public C selectedTab(String tabTitle) {
        // FIX: do not create a new Tab; resolve the existing child instead
        Tab existing = tabs.getChildren()
                .filter(c -> c instanceof Tab)
                .map(c -> (Tab) c)
                .filter(t -> Objects.equals(t.getLabel(), tabTitle))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            return selectedTab(existing);
        } else {
            LOGGER.warn("No child Tab with label '{}'", tabTitle);
            return getConfigurator();
        }
    }

    @Override
    public C withSelectedChangeEvent(ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
        tabs.addSelectedChangeListener(listener);
        return getConfigurator();
    }

    // ---------- Tab content registration (eager & lazy) ----------

    @Override
    public C withEagerTab(String label, Component component) {
        Tab tab = new Tab(label);
        return withEagerTab(tab, component);
    }

    @Override
    public C withEagerTab(Tab tab, Component component) {
        // Eager: always return the same instance provided by the caller
        tabs.add(tab);
        tabSupplierMap.put(tab, () -> component);
        return getConfigurator();
    }

    /**
     * Lazy convenience by label.
     */
    @Override
    public C withLazyTab(String label, Supplier<Component> supplier) {
        return withLazyTab(new Tab(label), supplier);
    }

    @Override
    public C withLazyTab(Tab tab, Supplier<Component> factory) {
        Objects.requireNonNull(factory, "The component cannot be null");
        tabs.add(tab);
        tabSupplierMap.put(tab, factory);
        return getConfigurator();
    }

    @Override
    public C withEagerTab(String label, Icon icon, Component component) {
        Tab tab = new Tab(icon, new Span(label));
        return withEagerTab(tab, component);
    }

    /**
     * Helper method for creating a badge.
     */
    private Span createBadge(int value) {
        return UIUtils.Badge.createBadge(value);
    }

    @Override
    public C withEagerTab(String label, int counter, Component component) {
        Tab tab = new Tab(new Span(label), createBadge(counter));
        return withEagerTab(tab, component);
    }

    @Override
    public C withEagerTab(String label, int counter, TabVariant tabVariant, Component component) {
        Tab tab = new Tab(new Span(label), createBadge(counter));
        tab.addThemeVariants(tabVariant);
        return withEagerTab(tab, component);
    }

    @Override
    public C withEagerTab(String label, Icon icon, TabVariant tabVariant, Component component) {
        Tab tab = new Tab(icon, new Span(label));
        tab.addThemeVariants(tabVariant);
        return withEagerTab(tab, component);
    }

    @Override
    public C withLazyTab(String label, Icon icon, Supplier<Component> component) {
        Tab tab = new Tab(icon, new Span(label));
        return withLazyTab(tab, component);
    }

    @Override
    public C withLazyTab(String label, int counter, Supplier<Component> factory) {
        Tab tab = new Tab(new Span(label), createBadge(counter));
        return withLazyTab(tab, factory);
    }

    @Override
    public C withLazyTab(String label, Icon icon, TabVariant tabVariant, Supplier<Component> factory) {
        Tab tab = new Tab(icon, new Span(label));
        tab.addThemeVariants(tabVariant);
        return withLazyTab(tab, factory);
    }

    @Override
    public C withLazyTab(String label, int counter, TabVariant tabVariant, Supplier<Component> factory) {
        Tab tab = new Tab(new Span(label), createBadge(counter));
        tab.addThemeVariants(tabVariant);
        return withLazyTab(tab, factory);
    }

    // ---------- Theme variants ----------

    @Override
    public C withThemeVariants(TabsVariant... variants) {
        tabs.addThemeVariants(variants);
        return getConfigurator();
    }

    // ---------- Capabilities (Vaadin 24.9) ----------

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(tabs);
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(tabs);
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.of(tabs); // Tabs implements HasEnabled in 24.9
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }
}