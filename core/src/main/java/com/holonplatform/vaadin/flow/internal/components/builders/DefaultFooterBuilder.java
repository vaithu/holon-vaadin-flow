package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.FooterBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;

public class DefaultFooterBuilder extends AbstractFooterConfigurator<FooterBuilder> implements FooterBuilder {

    public DefaultFooterBuilder(Footer footer) {
        super(footer);
    }

    @Override
    protected FooterBuilder getConfigurator() {
        return this;
    }

    @Override
    public Footer build() {
        return footer();
    }
}