package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.*;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultTabsConfigurator;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;

/**
 * Fluent configurator for a Vaadin {@link Tabs} <em>bar only</em>.
 * <p>
 * This configurator manages the {@link Tabs} header bar — tab items, orientation, selection state,
 * and theme variants. It does <strong>not</strong> manage tab content panels. Use
 * {@link com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder} when you need wired
 * tab-bar + content area with eager/lazy loading and optional caching.
 */
public interface TabsConfigurator<C extends TabsConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasThemeVariantConfigurator<TabsVariant, C>,
        HasSizeConfigurator<C>, HasEnabledConfigurator<C> {

    // ── Tab items ─────────────────────────────────────────────────────────────

    /** Adds the given {@link Tab} instances to the bar. */
    C withTab(Tab... tabs);

    /** Adds labeled tabs from plain string labels. */
    C withTab(String... labels);

    /** Inserts a tab as the first child. */
    C withTabAsFirst(Tab tab);

    /** Inserts a tab at the given zero-based index. */
    C withTabAtIndex(int index, Tab tab);

    /** Removes specific tabs from the bar. */
    C remove(Tab... tabs);

    /** Removes all tabs from the bar. */
    C removeAll();

    /** Replaces {@code oldTab} with {@code newTab} at the same position. */
    C replace(Tab oldTab, Tab newTab);

    // ── Behavior ──────────────────────────────────────────────────────────────

    /** Controls whether a tab is automatically selected on attach. */
    C autoselect(boolean autoselect);

    /** Sets the {@code flex-grow} CSS property on all enclosed tabs. */
    C flexGrowForEnclosedTabs(double flexGrow);

    /** Sets the orientation of the tab bar. */
    C orientation(Tabs.Orientation orientation);

    // ── Selection ─────────────────────────────────────────────────────────────

    /** Selects a tab by zero-based index. */
    C selectedIndex(int selectedIndex);

    /** Selects the given tab. */
    C selectedTab(Tab selectedTab);

    /** Registers a selection-change listener. */
    C withSelectedChangeListener(ComponentEventListener<Tabs.SelectedChangeEvent> listener);

    // ── Badge / icon decoration ───────────────────────────────────────────────

    /**
     * Adds a tab with a plain {@code label} and a numeric counter badge.
     * Equivalent to {@code add(new Tab(new Span(label), Badge.createBadge(counter)))}.
     */
    C withTab(String label, int counter);

    /**
     * Adds a tab with a leading {@code icon} and a plain string {@code label}.
     */
    C withTab(String label, Icon icon);

    // ── i18n labeled tabs ─────────────────────────────────────────────────────

    /**
     * Adds a tab whose label is resolved via the Holon {@link Localizable} API.
     * When {@link DeferrableLocalizationConfigurator#deferLocalization()} is active,
     * the resolved message is applied on the first UI attach cycle; otherwise it is
     * applied immediately. Falls back to {@link Localizable#getMessage()} when no
     * i18n provider is available.
     */
    C withTab(Localizable label);

    /** Adds an i18n-labeled tab with a leading {@link Icon}. */
    C withTab(Localizable label, Icon icon);

    /** Adds an i18n-labeled tab with a numeric counter badge. */
    C withTab(Localizable label, int counter);

    // ── configure factory ─────────────────────────────────────────────────────

    static BaseTabsConfigurator configure(Tabs tabs) {
        return new DefaultTabsConfigurator(tabs);
    }

    interface BaseTabsConfigurator extends TabsConfigurator<BaseTabsConfigurator>, DeferrableLocalizationConfigurator<BaseTabsConfigurator> {

    }
}
