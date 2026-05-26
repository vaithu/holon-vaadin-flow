package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.LazyTabsBuilder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class DefaultLazyTabsBuilder
        extends AbstractLazyTabsConfigurator<LazyTabsBuilder>
        implements LazyTabsBuilder {
    public DefaultLazyTabsBuilder(VerticalLayout component) {
        super(component);
    }

    @Override
    public VerticalLayout build() {
        VerticalLayout wrapper = getComponent();
        wrapper.setPadding(false);
        wrapper.setSpacing(false);
        wrapper.setSizeFull();
        wrapper.add(getTabs(), getContentContainer());
        // NOTE: SelectedChangeEvent listener is wired once in AbstractLazyTabsConfigurator constructor.
        // Calling switchToTab here loads the initially selected tab content.
        switchToTab(getTabs().getSelectedTab());
        return getComponent();
    }

    @Override
    protected LazyTabsBuilder getConfigurator() {
        return this;
    }
}
