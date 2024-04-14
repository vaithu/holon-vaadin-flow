package com.holonplatform.vaadin.flow.components;

import com.holonplatform.vaadin.flow.components.builders.ButtonConfigurator;
import com.holonplatform.vaadin.flow.components.builders.HorizontalLayoutBuilder;
import com.vaadin.flow.component.Component;

import java.util.function.Consumer;

public interface HasFormFooter extends HorizontalLayoutBuilder {
    /*void saveBtnAction();

    void discardBtnAction();

    void updateBtnAction();*/

    void saveBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);
    void discardBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);
    void updateBtnConfigurator(Consumer<ButtonConfigurator.BaseButtonConfigurator> configurator);

    void addAdditionalComponent(Component component);


}
