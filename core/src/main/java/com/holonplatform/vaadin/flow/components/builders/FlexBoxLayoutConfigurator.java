package com.holonplatform.vaadin.flow.components.builders;

import com.holonplatform.vaadin.flow.components.FlexBoxLayout;
import com.holonplatform.vaadin.flow.components.css.BorderRadius;
import com.holonplatform.vaadin.flow.components.css.BoxSizing;
import com.holonplatform.vaadin.flow.components.css.Display;
import com.holonplatform.vaadin.flow.components.css.Size;
import com.holonplatform.vaadin.flow.internal.components.builders.DefaultFlexBoxLayoutConfigurator;
import com.vaadin.flow.component.Component;

public interface FlexBoxLayoutConfigurator<C extends FlexBoxLayoutConfigurator<C>> extends HasComponentsConfigurator<C>,
        HasStyleConfigurator<C>, HasSizeConfigurator<C>, ComponentConfigurator<C> {

    C backgroundColor(String value);

    C backgroundColor(String value, String theme);

    C borderRadius(BorderRadius radius);

    C boxSizing(BoxSizing sizing);

    C display(Display display);

    C flex(String value, Component... components);

    C flexBasis(String value, Component... components);

    C flexShrink(String value, Component... components);

    C margin(Size... sizes);



    interface BaseFlexBoxLayoutConfigurator extends FlexBoxLayoutConfigurator<BaseFlexBoxLayoutConfigurator> {

    }

    static BaseFlexBoxLayoutConfigurator configure(FlexBoxLayout layout) {
        return new DefaultFlexBoxLayoutConfigurator(layout);
    }
}
