package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.HasComponent;
import com.holonplatform.vaadin.flow.components.utils.UIUtils;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultSplitLayoutConfigurator;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;

public interface SplitLayoutConfigurator<C extends SplitLayoutConfigurator<C>>
        extends ComponentConfigurator<C>, HasStyleConfigurator<C>, HasThemeVariantConfigurator<SplitLayoutVariant, C> {

    C primaryComponent(Component... components);
   default C primaryComponent(HasComponent... components) {
       return primaryComponent(UIUtils.toComponents(components));
   }

    C secondaryComponent(Component... components);
    default C secondaryComponent(HasComponent... components) {
        return secondaryComponent(UIUtils.toComponents(components));
    }

    void remove(Component... components);

    default void remove(HasComponent... components) {
        remove(UIUtils.toComponents(components));
    }

    void removeAll();

    C orientation(SplitLayout.Orientation orientation);

    C splitterPosition(double position);

    C primaryStyle(String styleName,
                   String value);

    C secondaryStyle(String styleName,
                     String value);

    static BaseSplitLayoutConfigurator configure(SplitLayout splitLayout) {
        return new DefaultSplitLayoutConfigurator(splitLayout);
    }

    interface BaseSplitLayoutConfigurator extends SplitLayoutConfigurator<BaseSplitLayoutConfigurator> {

    }



}
