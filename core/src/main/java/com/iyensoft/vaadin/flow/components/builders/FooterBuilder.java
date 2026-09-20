package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.ComponentBuilder;

import com.iyensoft.vaadin.flow.internal.components.builders.DefaultFooterBuilder;
import com.iyensoft.vaadin.flow.components.Footer;

public interface FooterBuilder extends FooterConfigurator<FooterBuilder>, ComponentBuilder<Footer, FooterBuilder> {

    static FooterBuilder create() {
        return new DefaultFooterBuilder(new Footer());
    }

    static FooterBuilder create(Footer footer) {
        return new DefaultFooterBuilder(footer);
    }
}