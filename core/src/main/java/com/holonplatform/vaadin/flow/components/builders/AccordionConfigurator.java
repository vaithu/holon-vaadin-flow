package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultAccordionConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.DetailsVariant;

public interface AccordionConfigurator<C extends AccordionConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>,
        HasSizeConfigurator<C> {

    C withAccordionPanel(AccordionPanel panel);

    C withAccordion(String summary,
                    Component content);

    AccordionPanelBuilder<C> withPanel();

    AccordionPanelBuilder<C> withPanel(Component summary);
    AccordionPanelBuilder<C> withPanel(String summary);
    AccordionPanelBuilder<C> withPanel(String summary,
                                       Component... components);
    AccordionPanelBuilder<C> withPanel(Component summary,
                                       Component... components);
    AccordionPanelBuilder<C> withPanel(Component summary,
                                       Component content);

    default C withAccordionPanel(String summary,
                                 Component content) {
        return withAccordionPanel(new AccordionPanel(summary, content));
    }

    C withOpenedChangeListener(ComponentEventListener<Accordion.OpenedChangeEvent> listener);


    static BaseAccordionConfigurator configure(Accordion accordion) {

        return new DefaultAccordionConfigurator(accordion);
    }

    interface BaseAccordionConfigurator extends AccordionConfigurator<BaseAccordionConfigurator>, DeferrableLocalizationConfigurator<BaseAccordionConfigurator> {

    }

    interface AccordionPanelBuilder<B extends AccordionConfigurator<B>> extends HasThemeVariantConfigurator<DetailsVariant, AccordionPanelBuilder<B>>
            , HasTooltipConfigurator<AccordionPanelBuilder<B>>, HasComponentsConfigurator<AccordionPanelBuilder<B>>,HasStyleConfigurator<AccordionPanelBuilder<B>> {
        B add();

    }


}
