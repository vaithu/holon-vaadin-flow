package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.Badge;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultTabConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.signals.Signal;

public interface TabConfigurator<C extends TabConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasThemeVariantConfigurator<TabVariant, C>,
        HasSizeConfigurator<C>, HasEnabledConfigurator<C>,HasTooltipConfigurator<C>,DeferrableLocalizationConfigurator<C> {

    C flexGrow(double flexGrow);

    C label(String label);

  /**
   * Bind the tab label to given {@link Signal}.
   * @param labelSignal Label signal (not null)
   * @return this
   * @since 5.5.8
   */
  @SuppressWarnings("unchecked")
  default C bindLabel(Signal<String> labelSignal) {
    SignalBindings.bind(this, labelSignal, this::label);
    return (C) this;
  }

    C selected(boolean selected);

  /**
   * Bind the tab selected state to given {@link Signal}.
   * @param selectedSignal Selected-state signal (not null)
   * @return this
   * @since 5.5.8
   */
  @SuppressWarnings("unchecked")
  default C bindSelected(Signal<? extends Boolean> selectedSignal) {
    SignalBindings.bind(this, selectedSignal, value -> selected(value != null && value));
    return (C) this;
  }

    C icon(VaadinIcon icon);

    C icon(Icon icon);

    C span(String label);

    C badge(Badge badge);

    C badge(int value);

    C iconOnTop();

    default C add(String label, VaadinIcon icon) {
        icon(icon.create());
        return span(label);
    }

    default C add(String label, Icon icon) {
        icon(icon);
        return span(label);
    }



    C componentAsFirst(Component component);

    C componentAtIndex(int index, Component component);

    static BaseTabConfigurator configure(Tab tab) {
        return new DefaultTabConfigurator(tab);
    }

    interface BaseTabConfigurator extends TabConfigurator<BaseTabConfigurator> {

    }


}
