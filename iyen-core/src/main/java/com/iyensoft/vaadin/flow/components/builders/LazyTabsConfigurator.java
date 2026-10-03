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
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.function.SerializableFunction;

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
    /**
     * Register eager tab contents whose child components should be added directly to the content
     * container, without attaching the supplied {@link Div} as an additional wrapper.
     *
     * @param label Tab label
     * @param contents Container whose children are rendered directly in the content container
     * @return this configurator
     */
    C withEagerTabContents(String label, Div contents);
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
    /**
     * Register lazy tab contents whose child components should be added directly to the content
     * container, without attaching the supplied {@link Div} as an additional wrapper.
     *
     * @param label Tab label
     * @param factory Factory for the container whose children are rendered directly
     * @return this configurator
     */
    C withLazyTabContents(String label, Supplier<Div> factory);
    C withLazyTabContents(String label, int counter, Supplier<Div> factory);
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

    // ── Selection-driven counters ────────────────────────────────────────────

    /**
     * Registers an eager tab whose badge is recomputed from the selected master item.
     *
     * <p>Counters are refreshed for every tab, visible or not, each time the master-detail
     * selection changes, so keep {@code counter} to a cheap count query. The badge stays hidden
     * until the first selection and whenever {@code counter} returns {@code null}. Requires the
     * default content container, i.e. do not replace it with {@link #withContainer(Div)}.</p>
     *
     * <pre>{@code
     * Components.lazyTabs()
     *     .withLazyTab("Orders", orderService::countByCustomer, OrdersTab::new)
     * }</pre>
     *
     * @param <T> selected item type
     * @param label tab label
     * @param counter maps the selected item to the badge value (not null)
     * @param component tab content
     * @return this configurator
     */
    <T> C withEagerTab(String label, SerializableFunction<T, Integer> counter, Component component);

    /**
     * Registers a lazy tab whose badge is recomputed from the selected master item.
     *
     * @see #withEagerTab(String, SerializableFunction, Component)
     */
    <T> C withLazyTab(String label, SerializableFunction<T, Integer> counter, Supplier<Component> factory);

    /**
     * Registers a localized lazy tab whose badge is recomputed from the selected master item.
     *
     * @see #withEagerTab(String, SerializableFunction, Component)
     */
    <T> C withLazyTab(Localizable label, SerializableFunction<T, Integer> counter, Supplier<Component> factory);

    /**
     * Registers flattened lazy tab contents whose badge is recomputed from the selected master item.
     *
     * @see #withLazyTabContents(String, int, Supplier)
     * @see #withEagerTab(String, SerializableFunction, Component)
     */
    <T> C withLazyTabContents(String label, SerializableFunction<T, Integer> counter, Supplier<Div> factory);

    C withContainer(Div div);

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
     * Attaches the configurator to an existing {@link Tabs} shell.
     * The passed layout is used only as the outer shell when wiring tabs and content
     * manually (e.g., vertical orientation side-by-side). The content area is an
     * internally managed {@link Div}; retrieve it via {@link #getContentContainer()}.
     * Use {@link LazyTabsBuilder#create()} when you want the builder to manage the full tabs.
     */
    static BaseTabsConfigurator configure(Tabs tabs) {
        return new DefaultLazyTabsConfigurator(tabs);
    }

    interface BaseTabsConfigurator extends LazyTabsConfigurator<BaseTabsConfigurator>, DeferrableLocalizationConfigurator<BaseTabsConfigurator> {

    }
}
