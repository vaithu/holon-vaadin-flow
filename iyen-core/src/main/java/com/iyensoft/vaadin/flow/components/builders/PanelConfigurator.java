package com.iyensoft.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.builders.*;
import com.iyensoft.vaadin.flow.components.Panel;
import com.iyensoft.vaadin.flow.internal.components.builders.DefaultPanelConfigurator;
import com.vaadin.flow.component.Component;

public interface PanelConfigurator<C extends PanelConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    HeaderBuilder<C> header();

    FooterBuilder<C> footer();

    C content(Component... components);
    C emptyState(String title);

    C header(Component header);

    C header(String title);

    C footer(Component footer);

    C card();

    static BasePanelConfigurator configure(Panel panel) {
        return new DefaultPanelConfigurator(panel);
    }

    interface BasePanelConfigurator extends PanelConfigurator<BasePanelConfigurator> {

    }

    interface HeaderBuilder<B extends PanelConfigurator<B>> extends HeaderConfigurator<HeaderBuilder<B>> {
        B add();
    }
    interface FooterBuilder<D extends PanelConfigurator<D>> extends FooterConfigurator<FooterBuilder<D>> {
        D add();
    }
}
