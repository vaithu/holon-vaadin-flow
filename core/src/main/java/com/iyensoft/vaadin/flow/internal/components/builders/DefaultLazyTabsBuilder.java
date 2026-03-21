package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultLazyTabsBuilder
        extends AbstractLazyTabsConfigurator<LazyTabsBuilder>
        implements LazyTabsBuilder {
    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public DefaultLazyTabsBuilder(VerticalLayout component) {
        super(component);
    }

    /**
     * Build and returns the component.
     *
     * @return The component instance
     */
    @Override
    public VerticalLayout build() {
        VerticalLayout wrapper = getComponent();
        wrapper.setPadding(false);
        wrapper.setSpacing(false);
        wrapper.setSizeFull();
        wrapper.add(getTabs(), getContentContainer());

        getTabs().addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            if (selectedTab != null) {
                switchToTab(selectedTab);
            } else {
                getContentContainer().removeAll();
            }
        });

        switchToTab(getTabs().getSelectedTab());

        return
                getComponent();
    }

    /**
     * Get the actual configurator.
     *
     * @return the actual configurator
     */
    @Override
    protected LazyTabsBuilder getConfigurator() {
        return this;
    }
}
