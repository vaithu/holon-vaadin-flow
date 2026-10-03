package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.Panel;
import com.iyensoft.vaadin.flow.components.builders.DetailPanelBuilder;

/**
 * Default {@link DetailPanelBuilder} implementation.
 */
public class DefaultDetailPanelBuilder
        extends AbstractDetailPanelConfigurator<DetailPanelBuilder>
        implements DetailPanelBuilder {

    public DefaultDetailPanelBuilder(String title) {
        super(new Panel(), title);
    }

    @Override
    public Panel build() {
        applyPostProcessors();
        return getComponent();
    }

    @Override
    protected DetailPanelBuilder getConfigurator() {
        return this;
    }
}
