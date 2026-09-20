package com.iyensoft.vaadin.flow.components.builders;

import com.iyensoft.vaadin.flow.internal.components.builders.DefaultFooterConfigurator;
import com.iyensoft.vaadin.flow.components.Footer;
import com.iyensoft.vaadin.flow.utils.Color;
import com.vaadin.flow.component.Component;

public interface FooterConfigurator<C extends FooterConfigurator<C>> extends LayoutConfigurator<C> {

    C prefix(Component... components);

    C details(Component... components);

    C actions(Component... components);

    C meta(Component... components);

    C legal(Component... components);

    default C withoutBorder() {
        return bordered(false);
    }

    C bordered(boolean bordered);

    C background(Color.Background color);

    static FooterConfigurator.BaseFooterConfigurator configure(Footer footer) {
        return new DefaultFooterConfigurator(footer);
    }

    interface BaseFooterConfigurator extends FooterConfigurator<FooterConfigurator.BaseFooterConfigurator> {

    }
}