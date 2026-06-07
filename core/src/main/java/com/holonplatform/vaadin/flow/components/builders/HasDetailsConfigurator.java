package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.internal.components.builders.DefaultDetailsConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.signals.Signal;

public interface HasDetailsConfigurator<C extends HasDetailsConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasThemeVariantConfigurator<DetailsVariant, C>,
        HasSizeConfigurator<C>, DeferrableLocalizationConfigurator<C>, HasComponentsConfigurator<C> {

    C opened(boolean opened);

  /**
   * Bind the details opened state to given {@link Signal}.
   * @param openedSignal Opened-state signal (not null)
   * @return this
   * @since 5.5.8
   */
  @SuppressWarnings("unchecked")
  default C bindOpened(Signal<? extends Boolean> openedSignal) {
    SignalBindings.bind(this, openedSignal, value -> opened(value != null && value));
    return (C) this;
  }

    C summary(Component summary);

    C summaryText(String summary);


      static BaseDetailsConfigurator configure(Details details) {
        return new DefaultDetailsConfigurator(details);
    }

    interface BaseDetailsConfigurator extends HasDetailsConfigurator<BaseDetailsConfigurator> {

    }


}
