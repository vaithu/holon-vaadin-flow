package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;

import java.util.function.Consumer;

public interface HasSearchBar {

    void search(Consumer<String> consumer);

    void newBtnConfigurator(Consumer<ButtonConfigurator> configurator);
}
