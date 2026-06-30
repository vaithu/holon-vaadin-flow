package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFooterBuilder;
import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;

public interface FooterBuilder extends FooterConfigurator<FooterBuilder>, ComponentBuilder<Footer, FooterBuilder> {

    static FooterBuilder create() {
        return new DefaultFooterBuilder(new Footer());
    }

    static FooterBuilder create(Footer footer) {
        return new DefaultFooterBuilder(footer);
    }
}