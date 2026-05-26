package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.Components;
import com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractLocalizableComponentConfigurator;
import com.iyensoft.vaadin.flow.components.builders.LazyTabsConfigurator;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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
 */
public abstract class AbstractLazyTabsConfigurator<C extends LazyTabsConfigurator<C> & DeferrableLocalizationConfigurator<C>>
        extends AbstractLocalizableComponentConfigurator<VerticalLayout, C>
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

    private final Tabs tabs = new Tabs();

    // Display area
    private final Div contentContainer = Components.div().build();

    public Tabs getTabs() {
        return tabs;
    }

    public Div getContentContainer() {
        return contentContainer;
    }

    public AbstractLazyTabsConfigurator(VerticalLayout component) {
        super(component);
        contentContainer.addClassName("lazy-tabs-content");
        contentContainer.setSizeFull();
        contentContainer.addAttachListener(e -> e.getUI().getPage().addStyleSheet("context://lazy-tabs.css"));
        tabs.setWidthFull();

        // Keep the content area in sync with the selected tab
        tabs.addSelectedChangeListener(e -> switchToTab(e.getSelectedTab()));
    }

    // ── Deferred-localization helper ──────────────────────────────────────────

    /**
     * Creates a {@link Tab} whose label text respects the current deferred-localization flag.
     * <ul>
     *   <li>Deferred OFF (default): label resolved immediately, fallback to message().</li>
     *   <li>Deferred ON: fallback message shown immediately; proper translation applied on
     *       the first UI attach cycle via an {@code AttachListener} on the {@link Tab}.</li>
     * </ul>
     */
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

    /**
     * Convenience wrapper: Tabs on top + content below.
     */
    public Component buildTabsWithContent() {
        VerticalLayout wrapper = Components.vl().build();
        wrapper.setPadding(false);
        wrapper.setSpacing(false);
        wrapper.setSizeFull();
        wrapper.add(tabs, contentContainer);
        return wrapper;
    }

    /**
     * Convenience wrapper: Tabs on the left + content on the right.
     * Use this for vertical-orientation side-by-side layouts instead of
     * assembling {@code getTabs()} + {@code getContentContainer()} manually.
     */
    @Override
    public HorizontalLayout buildHorizontal() {
        HorizontalLayout layout = Components.hl().add(tabs, contentContainer).build();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setSizeFull();
        return layout;
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
                content = Components.div().add(Components.span().text("No content registered for this tab.").build()).build();
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

    // ── Configuration API ────────────────────────────────────────────────────

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
    public C scrollIntoView(ScrollIntoViewOption... options) {
        tabs.scrollIntoView(options);
        return getConfigurator();
    }

    @Override
    public C autoselect(boolean autoselect) {
        tabs.setAutoselect(autoselect);
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
        if (selected != null) switchToTab(selected);
        return getConfigurator();
    }

    @Override
    public C selectedTab(Tab tab) {
        getTabs().setSelectedTab(tab);
        Tab selected = tabs.getSelectedTab();
        if (selected != null) switchToTab(selected);
        return getConfigurator();
    }

    @Override
    public C selectedTab(String tabTitle) {
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
    public C withSelectedChangeListener(ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
        tabs.addSelectedChangeListener(listener);
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
        tabSupplierMap.put(tab, () -> component);
        tabs.add(tab);
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
        tabSupplierMap.put(tab, factory);
        tabs.add(tab);
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
        tabs.addThemeVariants(variants);
        return getConfigurator();
    }

    // ── Capabilities ──────────────────────────────────────────────────────────

    @Override
    protected Optional<HasSize> hasSize() { return Optional.of(tabs); }

    @Override
    protected Optional<HasStyle> hasStyle() { return Optional.of(tabs); }

    @Override
    protected Optional<HasEnabled> hasEnabled() { return Optional.of(tabs); }
}

