package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.components.builders.*;
import com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultLazyTabsConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ScrollIntoViewOption;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;

import java.util.function.Supplier;


public interface LazyTabsConfigurator<C extends LazyTabsConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasThemeVariantConfigurator<TabsVariant, C>,
        HasSizeConfigurator<C>, HasEnabledConfigurator<C> {

    C enableCache(boolean enableCache);

    C cacheEnabled();

    C scrollIntoView();
    C scrollIntoView(ScrollIntoViewOption... options);


    C autoselect(boolean autoselect);

    C flexGrowForEnclosedTabs(double flexGrow);

    C orientation(Tabs.Orientation orientation);

    C selectedIndex(int selectedIndex);

    C selectedTab(Tab tab);
    C selectedTab(String tabTitle);

    C withSelectedChangeListener(ComponentEventListener<Tabs.SelectedChangeEvent> listener);

    // ── Eager tabs (String label) ─────────────────────────────────────────────

    C withEagerTab(String label, Component component);
    C withEagerTab(String label, Icon icon, Component component);
    C withEagerTab(String label, int counter, Component component);
    C withEagerTab(String label, int counter, TabVariant tabVariant, Component component);
    C withEagerTab(String label, Icon icon, TabVariant tabVariant, Component component);
    C withEagerTab(Tab tab, Component component);

    // ── Eager tabs (Localizable i18n label) ──────────────────────────────────

    C withEagerTab(Localizable label, Component component);
    C withEagerTab(Localizable label, Icon icon, Component component);
    C withEagerTab(Localizable label, int counter, Component component);
    C withEagerTab(Localizable label, int counter, TabVariant tabVariant, Component component);
    C withEagerTab(Localizable label, Icon icon, TabVariant tabVariant, Component component);

    // ── Lazy tabs (String label) ──────────────────────────────────────────────

    C withLazyTab(String label, Supplier<Component> factory);
    C withLazyTab(String label, Icon icon, Supplier<Component> factory);
    C withLazyTab(String label, int counter, Supplier<Component> factory);
    C withLazyTab(String label, Icon icon, TabVariant tabVariant, Supplier<Component> factory);
    C withLazyTab(String label, int counter, TabVariant tabVariant, Supplier<Component> factory);
    C withLazyTab(Tab tab, Supplier<Component> factory);

    // ── Lazy tabs (Localizable i18n label) ───────────────────────────────────

    C withLazyTab(Localizable label, Supplier<Component> factory);
    C withLazyTab(Localizable label, Icon icon, Supplier<Component> factory);
    C withLazyTab(Localizable label, int counter, Supplier<Component> factory);
    C withLazyTab(Localizable label, int counter, TabVariant tabVariant, Supplier<Component> factory);
    C withLazyTab(Localizable label, Icon icon, TabVariant tabVariant, Supplier<Component> factory);

    /** @deprecated Use {@link #buildHorizontal()} for side-by-side layouts instead of accessing the content container directly. */
    @Deprecated(since = "next")
    Div getContentContainer();

    Tab getSelectedTab();
    Tabs getTabs();

    /**
     * Assembles a side-by-side {@link HorizontalLayout} with the tab bar on the left
     * and the content area on the right. Equivalent to the manual
     * {@code new HorizontalLayout(getTabs(), getContentContainer())} pattern.
     *
     * <pre>{@code
     * var sidebar = LazyTabsConfigurator.configure(new VerticalLayout())
     *     .orientation(Tabs.Orientation.VERTICAL)
     *     .withLazyTab("Profile",  () -> profilePanel())
     *     .withLazyTab("Settings", () -> settingsPanel())
     *     .selectedIndex(0)
     *     .buildHorizontal();   // ← tabs left, content right
     * }</pre>
     */
    HorizontalLayout buildHorizontal();

    /**
     * Attaches the configurator to an existing {@link VerticalLayout} shell.
     * The passed layout is used only as the outer shell when wiring tabs and content
     * manually (e.g., vertical orientation side-by-side). The content area is an
     * internally managed {@link Div}; retrieve it via {@link #getContentContainer()}.
     * Use {@link LazyTabsBuilder#create()} when you want the builder to manage the full layout.
     */
    static BaseTabsConfigurator configure(VerticalLayout layout) {
        return new DefaultLazyTabsConfigurator(layout);
    }

    interface BaseTabsConfigurator extends LazyTabsConfigurator<BaseTabsConfigurator>, DeferrableLocalizationConfigurator<BaseTabsConfigurator> {

    }
}
