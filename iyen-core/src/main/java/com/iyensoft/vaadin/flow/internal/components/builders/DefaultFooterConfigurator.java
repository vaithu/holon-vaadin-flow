package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.FooterConfigurator;
import com.iyensoft.vaadin.flow.components.Footer;

public class DefaultFooterConfigurator extends AbstractFooterConfigurator<FooterConfigurator.BaseFooterConfigurator>
        implements FooterConfigurator.BaseFooterConfigurator {

    public DefaultFooterConfigurator(Footer component) {
        super(component);
    }

    @Override
    protected BaseFooterConfigurator getConfigurator() {
        return this;
    }
}