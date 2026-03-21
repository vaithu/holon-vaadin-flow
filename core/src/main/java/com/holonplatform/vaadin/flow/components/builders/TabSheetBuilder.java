package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTabSheetBuilder;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTabsBuilder;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.Tabs;

public interface TabSheetBuilder extends TabSheetConfigurator<TabSheetBuilder>,
        ComponentBuilder<TabSheet, TabSheetBuilder> {

    static TabSheetBuilder create() {
        return new DefaultTabSheetBuilder(new TabSheet());
    }

}
