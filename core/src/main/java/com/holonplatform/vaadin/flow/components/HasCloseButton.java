package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.builders.ButtonBuilder;

import java.util.function.Consumer;

public interface HasCloseButton<C extends HasCloseButton<C>> extends ButtonBuilder {

    C closeBtnConfigurator(Consumer<BaseButtonConfigurator> configurator);
}
