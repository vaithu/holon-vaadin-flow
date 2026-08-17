package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultEntityCreationFormConfigurator;
import com.holonplatform.vaadin.flow.vaadinplus.components.EntityCreationForm;
import com.holonplatform.vaadin.flow.vaadinplus.components.FormStepCard;
import com.holonplatform.vaadin.flow.vaadinplus.components.StickyActionBar;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.ListItem;

/**
 * Configurator for {@link EntityCreationForm} components.
 *
 * @param <C> Concrete configurator type
 */
public interface EntityCreationFormConfigurator<C extends EntityCreationFormConfigurator<C>>
        extends ComponentConfigurator<C>, HasSizeConfigurator<C>, HasStyleConfigurator<C> {

    C breadcrumb(ListItem... items);
    C title(String title);
    C subtitle(String subtitle);
    C subtitle(Component component);
    C draftBadge(String badgeText);
    C headerAction(Component... components);
    C step(FormStepCard card);
    C steps(FormStepCard... cards);
    C status(String text, StickyActionBar.Variant variant);
    C progress(String label, int percent);
    C barAction(Component... components);

    static BaseEntityCreationFormConfigurator configure(EntityCreationForm form) {
        return new DefaultEntityCreationFormConfigurator(form);
    }

    interface BaseEntityCreationFormConfigurator
            extends EntityCreationFormConfigurator<BaseEntityCreationFormConfigurator> {}
}
