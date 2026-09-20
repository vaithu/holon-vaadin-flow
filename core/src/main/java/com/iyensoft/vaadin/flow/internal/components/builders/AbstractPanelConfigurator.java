package com.iyensoft.vaadin.flow.internal.components.builders;

import com.iyensoft.vaadin.flow.internal.components.builders.DefaultMaterialHeaderBuilder;
import com.iyensoft.vaadin.flow.components.MaterialHeader;
import com.iyensoft.vaadin.flow.components.builders.PanelConfigurator;

import java.util.Optional;

import com.holonplatform.vaadin.flow.internal.components.builders.AbstractComponentConfigurator;
import com.iyensoft.vaadin.flow.components.Footer;
import com.iyensoft.vaadin.flow.components.Header;
import com.iyensoft.vaadin.flow.components.Panel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.shared.HasTooltip;

public abstract class AbstractPanelConfigurator<C extends PanelConfigurator<C>>

        extends AbstractComponentConfigurator<Panel, C>

        implements PanelConfigurator<C> {

    public AbstractPanelConfigurator(Panel component) {
        super(component);
    }

    @Override
    public PanelConfigurator.HeaderBuilder<C> header() {
        return new DefaultPanelHeaderBuilder<>(new Header(""), getComponent(), getConfigurator());
    }

    @Override
    public PanelConfigurator.FooterBuilder<C> footer() {
        return new DefaultPanelFooterBuilder<>(new Footer(), getComponent(), getConfigurator());
    }

    @Override
    public C content(Component... components) {
        getComponent().setContent(components);
        return getConfigurator();
    }

    @Override
    public C emptyState(String title) {
        getComponent().setEmptyState(title);
        return getConfigurator();
    }

    @Override
    public C card() {
        getComponent().addClassName("rdiv-card");
        return getConfigurator();
    }


    @Override
    public C footer(Component footer) {
        getComponent().setFooter(footer);
        return getConfigurator();
    }

    @Override
    public C header(Component header) {
       getComponent().setHeader(header);
        return getConfigurator();
    }

    @Override
    public C header(String title) {
        getComponent().setHeader(title);
        return getConfigurator();
    }

    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.ofNullable(getComponent());
    }

    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.ofNullable(getComponent());
    }

    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.ofNullable(getComponent());
    }

    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }


}
