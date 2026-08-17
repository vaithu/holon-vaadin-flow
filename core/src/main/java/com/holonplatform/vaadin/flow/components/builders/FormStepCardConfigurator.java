package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFormStepCardConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.FormStepCard;
import com.vaadin.flow.component.Component;

/**
 * Configurator for {@link FormStepCard} components.
 *
 * @param <C> Concrete configurator type
 */
public interface FormStepCardConfigurator<C extends FormStepCardConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    C stepNumber(int stepNumber);
    C totalSteps(int totalSteps);
    C title(String title);
    C subtitle(String subtitle);
    C helpText(String helpText);
    C state(FormStepCard.StepState state);
    C content(Component... components);

    static BaseFormStepCardConfigurator configure(FormStepCard card) {
        return new DefaultFormStepCardConfigurator(card);
    }

    interface BaseFormStepCardConfigurator extends FormStepCardConfigurator<BaseFormStepCardConfigurator> {}
}
