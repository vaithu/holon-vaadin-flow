package com.holonplatform.vaadin.flow.components.builders;

import com.vaadin.flow.component.html.Span;

public interface DividerConfigurator<C extends DividerConfigurator<C>> extends LabelBuilder<Span> {

    C verticalSeparator() ;

    C horizontalSeparator();

    C iconSeparator();

    C size(int size);

}
