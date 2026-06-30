package com.iyensoft.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;
import com.holonplatform.vaadin.flow.internal.components.builders.AbstractFooterConfigurator;
import com.iyensoft.vaadin.flow.components.Panel;
import com.iyensoft.vaadin.flow.components.builders.PanelConfigurator;

public class DefaultPanelFooterBuilder<D extends PanelConfigurator<D>>
        extends AbstractFooterConfigurator<PanelConfigurator.FooterBuilder<D>>
        implements PanelConfigurator.FooterBuilder<D> {

    private final Panel panel;
    private final D parent;

    public DefaultPanelFooterBuilder(Footer footer, Panel panel, D parent) {
        super(footer);
        this.panel = panel;
        this.parent = parent;
    }

    @Override
    protected PanelConfigurator.FooterBuilder<D> getConfigurator() {
        return this;
    }

    @Override
    public D add() {
        panel.setFooter(footer());
        return parent;
    }
}