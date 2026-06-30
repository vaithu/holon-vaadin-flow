package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.vaadinplus.components.Header;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractHeaderConfigurator;
import com.iyensoft.vaadin.flow.components.Panel;
import com.iyensoft.vaadin.flow.components.builders.PanelConfigurator;

public class DefaultPanelHeaderBuilder<B extends PanelConfigurator<B>>
        extends AbstractHeaderConfigurator<PanelConfigurator.HeaderBuilder<B>>
        implements PanelConfigurator.HeaderBuilder<B> {

    private final Panel panel;
    private final B parent;

    public DefaultPanelHeaderBuilder(Header header, Panel panel, B parent) {
        super(header);
        this.panel = panel;
        this.parent = parent;
    }

    @Override
    protected PanelConfigurator.HeaderBuilder<B> getConfigurator() {
        return this;
    }

    @Override
    public B add() {
        panel.setHeader(getComponent());
        return parent;
    }
}