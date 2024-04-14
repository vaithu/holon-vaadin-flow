package com.holonplatform.vaadin.flow.components.builders;

public interface HasBorderLayoutConfigurator<C extends HasBorderLayoutConfigurator<C>> extends HasStyleConfigurator<C> {

    C border();

    C border(String... styles);

    void removeBorder();

    C withoutBorder();
}
