package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.flow.components.builders.AccordionConfigurator;
import com.holonplatform.vaadin.flow.i18n.LocalizationProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.dom.DomEventListener;

public abstract class AbstractAccordionPanelConfigurator<B extends AccordionConfigurator<B>>

        implements AccordionConfigurator.AccordionPanelBuilder<B> {

    private final B parentBuilder;
    private final AccordionPanel accordionPanel;

    public AbstractAccordionPanelConfigurator(B parentBuilder, AccordionPanel accordionPanel) {
        super();
        ObjectUtils.argumentNotNull(parentBuilder, "Parent builder must be not null");
        ObjectUtils.argumentNotNull(accordionPanel, "AccordionPanel item must be not null");
        this.accordionPanel = accordionPanel;
        this.parentBuilder = parentBuilder;
    }

    @Override
    public B add() {
        parentBuilder.withAccordionPanel(accordionPanel);
        return parentBuilder;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> add(Component... components) {
        accordionPanel.add(components);
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> addComponentAsFirst(Component component) {
        accordionPanel.addComponentAsFirst(component);
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> addComponentAtIndex(int index, Component component) {
        accordionPanel.addComponentAtIndex(index, component);
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> add(String text) {
        accordionPanel.add(text);
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> withThemeVariants(DetailsVariant... variants) {
        accordionPanel.addThemeVariants(variants);
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> tooltip(Localizable tooltip) {
        if (tooltip == null) {
            accordionPanel.setTooltipText(null);
        } else {
            LocalizationProvider.localize(tooltip).ifPresent(accordionPanel::setTooltipText);
        }
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> tooltipText(String text) {
        accordionPanel.setTooltipText(text);
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> withThemeName(String themeName) {
        accordionPanel.addThemeName(themeName);
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> withEventListener(String eventType, DomEventListener listener) {
        accordionPanel.getElement().addEventListener(eventType, listener);
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> withEventListener(String eventType, DomEventListener listener, String filter) {
        accordionPanel.getElement().addEventListener(eventType, listener).setFilter(filter);
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> enabled(boolean enabled) {
        accordionPanel.setEnabled(enabled);
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> styleName(String styleName) {
        accordionPanel.addClassName(styleName);
        return this;
    }

    @Override
    public AccordionConfigurator.AccordionPanelBuilder<B> styleNames(String... styleNames) {
        accordionPanel.addClassNames(styleNames);
        return this;
    }
}
