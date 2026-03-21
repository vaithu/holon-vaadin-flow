package com.holonplatform.vaadin.flow.internal.components.builders;

import com.holonplatform.vaadin.flow.components.builders.AccordionConfigurator;
import com.holonplatform.vaadin.flow.components.builders.DeferrableLocalizationConfigurator;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.shared.HasTooltip;

import java.util.Optional;

public abstract class AbstractAccordionConfigurator<C extends AccordionConfigurator<C> & DeferrableLocalizationConfigurator<C>>
        extends AbstractLocalizableComponentConfigurator<Accordion, C> implements AccordionConfigurator<C> {


    /**
     * Constructor.
     *
     * @param component The component instance (not null)
     */
    public AbstractAccordionConfigurator(Accordion component) {
        super(component);
    }

    /**
     * If the component supports {@link HasSize}, return the component as {@link HasSize}.
     *
     * @return Optional component as {@link HasSize}, if supported
     */
    @Override
    protected Optional<HasSize> hasSize() {
        return Optional.of(getComponent());
    }

    /**
     * If the component supports {@link HasStyle}, return the component as {@link HasStyle}.
     *
     * @return Optional component as {@link HasStyle}, if supported
     */
    @Override
    protected Optional<HasStyle> hasStyle() {
        return Optional.of(getComponent());
    }

    /**
     * If the component supports {@link HasEnabled}, return the component as {@link HasEnabled}.
     *
     * @return Optional component as {@link HasEnabled}, if supported
     */
    @Override
    protected Optional<HasEnabled> hasEnabled() {
        return Optional.empty();
    }

    /**
     * If the component supports {@link HasTooltip}, return the component as {@link HasTooltip}.
     *
     * @return Optional component as {@link HasTooltip}, if supported
     */
    @Override
    protected Optional<HasTooltip> hasTooltip() {
        return Optional.empty();
    }

    @Override
    public C withAccordionPanel(AccordionPanel panel) {
        getComponent().add(panel);
        return getConfigurator();
    }

    @Override
    public C withAccordion(String summary, Component content) {
        getComponent().add(summary, content);
        return getConfigurator();
    }


    @Override
    public C withOpenedChangeListener(ComponentEventListener<Accordion.OpenedChangeEvent> listener) {
        getComponent().addOpenedChangeListener(listener);
        return getConfigurator();
    }

    @Override
    public AccordionPanelBuilder<C> withPanel() {

        return new DefaultAccordionPanelBuilder<>(getConfigurator(),new AccordionPanel());
    }

    @Override
    public AccordionPanelBuilder<C> withPanel(Component summary) {
        return new DefaultAccordionPanelBuilder<>(getConfigurator(),new AccordionPanel(summary));
    }

    @Override
    public AccordionPanelBuilder<C> withPanel(String summary) {
        return new DefaultAccordionPanelBuilder<>(getConfigurator(),new AccordionPanel(summary));
    }

    @Override
    public AccordionPanelBuilder<C> withPanel(String summary, Component... components) {
        return new DefaultAccordionPanelBuilder<>(getConfigurator(),new AccordionPanel(summary,components));
    }

    @Override
    public AccordionPanelBuilder<C> withPanel(Component summary, Component... components) {
        return new DefaultAccordionPanelBuilder<>(getConfigurator(),new AccordionPanel(summary,components));
    }

    @Override
    public AccordionPanelBuilder<C> withPanel(Component summary, Component content) {
        return new DefaultAccordionPanelBuilder<>(getConfigurator(),new AccordionPanel(summary,content));
    }

}
