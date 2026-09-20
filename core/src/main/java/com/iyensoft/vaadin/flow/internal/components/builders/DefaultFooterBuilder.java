package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.FooterBuilder;
import com.iyensoft.vaadin.flow.components.Footer;

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
        applyPostProcessors();
        return footer();
    }
}
