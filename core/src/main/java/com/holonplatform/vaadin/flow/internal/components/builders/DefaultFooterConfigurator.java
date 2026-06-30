package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.FooterConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;

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