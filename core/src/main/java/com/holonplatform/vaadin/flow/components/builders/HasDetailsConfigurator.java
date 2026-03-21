package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultDetailsConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;

public interface HasDetailsConfigurator<C extends HasDetailsConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasThemeVariantConfigurator<DetailsVariant, C>,
        HasSizeConfigurator<C>,HasEnabledConfigurator<C>,DeferrableLocalizationConfigurator<C>,HasComponentsConfigurator<C> {

    C opened(boolean opened);

    C summary(Component summary);

    C summaryText(String summary);


      static BaseDetailsConfigurator configure(Details details) {
        return new DefaultDetailsConfigurator(details);
    }

    interface BaseDetailsConfigurator extends HasDetailsConfigurator<BaseDetailsConfigurator> {

    }


}
