package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.FooterConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.Footer;
import com.holonplatform.vaadin.flow.vaadinplus.utilities.Color.Background;
import com.vaadin.flow.component.Component;

public abstract class AbstractFooterConfigurator<C extends FooterConfigurator<C>>
        extends AbstractLayoutConfigurator<C>
        implements FooterConfigurator<C> {

    public AbstractFooterConfigurator(Footer component) {
        super(component);
    }

    protected Footer footer() {
        return (Footer) super.getComponent();
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