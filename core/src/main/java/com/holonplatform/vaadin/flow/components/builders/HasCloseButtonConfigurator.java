package com.holonplatform.vaadin.flow.components.builders;

import java.util.function.Consumer;

public interface HasCloseButtonConfigurator<C extends HasCloseButtonConfigurator<C>> extends ComponentConfigurator<C> {

    C closeButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    interface BaseCloseButtonConfigurator extends HasCloseButtonConfigurator<HasCloseButtonConfigurator.BaseCloseButtonConfigurator> {

    }
}
