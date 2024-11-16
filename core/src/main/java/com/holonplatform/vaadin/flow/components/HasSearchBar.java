package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HasOptionsMenuBarConfigurator;

import java.util.function.Consumer;

public interface HasSearchBar<C extends HasSearchBar<C>> extends HasOptionsMenuBarConfigurator<C> {

    C search(ValueHolder.ValueChangeListener < String, ValueHolder.ValueChangeEvent < String >> listener);

    C newButton(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);





}
