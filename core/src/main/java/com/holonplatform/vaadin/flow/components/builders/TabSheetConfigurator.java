package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.core.Initializer;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.vaadin.flow.LazyComponent;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTabSheetConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;

public interface TabSheetConfigurator<C extends TabSheetConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasThemeVariantConfigurator<TabSheetVariant, C>,
        HasSizeConfigurator<C>, HasEnabledConfigurator<C>, DeferrableLocalizationConfigurator<C> {

    C withTab(Component tabContent,
              Component content);

    C withTab(Tab tab,
              Component content);

    C withTab(Tab tab,
              Initializer<Component> initializer);

    C withTab(Tab tab,
              LazyComponent component);

    C withTab(Tab tab,
              Component content,
              int position);

    C withTab(String tabText,
              Component content);

    C withTab(String tabText,
              Initializer<Component> initializer);

    C withTab(Icon icon, String tabText, Component component);

    C withTab(Icon icon, String tabText, Initializer<Component> initializer);

    C withTab(String tabText,
              LazyComponent component);

    C withTab(Icon icon, String tabText, LazyComponent component);

    // ── Localizable overloads ──────────────────────────────────────────────

    /**
     * Adds a tab with a localizable tab label.
     *
     * @param tabText localizable tab label (not null)
     * @param content tab content component (not null)
     * @return this configurator
     */
    C withTab(Localizable tabText, Component content);

    /**
     * Adds a lazy tab with a localizable tab label.
     *
     * @param tabText     localizable tab label (not null)
     * @param initializer content initializer (not null)
     * @return this configurator
     */
    C withTab(Localizable tabText, Initializer<Component> initializer);

    /**
     * Adds a tab with an icon and a localizable tab label.
     *
     * @param icon    tab icon (not null)
     * @param tabText localizable tab label (not null)
     * @param content tab content component (not null)
     * @return this configurator
     */
    C withTab(Icon icon, Localizable tabText, Component content);

    /**
     * Adds a lazy tab with an icon and a localizable tab label.
     *
     * @param icon        tab icon (not null)
     * @param tabText     localizable tab label (not null)
     * @param initializer content initializer (not null)
     * @return this configurator
     */
    C withTab(Icon icon, Localizable tabText, Initializer<Component> initializer);

    /**
     * Adds a lazy-component tab with a localizable tab label.
     *
     * @param tabText   localizable tab label (not null)
     * @param component lazy component (not null)
     * @return this configurator
     */
    C withTab(Localizable tabText, LazyComponent component);

    /**
     * Adds a lazy-component tab with an icon and a localizable tab label.
     *
     * @param icon      tab icon (not null)
     * @param tabText   localizable tab label (not null)
     * @param component lazy component (not null)
     * @return this configurator
     */
    C withTab(Icon icon, Localizable tabText, LazyComponent component);

    C bordered();

    C prefixComponent(Component component);

    C suffixComponent(Component component);


    C withSelectedChangeListener(ComponentEventListener<TabSheet.SelectedChangeEvent> listener);

    static BaseTabSheetConfigurator configure(TabSheet tabSheet) {
        return new DefaultTabSheetConfigurator(tabSheet);
    }

    interface BaseTabSheetConfigurator extends TabSheetConfigurator<BaseTabSheetConfigurator> {

    }


}
