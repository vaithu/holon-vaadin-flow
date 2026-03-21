package com.holonplatform.vaadin.flow;

public interface HasCard<C extends HasCard<C>> {

    C card();

    C card(String... styles);
}
