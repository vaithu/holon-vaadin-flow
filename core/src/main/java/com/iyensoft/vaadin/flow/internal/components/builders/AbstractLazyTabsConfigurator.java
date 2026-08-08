package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractLocalizableComponentConfigurator;
import com.iyensoft.vaadin.flow.components.builders.LazyTabsConfigurator;
import com.iyensoft.vaadin.flow.components.DetailSyncAware;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Tabs configurator with optional lazy content and caching.
 * <p>
 * Extends {@link AbstractLocalizableComponentConfigurator} so that the
 * {@link DeferrableLocalizationConfigurator} contract is satisfied: when deferred localization
 * is enabled, {@link Localizable} tab labels are applied on the first UI attach cycle;
 * otherwise they are resolved immediately at configuration time.
 *
 * <h3>Session serialization</h3>
 * All mutable runtime state (supplier map, cache, selected tab, content container) is isolated
 * in a {@link TabController} that implements {@link Serializable}. The
 * {@code SelectedChangeListener} registered on the {@link Tabs} component captures only the
 * controller — never {@code this} (the builder) — so the builder itself is never reachable from
 * the Vaadin component tree and cannot block session serialization.
 */
public abstract class AbstractLazyTabsConfigurator<C extends LazyTabsConfigurator<C> & DeferrableLocalizationConfigurator<C>>
        extends AbstractLocalizableComponentConfigurator<Tabs, C>
        implements LazyTabsConfigurator<C> {

    /** All mutable runtime state lives here; captured by the Tabs listener instead of the builder. */
    private final TabController tabController = new TabController();

    public Tabs getTabs() {
        return getComponent();
    }

    @Override
    public Div getContentContainer() {
        return tabController.contentContainer;
    }

    public AbstractLazyTabsConfigurator(Tabs component) {
        super(component);
        getComponent().setWidthFull();
        // Initialize a default content container so switchToTab is always safe to call,
        // even when withContainer() is never invoked.
        tabController.contentContainer = new SyncableContentContainer();
        // Register the listener once here. withContainer() only swaps the container
        // reference; it must NOT add another listener.
        TabController tc = tabController;
        getComponent().addSelectedChangeListener(e -> tc.switchToTab(e.getSelectedTab()));
    }

    // ── Deferred-localization helper ──────────────────────────────────────────

    private Tab createTab(Localizable label) {
        if (isDeferredLocalizationEnabled()) {
            Tab tab = new Tab(label.getMessage());
            tab.addAttachListener(e -> {
                if (e.isInitialAttach()) {
                    LocalizationProvider.localize(label).ifPresent(tab::setLabel);
                }
            });
            return tab;
        } else {
            return new Tab(LocalizationProvider.localize(label).orElse(label.getMessage()));
        }
    }

    private Span createBadge(int value) {
        return UIUtils.Badge.createBadge(value);
    }

    @Override
    public HorizontalLayout buildHorizontal() {
        HorizontalLayout layout = Components.hl().add(getComponent(), tabController.contentContainer).build();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setSizeFull();
        return layout;
    }

    @Override
    public Tab getSelectedTab() {
        return getTabs().getSelectedTab();
    }

    // ── Configuration API ────────────────────────────────────────────────────

    @Override
    public C enableCache(boolean enableCache) {
        tabController.enableCaching = enableCache;
        return getConfigurator();
    }

    @Override
    public C cacheEnabled() {
        tabController.enableCaching = true;
        return getConfigurator();
    }

    @Override
    public C scrollIntoView() {
        getComponent().scrollIntoView();
        return getConfigurator();
    }

    @Override
    public C scrollIntoView(ScrollIntoViewOption... options) {
        getComponent().scrollIntoView(options);
        return getConfigurator();
    }

    @Override
    public C autoselect(boolean autoselect) {
        getComponent().setAutoselect(autoselect);
        return getConfigurator();
    }

    @Override
    public C flexGrowForEnclosedTabs(double flexGrow) {
        getComponent().setFlexGrowForEnclosedTabs(flexGrow);
        return getConfigurator();
    }

    @Override
    public C orientation(Tabs.Orientation orientation) {
        getComponent().setOrientation(orientation);
        return getConfigurator();
    }

    @Override
    public C selectedIndex(int selectedIndex) {
        getComponent().setSelectedIndex(selectedIndex);
        Tab selected = getComponent().getSelectedTab();
        if (selected != null) tabController.switchToTab(selected);
        return getConfigurator();
    }

    @Override
    public C selectedTab(Tab tab) {
        getTabs().setSelectedTab(tab);
        Tab selected = getComponent().getSelectedTab();
        if (selected != null) tabController.switchToTab(selected);
        return getConfigurator();
    }

    @Override
    public C selectedTab(String tabTitle) {
        Tab existing = getComponent().getChildren()
                .filter(c -> c instanceof Tab)
                .map(c -> (Tab) c)
                .filter(t -> Objects.equals(t.getLabel(), tabTitle))
                .findFirst()
                .orElse(null);
        if (existing != null) {
            return selectedTab(existing);
        } else {
            TabController.LOGGER.warn("No child Tab with label '{}'", tabTitle);
            return getConfigurator();
        }
    }

    @Override
    public C withSelectedChangeListener(ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
        getComponent().addSelectedChangeListener(listener);
        return getConfigurator();
    }

    // ── Eager tabs (String label) ─────────────────────────────────────────────

    @Override
    public C withEagerTab(String label, Component component) {
        return withEagerTab(new Tab(label), component);
    }

    @Override
    public C withEagerTab(Tab tab, Component component) {
        // Register supplier BEFORE adding to Tabs: Tabs auto-selects the first
        // added tab and fires SelectedChangeEvent immediately, so the supplier
        // must already be present in the map when switchToTab is invoked.
        tabController.getSupplierMap().put(tab, () -> component);
        getComponent().add(tab);
        return getConfigurator();
    }

    @Override
    public C withEagerTab(String label, Icon icon, Component component) {
        return withEagerTab(new Tab(icon, Components.span().text(label).build()), component);
    }

    @Override
    public C withEagerTab(String label, int counter, Component component) {
        return withEagerTab(new Tab(Components.span().text(label).build(), createBadge(counter)), component);
    }

    @Override
    public C withEagerTab(String label, int counter, TabVariant tabVariant, Component component) {
        Tab tab = new Tab(Components.span().text(label).build(), createBadge(counter));
        tab.addThemeVariants(tabVariant);
        return withEagerTab(tab, component);
    }

    @Override
    public C withEagerTab(String label, Icon icon, TabVariant tabVariant, Component component) {
        Tab tab = new Tab(icon, Components.span().text(label).build());
        tab.addThemeVariants(tabVariant);
        return withEagerTab(tab, component);
    }

    // ── Eager tabs (Localizable i18n label) ──────────────────────────────────

    @Override
    public C withEagerTab(Localizable label, Component component) {
        return withEagerTab(createTab(label), component);
    }

    @Override
    public C withEagerTab(Localizable label, Icon icon, Component component) {
        Tab tab = createTab(label);
        tab.addComponentAsFirst(icon);
        return withEagerTab(tab, component);
    }

    @Override
    public C withEagerTab(Localizable label, int counter, Component component) {
        Tab tab = createTab(label);
        tab.add(createBadge(counter));
        return withEagerTab(tab, component);
    }

    @Override
    public C withEagerTab(Localizable label, int counter, TabVariant tabVariant, Component component) {
        Tab tab = createTab(label);
        tab.add(createBadge(counter));
        tab.addThemeVariants(tabVariant);
        return withEagerTab(tab, component);
    }

    @Override
    public C withEagerTab(Localizable label, Icon icon, TabVariant tabVariant, Component component) {
        Tab tab = createTab(label);
        tab.addComponentAsFirst(icon);
        tab.addThemeVariants(tabVariant);
        return withEagerTab(tab, component);
    }

    // ── Lazy tabs (String label) ──────────────────────────────────────────────

    @Override
    public C withLazyTab(String label, Supplier<Component> factory) {
        return withLazyTab(new Tab(label), factory);
    }

    @Override
    public C withLazyTab(Tab tab, Supplier<Component> factory) {
        Objects.requireNonNull(factory, "factory must not be null");
        // Register supplier BEFORE adding to Tabs for the same reason as withEagerTab.
        tabController.getSupplierMap().put(tab, factory);
        getComponent().add(tab);
        return getConfigurator();
    }

    @Override
    public C withLazyTab(String label, Icon icon, Supplier<Component> factory) {
        return withLazyTab(new Tab(icon, Components.span().text(label).build()), factory);
    }

    @Override
    public C withLazyTab(String label, int counter, Supplier<Component> factory) {
        return withLazyTab(new Tab(Components.span().text(label).build(), createBadge(counter)), factory);
    }

    @Override
    public C withLazyTab(String label, Icon icon, TabVariant tabVariant, Supplier<Component> factory) {
        Tab tab = new Tab(icon, Components.span().text(label).build());
        tab.addThemeVariants(tabVariant);
        return withLazyTab(tab, factory);
    }

    @Override
    public C withLazyTab(String label, int counter, TabVariant tabVariant, Supplier<Component> factory) {
        Tab tab = new Tab(Components.span().text(label).build(), createBadge(counter));
        tab.addThemeVariants(tabVariant);
        return withLazyTab(tab, factory);
    }

    // ── Lazy tabs (Localizable i18n label) ───────────────────────────────────

    @Override
    public C withLazyTab(Localizable label, Supplier<Component> factory) {
        return withLazyTab(createTab(label), factory);
    }

    @Override
    public C withLazyTab(Localizable label, Icon icon, Supplier<Component> factory) {
        Tab tab = createTab(label);
        tab.addComponentAsFirst(icon);
        return withLazyTab(tab, factory);
    }

    @Override
    public C withLazyTab(Localizable label, int counter, Supplier<Component> factory) {
        Tab tab = createTab(label);
        tab.add(createBadge(counter));
        return withLazyTab(tab, factory);
    }

    @Override
    public C withLazyTab(Localizable label, int counter, TabVariant tabVariant, Supplier<Component> factory) {
        Tab tab = createTab(label);
        tab.add(createBadge(counter));
        tab.addThemeVariants(tabVariant);
        return withLazyTab(tab, factory);
    }

    @Override
    public C withLazyTab(Localizable label, Icon icon, TabVariant tabVariant, Supplier<Component> factory) {
        Tab tab = createTab(label);
        tab.addComponentAsFirst(icon);
        tab.addThemeVariants(tabVariant);
        return withLazyTab(tab, factory);
    }

    // ── Theme variants ────────────────────────────────────────────────────────

    @Override
    public C withThemeVariants(TabsVariant... variants) {
        getComponent().addThemeVariants(variants);
        return getConfigurator();
    }

    // ── Capabilities ──────────────────────────────────────────────────────────

    @Override
    protected Optional<HasSize> hasSize() { return Optional.of(getComponent()); }

    @Override
    protected Optional<HasStyle> hasStyle() { return Optional.of(getComponent()); }

    @Override
    protected Optional<HasEnabled> hasEnabled() { return Optional.of(getComponent()); }

    @Override
    public C withContainer(Div div) {
        // Use the passed div directly as the content container so that the caller's
        // DOM element receives tab content. Previously a new SyncableContentContainer
        // was created here and the passed div was silently ignored — content was written
        // into an off-DOM object. The listener is already registered in the constructor;
        // do NOT add it again here.
        if (div instanceof SyncableContentContainer existing) {
            tabController.contentContainer = existing;
        } else {
            tabController.contentContainer = div;
        }
        // Re-render the currently selected tab into the new container.
        // This is a no-op when withContainer() is called before any tabs are added
        // (selectedTab == null), and ensures correctness in the rare case it is called
        // after tabs have already been added and auto-selected.
        Tab current = getComponent().getSelectedTab();
        if (current != null) {
            tabController.switchToTab(current);
        }
        return getConfigurator();
    }

    // ── Serializable runtime controller ──────────────────────────────────────

    /**
     * Holds all mutable runtime state for the tab-switching logic and is the only object
     * captured by the {@link Tabs} {@code SelectedChangeListener}. Being {@link Serializable}
     * ensures Vaadin session serialization works without pulling the builder into the graph.
     */
    private static final class TabController implements Serializable {

        private static final Logger LOGGER = LoggerFactory.getLogger(TabController.class);
        private static final int MAX_CACHE = 5;

        /**
         * Factories for tab content. Transient: Supplier lambdas are not guaranteed to be
         * serializable. After session deserialization this map is null; switchToTab() handles
         * that gracefully by showing a placeholder.
         */
        transient Map<Tab, Supplier<Component>> tabSupplierMap;

        /** Returns the supplier map, initializing lazily if needed. */
        Map<Tab, Supplier<Component>> getSupplierMap() {
            if (tabSupplierMap == null) tabSupplierMap = new HashMap<>();
            return tabSupplierMap;
        }

        /**
         * LRU cache of realized components when caching is enabled.
         * Access-order iteration ensures the least recently viewed tab is evicted first.
         */
        final Map<Tab, Component> cachedComponents = new LinkedHashMap<>(MAX_CACHE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Tab, Component> eldest) {
                return size() > MAX_CACHE;
            }
        };

        Tab currentTab;
        boolean enableCaching = false;
        Div contentContainer;

        void switchToTab(Tab tab) {
            if (tab == null) {
                contentContainer.removeAll();
                currentTab = null;
                return;
            }

            if (!enableCaching && currentTab != null) {
                cachedComponents.remove(currentTab);
            }

            Component content = null;

            if (enableCaching) {
                content = cachedComponents.get(tab);
            }

            if (content == null) {
                Supplier<Component> supplier = tabSupplierMap != null ? tabSupplierMap.get(tab) : null;
                if (supplier == null) {
                    content = Components.div()
                            .add(Components.span().text(LocalizationProvider.localize(
                                    "No content registered for this tab.", "tabs.no_content")).build())
                            .build();
                    LOGGER.warn("No content supplier registered for tab: {}", safeLabel(tab));
                } else {
                    content = supplier.get();
                    if (enableCaching && content != null) {
                        cachedComponents.put(tab, content);
                    }
                }
            }

            if (content != null) {
                contentContainer.removeAll();
                contentContainer.add(content);
                if (contentContainer instanceof SyncableContentContainer scc
                        && scc.getLastItem() != null
                        && content instanceof DetailSyncAware<?> aware) {
                    //noinspection unchecked
                    ((DetailSyncAware<Object>) aware).onItemSelected(scc.getLastItem());
                }
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
    }

    // ── SaaS-scale DetailSyncAware relay ─────────────────────────────────────

    /**
     * Content container that implements {@link DetailSyncAware} to act as a relay
     * between the master-detail sync dispatcher and individual tab content components.
     * Buffers the last selected item so that lazily-built tab components receive the
     * correct item immediately upon construction.
     */
    private static class SyncableContentContainer extends Div implements DetailSyncAware<Object> {

        private Object lastItem;

        SyncableContentContainer() {
        }

        @Override
        public void onItemSelected(Object item) {
            this.lastItem = item;
            getChildren().findFirst().ifPresent(child -> {
                if (child instanceof DetailSyncAware<?> aware) {
                    //noinspection unchecked
                    ((DetailSyncAware<Object>) aware).onItemSelected(item);
                }
            });
        }

        Object getLastItem() {
            return lastItem;
        }
    }
}

