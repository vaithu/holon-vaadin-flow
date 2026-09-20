package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.components.builders.FooterConfigurator;
import com.iyensoft.vaadin.flow.components.Footer;
import com.iyensoft.vaadin.flow.utils.Color.Background;
import com.vaadin.flow.component.Component;

public abstract class AbstractFooterConfigurator<C extends FooterConfigurator<C>>
        extends AbstractLayoutConfigurator<Footer, C>
        implements FooterConfigurator<C> {

    public AbstractFooterConfigurator(Footer component) {
        super(component);
    }

    protected Footer footer() {
        return getComponent();
    }

    @Override
    public C prefix(Component... components) {
        footer().setPrefix(components);
        return getConfigurator();
    }

    @Override
    public C details(Component... components) {
        footer().setDetails(components);
        return getConfigurator();
    }

    @Override
    public C actions(Component... components) {
        footer().setActions(components);
        return getConfigurator();
    }

    @Override
    public C meta(Component... components) {
        footer().setMeta(components);
        return getConfigurator();
    }

    @Override
    public C legal(Component... components) {
        footer().setLegal(components);
        return getConfigurator();
    }

    @Override
    public C bordered(boolean bordered) {
        footer().setBordered(bordered);
        return getConfigurator();
    }

    @Override
    public C background(Background color) {
       footer().background(color);
        return getConfigurator();
    }




}
