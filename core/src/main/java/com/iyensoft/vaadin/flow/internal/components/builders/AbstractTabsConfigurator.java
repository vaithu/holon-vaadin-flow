package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractLocalizableComponentConfigurator;
import com.iyensoft.vaadin.flow.components.builders.TabsConfigurator;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;

import java.util.Optional;

/**
 * Base implementation for the {@link TabsConfigurator} — manages a Vaadin {@link Tabs} bar only.
 * Content panels are NOT managed here; use
 * {@link com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder} for that purpose.
 * <p>
 * Extends {@link AbstractLocalizableComponentConfigurator} to provide
 * {@link DeferrableLocalizationConfigurator} support: when deferred localization is enabled,
 * {@link Localizable} tab labels are resolved on the first UI attach cycle instead of immediately
 * at build time.
 */
public abstract class AbstractTabsConfigurator<C extends TabsConfigurator<C> & DeferrableLocalizationConfigurator<C>>
        extends AbstractLocalizableComponentConfigurator<Tabs, C>
        implements TabsConfigurator<C> {

    public AbstractTabsConfigurator(Tabs component) {
        super(component);
    }

    // ── Deferred-localization helper ──────────────────────────────────────────

    /**
     * Creates a {@link Tab} whose label text respects the current deferred-localization flag.
     * <ul>
     *   <li>If deferred mode is <em>off</em> (default): label is resolved immediately via
     *       {@link LocalizationProvider}, falling back to {@link Localizable#getMessage()}.</li>
     *   <li>If deferred mode is <em>on</em>: the fallback message is set now, and an attach
     *       listener resolves the proper translation on the first UI attach cycle.</li>
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

    @Override
    protected Optional<HasSize> hasSize() { return Optional.of(getComponent()); }

    @Override
    protected Optional<HasStyle> hasStyle() { return Optional.of(getComponent()); }

    @Override
    protected Optional<HasEnabled> hasEnabled() { return Optional.of(getComponent()); }

    // ── Tab items ─────────────────────────────────────────────────────────────

    @Override
    public C withTab(Tab... tabsToAdd) {
        getComponent().add(tabsToAdd);
        return getConfigurator();
    }

    @Override
    public C withTab(String... labels) {
        for (String label : labels) {
            getComponent().add(new Tab(label));
        }
        return getConfigurator();
    }

    @Override
    public C withTab(Localizable label) {
        getComponent().add(createTab(label));
        return getConfigurator();
    }

    @Override
    public C withTab(String label, int counter) {
        getComponent().add(new Tab(new com.vaadin.flow.component.html.Span(label), UIUtils.Badge.createBadge(counter)));
        return getConfigurator();
    }

    @Override
    public C withTab(String label, Icon icon) {
        getComponent().add(new Tab(icon, new com.vaadin.flow.component.html.Span(label)));
        return getConfigurator();
    }

    @Override
    public C withTab(Localizable label, Icon icon) {
        Tab tab = createTab(label);
        tab.addComponentAsFirst(icon);
        getComponent().add(tab);
        return getConfigurator();
    }

    @Override
    public C withTab(Localizable label, int counter) {
        Tab tab = createTab(label);
        tab.add(UIUtils.Badge.createBadge(counter));
        getComponent().add(tab);
        return getConfigurator();
    }

    @Override
    public C withTabAsFirst(Tab tab) {
        getComponent().addTabAsFirst(tab);
        return getConfigurator();
    }

    @Override
    public C withTabAtIndex(int index, Tab tab) {
        getComponent().addTabAtIndex(index, tab);
        return getConfigurator();
    }

    @Override
    public C remove(Tab... tabsToRemove) {
        getComponent().remove(tabsToRemove);
        return getConfigurator();
    }

    @Override
    public C removeAll() {
        getComponent().removeAll();
        return getConfigurator();
    }

    @Override
    public C replace(Tab oldTab, Tab newTab) {
        getComponent().replace(oldTab, newTab);
        return getConfigurator();
    }

    // ── Behavior ──────────────────────────────────────────────────────────────

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

    // ── Selection ─────────────────────────────────────────────────────────────

    @Override
    public C selectedIndex(int selectedIndex) {
        getComponent().setSelectedIndex(selectedIndex);
        return getConfigurator();
    }

    @Override
    public C selectedTab(Tab selectedTab) {
        getComponent().setSelectedTab(selectedTab);
        return getConfigurator();
    }

    @Override
    public C withSelectedChangeListener(ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
        getComponent().addSelectedChangeListener(listener);
        return getConfigurator();
    }

    // ── Theme variants ────────────────────────────────────────────────────────

    @Override
    public C withThemeVariants(TabsVariant... variants) {
        getComponent().addThemeVariants(variants);
        return getConfigurator();
    }
}
